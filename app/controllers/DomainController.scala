package controllers

import javax.inject._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import crawler.actor.Filter.{Domains, GetDomains, SetDomains}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class DomainController @Inject()(@Named("filter") urlFilter: ActorRef, implicit val timeout: Timeout)(implicit exec: ExecutionContext) extends Controller {

  def setDomains = Action { request =>
    val domains = request.body.asJson.get.as[List[String]]
    urlFilter ! SetDomains(domains)
    Ok
  }

  def getDomains = Action.async {
    (urlFilter ? GetDomains)
      .map {
        case d: Domains => Ok(Json.toJson(d.domains))
      }
  }
}
