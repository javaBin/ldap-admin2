package no.java.ldapadmin

import org.scalatra._
import org.squeryl._
import adapters.DerbyAdapter
import PrimitiveTypeMode._
import java.net.URL
import scalate.ScalateSupport
import no.java.core.{UserService, DefaultUserService}
import java.util.{List, Calendar}
import no.java.core.model.User
import java.sql.{Timestamp, DriverManager}

class LdapAdminServlet extends ScalatraServlet with ScalateSupport with UrlSupport with FakeMailSender {

  val temporaryUserServiceStub = new UserService {
      def setPassword(p1: String, p2: String) {}

      def saveUser(p1: User) {
        println("user saved")
      }

      def getUserByMail(p1: String) = null

      def findGroupsByMember(p1: String) = null

      def createUser(p1: User) = null

      def resetPassword(p1: String) = ""

      def getUser(p1: String): User = {
        val user = new User()
        user.setUid("user.name")
        user
      }

      def getDnByUid(p1: String) = ""

      def getUsers = null

      def findUserByMail(p1: String): User = {
        val user = new User()
        user.setUid("user.name")
        user
      }

      def deleteUser(p1: String) {}

      def search(p1: String) = null

      def saveGroups(p1: String, p2: List[String]) {}

      def getUserByDn(p1: String) = null
    }

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
    val user = temporaryUserServiceStub.getUser(uid);
    user.setPassword(pwd)
    temporaryUserServiceStub.saveUser(user)

    println("Password for set to " + pwd + " for user " + uid)
  }

  post("/recover-password") {
    val email = params("email")
    val user = temporaryUserServiceStub.findUserByMail(email)

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
