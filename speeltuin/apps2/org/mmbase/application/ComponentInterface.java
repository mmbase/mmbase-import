/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

/**
 * Am application uses this interface to access all diferent components.
 * The standard set of components are: Display, DataSet, CloudLayout
 */
public interface ComponentInterface {
    
    /**
     * Install a component. This will install all parts of the component.
     */
    public void install() throws Exception;
    
    /**
     * Uninstall a component. This will uninstall all parts of the component.
     */
    public void uninstall() throws Exception;
    
    /**
     * save a component. Jar's will be made with the informations of the elements belonging to the component.
     */
    public void save() throws Exception;
    
    /**
     * checks if all conditions are satisfied
     * @return false if one dependency is missing, true otherwise
     */
    public void checkDependencies() throws Exception;
    
    void autoDeploy() throws Exception;
    
    /**
     * tell if a certain element exist
     */
    boolean exists(String elementName);
    
    /**
     * enables you to get information from a component.
     * This can be used to pass information to an interface.
     */
    String getStatus(String which);
    public void deleteElement(String elementname) throws Exception;
}
