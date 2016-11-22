package controllers

import javax.inject._

import akka.actor.ActorRef
import akka.routing.Broadcast
import crawler.actor.Crawler.{FetchNext, Pause, Resume}
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class CrawlController @Inject()(@Named("crawler") crawler: ActorRef)(implicit exec: ExecutionContext) extends Controller {
  crawler ! Broadcast(FetchNext)

  def pause() = Action {
    crawler ! Broadcast(Pause)
    Ok
  }
  def resume() = Action {
    crawler ! Broadcast(Resume)
    Ok
  }
}
