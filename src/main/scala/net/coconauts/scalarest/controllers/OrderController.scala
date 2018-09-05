package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.Global
import net.coconauts.scalarest.models.OrderStatus.OrderStatus
import net.coconauts.scalarest.models.OrderJsonProtocol._
import net.coconauts.scalarest.models._
import org.slf4j.LoggerFactory
import spray.httpx.SprayJsonSupport._
import spray.json.{JsNumber, JsObject}
import spray.routing._

import scala.slick.driver.JdbcDriver.simple._

trait OrderController extends HttpService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val orderRoutes =
    path("store" / "order" / IntNumber) { id =>
      pathEnd {
        get {
          complete {
            implicit val s = Global.db.createSession()
            Orders.get(id)
          }
        } ~
        delete {

          complete{
            logger.info(s"Received request DELETE '/store/orders/$id' ")

            implicit val s = Global.db.createSession()

            Orders.delete(id)
            ""
          }
        }
      }
    } ~ path("store" / "order") {
      post {
        //This entity parses all the PUT body and convert it into a Pet model
        entity(as[Order]) { order =>

          complete{
            logger.debug(s"Received request POST '/store/order' $order")
            implicit val s = Global.db.createSession()

            val id = Orders.insert(order)
            logger.info(s"Inserted order with id ${id}")
            val insertedOrder = Orders.get(id).get

            insertedOrder
          }
        }
      }
    }  ~ path("store" / "inventory") {
      get {
        //TODO this endpoint is not optimized, there are 2 things that can be done here:
        // 1: replace manual made JSON response with Inventory case class and JSOnformat
        // 2: Use group by status to return the results in just 1 query (necessary if more status are going to be added in the future)

        implicit val OrderStatusMapper = MappedColumnType.base[OrderStatus, String](
          e => e.toString,
          s => OrderStatus.withName(s)
        )
        implicit val s = Global.db.createSession()

        complete {
          JsObject(
            "placed" -> JsNumber(Orders.objects.filter(_.status === OrderStatus.placed).list.size),
            "approved" -> JsNumber(Orders.objects.filter(_.status === OrderStatus.approved).list.size),
            "delivered" -> JsNumber(Orders.objects.filter(_.status === OrderStatus.delivered).list.size)
          )
        }
      }
    }

}
