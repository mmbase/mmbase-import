/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.util.Arguments;
import org.mmbase.module.core.MMObjectBuilder;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.bridge.NodeManager;

/**
 * Lives under a nodeprovider. Can give information about the node,
 * like what its nodemanager is.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeInfoTag.java,v 1.26 2003-11-19 16:57:42 michiel Exp $ 
 */

public class NodeInfoTag extends NodeReferrerTag implements Writer {

    private static final int TYPE_NODEMANAGER           = 0;
    private static final int TYPE_GUINODEMANAGER        = 1;
    private static final int TYPE_GUINODEMANAGER_PLURAL = 2;
    private static final int TYPE_NODENUMBER            = 3;
    private static final int TYPE_GUI                   = 4;
    private static final int TYPE_DESCRIPTION           = 5;


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
        } else if ("plural_guinodemanager".equals(t) || "plural_guitype".equals(t)) {
            return TYPE_GUINODEMANAGER_PLURAL;
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
        case TYPE_GUINODEMANAGER_PLURAL:
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
        case TYPE_GUINODEMANAGER_PLURAL:
            show = nodeManager.getGUIName(10);
            break;
        case TYPE_GUI: {
            helper.useEscaper(false); // gui produces html
            String sessionName = "";
            CloudTag ct = null;
            ct = (CloudTag) findParentTag(CloudTag.class, null, false);
            if (ct != null) {
                sessionName = ct.getSessionName();
            }
            Arguments args = new Arguments(MMObjectBuilder.GUI_ARGUMENTS);
            args.set("field", ""); // lot of function implementations would not stand 'null' as field name value
            args.set("language", getCloud().getLocale().getLanguage());
            args.set("session",  sessionName);
            args.set("response", pageContext.getResponse());
            args.set("request",  pageContext.getRequest());
            show = getNode().getFunctionValue("gui", args).toString();
            break;
        }
        default:
        }

        
        helper.setValue(show);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     * Write the value of the nodeinfo.
     */
    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }
}
