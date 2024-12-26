/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.logging

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  @transient
  protected lazy val log: Logger = LoggerFactory getLogger getClass
}
