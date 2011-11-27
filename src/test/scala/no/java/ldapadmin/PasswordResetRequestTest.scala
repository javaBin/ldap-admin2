package no.java.ldapadmin

import org.scalatest.FunSuite
import java.util.Calendar
import java.sql.Timestamp


class PasswordResetRequestTest extends FunSuite {
  test("A password reset request is only valid if it's less than 12 hours old") {
    var cal = Calendar.getInstance
    cal.add(Calendar.HOUR, -13)
    val moreThan12HoursAgo = new Timestamp(cal.getTimeInMillis)
    cal = Calendar.getInstance
    cal.add(Calendar.HOUR, -11)
    val lessThan12HoursAgo = new Timestamp(cal.getTimeInMillis)

    val invalidRequest = new PasswordResetRequest("test", "identifier", moreThan12HoursAgo)
    val validRequest = new PasswordResetRequest("test", "identifier", lessThan12HoursAgo)

    assert(invalidRequest.isValid === false)
    assert(validRequest.isValid === true)
  }
}