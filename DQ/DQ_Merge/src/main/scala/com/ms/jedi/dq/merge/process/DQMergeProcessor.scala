package com.ms.jedi.dq.merge.process

/**
 * DQMergeProcessor is the main entry class for the current functionality
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.db.adapter.AdlsAdapter
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.db.util.KeyValParser
import java.sql.Time
import java.util.Calendar
import java.text.SimpleDateFormat
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import org.apache.spark.sql.SaveMode
import com.ms.jedi.dq.merge.process.support.DQMergeProcSupport

object DQMergeProcessor extends DQMergeProcSupport {

  def main(args: Array[String]): Unit = {
    val source = args.apply(0)
    println("Started with the DQMergeProcessor for the source :::--------------------------> " + source)
    getDistinctSourceEntity(source).collect().map({ row =>
      try {
        val entity = row.getString(0)
        val fullDelta = row.getString(1)
        val BatchInfo = getBatchList(source, entity, fullDelta)
        println("Source:--> " + source + " Entity:--> " + entity + " <------> " + fullDelta + "BatchInfo size is --------------->  " + BatchInfo._1.size)
        if (null != BatchInfo._1 && !BatchInfo._1.isEmpty) {
          merge(source, entity, BatchInfo._1, fullDelta)
          updateWaterMark(source, entity, BatchInfo._2, fullDelta)
        }
      } catch {
        case e: Exception =>
          System.err.println("ERROR : Source===> " + source + " Entity=====> " + row.getString(0) + " WriteDataFrameToFile : Unknown Exception : " + e.getMessage)
      }
    })
    println("<-------------------- DQMerger process successfully completed-------------------------------->")
  }
}