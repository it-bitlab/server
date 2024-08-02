package bitlab.notify

import bitlab.HttpManager.{ErrorTextResponse, SuccessTextResponse}
import bitlab.app.Envs
import bitlab.http.{HttpActorResponse, MailManagerMessage}
import com.typesafe.config.ConfigFactory
import io.circe.generic.JsonCodec
import io.circe.jawn.decode
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.HttpRequest
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder
import org.slf4j.LoggerFactory

import java.net.URLEncoder
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait NotifyManagerControl {

  private val logger = LoggerFactory.getLogger("MailManagerControl")
  private val config = ConfigFactory.load()

  implicit val sys: ActorSystem[_] = ActorSystem.apply(Behaviors.empty, "notify")

  case class SendMail(replyTo: ActorRef[HttpActorResponse], json: String) extends MailManagerMessage

  @JsonCodec case class NotifyMessage(name: String, email: String, company: String, text: String, attachments: List[String])

  private case class Mail(to: String, subject: String, text: String)
  private case class Telegram(text: String)


  def notifyManagerControl: PartialFunction[MailManagerMessage, Behavior[MailManagerMessage]] = {
    case sender@SendMail(replyTo, json) =>
      decode[NotifyMessage](json) match {
        case Right(value) =>
          val text = "Пришло сообщение от " + value.name + ", компания " + value.company + ", почта " + value.email + ", вложения: " + value.attachments.mkString(",")
          sendMail(Mail(Envs.mail_to, "Новое сообщение", text))
          sendTelegram(text)
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
  private def sendTelegram(text: String): Unit = {
    try{
      val url = config.getString("telegram.host") + Envs.telegram_bot + "/sendMessage?chat_id=" + Envs.telegram_chat_id + "&text=" + URLEncoder.encode(text, "utf-8")
      Http().singleRequest(HttpRequest(uri = url))
        .onComplete {
          case Success(res) =>
            logger.info("notify message has been send via telegram")
          case Failure(exception) =>
            logger.error(exception.toString)
        }
    }
    catch {
      case e: Throwable => logger.error(e.toString)
    }
  }

}
