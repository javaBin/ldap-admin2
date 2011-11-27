package no.java.ldapadmin.ldap

import javax.naming.directory.Attributes

class UserDirStateFactory (requiredType: Class[User]) extends AbstractDirStateFactory[User] {
  def this() {
    this(classOf[User])
    addObjectClass("inetOrgPerson")
  }

  protected def convert(o: User, attributes: Attributes): Attributes = {
    var user: User = o.asInstanceOf[User]
    attributes.put("uid", user.getUid)
    attributes.put("cn", user.getUid)
    attributes.put("givenName", user.getFirstName)
    attributes.put("sn", user.getLastName)
    putIfNotEmpty("mail", user.getMail)
    putIfNotEmpty("mobile", user.getMobilePhoneNumber)
    var password: String = user.getPassword
    if (password != null) {
      attributes.put("userPassword", password)
    }
    attributes
  }
}

