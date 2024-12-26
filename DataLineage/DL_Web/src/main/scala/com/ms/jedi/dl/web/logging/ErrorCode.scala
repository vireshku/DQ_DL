/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.logging

import java.util.UUID
import java.util.UUID.randomUUID

/**
  * <p>
  * An unexpected exception wrapper that is aimed for being sent to a client as a result of a failed operation.
  * ErrorCode encapsulates a unique error identifier (UUID) and logs the underlying exception along with that ID,
  * so that it is easily identifiable in the logs afterwards (useful for the client support and bug tracing purposes).
  * </p>
  *
  * <h1>Usage:</h1>
  *
  * <p>
  * Whenever an unexpected operation is occurred during processing any client request, instead of propagating the exception
  * to the client or silently logging it and hide from the client, simply create an ErrorCode instance with that exception
  * and return it as a response body along with an appropriate response status code (e.g. 500).
  * </p>
  * <p>
  * The underlying exception will be given a unique identifier, logged (via Logger) with that identifier and only the identifier
  * will be sent to the client.
  * </p>
  */
case class ErrorCode(error_id: UUID)

case object ErrorCode extends Logging {

  /**
    * Create an ErrorCode instance and log the given exception.
    *
    * @param e an exception to be tagged and logged
    * @return instance of ErrorCode encapsulating a unique identifier with which the given exception was logged.
    */
  def apply(e: Throwable): ErrorCode = {
    val errorCode = ErrorCode(randomUUID)
    log.error(errorCode.toString, e)
    errorCode
  }
}
