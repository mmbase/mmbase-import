/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.ContextProvider;

import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Tags which are meant to live as a child of the CloudTag, could extend this
 * class. 
 *
 * @author Michiel Meeuwissen 
 * @version $Id: CloudReferrerTag.java,v 1.21 2003-08-27 21:33:29 michiel Exp $ 
 */

public abstract class CloudReferrerTag extends ContextReferrerTag {
	
    private static final Logger log = Logging.getLoggerInstance(CloudReferrerTag.class.getName()); 

    private static CloudContext cloudContext;

    private Attribute cloudId = Attribute.NULL; 
    // the id of the cloud to which we refer
    // not yet supported by CloudTag


    /**
     * If there are more clouds to choose from, you can have a 'cloud'
     * attribute in your tag, in wich you can indicate the id of the
     * cloud you mean.    
     */
    public void setCloud(String c) throws JspTagException {
        cloudId = getAttribute(c);
    }


    /**
    * This method tries to find an ancestor object of type CloudProvider.
    *
    * @return the CloudTag if found, else an exception.
    */
	
    protected CloudProvider findCloudProvider() throws JspTagException {
        return (CloudProvider) findParentTag(CloudProvider.class, (String) cloudId.getValue(this));
    }

    /**
     * This method tries to find an ancestor object of type CloudProvider.
     *
     * @return the CloudProvider or null.
     *
    */	
    public CloudProvider findCloudProvider(boolean throwexception) throws JspTagException {        
        return (CloudProvider) findParentTag(CloudProvider.class, (String) cloudId.getValue(this), throwexception);
    }

    
    /**
     * Find the CloudProvider and return its cloud variable in one
     * step. And the result of findCloudProvider is stored, so
     * invoking this function more often is better then invoking
     * findCloudProvider every time.
     *
     * @return a Cloud
     */
    public Cloud getCloud() throws JspTagException {
        return findCloudProvider().getCloudVar();
    }



    /**
    * @return the cloud context 
    */
    protected CloudContext getCloudContext(){
        if (cloudContext == null){
            cloudContext = ContextProvider.getDefaultCloudContext();
        } 
        return cloudContext;
    }

    protected Node getNode(String key) throws JspTagException {
        Node n = getNodeOrNull(key);
        if (n == null) getCloud().getNode((String) getObject(key)); // cause exception
        return n;
    }
    /**
     * Gets a node from the context.
     */

    protected Node getNodeOrNull(String key) throws JspTagException {        
        Object n = getObject(key);
        if (n instanceof Node) {
            log.debug("found a Node in Context");
            return (Node) n;
        } else if ((n instanceof String) || (n instanceof Number)) {
            log.debug("found a Node Number in Context");
            if (! getCloud().hasNode(n.toString())) return null;
            return getCloud().getNode(n.toString());
        } else {
            throw new JspTagException("Element " + referid + " from context " + contextId + " cannot be converted to node (because it is a " + n.getClass().getName() + " now)");
        }
    }


}
