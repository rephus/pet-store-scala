package net.coconauts.scalarest.models

import net.coconauts.scalarest.{Global, PostgresTest}
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification

class OrderIntegrationTest extends Specification with JsonMatchers with PostgresTest {

  "Db test" should {

    "Correctly save order in database" in {

      implicit val s = Global.db.createSession()

      val id = Orders.insert(Orders.random)
      id must be greaterThan (0)
    }

    "Correctly get Order from database" in {

      implicit val s = Global.db.createSession()

      val randomOrder = Orders.random
      val id = Orders.insert(randomOrder)

      val Order = Orders.get(id).get

      randomOrder.petId === Order.petId
      randomOrder.quantity === Order.quantity
      randomOrder.complete === Order.complete
      randomOrder.status === Order.status
    }
  }

}
