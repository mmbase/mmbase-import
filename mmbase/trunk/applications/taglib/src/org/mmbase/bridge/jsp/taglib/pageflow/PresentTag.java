/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.bridge.jsp.taglib.ContextTag;

import org.mmbase.bridge.jsp.taglib.ConditionTag;

import javax.servlet.jsp.JspTagException;


/**
* A very simple tag to check if certain id is present in the parent context.
* 
* @author Michiel Meeuwissen
*/
public class PresentTag extends ContextReferrerTag implements ConditionTag {

    protected boolean inverse = false;

    public void setInverse(Boolean b) {
        inverse = b.booleanValue();
    }
               
    public int doStartTag() throws JspTagException {
        if ((getContextTag().isPresent(getReferid())) != inverse) {
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

}
