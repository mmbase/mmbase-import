/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import com.finalist.cmsc.mmbase.ResourcesUtil;

@SuppressWarnings("serial")
public class ContentUrlTag extends NodeReferrerTag {

    /** Holds value of property number. */
    private Attribute number = Attribute.NULL;

    public void setNumber(String t) throws JspTagException {
        number = getAttribute(t);
    }

    
    @Override
    public int doStartTag() throws JspException {
        Node node = null;
        int nr = number.getInt(this, -1);
        if (nr == -1) {
            node = getNode();
        }
        else {
            node = getCloudVar().getNode(nr);
        }
        if (node == null) {
            throw new JspTagException("Node not found for content url tag");
        }

        String url = null;
        String builderName = node.getNodeManager().getName();
        if ("attachments".equals(builderName)) {
            url = ResourcesUtil.getServletPath(node, node.getStringValue("number"));
        } else {
            if ("urls".equals(builderName)) {
                url = node.getStringValue("url");
            }
            else {
                url = getContentUrl(node);
            }
        }
        
        if (url != null) {
            helper.setValue(url);
        }

        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }


        
        return EVAL_BODY_BUFFERED;
    }
    
    private String getContentUrl(Node node) {
        String servletpath = ResourcesUtil.getServletPathWithAssociation("content", "/content/*");
        return servletpath + "/" + node.getStringValue("number") + "/" + node.getStringValue("title");
    }

    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }
    
}
