package com.finalist.cmsc.services.community;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This class should be used by non-spring-wired classes if they need access to the application context
 */
public class ApplicationContextFactory implements ServletContextAware {
   private static Log log = LogFactory.getLog(ApplicationContextFactory.class);

   private static Object initObj = null;
   private static int count = 0;
   private static ApplicationContext context;

   public static void init(Object o) {
      if (count > 0) {
         log.error("Can't initialize the application context twice: THIS SHOULD ONLY HAPPEN DURING TESTING");
      }
      initObj = o;
      count++;
   }

   public static ApplicationContext getApplicationContext() {
      if (initObj == null) {
         throw new IllegalStateException("Application context not initialized");
      } else if (initObj instanceof ServletContext) {
         ServletContext servletContext = (ServletContext) initObj;
         return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

      } else if (initObj instanceof String) {
         if (context == null) {
            String contextResourceLocation = (String) initObj;
            context = new ClassPathXmlApplicationContext(contextResourceLocation);
         }
         return context;
      } else {
         throw new IllegalStateException("You must initialize the context with a String");
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.springframework.web.context.ServletContextAwar e#setServletContext(javax.servlet.ServletContext)
    */
   public void setServletContext(ServletContext context) {
      init(context);
   }

   public static Object getBean(String name) {
      return getApplicationContext().getBean(name);
   }

}