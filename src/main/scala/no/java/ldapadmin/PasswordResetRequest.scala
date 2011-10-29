package no.java.ldapadmin

import org.squeryl.Schema
import java.util.{Calendar, Date}


class PasswordResetRequest(val username: String, val identifier: String, val requestTime: Date) {
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