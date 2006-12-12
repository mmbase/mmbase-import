package com.finalist.cmsc.struts;

import javax.servlet.ServletException;

import org.apache.commons.digester.Digester;
import org.apache.struts.Globals;
import org.apache.struts.config.*;

public class ActionServlet extends org.apache.struts.action.ActionServlet {


    /**
     * <p>Initialize the module configuration information for the
     * specified module.</p>
     *
     * @param prefix Module prefix for this module
     * @param paths Comma-separated list of context-relative resource path(s)
     *  for this modules's configuration resource(s)
     *
     * @exception ServletException if initialization cannot be performed
     * @since Struts 1.1
     */
    protected ModuleConfig initModuleConfig(String prefix, String paths)
        throws ServletException {

        // :FIXME: Document UnavailableException? (Doesn't actually throw anything)

        if (log.isDebugEnabled()) {
            log.debug(
                "Initializing module path '"
                    + prefix
                    + "' configuration from '"
                    + paths
                    + "'");
        }

        // Parse the configuration for this module
        ModuleConfigFactory factoryObject = ModuleConfigFactory.createFactory();
        ModuleConfig config = factoryObject.createModuleConfig(prefix);

        // Configure the Digester instance we will use
        Digester digester = initConfigDigester();

        // Process each specified resource path
        while (paths.length() > 0) {
            digester.push(config);
            String path = null;
            int comma = paths.indexOf(',');
            if (comma >= 0) {
                path = paths.substring(0, comma).trim();
                paths = paths.substring(comma + 1);
            } else {
                path = paths.trim();
                paths = "";
            }

            if (path.length() < 1) {
                break;
            }

            this.parseModuleConfigFile(digester, path);
        }

        getServletContext().setAttribute(
            Globals.MODULE_KEY + config.getPrefix(),
            config);

        // Force creation and registration of DynaActionFormClass instances
        // for all dynamic form beans we wil be using
        FormBeanConfig fbs[] = config.findFormBeanConfigs();
        for (int i = 0; i < fbs.length; i++) {
            if (fbs[i].getDynamic()) {
                fbs[i].getDynaActionFormClass();
            }
        }

        return config;
    }



}