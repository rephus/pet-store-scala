package net.coconauts.scalarest.models

import net.coconauts.scalarest.models.OrderStatus.OrderStatus
import net.coconauts.scalarest.{Global, Utils}
import spray.json._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted._
import scala.util.Random

case class Order(id: Option[Int] = None, complete: Boolean, petId: Int, quantity: Int, status: OrderStatus)


object OrderStatus extends Enumeration {
  type OrderStatus = Value
  val placed = Value("placed")
  val approved = Value("approved")
  val delivered = Value("delivered")
}


class Orders(tag: Tag) extends Table[Order](tag, "order") {


  // Mapper required for column specification
  implicit val OrderStatusMapper = MappedColumnType.base[OrderStatus, String](
    e => e.toString,
    s => OrderStatus.withName(s)
  )

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def complete = column[Boolean]("complete")
  def petId = column[Int]("pet_id")
  def quantity = column[Int]("quantity")
  def status = column[OrderStatus]("status")

  def * = (id.?, complete, petId, quantity, status) <>(Order.tupled, Order.unapply)
}

object Orders {

  import scala.slick.driver.JdbcDriver.simple._

  lazy val objects = TableQuery[Orders]

  def insert(Order: Order)(implicit session: Session): Int = {

    val inserting = objects returning objects.map(_.id)
    inserting += Order
  }

  def get(id: Int)(implicit session: Session): Option[Order] = {
    objects.filter(_.id === id).list.headOption
  }

  /**
    * Creates a new random Order, useful for tests
    */
  def random: Order = {
    Order(
      id = Some(Math.abs(Random.nextInt)),
      complete = false,
      petId = Math.abs(Random.nextInt),
      quantity = Math.abs(Random.nextInt),
      status = OrderStatus.placed
    )
  }
}

object OrderJsonProtocol extends DefaultJsonProtocol {
  //implicit val OrderFormat = jsonFormat5(Order)
  implicit object OrderFormat extends JsonFormat[Order] {
    def write(order: Order) = JsObject(
      "id" -> JsNumber(order.id.get),
      "complete" -> JsBoolean(order.complete),
      "petId" -> JsNumber(order.quantity),
      "quantity" -> JsNumber(order.quantity),
      "status" -> JsString(OrderStatus.toString)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "complete", "petId", "quantity", "status") match {
        case Seq(JsNumber(id), JsBoolean(complete), JsNumber(petId),  JsNumber(quantity), JsString(status)) =>
          new Order(id=Some(id.toInt),
              complete = complete,
              petId = petId.toInt,
              quantity = quantity.toInt,
              status = OrderStatus.withName(status))
        case _ => throw new DeserializationException("Order expected")
      }
    }
  }
}

