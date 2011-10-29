package no.java.ldapadmin

import org.scalatest.FunSuite
import java.util.Calendar


class PasswordResetRequestTest extends FunSuite {
  test("A password reset request is only valid if it's less than 12 hours old") {
    var cal = Calendar.getInstance
    cal.add(Calendar.HOUR, -13)
    val moreThan12HoursAgo = cal.getTime
    cal = Calendar.getInstance
    cal.add(Calendar.HOUR, -11)
    val lessThan12HoursAgo = cal.getTime

    val invalidRequest = new PasswordResetRequest("test", "identifier", moreThan12HoursAgo)
    val validRequest = new PasswordResetRequest("test", "identifier", lessThan12HoursAgo)

    assert(invalidRequest.isValid === false)
    assert(validRequest.isValid === true)
  }
}