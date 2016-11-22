import java.time.Clock

import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.google.inject.AbstractModule
import com.typesafe.config.ConfigFactory
import crawler.actor._
import crawler.actor.factory.{FetcherFactory, StorageFactory}
import crawler.http.{Http, HttpClient}
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.duration._

/**
  * This class is a Guice module that tells Guice how to bind several
  * different types. This Guice module is created when the Play
  * application starts.
  *
  * Play will automatically use any class called `Module` that is in
  * the root package. You can create modules in other locations by
  * adding `play.modules.enabled` settings to the `application.conf`
  * configuration file.
  */
class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure() = {
    bind(classOf[FetcherFactory]).asEagerSingleton()
    bind(classOf[StorageFactory]).asEagerSingleton()

    bindActor[Queue]("queue")
    bindActor[Statistician]("statistician")
    bindActor[Filter]("filter")
    bindActor[Crawler]("crawler", p => new RoundRobinPool(ConfigFactory.load().getInt("crawler.nrOfInstances")).props(p))


    bind(classOf[Http]).toInstance(HttpClient)
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bind(classOf[Timeout]).toInstance(akka.util.Timeout(5.seconds))
  }

}
