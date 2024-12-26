/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.rest.service

import com.ms.jedi.dl.model.dt.DataType
import com.ms.jedi.dl.model.op.Composite
import com.ms.jedi.dl.model.{Attribute, MetaDataset}

case class HigherLevelLineageOverview
(
  operations: Seq[Composite],
  datasets: Seq[MetaDataset],
  attributes: Seq[Attribute],
  dataTypes: Seq[DataType]
)
