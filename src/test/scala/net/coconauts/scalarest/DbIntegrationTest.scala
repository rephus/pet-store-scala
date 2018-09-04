package net.coconauts.scalarest

import net.coconauts.scalarest.models.Users
import org.specs2.mutable.Specification

import scala.slick.driver.JdbcDriver.simple._

class DbIntegrationTest extends Specification with PostgresTest {

  "Db test" should {

    "Correctly save user in database" in {

      implicit val s = Global.db.createSession()

      val id = Users.insert(Users.random)
      id must be greaterThan (0)
    }

    "Correctly get user from database" in {

      implicit val s = Global.db.createSession()

      val randomUser = Users.random
      val id = Users.insert(randomUser)

      val user = Users.objects.filter(_.id === id).list.head

      randomUser.email === user.email
    }

    "Check we clean the database between tests" in {

      implicit val s = Global.db.createSession()

      Users.objects.list.size must be lessThan (10)
    }
  }

}
