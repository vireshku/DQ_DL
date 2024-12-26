package com.ms.jedi.dq.merge.watermark

/**
 * DQMergeWaterMark is the trait to wrap api(s) related to the watermark phenomenon
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import com.ms.db.adapter.AdlsAdapter
import com.ms.db.util.KeyValParser
import org.apache.spark.sql.functions._

trait DQMergeWaterMark {

  def waterMarkStart(source: String, entity: String, fullFlag: String): String = {
    val spark = AdlsAdapter.spark("Merge")
    var lstRunCompletedTime = "17530101"
    val lastRunData = spark.sql("select max(watermarkendvalue) from ComputeStore.DQMergeWaterMark where source='" + source + "' and entity='" + entity + "' and fulldelta='" + fullFlag + "'")
    if (lastRunData.first().getString(0) != null)
      lstRunCompletedTime = lastRunData.first().getString(0)
    println("lstRunCompletedTime is ------------------>" + lstRunCompletedTime)
    lstRunCompletedTime
  }

  def waterMarkEnd(source: String, entity: String, fullFlag: String): String = {
    val spark = AdlsAdapter.spark("Merge")
    var lstRunCompletedTime = "17530101"
    val env = KeyValParser.kv().getProperty("env")
    val lastRunData = spark.sql("select max(refreshedtime) from ComputeStore.LakeObserver" + env + " where source= '" + source + "' AND entity= '" + entity + "' AND  fulldelta='" + fullFlag + "' AND region='Raw'")
    if (!lastRunData.take(1).isEmpty)
      lstRunCompletedTime = lastRunData.first().getLong(0).toString()
    println("lstRunCompletedTime is ------------------>" + lstRunCompletedTime)
    lstRunCompletedTime
  }

  def updateWaterMark(source: String, entity: String, waterMarkEndVal: String, fullFlag: String) = {
    println("updating the water mark")
    val ss = AdlsAdapter.spark("Merge")
    val q = s"insert into ComputeStore.DQMergeWaterMark values('" + source + "', '" + entity + "' ,'17530101', " + waterMarkEndVal + " , " + current_timestamp() + " , " + current_timestamp() + " , '" + fullFlag + "')"
    ss.sql(q)
  }

}