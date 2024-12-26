/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.web

import java.util.Arrays.asList
import java.{ util => ju }

import org.apache.commons.configuration.{ CompositeConfiguration, EnvironmentConfiguration, JNDIConfiguration, SystemConfiguration }
import org.springframework.context.annotation.{ Bean, Configuration }
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import com.ms.jedi.dl.persistence.api.DataLineageReader
import com.ms.jedi.dl.persistence.dao.{ LineageDAOv3, LineageDAOv4, MultiVersionLineageDAO }
import com.ms.jedi.dl.persistence.impl.{ MongoConnectionImpl, MongoDataLineageReader }
import com.ms.jedi.dl.web.handler.{ ScalaFutureMethodReturnValueHandler, UnitMethodReturnValueHandler }
import com.ms.jedi.dl.web.rest.service.LineageService

import scala.concurrent.duration._

@Configuration
class LineageWebAppConfig extends WebMvcConfigurer with ExecutionContextImplicit {

  import com.ms.jedi.dl.common.ConfigurationImplicits._

  private val confProps = new CompositeConfiguration(asList(
    new JNDIConfiguration("java:comp/env"),
    new SystemConfiguration,
    new EnvironmentConfiguration))

  override def addReturnValueHandlers(returnValueHandlers: ju.List[HandlerMethodReturnValueHandler]): Unit = {
    returnValueHandlers.add(new UnitMethodReturnValueHandler)
    returnValueHandlers.add(new ScalaFutureMethodReturnValueHandler(
      minEstimatedTimeout = confProps.getLong("spline.adaptive_timeout.min", 3.seconds.toMillis),
      durationToleranceFactor = confProps.getDouble("spline.adaptive_timeout.duration_factor", 1.5)))
  }

  @Bean def lineageReader: DataLineageReader = {
    val mongoConnection = new MongoConnectionImpl(
      dbUrl = confProps getRequiredString "vk.dl.mongodb.url",
      dbName = confProps getRequiredString "vk.dl.mongodb.name")
    val dao = new MultiVersionLineageDAO(
      new LineageDAOv3(mongoConnection),
      new LineageDAOv4(mongoConnection))
    new MongoDataLineageReader(dao)
  }

  @Bean def lineageService(reader: DataLineageReader): LineageService =
    new LineageService(reader)
}
