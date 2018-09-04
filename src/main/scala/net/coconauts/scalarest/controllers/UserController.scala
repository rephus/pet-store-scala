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
    path("v2/user") {
      get {
        //Optional parameters we get from the URL
        parameters('id ?, 'email ?, 'limit ?) {
          (id, email, limit) =>

            val limitResults = limit.map(_.toInt).getOrElse(20)

            implicit val s = Global.db.createSession()
            val users = MaybeFilter(Users.objects)
              .filter(email)(v => d => d.email === v)
              .filter(id)(v => d => d.id === v.toInt)
              .query.list

            import net.coconauts.scalarest.models.UserJsonProtocol._

            val response = Map("count" -> JsNumber(users.size),
              "limit" -> JsNumber(limitResults),
              "results" -> users.take(limitResults).toJson
            )
            /* val response = UserListResponse(count=users.size,
               limit = limitResults,
               results =  users.take(limitResults)
             )*/
            complete(response)
        }
      }
    } ~ path("v2/user" / Segment) { id =>
      get {
        logger.debug(s"Received request GET '/locum/$id' ")
        implicit val s = Global.db.createSession()
        import net.coconauts.scalarest.models.UserJsonProtocol._

        rejectEmptyResponse {
          val user = Users.objects.filter(_.id === id.toInt).list.headOption
          complete(user)
        }
      }
    } ~ path("v2/user") {
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

// We can use a case class to return the response list, but we will need a serializer too
//case class UserListResponse(count: Int, limit: Int, results: Seq[Users])
