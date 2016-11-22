package crawler.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import crawler.http.Http.{NoPage, Page}
import crawler.http.HttpClient
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.Configuration

import scala.concurrent.duration._

class FetcherSpec extends TestKit(ActorSystem())
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory {
  implicit val timeout = 2.second

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Downloader" should {
    val retryCount = 5
    val conf = Configuration(("crawler.retry_count", retryCount))

    "fetch http://google.com" in {
      val c = HttpClient
      val url = "http://google.com"
      val d = system.actorOf(Fetcher.props(conf, c))

      d ! url
      expectMsgPF() {
        case Page(url, _, l) => l should not be empty
      }
    }

    "return NoPage" in {
      val c = HttpClient
      val d = system.actorOf(Fetcher.props(conf, c))
      val url = "sadhttp://foo"

      (d ! url)

      expectMsg(NoPage(url))
    }

  }
}