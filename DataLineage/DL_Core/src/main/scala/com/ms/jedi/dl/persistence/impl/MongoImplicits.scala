package com.ms.jedi.dl.persistence.impl

import com.mongodb.casbah.query.Imports.DBObject
import com.mongodb.casbah.query.dsl.{QueryExpressionObject, QueryOperator, ValueTestFluidQueryOperators}

import scala.language.implicitConversions

object MongoImplicits {

  implicit def mongoNestedDBObjectQueryStatementsExtended(nested: DBObject with QueryExpressionObject)
  : ValueTestFluidQueryOperators with MongoOpsMissingFromCasbahQueryDSL =
    new {
      val field = nested.field
    } with ValueTestFluidQueryOperators with MongoOpsMissingFromCasbahQueryDSL {
      dbObj = Some(nested.get(nested.field).asInstanceOf[DBObject])
    }


  trait MongoOpsMissingFromCasbahQueryDSL
    extends OptionsOp

  trait OptionsOp extends QueryOperator {
    private val oper = "$options"

    def $options(arg: String): DBObject with QueryExpressionObject = queryOp(oper, arg)
  }

}
