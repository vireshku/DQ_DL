/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.conf

import org.apache.commons.configuration.AbstractConfiguration

abstract class ReadOnlyConfiguration extends AbstractConfiguration {
  override def addPropertyDirect(key: String, value: scala.Any): Unit = throw new UnsupportedOperationException
}
