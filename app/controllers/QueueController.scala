package controllers

import java.net.URL
import javax.inject._

import akka.actor.ActorRef
import crawler.actor.Queue.InjectUrl
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class QueueController @Inject()(@Named("queue") queue: ActorRef)(implicit exec: ExecutionContext) extends Controller {

  def inject: Action[AnyContent] = Action { request =>
    if (request.body.asJson.isDefined) {
      val url = new URL(request.body.asJson.get.as[String]).toString
      queue ! InjectUrl(url)
      Ok
    }
    else if (request.body.asText.isDefined) {
      val url = new URL(request.body.asText.get.toString).toString
      queue ! InjectUrl(url)
      Ok
    }
    else BadRequest
  }
}
