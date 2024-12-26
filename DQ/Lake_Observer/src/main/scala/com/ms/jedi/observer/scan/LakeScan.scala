package com.ms.jedi.observer.scan

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.Dataset
import com.ms.jedi.observer.model.Audit
import com.ms.db.adapter.AdlsAdapter
import com.ms.db.util.KeyValParser

object LakeScan {

  def getDeltaScan(region: String, timestamp: Long, env: String): Dataset[Audit] = {
    val spark = AdlsAdapter.spark("Lake-Observer")
    println("Started with getDeltaScan-------------------------->" + region)
    var auditcollection = Array[Audit]()
    val basePath = "/mnt/observer" + env + "/" + region + "/"
    val fileType = ".parquet"
    var files = Array[String]()
    var maxTS: Long = 0
    val conf = new Configuration()
    val hdfs = FileSystem.get(conf)
    val f = new Path(basePath)
    val messageFile = hdfs.listFiles(f, true)
    while (messageFile.hasNext()) {
      val message = messageFile.next()
      if ((message.getPath.toString().endsWith(".parquet") || message.getPath.toString().endsWith(".tsv")) && !(message.getPath.toString().startsWith("part"))) {
        if (message.getModificationTime > timestamp) {
          val file = message.getPath.toString()
          files = files :+ file
          val sf = file.replace("dbfs:/mnt/observer" + env + "/" + region + "/", "").split("/")
          val base = KeyValParser.kv().getProperty("aadls")
          val pp = base + "/" + env + "/" + region + "/" + file.replace("dbfs:/mnt/observer" + env + "/" + region + "/", "")
          if (message.getModificationTime > maxTS) {
            maxTS = message.getModificationTime
          }
          if (file.contains("/Full/"))
            auditcollection = auditcollection :+ Audit.apply(sf.apply(0), sf.apply(1), pp, region, maxTS, "Full")
          else
            auditcollection = auditcollection :+ Audit.apply(sf.apply(0), sf.apply(1), pp, region, maxTS, "Delta")
        }
      }
    }
    import spark.sqlContext.implicits._
    println("getDeltaScan scan auditcollection list size -----> " + auditcollection.size)
    println("getDeltaScan scan file list size -----> " + files.size)
    println("getDeltaScan scan file nax TS -----> " + maxTS)
    spark.createDataset(auditcollection)
  }

}