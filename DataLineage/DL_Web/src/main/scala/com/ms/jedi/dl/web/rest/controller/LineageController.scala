/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.rest.controller

import java.util.UUID

import javax.servlet.http.HttpServletResponse
import org.apache.commons.lang.StringUtils.trimToNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestParam, ResponseBody}
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.persistence.api.DataLineageReader.PageRequest
import com.ms.jedi.dl.web.ExecutionContextImplicit
import com.ms.jedi.dl.web.handler.EstimableFuture
import com.ms.jedi.dl.web.json.StringJSONConverters
import com.ms.jedi.dl.web.rest.service.LineageService

import scala.language.postfixOps

@Controller
@RequestMapping(
  method = Array(GET),
  produces = Array(APPLICATION_JSON_VALUE))
class LineageController @Autowired()
(
  val reader: DataLineageReader,
  val service: LineageService
) extends ExecutionContextImplicit with EstimableFuture.Implicits {

  import StringJSONConverters._

  @RequestMapping(Array("/dataset/descriptors"))
  def datasetDescriptors
  (
    @RequestParam(name = "q", required = false) text: String,
    @RequestParam(name = "asAtTime", required = false, defaultValue = "9223372036854775807") timestamp: Long,
    @RequestParam(name = "offset", required = false, defaultValue = "0") offset: Int,
    @RequestParam(name = "size", required = false, defaultValue = "2147483647") size: Int,
    response: HttpServletResponse
  ): EstimableFuture[Unit] = {
    val futureResult =
      reader.findDatasets(
        Option(trimToNull(text)),
        PageRequest(timestamp, offset, size))

    futureResult.map(_ asJsonArrayInto response.getWriter).asEstimable(category = s"lineage/descriptors:$size")
  }

  @RequestMapping(Array("/dataset/{id}/descriptor"))
  @ResponseBody
  def datasetDescriptor(@PathVariable("id") id: UUID): EstimableFuture[String] =
    reader.getDatasetDescriptor(id).map(_.toJson).asEstimable(category = "lineage/descriptor")

  @RequestMapping(Array("/dataset/{id}/lineage/partial"))
  @ResponseBody
  def datasetLineage(@PathVariable("id") id: UUID): EstimableFuture[String] =
    reader.loadByDatasetId(id, overviewOnly = false).map(_.get.toJson).asEstimable(category = "lineage/partial")

  @RequestMapping(path = Array("/dataset/{id}/lineage/overview"), method = Array(GET))
  @ResponseBody
  def datasetLineageOverview(@PathVariable("id") id: UUID): EstimableFuture[String] = {
   println(" datasetLineageOverview ------------------------>")
    service.getDatasetLineageOverview(id).map(_.toJson).asEstimable(category = "lineage/overview")
  }

}
