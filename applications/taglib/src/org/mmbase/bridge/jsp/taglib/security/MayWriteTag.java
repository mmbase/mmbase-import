/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;

import org.mmbase.bridge.jsp.taglib.ConditionTag;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;

import javax.servlet.jsp.JspTagException;


/**
* A very simple tag to check if node may be deleted.
* 
* @author Michiel Meeuwissen
*/
public class MayWriteTag extends NodeReferrerTag implements ConditionTag {

    protected boolean inverse = false;

    public void setInverse(Boolean b) {
        inverse = b.booleanValue();
    }
               
    public int doStartTag() throws JspTagException {
        if ((getNode().mayWrite()) != inverse) {
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
        inverse = false;
        return EVAL_PAGE;
    }
}
