/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.util.functions.*;
import org.mmbase.util.Casting;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Lives under a nodeprovider. Can give information about the node,
 * like what its nodemanager is.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeInfoTag.java,v 1.46 2008-08-14 13:59:34 michiel Exp $
 */

public class NodeInfoTag extends NodeReferrerTag implements Writer {

    private static final Logger log = Logging.getLoggerInstance(NodeInfoTag.class);

    private static final int TYPE_NODEMANAGER           = 0;
    private static final int TYPE_GUINODEMANAGER        = 1;
    private static final int TYPE_GUINODEMANAGER_PLURAL = 2;
    private static final int TYPE_NODENUMBER            = 3;
    private static final int TYPE_GUI                   = 4;
    private static final int TYPE_DESCRIPTION           = 5;
    private static final int TYPE_CONTEXT               = 6;
    private static final int TYPE_QUERY                 = 50; // for debug


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
        } else if ("context".equals(t)) {
            return  TYPE_CONTEXT;
        } else if ("number".equals(t)) {
            return  TYPE_NODENUMBER;
        } else if ("gui".equals(t)) {
            return TYPE_GUI;
        } else if ("query".equals(t)) {
            return TYPE_QUERY;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }

    private Attribute nodeManagerAtt = Attribute.NULL;
    public void setNodetype(String t) throws JspTagException {
        nodeManagerAtt = getAttribute(t);
    }

    public int doStartTag() throws JspException{
        initTag();
        NodeManager nodeManager = null;
        if (nodeManagerAtt == Attribute.NULL) { // living as NodeReferrer
            Node node = getNode();
            if (node == null) {
                log.warn("Node is null");

                nodeManager = getCloudVar().getNodeManager("object");
            } else {
                nodeManager = node.getNodeManager();
            }
        } else {
            nodeManager = getCloudVar().getNodeManager(nodeManagerAtt.getString(this));
        }
        Object show = "";

        // set node if necessary:
        int t = getType();
        switch(t) {
        case TYPE_NODENUMBER:
            if (nodeManagerAtt == Attribute.NULL) { // living as NodeReferrer
                show = getNode().getNumber();
            } else {
                show = nodeManager.getNumber();
            }
            break;
        case TYPE_CONTEXT:
            if (nodeManagerAtt == Attribute.NULL) { // living as NodeReferrer
                show = getNode().getContext();
            } else {
                show = nodeManager.getContext();
            }
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
            if (nodeManagerAtt == Attribute.NULL) { // living as NodeReferrer
                helper.useEscaper(false); // gui produces html
                String sessionName = "";
                CloudTag ct = findParentTag(CloudTag.class, null, false);
                if (ct != null) {
                    sessionName = ct.getSessionName();
                }
                Node node = getNode();
                if (node == null) {
                    show = "NULL";
                } else {
                    Function guiFunction = node.getFunction("gui");
                    Parameters args = guiFunction.createParameters();
                    args.set(Parameter.FIELD, ""); // lot of gui implementations would not stand 'null' as field name value
                    if (args.containsParameter("session")) {
                        args.set("session",  sessionName);
                    }
                    fillStandardParameters(args);
                    show = Casting.toString(guiFunction.getFunctionValue(args));
                }
            } else {
                show = nodeManager.getGUIName();
            }
            break;
        }
        case TYPE_QUERY:
            show = findNodeProvider().getGeneratingQuery();
            break;
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
        helper.doEndTag();
        return super.doEndTag();

    }
}
