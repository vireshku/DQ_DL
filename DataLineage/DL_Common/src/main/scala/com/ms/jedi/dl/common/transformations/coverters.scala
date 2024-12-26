/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.common.transformations

import scala.collection.mutable

trait AbstractConverter {
  type From
  type To

  def convert(arg: From): To
}

trait CachingConverter extends AbstractConverter {
  private val cache = mutable.LinkedHashMap.empty[From, To]

  def values: Seq[To] = cache.values.toSeq

  abstract override def convert(arg: From): To =
    cache.getOrElseUpdate(arg, super.convert(arg))
}

