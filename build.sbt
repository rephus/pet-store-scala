import org.flywaydb.sbt.FlywayPlugin.autoImport._
import sbt.Keys.{mainClass, _}
import sbt._

lazy val commonSettings = Seq(
  scalaVersion := "2.11.6",
  organization := "net.coconauts",
  version := "0.1",
  name := "pet-store"

)

val akkaV = "2.3.9"
val sprayV = "1.3.3"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

def isIntegrationTest(name: String): Boolean = name endsWith "IntegrationTest"
def isUnitTest(name: String): Boolean = !isIntegrationTest(name)

lazy val ITTest = config("it") extend Test


lazy val root = (project in file("."))

  .configs(ITTest)
  .settings(inConfig(ITTest)(Defaults.testTasks): _*)
  .settings(
    testOptions in Test := Seq(Tests.Filter(isUnitTest)),
    testOptions in ITTest := Seq(Tests.Filter(isIntegrationTest)),

    parallelExecution in Test := false,
    parallelExecution in ITTest := false,

  )
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "2.1.0",
      "postgresql" % "postgresql" % "9.1-901.jdbc4",

      "ch.qos.logback" % "logback-classic" % "1.1.7",
      //"com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
      "io.spray" %% "spray-can" % sprayV,
      "io.spray" %% "spray-json" % "1.3.2",
      "io.spray" %% "spray-routing" % sprayV,
      "io.spray" %% "spray-testkit" % sprayV % "test",
      "com.typesafe.akka" %% "akka-actor" % akkaV,
      "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",

      "org.specs2" %% "specs2" % "2.3.11" % "test",
      "com.google.code.gson" % "gson" % "2.6.2",

      "org.flywaydb" % "flyway-core" % "4.0",

      "org.slf4j" % "slf4j-api" % "1.7.2",
      "com.typesafe.akka" %% "akka-slf4j" % "2.3.15"

    )
  )
