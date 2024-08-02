package bitlab.app

import org.slf4j.LoggerFactory
import scala.util.Properties

object Envs extends Codes {

  private val logger = LoggerFactory.getLogger("props")

  val mail_host: String = Properties.envOrElse("mail_host", {
    logger.error("mail_host" + EnvCodes.NotSet)
    ""
  })
  val mail_port: Int = Properties.envOrElse("mail_port", {
    logger.error("mail_port" + EnvCodes.NotSet)
    "0"
  }).toIntOption.getOrElse(0)
  val mail_login: String = Properties.envOrElse("mail_login", {
    logger.error("mail_login" + EnvCodes.NotSet)
    ""
  })
  val mail_password: String = Properties.envOrElse("mail_password", {
    logger.error("mail_password" + EnvCodes.NotSet)
    ""
  })
  val mail_to: String = Properties.envOrElse("mail_to", {
    logger.error("mail_to" + EnvCodes.NotSet)
    ""
  })
  val telegram_bot: String = Properties.envOrElse("telegram_bot", {
    logger.error("telegram_bot" + EnvCodes.NotSet)
    ""
  })
  val telegram_chat_id: String = Properties.envOrElse("telegram_chat_id", {
    logger.error("telegram_chat_id" + EnvCodes.NotSet)
    ""
  })


  def check(): Boolean ={
    !List(mail_host, mail_port, mail_login, mail_password, mail_to, telegram_bot, telegram_chat_id).contains("")
  }

}
