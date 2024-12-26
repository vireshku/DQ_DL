/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.initial

import java.util.Properties
import com.ms.jedi.dl.common.ARM._


object DLBuildInfo {

  private val buildProps =
    using(this.getClass getResourceAsStream "/build.properties") { stream =>
      new Properties() {
        load(stream)
      }
    }

  val version: String = buildProps getProperty "build.version"
  val timestamp: String = buildProps getProperty "build.timestamp"
}
