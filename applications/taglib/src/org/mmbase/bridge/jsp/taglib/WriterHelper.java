/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import java.io.IOException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Tags that are Writers can use the this class. It's a pitty that
 * they can't extend, but that's life.
 *
 *
 * @author Michiel Meeuwissen 
 */

public class WriterHelper {

    private static Logger log = Logging.getLoggerInstance(WriterHelper.class.getName());

    static final int TYPE_UNKNOWN = -10;
    static final int TYPE_UNSET   = -1;
    static final int TYPE_OBJECT  = 0;
    static final int TYPE_LIST    = 1;
    static final int TYPE_VECTOR  = 2;
    static final int TYPE_INTEGER = 3;
    static final int TYPE_STRING  = 4;
    static final int TYPE_BYTES   = 6;
    static final int TYPE_DOUBLE  = 7;
    static final int TYPE_LONG    = 8;
    static final int TYPE_FLOAT   = 9;
    static final int TYPE_DECIMAL = 10;

    static final int TYPE_NODE    = 20;
    static final int TYPE_CLOUD   = 21;
    static final int TYPE_TRANSACTION   = 22;


    static final int stringToType(String t) {
        if ("String".equalsIgnoreCase(t)) {
            return TYPE_STRING;
        } else if ("Node".equalsIgnoreCase(t)) {
            return TYPE_NODE;
        } else if ("Cloud".equalsIgnoreCase(t)) {
            return TYPE_CLOUD;
        } else if ("Transaction".equalsIgnoreCase(t)) {
            return TYPE_TRANSACTION;
        } else if ("decimal".equalsIgnoreCase(t)) {
            return TYPE_DECIMAL;
        } else if ("Integer".equalsIgnoreCase(t)) {
            return TYPE_INTEGER;
        } else if ("Long".equalsIgnoreCase(t)) {
            return TYPE_LONG;
        } else if ("Double".equalsIgnoreCase(t)) {
            return TYPE_DOUBLE;
        } else if ("Float".equalsIgnoreCase(t)) {
            return TYPE_FLOAT;
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

    private   Object  value             = null;

    private   String  jspvar           = null;
    private   Boolean write            = null;
    private   Boolean overridewrite    = null;
    private   int     vartype          = TYPE_UNSET;
    private   BodyContent  bodyContent;
    private   boolean hasBody          = false;

    /**
     * For implementation of the write attribute.
     */
    public void setWrite(Boolean w) {
        log.debug("Setting write to " + w);
        write = w;
    }

    /**
     * There is a default behavior for what should happen if the 'write' attribute is not set.
     * if you want to override this, then call this function.
     */
    public void overrideWrite(boolean w) {
        overridewrite = w ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean  isWrite() {
        if (write == null) {
            if (log.isDebugEnabled()) {
                log.debug("write is unset, using default " + overridewrite + " with body == '" + bodyContent.getString() + "' and hasBody (which is determined by childs) = " + hasBody);
            }
            if (overridewrite != null) return overridewrite.booleanValue();            
            return "".equals(bodyContent.getString()) && (! hasBody);
        } else {
            log.debug("Write: " + write);
            return write.booleanValue();        
        }
    }
    
    public void setJspvar(String j) {
        jspvar = j;
    }
    public String getJspvar() {
        return jspvar;
    }

    public void setValue(Object v) {
        value = v;
    }
    public void setBodyContent(BodyContent b) {
        bodyContent = b;
    }
    public Object getValue() {
        return value;
    }

    /**
     * Don't forget to call 'setValue' first!
     */
    
    public void setJspvar(javax.servlet.jsp.PageContext pageContext) throws JspTagException {
      
        if (jspvar == null) return;
        if (log.isDebugEnabled()) {
            log.debug("Setting variable " + jspvar + " to " + value);
        }
        switch (vartype) {
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
            // pageContext.setAttribute(jspvar, null);
            return;
        } 

        switch (vartype) {
        case TYPE_INTEGER:
            if (! (value instanceof Integer)) {
                pageContext.setAttribute(jspvar, new Integer(value.toString()));
                return;
            } 
            break;
        case TYPE_DOUBLE:
            if (! (value instanceof Double)) {
                pageContext.setAttribute(jspvar, new Double(value.toString()));
                return;
            }
            break;
        case TYPE_LONG:
            if (! (value instanceof Long)) {
                pageContext.setAttribute(jspvar, new Long(value.toString()));
                return;
            }
            break;
        case TYPE_FLOAT:
            if (! (value instanceof Float)) {
                pageContext.setAttribute(jspvar, new Float(value.toString()));
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
        log.trace("setting as object");
        pageContext.setAttribute(jspvar, value);

    }


    public void setVartype(String t) throws JspTagException {
        vartype = stringToType(t);
        if (vartype == TYPE_UNKNOWN) {
            throw new JspTagException("Type " + t + " is not known");
        }
    }
    
    public int getVartype() {
        return vartype;
    }

    /**
     * Returns a string which can be written to the page.
     */

    protected String getPageString() throws JspTagException {
        if (value == null) return "";

        if (value instanceof byte[]) {         
            // writing bytes to the page?? We write base64 encoded...
            return org.mmbase.util.Encode.encode("BASE64", (byte[]) value); 
        }
        return value.toString();
    }

    /**
     *
     */
    public void haveBody() {
        hasBody = true;
    }
    
    /**
     * A basic afterbody for Writers.
     *
     * It decides if to write or not.
     */
    public int doAfterBody() throws JspTagException {
        try {
            String body = bodyContent.getString();
            if (isWrite()) {
                bodyContent.clearBody();
                log.debug("writing to page");
                bodyContent.print(getPageString() + body);
            } else {
                log.debug("not writing to page");
            }
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }            
        overridewrite = null; // for use next time
        hasBody       = false;
        return javax.servlet.jsp.tagext.BodyTagSupport.SKIP_BODY;
    }

}
