package com.finalist.cmsc.struts;

import java.util.Set;

import javax.servlet.ServletException;

import org.apache.struts.config.*;

@SuppressWarnings("serial")
public class ActionServlet extends org.apache.struts.action.ActionServlet {

   /**
    * <p>
    * Initialize the module configuration information for the specified module.
    * </p>
    * 
    * @param prefix
    *           Module prefix for this module
    * @param paths
    *           Comma-separated list of context-relative resource path(s) for
    *           this modules's configuration resource(s)
    * @exception ServletException
    *               if initialization cannot be performed
    * @since Struts 1.1
    */
   @Override
   protected ModuleConfig initModuleConfig(String prefix, String paths) throws ServletException {

      StringBuilder configpaths = new StringBuilder();

      Set<String> webInfResources = getServletContext().getResourcePaths("/WEB-INF/");
      for (String resource : webInfResources) {
         if (resource.startsWith("/WEB-INF/struts-")) {
            if (configpaths.length() > 0) {
               configpaths.append(",");
            }
            configpaths.append(resource);
         }
      }

      if (configpaths.length() <= 0) {
         configpaths.append(paths);
      }

      return super.initModuleConfig(prefix, configpaths.toString());
   }

}