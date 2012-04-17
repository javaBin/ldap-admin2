package no.java.ldapadmin

import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Transport, Session, Message}
import org.slf4j.LoggerFactory

trait DefaultMailSender {

  val logger = LoggerFactory.getLogger(getClass)

  def sendMail(from: String, to: String, subject: String, body: String) {
    val props = new Properties();
    props.put("mail.smtp.host","localhost")
    val session = Session.getInstance(props);
    val msg = new MimeMessage(session)

    msg.setFrom(new InternetAddress(from))
    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
    msg.setSubject(subject)
    msg.setText(body)


    logger.info("Trying to send mail to " + to)

    try {
      Transport.send(msg)
    } catch {
      case e: Exception => logger.error("Something went wrong while sending mail", e)
    }

    logger.info("Successfully sent mail")
  }
}