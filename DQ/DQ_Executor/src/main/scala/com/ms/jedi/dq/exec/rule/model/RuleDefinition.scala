package com.ms.jedi.dq.exec.rule.model

/**
 * Schema model class for rules definition,this is the blueprint feed for executor
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

case class Columns(
  Column:   String,
  DataType: String)
case class MetaData(
  Source:  String,
  Entity:  Entity,
  ControlFile: String,
  Columns: List[Columns])

case class RuleDefinition(
  RuleDefId: String,
  MetaData:  List[MetaData],
  Rules:     List[Rules])
case class Entity(
  eName:    String,
  isMaster: Boolean)