import org.scalatra.LifeCycle
import javax.servlet.ServletContext
import no.java.ldapadmin._

class Scalatra extends LifeCycle {

  override def init(context: ServletContext) {

    // mount servlets like this:
    context mount(new LdapAdminServlet, "/*")

    // set init params like this:
    // org.scalatra.cors.allowedOrigins = "http://example.com"
  }
}