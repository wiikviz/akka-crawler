package crawler.http

object HttpClient extends Http {

  import Http._
  import org.apache.commons.validator.routines.UrlValidator
  import org.jsoup.Jsoup

  import scala.collection.JavaConverters._
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future

  private val urlValidator = new UrlValidator()

  override def fetch(url: String): Future[Response] = Future {
    try {
      val response = Jsoup
        .connect(url)
        .ignoreContentType(true)
        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1")
        .execute()
      if (response.contentType.startsWith("text/html")) {
        val links = response.parse
          .getElementsByTag("a")
          .asScala
          .map(e => e.attr("abs:href"))
          .filter(s => urlValidator.isValid(s))
          .toList
        Page(url, response.bodyAsBytes(), links)
      } else NoPage(url)
    }
    catch {
      case t: Throwable => NoPage(url)
    }
  }
}
