package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.{Global, MaybeFilter}
import net.coconauts.scalarest.models._
import spray.json.JsNumber
import spray.routing._
import org.slf4j.LoggerFactory
import spray.routing._
import spray.httpx.SprayJsonSupport._
import spray.routing._
import spray.json._
import DefaultJsonProtocol._
import net.coconauts.scalarest.models.UserJsonProtocol._
import spray.json._

import scala.slick.driver.JdbcDriver.simple._

trait UserController extends HttpService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val userRoutes =
    path("user") {
      post {
        //This entity parses all the PUT body and convert it into a user model
        entity(as[User]) { user =>

          logger.debug(s"Received request PUT '/user' $user")
          implicit val s = Global.db.createSession()
          import net.coconauts.scalarest.models.UserJsonProtocol._

          val id = Users.insert(user)
          logger.info(s"Inserted user with id ${id}")

          val insertedUser = Users.get(id).get
          complete(insertedUser)
        }
      }
    }
}
