/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;

import javax.servlet.jsp.JspTagException;


/**
 * Shows the valid state of an mm:form
 *
 * @author Michiel Meeuwissen
 * @version $Id: ValidTag.java,v 1.3 2007-06-27 13:19:56 michiel Exp $
 * @since MMBase-1.8
 */

public class ValidTag extends ContextReferrerTag implements Condition {


    protected Attribute form    = Attribute.NULL;
    protected Attribute inverse = Attribute.NULL;

    public void setForm(String f) throws JspTagException {
        form = getAttribute(f);
    }

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }


    public int doStartTag() throws JspTagException {
        FormTag formTag = getFormTag(true, form);
        if (formTag.isValid() != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }
    
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            if (bodyContent != null) {
                try{
                    if(bodyContent != null) {
                        bodyContent.writeOut(bodyContent.getEnclosingWriter());
                    }
                } catch(java.io.IOException e){
                    throw new TaglibException(e);
                }
            }
        }
        return SKIP_BODY;
    }
}
