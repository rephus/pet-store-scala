package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.models.UserJsonProtocol._
import net.coconauts.scalarest.models.{User, Users}
import net.coconauts.scalarest.{Global, PostgresTest}
import org.specs2.mutable._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.testkit.Specs2RouteTest
import spray.json._

//http://blog.scalac.io/2015/03/27/specs2-notes.html
class UserControllerIntegrationTest extends Specification with Specs2RouteTest with UserController with PostgresTest {
  def actorRefFactory = system

  "User service" should {

    "leave GET requests to other paths unhandled" in {
      Get("/") ~> userRoutes ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for GET requests to the `/user` path" in {
      Put("/user") ~> sealRoute(userRoutes) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: POST"
      }
    }

    "Get 400 error trying to save user with no arguments" in {
      Post("/user") ~> sealRoute(userRoutes) ~> check {
        status === BadRequest
        responseAs[String] === "Request entity expected but not supplied"
      }
    }

    "Save user" in {

      val user = Users.random
      Post("/user", user) ~> sealRoute(userRoutes) ~> check {
        status === OK

        val savedUser = responseAs[User]
        savedUser.email === user.email
      }

    }
  }

}
