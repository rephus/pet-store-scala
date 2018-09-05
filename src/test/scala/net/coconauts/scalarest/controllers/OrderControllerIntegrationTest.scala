package net.coconauts.scalarest.controllers

import net.coconauts.scalarest.models.OrderJsonProtocol._
import net.coconauts.scalarest.models._
import net.coconauts.scalarest.{Global, PostgresTest}
import org.specs2.mutable._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.testkit.Specs2RouteTest

//http://blog.scalac.io/2015/03/27/specs2-notes.html
class OrderControllerIntegrationTest extends Specification with Specs2RouteTest with OrderController with PostgresTest {
  def actorRefFactory = system

  "Order service" should {

    "Save order" in {

      val order = Orders.random
      Post("/store/order", order) ~> orderRoutes ~> check {
        status === OK

        val savedOrder = responseAs[Order]

        savedOrder.petId === order.petId
        savedOrder.quantity === order.quantity
      }

    }
    "Delete order" in {
      implicit val s = Global.db.createSession()

      // Save a pet to overwrite later
      val orderId = Orders.insert(Orders.random)

      Delete("/store/order/" + orderId) ~> sealRoute(orderRoutes) ~> check {
        status === OK

        Pets.get(orderId) === None
      }


      "Get order" in {
        implicit val s = Global.db.createSession()

        // Save a pet to overwrite later
        val order = Orders.random
        val orderId = Orders.insert(order)

        val newOrder = Orders.get(orderId)

        order.copy(id=orderId) === newOrder.get

        Get("/store/order/" + orderId) ~> sealRoute(orderRoutes) ~> check {
          status === OK

          val savedOrder = responseAs[Order]
          /*
          savedOrder.id === orderId
          savedOrder.petId === order.petId
          savedOrder.quantity === order.quantity
          savedOrder.status === order.status
          */
          order.copy(id=orderId) === savedOrder

        }

      }

      "Inventory by status" in {
        implicit val s = Global.db.createSession()

        // Save a pet to overwrite later
        Orders.insert(Orders.random.copy(status = OrderStatus.placed))
        Orders.insert(Orders.random.copy(status = OrderStatus.approved))
        Orders.insert(Orders.random.copy(status = OrderStatus.delivered))
        Orders.insert(Orders.random.copy(status = OrderStatus.delivered))

        Get("/store/inventory") ~> sealRoute(orderRoutes) ~> check {
          status === OK

          val response = responseAs[Map[String,Int]]
          response("placed") must be greaterThan(0)
          response("approved") must be greaterThan(0)
          response("delivered") must be greaterThan(1)
        }

      }
    }
  }

}
