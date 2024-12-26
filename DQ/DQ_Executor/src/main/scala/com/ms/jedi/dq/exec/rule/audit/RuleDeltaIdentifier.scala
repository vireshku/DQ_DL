package com.ms.jedi.dq.exec.rule.audit

/**
 * Utility class for identifying the delta batches for an entity based on the audit framework
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Dataset
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import com.ms.db.util.KeyValParser
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.Dataset

trait RuleDeltaIdentifier extends RuleDeltaTrait {

  /**
   * Function to retrieve the delta batch data files for the Current Run
   * @Input								: Table name
   * entity     					: Value { OMIDimensions }
   * waterMarkStartValue 	: Value { End date for the delta file processed in last run }
   * @Output 							: Map with key as the delta file path and value as the corresponding DataSet for the same
   *
   */
  def delta(source: String, entity: String, waterMarkStartVal: String, waterMarkEndVal: String): Map[String, Dataset[Row]] = {
    println("Starting with the delta identification process-------------")
    println(" ----------------------waterMarkStartVal --------------->  " + waterMarkStartVal)
    println(" ----------------------waterMarkEndVal --------------->  " + waterMarkEndVal)
    getDeltaFilePaths(source, entity, waterMarkStartVal, waterMarkEndVal)
  }

  /**
   * Function to retrieve the list of FilePaths from FileAudit component based on SourceTable name and WaterMark Start and End Values for the current run
   * @Input
   * sourceTableName     : Value { SourceName_Tablename Ex. SMDP_DimRevSumHierarchy }
   * waterMarkStartValue : WaterMarkStartValue of the current run
   * waterMarkEndValue   : WaterMarkEndValue of the current run
   * adlsPath            : Adls path from the environment config, retrieve FilePaths get appended to the Adls path
   * @Output             : Map
   * Map              	 : key as the file data path and value as the corresponding Dataset
   */

  def getDeltaFilePaths(source: String, sourceTableName: String, waterMarkStartValue: String, waterMarkEndValue: String): Map[String, Dataset[Row]] = {
    val spark = AdlsAdapter.spark(sourceTableName)
    val deltaBatchMap = scala.collection.mutable.Map[String, Dataset[Row]]()
    val data = filterData(source, sourceTableName, waterMarkStartValue, waterMarkEndValue)
    import spark.implicits._
    data.foreach {
      d =>
        println("File Path :: -->" + d)
        val mData = readData(d,sourceTableName).withColumn("jId", monotonically_increasing_id())
        deltaBatchMap += (d -> mData)
    }
    println("Delta batch prepared ---------------- Batch size ====> " + deltaBatchMap.size)
    Map(deltaBatchMap.toSeq: _*)
  }

}