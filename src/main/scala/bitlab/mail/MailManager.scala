package bitlab.mail

import bitlab.http.MailManagerMessage
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object MailManager extends MailManagerControl with NoneControl {

  def apply(): Behavior[MailManagerMessage] = Behaviors.setup(context => {

    Behaviors.receiveMessagePartial(
      mailManagerControl
        .orElse(noneControl)
    )
  })
}
