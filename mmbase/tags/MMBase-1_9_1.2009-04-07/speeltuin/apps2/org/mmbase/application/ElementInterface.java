/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

/**
 * in the element interface are all common methods specified that all elements should have such as install(), save()
 */
public interface ElementInterface {
    
    /**
     * install an element
     */
    public void install() throws Exception;
    
    /**
     * uninstall al element
     */
    public void uninstall() throws Exception;
    
    /**
     * check require entries of element
     */
    public void checkDependencies() throws Exception;
   
    public int getInstalledVersion();
    /**
     * auto deploy the element
     */
    void autoDeploy() throws Exception;
    
    /**
     * save the element
     */
    public void save() throws Exception;
    
    /**
     * give the name of the element
     */
    public String getName();
    public String getDescription();
    public void setDescription(String description);
    public void setAutoDeploy(String autodeploy);
    
    
    /**
     * give the MD5 code of the element
     */
    public String getMD5();
    
    /**
     * give the creation date of the element
     */
    public String getCreationDate();
    
    /**
     * tell if the element has to be auto deployed
     */
    
    public String getAutoDeploy();
    
    /**
     * delete the element
     */
    public void delete() throws Exception;
    
    
}
