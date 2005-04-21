/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine.util;

import simplexml.*;
import org.mmbase.applications.templateengine.*;

import org.mmbase.util.logging.*;

/**
 * Utility class to load a navigation based on an xml file
 * @author Kees Jongenburger
 */
public abstract class NavigationLoader {
    private static Logger log = Logging.getLoggerInstance(NavigationLoader.class);

    /**
    * load the navigation object from xml
    * the xml must follow the navigation.dtd
    * provided
    **/
    public static Navigation parseXML(String xml) {
        XMLElement xmle = new XMLElement();
        xmle.parseString(xml);
        Navigation nav = createNavigation(null, xmle);
        return nav;
    }

    private static Navigation createNavigation(Navigation parent, XMLElement xmle) {
        Navigation retval = null;
        if (xmle.getTagName().equals("navigationparam")) {
			String className = xmle.getProperty("class");
			Class paramClass;
			try {
				paramClass = Class.forName(className);
				retval = (Navigation) paramClass.newInstance();
				if (retval instanceof Configurable){
					((Configurable)retval).setConfig(xmle.toString());
				}
			} catch (ClassNotFoundException e) {
				log.warn("the xml entry " + xmle + " does not contain a valid class\n" + Logging.stackTrace(e));
				return null;
			} catch (InstantiationException e) {
				log.warn(Logging.stackTrace(e));
				return null;
			} catch (IllegalAccessException e) {
				log.warn(Logging.stackTrace(e));
				return null;
			} catch (RuntimeException e) {
				log.warn("error while loading entrypoint {" + className + "} the error was " + e.getMessage() + " : stacktrace " + Logging.stackTrace(e));
				return null;
			}
				 
        } else if (xmle.getTagName().equals("entrypoint")) {
            String className = xmle.getProperty("class");
            Class entryClass;
            try {
                entryClass = Class.forName(className);
                NavigationControl control = (NavigationControl) entryClass.newInstance();
				if (control instanceof Configurable){
					((Configurable)control).setConfig(xmle.toString());
				}

                retval = control.getNavigation();
                return retval; // we do not need to parse more content
            } catch (ClassNotFoundException e) {
                log.warn("the xml entry " + xmle + " does not contain a valid class\n" + Logging.stackTrace(e));
                return null;
            } catch (InstantiationException e) {
                log.warn(Logging.stackTrace(e));
                return null;
            } catch (IllegalAccessException e) {
                log.warn(Logging.stackTrace(e));
                return null;
            } catch (RuntimeException e) {
                log.warn("error while loading entrypoint {" + className + "} the error was " + e.getMessage() + " : stacktrace " + Logging.stackTrace(e));
                return null;
            }
        } else {
            retval = new StaticNavigation(xmle.getProperty("id"), xmle.getProperty("name"));
            if (xmle.getProperty("visible") != null && xmle.getProperty("visible").equals("false")) {
                retval.setVisible(false);
            }
        }

        for (int x = 0; x < xmle.countChildren(); x++) {
            XMLElement child = xmle.getChildAt(x);
            if (child.getTagName().equals("navigation")) {
                retval.addChild(createNavigation(retval, xmle.getChildAt(x)));
            } else if (child.getTagName().equals("entrypoint")) {
                Navigation newChild = createNavigation(retval, xmle.getChildAt(x));
                if (newChild != null) {
                    retval.addChild(newChild);
                }
            } else if (child.getTagName().equals("property")) {
                retval.setProperty(child.getProperty("name"), child.getContents());
            } else if (child.getTagName().equals("navigationparam")) {
                retval.addParamChild(createNavigation(retval, xmle.getChildAt(x)));
            } else {
                log.warn("unknown subtag" + child);
            }
        }
        return retval;
    }
}
