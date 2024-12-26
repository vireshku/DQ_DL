package com.ms.jedi.dl.persistence.serialise

import com.mongodb.casbah.Imports._
import salat.{Context, grater}
import salat.transformers.CustomTransformer


import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import com.ms.jedi.dl.model.op.Aggregate

object AggregateOperationTransformer_SL126 {
  private val ESC = "\u001b"
  private val symbolsToEscape = Seq(".", "$")

  private val escapeRegex = symbolsToEscape.mkString("[", "", "]").r
  private val escapeTable = symbolsToEscape.zipWithIndex.toMap.mapValues(_.toString)

  private val unescapeRegex = s"$ESC(\\d)".r
  private val unescapeTable = escapeTable.map(_.swap)

  private implicit val ctx: Context = new BSONSalatContext
}

/**
  * A workaround for SL-126
  */
class AggregateOperationTransformer_SL126 extends CustomTransformer[Aggregate, DBObject]() {
  import AggregateOperationTransformer_SL126._

  override def serialize(a: Aggregate): DBObject = {
    val escapedAggregate = copyAggregationsWithTransform(escapeRegex, m => ESC + escapeTable(m.matched))(a)
    grater[Aggregate] asDBObject escapedAggregate
  }

  override def deserialize(b: DBObject): Aggregate = {
    val escapedAggregate = grater[Aggregate] asObject b
    copyAggregationsWithTransform(unescapeRegex, m => "\\" + unescapeTable(m.group(1)))(escapedAggregate)
  }

  private def copyAggregationsWithTransform(r:Regex, rep:Match => String)(agg: Aggregate): Aggregate =
    agg.copy(aggregations = agg.aggregations map { case (k, v) => (r.replaceAllIn(k, rep), v) })
}