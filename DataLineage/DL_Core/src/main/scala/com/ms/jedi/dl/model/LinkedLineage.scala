/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.model


class LinkedLineage(linked: DataLineage, val original: DataLineage)
  extends DataLineage(
    linked.appId,
    linked.appName,
    linked.timestamp,
    linked.sparkVer,
    linked.operations,
    linked.datasets,
    linked.attributes,
    linked.dataTypes)
