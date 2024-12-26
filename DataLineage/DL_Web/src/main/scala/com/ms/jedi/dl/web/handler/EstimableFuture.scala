/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.handler

import scala.collection.concurrent.{Map, TrieMap}
import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{CanAwait, ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

class EstimableFuture[+T](underlyingFuture: Future[T], avgDuration: () => Long) extends Future[T] {
  override def onComplete[U](f: Try[T] => U)(implicit executor: ExecutionContext): Unit = underlyingFuture.onComplete(f)

  override def isCompleted: Boolean = underlyingFuture.isCompleted

  override def value: Option[Try[T]] = underlyingFuture.value

  override def ready(atMost: Duration)(implicit permit: CanAwait): EstimableFuture.this.type = {
    underlyingFuture.ready(atMost)
    this
  }

  override def result(atMost: Duration)(implicit permit: CanAwait): T = underlyingFuture.result(atMost)

  lazy val estimatedDuration: Long = avgDuration()
}

object EstimableFuture {

  trait Implicits {

    implicit class FutureToDeferredResultAdapterImpl[T](val future: Future[T]) extends FutureToDeferredResultAdapter[T]

  }

  private[this] val durationMeasurersByCategory: Map[String, MovingAverageCalculator] = TrieMap.empty

  trait FutureToDeferredResultAdapter[T] {
    val future: Future[T]

    def asEstimable(category: String)(implicit executor: ExecutionContext): EstimableFuture[T] = {
      val durationMeasurer = durationMeasurersByCategory.getOrElseUpdate(category, {
        new MovingAverageCalculator(10.seconds.toMillis, 0.05)
      })
      val startTime = System.currentTimeMillis
      future.onSuccess { case _ => durationMeasurer.addMeasurement(System.currentTimeMillis - startTime) }
      new EstimableFuture(future, durationMeasurer.currentAverage _)
    }
  }

}

