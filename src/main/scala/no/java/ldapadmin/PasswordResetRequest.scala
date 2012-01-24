package no.java.ldapadmin

import java.sql.Timestamp
import java.util.{Date, Calendar}
import collection.mutable.{HashMap}


class PasswordResetRequest(val uid: String, val identifier: String, val requestTime: Timestamp, var active: Boolean) {

  def this(uid: String, identifier: String, requestTime: Timestamp) = {
    this(uid, identifier, requestTime, true)
  }

  def isValid: Boolean = {
    val now = Calendar.getInstance.getTime
    val diff = now.getTime - requestTime.getTime
    val diffInHours = diff / (60 * 60 * 1000)
    diffInHours < 12L
  }
}

object ResetRequestDB {
  private val requests = new HashMap[String, PasswordResetRequest]()
  
  def addRequest(request: PasswordResetRequest) {
    requests.put(request.uid, request)
  }

  def deleteByUser(uid: String) {
    requests.remove(uid)
  }

  def doesItExist(uid : String, identifier : String) : Boolean = {
    requests.get(uid) match {
      case Some(req) => req.identifier == identifier && req.isValid
      case None => false
    }
  }
}