/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.BodyTag;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import org.mmbase.bridge.jsp.taglib.NodeTag;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* A NodeProvider which creates a new node, which will be commited after the body. So, you
* can use `setField's in the body.
*
* @author Michiel Meeuwissen
*/
public class CreateNodeTag extends NodeTag implements BodyTag {

    private static Logger log = Logging.getLoggerInstance(CreateNodeTag.class.getName());

    private Attribute nodeManager = Attribute.NULL;

    public void setType(String t) throws JspTagException {
        nodeManager = getAttribute(t);
    }


    public int doStartTag() throws JspTagException{
        Node node;
        NodeManager nm;
        nm = getCloud().getNodeManager(nodeManager.getString(this));
        if (nm == null) {
            throw new JspTagException("Could not find nodemanager " + nodeManager.getString(this));
        }
        node = nm.createNode();
        if (node == null) {
            throw new JspTagException("Could not create node of type " + nm.getName());
        }
        setNodeVar(node);
        setModified();
        if (log.isDebugEnabled()) {
            log.debug("created node " + node.getValue("gui()"));
        }
        fillVars();
        return EVAL_BODY_BUFFERED;
    }

}
