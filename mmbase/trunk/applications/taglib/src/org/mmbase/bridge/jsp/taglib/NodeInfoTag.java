/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;

/**
* Lives under a nodeprovider. Can give information about the node,
* like what its nodemanager is.
*
* @author Michiel Meeuwissen
*/

public class NodeInfoTag extends NodeReferrerTag {

    private static final int TYPE_NODEMANAGER    = 0;
    private static final int TYPE_GUINODEMANAGER = 1;

    private int type;

    public void setType(String t) throws JspTagException {
        // note: 'nodemanager' and 'guinodemanager' values are deprecated
        // use 'type' and 'guitype' instead
        if ("nodemanager".equalsIgnoreCase(t) || "type".equalsIgnoreCase(t)) {
            type = TYPE_NODEMANAGER;
        } else if ("guinodemanager".equalsIgnoreCase(t) || "guitype".equalsIgnoreCase(t)) {
            type = TYPE_GUINODEMANAGER;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }

    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }


    /**
    * Write the value of the nodeinfo.
    */
    public int doAfterBody() throws JspTagException {

        Node node = findNodeProvider().getNodeVar();

        String show = "";

        // set node if necessary:
        switch(type) {
        case TYPE_NODEMANAGER:
            show = node.getNodeManager().getName();
            break;
        case TYPE_GUINODEMANAGER:
            show = node.getNodeManager().getGUIName();
            break;
        default:
        }

        try {
            bodyContent.print(show);
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());
        }
        return SKIP_BODY;
    }
}
