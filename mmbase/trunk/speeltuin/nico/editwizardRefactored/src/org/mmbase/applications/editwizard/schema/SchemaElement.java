/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.schema;

import java.util.Map;
import java.util.TreeMap;

/**
 * this class is act as a POJO base object to present elements in <wizard-schema> xml files. 
 * @todo javadoc
 * 
 * @author caicai
 * @created Jul 18, 2005
 * @version $Id: SchemaElement.java,v 1.2 2005-12-11 11:51:04 nklasens Exp $
 */
public abstract class SchemaElement {
    
    private SchemaElement parent = null;
    
    private final Map attributes = new TreeMap();

    /**
     * @return Returns the parent.
     */
    public SchemaElement getParent() {
        return parent;
    }
    
    /**
     * @param parent The parent to set.
     */
    public void setParent(SchemaElement parent) {
        this.parent = parent;
    }
    
    /**
     * @param child the child elements should be added into parent
     */
    public void addChildElement(SchemaElement child) {
        child.setParent(this);
    }
    
    /**
     * get map of attributes.
     * @return Returns the attributes.
     */
    public Map getAttributes() {
        return attributes;
    }
    
    /**
     * set attribute of this element
     * @param attrName attribute's name
     * @param attrValue attribute's value
     */
    public void setAttribute(String attrName, Object attrValue) {
        if (attrName ==null || attrValue == null) {
            // if key or value is null, ignore this attribute
            return;
        }
        this.attributes.put(attrName,attrValue);
    }
    
    /**
     * get value of attribute
     * @param attrName attribute's name
     * @return
     */
    public String getAttribute(String attrName){
        return (String)this.attributes.get(attrName);
    }
    
    /**
     * get boolean value of attribute. 
     * @param attrName attribute's name
     * @return true, attribute's value eqauls(ignore case) "true"; false, otherwise.
     */
    public boolean getBoolean(String attrName) {
        return "true".equalsIgnoreCase((String)this.attributes.get(attrName));
    }
    
    /**
     * get int value of attribute.
     * @param attrName attribute's name
     * @return
     */
    public int getInt(String attrName) {
        return Integer.parseInt((String)this.attributes.get(attrName));
    }
    
    /**
     * get localizable value of attribute.
     * @param attrName attribute's name
     * @return
     */
    public Localizable getLocalizable(String attrName) {
        return (Localizable)this.attributes.get(attrName);
    }
    
}
