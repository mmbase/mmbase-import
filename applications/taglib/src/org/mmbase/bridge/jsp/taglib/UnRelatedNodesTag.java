/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;


import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like listnodes tag, but is is also a node-referrer, and substracts the related nodes of the referred node.
 *
 * @author Michiel Meeuwissen
 * @version $Id: UnRelatedNodesTag.java,v 1.1 2003-08-01 16:50:58 michiel Exp $ 
 * @since MMBase-1.7
 */

public class UnRelatedNodesTag extends ListNodesTag {
    private static Logger log = Logging.getLoggerInstance(UnRelatedNodesTag.class);


    protected NodeList getNodes() throws JspTagException {
        // obtain a reference to the node through a parent tag
        Node parentNode = getNode();
        if (parentNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }

        NodeList nodes = super.getNodes();
        NodeList relatedNodes = parentNode.getRelatedNodes(getNodeManager());

        nodes.removeAll(relatedNodes);
        return nodes;
    }
    
}

