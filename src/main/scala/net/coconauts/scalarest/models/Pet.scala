package net.coconauts.scalarest.models

import net.coconauts.scalarest.models.PetStatus.PetStatus
import net.coconauts.scalarest.{Global, Utils}
import org.slf4j.LoggerFactory
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted._
import scala.util.Random
import scala.util.regexp.Base
import scala.slick.jdbc.{GetResult, StaticQuery}

case class Pet(id: Int, name: String, photoUrls: List[String], status: PetStatus)


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

  def * = (id, name, photoUrls, status) <>(Pet.tupled, Pet.unapply)
}

object Pets {

  private val logger = LoggerFactory.getLogger(this.getClass)

  import scala.slick.driver.PostgresDriver.simple._

  lazy val objects = TableQuery[Pets]

  def insert(pet: Pet)(implicit session: Session): Int = {
    //val id = StaticQuery.queryNA[Int]("select nextval('pet_id_seq')").first
    //objects.insert(pet.copy(id=id))
    //print("INSERTED" , id)
    //id
    val inserting = objects returning objects.map(_.id)
    inserting += pet
  }
  def update(pet: Pet)(implicit session: Session) = {
    logger.info("Updating pet " + pet.id)
    objects.filter(_.id === pet.id).update(pet)
  }

  def delete(id: Int)(implicit session: Session) = {
    logger.info("Deleting pet " + id)
    objects.filter(_.id === id).delete
  }

  def get(id: Int)(implicit session: Session): Option[Pet] = {
    objects.filter(_.id === id).list.headOption
  }

  /**
    * Creates a new random Pet, useful for tests
    */
  def random: Pet = {
    Pet(
      id = Math.abs(Random.nextInt),
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
      "id" -> JsNumber(pet.id),
      "name" -> JsString(pet.name),
      "photoUrls" -> JsArray(pet.photoUrls.map(JsString(_))),
      "status" -> JsString(pet.status.toString)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "name", "photoUrls", "status") match {
        case Seq(JsNumber(id), JsString(name), JsArray(photoUrls),  JsString(status)) =>
          new Pet(id=id.toInt,
              name=name,
              photoUrls = photoUrls.map(_.convertTo[String]).to[List],
              status = PetStatus.withName(status))
        case _ => throw new DeserializationException("Pet expected")
      }
    }
  }
}

