package org.jahia.jahiawebapps.jforum;

import java.util.*;

import javax.servlet.*;
import javax.portlet.PortletConfig;

import portlet.wrappers.ServletContextWrapper;

public class ServletConfigWrapper implements ServletConfig {
   private PortletConfig config;

   public ServletConfigWrapper(PortletConfig config) {
      this.config = config;
   }

   /**
    * Returns a <code>String</code> containing the value of the named
    * initialization parameter, or <code>null</code> if the parameter does
    * not exist.
    *
    * @param name a <code>String</code> specifying the name of the
    *             initialization parameter
    * @return a <code>String</code> containing the value of the
    *         initialization parameter
    * @todo Implement this javax.servlet.ServletConfig method
    */
   public String getInitParameter(String name) {
      return config.getInitParameter(name);
   }

   /**
    * Returns the names of the servlet's initialization parameters as an
    * <code>Enumeration</code> of <code>String</code> objects, or an empty
    * <code>Enumeration</code> if the servlet has no initialization
    * parameters.
    *
    * @return an <code>Enumeration</code> of <code>String</code> objects
    *         containing the names of the servlet's initialization parameters
    * @todo Implement this javax.servlet.ServletConfig method
    */
   public Enumeration getInitParameterNames() {
      return config.getInitParameterNames();
   }

   /**
    * Returns a reference to the {@link ServletContext} in which the caller
    * is executing.
    *
    * @return a {@link ServletContext} object, used by the caller to
    *         interact with its servlet container
    * @todo Implement this javax.servlet.ServletConfig method
    */
   public ServletContext getServletContext() {
      ServletContextWrapper servletContext = new ServletContextWrapper(config.getPortletContext());
      return servletContext;
   }

   /**
    * Returns the name of this servlet instance.
    *
    * @return the name of the servlet instance
    * @todo Implement this javax.servlet.ServletConfig method
    */
   public String getServletName() {
      return config.getPortletName();
   }
}
