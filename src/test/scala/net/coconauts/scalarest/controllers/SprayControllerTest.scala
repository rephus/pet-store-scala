package net.coconauts.scalarest.controllers

import akka.testkit.TestActorRef
import org.specs2.mutable._
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest

class SprayControllerTest extends Specification with Specs2RouteTest with HttpService {
  def actorRefFactory = system

  val sprayActor = TestActorRef(new SprayActor)

  "Spray controller" should {

    "Send complete message to actor" in {

      sprayActor ! complete("ok")
      true
    }
  }
}
