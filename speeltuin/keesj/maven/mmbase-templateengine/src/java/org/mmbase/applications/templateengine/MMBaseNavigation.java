/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

import org.mmbase.applications.templateengine.util.*;
import simplexml.*;
/**
 * @author keesj
 * @version $Id: MMBaseNavigation.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 * paramerted navigation is a navigation that consists of 2 path items
 * the first is the real name of the navigation. the second is a parameter
 * example /episodes/345632
 */
public class MMBaseNavigation extends AbstractNavigation implements Configurable {
    private static Logger log = Logging.getLoggerInstance(MMBaseNavigation.class);
    protected String config = null;
	protected String type;
	protected String field;
	protected String guifield;

    public MMBaseNavigation() {
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
        this.guifield = xmle.getProperty("guifield");
        log.info("initialised " + type + " " + field);
        try {
            NodeManager nm = getCloud().getNodeManager(type);
        } catch (BridgeException e) {
            log.error("did not find the node manager " + type + " " + Logging.stackTrace(e));
        }
    }
    /* (non-Javadoc)
     * @see te.NavigationResolver#resolveNavigation(te.Path)
     */
    public Navigation resolveNavigation(Path path) {
        log.debug("resolve " + path.current());
        String current = path.current();
        Cloud cloud = getCloud();
        NodeManager nm = getCloud().getNodeManager(type);
        Field nodeManagerField = nm.getField(field);
        Node node = null;
        if (nodeManagerField.getType() == Field.TYPE_INTEGER) {
            if (current.charAt(0) >= '0' && current.charAt(0) <= '9') {
                node = cloud.getNode(Integer.parseInt(current));
            } else {
                log.debug(current + " is not a number");
                return null;
            }
        } else {
            //need beter code for performance (for example a "best" reverse mapping for url)
            //maybe at least do a query where unserscores are replaced by question marks
            NodeList nl = nm.getList(null, null, null);
            for (int x = 0; x < nl.size(); x++) {
                Node nodeFromList = nl.getNode(x);
                String nodeField = nodeFromList.getStringValue(field);
                if (current.equals(URLConverter.toURL(nodeField))) {
                    log.debug("found node based on field" + field);
                    node = nodeFromList;
                }
            }
        }

        if (node == null) {
            return null;
        }

        Navigation st = new NavigationParam("" + node.getNumber(), node.getStringValue(field), field, guifield, type);
        if (guifield != null) {
            st.setGUIName(node.getStringValue(guifield));
        }
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
    }

    private static Cloud getCloud() {
        return ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        //return ContextProvider.getCloudContext("rmi://127.0.0.1:1111/remotecontext").getCloud("mmbase");
    }
}
