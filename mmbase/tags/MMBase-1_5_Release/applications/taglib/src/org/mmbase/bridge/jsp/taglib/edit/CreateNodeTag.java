/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyContent;

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

    private String nodemanager = null;

    public void setType(String t) throws JspTagException {
        nodemanager = getAttributeValue(t);
    }


    public int doStartTag() throws JspTagException{
        Node node;
        NodeManager nm;
        nm = getCloud().getNodeManager(nodemanager);
        if (nm == null) {
            throw new JspTagException("Could not find nodemanager " + nodemanager);
        }
        node = nm.createNode();
        if (node == null) {
            throw new JspTagException("Could not create node of type " + nodemanager);
        }
        setNodeVar(node);
        setModified(); 
        if (log.isDebugEnabled()) {
            log.debug("created node " + node.getValue("gui()"));
        }
        return EVAL_BODY_TAG;
    }

}
