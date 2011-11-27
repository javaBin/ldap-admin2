package no.java.ldapadmin

import ldap.LdapUserOperations
import org.scalatra._
import org.squeryl._
import adapters.DerbyAdapter
import PrimitiveTypeMode._
import scalate.ScalateSupport
import java.util.Calendar
import java.sql.{Timestamp, DriverManager}
import util.HashGenerator

class LdapAdminServlet extends ScalatraServlet with ScalateSupport with UrlSupport with FakeMailSender with LdapUserOperations {

  Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
  SessionFactory.concreteFactory = Some(() =>
    Session.create(
      DriverManager.getConnection("jdbc:derby:ldapadmin;create=true"),
      new DerbyAdapter
    )
  )

  get("/") {
    showView("index")
  }

  get("/recover-password") {
    showView("recover-password")
  }

  def validResetRequest(username: String, identifier: String): Boolean = {
    transaction {
      val query = DB.resetRequests.where(r => r.username === username and r.identifier === identifier)
      if (query.size > 0) {
        val req = query.single
        req.isValid
      } else {
        false
      }
    }
  }

  get("/reset-password/:userid/:identifier") {
    if (validResetRequest(params("userid"), params("identifier"))) {
      showView("reset-password", "userid" -> params("userid"))
    } else {
      showView("recover-password", "errorMessage" -> Some("The reset-link was expired or unknowwn."))
    }
  }

  post("/reset-password/:userid") {
    val uid = params("userid")
    val pwd = params("pwd")
    val user = findUserByUid(uid).get
    user.setPassword(HashGenerator.createHash(pwd))
    saveUser(user)

    println("Password set to " + pwd + "(" + HashGenerator.createHash(pwd) + " for user " + uid)
  }

  post("/recover-password") {
    val email = params("email")
    val user = findUserByEmail(email).get

    if (user != null) {
      val now = Calendar.getInstance().getTime
      val rnd = new java.util.Random
      val identifier = rnd.nextLong.toHexString + "-" + rnd.nextLong.toHexString
      val pr = new PasswordResetRequest(user.getUid, identifier, new Timestamp(now.getTime))
      transaction {
        DB.resetRequests.insert(pr)
      }
      sendResetLinkToUser(email, user.getUid, identifier)
    } else {
      showView("recover-password", "errorMessage" -> Some("No user with the given e-mail address"))
    }
  }

  def sendResetLinkToUser(email: String, username: String, identifier: String) {
    val resetUrl = url("/reset-password/" + username + "/" + identifier)
    sendMail("noreply@java.no", email, "Password reset requested", "Reset your password here: " + resetUrl)
  }

  notFound {
    resourceNotFound()
  }

  def showView(view: String, attributes: (String, Any)*) {
    contentType = "text/html"
    scaml(view, attributes: _*)
  }

  protected def contextPath = request.getContextPath
}
