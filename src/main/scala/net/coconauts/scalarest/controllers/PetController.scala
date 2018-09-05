package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.models.PetJsonProtocol._
import net.coconauts.scalarest.models.PetStatus.PetStatus
import net.coconauts.scalarest.models._
import net.coconauts.scalarest.{Global, MaybeFilter}
import org.slf4j.LoggerFactory
import spray.httpx.SprayJsonSupport._
import spray.routing._

import scala.slick.driver.JdbcDriver.simple._

trait PetController extends HttpService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val petRoutes =
    path("pet" / IntNumber) { id =>
      pathEnd {

        post {
          entity(as[Pet]) { pet =>

            complete{
              logger.info(s"Received request PUT '/pet/$id' " )
              implicit val s = Global.db.createSession()
              Pets.update(pet.copy(id=id))

              pet
            }
         }
        } ~
          get {

            complete {
              logger.info(s"Received request PUT '/pet/$id' " )
              println("PARAMETER " , parameter('method))
              implicit val s = Global.db.createSession()

              val pet = Pets.objects.filter(_.id === id.toInt).list.headOption

              pet
            }
          } ~
          delete {

            complete{
              logger.info(s"Received request DELETE '/pet/$id' ")

              implicit val s = Global.db.createSession()

              Pets.delete(id.toInt)
              ""
            }
          }
      }
    } ~ path("pet") {
      put {
        //This entity parses all the PUT body and convert it into a Pet model
        entity(as[Pet]) { pet =>

          complete{
            logger.debug(s"Received PUT '/pet' $pet")
            implicit val s = Global.db.createSession()

            Pets.update(pet)
            pet
          }
        }
      } ~
      post {
        //This entity parses all the PUT body and convert it into a Pet model
        entity(as[Pet]) { pet =>

          complete{
            logger.debug(s"Received request POST '/pet' $pet")
            implicit val s = Global.db.createSession()

            val id = Pets.insert(pet)
            logger.info(s"Inserted Pet with id ${id}")
            val insertedPet = Pets.get(id).get

            insertedPet
          }
        }
      }
    }  ~ path("pet" / "findByStatus") {
      get {
        parameters('status) { (status) =>

          complete {
            implicit val petStatusMapper = MappedColumnType.base[PetStatus, String](
              e => e.toString,
              s => PetStatus.withName(s)
            )

            implicit val s = Global.db.createSession()
            val petStatus = PetStatus.withName(status)
            Pets.objects.filter(_.status === petStatus).list
          }
        }

      }
    }
}
