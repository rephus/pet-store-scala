package net.coconauts.scalarest

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.slick.driver.PostgresDriver.simple._

object Global {

  private val logger = LoggerFactory.getLogger(this.getClass)

  // Load enviroment settings,
  // If none, it will load `application.conf` only
  private val env = System.getenv("MODE")
  var conf: Config = null
  try {
    conf = ConfigFactory.load(env + ".conf")
    logger.info(s"Loaded settings: ${conf.getString("env")}")

  } catch {
    case _: Throwable => {
      logger.info(s"Unable to load config $env, loading defaults")
      conf = ConfigFactory.load()
      logger.info(s"Loaded settings: ${conf.getString("env")}")
    }
  }
  logger.info(s"Loading db from host " + conf.getString("db.url"))

  val db = Database.forConfig("db", conf)

}
