package crawler.actor

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import crawler.actor.Filter.{Domains, GetDomains, SetDomains}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class FilterSpec extends TestKit(ActorSystem())
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A Queue" should {
    "be an empty on start" in {
      val f = system.actorOf(Filter.props())
      f ! GetDomains
      expectMsg(Domains(List.empty))
    }

    "be able to asynchronous set domains call" in {
      val f = system.actorOf(Filter.props())
      val domains = List("ya.ru", "google.com")
      f ! SetDomains(domains)
      expectNoMsg()
    }

    "be able to return domains in alphabet order" in {
      val f = system.actorOf(Filter.props())
      val domains = List("ya.ru", "google.com")
      f ! SetDomains(domains)
      f ! GetDomains
      expectMsg(Domains(List("google.com", "ya.ru")))
    }
  }
}