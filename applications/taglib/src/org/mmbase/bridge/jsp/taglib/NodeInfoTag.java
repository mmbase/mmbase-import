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
    private static final int TYPE_NODENUMBER= 2;


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
    public Object getValue() {
        return helper.getValue();
    }


    private int type;

    public void setType(String t) throws JspTagException {
        // note: 'nodemanager' and 'guinodemanager' values are deprecated
        // use 'type' and 'guitype' instead
        if ("nodemanager".equalsIgnoreCase(t) || "type".equalsIgnoreCase(t)) {
            type = TYPE_NODEMANAGER;
        } else if ("guinodemanager".equalsIgnoreCase(t) || "guitype".equalsIgnoreCase(t)) {
            type = TYPE_GUINODEMANAGER;
        } else if ("number".equalsIgnoreCase(t)) {
            type = TYPE_NODENUMBER;
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
        default:
        }

        helper.setValue(show);        
        helper.setJspvar(pageContext);  
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_TAG;
    }


    /**
     * Write the value of the nodeinfo.
     */
    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }
}
