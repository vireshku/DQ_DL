/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


package com.ms.jedi.dl.web.html

import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.{DefaultServletHandlerConfigurer, EnableWebMvc, WebMvcConfigurer}
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.ThymeleafViewResolver
import org.thymeleaf.spring5.{ISpringTemplateEngine, SpringTemplateEngine}
import org.thymeleaf.templatemode.TemplateMode.HTML
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
@EnableWebMvc
@ComponentScan(Array("com.ms.jedi.dl.web.html.controller"))
class HTMLDispatcherConfig extends WebMvcConfigurer {

  override def configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer): Unit = configurer.enable()

  @Bean
  def viewResolver: ViewResolver = new ThymeleafViewResolver() {
    setTemplateEngine(templateEngine)
    setCharacterEncoding("UTF-8")
  }

  @Bean
  def templateEngine: ISpringTemplateEngine = new SpringTemplateEngine() {
    setTemplateResolver(templateResolver)
  }

  @Bean
  def templateResolver: ITemplateResolver = new SpringResourceTemplateResolver() {
    setPrefix("/WEB-INF/html/")
    setSuffix(".html")
    setTemplateMode(HTML)
    setCacheable(false)
  }

}
