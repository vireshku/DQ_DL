package com.ms.jedi.dq.post.rule.delta

/**
 * Utility class for identifying the delta batches for an entity based on the audit framework
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

import com.ms.db.adapter.AdlsAdapter
import com.ms.db.util.KeyValParser
import com.ms.jedi.dq.post.rule.model.DQPostMetadata
import com.ms.jedi.dq.post.rule.model.DQRuleAudit

trait RuleDeltaIdentifier extends RuleDeltaTrait {

  def delta(source: String, start: String, end: String): Map[DQPostMetadata, Dataset[Row]] = {

    println("Starting with the delta identification process for Post Processing-------------")
    println(" ----------------------waterMarkStartVal --------------->  " + start)
    println(" ----------------------waterMarkEndVal --------------->  " + end)
    getDeltaFilePaths(source, start, end)
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

  def getDeltaFilePaths(source: String, waterMarkStartValue: String, waterMarkEndValue: String): Map[DQPostMetadata, Dataset[Row]] = {
    val deltaBatchMap = filterData(source, waterMarkStartValue, waterMarkEndValue)
    println("Delta batch prepared ---------------- Batch size ====> " + deltaBatchMap.size)
    deltaBatchMap.toMap
  }
}