package com.ms.jedi.dq.post.rule.delta

object test {
  
  def main(args: Array[String]): Unit = {
    
    
    val ss = "/mnt/dq/Axis/dbo_Airports/Full/D_Airports_v1_2019-03-15-00-00.parquet"
    val sf = ss.replace("/mnt/dq/", "").split("/")
    println(sf.apply(0))
    println(sf.apply(1))
    
  }
  
}