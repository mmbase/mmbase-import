/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.session;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.action.ActionUtils;
import org.mmbase.applications.editwizard.schema.WizardSchema;
import org.mmbase.applications.editwizard.schema.WizardSchemaLoader;
import org.mmbase.applications.editwizard.util.HttpUtil;
import org.mmbase.util.xml.URIResolver;

public abstract class AbstractConfig {
    
    private boolean debug = false;
    private String wizardName;
    private String page;
    private String popupId = ""; // indicate whether current window is popped up.

    private final Map popups = new HashMap(); // all popups now in use below this (key -> Config)

    private final Map attributes = new HashMap();
    
    private WizardSchema wizardSchema = null;
    
    /**
     * Basic configuration. The configuration object passed is updated with information retrieved
     * from the request object with which the configurator was created. The following parameters are accepted:
     *
     * <ul>
     *   <li>wizard</li>
     //*   <li>origin</li>
     //*   <li>context</li>
     *   <li>debug</li>
     * </ul>
     *
     * @since MMBase-1.6.4
     * @param configurator the configurator containing request information
     * @throws WizardException if expected parameters were not given or ad bad content
     */
    public void configure(HttpServletRequest request, URIResolver uriResolver) throws WizardException  {
        this.popupId = HttpUtil.getParam(request, "popupid",  popupId);
        
        this.wizardName = HttpUtil.getParam(request, "wizard", wizardName);
        if (wizardName != null && wizardName.startsWith("/")) {
            wizardName = ActionUtils.getResource(wizardName).toString();
        }
        
        if (wizardName != null) {
            attributes.put("wizard",wizardName);
        }
        attributes.put("popupid",popupId);

        // debug parameter
        debug = HttpUtil.getParam(request, "debug",  debug);
        attributes.put("debug", String.valueOf(debug));
        
        if (wizardName != null) {
            // load schema
            WizardSchemaLoader wizardSchemaLoader = WizardSchemaLoader.getInstance(uriResolver);
            this.wizardSchema = wizardSchemaLoader.getSchema(wizardName);
        } else {
            // if wizardName==null means it's a popup search list. 
            // no schema required for process.
        }
    }

    /**
     * @return Returns the wizardSchema.
     */
    public WizardSchema getWizardSchema() {
        return wizardSchema;
    }
    
    /**
     * Returns available attributes in a map, so they can be passed to the list stylesheet
     */
    public Map getAttributes() {
        return this.attributes;
    }
    
    /**
     * @return Returns the popups.
     */
    public Map getPopups() {
        return popups;
    }

    
    /**
     * @return Returns the page.
     */
    public String getPage() {
        return page;
    }

    
    /**
     * @param page The page to set.
     */
    public void setPage(String page) {
        this.page = page;
    }

    
    /**
     * @return Returns the wizardName.
     */
    public String getWizardName() {
        return wizardName;
    }

    
    /**
     * @param popupId The popupId to set.
     */
    public void setPopupId(String popupId) {
        this.popupId = popupId;
    }

    
    public String getPopupId() {
        return popupId;
    }
    
}