package crawler.actor

import javax.inject._

import akka.actor.ActorRef
import crawler.actor.Filter.{BadUrl, CheckUrl, GoodUrl}

object Queue {
  def props(filter: ActorRef) = akka.actor.Props(classOf[Queue], filter)

  final case class InjectUrl(url: String)

  final case class Url(url: String)

  final case class QueueSize(count: Int)

  case object PoolUrl

  case object GetQueueSize

}

class Queue @Inject()(@Named("filter") filter: ActorRef) extends akka.actor.Actor {

  import Queue._

  private val queue = scala.collection.mutable.Queue[String]()
  private val visited = scala.collection.mutable.HashSet[String]()
  private val waiting = scala.collection.mutable.Queue[ActorRef]()

  def receive = {
    case PoolUrl =>
      if (!queue.isEmpty) {
        sender() ! Url(queue.dequeue())
      }
      else {
        waiting.enqueue(sender())
      }
    case InjectUrl(url) =>
      if (!visited.contains(url)) {
        filter ! CheckUrl(url)
      }
    case GoodUrl(url) =>
      if (!visited.contains(url))
        if (waiting.nonEmpty) {
          waiting.dequeue ! Url(url)
          visited.add(url)
        }
        else queue.enqueue(url)
    case BadUrl(url) => ()

    case GetQueueSize =>
      val size: Int = queue.size
      sender() ! QueueSize(size)
  }
}