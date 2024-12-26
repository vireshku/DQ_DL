/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.conf

import org.apache.spark.SparkConf
import java.util
import scala.collection.JavaConverters._

/**
 * {@link org.apache.spark.SparkConf} to {@link org.apache.commons.configuration.Configuration} adapter
 *
 * @param conf A source of Spark configuration
 */
class SparkConfiguration(conf: SparkConf) extends ReadOnlyConfiguration {

  import SparkConfiguration._

  override def getProperty(key: String): AnyRef =
    try
      conf get s"$KEY_PREFIX$key"
    catch {
      case _: NoSuchElementException => null
    }

  override def getKeys: util.Iterator[String] = conf.getAll.iterator.map(_._1.drop(KEY_PREFIX.length)).asJava

  override def containsKey(key: String): Boolean = conf contains s"$KEY_PREFIX$key"

  override def isEmpty: Boolean = conf.getAll.isEmpty
}

object SparkConfiguration {
  private val KEY_PREFIX = "spark."
}