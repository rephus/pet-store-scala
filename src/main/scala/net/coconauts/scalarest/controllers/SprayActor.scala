package net.coconauts.scalarest.controllers

import akka.actor.Actor

class SprayActor extends Actor
  with UserController
  with PetController
  {

  def actorRefFactory = context

  val routes = {
    userRoutes ~ petRoutes
  }

  def receive = runRoute(routes)
}



