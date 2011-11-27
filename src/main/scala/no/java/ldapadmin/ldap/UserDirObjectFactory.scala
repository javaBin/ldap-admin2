package no.java.ldapadmin.ldap

object UserDirObjectFactory {
  val REQUIRED_ATTRIBUTES: Array[String] = Array[String]("uid", "givenName", "sn", "mail")
}

class UserDirObjectFactory extends AbstractDirObjectFactory {
  addRequiredObjectClass("inetOrgPerson")
  setIgnoreAttributeExeptionDuringConvertion(true)

  def convert: AnyRef = {
    val user: User = new User
    user.setDn(getName.toString)
    user.setUid(getStringAttribute("uid"))
    user.setFirstName(getStringAttribute("givenName"))
    user.setLastName(getStringAttribute("sn"))
    user.setMail(getStringAttribute("mail"))
    user.setMobilePhoneNumber(getStringAttribute("mobile", null))
    user
  }
}

