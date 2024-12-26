/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.model.expr

import java.util.UUID

import salat.annotations.Salat

@Salat
sealed trait Expression {
  def children: Seq[Expression]

  def allRefLikeChildrenFlattened: Seq[Expression] = children.flatMap(_.allRefLikeChildrenFlattened)
}

@Salat
sealed trait LeafExpression extends Expression {
  override final def children: Seq[Expression] = Nil
}

@Salat
sealed trait RefLikeExpression extends Expression {
  override final def allRefLikeChildrenFlattened: Seq[Expression] = this +: super.allRefLikeChildrenFlattened
}

trait TypedExpression {
  def dataTypeId: UUID
}

trait GenericExpressionLike {
  def name: String

  def exprType: String

  def params: Option[Map[String, Any]]
}

case class Generic
(
  override val name: String,
  override val dataTypeId: UUID,
  override val children: Seq[Expression],
  override val exprType: String,
  override val params: Option[Map[String, Any]]
) extends Expression
  with TypedExpression
  with GenericExpressionLike

case class GenericLeaf
(
  override val name: String,
  override val dataTypeId: UUID,
  override val exprType: String,
  override val params: Option[Map[String, Any]]
) extends Expression
  with LeafExpression
  with TypedExpression
  with GenericExpressionLike

case class Alias
(
  alias: String,
  child: Expression
) extends Expression
  with RefLikeExpression {
  override def children: Seq[Expression] = Seq(child)
}

case class Binary
(
  symbol: String,
  override val dataTypeId: UUID,
  override val children: Seq[Expression]
) extends Expression
  with TypedExpression

case class AttrRef(refId: UUID) extends Expression with LeafExpression with RefLikeExpression

case class Literal
(
  value: Any = null, //spline-53: Salat requires nullable non-Option properties to have default values.
  override val dataTypeId: UUID
) extends Expression
  with LeafExpression
  with TypedExpression

case class UDF
(
  name: String,
  override val dataTypeId: UUID,
  override val children: Seq[Expression]
) extends Expression
  with TypedExpression
