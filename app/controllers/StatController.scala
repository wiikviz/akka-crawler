package controllers

import javax.inject._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import crawler.actor.Statistician.{GetStatistics, Statistics}
import play.api.libs.json.{Json, Writes}
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class StatController @Inject()(@Named("statistician") stat: ActorRef, implicit val timeout: Timeout)
                              (implicit exec: ExecutionContext) extends Controller {
  implicit val statWrites = new Writes[Statistics] {
    def writes(stat: Statistics) = Json.obj(
      "in-progress" -> stat.inQueue,
      "saved" -> stat.saved
    )
  }

  def getStatus = Action.async {
    (stat ? GetStatistics)
      .map {
        case s: Statistics =>
          Ok(Json.toJson(s))
      }
  }
}
