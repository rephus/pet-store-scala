package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.models.PetJsonProtocol._
import net.coconauts.scalarest.models.{Pet, Pets}
import net.coconauts.scalarest.{Global, PostgresTest}
import org.specs2.mutable._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.testkit.Specs2RouteTest

//http://blog.scalac.io/2015/03/27/specs2-notes.html
class PetControllerIntegrationTest extends Specification with Specs2RouteTest with PetController with PostgresTest {
  def actorRefFactory = system

  "Pet service" should {
/*

    "leave GET requests to other paths unhandled" in {
      Get("/") ~> userRoutes ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for GET requests to the `/user` path" in {
      Put("/pet") ~> sealRoute(userRoutes) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET, POST"
      }
    }

    "Get 400 error trying to save user with no arguments" in {
      Post("/user") ~> sealRoute(userRoutes) ~> check {
        status === BadRequest
        responseAs[String] === "Request entity expected but not supplied"
      }
    }
    "List users" in {
      Get("/user") ~> sealRoute(userRoutes) ~> check {

        status === OK

        val response = responseAs[Map[String, JsValue]]
        response("count").toString.toInt must be greaterThan (0)
      }
    }

*/
    "return a pet with id 1" in {

      implicit val s = Global.db.createSession()
      val petId = Pets.insert(Pets.random)

      Get("/pet/" + petId) ~> petRoutes ~> check {
        responseAs[Pet].id === petId
      }
    }


    "Save pet" in {

      val pet = Pets.random
      Post("/pet", pet) ~> sealRoute(petRoutes) ~> check {
        status === OK

        val savedPet = responseAs[Pet]

        savedPet.name === pet.name
        savedPet.photoUrls must not be empty
        savedPet.photoUrls === pet.photoUrls
        savedPet.status === pet.status
      }

    }

    "Update pet" in {
      implicit val s = Global.db.createSession()

      // Save a pet to overwrite later
      val oldPet = Pets.random
      val petId = Pets.insert(oldPet)

      val pet = Pets.random
      Put("/pet/" + petId, pet) ~> sealRoute(petRoutes) ~> check {
        status === OK

        val savedPet = responseAs[Pet]
        savedPet.id === Some(petId) /// same petId
        savedPet.name !== oldPet.name
        savedPet.name === pet.name
      }

    }
  }

}
