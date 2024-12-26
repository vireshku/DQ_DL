/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web

import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.util.MultiValueMap

case class NonStandardResponseEntity[T](statusCode: Int, body: T = null, headers: MultiValueMap[String, String] = null)
  extends ResponseEntity(body, headers, HttpStatus.SEE_OTHER) {

  require(HttpStatus.resolve(statusCode) == null, "For standard response codes, use ResponseEntity class instead")

  override def getStatusCodeValue: Int = statusCode

  override def toString: String = {
    val builder = new StringBuilder("<")
    builder.append(statusCode)
    builder.append(',')
    if (body != null) {
      builder.append(body)
      builder.append(',')
    }
    builder.append(getHeaders)
    builder.append('>')
    builder.toString
  }
}
