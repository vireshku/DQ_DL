package com.ms.jedi.dq.pre.rule.writer

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

object RuleResultWriter {

  def write(data: Dataset[Row]) = {
    println("starting to write to the lake....")
    data.coalesce(1).write.parquet("adl://psbidevuse2adls01.azuredatalakestore.net/PPE/Raw/Taxonomy/dbo_vwPPGTaxonomyPlan/full/D_vwPPGTaxonomyPlan_Qualified_1.parquet")
     println("write to the lake completed....")
  }

}