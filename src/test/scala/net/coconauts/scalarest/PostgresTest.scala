package net.coconauts.scalarest

import net.coconauts.scalarest.FlywaySettings._
import org.flywaydb.core.Flyway

trait PostgresTest {

  //Run migrations once, triggered by this interface
  migrate()

}

object FlywaySettings {

  val conf = Global.conf

  var triggered = false

  def migrate() {

    if (triggered) return
    triggered = true

    val flyway = new Flyway()

    flyway.setDataSource(conf.getString("db.url"), conf.getString("db.user"), conf.getString("db.password"))

    //This runs on class initialization (aka when the test starts, not between tests)
    println("Running Flyway migration on test db")
    println("Trying to clean existing DB")

    flyway.clean()

    flyway.migrate()

  }

}