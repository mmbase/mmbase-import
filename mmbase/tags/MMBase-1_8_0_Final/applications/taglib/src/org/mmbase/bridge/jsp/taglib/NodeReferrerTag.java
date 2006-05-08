/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.functions.Parameters;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
/**
 * A tag which is a 'NodeReferrerTag's can be the child of a
 * NodeProvider tag, which supplies a 'Node' to its child tags. For
 * example the FieldTag, needs the use the Node of the parent
 * NodeProviderTag and therefore would be a NodeReferrerTag.
 *
 * @author Michiel Meeuwissen 
 * @version $Id: NodeReferrerTag.java,v 1.23 2006-02-10 18:04:26 michiel Exp $ 
 */

public abstract class NodeReferrerTag extends CloudReferrerTag {	

    private static final Logger log = Logging.getLoggerInstance(NodeReferrerTag.class); 
    protected Attribute parentNodeId = Attribute.NULL;
    private Attribute element  = Attribute.NULL;
    /**
     * The element attribute is used to access elements of
     * clusternodes.
     * @since MMBase-1.7.4
     */
    public void setElement(String e) throws JspTagException {
        element = getAttribute(e);
    }


    /**
     * A NodeReferrer probably wants to supply the attribute 'node',
     * to make it possible to refer to another node than the direct
     * ancestor.
     **/

    public void setNode(String node) throws JspTagException {
        parentNodeId = getAttribute(node);
    }

    /**
    * This method tries to find an ancestor object of type NodeProvider
    * @return the NodeProvider if found else an exception.
    *
    */	
    public NodeProvider findNodeProvider() throws JspTagException {        
        return (NodeProvider) findParentTag(NodeProvider.class, (String) parentNodeId.getValue(this));
    }
    /**
    * This method tries to find an ancestor object of type NodeProvider
    * @return the NodeProvider or null.
    *
    */	
    public NodeProvider findNodeProvider(boolean throwexception) throws JspTagException {        
        return (NodeProvider) findParentTag(NodeProvider.class, (String) parentNodeId.getValue(this), throwexception);
    }

    /**
     * Gets the Node variable from the parent NodeProvider.
     * @return a org.mmbase.bridge.Node
     */

    protected Node getNode() throws JspTagException {
        Node node =  findNodeProvider().getNodeVar();
        if (node != null && element != Attribute.NULL) {
            node = node.getNodeValue(element.getString(this));            
        }
        return node;
    }

    protected void fillStandardParameters(Parameters p) throws JspTagException {
        super.fillStandardParameters(p);
        NodeProvider np = findNodeProvider(false);
        if (np != null) {
            Node node = np.getNodeVar();
            Cloud cloud = node.getCloud();
            p.setIfDefined(Parameter.CLOUD, cloud);
            p.setIfDefined(Parameter.USER, cloud.getUser());
            
        }
    }

}
