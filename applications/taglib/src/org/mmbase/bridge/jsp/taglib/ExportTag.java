/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* The exporttag can take a variable from the context and put it in a jsp variable.
* 
* @author Michiel Meeuwissen
*/
public class ExportTag extends CloudReferrerTag {
    
    private static Logger log = Logging.getLoggerInstance(ExportTag.class.getName()); 

    private String jspvar = null;
    private String key = null;
    private boolean declare = true;
    
    public void setType(String t) {
        // nothing to do, the type property is only used in the TEI.
    }    

    public void setJspvar(String j) {
        jspvar = j;
    }

    public void setKey(String k) {
        key = k;
    }

    
    public int doStartTag() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("getting object " + key + "-> " + getContextTag().getObject(key));
        }    
        Object value = getContextTag().getObject(key);
        if (value != null) {
            pageContext.setAttribute(jspvar, value);
        }
        return EVAL_BODY_TAG;
    }
    public int doAfterBody() throws JspTagException {
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());            
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

}
