/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.*;

import org.mmbase.applications.templateengine.util.NavigationLoader;
import org.mmbase.util.logging.*;

import simplexml.XMLElement;
/**
 * @author keesj
 * @version $Id: EnumNavigation.java,v 1.1 2004-04-07 08:56:30 keesj Exp $
 * paramerted navigation is a navigation that consists of 2 path items
 * the first is the real name of the navigation. the second is a parameter
 * example /episodes/345632
 */
public class EnumNavigation extends AbstractNavigation implements Configurable {
	
    private static Logger log = Logging.getLoggerInstance(EnumNavigation.class);
    
    protected String config = null;
    protected String type;
    protected String field;
    protected String guifield;

    public EnumNavigation() {
        super();
        setVisible(false);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getID() {
        return "${" + type + ",number}";
    }

    public String getName() {
        return "${" + type + "," + field + "}";
    }

    public String getGUIName() {
        if (guifield == null) {
            return getName();
        }
        return "${" + type + "," + guifield + "}";
    }

    /* (non-Javadoc)
     * @see te.ParamNavigation#setConfig(java.lang.String)
     */
    public void setConfig(String config) {
        this.config = config;
        XMLElement xmle = new XMLElement();
        xmle.parseString(config);
        this.type = xmle.getProperty("type");
        this.field = xmle.getProperty("field");
        //this.guifield = "dummygui";
        log.info("initialised " + type + " " + field);
    }

    /* (non-Javadoc)
     * @see te.NavigationResolver#resolveNavigation(te.Path)
     */
    public Navigation resolveNavigation(Path path) {
        log.debug("resolve " + path.current());
        String current = path.current();
        log.debug(" input = " + current);
        log.debug("field" + field);
        StringTokenizer stringTokenizer = new StringTokenizer(field, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String token =stringTokenizer.nextToken();
            if (token.equals(current)) {
                Navigation st = new NavigationParam(current, current, "timefield", "dummy", "dummytype");
                log.debug(st);

                Navigation g = NavigationLoader.parseXML(config);
                Enumeration enum = g.getProperties().keys();
                while (enum.hasMoreElements()) {
                    String key = (String)enum.nextElement();
                    st.setProperty(key, g.getProperty(key));
                }

                Navigations n = g.getChildNavigations();
                for (int x = 0; x < n.size(); x++) {
                    st.addChild(n.getNavigation(x));
                }
                n = g.getParamChilds();
                for (int x = 0; x < n.size(); x++) {
                    st.addParamChild(n.getNavigation(x));
                }

                log.debug("creating a new Navigation for " + path.current() + " result \n" + st.toString());

                getParentNavigation().addChild(st);
                return st.resolveNavigation(path);

            }
        }
        return null;
    }
}
