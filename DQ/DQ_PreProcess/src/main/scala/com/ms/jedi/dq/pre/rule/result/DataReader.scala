package com.ms.jedi.dq.pre.rule.result

/**
 * Data Reader utility scala object 
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 *
 */

import net.liftweb.json.DefaultFormats
import org.apache.spark.sql.Dataset
import com.ms.db.adapter.AdlsAdapter
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

object DataReader {

  def rawData(): Dataset[Row] = {

    System.setProperty("hadoop.home.dir", "c:\\spark");
    val sc = AdlsAdapter.sc("Pre-Process")
    val sqc = AdlsAdapter.sqc("Pre-Process")
    val ss = AdlsAdapter.spark("Pre-Process")
    import sqc.implicits._
    implicit val formats = DefaultFormats

    val data = sqc.read.parquet("adl://psbidevuse2adls01.azuredatalakestore.net/PPE/Raw/Taxonomy/dbo_vwPPGTaxonomyPlan/full/D_vwPPGTaxonomyPlan_v1_2018-08-28-09-00.parquet")
    data
  }

  def testRead() = {
    System.setProperty("hadoop.home.dir", "c:\\spark");
    val sc = AdlsAdapter.sc("Pre-Process")
    val sqc = AdlsAdapter.sqc("Pre-Process")
    val ss = AdlsAdapter.spark("Pre-Process")
    import sqc.implicits._
    implicit val formats = DefaultFormats
    val data = sqc.read.parquet("adl://psbidevuse2adls01.azuredatalakestore.net/PPE/Raw/Taxonomy/dbo_vwPPGTaxonomyPlan/full/D_vwPPGTaxonomyPlan_Qualified.parquet")
    println(data.show())

  }

  def main(args: Array[String]): Unit = {
    testRead()
  }


}