/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.StringReader;

import java.util.*;

import org.mmbase.util.StringSplitter;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.Casting; // not used enough

/**
 * Tags that are Writers can use the this class. It's a pitty that
 * they can't extend, but that's life.
 *
 * @author Michiel Meeuwissen
 * @version $Id: WriterHelper.java,v 1.40 2003-11-19 16:57:43 michiel Exp $
 */

public class WriterHelper extends BodyTagSupport {
    // extending from it, becase we need access to protected vars.
    // this tag is not acutally used as a tag

    private static final Logger log = Logging.getLoggerInstance(WriterHelper.class);
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
    static final int TYPE_DATE    = 11;

    static final int TYPE_NODE    = 20;
    static final int TYPE_CLOUD   = 21;
    static final int TYPE_TRANSACTION   = 22;
    static final int TYPE_FIELD   = 23;
    static final int TYPE_FIELDVALUE   = 24;


    static final int stringToType(String tt) {
        String t = tt.toLowerCase();
        if ("string".equals(t)) {
            return TYPE_STRING;
        } else if ("node".equals(t)) {
            return TYPE_NODE;
        } else if ("cloud".equals(t)) {
            return TYPE_CLOUD;
        } else if ("transaction".equals(t)) {
            return TYPE_TRANSACTION;
        } else if ("decimal".equals(t)) {
            return TYPE_DECIMAL;
        } else if ("integer".equals(t)) {
            return TYPE_INTEGER;
        } else if ("long".equals(t)) {
            return TYPE_LONG;
        } else if ("double".equals(t)) {
            return TYPE_DOUBLE;
        } else if ("float".equals(t)) {
            return TYPE_FLOAT;
        } else if ("vector".equals(t)) {
            return TYPE_VECTOR;
        } else if ("list".equals(t)) {
            return TYPE_LIST;
        } else if ("bytes".equals(t)) {
            return TYPE_BYTES;
        } else if ("object".equals(t)) {
            return TYPE_OBJECT;
        } else if ("date".equals(t)) {
            return TYPE_DATE;
        } else if ("field".equals(t)) {
            return TYPE_FIELD;
        } else if ("fieldvalue".equals(t)) {
            return TYPE_FIELDVALUE;
        } else {
            return TYPE_UNKNOWN;
        }
    }

    private   Object  value             = null;

    private   String  jspvar           = null;
    private   Attribute write          = Attribute.NULL;
    private   Attribute escape         = Attribute.NULL;
    private   Boolean overrideWrite    = null;
    private   int     vartype          = TYPE_UNSET;

    private   ContextReferrerTag thisTag  = null;

    private   boolean hasBody          = false;

    private   boolean useEscaper       = true;


    public WriterHelper(ContextReferrerTag tag) {
        thisTag = tag;
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
     * For implementation of the escape attribute.
     * @since MMBase-1.7
     */

    public void setEscape(Attribute e) {
        if (log.isDebugEnabled()) {
            log.debug("Setting escape to " + e);
        }
        escape = e;
    }

    /**
     * Gets specified escaper (as a string) or null (if not set)
     * @since MMBase-1.7
     */

    public String getEscape() throws JspTagException {
        return (String) escape.getValue(thisTag);
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
        overrideWrite = w ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Some writer tags produce very specific content, and take care
     * of escaping themselves (UrlTag). They turn off escaping (on default).
     */

    public void useEscaper(boolean ue) {
        useEscaper = ue;
    }

    public boolean  isWrite() throws JspTagException {
        if (write == Attribute.NULL) {
            if (log.isDebugEnabled()) {
                log.debug("write is unset, using default " + overrideWrite + " with body == '" + getString() + "' and hasBody (which is determined by childs) = " + hasBody);
            }
            if (overrideWrite != null) return overrideWrite.booleanValue();
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
        pageContext = thisTag.getPageContext();
        value = null;
        switch (vartype) {
            // these accept a value == null (meaning that they are empty)
        case TYPE_LIST:
            if (v instanceof String || v == null) {
                if (! "".equals(v)) {
                    value = StringSplitter.split((String) v);
                } else {
                    value = new ArrayList();
                }
            } else if (v instanceof List) {
                value = v;
            } else if (v instanceof Collection) {
                value = new ArrayList((Collection) v);
            } else { // dont' know any more
                value = v; // wil perhaps fail
                
            }
            setJspvar();
            return;
        case TYPE_VECTOR: // I think the type Vector should be deprecated?
            if (v == null) {
                // if a vector is requested, but the value is not present,
                // make a vector of size 0.
                value = new Vector();
            } else if (! (v instanceof Vector)) {
                // if a vector is requested, but the value is not a vector,
                if (! (v instanceof Collection)) {
                    // not even a Collection!
                    // make a vector of size 1.
                    value = new Vector();
                    ((Vector)value).add(v);
                } else {
                    value = new Vector((Collection)v);
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
            if (v instanceof List) {
                if (vartype != TYPE_LIST && vartype != TYPE_VECTOR) {
                    List l = (List) v;
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
                value = Casting.toInteger((v.toString()));
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
        case TYPE_DATE:
            if (! (v instanceof Date)) {
                value = Casting.toDate(v);
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

    protected java.io.Writer getPageString(java.io.Writer w) throws JspTagException, IOException {
        if (value == null) return w;

        if (value instanceof byte[]) {
            // writing bytes to the page?? We write base64 encoded...
            // this is an ondocumented feature...
            w.write(org.mmbase.util.Encode.encode("BASE64", (byte[]) value));
            return w;
        }
        if (useEscaper || escape != Attribute.NULL) {
            CharTransformer escaper;
            if (! escape.getString(thisTag).equals("")) {
                escaper = ContentTag.getCharTransformer(escape.getString(thisTag));
            } else {
                escaper = thisTag.getContentTag().getWriteEscaper();
            }
            if (log.isDebugEnabled()) {
                log.debug("Using escaper " + escaper);
            }
            return  escaper.transform(new StringReader(Casting.toString(value)), w);
        } else {
            Casting.toWriter(w, value);
            return w;
        }
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
                getPageString(pageContext.getOut()).write(body);
            } else {
                log.debug("not writing to page");
            }
            if (bodyContent != null) {
                // if this writer (still) has body, it should be written out.
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
        } catch (IOException ioe){
            throw new TaglibException(ioe);
        }
        release();
        log.debug("End of doEndTag");
        return javax.servlet.jsp.tagext.BodyTagSupport.EVAL_PAGE;
    }

    public void release() {
        overrideWrite = null; // for use next time
        hasBody       = false;
        bodyContent   = null;
        pageContext   = null;
        value         = null;
        write         = Attribute.NULL;
    }


}
