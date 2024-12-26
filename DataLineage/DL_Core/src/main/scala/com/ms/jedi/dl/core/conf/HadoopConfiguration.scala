/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.conf

import java.util

import org.apache.hadoop.conf.{Configuration => SparkHadoopConf}

import scala.collection.JavaConverters._


/**
 * {@link org.apache.hadoop.conf.Configuration} to {@link org.apache.commons.configuration.Configuration} adapter
 *
 * @param shc A source of Hadoop configuration
 */
class HadoopConfiguration(shc: SparkHadoopConf) extends ReadOnlyConfiguration {

  override def getProperty(key: String): AnyRef = shc get key

  override def getKeys: util.Iterator[String] = shc.iterator.asScala.map(_.getKey).asJava

  override def containsKey(key: String): Boolean = Option(shc get key).isDefined

  override def isEmpty: Boolean = shc.size < 1
}
