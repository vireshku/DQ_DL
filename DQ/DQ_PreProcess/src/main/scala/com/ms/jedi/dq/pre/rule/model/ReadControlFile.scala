package com.ms.jedi.dq.pre.rule.model

/**
 * Reader class for reading control file
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 *
 */

import scala.io.Source
import net.liftweb.json.DefaultFormats
import com.ms.db.adapter.AdlsAdapter

object ReadControlFile {

  def read(cFile: String): RuleControlFile2 = {

    val sc = AdlsAdapter.sc("Pre-Process")
    val sqc = AdlsAdapter.sqc("Pre-Process")

    val controFileJson = sc.wholeTextFiles("adl://psinsightsadlsdev01.azuredatalakestore.net/"+ cFile)
    val jsonString = controFileJson.first()
    println(jsonString)
    implicit val formats = DefaultFormats
    val parsedJson = net.liftweb.json.parse(jsonString._2)
    val input = parsedJson.extract[RuleControlFile2]
    println(input.DeprecationdDate)
    input
  }

  def main(args: Array[String]): Unit = {

    read("PPE/Raw/CritsitArchive/dbo_ActivityPointerBase/Control File/C_dbo_ActivityPointerBase_v1.json")
  }
}