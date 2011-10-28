import no.java.core.DefaultUserService
import org.scalatra._
import java.net.URL
import scalate.ScalateSupport

class LdapAdminServlet extends ScalatraServlet with ScalateSupport {

  get("/") {
    showView("index")
  }

  get("/recover-password") {
    showView("recover-password")
  }

  get("/reset-password/:username/:identifier") {
    if(validResetRequest(params("username"), params("identifier"))){
      showView("reset-password")
    }
  }

  def sendPasswordToUser(email: String, generatedPassword: String) {

  }

  post("/reset-password") {
    val email = params("email")
//    val userService = new DefaultUserService();
//    val user = userService.findUserByMail(email);
//    val generatedPassword = userService.resetPassword(user.getUid)
    sendPasswordToUser("thomas.skardal@gmail.com", "trallala")
  }


  notFound {
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() 
  }

  def showView(view: String) {
    contentType = "text/html"
    scaml(view)
  }
}
