/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.*;

import org.mmbase.applications.templateengine.util.NavigationLoader;

import org.mmbase.bridge.*;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.*;
/**
 * Programma site MMBase navigation
 * @author keesj
 * @version $Id: ProgrammaSiteMMBaseNavigation.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public class ProgrammaSiteMMBaseNavigation extends MMBaseNavigation {
    private static Logger log = Logging.getLoggerInstance(ProgrammaSiteMMBaseNavigation.class);
    private Cloud _cloud;

    public ProgrammaSiteMMBaseNavigation() {
        super();
        type = "tesites";
        field = "path";
        guifield="name";
    }

    public Navigation resolveNavigation(Path path) {
        log.debug("resolve " + path.current());
        String current = path.current();
		String sqlSafe = Encode.encode("ESCAPE_SINGLE_QUOTE",current);
        //get the list of sites managed for this entry (programmasites) and create a query 
        //for the maps object
        Hashtable siteMapsHash = new Hashtable();
        StringBuffer sb = new StringBuffer();
        Cloud templateCloud = getCloud();
        NodeList sites = templateCloud.getNodeManager("tesites").getList("state <> 0 and path='"+ sqlSafe +"'", null, null);
        if (sites.size() == 0){
        	return null;
        }
        if (sites.size() > 1){
        	log.error("found multiple tesites for path " + current);
        }
        Node theNode = sites.getNode(0);
        

        Navigation st = new NavigationParam("" + theNode.getNumber(), theNode.getStringValue("path"), field, guifield, type);
        
        if (guifield != null) {
            st.setGUIName(theNode.getStringValue(guifield));
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

        //find back the programmasite via the hash and select the right frontpage template
        //Node siteNode = (Node) siteMapsHash.get("" + node.getNumber());
        //Node templateNode = templateCloud.getNode(siteNode.getIntValue("frontpage"));
        st.setProperty("type", theNode.getStringValue("frontpage"));
        log.debug("creating a new Navigation for " + path.current() + " result style ={" + theNode.getStringValue("name") + "} \n" + st.toString());
        st.setProperty("maps", theNode.getStringValue("maps"));
        getParentNavigation().addChild(st);
        return st.resolveNavigation(path);
    }

    private static Cloud getCloud() {
        return ContextProvider.getDefaultCloudContext().getCloud("mmbase");
        
    }
}
