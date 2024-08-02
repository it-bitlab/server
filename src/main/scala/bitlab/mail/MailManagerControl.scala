package bitlab.mail

import bitlab.HttpManager.{ErrorTextResponse, SuccessTextResponse}
import bitlab.app.Envs
import bitlab.http.{HttpActorResponse, MailManagerMessage}
import io.circe.generic.JsonCodec
import io.circe.jawn.decode
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import org.slf4j.LoggerFactory

trait MailManagerControl {

  private val logger = LoggerFactory.getLogger("MailManagerControl")

  case class SendMail(replyTo: ActorRef[HttpActorResponse], json: String) extends MailManagerMessage

  @JsonCodec case class MailMessage(name: String, email: String, company: String, text: String, attachments: List[String])

  private case class Mail(to: String, subject: String, text: String)


  def mailManagerControl: PartialFunction[MailManagerMessage, Behavior[MailManagerMessage]] = {
    case sender@SendMail(replyTo, json) =>
      decode[MailMessage](json) match {
        case Right(value) =>
          val text = "Пришло сообщение от " + value.name + ", компания " + value.company + ", почта " + value.email + ", вложения: " + value.attachments.mkString(",")
          sendMail(Mail(Envs.mail_to, "Новое сообщение", text))
          replyTo.tell(SuccessTextResponse("send"))
        case Left(value) =>
          logger.error(value.toString)
          replyTo.tell(ErrorTextResponse(value.toString))
      }
      Behaviors.same
  }
  private def sendMail(mail: Mail): Unit = {
    try{
      val email = EmailBuilder.startingBlank()
        .to(mail.to)
        .from("BitLab", Envs.mail_login)
        .withSubject(mail.subject)
        .withPlainText(mail.text)
        .buildEmail()
      val mailer = MailerBuilder
        .withSMTPServer(Envs.mail_host, Envs.mail_port, Envs.mail_login, Envs.mail_password)
        .withTransportStrategy(TransportStrategy.SMTPS)
        .async()
        .buildMailer()
      mailer.sendMail(email)
    }
    catch {
      case e: Throwable => logger.error(e.toString)
    }
  }
}
