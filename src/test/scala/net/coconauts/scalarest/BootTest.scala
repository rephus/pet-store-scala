package net.coconauts.scalarest

import net.coconauts.scalarest.controllers.UserController
import net.coconauts.scalarest.models.Users
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest

import scala.slick.driver.JdbcDriver.simple._

class BootTest extends Specification with Specs2RouteTest with PostgresTest {
  def actorRefFactory = system

  "Boot test" should {

    "Load boot successfully" in {
      Boot.conf === null
      true // we don't care about any value, if something is wrong on boot, this will fail
    }
  }

}
