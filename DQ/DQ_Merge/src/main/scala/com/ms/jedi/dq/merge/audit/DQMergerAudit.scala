package com.ms.jedi.dq.merge.audit

import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SaveMode
import com.databricks.dbutils_v1.DBUtilsHolder.dbutils
import com.ms.db.util.KeyValParser

object DQMergerAudit {

  def getFull(env: String) =
    {
      val spark = AdlsAdapter.spark("Merge")
      val data = spark.sql("select * from (select row_number() over(partition by source,entity order by refreshedtime desc) " +
        " as RowNo,source,entity,FilePath,refreshedtime from ComputeStore.LakeObserver"+ env  +" where region='Raw' and fulldelta='Full' "
        + " ) as a where a.RowNo = 1")

      import spark.implicits._
      data.collect.map({
        record =>
          try {
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
          } catch {
            case e: Exception =>
              println("ERROR : WriteDataFrameToFile : Unknown Exception : " + e.getMessage)
          }

      })
    }

  def getDelta(env: String) = {
    val spark = AdlsAdapter.spark("Merge")
    val data = spark.sql("select * from (select row_number() over(partition by source,entity order by refreshedtime desc) " +
      " as RowNo,source,entity,FilePath,refreshedtime from ComputeStore.LakeObserver"+ env  +" where region='Raw' and fulldelta='Delta' "
      + " ) as a where a.RowNo = 1")

    import spark.implicits._
    data.collect.map({
      record =>
        try {
          val fullPath = record.getString(3)
          val name = fullPath.substring(fullPath.indexOf("Delta") + 5)
          val rpath = fullPath.substring(0, fullPath.indexOf("Delta") + 5)
          val tPath = rpath.replace("Raw", "RawMerge")
          val mergerdData = spark.read.option("mergeSchema", "true").parquet(rpath)
          val fData = mergerdData.withColumn("filename", input_file_name())
          fData.coalesce(1).write.mode(SaveMode.Append).parquet(tPath + "temp")
          val partitionPath = dbutils.fs.ls(tPath + "temp").filter(file => file.name.endsWith("." + "parquet"))(0).path
          //println( tPath+ "/" + name)
          dbutils.fs.cp(partitionPath, tPath + "/" + name)
          dbutils.fs.rm(tPath + "temp", recurse = true)
        } catch {
          case e: Exception =>
            println("ERROR : WriteDataFrameToFile : Unknown Exception : " + e.getMessage)
        }

    })
  }

}