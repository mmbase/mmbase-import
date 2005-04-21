/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine.util;

import java.util.*;

import simplexml.*;

import org.mmbase.util.logging.*;

import org.mmbase.applications.templateengine.*;
/**
 * @author keesj
 * @version $Id: XMLStorage.java,v 1.1.1.1 2004-04-02 14:58:48 keesj Exp $
 */
public class XMLStorage {
    private static Logger log = Logging.getLoggerInstance(XMLStorage.class);

    public String componentToString(Component c) {
        return componentToXML(c).toString();
    }

    public XMLElement componentToXML(Component c) {
        XMLElement xmle = new XMLElement();
        if (c instanceof Template) {
            xmle.setTagName("template");
        } else if (c instanceof Container) {
            xmle.setTagName("container");
        } else {
            xmle.setTagName("component");
        }
        xmle.addProperty("name", "" + c.getName());
        Properties props = c.getProperties();
        Enumeration enum = props.keys();
        while (enum.hasMoreElements()) {
            String key = (String) enum.nextElement();
            String value = props.getProperty(key);
            XMLElement prop = new XMLElement();
            prop.setTagName("property");
            prop.addProperty("name", "" + key);
            prop.setContent(value);
            xmle.addChild(prop);
        }
        if (c instanceof Container) {
            Container cont = (Container) c;
            if (cont.getLayoutManager() != null) {
                xmle.addProperty("layout", "" + cont.getLayoutManager().getName());
            }
            Components childs = cont.getComponents();
            for (int x = 0; x < childs.size(); x++) {
                Component child = childs.getComponent(x);
                xmle.addChild(componentToXML(child));
            }
        }
        return xmle;
    }

    public Component stringToComponent(String data) {
        XMLElement xmle = new XMLElement();
        xmle.parseString(data);
        return XMLToComponent(xmle);
    }

    public Component XMLToComponent(XMLElement xmle) {
        ComponentRegistry reg = Engine.getFacade().getComponentRegistry();
        Component retval = null;
        if (xmle.getTagName().equals("template")) {
            Template t = reg.getTemplate(xmle.getProperty("name"));
            if (t == null) {
                t = reg.getTemplate("default");
                t.setName(xmle.getProperty("name"));
            }
            if (xmle.getProperty("layout") != null) {
                String layout = xmle.getProperty("layout");
                t.setLayoutManager(reg.getLayoutManager(layout));
            }
            retval = t;
        } else if (xmle.getTagName().equals("container")) {
            Container c = reg.getContainer(xmle.getProperty("name"));
            if (xmle.getProperty("layout") != null) {
                String layout = xmle.getProperty("layout");
                c.setLayoutManager(reg.getLayoutManager(layout));
            }
            retval = c;
        } else if (xmle.getTagName().equals("component")) {
            retval = reg.getComponent(xmle.getProperty("name"));
        } else {
            log.error("unknown subtab " + xmle.getTagName());
            return null;
        }
        for (int x = 0; x < xmle.countChildren(); x++) {
            XMLElement child = xmle.getChildAt(x);
            if (child.getTagName().equals("property")) {
                retval.setProperty(child.getProperty("name"), child.getContents());
            } else if (retval instanceof Container) {
                Container cont = (Container) retval;
                Component comp = XMLToComponent(child);
                if (comp != null) {
                    cont.addComponent(comp);
                }
            }
        }
        return retval;
    }
}
