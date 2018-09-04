package net.coconauts.scalarest

import java.sql.Timestamp

import org.slf4j.LoggerFactory
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.slick.lifted.{CanBeQueryCondition, Query}
import scala.util.Random

object Utils {

  private val logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Wrap a function to retry a few times,
    * useful on tests that involves external apis, async process (akka) or background processors
    *
    * @param n     number of times to retry
    * @param sleep milisecods to sleep between retries
    * @param fn    function
    * @return This method will return the same result of the function provided, or an exception
    */
  def retry[T](n: Int, sleep: Int = 1000)(fn: => T): T = {
    try {
      fn
    } catch {
      case e: Throwable =>
        if (n > 1) {
          logger.warn(s"Got error on retry function: $e, retrying...")
          Thread.sleep(sleep)
          retry(n - 1)(fn)
        } else throw e
    }
  }

  //Generates a random string, useful on factories or tests
  def randomString = Random.alphanumeric.take(Random.nextInt(10)).mkString

  /**
    * Nested filter on a slick database model, only if the filter is provided,
    * usage: val users = MaybeFilter(Users.objects)
    * .filter(id)(v => d => d.id === v.toInt)
    * .filter(email)(v => d => d.email === v.toInt)
    * .query.list
    */
}

case class MaybeFilter[X, Y](val query: Query[X, Y, Seq]) {
  def filter[T, R: CanBeQueryCondition](data: Option[T])(f: T => X => R) = {
    data.map(v => MaybeFilter(query.withFilter(f(v)))).getOrElse(this)
  }
}
