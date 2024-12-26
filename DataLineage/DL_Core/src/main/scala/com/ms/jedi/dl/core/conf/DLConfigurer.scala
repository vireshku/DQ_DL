/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.conf

import com.ms.jedi.dl.core.harvester.QueryExecutionEventHandler

/**
 * The trait describes settings needed for initialization of the library.
 */
trait DLConfigurer {

  import DLConfigurer.DLMode._

  /**
   * A listener handling events from batch processing
   * @return [[QueryExecutionEventHandler]]
   */
  def queryExecutionEventHandler: QueryExecutionEventHandler

  /**
   * DataLineage mode designates how DataLineage should behave in a context of a Spark application.
   * It mostly relates to error handling. E.g. is lineage tracking is mandatory for the given Spark app or is it good to have.
   * Should the Spark app be aborted on DataLineage errors or not.
   *
   * @see [[DLMode]]
   * @return [[DLMode]]
   */
  def dlMode: DLMode
}

object DLConfigurer {

  object DLMode extends Enumeration {
    type DLMode = Value
    val
    
    /**
      * DL is disabled completely
      */ 
    DISABLED, 
    
    /**
      * Abort on DL initialization errors
      */ 
    
    REQUIRED, 
    
    /**
      * If DL initialization fails then disable Spline and continue without lineage tracking
      */ 
    
    BEST_EFFORT = Value
  }

}