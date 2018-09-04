package net.coconauts.scalarest

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import net.coconauts.scalarest.controllers.SprayActor
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val conf = Global.conf

  {
    val url = conf.getString("db.url")
    logger.info(s"Migrating database on url ${url}")
    val flyway = new Flyway()
    flyway.setDataSource(url, conf.getString("db.user"), conf.getString("db.password"))
    flyway.migrate()
  }

  logger.debug("Service starting")
  // we need an ActorSystem to host our application in
  private implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  private val service = system.actorOf(Props[SprayActor], conf.getString("spray.service"))

  private implicit val timeout = Timeout(conf.getInt("spray.timeout").seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler

  private val port = conf.getInt("spray.port")
  private val host = conf.getString("spray.host")
  IO(Http) ? Http.Bind(service, interface = host, port = port)

  logger.debug("Service initialization finished")

}

