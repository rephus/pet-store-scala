package net.coconauts.scalarest.models

import net.coconauts.scalarest.{Global, Utils}
import spray.json._

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.lifted._
import scala.util.Random

case class User(id: Option[Int] = None, email: String = "", password: String = "")

class Users(tag: Tag) extends Table[User](tag, "user") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def email = column[String]("email")
  def password = column[String]("password")


  def * = (id.?, email, password) <>(User.tupled, User.unapply)
}

object Users {

  import scala.slick.driver.JdbcDriver.simple._

  lazy val objects = TableQuery[Users]

  def insert(user: User)(implicit session: Session): Int = {

    val inserting = objects returning objects.map(_.id)
    inserting += user
  }

  def get(id: Int)(implicit session: Session): Option[User] = {
    objects.filter(_.id === id).list.headOption
  }

  /**
    * Creates a new random user, useful for tests
    */
  def random: User = {
    User(
      id = Some(Math.abs(Random.nextInt)),
      email = s"${Utils.randomString}@${Utils.randomString}.com",
      password = Utils.randomString
    )
  }

}

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(User)
}

