/**
 * Utility class for parsing the key value propeties file
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.db.util

import java.util.Properties
import scala.io.Source

object KeyValParser {

  def kv(): Properties = {
    var properties: Properties = null
    val url = getClass.getResource("/core.properties")
    if (url != null) {
      val source = Source.fromURL(url)
      properties = new Properties()
      properties.load(source.bufferedReader())
    }
    properties
  }

  def main(args: Array[String]): Unit = {
    println(kv.getProperty("refreshurl"))
  }
}