/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.coreadapterapi

import org.apache.spark

trait SparkVersionRequirement {

  protected val versionPrefix: String

  def requireSupportedVersion(): Unit = {
    require(spark.SPARK_VERSION.startsWith(versionPrefix), s"Unsupported Spark version: ${spark.SPARK_VERSION}. Required version $versionPrefix.*.")
  }

}

object SparkVersionRequirement extends AdapterFactory[SparkVersionRequirement]
