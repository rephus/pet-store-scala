package net.coconauts.scalarest.models

import net.coconauts.scalarest.models.PetJsonProtocol._
import org.specs2.mutable.Specification
import spray.json._
import net.coconauts.scalarest.models.PetJsonProtocol._

class PetTest extends Specification {

  "Pet" should {

    "should transform to JSON" in {

      val pet = Pets.random

      val json = pet.toJson

      json.asJsObject.getFields("name").head.convertTo[String] === pet.name

    }

    "should parse from JSON" in {

      val pet = Pets.random

      val map: Map[String, JsValue] = Map(
        "id" -> JsNumber(pet.id.get),
        "name" -> JsString(pet.name),
        "photoUrls" -> JsArray(pet.photoUrls.map(JsString(_))),
        "status" -> JsString(pet.status.toString)
      )
      val json = map.toJson

      PetJsonProtocol.petFormat.read(json) === pet
    }

  }
}
