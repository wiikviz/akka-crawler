package crawler.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import crawler.actor.Queue._
import org.scalatest.concurrent.Eventually._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}


class QueueSpec extends TestKit(ActorSystem())
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  private def noFilter() = system.actorOf(Props(classOf[NoFilter]))

  "A Queue" should {
    "be an empty on start and return QueueSize(0)" in {
      val q = system.actorOf(Queue.props(noFilter()))
      q ! GetQueueSize
      expectMsg(QueueSize(0))
    }

    "calculated correctly the queue size" in {
      val q = system.actorOf(Queue.props(noFilter()))
      q ! InjectUrl("http://ya.ru")
      q ! InjectUrl("http://google.com")
      eventually(timeout(Span(2, Seconds))) {
        q ! GetQueueSize
        expectMsg(QueueSize(2))
      }
    }

    "replay Url from PoolUrl" in {
      val q = system.actorOf(Queue.props(noFilter()))
      q ! InjectUrl("http://ya.ru")
      q ! InjectUrl("http://google.com")
      q ! PoolUrl
      expectMsg(Url("http://ya.ru"))
    }

  }
}

class NoFilter extends akka.actor.Actor {

  import Filter._

  def receive = {
    case CheckUrl(url) =>
      sender ! GoodUrl(url)
  }
}