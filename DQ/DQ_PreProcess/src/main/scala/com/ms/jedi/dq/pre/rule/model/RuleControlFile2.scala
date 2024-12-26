package com.ms.jedi.dq.pre.rule.model


case class TableUserProperty(

)
case class FieldUserProperty(
  OriginalFieldName: String
)
case class FieldList(
  FieldName: String,
  FieldDataType: String,
  isPrimaryKey: String,
  isPII: String,
  FieldUserProperty: FieldUserProperty
)
case class Table(
  Name: String,
  Version: Double,
  Classification: String,
  isVisibleCatalog: String,
  HeaderPresent: String,
  Format: String,
  Compression: String,
  FieldCount: Double,
  TableUserProperty: TableUserProperty,
  FieldList: List[FieldList]
)
case class TableSecurity(
  SecurityName: String,
  SecurityDefinition: String
)
case class RuleControlFile2(
  DeprecationdDate: String,
  Table: Table,
  TableSecurity: TableSecurity
)
