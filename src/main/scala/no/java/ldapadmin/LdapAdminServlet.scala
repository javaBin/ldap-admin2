package no.java.ldapadmin

import no.java.core.DefaultUserService
import org.scalatra._
import org.squeryl._
import adapters.DerbyAdapter
import PrimitiveTypeMode._
import java.net.URL
import scalate.ScalateSupport
import java.util.Calendar
import java.sql.DriverManager

class LdapAdminServlet extends ScalatraServlet with ScalateSupport with DefaultMailSender {

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

  get("/reset-password/:username/:identifier") {
    if (validResetRequest(params("username"), params("identifier"))) {
      showView("reset-password")
    } else {
      showView("recover-password", "errorMessage" -> Some("The reset-link was expired or unknowwn."))
    }
  }

  def sendRestLinkToUser(email: String, generatedPassword: String) {
      sendMail("", email, "Password reset requested", "")
  }

  post("/reset-password") {
    val email = params("email")
//    val userService = new DefaultUserService();
//    val user = userService.findUserByMail(email);
//    val generatedPassword = userService.resetPassword(user.getUid)
  }

  post("/recover-password") {
    val email = params("email")
    val now = Calendar.getInstance().getTime
    val rnd = new java.util.Random
    val identifier = rnd.nextLong.toHexString + "-" + rnd.nextLong.toHexString
    val pr = new PasswordResetRequest(email, identifier, now)
    transaction {
      DB.resetRequests.insert(pr)
    }
  }


  notFound {
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map {
      path =>
        contentType = "text/html"
        layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }

  def showView(view: String, attributes: (String, Any)*) {
    contentType = "text/html"
    scaml(view, attributes:_*)
  }
}
