/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;

import org.mmbase.bridge.jsp.taglib.util.StringSplitter;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.Casting; // not used enough


/**
 * Tags that are Writers can use the this class. It's a pitty that
 * they can't extend, but that's life.
 *
 *
 * @author Michiel Meeuwissen 
 */

public class WriterHelper extends BodyTagSupport {
    // extending from it, becase we need access to protected vars.
    // this tag is not acutally used as a tag

    private static Logger log = Logging.getLoggerInstance(WriterHelper.class.getName());
    public static boolean NOIMPLICITLIST = true;
    public static boolean IMPLICITLIST   = false;

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
    private   Attribute write          = Attribute.NULL;
    private   Boolean overridewrite    = null;
    private   int     vartype          = TYPE_UNSET;

    private   ContextReferrerTag thisTag  = null;
    
    private   boolean hasBody          = false;


    public WriterHelper() {
    }

    /**
     * For implementation of the write attribute.
     */
    public void setWrite(Attribute w) {
        if (log.isDebugEnabled()) {
            log.debug("Setting write to " + w);
        }
        write = w;
    }

    /**
     * @deprecated Use setWrite(Attribute)
     */
    public void setWrite(Boolean b) { 
        try {
            write = Attribute.getAttribute(b.toString());
        } catch (JspTagException e) {
            log.error(e.toString());
        }
    }

    /**
     * There is a default behavior for what should happen if the 'write' attribute is not set.
     * if you want to override this, then call this function.
     */
    public void overrideWrite(boolean w) {
        overridewrite = w ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean  isWrite() throws JspTagException {
        if (write == Attribute.NULL) {
            if (log.isDebugEnabled()) {
                log.debug("write is unset, using default " + overridewrite + " with body == '" + getString() + "' and hasBody (which is determined by childs) = " + hasBody);
            }
            if (overridewrite != null) return overridewrite.booleanValue();            
            return "".equals(getString()) && (! hasBody);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Write: " + write);
            }
            return write.getBoolean(thisTag, true);
        }
    }
    
    public void setJspvar(String j) {
        jspvar = j;
    }

    /**
     * @deprecated use 'setTag' in doStartTag. Jspvar will be set by setValue then
     */
    public void setJspvar(PageContext p) throws JspTagException {
        pageContext = p;
        setJspvar();
    }

    public String getJspvar() {
        return jspvar;
    }

    /**
     * Sets the vartype for this variable (used for jspvar as well as for taglib var).
     *
     * Some 'casting' functionality is present here.
     */

    public void setValue(Object v) throws JspTagException {
        setValue(v, IMPLICITLIST);        
    }
    public void setValue(Object v, boolean noImplicitList) throws JspTagException {
        value = null;
        switch (vartype) { 
            // these accept a value == null (meaning that they are empty)
        case TYPE_LIST:
            if (v instanceof java.lang.String) {
                if (! "".equals(value)) {
                    value = StringSplitter.split((String) v);
                } else { 
                    value = new java.util.Vector(); 
                }
                setJspvar();
                return;
            }
        case TYPE_VECTOR: // I think the type Vector should be deprecated?
            if (v == null) {
                // if a vector is requested, but the value is not present,
                // make a vector of size 0.
                value = new java.util.Vector();
            }
            if (! (v instanceof java.util.Vector)) {
                // if a vector is requested, but the value is not a vector,
                if (! (v instanceof java.util.Collection)) {
                    // not even a Collection!
                    // make a vector of size 1.
                    value = new java.util.Vector();
                    ((java.util.Vector)value).add(v);
                } else {
                    value = new java.util.Vector((java.util.Collection)v);
                }
            } else {
                value = v;
            }     
            setJspvar();
            return;
        }

        // other can't be valid and still do something reasonable with 'null'.
        if (v == null) {
            value = null;
            setJspvar();
            return;
        } 

        if (noImplicitList) {
            // Take last of list if vartype defined not to be a list:
            if (v instanceof java.util.List) {
                if (vartype != TYPE_LIST && vartype != TYPE_VECTOR) {
                    java.util.List l = (java.util.List) v;
                    if (l.size() > 0) { 
                        v = l.get(l.size() - 1);
                    } else {
                        v = null;
                    }
                }
            }
        }

        // types which cannot accept null;
        switch (vartype) {
        case TYPE_INTEGER:
            if (! (v instanceof Integer)) {
                value = new Integer(v.toString());
                setJspvar();
                return;
            }
            break;
        case TYPE_DOUBLE:
            if (! (v instanceof Double)) {
                value = new Double(v.toString());
                setJspvar();
                return;
            }
            break;
        case TYPE_LONG:
            if (! (v instanceof Long)) {
                value = new Long(v.toString());
                setJspvar();
                return;
            }
            break;
        case TYPE_FLOAT:
            if (! (v instanceof Float)) {
                value =  new Float(v.toString());
                setJspvar();
                return;
            }
            break;
        case TYPE_STRING:
            if (! (v instanceof String)) {                
                value = Casting.toString(v);
                setJspvar();
                return;
            } 
            break;
        case TYPE_NODE:
            if (! (v instanceof org.mmbase.bridge.Node)) {
                throw new JspTagException("Variable is not of type Node, but of type " + v.getClass().getName() + ". Conversion is not yet supported by this Tag");
            }
            break;
        }
        if (value == null) value = v;
        setJspvar();
    }
    

    /**
     * To evaluate Attributes and to obtain the pagecontext for jspvars, the helper needs the Tag.
     * Call this in your doStartTag.
     * @since MMBase-1.7
     */

    public void setTag(ContextReferrerTag b) {
        thisTag     = b;
        pageContext = b.getPageContext();
    }

    public Object getValue() {
        return value;
    }

    
    /**
     * Don't forget to call 'setValue' first!
     */
    
    private void setJspvar() throws JspTagException {      
        if (jspvar == null) return;
        if (log.isDebugEnabled()) {
            log.debug("Setting variable " + jspvar + " to " + value + "(" + (value != null ? value.getClass().getName() : "" ) + ")");
        }
        if (value != null) { 
            // if the underlying implementation uses a Hashtable (TomCat) then the value may not be null
            // When it doesn't, it goes ok. (at least I think that this is the difference between orion and tomcat)
            pageContext.setAttribute(jspvar, value);
        }
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
            // this is an ondocumented feature...
            return org.mmbase.util.Encode.encode("BASE64", (byte[]) value); 
        }
        return value.toString();
    }

    /**
     * To be called by subtags. Even if they don't produce output,
     * they say that the tag had a body.
     */
    public void haveBody() {
        hasBody = true;
    }
    

    public String getString() {
        if (bodyContent != null) {
            return bodyContent.getString();
        } else {
            log.debug("bodycontent is null, returning empty string");
            return "";
        }
    }

    /**
     * Sets the bodycontent (to be used in doEndTag)
     * @since MMBase-1.7
     */

    public int doAfterBody() throws JspException {
        bodyContent = thisTag.getBodyContent();
        return super.doAfterBody();
    }

    /**
     * A basic doEndTag for Writers.
     *
     * It decides if to write or not.
     */
    public int doEndTag() throws JspTagException {
        log.debug("doEndTag of WriterHelper");    
        try {
            String body = getString();
            if (isWrite()) {
                if (bodyContent != null) bodyContent.clearBody(); // clear all space and so on
                log.debug("writing to page");
                pageContext.getOut().print(getPageString() + body);
            } else {
                log.debug("not writing to page");
            }
            if (bodyContent != null) {
                // if this writer (still) has body, it should be written out.
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }            
        release();
        log.debug("End of doEndTag");
        return javax.servlet.jsp.tagext.BodyTagSupport.EVAL_PAGE;
    }

    public void release() {
        overridewrite = null; // for use next time
        hasBody       = false;
        bodyContent   = null;
        pageContext   = null;
        value         = null;
        jspvar        = null;
        write         = Attribute.NULL;
        vartype       = TYPE_UNSET;
    }


}
