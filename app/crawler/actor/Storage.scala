package crawler.actor


import java.net.URLEncoder
import java.nio.file.{Files, Paths}
import javax.inject.Named

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.google.inject.Inject
import crawler.actor.Statistician.DocumentSaved
import play.api.Configuration

import scala.util.{Failure, Success, Try}

object Storage {
  def props(conf: Configuration, stat: ActorRef) = akka.actor.Props(classOf[Storage], conf, stat)

  final case class SaveDocument(url: String, content: Array[Byte])

}

class Storage @Inject()(conf: Configuration, @Named("statistician") stat: ActorRef) extends Actor with ActorLogging {

  import Storage._

  private val downloadPath = ifNotExistTerminate(conf.getString("crawler.download_folder"))

  def receive = {
    case SaveDocument(url, bytes) =>
      val fileName = URLEncoder.encode(url, "UTF-8")
      val path = Paths.get(downloadPath, fileName)
      Try(Files.write(path, bytes)) match {
        case Failure(t) => log.error(t, s"I'm cannot save $url in $path. Life is shit!")
        case Success(s) =>
          log.debug(s"Save: $url in $path")
          stat ! DocumentSaved
      }
  }

  private def ifNotExistTerminate(p: Option[String]): String = {
    if (p.isEmpty) {
      log.error("Error: Missing configuration for parameter key \"crawler.download_folder\"")
      context.system.terminate()
    }
    else if (!Files.exists(Paths.get(p.get))) {
      log.error(s"`$p` the specified path does not exist.")
      context.system.terminate()
    }
    p.get
  }
}