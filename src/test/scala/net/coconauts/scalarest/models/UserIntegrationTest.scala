package net.coconauts.scalarest.models

import net.coconauts.scalarest.{Global, PostgresTest}
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification

import scala.slick.driver.JdbcDriver.simple._

class UserIntegrationTest extends Specification with JsonMatchers with PostgresTest {

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

      val user = Users.get(id).get

      randomUser.email === user.email
      randomUser.password === user.password

    }
  }

}
