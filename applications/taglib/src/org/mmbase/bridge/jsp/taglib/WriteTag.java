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
* The writetag can take a variable from the context and put it in a
* jsp variable, or write it to the page.
*
* @author Michiel Meeuwissen 
*/

public class WriteTag extends ContextReferrerTag {

    private static Logger log = Logging.getLoggerInstance(WriteTag.class.getName());

    protected String  jspvar = null;
    private String  extraBodyContent = null;
    protected String  type = null;

    public void setType(String t) {
        // nothing to do, the type property is only used in the TEI.
        type = t;
    }

    public void setJspvar(String j) {
        jspvar = j;
    }


    /**
     * Release all allocated resources.
     */
    public void release() {   
        log.debug("releasing" );
        super.release();       
    }
    

    public int doStartTag() throws JspTagException {

        if (log.isDebugEnabled()) {
            log.debug("getting object " + getReferid());
        }
        if ("bytes".equalsIgnoreCase(type)) { 
            log.debug("Indicated that this are bytes");
            // writing bytes to the page?? We write uuencoded...
            if (jspvar != null) {
                throw new JspTagException("Jspvar of type 'bytes' is not supported");                
            }            
            byte [] bytes = getContextTag().getBytes(getReferid());
            if (bytes != null) {
                extraBodyContent = org.mmbase.util.Encode.encode("BASE64", bytes); 
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("-> " + getObject(getReferid()));
            }
            
            Object value = getObject(getReferid());
            
            extraBodyContent = null;
            if (jspvar != null) { // a jspvar was defined, don't write to page.
                if ("Vector".equalsIgnoreCase(type)) {
                    if (value == null) {
                        // if a vector is requested, but the value is not present,
                        // make a vector of size 0.
                        value = new java.util.Vector();
                    }
                    if (! (value instanceof java.util.Vector)) {
                        // if a vector is requested, but the value is not a vector,
                        if (! (value instanceof java.util.Collection)) {
                            // not even a Collection!
                            // make a vector of size 1.
                            java.util.Vector v = new java.util.Vector();
                            v.add(value);
                            value = v;
                        } else {
                            value = new java.util.Vector((java.util.Collection)value);
                        }
                    }             
                    
                } 
                pageContext.setAttribute(jspvar, value);
                
            } else { // write to page, (set extraBodyContent)
                if (type != null) {
                    throw new JspTagException("It does not make sense to specify the type attribute (" + type + ") without the jspvar attribute (unless the type is 'bytes'");
                }
                if (value != null) {
                    extraBodyContent = value.toString();
                }
            }            
           

        }
    
        return EVAL_BODY_TAG;
    }    
    
    public int doAfterBody() throws JspTagException {
        try {
            if (extraBodyContent != null) {
                log.debug("extra body content" + extraBodyContent);
                String body = bodyContent.getString();
                bodyContent.clearBody();
                bodyContent.print(extraBodyContent + body);
            }
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }

    }

}
