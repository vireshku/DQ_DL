/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.persistence.impl

import java.util.UUID

import salat.annotations.Persist
import com.ms.jedi.dl.model.{ DataLineageId, MetaDataset }
import com.ms.jedi.dl.model.expr.Expression
import com.ms.jedi.dl.model.op.Operation

case class DataLineagePO(
  appId:         String,
  appName:       String,
  timestamp:     Long,
  sparkVer:      String,
  rootOperation: Operation,
  rootDataset:   MetaDataset) {
  
  @Persist
  lazy val id: String = DataLineageId.fromDatasetId(rootDataset.id)
}

case class TransformationPO(expr: Expression, opId: UUID)
