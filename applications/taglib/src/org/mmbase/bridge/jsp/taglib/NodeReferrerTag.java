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
* A tag which is a 'NodeReferrerTag's is for example the FieldTag. A
* FieldTag must be a child of a NodeProvider.
*
* Originally, I named it 'NodeRefererTag' but this would have been a
* misspeling, though a common one. Cite from foldoc:
* <pre>
* From The Free On-line Dictionary of Computing (13 Mar 01) [foldoc]:
*
*  referer
*
*     <World-Wide Web> A misspelling of "referrer" which somehow
*     made it into the {HTTP} standard.  A given {web page}'s
*     referer (sic) is the {URL} of whatever web page contains the
*     link that the user followed to the current page.  Most
*     browsers pass this information as part of a request.
*
*     (1998-10-19)
*
* </pre>
*
* @author Michiel Meeuwissen 
**/

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

    protected Node getNode() throws JspTagException {
        return findNodeProvider().getNodeVar();
    }

}
