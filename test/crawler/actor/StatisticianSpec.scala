package crawler.actor

import crawler.actor.Queue.GetQueueSize
import crawler.actor.Statistician.{GetStatistics, DocumentSaved, Statistics}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class StatisticianSpec extends TestKit(ActorSystem())
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Statistician crawler.actor" should {
    "calculate statistics correctly" in {
      val filter = system.actorOf(Filter.props())
      val q = system.actorOf(Queue.props(filter))
      val s = system.actorOf(Statistician.props(q))
      s ! DocumentSaved
      s ! DocumentSaved
      s ! DocumentSaved
      s ! GetStatistics
      expectMsg(Statistics(3, 0))
    }

    "request a Queue to count of the document in a queue " in {
      val probe = TestProbe()
      val s = system.actorOf(Props(classOf[Statistician], probe.ref))
      s ! GetStatistics
      probe.expectMsg(GetQueueSize)
    }

  }
}