package crawler.actor

import javax.inject.{Inject, Named}

import crawler.actor.Queue.{GetQueueSize, QueueSize}
import akka.actor.ActorRef

object Statistician {
  def props(queue: ActorRef) = akka.actor.Props(classOf[Statistician], queue)

  final case class Statistics(saved: Int, inQueue: Int)

  case object DocumentSaved

  case object GetStatistics

}

class Statistician @Inject()(@Named("queue") queue: ActorRef) extends akka.actor.Actor with akka.actor.ActorLogging {

  import Statistician._

  import scala.collection.mutable

  private val waitReplay = mutable.Queue[ActorRef]()
  private var savedDocs = 0

  def receive = {
    case DocumentSaved => savedDocs += 1
    case QueueSize(inQueue) =>
      val waiter = waitReplay.dequeue()
      val statistics = Statistics(savedDocs, inQueue)
      log.debug(s"Replay $statistics to $waiter")
      waiter ! statistics
    case GetStatistics =>
      log.debug(s"Recive $GetStatistics from ${sender()}. Sent GetQueueSize message to $queue")
      waitReplay.enqueue(sender())
      queue ! GetQueueSize
  }
}