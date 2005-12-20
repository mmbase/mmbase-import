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
 * FormTag can be used to generate the 'action' attribute of an HTML Form. But more importantly, it
 * collects 'validation' from mm:fieldinfo type="check" or type="errors".
 * 
 * The result can be reported with mm:valid.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FormTag.java,v 1.1 2005-12-20 23:00:47 michiel Exp $
 * @since MMBase-1.8
 */

public class FormTag extends org.mmbase.bridge.jsp.taglib.pageflow.UrlWriterTag {

    protected boolean valid = true;
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }
    public int doStartTag() throws JspTagException {
        valid = true;
        return EVAL_BODY;
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
