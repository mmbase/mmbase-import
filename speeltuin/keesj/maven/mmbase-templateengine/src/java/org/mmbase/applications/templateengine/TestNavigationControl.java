/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.*;

import simplexml.*;

import org.mmbase.util.logging.*;

import org.mmbase.applications.templateengine.jsp.JSPTemplate;
import org.mmbase.applications.templateengine.util.NavigationLoader;
/**
 * @author Kees Jongenburger
 */
public class TestNavigationControl extends NavigationControl implements Configurable {
    private static Logger log = Logging.getLoggerInstance(TestNavigationControl.class);

    Navigation navigation;
    String config = null;
    
    public TestNavigationControl() {

    }

    public Navigation getNavigation() {
        return navigation;
    }

    public Template getTemplate(Navigation navigation) {
        JSPTemplate t = new JSPTemplate("/test.jsp", null);
        t.setMapRenderRelativeToRender(true);
        return t;
    }

    /* (non-Javadoc)
     * @see te.Configurable#setConfig(java.lang.String)
     */
    public void setConfig(String config) {
        this.config = config;
        XMLElement e = new XMLElement();
        e.parseString(config);
        e.setTagName("navigation");
        e.addProperty("name","test controler");
		e.addProperty("id","testcontroler");
        navigation = NavigationLoader.parseXML(e.toString());
		navigation.setNavigationControl(this);
		navigation.setVisible(false);
		log.debug("Test navigation = " + navigation);
    }

    /* (non-Javadoc)
     * @see te.NavigationControl#resoveURL(te.Navigation, java.util.List)
     */
    public String resoveURL(Navigation currentNavigation, List params) {
        
        return null;
    }

}
