package com.ms.jedi.dq.exec.rule.monitor

import com.microsoft.applicationinsights.TelemetryClient
import java.util.Formatter.DateTime
import java.util.Calendar
import java.text.SimpleDateFormat

object RuleMonitor {

  def NewTelemetryClient(): TelemetryClient =
    {

      val telemetryClient = new TelemetryClient
      val id = telemetryClient.getContext.getDevice.getId

      /*tc.context.application.id = applicationId
      tc.context.application.ver = "0.0.1"
      tc.context.device.id = "Databricks notebook"
      tc.context.operation.id = operationId
      tc.context.operation.parentId = parentOperationId*/

      telemetryClient.trackEvent("name")

      val jobStartTimeStr = time

      /* telemetryClient.trackEvent("Start Job, { Start Time: jobStartTimeStr,
  'perfDataFilePath':perfDataFilePath, 'perfDataFileNamePrefix' :
  perfDataFileNamePrefix, 'consolidatedDataPath':consolidatedDataPath,
  'transformedFilePath' : transformedFilePath, 'perfDBTableName': perfDBTableName})*/

      telemetryClient.flush()
      return telemetryClient

    }

  def time(): String = {

    val now = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("yyyyMMdd_mmss")
    minuteFormat.format(now)

  }

}