/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.Cloud;

import javax.servlet.jsp.JspException;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
**
* @author Michiel Meeuwissen 
**/

public abstract class CloudReferrerTag extends BodyTagSupport {
	
    private static Logger log = Logging.getLoggerInstance(CloudReferrerTag.class.getName()); 

    /**
    * This method tries to find an ancestor object of type NodeProvider
    * @param id the id of the parent we are looking for , this id might be null or ""
    * in that case the first node provider found will be taken
    * @return the NodeProvider if found else an exception.
    *
    */
	
    public CloudTag findCloudTag(String id) throws JspException {

        Class cloudClass;
        try {
            cloudClass = Class.forName("org.mmbase.bridge.jsp.taglib.CloudTag");

        } catch (java.lang.ClassNotFoundException e) {
            throw new JspException ("Could not find CloudTag class");  
        }

        CloudTag cloud = (CloudTag) findAncestorWithClass((Tag)this, cloudClass); 
        if (cloud == null) {
            throw new JspException ("Could not find parent cloud");  
        }

        if ("".equals(id)) id = null;

        if (id != null) { // search further, if necessary
            while (cloud.getId() != id) {
                cloud = (CloudTag) findAncestorWithClass((Tag)cloud, cloudClass);            
                if (cloud == null) {
                    throw new JspException ("Could not find parent with id " + id);  
                }
            }
            
        }
        return cloud;
    }

    /**
     * Returns the closest ancestor which is a CloudTag.
     *
     */

    public CloudTag findCloudTag() throws JspException {
        return findCloudTag(null);
    }

    public Cloud getDefaultCloud() throws JspException {
        return findCloudTag().getCloud();
    }

}
