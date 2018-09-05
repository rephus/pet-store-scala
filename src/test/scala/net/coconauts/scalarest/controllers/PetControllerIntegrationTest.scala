package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.models.PetJsonProtocol._
import net.coconauts.scalarest.models.PetStatus
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

    "return a pet by id" in {

      implicit val s = Global.db.createSession()
      val petId = Pets.insert(Pets.random)

      Get("/pet/" + petId) ~> petRoutes ~> check {
        responseAs[Pet].id === petId
      }
    }

    "Save pet" in {

      val pet = Pets.random
      Post("/pet", pet) ~> petRoutes ~> check {
        status === OK

        val savedPet = responseAs[Pet]

        savedPet.name === pet.name
        savedPet.photoUrls must not be empty
        savedPet.photoUrls === pet.photoUrls
        savedPet.status === pet.status
      }

    }

    "Updates a pet in the store with form data" in {
      implicit val s = Global.db.createSession()

      // Save a pet to overwrite later
      val oldPet = Pets.random
      val petId = Pets.insert(oldPet)
      val pet = oldPet.copy(name = "new name")

      Post("/pet/" + petId, pet) ~> petRoutes ~> check {
        status === OK

        val savedPet = Pets.get(petId).get
        savedPet.id === petId /// same petId
        savedPet.name !== oldPet.name
        savedPet.name === pet.name
      }

    }
    "Updates an existing pet" in {
      implicit val s = Global.db.createSession()

      // Save a pet to overwrite later
      val oldPet = Pets.random
      val petId = Pets.insert(oldPet)
      val pet = oldPet.copy(id=petId, name = "new name")

      Put("/pet", pet) ~> petRoutes ~> check {
        status === OK

        val savedPet = Pets.get(petId).get
        savedPet.id === petId /// same petId
        savedPet.name !== oldPet.name
        savedPet.name === pet.name
      }

    }

    "Delete pet" in {
      implicit val s = Global.db.createSession()

      // Save a pet to overwrite later
      val petId = Pets.insert(Pets.random)

      Delete("/pet/" + petId) ~> sealRoute(petRoutes) ~> check {
        status === OK

        Pets.get(petId) === None
      }

    }

    "Find by status" in {
      implicit val s = Global.db.createSession()

      // Save a pet to overwrite later
      val soldPet = Pets.random.copy(status = PetStatus.sold)
      Pets.insert(soldPet)
      Pets.insert(Pets.random.copy(status = PetStatus.pending))

      Get("/pet/findByStatus?status=sold") ~> sealRoute(petRoutes) ~> check {
        status === OK

        val pets = responseAs[List[Pet]]
        pets.size === 1
        val pet = pets.head
        pet.name === soldPet.name
        pet.status === PetStatus.sold
      }

    }
  }

}
