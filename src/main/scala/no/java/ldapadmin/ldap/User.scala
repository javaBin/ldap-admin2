package no.java.ldapadmin.ldap

import java.io.Serializable
import java.util.List

class User extends Serializable {
  def getDn: String = {
    return dn
  }

  def setDn(dn: String): Unit = {
    this.dn = dn
  }

  def getUid: String = {
    return uid
  }

  def setUid(uid: String): Unit = {
    this.uid = uid
  }

  def getFirstName: String = {
    return firstName
  }

  def setFirstName(firstName: String): Unit = {
    this.firstName = firstName
  }

  def getLastName: String = {
    return lastName
  }

  def setLastName(lastName: String): Unit = {
    this.lastName = lastName
  }

  def getMail: String = {
    return mail
  }

  def setMail(mail: String): Unit = {
    this.mail = mail
  }

  def getPassword: String = {
    return password
  }

  def setPassword(password: String): Unit = {
    this.password = password
  }

  def getMobilePhoneNumber: String = {
    return mobilePhoneNumber
  }

  def setMobilePhoneNumber(mobilePhoneNumber: String): Unit = {
    this.mobilePhoneNumber = mobilePhoneNumber
  }

  def getGroups: List[String] = {
    return groups
  }

  def setGroups(groups: List[String]): Unit = {
    this.groups = groups
  }

  override def toString: String = {
    return "[User: uid=" + uid + ", dn=" + dn + "]"
  }

  private var dn: String = null
  private var uid: String = null
  private var firstName: String = null
  private var lastName: String = null
  private var mail: String = null
  private var password: String = null
  private var mobilePhoneNumber: String = null
  private var groups: List[String] = null
}

