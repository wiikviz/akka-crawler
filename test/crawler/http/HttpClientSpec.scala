package crawler.http

import crawler.http.Http.Page
import org.scalatest._


class HttpClientSpec extends FlatSpec with Matchers {

  import scala.concurrent.ExecutionContext.Implicits.global

  "A HttpClient" should "success fetch a https://www.yahoo.com" in {
    val fetch = HttpClient.fetch("https://www.yahoo.com")
    fetch.map(x =>
      x should matchPattern { case Page(_, _, _) => })
  }

  it should "return page with non empty content" in {
    HttpClient.fetch("https://www.yahoo.com").map {
      case Page(_, c, _) => c shouldNot be(null)
    }
  }

  it should "return page with parsed links" in {
    HttpClient.fetch("https://www.yahoo.com").map {
      case Page(_, _, l) => l should not be empty
    }
  }
}