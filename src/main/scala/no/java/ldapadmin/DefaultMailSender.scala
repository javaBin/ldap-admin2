package no.java.ldapadmin

import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Transport, Session, Message}

trait DefaultMailSender {
  def sendMail(from: String, to: String, subject: String, body: String) {
    val props = new Properties();
    props.put("mail.smtp.host","localhost")
    val session = Session.getInstance(props);
    val msg = new MimeMessage(session)

    msg.setFrom(new InternetAddress(from))
    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
    msg.setSubject(subject)
    msg.setText(body)

    Transport.send(msg)
  }
}