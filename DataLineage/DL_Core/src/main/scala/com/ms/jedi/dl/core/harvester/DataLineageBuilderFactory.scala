/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.core.harvester


import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.execution.SparkPlan

import scala.language.postfixOps
import com.ms.jedi.dl.coreadapterapi.WriteCommandParserFactory

/** The class is actually responsible for gathering lineage information from Spark logical plan
 *
 * @param hadoopConfiguration A hadoop configuration
 */
class DataLineageBuilderFactory(hadoopConfiguration: Configuration) {

  private val writeCommandParserFactory = WriteCommandParserFactory.instance

  /** A main method of the object that performs transformation of Spark internal structures to Data Lineage representation.
   *
   * @return A lineage representation
   */
  def createBuilder(logicalPlan: LogicalPlan, executedPlan: Option[SparkPlan], sparkContext: SparkContext): DataLineageBuilder = {
    new DataLineageBuilder(logicalPlan, executedPlan, sparkContext)(hadoopConfiguration, writeCommandParserFactory)
  }
}
