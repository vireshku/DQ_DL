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
import org.springframework.web.context.request.async.{DeferredResult, WebAsyncUtils}
import org.springframework.web.context.request.{NativeWebRequest, WebRequest}
import org.springframework.web.method.support.{AsyncHandlerMethodReturnValueHandler, ModelAndViewContainer}
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ScalaFutureMethodReturnValueHandler(minEstimatedTimeout: Long, durationToleranceFactor: Double)
                                         (implicit ec: ExecutionContext)
  extends DeferredResultMethodReturnValueHandler
    with AsyncHandlerMethodReturnValueHandler {

  import ScalaFutureMethodReturnValueHandler._

  override def supportsReturnType(returnType: MethodParameter): Boolean =
    isFutureClass(returnType.getParameterType) || super.supportsReturnType(returnType)

  override def isAsyncReturnValue(returnValue: scala.Any, returnType: MethodParameter): Boolean =
    isFutureClass(returnType.getParameterType)

  override def handleReturnValue(retVal: scala.Any, retType: MethodParameter, mavContainer: ModelAndViewContainer, req: NativeWebRequest): Unit = {
    retVal match {
      case future: EstimableFuture[_] =>
        val timeout = requestedTimeout(req).orElse(Some(estimatedTimeout(future)))
        startProcessing(future, timeout)

      case future: Future[_] =>
        startProcessing(future, requestedTimeout(req))

      case _ =>
        super.handleReturnValue(retVal, retType, mavContainer, req)
    }

    def estimatedTimeout(future: EstimableFuture[_]) =
      math.max(minEstimatedTimeout, (future.estimatedDuration * durationToleranceFactor).toLong)

    def startProcessing[T](future: Future[T], timeout: Option[Long]): Unit = {
      val deferredResult = toDeferredResult(future, timeout)
      WebAsyncUtils.getAsyncManager(req).startDeferredResultProcessing(deferredResult, mavContainer)
    }
  }

  private def toDeferredResult[T](returnValue: Future[T], timeout: Option[Long]): DeferredResult[T] =
    new DeferredResult[T](timeout.map(Long.box).orNull) {
      returnValue.andThen {
        case Success(value) => setResult(value)
        case Failure(error) => setErrorResult(error)
      }
    }
}

object ScalaFutureMethodReturnValueHandler {
  private[this] val TIMEOUT_HEADER = "X-SPLINE-TIMEOUT"

  private def requestedTimeout(req: WebRequest) =
    Option(req.getHeader(TIMEOUT_HEADER)).map(_.toLong)

  private def isFutureClass(clazz: Class[_]) =
    classOf[Future[_]].isAssignableFrom(clazz)
}
