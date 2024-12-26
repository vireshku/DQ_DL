/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web

import javax.servlet.ServletContext

import com.ms.jedi.dl.web.html.HTMLDispatcherConfig
import com.ms.jedi.dl.web.rest.RESTDispatcherConfig
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet

class LineageWebAppInitializer extends WebApplicationInitializer {

  override def onStartup(container: ServletContext): Unit = {
    container addListener new ContextLoaderListener(new AnnotationConfigWebApplicationContext {
      register(classOf[LineageWebAppConfig])
    })

    registerDispatcher("rest_dispatcher", classOf[RESTDispatcherConfig], "/rest/*")(container)
    registerDispatcher("html_dispatcher", classOf[HTMLDispatcherConfig], "/*")(container)
  }

  private def registerDispatcher(servletName: String, clazz: Class[_], mappingPattern: String)(container: ServletContext): Unit = {
    val dispatcher = container.addServlet(servletName, new DispatcherServlet(new AnnotationConfigWebApplicationContext {
      register(clazz)
    }))

    dispatcher setLoadOnStartup 1
    dispatcher addMapping mappingPattern
    dispatcher setAsyncSupported true
  }
}
