/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.cache.Cache;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * MMBase Taglib attributes can contain $-variables. Parsing of these $-variables is
 * cached. Evaluation of these vars must be postponed until doStartTag because servlet containers can
 * decide not to call the set-function of the attribute (in case of tag-instance-reuse).
 *
 * @author Michiel Meeuwissen
 * @version $Id: Attribute.java,v 1.28.2.1 2008-04-10 16:35:58 michiel Exp $
 * @since   MMBase-1.7
 */

public class Attribute {
    private static final Logger log = Logging.getLoggerInstance(Attribute.class);

    private final static AttributeCache cache = new AttributeCache();
    // not sure the cache is actually useful for performance, perhaps it can as well be switched off.

    public final static Attribute NULL = new NullAttribute();

    static {
        // put the cache in the mmbase cache repository (making it configurable)
        cache.putCache();
    }

    /**
     * This is the function for public use. It takes the string and returns an Attribute, creating
     * a new one if it is not in the Attribute cache.
     */
    public static final Attribute getAttribute(final Object at) throws JspTagException {
        if (at == null) return NULL;
        return cache.getAttribute(at);
    }

    /**
     * Whether the attribute contains any $-vars.
     */
    private  boolean containsVars;

    final boolean containsVars() {
        return containsVars;
    }

    /**
     * The unparsed attribute.
     */
    private Object attribute;

    /**
     * List of AttributeParts (the parsed attribute). This can be null
     * if containsVars is false (then simply 'attribute' can be returned
     * as value).
     */
    private List    attributeParts;

    /**
     * The constructor is protected, construction is done by the cache.
     */
    protected Attribute(Object at) throws JspTagException {
        attribute = at;
        parse();
    }

    protected Attribute() {}

    /**
     * Appends the evaluated Attribute to StringBuffer
     *
     * @param tag The tag relative to which the variable evalutations must be done
     *            (normally 'this' in a Tag implementation)
     */

    public void appendValue(ContextReferrerTag tag, StringBuffer buffer) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("Appending " + attribute);
        }
        if (! containsVars) buffer.append(attribute.toString());
        Iterator i = attributeParts.iterator();
        while (i.hasNext()) {
            Part ap = (Part) i.next();
            ap.appendValue(tag, buffer);
        }
    }

    /**
     * Returns the evaluated Attribute as an Object. Can also be null.
     */
    public Object getValue(ContextReferrerTag tag) throws JspTagException {
        if (! containsVars) return attribute;

        if (attributeParts.size() == 1) { // avoid construction of StringBuffer for this simple case
            Part ap = (Part) attributeParts.get(0);
            return ap.getValue(tag);
        }
        StringBuffer result = new StringBuffer();
        appendValue(tag, result);
        return result.toString();
    }


    /**
     * Returns the evaluated Attribute as a String. This is never null (empty string in that case)..
     */
    public String getString(ContextReferrerTag tag) throws JspTagException {
        return Casting.toString(getValue(tag));
    }

    /**
     * Returns the evaluated Attribute as a int
     */

    public int getInt(ContextReferrerTag tag, int def) throws JspTagException {
        return org.mmbase.util.Casting.toInt(getValue(tag), def);
    }

    public long getLong(ContextReferrerTag tag, long def) throws JspTagException {
        return org.mmbase.util.Casting.toLong(getValue(tag), def);
    }

    /**
     * Returns the evaluated Attribute as a List (evalatued to comma-seperated String, which is 'split').
     * The List is empty if getValue would give empty String or null.
     *
     */

    public List getList(ContextReferrerTag tag) throws JspTagException {
        String string = getString(tag).trim();
        return "".equals(string) ? Collections.EMPTY_LIST : Arrays.asList(string.split("\\s*,\\s*"));
    }

    /**
     * Returns the evaluated Attribute as a boolen (depending on if getValue returns one of the
     * strings 'true' or 'false' (case insensitve)).
     *
     * @param  def If the string is not "true" or "false', then this value is returned.
     * @return true or false
     */

    public boolean getBoolean(ContextReferrerTag tag, boolean def) throws JspTagException {
        String val = getString(tag).toLowerCase();
        if ("true".equals(val)) return true;
        if ("false".equals(val)) return false;
        if ("yes".equals(val)) return true;
        if ("no".equals(val)) return false;
        if ("1".equals(val)) return true;
        if ("0".equals(val)) return false;
        if ("".equals(val)) return def;
        throw new JspTagException(" " + getString(tag) + " is no boolean");
    }

    /**
     * String representation of this Attribute object (for debugging)
     */
    public String toString() {
        return "att: " + attribute.toString() + " parts: " + attributeParts;
    }

    /**
     * Parses this attribute into list of 'attributeparts'. This is
     * the heart of the Attribute class.  The method {@link #getValue}
     * will concatenate them together again (after evaluation).
     */

    protected void parse() throws JspTagException {
        String attr = (String) attribute;
        // search all occurences of $
        int foundPos     = attr.indexOf('$');
        if (foundPos == -1) {
            containsVars = false;
            return; // if none, return imediately.
        } else {
            attributeParts = new ArrayList();
            containsVars = true;
        }

        int pos          = 0;
        while (foundPos >= 0) { // we found a variable!
            String npart = attr.substring(pos, foundPos);
            if (npart.length() > 0) {
                attributeParts.add(new StringPart(npart));
            }
            // piece of string until now is ready.
            foundPos ++;
            if (foundPos >= attr.length()) { // end of string
                // could not happen :-)
                break;
            }
            char c = attr.charAt(foundPos);
            if (c == '{' || c == '[') { // using parentheses
                char close = (c == '{' ? '}' : ']');
                // find matching closing parenthes
                pos = ++foundPos;
                int opened = 1;
                while (opened > 0) {
                    int posClose = attr.indexOf(close, pos);
                    if (posClose == -1) {
                        log.error("Unbalanced parentheses in '" + this + "'");
                        throw new AttributeException("Unbalanced parentheses in '" + this + "'");
                    }
                    int posOpen  = attr.indexOf(c, pos);

                    if (posOpen > -1 && posOpen < posClose) { // another one was opened!
                        opened++;
                        pos = posOpen + 1;
                    } else {
                        opened--;
                        pos = posClose + 1;
                    }
                }
                if (attr.charAt(foundPos) != '+') {
                    Attribute var = getAttribute(attr.substring(foundPos, pos - 1));
                    attributeParts.add(new VariablePart(var));
                } else {
                    Attribute var = getAttribute(attr.substring(foundPos + 1, pos - 1));
                    attributeParts.add(new ExpressionPart(var));
                }
            } else { // not using parentheses.
                pos = foundPos;
                if (c == '$') { // make escaping of $ possible
                    attributeParts.add(new StringPart("$"));
                    pos++;
                } else {        // search until non-identifier
                    StringBuffer varName = new StringBuffer();
                    while (ContextContainer.isContextIdentifierChar(c)) {
                        varName.append(c);
                        pos++;
                        if (pos >= attr.length()) break; // end of string
                        c = attr.charAt(pos);
                    }
                   Attribute var = getAttribute(varName.toString());
                   attributeParts.add(new VariablePart(var));
                }
            }
            // ready with this $, search next occasion;
            foundPos = attr.indexOf('$', pos);
        }
        // no more $'es, add rest of string
        String rest = attr.substring(pos);
        if (rest.length() > 0) {
            attributeParts.add(new StringPart(rest));
        }
        return;
    }

    /**
     * A Part represents one part of an Attribute.
     */

    static abstract class Part {

        protected Object part;

        /**
         * Returns the 'type' of a Part as a string. For debugging use.
         */
        abstract protected String getType();

        /**
         * String representation of this AttributePart (for debugging)
         */
        public String toString() {
            return "(" + getType() + "/" + part.toString() + ")";
        }

        abstract Object getValue(ContextReferrerTag tag) throws JspTagException;

        final void  appendValue(ContextReferrerTag tag, StringBuffer buffer) throws JspTagException {
            Casting.toStringBuffer(buffer, getValue(tag));
        }
    }

    /**
     * A part containing a $-variable.
     */
    static class VariablePart extends Part {
        protected boolean containsVars; //wether the name of variable itself contains variables.
        VariablePart(Attribute a) throws JspTagException {
            containsVars = a.containsVars();
            if (containsVars) {
                part = a;
            } else {
                String var =  (String) a.getValue(null);
                if (var.length() < 1) {
                    log.error("Expression too short :" + var);
                    throw new AttributeException("Expression too short");
                }
                part = var;
            }
        }
        protected String getType() { return "Variable"; }
        final Object getValue(ContextReferrerTag tag) throws JspTagException {
            String v;
            if (containsVars) {
                v = (String) ((Attribute) part).getValue(tag);
            } else {
                v = (String) part;
            }
            if ("_".equals(v)) {
                return tag.findWriter().getWriterValue();
            } else {
                return tag.getObject(v);
            }
        }
    }

    /**
     * A ${+ } part containing an 'expression'.  This is in fact an
     * undocumented feature of the taglib. It is based on ExprCalc of
     * org.mmbase.util.
     */

    static class ExpressionPart extends Part {
        protected boolean evaluated;
        protected String getEvaluated() {
            return evaluated ? "evaluated" : "not evaluated";
        }

        ExpressionPart(Attribute a) throws JspTagException {
            if (a.containsVars()) {
                evaluated = false;
                part = a;
            } else {
                evaluated = true;
                ExprCalc cl = new ExprCalc((String) a.getValue(null));
                part = new Double(cl.getResult());
            }
        }
        protected String getType() { return "Expression (" + getEvaluated() + ")"; }
        final Object getValue(ContextReferrerTag tag) throws JspTagException {
            if (evaluated) {
                return part;
            } else {
                ExprCalc cl = new ExprCalc( ((Attribute) part).getString(tag));
                return new Double(cl.getResult());
            }
        }
    }

    /**
     * A simple 'string' part, wich does not need any evaluating or parsing any more.
     *
     */

    static class StringPart extends Part {
        StringPart(String o) {  part = o; }
        protected String getType() { return "String"; }
        final Object getValue(ContextReferrerTag tag) throws JspTagException {
            return part;
        }
    }
}


/**
 * Cache which relates unparsed Attribute Strings with parsed
 * `Attribute' objects.  It is not sure that this cache actually
 * increases performance. Perhaps we can as well do without.

 */

class AttributeCache extends Cache {

    AttributeCache() {
        super(1000);
    }

    public String getName()        { return "TagAttributeCache"; }
    public String getDescription() { return "Cache for parsed Tag Attributes"; }
    public final Attribute getAttribute(final Object att) throws JspTagException {
        Attribute res = (Attribute) super.get(att);
        if (res == null) {
            res = new Attribute(att);
            super.put(att, res);
        }
        return res;
    }

}

/**
 * Exception related to errors in tag-attributes
 */
class AttributeException extends JspTagException {
    AttributeException(String s) { super(s); }
}

/**
 * The attribute containing 'null' is special. No parsing needed, nothing needed. It is very often
 * used, so we provide an implementation optimized for speed.
 */
final class NullAttribute extends Attribute {
    NullAttribute() { }
    public final Object getValue(ContextReferrerTag tag)  throws JspTagException { return null; }
    public final String getString(ContextReferrerTag tag) throws JspTagException { return ""; }
    public final void   appendValue(ContextReferrerTag tag, StringBuffer buffer) throws JspTagException { return; }
    public final String toString() { return "NULLATTRIBUTE"; }
}
