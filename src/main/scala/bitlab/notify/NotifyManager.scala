package bitlab.notify

import bitlab.http.MailManagerMessage
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors

object NotifyManager extends NotifyManagerControl with NoneControl {

  def apply(): Behavior[MailManagerMessage] = Behaviors.setup(context => {

    Behaviors.receiveMessagePartial(
      notifyManagerControl
        .orElse(noneControl)
    )
  })
}
