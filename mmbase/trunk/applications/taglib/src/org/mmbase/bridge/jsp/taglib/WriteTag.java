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

    static final int TYPE_UNKNOWN = -10;
    static final int TYPE_UNSET   = -1;
    static final int TYPE_OBJECT  = 0;
    static final int TYPE_LIST    = 1;
    static final int TYPE_VECTOR  = 2;
    static final int TYPE_INTEGER = 3;
    static final int TYPE_STRING  = 4;
    static final int TYPE_NODE    = 5;
    static final int TYPE_BYTES   = 6;

    static final int stringToType(String t) {
        if ("String".equalsIgnoreCase(t)) {
            return TYPE_STRING;
        } else if ("Node".equalsIgnoreCase(t)) {
            return TYPE_NODE;
        } else if ("Integer".equalsIgnoreCase(t)) {
            return TYPE_INTEGER;
        } else if ("Vector".equalsIgnoreCase(t)) {
            return TYPE_VECTOR;
        } else if ("List".equalsIgnoreCase(t)) {
            return TYPE_LIST;
        } else if ("bytes".equalsIgnoreCase(t)) {
            return TYPE_BYTES;
        } else if ("Object".equalsIgnoreCase(t)) {
            return TYPE_OBJECT;
        } else {
            return TYPE_UNKNOWN;
        }
    }


    protected String  jspvar = null;
    private   String  extraBodyContent = null;
    protected int type = TYPE_UNSET;

    public void setType(String t) throws JspTagException {
        // nothing to do, the type property is only used in the TEI.
        type = stringToType(t);
        if (type == TYPE_UNKNOWN) {
            throw new JspTagException("Type " + t + " is not known");
        }
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

    protected void setJspVar(Object value) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("Setting variable " + jspvar + " to " + value);
        }
        switch (type) {
        case TYPE_LIST:
            if (value instanceof java.util.List) {
                pageContext.setAttribute(jspvar, value);
                return;
            }
        case TYPE_VECTOR:
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
            pageContext.setAttribute(jspvar, value);         
            return;
        }
        if (value == null) {
            pageContext.setAttribute(jspvar, null);
            return;
        } 

        switch (type) {
        case TYPE_INTEGER:
            if (! (value instanceof Integer)) {
                pageContext.setAttribute(jspvar, new Integer(value.toString()));
                return;
            } 
            break;
        case TYPE_STRING:
            if (! (value instanceof String)) {
                pageContext.setAttribute(jspvar, value.toString());
                return;
            } 
            break;
        case TYPE_NODE:
            if (! (value instanceof org.mmbase.bridge.Node)) {
                throw new JspTagException("Variable is not of type Node. Conversion is not yet supported by this Tag");
            }
            break;
        }
        pageContext.setAttribute(jspvar, value);

    }
    

    public int doStartTag() throws JspTagException {

        if (log.isDebugEnabled()) {
            log.debug("getting object " + getReferid());
        }
        if (type == TYPE_BYTES) { 
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
                setJspVar(value);                
            } else { // write to page, (set extraBodyContent)
                if (type != TYPE_UNSET) {
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
