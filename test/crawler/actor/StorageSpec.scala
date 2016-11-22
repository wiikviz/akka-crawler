package crawler.actor

import java.nio.file.{Files, Paths}

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import crawler.actor.Storage.SaveDocument
import org.scalatest.concurrent.Eventually._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import play.api.Configuration

import scala.io.Source

class StorageSpec extends TestKit(ActorSystem())
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


  "A Storage crawler.actor" should {
    val downloadFolder = Files.createTempDirectory("storage-test").toString
    val conf: Configuration = Configuration(("crawler.download_folder", downloadFolder))
    val stat: TestProbe = TestProbe()

    "save document to configuration download folder" in {
      val path = Paths.get(downloadFolder, "abc.html").toString
      val fileContent: Array[Byte] = "abc".getBytes
      val uri: String = "abc.html"

      val s: ActorRef = system.actorOf(Storage.props(conf, stat.ref))

      s ! SaveDocument(uri, fileContent)
      eventually(timeout(Span(2, Seconds))) {
        Source.fromFile(path).mkString.getBytes should be equals fileContent
      }

    }

    "encode uri to file name" in {
      val uri: String = "http://stackoverflow.com/questions/15571496/how-to-check-if-a-folder-exists"
      val s: ActorRef = system.actorOf(Storage.props(conf, stat.ref))

      s ! SaveDocument(uri, "abc".getBytes)

      eventually(timeout(Span(2, Seconds))) {
        Files.exists(Paths.get(downloadFolder, "http%3A%2F%2Fstackoverflow.com%2Fquestions%2F15571496%2Fhow-to-check-if-a-folder-exists")) shouldBe true
      }
    }
  }
}