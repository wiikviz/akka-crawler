package crawler.actor

import java.net.{MalformedURLException, URL}
import javax.inject._

import scala.util.Try


object Filter {
  def props() = akka.actor.Props(classOf[Filter])

  final case class CheckUrl(url: String)

  final case class SetDomains(domains: List[String])

  final case class GoodUrl(url: String)

  final case class BadUrl(url: String)

  final case class Domains(domains: List[String])

  case object GetDomains

}

class Filter @Inject() extends akka.actor.Actor {

  import Filter._

  import scala.collection.immutable.HashSet

  private var domains = HashSet[String]()

  def receive = {
    case CheckUrl(url) => Try {
      if (domains.contains(trim3w(new URL(url).getHost)))
        sender ! GoodUrl(url)
      else
        sender ! BadUrl(url)
    }.recover {
      case t: MalformedURLException =>
        sender ! BadUrl(url)
    }
    case SetDomains(d) => domains = HashSet() ++ d
    case GetDomains => sender() ! Domains(domains.toList.sorted)
  }

  private def trim3w(s: String): String = if (s.startsWith("www.")) s.substring(4) else s
}