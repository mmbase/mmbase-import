/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.templateengine;

import java.util.*;
import org.mmbase.applications.templateengine.util.*;

import org.mmbase.util.logging.*;

/**
 * @author Kees Jongenburger
 */

public abstract class AbstractNavigation implements Navigation {

    private static Logger log = Logging.getLoggerInstance(AbstractNavigation.class);

    Navigation parent = null;
    Properties properties = new Properties();
    boolean visible = true;
    NavigationControl navigationControl;
    Navigations childNavigation = new Navigations();
    Navigations paramNavigations = new Navigations();
    String guiName = null;
    public AbstractNavigation() {
    }

    public boolean isRootNavigation() {
        return parent == null;
    }

    public void setParentNavigation(Navigation navigation) {
        this.parent = navigation;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /** 
     * @return unique id of this navigation typicaly a number
     */
    public abstract String getID();

    /** 
     * @return the name of the navigation nieuws
     */
    public abstract String getName();

    public Navigation getChildByName(String name) {
        Navigations navs = getChildNavigations();
        for (int x = 0; x < navs.size(); x++) {
            Navigation nav = navs.getNavigation(x);
            if (nav.getName().equals(name)) {
                return nav;
            }
        }
        return null;
    }
    /**
     * 
     * @return the escaped name of the navigation
     */
    public String getURLString() {
        return URLConverter.toURL(getName());
    }

    public String getFullURLString() {
        NavigationControl nc = getNavigationControl();
        return nc.getURLString(this);
    }

    /**
     * @return the child navigations of this component
     */
    public Navigations getChildNavigations() {
        return childNavigation;
    }

    /**
     * @return the parent navigation of the component
     */
    public Navigation getParentNavigation() {
        return parent;
    }

    /** 
     * @return the properties of the navigation
     */
    public Properties getProperties() {
        return properties;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setNavigationControl(NavigationControl navigationControl) {
        this.navigationControl = navigationControl;
    }

    public NavigationControl getNavigationControl() {
        if (navigationControl != null) {
            return navigationControl;
        } else if (!isRootNavigation()) {
            return parent.getNavigationControl();
        }
        return null;
    }

    public Template getTemplate() {
        return getNavigationControl().getTemplate(this);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Navigation resolveNavigation(Path path) {
        //if the current navigation is not visible call the child navigations and try to resolve
        if (!isVisible()) {
            //log.debug("nav " + getName() + " not visible trying child navigations");
            Navigations navs = getChildNavigations();
            for (int x = 0; x < navs.size(); x++) {
                Navigation resolved = navs.getNavigation(x).resolveNavigation(path);
                if (resolved != null) {
                    //log.debug("nav " + getName() + " child navigation returned true " + resolved.getName());
                    return resolved;
                }
            }
            //log.debug("nav " + getName() + " not visible trying param navigations");
            navs = paramNavigations;
            for (int x = 0; x < navs.size(); x++) {
                Navigation resolved = navs.getNavigation(x).resolveNavigation(path);
                if (resolved != null) {
                    //log.debug("nav " + getName() + " child navigation returned true " + resolved.getName());
                    return resolved;
                }
            }

            //the current navigaiton was not visible and the child navigations did not return a valid navigation
            //so te retrun null
            return null;
        }

        //the current navigation is visible
        //take the current element in the path
        if (path.hasCurrent()) {
            String currentPath = path.current(); // this is the current path
            if (currentPath.equals(getURLString())) {
                //if there are more elements in the path try to resolve them
                if (path.hasNext()) {
                    path.next();
                    Navigations navs = getChildNavigations();
                    for (int x = 0; x < navs.size(); x++) {
                        Navigation theChild = navs.getNavigation(x);
                        Navigation resolved = theChild.resolveNavigation(path);
                        if (resolved != null) {
                            return resolved;
                        }
                    }
                    //log.debug(getName() + " resolve via params " + path.current());
                    Navigations params = getParamChilds();
                    for (int x = 0; x < params.size(); x++) {
                        Navigation theChild = params.getNavigation(x);
                        Navigation resolved = theChild.resolveNavigation(path);
                        if (resolved != null) {
                            return resolved;
                        }
                    }
                    path.previous();
                }
                //the current path is the "best" navigation , if the navigation as a type or template it is a
                if (properties.containsKey("template") || properties.containsKey("type")) {
                    return this;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("path " + path + " has a navigation {"+ getURLString()+"} but no template of type");
                    }
                }
            }
        }
        return null;
    }

    public Navigation addChild(Navigation nav) {
        //log.debug(getName() + " adding child " + nav.getName());
        nav.setParentNavigation(this);
        childNavigation.add(nav);
        return nav;
    }

    public void addChilds(Navigations navs) {
        for (Iterator iter = navs.iterator(); iter.hasNext();) {
            Navigation nav = (Navigation) iter.next();
            addChild(nav);
        }
    }

    public String getGUIName() {
        if (guiName == null) {
            return getName();
        }
        return guiName;
    }

    public void setGUIName(String guiName) {
        this.guiName = guiName;
    }

    public Navigation addParamChild(Navigation navigation) {

        navigation.setParentNavigation(this);
        paramNavigations.add(navigation);
        return navigation;
    }
    public void toString(StringBuffer sb, int deep) {
        for (int x = 0; x < deep; x++) {
            sb.append(" ");
        }
        sb.append(getName() + " " + getClass().getName());
        for (int x = 0; x < paramNavigations.size(); x++) {
            sb.append(" " + paramNavigations.getNavigation(x).getName());
        }
        sb.append("\n");
        for (int x = 0; x < childNavigation.size(); x++) {
            childNavigation.getNavigation(x).toString(sb, deep + 1);
        }
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        toString(sb, 0);
        return sb.toString();
    }

    public Navigations getParamChilds() {
        return paramNavigations;
    }

    public Navigation removeChild(Navigation nav) {
        if (childNavigation.contains(nav)) {
            childNavigation.remove(nav);
        }
        return nav;
    }
}