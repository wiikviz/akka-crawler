package crawler.actor

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import crawler.actor.Queue.{InjectUrl, PoolUrl, Url}
import crawler.actor.Storage.SaveDocument
import crawler.actor.factory.{FetcherFactory, StorageFactory}
import crawler.http.Http.{NoPage, Page}

object Crawler {
  def props(queue: ActorRef, fetcherFactory: FetcherFactory, storageFactory: StorageFactory): Props = Props(classOf[Crawler], queue, fetcherFactory, storageFactory)

  case object FetchNext

  case object Pause

  case object Resume

}

class Crawler @Inject()(@Named("queue") queue: ActorRef,
                        fetcherFactory: FetcherFactory,
                        storageFactory: StorageFactory) extends Actor with ActorLogging {

  import Crawler._

  private val storage = storageFactory.createStorage(context)
  private val fetcher = fetcherFactory.createFetcher(context)
  private var isRunning = true

  def receive = {
    case FetchNext => if (isRunning) queue ! PoolUrl
    case Pause => if (isRunning) isRunning = false
    case Resume =>
      if (!isRunning) {
        isRunning = true
        self ! FetchNext
      }
    case Url(url) => fetcher ! url
    case Page(href, content, outlinks) =>
      log.debug(s"$self fetch $href")
      storage ! SaveDocument(href, content)
      outlinks.foreach(x => queue ! InjectUrl(x))
      self ! FetchNext
    case NoPage(u) =>
      log.debug(s"BadResponse: $u")
      self ! FetchNext
  }
}