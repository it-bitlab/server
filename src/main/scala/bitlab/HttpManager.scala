package bitlab

import bitlab.app.Codes
import bitlab.http.{HttpActorResponse, MailManagerMessage}
import bitlab.mail.MailManager.SendMail
import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem}
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.directives.FileInfo
import org.apache.pekko.util.Timeout
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.{Files, Paths}
import java.util.{Date, UUID}
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, SECONDS}
import scala.util.{Failure, Success}

object HttpManager extends Codes with HttpActorResponse {

  private val logger = LoggerFactory.getLogger("http")
  private val config = ConfigFactory.load()


  def apply(system: ActorSystem[Nothing],
            mail: ActorRef[MailManagerMessage],
           ): Future[Http.ServerBinding] = {
    try {
      implicit val sys: ActorSystem[Nothing] = system
      implicit val timeout: Timeout = Duration(5, SECONDS)
      val route: Route = {
        concat(
          (post & path("mail") & entity(as[String])) { (json) =>
            forward(mail.ask(ref => SendMail(ref, json)))
          },
          (storeUploadedFile("file", fileSave)) {  (metadata, file) =>
            complete(HttpEntity(file.toString))
          }
        )
      }
      logger.info("http started at " + config.getString("http.host") + ":" + config.getString("http.port"))
      Http().newServerAt(config.getString("http.host"), config.getInt("http.port")).bind(route)
    }
    catch {
      case e: Throwable =>
        println(e.toString)
        Thread.sleep(5 * 1000)
        HttpManager(system, mail)
    }
  }
  def fileSave(fileInfo: FileInfo): File = {
    val path = config.getString("files.path")
    val uid = UUID.randomUUID().toString.replace("-", "").take(5) + "-" + new Date().getTime.toString
    Files.createDirectory(Paths.get(path, uid))
    Files.createFile(Paths.get(path, uid, fileInfo.getFileName)).toFile
  }


  private def forward(future: Future[HttpActorResponse]): Route = {
    try {
      onComplete(future) {
        case Success(value) => value match {
          case SuccessTextResponse(value) => complete(HttpEntity(value))
          case ErrorTextResponse(value) => complete(HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(value)))
          case _ =>
            logger.error(ErrorCodes.UnidentifiedMessageFromActor)
            complete(HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(ErrorCodes.UnidentifiedMessageFromActor)))
        }
        case Failure(exception) =>
          logger.error(exception.toString)
          complete(HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(exception.toString)))
      }
    }
    catch {
      case e: Throwable =>
        println(e.toString)
        complete(HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(e.toString)))
    }
  }
}
