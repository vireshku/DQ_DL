/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.common.transformations

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future.successful



/**
  * The class represents a pipeline that gradually applies transformations onto a input instance.
  *
  * @param transformations A sequence of transformations
  * @tparam T A type of a transformed instance
  */
class AsyncTransformationPipeline[T](transformations: AsyncTransformation[T]*) extends AsyncTransformation[T] {

  /**
    * The method transforms a input instance by a logic of inner transformations.
    *
    * @param input An input instance
    * @return A transformed result
    */
  def apply(input: T)(implicit ec: ExecutionContext): Future[T] = (successful(input) /: transformations) (_ flatMap _.apply)
}
