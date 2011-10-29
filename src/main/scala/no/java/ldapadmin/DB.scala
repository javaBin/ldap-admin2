package no.java.ldapadmin

import org.squeryl.Schema

object DB extends Schema {
  val resetRequests = table[PasswordResetRequest]
  drop
  create
  printDdl
}