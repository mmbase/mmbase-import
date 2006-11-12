/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.NodeTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 * A NodeProvider which creates a new node, which will be commited after the body. So, you
 * can use `setField's in the body.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CreateNodeTag.java,v 1.23 2006-11-12 14:29:33 michiel Exp $
 */

public class CreateNodeTag extends NodeTag {

    private static final Logger log = Logging.getLoggerInstance(CreateNodeTag.class);

    private Attribute nodeManager = Attribute.NULL;

    public void setType(String t) throws JspTagException {
        nodeManager = getAttribute(t);
    }

    public int doStartTag() throws JspTagException{
        Cloud cloud = getCloudVar();
        NodeManager nm = cloud.getNodeManager(nodeManager.getString(this));
        if (nm == null) {
            throw new JspTagException("Could not find nodemanager " + nodeManager.getString(this));
        }
        Node node = nm.createNode();
        if (node == null) {
            throw new JspTagException("Could not create node of type " + nm.getName());
        }

        setNodeVar(node);
        fillVars();
        return EVAL_BODY;
    }


}
