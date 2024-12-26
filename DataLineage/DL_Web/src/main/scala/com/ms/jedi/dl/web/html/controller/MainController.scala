/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.html.controller

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod.{GET, HEAD}
import org.springframework.web.bind.annotation.{RequestMapping, RequestParam, ResponseBody}
import com.ms.jedi.dl.common.ARM._
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.web.ExecutionContextImplicit
import com.ms.jedi.dl.web.exception.LineageNotFoundException

import scala.concurrent.Future
import scala.io.Source.fromInputStream
import scala.language.postfixOps

@Controller
class MainController @Autowired()
(
  val reader: DataLineageReader
) extends ExecutionContextImplicit {

  @RequestMapping(path = Array("/", "/dataset/**", "/dashboard/**"), method = Array(GET, HEAD))
  def index = "index"

  @RequestMapping(path = Array("/dataset/lineage/_search"), method = Array(GET))
  def lineage(
               @RequestParam("path") path: String,
               @RequestParam("application_id") applicationId: String,
               httpReq: HttpServletRequest,
               httpRes: HttpServletResponse): Future[String] =
    reader.searchDataset(path, applicationId) map {
      case Some(dsId) => s"redirect:/dataset/$dsId/lineage/overview#datasource"
      case None => throw new LineageNotFoundException(s"dataset_path=$path AND app_id=$applicationId")
    }

  @RequestMapping(path = Array("/build-info"), method = Array(GET), produces = Array("text/x-java-properties"))
  @ResponseBody
  def buildInfo: String = {
    val lines = for {
      stream <- managed(this.getClass getResourceAsStream "/build.properties")
      line <- fromInputStream(stream).getLines if line.nonEmpty && !line.startsWith("#")
    } yield line
    lines.mkString("\n")
  }
}
