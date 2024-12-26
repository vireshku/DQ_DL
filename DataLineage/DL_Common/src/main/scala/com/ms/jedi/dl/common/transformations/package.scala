/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.common

import scala.concurrent.{ExecutionContext, Future}

/**
  * THe package contains various lineage transformers.
  */
package object transformations {

  /**
    * Abstract asynchronous transformer trait.
    * @tparam T a type of a value being transformed
    */
  trait AsyncTransformation[T] {
    def apply(input: T)(implicit ec: ExecutionContext): Future[T]
  }

}
