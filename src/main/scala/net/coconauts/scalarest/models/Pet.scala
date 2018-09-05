package net.coconauts.scalarest.models

import net.coconauts.scalarest.models.PetStatus.PetStatus
import net.coconauts.scalarest.{Global, Utils}
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted._
import scala.util.Random
import scala.util.regexp.Base

case class Pet(id: Option[Int] = None, name: String, photoUrls: List[String], status: PetStatus)


object PetStatus extends Enumeration {
  type PetStatus = Value
  val available = Value("available")
  val pending = Value("pending")
  val sold = Value("sold")
}


class Pets(tag: Tag) extends Table[Pet](tag, "pet") {


  // Mapper required for column specification
  implicit val petStatusMapper = MappedColumnType.base[PetStatus, String](
    e => e.toString,
    s => PetStatus.withName(s)
  )
  implicit val photoUrlMapper = MappedColumnType.base[List[String], String](
    l => l.mkString(","), // array to string
    s => s.split(",").toList // string to array
  )

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")
  def photoUrls = column[List[String]]("photo_urls")
  def status = column[PetStatus]("status")

  def * = (id.?, name, photoUrls, status) <>(Pet.tupled, Pet.unapply)
}

object Pets {

  import scala.slick.driver.JdbcDriver.simple._

  lazy val objects = TableQuery[Pets]

  def insert(Pet: Pet)(implicit session: Session): Int = {

    val inserting = objects returning objects.map(_.id)
    inserting += Pet
  }

  def get(id: Int)(implicit session: Session): Option[Pet] = {
    objects.filter(_.id === id).list.headOption
  }

  /**
    * Creates a new random Pet, useful for tests
    */
  def random: Pet = {
    Pet(
      id = Some(Math.abs(Random.nextInt)),
      name = Utils.randomString,
      photoUrls = List(Utils.randomString),
      status = PetStatus.available
    )
  }

}



/*
implicit def PetStatusFormat: RootJsonFormat[enum.Value] = {
  //implicit val PetFormat = jsonFormat5(Pet)
  def write(petStatus: PetStatus) = JsString(petStatus.toString)
  def read(value: JsString) = PetStatus.withName(value.value)
}*/

object PetJsonProtocol extends DefaultJsonProtocol {
  //implicit val PetFormat = jsonFormat5(Pet)

  //implicit val petFormat = jsonFormat4(Pet)

  implicit object petFormat extends RootJsonFormat[Pet] {
    def write(pet: Pet) = JsObject(
      "id" -> JsNumber(pet.id.get),
      "name" -> JsString(pet.name),
      "photoUrls" -> JsArray(pet.photoUrls.map(JsString(_))),
      "status" -> JsString(pet.status.toString)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "name", "photoUrls", "status") match {
        case Seq(JsNumber(id), JsString(name), JsArray(photoUrls),  JsString(status)) =>
          new Pet(id=Some(id.toInt),
              name=name,
              photoUrls = photoUrls.map(_.convertTo[String]).to[List],
              status = PetStatus.withName(status))
        case _ => throw new DeserializationException("Pet expected")
      }
    }
  }
}

