/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;


import java.text.SimpleDateFormat;
import java.util.*;

import org.mmbase.applications.templateengine.util.NavigationLoader;
import org.mmbase.util.logging.*;

import simplexml.XMLElement;
/**
 * @author keesj
 * @version $Id: TimeNavigation.java,v 1.1 2004-04-07 08:56:30 keesj Exp $
 * paramerted navigation is a navigation that consists of 2 path items
 * the first is the real name of the navigation. the second is a parameter
 * example /episodes/345632
 */
public class TimeNavigation extends AbstractNavigation implements Configurable {
    private static Logger log = Logging.getLoggerInstance(TimeNavigation.class);
    protected String config = null;
    protected String type;
    protected String field;
    protected String guifield;
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    public TimeNavigation() {
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
        this.type = "time";
        this.field = "dummyfield";
        this.guifield = "dummygui";
        log.info("initialised " + type + " " + field);
    }

    /* (non-Javadoc)
     * @see te.NavigationResolver#resolveNavigation(te.Path)
     */
    public Navigation resolveNavigation(Path path) {
        log.debug("resolve " + path.current());
        String current = path.current();
        log.debug("TimeNavigation input = " + current);
        try {
            Date  date = simpleDateFormat.parse(current);
            log.debug("TimeNavigation result = " + date);
    
            Navigation st = new NavigationParam( "" + (date.getTime() / 1000), current , "timefield", "dummy", "dummytype");
            log.debug(st);
    
            Navigation g = NavigationLoader.parseXML(config);
            Enumeration enum = g.getProperties().keys();
            while (enum.hasMoreElements()) {
                String key = (String) enum.nextElement();
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
    
        } catch (java.text.ParseException e){
            log.debug("TimeNavigation failed result = " + e.getMessage());
        }
        return null;
    }

}
