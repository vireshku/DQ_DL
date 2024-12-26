/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.handler

import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.{HandlerMethodReturnValueHandler, ModelAndViewContainer}
import com.ms.jedi.dl.web.handler.UnitMethodReturnValueHandler.unitClasses

import scala.runtime.BoxedUnit

object UnitMethodReturnValueHandler {
  private val unitClasses = Seq(
    classOf[Unit],
    classOf[BoxedUnit])
}

class UnitMethodReturnValueHandler extends HandlerMethodReturnValueHandler {

  override def supportsReturnType(returnType: MethodParameter): Boolean =
    unitClasses contains returnType.getGenericParameterType

  override def handleReturnValue(rv: scala.Any, rt: MethodParameter, mavContainer: ModelAndViewContainer, wr: NativeWebRequest): Unit =
    mavContainer.setRequestHandled(true)
}