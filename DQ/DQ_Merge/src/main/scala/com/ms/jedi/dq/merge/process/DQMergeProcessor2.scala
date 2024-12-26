package com.ms.jedi.dq.merge.process

import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SaveMode
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

object DQMergeProcessor2 {
  
  def main(args: Array[String]): Unit = {
    
   val spark = AdlsAdapter.spark("Merge")
    
   val dataobserver =  spark.sql("select source,entity,FilePath,refreshedtime from (select row_number() over(partition by source,entity order by refreshedtime desc) " +
         " as RowNo,source,entity,FilePath,refreshedtime from ComputeStore.LakeObserverPPEmerge where region='Raw' and fulldelta='Delta' "  
        + " ) as a where a.RowNo = 1")

val waterData = spark.sql("select source,entity,WaterMarkEndValue from (select row_number() over(partition by source,entity order by WaterMarkEndValue desc) " +
         " as RowNo,source,entity,WaterMarkEndValue from ComputeStore.DQMergeWaterMark "  
        + " ) as a where a.RowNo = 1")
   
    // two joins
        
        val data = dataobserver.join(waterData,Seq("source","entity"),"left")
    
    // loop joined data
    
    
      import spark.implicits._
      data.collect.map({
        record =>
          try {
           if(null==record.getString(5) && record.getString(4)>record.getString(5))
            {
            val fullPath = record.getString(3)
            val name = fullPath.substring(fullPath.indexOf("Full") + 4)
            val rpath = fullPath.substring(0, fullPath.indexOf("Full") + 4)
            val tPath = rpath.replace("Raw", "RawMerge")
            val mergerdData = spark.read.option("mergeSchema", "true").parquet(rpath)
            val fData = mergerdData.withColumn("filename", input_file_name())
            fData.coalesce(1).write.mode(SaveMode.Append).parquet(tPath + "temp")
            val partitionPath = dbutils.fs.ls(tPath + "temp").filter(file => file.name.endsWith("." + "parquet"))(0).path
            //println( tPath+ "/" + name)
            dbutils.fs.cp(partitionPath, tPath + "/" + name)
            dbutils.fs.rm(tPath + "temp", recurse = true)
            
          }
          } catch {
            case e: Exception =>
              println("ERROR : WriteDataFrameToFile : Unknown Exception : " + e.getMessage)
          }

      })
    
    
  }
  
}