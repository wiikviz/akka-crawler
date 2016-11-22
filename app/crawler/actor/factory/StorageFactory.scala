package crawler.actor.factory

import javax.inject.{Inject, Named}

import akka.actor.{ActorContext, ActorRef}
import crawler.actor.Storage
import play.api.Configuration

class StorageFactory @Inject()(conf: Configuration, @Named("statistician")stat: ActorRef) {
  def createStorage(ctx: ActorContext) = ctx.actorOf(Storage.props(conf, stat), "storage")
}
