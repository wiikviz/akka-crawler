import akka.actor.{ActorRef, ActorSystem}
import akka.routing.Broadcast
import akka.testkit.TestProbe
import crawler.actor.Crawler.FetchNext
import org.scalatest.TestData
import org.scalatestplus.play._
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  import play.api.inject.bind

  implicit val system = ActorSystem()
  val probe = TestProbe()

  override def newAppForTest(testData: TestData): Application = new GuiceApplicationBuilder()
    .overrides(bind[ActorRef].qualifiedWith("crawler").toInstance(probe.ref))
    .build


  "Routes" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("See help at")
    }

  }

  "StatController" should {

    "return an empty statistic" in {
      contentAsString(route(app, FakeRequest(GET, "/status")).get) mustBe """{"in-progress":0,"saved":0}"""
    }
  }

  "Application" should {

    "invoke crawl on start" in {
      route(app, FakeRequest(GET, "/")).get
      probe.expectMsg(Broadcast(FetchNext))
      //[Crawler]("crawler", p => new RoundRobinPool(15).props(p))
      //
    }

  }

}
