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

    private int type;

    public void setType(String tu) throws JspTagException {
        String t = getAttributeValue(tu).toLowerCase();
        // note: 'nodemanager' and 'guinodemanager' values are deprecated
        // use 'type' and 'guitype' instead
        if ("nodemanager".equals(t) || "type".equals(t)) {
            type = TYPE_NODEMANAGER;
        } else if ("guinodemanager".equals(t) || "guitype".equals(t)) {
            type = TYPE_GUINODEMANAGER;
        } else if ("number".equals(t)) {
            type = TYPE_NODENUMBER;
        } else if ("gui".equals(t)) {
            type = TYPE_GUI;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }

    private String nodeManagerString;
    public void setNodetype(String t) throws JspTagException {
        nodeManagerString = getAttributeValue(t);
    }

    public int doStartTag() throws JspTagException{

        NodeManager nodeManager = null;

        switch(type) {
        case TYPE_NODEMANAGER:
        case TYPE_GUINODEMANAGER:
            if (nodeManagerString == null) { // living as NodeReferrer
                nodeManager = getNode().getNodeManager();
            } else {
                nodeManager = getCloud().getNodeManager(nodeManagerString);
            }
        }
        String show = "";

        // set node if necessary:
        switch(type) {
        case TYPE_NODENUMBER:
            show = ""+getNode().getNumber();
            break;
        case TYPE_NODEMANAGER:
            show = nodeManager.getName();
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
            args.add(sessionName);
            args.add(getCloud().getLocale().getLanguage());
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
