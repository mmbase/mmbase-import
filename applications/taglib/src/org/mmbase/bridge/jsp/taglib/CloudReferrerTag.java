/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.Cloud;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
* Tag which are meant to live as a child of the CloudTag, could extend this
* class. 
*
* @author Michiel Meeuwissen 
**/

public abstract class CloudReferrerTag extends BodyTagSupport {
	
    private static Logger log = Logging.getLoggerInstance(CloudReferrerTag.class.getName()); 

    /**
    * This method tries to find an ancestor object of type CloudTag
    * @param id the id of the parent we are looking for , this id might be null or ""
    * in that case the first node provider found will be taken.
    * REMARK: the CloudTag does not yet have 'id', i think. We dont'
    * have multiple cloud support yet.
    * @return the CloudTag if found, else an exception.
    *
    */
	
    public CloudTag findCloudTag(String id) throws JspTagException {

        Class cloudClass;
        try {
            cloudClass = Class.forName("org.mmbase.bridge.jsp.taglib.CloudTag");

        } catch (java.lang.ClassNotFoundException e) {
            throw new JspTagException ("Could not find CloudTag class");  
        }

        CloudTag cloud = (CloudTag) findAncestorWithClass((Tag)this, cloudClass); 
        if (cloud == null) {
            throw new JspTagException ("Could not find parent cloud");  
        }

        if ("".equals(id)) id = null;

        if (id != null) { // search further, if necessary
            while (cloud.getId() != id) {
                cloud = (CloudTag) findAncestorWithClass((Tag)cloud, cloudClass);            
                if (cloud == null) {
                    throw new JspTagException ("Could not find parent with id " + id);  
                }
            }
            
        }
        return cloud;
    }

    /**
     * Returns the closest ancestor which is a CloudTag.
     *
     */

    public CloudTag findCloudTag() throws JspTagException {
        return findCloudTag(null);
    }
    
    /**
    * @return the default cloud being the cloud with name equals to the DEFAULT_CLOUD_NAME
    * defined in this class. 
    **/

    public Cloud getDefaultCloud() throws JspTagException {
        return findCloudTag().getCloud();
    }

    /**
    * @return the page cloud being the cloud set with the <mm:cloud>
    * tag
    *
    * REMARK: now exist 'getDefaultCloud' and 'getPageCloud'. I think
    * one such a function would be sufficient.
    **/

    public Cloud getPageCloud() throws JspTagException {
        return findCloudTag().getCloud();
    }

}
