package no.java.ldapadmin

import java.sql.Timestamp
import java.util.{Date, Calendar}


class PasswordResetRequest(val username: String, val identifier: String, val requestTime: Timestamp, var active: Boolean) {

  def this(username: String, identifier: String, requestTime: Timestamp) = {
    this(username, identifier, requestTime, true)
  }

  def isValid: Boolean = {
    val now = Calendar.getInstance.getTime
    val diff = now.getTime - requestTime.getTime
    val diffInHours = diff / (60 * 60 * 1000)
    if (diffInHours < 12L) {
      true
    } else {
      false
    }
  }
}