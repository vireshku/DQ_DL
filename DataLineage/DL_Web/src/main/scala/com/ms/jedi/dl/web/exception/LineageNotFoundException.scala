/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.exception

/**
  * Signals that a requested lineage was not found
  */
class LineageNotFoundException(criteria: String) extends RuntimeException(s"Lineage not found by criteria: $criteria")
