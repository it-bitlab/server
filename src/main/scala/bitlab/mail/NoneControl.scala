package bitlab.mail

import bitlab.app.Envs.ErrorCodes
import bitlab.http.MailManagerMessage
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.slf4j.LoggerFactory

trait NoneControl {

  private val logger = LoggerFactory.getLogger("NoneControl")

  def noneControl: PartialFunction[MailManagerMessage, Behavior[MailManagerMessage]] = {
    case _ =>
      logger.error(ErrorCodes.UnreachableCode)
      Behaviors.same
  }
}
