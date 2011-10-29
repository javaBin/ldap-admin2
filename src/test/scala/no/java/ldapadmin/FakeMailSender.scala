package no.java.ldapadmin


trait FakeMailSender extends DefaultMailSender {
  override def sendMail(from: String, to: String, subject: String, body: String) = {
    
  }
}