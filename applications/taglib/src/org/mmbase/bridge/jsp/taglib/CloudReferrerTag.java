/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.LocalContext;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
* Tags which are meant to live as a child of the CloudTag, could extend this
* class. 
*
* @author Michiel Meeuwissen 
**/

public abstract class CloudReferrerTag extends BodyTagSupport {
	
    private static Logger log = Logging.getLoggerInstance(CloudReferrerTag.class.getName()); 
    private static CloudContext cloudContext;

    private CloudProvider cloudTag = null;

    private String cloudId = null; 
    // the id of the cloud to which we refer
    // not yet supported by CloudTag


    /**
     * If there are more clouds to choose from, you can have a 'cloud'
     * attribute in your tag, in wich you can indicate the id of the
     * cloud you mean.
     *
     **/
    public void setCloud(String c) {
        cloudId = c;
    }
    
    /**
    * This method tries to find an ancestor object of type CloudTag
    * in that case the first node provider found will be taken.
    * REMARK: the CloudTag does not yet have 'id', i think. We dont'
    * have multiple cloud support yet.
    * @return the CloudTag if found, else an exception.
    *
    */
	
    protected CloudProvider findCloudProvider() throws JspTagException {

        Class cloudClass;
        try {
            cloudClass = Class.forName("org.mmbase.bridge.jsp.taglib.CloudProvider");
        } catch (java.lang.ClassNotFoundException e) {
            throw new JspTagException ("Could not find CloudProvider class");  
        }

        CloudProvider cTag = (CloudProvider) findAncestorWithClass((Tag)this, cloudClass); 
        if (cTag == null) {
            throw new JspTagException ("Could not find parent cloud");  
        }

        if ("".equals(cloudId)) cloudId = null;

        if (id != null) { // search further, if necessary
            while (cTag.getId() != cloudId) {
                cTag = (CloudProvider) findAncestorWithClass((Tag)cTag, cloudClass);
                if (cTag == null) {
                    throw new JspTagException ("Could not find parent with id " + cloudId);  
                }
            }
            
        }
        return cTag;
    }
    
    /**
     * Find the CloudProvider and return its cloud variable in one
     * step. And the result of findCloudProvider is stored, so
     * invoking this function more often is better then invoking
     * findCloudProvider every time.
     *
     * @return a Cloud
     **/

    protected Cloud getCloudProviderVar() throws JspTagException {
        if (cloudTag == null) {
            cloudTag = findCloudProvider();
        }
        return cloudTag.getCloudVar();
    }


    /**
    * @return the cloud context 
    **/
    protected CloudContext getCloudContext(){
        if (cloudContext == null){
            cloudContext = LocalContext.getCloudContext();
        } 
        return cloudContext;
    }

}
