/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.rest.controller

import org.springframework.http.HttpStatus.{INTERNAL_SERVER_ERROR, NOT_FOUND}
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import com.ms.jedi.dl.web.NonStandardResponseEntity
import com.ms.jedi.dl.web.json.StringJSONConverters.EntityToJson
import com.ms.jedi.dl.web.logging.ErrorCode

@ControllerAdvice(basePackageClasses = Array(classOf[_package]))
class RESTErrorControllerAdvice {

  @ExceptionHandler(Array(
    classOf[NoSuchElementException]
  ))
  def handle_404 = new ResponseEntity(NOT_FOUND)

  @ExceptionHandler
  def handle_500(e: Throwable) = new ResponseEntity(ErrorCode(e).toJson, INTERNAL_SERVER_ERROR)

  @ExceptionHandler(Array(
    classOf[AsyncRequestTimeoutException]
  ))
  def handle_598(e: AsyncRequestTimeoutException) = NonStandardResponseEntity(598, ErrorCode(e).toJson)
}
