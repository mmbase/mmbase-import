/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.data;


public class RelationData extends BaseData{

    /**
     * @return Returns the destination.
     */
    public String getDestination() {
        return this.getAttribute(ATTR_DESTINATION);
    }
    
    /**
     * @param destination The destination to set.
     */
    public void setDestination(String desc) {
        this.setAttribute(ATTR_DESTINATION, desc);
    }
    
    /**
     * @return Returns the role.
     */
    public String getRole() {
        return this.getAttribute(ATTR_ROLE);
    }
    
    /**
     * @param role The role to set.
     */
    public void setRole(String role) {
        this.setAttribute(ATTR_ROLE, role);
    }
    
    /**
     * @return Returns the source.
     */
    public String getSource() {
        return this.getAttribute(ATTR_SOURCE);
    }
    
    /**
     * @param source The source to set.
     */
    public void setSource(String src) {
        this.setAttribute(ATTR_SOURCE, src);
    }

    /**
     * @return Returns the relatedObject.
     */
    public ObjectData getRelatedObject() {
        if (children.size()>0) {
            return (ObjectData)children.get(0);
        }
        return null;
    }
    
    /**
     * @param relatedObject The relatedObject to set.
     */
    public void setRelatedObject(ObjectData relatedObject) {
        this.children.clear();
        this.addChild(relatedObject);
    }

   /**
     * @return Returns the mainObject.
     */
    public ObjectData getMainObject() {
        return (ObjectData)this.parent;
    }
    
}
