/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.BodyContent;
import java.io.IOException;

import java.util.*;

import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.bridge.jsp.taglib.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.Casting; // not used enough

/**
 * Tags that are Writers can use the this class. It's a pitty that
 * they can't extend, but that's life.
 *
 * @author Michiel Meeuwissen
 * @version $Id: WriterHelper.java,v 1.69 2005-05-04 22:24:51 michiel Exp $
 */

public class WriterHelper {

    private static final Logger log = Logging.getLoggerInstance(WriterHelper.class);
    public static final boolean NOIMPLICITLIST = true;
    public static final boolean IMPLICITLIST   = false;

    public static final String STACK_ATTRIBUTE = "org.mmbase.bridge.jsp.taglib._Stack";

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
    static final int TYPE_FIELD         = 23;
    static final int TYPE_FIELDVALUE    = 24;
    static final int TYPE_BOOLEAN       = 25;
    static final int TYPE_CHARSEQUENCE  = 26;


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
        } else if ("boolean".equals(t)) {
            return TYPE_BOOLEAN;
        } else if ("charsequence".equals(t)) {
            return TYPE_CHARSEQUENCE;
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

    private   BodyContent bodyContent;


    /**
     * 'underscore' stack, containing the values for '_'.
     * @since MMBase_1.8
     */
    private   Stack _Stack;
    private   boolean pushed = false;

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
     * @since MMBase-1.7
     */
    public Attribute getWrite() {
        return write;
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
     * @since MMBase-1.7.4
     */
    private boolean overrideNoImplicitList = false;
    public void overrideNoImplicitList() {
         overrideNoImplicitList = true;
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
            if (overrideWrite != null) {
                log.debug("override-write was used --> " + overrideWrite);
                return overrideWrite.booleanValue();
            }
            boolean result = "".equals(getString()) && (! hasBody);
            log.debug("Result " + result + " with body-string '" + getString() + "' and hasbody " + hasBody);
            return result; 
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
     * @deprecated jspvar will be set by setValue then
     */
    public void setJspvar(PageContext p) throws JspTagException {
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
    /**
     * @since MMBase-1.8
     */
    public CharTransformer getEscaper() throws JspTagException {
        String e = getEscape();
        if (e == null) {
            return (CharTransformer) thisTag.getPageContext().getAttribute(ContentTag.ESCAPER_KEY);
        } else {
            return ContentTag.getCharTransformer((String) e, thisTag.getContextProvider().getContextContainer());
        }
    }
    public void setValue(Object v, boolean noImplicitList) throws JspTagException {
        value = null;
        if (noImplicitList && ! overrideNoImplicitList &&  vartype != TYPE_LIST && vartype != TYPE_VECTOR) {
            // Take one element of list if vartype defined not to be a list.
            // this is usefull when using mm:includes and passing a var which also can be on the request
            if (v instanceof List) {
                List l = (List) v;
                if (l.size() > 0) {
                    // v = l.get(l.size() - 1); // last element
                    v = l.get(0);               // first element, allows for 'overriding'.
                } else {
                    v = null;
                }
            }
        }
        if (v != null || vartype == TYPE_LIST || vartype == TYPE_VECTOR) {
            switch (vartype) {
                // these accept a value == null (meaning that they are empty)
                case TYPE_LIST:
                    if (! (v instanceof List)) {
                        if ("".equals(v)) {
                            v = new ArrayList();
                        } else {
                            v = Casting.toList(v);
                        }
                    }
                    break;
                case TYPE_VECTOR: // I think the type Vector should be deprecated?
                    if (v == null) {
                        // if a vector is requested, but the value is not present,
                        // make a vector of size 0.
                        v = new Vector();
                    } else if (! (v instanceof Vector)) {
                        // if a vector is requested, but the value is not a vector,
                        if (! (v instanceof Collection)) {
                            // not even a Collection!
                            // make a vector of size 1.
                            Vector vector = new Vector();
                            vector.add(v);
                            v = vector;
                        } else {
                            v = new Vector((Collection)v);
                        }
                    }
                    break;
                case TYPE_UNSET:
                    break;
                case TYPE_INTEGER:
                    if (! (v instanceof Integer)) {
                        v = Casting.toInteger(v);
                    }
                    break;
                case TYPE_DOUBLE:
                    if (! (v instanceof Double)) {
                        v = new Double(Casting.toDouble(v));
                    }
                    break;
                case TYPE_LONG:
                    if (! (v instanceof Long)) {
                        v = new Long(Casting.toLong(v));
                    }
                    break;
                case TYPE_FLOAT:
                    if (! (v instanceof Float)) {
                        v = new Float(Casting.toFloat(v));
                    }
                    break;
                case TYPE_DECIMAL:
                    if (! (v instanceof java.math.BigDecimal)) {
                        v =  new java.math.BigDecimal(v.toString());
                    }
                    break;
                case TYPE_STRING:
                    if (! (v instanceof String)) {
                        v = Casting.toString(v);
                    }
                    break;
                case TYPE_CHARSEQUENCE:
                    if (! (v instanceof CharSequence)) {
                        v = Casting.toString(v);
                    }
                    break;
                case TYPE_DATE:
                    if (! (v instanceof Date)) {
                        v = Casting.toDate(v);
                    }
                    break;
                case TYPE_BOOLEAN:
                    if (! (v instanceof Boolean)) {
                        v = new Boolean(Casting.toBoolean(v));
                    }
                    break;
                case TYPE_NODE:
                    if (! (v instanceof org.mmbase.bridge.Node)) {
                        throw new JspTagException("Variable is not of type Node, but of type " + v.getClass().getName() + ". Conversion is not yet supported by this Tag");
                    }
                    break;
                default:
                    log.debug("Unknown vartype" + vartype);
                    break;
            }
            value = v;
        }

        PageContext pageContext = thisTag.getPageContext();

        _Stack = (Stack) pageContext.getAttribute(STACK_ATTRIBUTE);
        if (_Stack == null) {
            _Stack = new Stack();
            pageContext.setAttribute(STACK_ATTRIBUTE, _Stack);
        }

        setJspvar();
        if (pushed) {
            if (log.isDebugEnabled()) {
                log.debug("Value was set already by this tag");
            }
            _Stack.set(_Stack.size() - 1, value);
        } else {
            _Stack.push(value);
            pushed = true;
        }
        pageContext.setAttribute("_", Casting.wrap(value, getEscaper()));
        if (log.isDebugEnabled()) {
            log.debug("pushed " + value + " on _stack, for " + thisTag.getClass().getName() + "  now " + _Stack);
        }
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
            PageContext pageContext = thisTag.getPageContext();
            Object was = pageContext.getAttribute(jspvar);
            if (!pushed && was != null && ! was.equals(value)) {
                // throw new JspTagException("Jsp-var '" + jspvar + "' already in pagecontext! (" + was.getClass().getName() + " " + was + "), can't write " + value.getClass().getName() + " " + value + " in it. This may be a backwards-compatibility issue. Change jspvar name or switch on backwards-compatibility mode (in your web.xml)");
            }
            // if the underlying implementation uses a Hashtable (Tomcat) then the value may not be null
            // When it doesn't, it goes ok. (at least I think that this is the difference between orion and tomcat)
            if (vartype == TYPE_STRING) {
                // string is final, the wrapped version cannot be string..
                pageContext.setAttribute(jspvar, Casting.unWrap(value));
            } else {
                pageContext.setAttribute(jspvar, value);
            }
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
                escaper = ContentTag.getCharTransformer(escape.getString(thisTag), thisTag.getContextProvider().getContextContainer());
            } else {
                escaper = thisTag.getContentTag().getWriteEscaper();
            }
            if (log.isDebugEnabled()) {
                log.debug("Using escaper " + escaper);
            }
            if (escaper != null) {
                w.write(escaper.transform(Casting.toString(value)));
                return w;
            } else {
                return  Casting.toWriter(w, value);
            }
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
     * Pops one value of the _-stack, if not yet happend, if stack exists.
     * Puts the value of peek in "_" then, if not the stack is empty now, in which case "_" is removed.
     * @since MMBase-1.8
     */
    private void pop_Stack() throws JspTagException {
        if (_Stack != null) {
            Object pop = _Stack.pop();
            if (log.isDebugEnabled()) {
                log.debug("Removed " + pop +  "( " + (pop == null ? "NULL" : pop.getClass().getName()) + ")  from _stack for " + thisTag.getClass().getName() + " now: " + _Stack);
            }
            if (_Stack.empty()) {
                thisTag.getPageContext().removeAttribute("_");
            } else {
                thisTag.getPageContext().setAttribute("_", Casting.wrap(_Stack.peek(), getEscaper()));
            }
            _Stack = null;
        }
    }

    /**
     * Sets the bodycontent (to be used in doEndTag)
     * @since MMBase-1.7
     */
    public int doAfterBody() throws JspTagException {
        pop_Stack();
        pushed = false;
        bodyContent = thisTag.getBodyContent();
        return javax.servlet.jsp.tagext.Tag.SKIP_BODY;
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
                log.debug("Must write to page");
                if (bodyContent != null) bodyContent.clearBody(); // clear all space and so on
                getPageString(thisTag.getPageContext().getOut()).write(body);
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
        pop_Stack();
        release();
        log.debug("End of doEndTag");
        return javax.servlet.jsp.tagext.Tag.EVAL_PAGE;
    }

    public void release() {
        overrideWrite = null; // for use next time
        hasBody       = false;
        value         = null;
        _Stack        = null;
        pushed        = false;
        bodyContent   = null;
    }


}
