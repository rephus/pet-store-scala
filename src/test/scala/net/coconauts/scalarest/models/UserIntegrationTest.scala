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

      val user = Users.objects.filter(_.id === id).list.head

      randomUser.email === user.email
    }

    "Create" in {

      implicit val s = Global.db.createSession()

      val randomUser = Users.random

      println("Saving random user ", randomUser)
      val noneUser = Users.objects.filter(_.email === randomUser.email).list.headOption
      noneUser must beNone

      val createUserId = Users.insert(randomUser)
      val createUser = Users.get(createUserId).get

      randomUser.email === createUser.email
      createUser.id must beSome[Int]

    }
  }

}
