/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.util.Properties;

/**
 * @author keesj
 * @version $Id: Navigation.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public interface Navigation extends NavigationResolver {
    public boolean isRootNavigation();
    public void setParentNavigation(Navigation navigation);
    public void setProperty(String key, String value);
    /** 
     * @return unique id of this navigation typicaly a number
     */
    public String getID();
    /** 
     * @return the name of the navigation (as used in url)
     */
    public String getName();
    
    /**
     * 
     * @return the name of the navigation (as used in a breadcrum)
     */
    public String getGUIName();
    
	public void setGUIName(String name);
    public Navigation getChildByName(String name);
    /**
     * 
     * @return the escaped name of the navigation
     */
    public String getURLString();
        
    public String getFullURLString();
    
    /**
     * @return the child navigations of this component
     */
    public Navigations getChildNavigations();
	public Navigations getParamChilds();
    public Navigation addChild(Navigation navigation);
    public Navigation addParamChild(Navigation navigation);
    public Navigation removeChild(Navigation navigation);
    /**
     * @return the parent navigation of the component
     */
    public Navigation getParentNavigation();
    /** 
     * @return the properties of the navigation
     */
    public Properties getProperties();
    public String getProperty(String key);
    public void setNavigationControl(NavigationControl navigationControl);
    public NavigationControl getNavigationControl();
    public Template getTemplate();
    public boolean isVisible();
    public void setVisible(boolean visible);
}