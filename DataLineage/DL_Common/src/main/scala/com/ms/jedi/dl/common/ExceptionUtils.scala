package com.ms.jedi.dl.common

object ExceptionUtils {

  def `not applicable`: Nothing =
    throw new AssertionError("This statement should never be executed. If it happened then it's a clear indication of a programming mistake.")

}
