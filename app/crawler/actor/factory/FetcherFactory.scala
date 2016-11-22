package crawler.actor.factory

import javax.inject.Inject

import akka.actor.{ActorContext, ActorRef}
import crawler.actor.Fetcher
import crawler.http.Http
import play.api.Configuration

class FetcherFactory @Inject()(conf: Configuration, http: Http) {
  def createFetcher(ctx: ActorContext): ActorRef = ctx.actorOf(Fetcher.props(conf, http), "http")
}
