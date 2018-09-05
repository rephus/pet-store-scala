package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.models.PetJsonProtocol._
import net.coconauts.scalarest.models._
import net.coconauts.scalarest.{Global, MaybeFilter}
import org.slf4j.LoggerFactory
import spray.httpx.SprayJsonSupport._
import spray.routing._

import scala.slick.driver.JdbcDriver.simple._

trait PetController extends HttpService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val petRoutes =
    path("pet" / Segment) { id =>
      get {
        logger.debug(s"Received request GET '/pet/$id' ")
        implicit val s = Global.db.createSession()

        rejectEmptyResponse {
          val pet = Pets.objects.filter(_.id === id.toInt).list.headOption

          complete(pet)
        }
      } ~
      put {
        entity(as[Pet]) { Pet =>
          logger.debug(s"Received request PUT '/pet/$id' ")
          implicit val s = Global.db.createSession()

          rejectEmptyResponse {
            val pet = Pets.objects.filter(_.id === id.toInt).list.headOption

            complete(pet)
          }
        }
      }

    } ~ path("pet") {
      post {
        //This entity parses all the PUT body and convert it into a Pet model
        entity(as[Pet]) { Pet =>

          logger.debug(s"Received request PUT '/pet' $Pet")
          implicit val s = Global.db.createSession()

          val id = Pets.insert(Pet)
          logger.info(s"Inserted Pet with id ${id}")

          val insertedPet = Pets.get(id).get
          complete(insertedPet)
        }
      }
    }
}
