package controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestProbe
import crawler.actor.Queue.InjectUrl
import crawler.actor.factory.{FetcherFactory, StorageFactory}
import crawler.actor.{Crawler, Statistician}
import crawler.http.Http
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestData
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import play.api.{Application, Configuration}

import scala.concurrent.Future

class QueueControllerSpec extends PlaySpec with OneAppPerTest with MockFactory {

  import play.api.inject.bind

  implicit val system = ActorSystem()
  val queue = TestProbe()
  val conf: Configuration = Configuration(("crawler.retry_count", 5), ("crawler.download_folder", "${TEMP}"))
  val http = mock[Http]
  val stat: ActorRef = system.actorOf(Statistician.props(queue.ref))
  val fetcherFactory = mock[MockableFetcherFactory]
  val storageFactory = mock[MockableStorageFactory]
  val crawler = system.actorOf(Crawler.props(queue.ref, fetcherFactory, storageFactory))

  override def newAppForTest(testData: TestData): Application = {
    new GuiceApplicationBuilder()
      .overrides(bind[ActorRef].qualifiedWith("queue").toInstance(queue.ref))
      .overrides(bind[ActorRef].qualifiedWith("crawler").toInstance(crawler))
      .build
  }

  case class MockableFetcherFactory() extends FetcherFactory(conf, http)

  case class MockableStorageFactory() extends StorageFactory(conf, stat)


  "A QueueController" should {

    "inject url to Queue" in {
      val url = "'http://vk.com'"
      val fakeRequest = FakeRequest(POST, controllers.routes.QueueController.inject().url).withJsonBody(Json.toJson("http://vk.com"))
      val result: Future[Result] = route(app, fakeRequest).get
      status(result) must equal(OK)
      queue.expectMsg(InjectUrl("http://vk.com"))
    }

  }

}
