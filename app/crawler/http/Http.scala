package crawler.http


import scala.concurrent.Future

object Http {

  sealed trait Response

  case class Page(url: String, content: Array[Byte], outlinks: List[String]) extends Response

  case class NoPage(url:String) extends Response

}

trait Http {

  import Http._

  def fetch(url: String): Future[Response]
}
