/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;

import org.mmbase.bridge.jsp.taglib.ConditionTag;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;

import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if node may be created
 * 
 * @author Michiel Meeuwissen
 */
public class MayCreateTag extends CloudReferrerTag implements ConditionTag {

    protected boolean inverse = false;
    protected String type = null;

    public void setInverse(Boolean b) {
        inverse = b.booleanValue();
    }

    public void setType(String t) throws JspTagException {
        type = getAttributeValue(t);
    }
               
    public int doStartTag() throws JspTagException {
        if ((getCloud().getNodeManager(type).mayCreateNode()) != inverse) {
            return EVAL_BODY_TAG;
        } else {
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspTagException {
        try{
            if(bodyContent != null)
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch(java.io.IOException e){
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }   

    public int doEndTag() throws JspTagException {
        return EVAL_PAGE;
    }
}
