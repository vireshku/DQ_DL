package com.ms.jedi.observer.exec

import com.ms.jedi.observer.mount.LakeMount
import com.ms.jedi.observer.scan.LakeScan
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row
import com.ms.db.adapter.AdlsAdapter
import com.ms.jedi.observer.model.Audit

trait LakeObserverExecutorTrait {

  def doObserve(env: String): Dataset[Audit] = {
    val spark = AdlsAdapter.spark("Lake-Observer")
    import spark.implicits._
    var resultantData = AdlsAdapter.spark("Lake-Observer").createDataset(Seq[Audit]())

    List("Raw","RawMerge", "Gold", "Cooked", "NonPSOwnedRaw", "NonPSOwnedGold").map({ region =>
      LakeMount.doMount(region,env)
      resultantData = resultantData.union(LakeScan.getDeltaScan(region, "17530101".toLong,env))
    })
    resultantData
  }

  def doObserveAndWrite(env: String) = {
    doObserve(env).write.mode(SaveMode.Overwrite).insertInto("ComputeStore.LakeObserver" + env)
    println("FINISHED ::----------------------> Observation And Write.......")
  }
}