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

    "Correctly get pet from database" in {

      implicit val s = Global.db.createSession()

      val randompet = Pets.random
      val id = Pets.insert(randompet)

      val pet = Pets.get(id).get

      randompet.name === pet.name
      randompet.photoUrls === pet.photoUrls
      randompet.status === pet.status
    }
  }

}
