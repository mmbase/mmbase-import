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
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

/**
 * Lives under a nodeprovider. Can give information about the node,
 * like what its nodemanager is.
 *
 * @author Michiel Meeuwissen
 */

public class NodeInfoTag extends NodeReferrerTag implements Writer {

    private static final int TYPE_NODEMANAGER    = 0;
    private static final int TYPE_GUINODEMANAGER = 1;
    private static final int TYPE_NODENUMBER     = 2;
    private static final int TYPE_GUI            = 3;
    private static final int TYPE_DESCRIPTION = 4;


    protected WriterHelper helper = new WriterHelper();
    public void setVartype(String t) throws JspTagException {
        helper.setVartype(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getWriterValue() {
        return helper.getValue();
    }
    public void haveBody() { helper.haveBody(); }

    private Attribute type = Attribute.NULL;

    public void setType(String tu) throws JspTagException {
        type = getAttribute(tu);
    }

    private int getType() throws JspTagException {
        String t = type.getString(this).toLowerCase();
        // note: 'nodemanager' and 'guinodemanager' values are deprecated
        // use 'type' and 'guitype' instead
        if ("nodemanager".equals(t) || "type".equals(t)) {
            return TYPE_NODEMANAGER;
        } else if ("guinodemanager".equals(t) || "guitype".equals(t)) {
            return TYPE_GUINODEMANAGER;
        } else if ("description".equals(t)) {
            return  TYPE_DESCRIPTION;
        } else if ("number".equals(t)) {
            return  TYPE_NODENUMBER;
        } else if ("gui".equals(t)) {
            return TYPE_GUI;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }

    private Attribute nodeManagerAtt = Attribute.NULL;
    public void setNodetype(String t) throws JspTagException {
        nodeManagerAtt = getAttribute(t);
    }

    public int doStartTag() throws JspTagException{

        NodeManager nodeManager = null;
        int t = getType();
        switch(t) {
        case TYPE_NODEMANAGER:
        case TYPE_DESCRIPTION:
        case TYPE_GUINODEMANAGER:
            if (nodeManagerAtt == Attribute.NULL) { // living as NodeReferrer
                nodeManager = getNode().getNodeManager();
            } else {
                nodeManager = getCloud().getNodeManager(nodeManagerAtt.getString(this));
            }
        }
        String show = "";

        // set node if necessary:
        switch(t) {
        case TYPE_NODENUMBER:
            show = ""+getNode().getNumber();
            break;
        case TYPE_NODEMANAGER:
            show = nodeManager.getName();
            break;
        case TYPE_DESCRIPTION:
            show = nodeManager.getDescription();
            break;
        case TYPE_GUINODEMANAGER:
            show = nodeManager.getGUIName();
            break;
        case TYPE_GUI: {
            String sessionName = "";
            CloudTag ct = null;
            ct = (CloudTag) findParentTag("org.mmbase.bridge.jsp.taglib.CloudTag", null, false);
            if (ct != null) {
                sessionName = ct.getSessionName();
            }

            java.util.List args = new java.util.Vector();
            args.add("");
            args.add(getCloud().getLocale().getLanguage());
            args.add(sessionName);
            args.add(pageContext.getResponse());

            show = getNode().getFunctionValue("gui", args).toString();
            break;
        }
        default:
        }

        helper.setValue(show);
        helper.setJspvar(pageContext);
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        helper.setBodyContent(getBodyContent());
        return super.doAfterBody();
    }

    /**
     * Write the value of the nodeinfo.
     */
    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }
}
