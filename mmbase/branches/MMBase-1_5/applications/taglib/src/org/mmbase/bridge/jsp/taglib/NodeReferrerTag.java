/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A tag which is a 'NodeReferrerTag's can be the child of a
 * NodeProvider tag, which supplies a 'Node' to its child tags. For
 * example the FieldTag, needs the use the Node of the parent
 * NodeProviderTag and therefore would be a NodeReferrerTag.
 *
 * @author Michiel Meeuwissen 
 *
 */

public abstract class NodeReferrerTag extends CloudReferrerTag {	

    private static Logger log = Logging.getLoggerInstance(NodeReferrerTag.class.getName()); 

    private String parentNodeId = null;

    /**
     * A NodeReferrer probably wants to supply the attribute 'node',
     * to make it possible to refer to another node than the direct
     * ancestor.
     **/

    public void setNode(String node){
        parentNodeId = node;
    }

    /**
    * This method tries to find an ancestor object of type NodeProvider
    * @return the NodeProvider if found else an exception.
    *
    */	
    public NodeProvider findNodeProvider() throws JspTagException {        
        return (NodeProvider) findParentTag("org.mmbase.bridge.jsp.taglib.NodeProvider", parentNodeId);
    }
    /**
    * This method tries to find an ancestor object of type NodeProvider
    * @return the NodeProvider or null.
    *
    */	
    public NodeProvider findNodeProvider(boolean throwexception) throws JspTagException {        
        return (NodeProvider) findParentTag("org.mmbase.bridge.jsp.taglib.NodeProvider", parentNodeId, throwexception);
    }

    /**
     * Gets the Node variable from the parent NodeProvider.
     * @return a org.mmbase.bridge.Node
     */

    protected Node getNode() throws JspTagException {
        return findNodeProvider().getNodeVar();
    }
}
