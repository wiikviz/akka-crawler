package crawler.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.Inject
import crawler.http.Http
import crawler.http.Http.{NoPage, Response}
import org.jsoup.HttpStatusException
import play.api.Configuration

import scala.concurrent.Future

object Fetcher {
  def props(conf: Configuration, http: Http) = akka.actor.Props(classOf[Fetcher], conf, http)

  private final case class TryFetch(receiver: ActorRef, url: String, ret: Int)

}

class Fetcher @Inject()(conf: Configuration, http: Http) extends Actor with ActorLogging {

  import Fetcher._

  import scala.concurrent.ExecutionContext.Implicits.global

  if (conf.getInt("crawler.retry_count").isEmpty) {
    log.error("Error: Missing configuration for parameter key \"crawler.retry_count\"")
    context.system.terminate()
  }

  private val retryCount = conf.getInt("crawler.retry_count").get

  override def receive = {
    case u: String => self ! TryFetch(sender(), u, retryCount)
    case TryFetch(r, u, ret) =>
      if (ret > 0) {
        val fetch: Future[Response] = http.fetch(u)
        fetch
          .map { resp => r ! resp }
          .recover {
            case t: Throwable =>
              val num = retryCount - ret + 1
              log.debug(s"The $num attempt to fetch a $u finished at $t")
              t match {
                case t: HttpStatusException =>
                  if (t.getStatusCode == 403)
                    r ! NoPage(u)
                  else
                    tryAgain(r, u, ret - 1)
                case _ =>
                  tryAgain(r, u, ret - 1)
              }
          }
      }
      else {
        log.error(s"I can't load: $u I GIVE UP")
        r ! NoPage(u)
      }

  }

  private def tryAgain(r: ActorRef, u: String, ret: Int): Unit = {
    self ! TryFetch(r, u, ret - 1)
  }
}
