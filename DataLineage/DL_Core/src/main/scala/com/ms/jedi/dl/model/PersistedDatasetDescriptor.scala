/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.model


import java.net.URI
import java.util.UUID

/**
  * The case class represents a basic descriptor containing all necessary information to identify a persisted dataset.
  *
  * @param datasetId An unique identifier of the dataset
  * @param appId     An ID of the Spark application that produced this dataset
  * @param appName   A name of the Spark application that produced this dataset
  * @param path      Persisted dataset (file) URL
  * @param timestamp UNIX timestamp (in millis) when the dataset has been produced
  */
case class PersistedDatasetDescriptor
(
  datasetId: UUID,
  appId: String,
  appName: String,
  path: URI,
  timestamp: Long
)
