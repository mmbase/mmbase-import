/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.JspTagException;

import org.mmbase.cache.Cache;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.bridge.jsp.taglib.ContextTag;
import java.util.*;
import org.mmbase.util.ExprCalc;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MMBase Taglib attributes can contain $-variables. Parsing of these $-variable is
 * cached. Evaluation of these vars must be postponed until doStartTag because servlet containers can 
 * decide not to call the set-function of the attribute (in case of tag-instance-reuse).
 *
 * @author Michiel Meeuwissen
 * @version $Id: Attribute.java,v 1.1 2002-12-24 15:41:30 michiel Exp $
 * @since   MMBase-1.6.1
 */


/**
 * Exception related to errors in tag-attributes
 */
class AttributeException extends JspTagException {
    AttributeException(String s) { super(s); }
}

/**
 * Cache which relates unparsed Attribute Strings with parsed `Attribute' objects.
 */
class AttributeCache extends Cache {
    AttributeCache() {
        super(1000);
    }
    public String getName()        { return "TagAttributeCache"; }
    public String getDescription() { return "Cache for parsed Tag Attributes"; }
    public Attribute getAttribute(Object att) throws AttributeException {
        Attribute res;
        res = (Attribute) super.get(att);
        if (res == null) res = new Attribute(att);
        super.put(att, res);
        return res;
    }
        
}

public class Attribute {
    private static Logger log = Logging.getLoggerInstance(Attribute.class.getName());
    private static AttributeCache cache = new AttributeCache();

    public final static Attribute NULL = new NullAttribute();

    static {
        // put the cache in the mmbase cache repository (making it configurable)
        cache.putCache();
    }


    /**
     * This is the function for public use. It takes the string and returns an Attribute, creating
     * a new one if it is not in the Attribute cache.
     */
    public static Attribute getAttribute(Object at) throws AttributeException {
        return cache.getAttribute(at);
    }

    /**
     * Whether the attribute contains any $-vars.
     */
    private boolean containsVars;   
    /**
     * The unparsed attribute.
     */
    private Object attribute;     
    /**
     * List of AttributeParts (the parsed attribute). This can be null if containsVars is false.
     */
    private List    attributeParts; 

    /** 
     * The constructor is protected, construction is done by the cache.
     */
    protected Attribute(Object at) throws AttributeException {
        attribute    = at;
        parse();
    }
    
    protected Attribute() {}

    /**
     * Concatates every part of the attribute to a string for actual use in the tag implementations.
     * 
     * @param ContextReferrerTag The tag relative to which the variable evalutations must be done
     *                           (normally 'this')
     */

    public void appendValue(ContextReferrerTag tag, StringBuffer buffer) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("Appending " + attribute);
        }
        if (! containsVars) buffer.append(attribute.toString());
        Iterator i = attributeParts.iterator();
        while (i.hasNext()) {
            AttributePart ap = (AttributePart) i.next();
            ap.appendValue(tag, buffer);
        }
    }
    public Object getValue(ContextReferrerTag tag) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("Evaluating " + attribute);
        }
        if (! containsVars) return attribute;
        if (attributeParts.size() == 1) {
            AttributePart ap = (AttributePart) attributeParts.get(0);
            return ap.getValue(tag);
        }
        StringBuffer result = new StringBuffer();
        appendValue(tag, result);
        return result.toString();
    }


    public String getString(ContextReferrerTag tag) throws JspTagException {
        return (String) getValue(tag);
    }
    /**
     * Returns the unparsed Attribute as a string.
     */
    public String toString() {
        return attribute.toString();
    }

    /**
     * Parses this attribute into list of 'attributeparts'. toString will concatate then together again.
     */

    protected void parse() throws AttributeException {
        if (attribute == null) {
            containsVars = false;
            return;
        }
        String attr = (String) attribute;
        if (log.isDebugEnabled()) {
            log.debug("Parsing " + attr);
        }
        // search all occurences of $
        int foundpos     = attr.indexOf('$');
        if (foundpos == -1) { 
            containsVars = false;
            return; // if none, return imediately.
        } else {
            attributeParts = new ArrayList();
            containsVars = true;
        }

        int pos          = 0;
        while (foundpos >= 0) { // we found a variable!
            attributeParts.add(new AttributePart(attr.substring(pos, foundpos))); 
            // piece of string until now is ready.
            foundpos ++;
            if (foundpos >= attr.length()) { // end of string
                // could not happen :-)
                break;
            }

            if (attr.charAt(foundpos) == '{') { // using parentheses
                // find matching closing parenthes
                pos = ++foundpos;
                int opened = 1;
                while (opened > 0) {
                    int posclose = attr.indexOf('}', pos); 
                    int posopen  = attr.indexOf('{', pos);
                    if (posclose == -1) {
                        throw new AttributeException("Unbalanced parentheses in '" + this + "'");
                    }
                    if (posopen > -1 && posopen < posclose) { // another one was opened!
                        opened++;
                        pos = posopen + 1;
                    } else {
                        opened--;
                        pos = posclose + 1;
                    }
                }
                // even variable names can be in a variable, why not...
                Attribute var = getAttribute(attr.substring(foundpos, pos - 1));
                attributeParts.add(new AttributePart(var));
            } else { // not using parentheses.
                char c = attr.charAt(pos = foundpos);
                if (c == '$') { // make escaping of $ possible
                    attributeParts.add(new AttributePart("$"));
                    pos++;
                } else {        // search until non-identifier
                    StringBuffer varName = new StringBuffer(); 
                    while (ContextTag.isContextIdentifierChar(c)) {
                        varName.append(c);
                        pos++;
                        if (pos >= attr.length()) break; // end of string
                        c = attr.charAt(pos);
                    }
                   Attribute var = getAttribute(varName.toString());
                   attributeParts.add(new AttributePart(var));
                }
            }
            // ready with this $, search next occasion;
            foundpos = attr.indexOf('$', pos);
        }
        // no more $'es, add rest of string
        attributeParts.add(new AttributePart(attr.substring(pos)));
        return;
    }

    class AttributePart {
        final static int STRING      = 0;
        final static int VAR         = 1;
        final static int ATTRIBUTE   = 2;
        final static int EXPRESSION  = 3;
        private int type;
        private Object part;
        AttributePart(int t, Object p) throws AttributeException {
            type = t; part = p;
            check();
        }
        AttributePart(Attribute att) throws AttributeException {
            if(att.containsVars) {
                type = ATTRIBUTE;
                part = att;
            } else {
                type = VAR;
                part = att.toString();
            }
            check();
        }
        AttributePart(String att) throws AttributeException {
            this(STRING, att);
        }
        /**
         * After construction some basic checks can be done.
         */
        void check() throws AttributeException {
            if (log.isDebugEnabled()) log.trace("Checking new AttributePart " + part + "/" + type);
            switch(type) {
            case VAR: {
                String var = (String) part;
                if (var.length() < 1) throw new AttributeException("Expression too short in " + attribute);                
            }
            }
        }

        Object getValue(ContextReferrerTag tag) throws JspTagException {
            switch(type) {
            case STRING: return (String) part;
            case VAR:{
                if ("_".equals(part)) {
                    return tag.findWriter().getWriterValue();
                } else {
                    return tag.getObject((String) part);
                }
            }
            case ATTRIBUTE:   return ((Attribute) part).getValue(tag);
            case EXPRESSION: { 
                //ExprCalc cl = new ExprCalc((Attribute) part.toString(tag));
                //return cl.getResult();
                return "UNSUPPORTED";
            }
            default: throw new AttributeException("Found an unknown Attribute Part type");
            }
        }


        void appendValue(ContextReferrerTag tag, StringBuffer buffer) throws JspTagException {            
            /*
              // dropped aritmetic for the moment. Will be fixed.

                } else if (varName.charAt(0) == '+') { // make simple aritmetic possible
                    ExprCalc cl = new ExprCalc(varName.substring(1));
                    result.append(cl.getResult());
                } else {
                    result.append(getString(varName));
                }
            */
            if (log.isDebugEnabled()) {
                log.trace("Appending part of " + part + " type " + type);
            }
            switch(type) {
            case STRING:
            case VAR:
            case EXPRESSION:
                buffer.append(getValue(tag).toString()); 
                return;
            case ATTRIBUTE:   ((Attribute) part).appendValue(tag, buffer); return;
            default: throw new AttributeException("Found an unknown Attribute Part type");
            }

        }
    }
}

/**
 * The attribute containing 'null' is special. No parsing needed, nothing needed.
 */
class NullAttribute extends Attribute {
    NullAttribute() { 
    }
    public Object getValue(ContextReferrerTag tag) throws JspTagException { return null; }
    public void appendValue(ContextReferrerTag tag, StringBuffer buffer) throws JspTagException { return; }
    public String toString() { return ""; }
}
