package no.java.ldapadmin

import ldap.LdapUserOperations
import org.scalatra._
import scalate.ScalateSupport
import java.util.Calendar
import java.sql.Timestamp
import util.HashGenerator

class LdapAdminServlet extends ScalatraServlet with ScalateSupport with UrlSupport with DefaultMailSender with LdapUserOperations {

  get("/") {
    redirect(url("/recover-password"))
  }

  get("/recover-password") {
    showView("recover-password")
  }

  get("/reset-password/:userid/:identifier") {
    if (ResetRequestDB.doesItExist(params("userid"), params("identifier"))) {
      showView("reset-password", "userid" -> params("userid"), "identifier" -> params("identifier"))
    } else {
      showView("recover-password", "errorMessage" -> Some("The reset-link was expired or unknown."))
    }
  }

  post("/reset-password/:userid/:identifier") {
    val uid = params("userid")
    val identifier = params("identifier")
    val pwd = params("pwd")
    val user = findUserByUid(uid)
    if(user.isDefined && ResetRequestDB.doesItExist (uid, identifier)) {
      val u = user.get
      u.setPassword(HashGenerator.createHash(pwd))
      saveUser(u)
      ResetRequestDB.deleteByUser(u.getUid)
    } else {
      halt(400)
    }
  }

  post("/recover-password") {
    val email = params("email")
    val user = findUserByEmail(email)

    if (user.isDefined) {
      val now = Calendar.getInstance().getTime
      val rnd = new java.util.Random
      val identifier = rnd.nextLong.toHexString + "-" + rnd.nextLong.toHexString
      val uid = user.get.getUid
      val pr = new PasswordResetRequest(uid, identifier, new Timestamp(now.getTime))
      ResetRequestDB.addRequest(pr)
      sendResetLinkToUser(email, uid, identifier)
    } else {
      showView("recover-password", "errorMessage" -> Some("No user with the given e-mail address"))
    }
  }

  def sendResetLinkToUser(email: String, username: String, identifier: String) {
    var resetURL = request.getScheme + "://" + request.getServerName
    if (request.getServerPort != 80) {
      resetURL = resetURL.concat(":" + request.getServerPort)
    }
    resetURL = resetURL.concat(url("/reset-password/" + username + "/" + identifier))
    sendMail("noreply@java.no", email, "Password reset requested", "Reset your password here: " + resetURL)
  }

  def showView(view: String, attributes: (String, Any)*) {
    contentType = "text/html"
    scaml(view, attributes: _*)
  }

  notFound {
    resourceNotFound()
  }

  protected def contextPath = request.getContextPath
}
