package no.java.ldapadmin.ldap

import javax.naming.{NamingEnumeration, Context}
import javax.naming.directory.{SearchResult, SearchControls, InitialDirContext, DirContext}
import javax.naming.ldap.{Rdn, LdapName}
import java.util.Properties
trait LdapUserOperations {

  var context: DirContext = null
  val constraints = new SearchControls()
  constraints.setSearchScope(SearchControls.SUBTREE_SCOPE)
  constraints.setReturningObjFlag(true)

  private def connect() {
    val fileProps = new Properties
    fileProps.load(getClass.getClassLoader.getResourceAsStream("ldap.properties"))
    val props = new Properties
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
    props.setProperty(Context.PROVIDER_URL, fileProps.get("ldap.url").toString)
    props.setProperty(Context.URL_PKG_PREFIXES, "com.sun.jndi.url")
    props.setProperty(Context.REFERRAL, "ignore")
    props.setProperty(Context.SECURITY_AUTHENTICATION, "simple")
    props.setProperty(Context.OBJECT_FACTORIES, classOf[UserDirObjectFactory].getName)
    props.setProperty(Context.STATE_FACTORIES, classOf[UserDirStateFactory].getName)
    props.setProperty(Context.SECURITY_PRINCIPAL, fileProps.get("ldap.principal").toString)
    props.setProperty(Context.SECURITY_CREDENTIALS, fileProps.get("ldap.credential").toString)
    context = new InitialDirContext(props)
  }

  def findUserByEmail(email: String): Option[User] = {
    connect
    val values = Array[AnyRef](email)
    val baseDn = new LdapName("ou=People,dc=java,dc=no")
    val namingEnumeration: NamingEnumeration[SearchResult] = context.search(baseDn, "(mail={0})", values, constraints)

    while (namingEnumeration.hasMore) {
      val sr = namingEnumeration.next
      val o = sr.getObject
      if (classOf[User].isAssignableFrom(o.getClass)) {
        val usr: User = o.asInstanceOf[User]
        return Some(usr)
      }
    }
    None
  }

  def findUserByUid(uid: String): Option[User] = {
    connect
    val baseDn = new LdapName("ou=People,dc=java,dc=no")
    val result = context.lookup(baseDn.add(new Rdn("uid", uid)))
    if (classOf[User].isAssignableFrom(result.getClass)) {
      val usr: User = result.asInstanceOf[User]
      return Some(usr)
    }
    None
  }

  def saveUser(user: User) {
    connect
    val baseDn = new LdapName("ou=People,dc=java,dc=no")
    baseDn.add(new Rdn("uid", user.getUid))
    context.rebind(baseDn, user)
  }
}