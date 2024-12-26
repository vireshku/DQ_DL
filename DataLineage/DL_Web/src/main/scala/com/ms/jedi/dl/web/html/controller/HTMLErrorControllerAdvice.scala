/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.html.controller

import org.springframework.http.HttpStatus.{INTERNAL_SERVER_ERROR, NOT_FOUND, SERVICE_UNAVAILABLE}
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler, ResponseStatus}
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.servlet.ModelAndView
import com.ms.jedi.dl.web.exception.LineageNotFoundException
import com.ms.jedi.dl.web.logging.ErrorCode

@ControllerAdvice(basePackageClasses = Array(classOf[_package]))
class HTMLErrorControllerAdvice {

  @ExceptionHandler(Array(
    classOf[NoSuchElementException]
  ))
  @ResponseStatus(NOT_FOUND)
  def handle_404_generic = new ModelAndView("errors/generic", "message", "resource not found")

  @ExceptionHandler(Array(classOf[LineageNotFoundException]))
  @ResponseStatus(NOT_FOUND)
  def handle_404_lineage = new ModelAndView("errors/generic", "message", "lineage not found")

  @ExceptionHandler
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  def handle_500(e: Throwable): ModelAndView =
    new ModelAndView("errors/generic")
      .addObject("message", "Oops! Something went wrong")
      .addObject("error_code", ErrorCode(e).error_id)

  @ExceptionHandler(Array(
    classOf[AsyncRequestTimeoutException]
  ))
  @ResponseStatus(SERVICE_UNAVAILABLE)
  def handle_503(e: Throwable): ModelAndView =
    new ModelAndView("errors/generic")
      .addObject("message", "Request timed out. Please try again later.")
      .addObject("error_code", ErrorCode(e).error_id)
}
