

package com.ms.jedi.dl.common

import org.apache.commons.lang.time.StopWatch
import org.slf4s.Logging

object TimeTracingUtils extends Logging {
  def logTime[T](msg: String)(body: => T): T = {
    val sw = new StopWatch()
    sw.start()
    try body
    finally
      log.info(s"$msg:\t${sw.getTime}")
  }
}
