/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.handler

import java.util.concurrent.atomic.AtomicLong
import java.util.function.LongBinaryOperator

class MovingAverageCalculator(initialValue: Long, alpha: Double) {

  private val lastAvg = new AtomicLong(initialValue)

  def currentAverage: Long = lastAvg.get

  def addMeasurement(elapsedTime: Long): Unit =
    lastAvg.accumulateAndGet(elapsedTime, new LongBinaryOperator {
      override def applyAsLong(left: Long, right: Long): Long =
        (alpha * elapsedTime + (1 - alpha) * lastAvg.get).toLong
    })
}
