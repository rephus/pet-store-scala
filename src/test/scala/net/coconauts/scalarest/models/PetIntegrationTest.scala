package net.coconauts.scalarest.models

import net.coconauts.scalarest.{Global, PostgresTest}
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification

import scala.slick.driver.JdbcDriver.simple._

class PetIntegrationTest extends Specification with JsonMatchers with PostgresTest {

  "Db test" should {

    "Correctly save pet in database" in {

      implicit val s = Global.db.createSession()

      val id = Pets.insert(Pets.random)
      id must be greaterThan (0)
    }
    "Correctly save multiple pets in database" in {

      implicit val s = Global.db.createSession()

      val id = Pets.insert(Pets.random)
      id must be greaterThan (0)
      val rows = Pets.objects.list.size
      rows must be greaterThan(0)

      val newId = Pets.insert(Pets.random)
      id !== newId

      rows < Pets.objects.list.size //rows increased

    }
    "Correctly get pet from database" in {

      implicit val s = Global.db.createSession()

      val randompet = Pets.random
      val id = Pets.insert(randompet)

      val pet = Pets.get(id).get

      randompet.name === pet.name
      randompet.photoUrls === pet.photoUrls
      randompet.status === pet.status
    }

    "Update pet in database" in {

      implicit val s = Global.db.createSession()

      val oldPet = Pets.random
      val id = Pets.insert(oldPet)

      val pet = oldPet.copy(id=id, name="new name")
      Pets.update(pet)

      val updatedPet = Pets.get(id).get
      updatedPet.status === oldPet.status
      updatedPet.photoUrls === oldPet.photoUrls

      updatedPet.name === "new name"
      updatedPet.name !== oldPet.name
    }


    "Delete pet in database" in {

      implicit val s = Global.db.createSession()

      val oldPet = Pets.random
      val id = Pets.insert(oldPet)

      Pets.delete(id)

      val deletedPet = Pets.get(id)
      deletedPet === None

      Pets.objects.list.size must be greaterThan(0)

    }
  }

}
