/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.util.Vector;
import java.util.StringTokenizer;

import org.mmbase.bridge.Node;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;


import org.mmbase.util.ExprCalc;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * If you want to have attributes which obtain the value from a
 * parameter or other context variable, or if you want to be able to
 * refer to other tags, then your tag can extend from this one.
 *
 * It also contains a few other 'utily' functions, which can be handy
 * when constructing tags. 
 *
 * @author Michiel Meeuwissen 
 * @see    ContextTag
 */

public abstract class ContextReferrerTag extends BodyTagSupport {

    private static Logger log = Logging.getLoggerInstance(ContextReferrerTag.class.getName());

    protected ContextTag pageContextTag = null;

    protected String     contextId = null; // context to which this tag is referring to.
    protected String     referid = null;

    void setPageContextOnly(PageContext pc) {
        super.setPageContext(pc);
        // the 'page' Context
    }
  
    public void setPageContext(PageContext pc) {
        if (log.isDebugEnabled()) {
            log.debug("setting page context: " + this.getClass().getName());
        }
        setPageContextOnly(pc); // make pageContext availabe
        pageContextTag = (ContextTag) pageContext.getAttribute("__context");

        if (pageContextTag == null) { // not yet put 
            log.debug("No pageContexTag found in pagecontext, creating..");

            pageContextTag = new ContextTag();
            pageContextTag.setId(null);

            // page context has no id, this also avoids that it tries
            // registering itself in the parent (which it is itself)
            // so don't change this!

            pageContextTag.setPageContextOnly(pageContext);
            //pageContextTag.pageContextTag = pageContextTag;
            pageContextTag.fillVars();
            // register also the tag itself under __context.
            // _must_ set __context before calling setPageContext otherwise in infinite loop. 
            pageContextTag.createContainer(null);
            pageContextTag.pageContextTag = pageContextTag; // the 'parent' of pageContextTag is itself..
            pageContext.setAttribute("__context", pageContextTag);
           
            // there is one implicit ContextTag in every page.
            // its id is null, it is registered in the pageContext as __context.
            // 
            // it is called pageContext, because it is similar to the pageContext, but not the same.
        }  
    }

    /**
     * ContextReferrers normally can have the attribute 'referid'. If
     * a ContextReferrer has the 'id' attribute it registers its
     * output in the surrounding Context.  With 'referid' you can 'repeat' a
     * tag which had the 'id' attribute.
     */

    public void setReferid(String r) throws JspTagException {
        referid = getAttributeValue(r);
    }

    protected String getReferid() throws JspTagException {
        return referid;
    }

    /**
     * Release all allocated resources.
     */
    public void release() {        
        super.release();
        log.debug("releasing context-referrer " + this.getClass().getName());
        id = null;
        referid = null;
        contextId = null;
        pageContextTag = null;
    }


    /**
     * Refer to a specific context. With this you can refer to, and
     * write in, another context then the direct parent (but is must
     * be an ancestor).  This is for analogy with other attributes
     * like this.
     * 
     */

    public void setContext(String c) {
        log.debug("setting contextid to " + c);
        contextId = c;
    }

    /**
     * Call this function in your set-attribute function. It makes it
     * possible for the user of the taglib to include ids of values stored in
     * the context.
     *
     * The method replaces all occurrences of ${x} and $x, where x is a reference to
     * a attribute value, possibly prefixed with context names. The
     * end of the variable if determined by the closing bracket (${x})
     * or by the first non ContextIndentifierChar or end of string ($x).
     *
     * Simple aritmetic is possible with ${+...}, and since you can
     * even use $-vars inside ${}, you can do in this way some
     * arithmetic on variables.
     *
     */

    public String getAttributeValue(String attribute) throws JspTagException {
        String result = "";

        // search all occurences of $
        int foundpos     = attribute.indexOf('$');
        int pos          = 0;
        while (foundpos >= 0) { // we found a variable!
            result += attribute.substring(pos, foundpos); // piece of string until now is ready.
            foundpos ++;
            if (foundpos >= attribute.length()) { // end of string
                break;
            }

            if (attribute.charAt(foundpos) == '{') { // using parentheses
                // find matching closing parenthes
                pos = ++foundpos;
                int opened = 1;
                while (opened > 0) {
                    log.debug("pos " + pos);
                    int posclose = attribute.indexOf("}", pos); 
                    int posopen  = attribute.indexOf("{", pos);
                    if (posclose == -1) {
                        throw new JspTagException("Unbalanced parentheses in '" + attribute + "'");
                    }
                    if (posopen > -1 && posopen < posclose) { // another one was opened!
                        opened++;
                        pos = posopen + 1;
                    } else {
                        opened--;
                        pos = posclose + 1;
                    }
                }
                String varName = getAttributeValue(attribute.substring(foundpos, pos - 1)); // even variable names can be in a variable, why not...
                if (varName.length() < 1) throw new JspTagException("Expression to short in " + attribute);
                if (varName.charAt(0) == '+') { // make simple aritmetic possible
                    ExprCalc cl = new ExprCalc(varName.substring(1));
                    result += cl.getResult();
                } else {
                    result += getString(varName);
                }
            } else { // not using parentheses.
                String varName = ""; //
                char c = attribute.charAt(pos = foundpos);
                if (c == '$') { // make escaping of $ possible
                    result += c;
                    pos++; 
                } else {        // search until non-identifier
                    while (ContextTag.isContextIdentifierChar(c)) {
                        varName += c;
                        pos++;
                        if (pos >= attribute.length()) break; // end of string
                        c = attribute.charAt(pos);
                    }
                    if (varName.length() < 1) throw new JspTagException("Expression to short in " + attribute);
                    result += getString(varName);
                }
            }
            // ready with this $, search next occasion;
            foundpos = attribute.indexOf("$", pos);
        }
        // no more $'es, add rest of string
        result += attribute.substring(pos);
        return result;
    }



    protected Boolean getAttributeBoolean(String b) throws JspTagException {
        String r = getAttributeValue(b);
        if ("true".equalsIgnoreCase(r)) {
            return Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(r)) {
            return Boolean.FALSE;
        } else {
            throw new JspTagException("'" + r + "' cannot be converted to a boolean");
        }
    }

    protected Integer getAttributeInteger(String i) throws JspTagException {
        try {
            i = getAttributeValue(i);
            return new Integer(i);
        } catch (NumberFormatException e) {
            throw new JspTagException(i + " is not an integer value ");
        }
    }

    /**
     * Finds a parent tag by class and id. This is a base function for
     * 'getContext', but it is handy in itself, so also available for
     * extended classes.
     *
     * @param classname the classname of the Tag to find.
     * @param id        the id of the Tag to find.
     * @param exception if it has to throw an exception if the parent can not be found (default: yes).  */

    final protected TagSupport findParentTag(String classname, String id, boolean exception) throws JspTagException {
        Class clazz ;
        try {
            clazz = Class.forName(classname);
        } catch (java.lang.ClassNotFoundException e) {
            throw new JspTagException ("Could not find " + classname + " class");
        }

        TagSupport cTag = (TagSupport) findAncestorWithClass((Tag)this, clazz);
        if (cTag == null) {
            if (exception) {
                throw new JspTagException ("Could not find parent of type " + classname);
            } else {
                log.debug("Could not find parent of type " + classname);
                return null;
            }
        }

        if (id != null) { // search further, if necessary
            if (log.isDebugEnabled()) {
                log.debug(" with id ("  + id + ")");
            }
            while (! id.equals(cTag.getId())) {
                cTag = (TagSupport) findAncestorWithClass((Tag)cTag, clazz);
                if (cTag == null) {
                    if (exception) {
                        throw new JspTagException ("Could not find parent Tag of type " + classname + " with id " + id);
                    } else {
                        return null;
                    }
                }
            }
        }
        return cTag;

    }
    final protected TagSupport findParentTag(String classname, String id) throws JspTagException {
        return findParentTag(classname, id, true);
    }

    /**
     * Finds the parent context tag.
     *
     */

    protected ContextTag getContextTag() throws JspTagException {
        return getContextTag(contextId);
    }
    /**
     * Finds a parent context tag using an id. 
     *
     */

    private ContextTag getContextTag(String contextid) throws JspTagException {

        if(log.isDebugEnabled()) {
            log.debug("Searching context " + contextid);
        }
        ContextTag contextTag = (ContextTag) findParentTag("org.mmbase.bridge.jsp.taglib.ContextTag", contextid, false);
        if (contextTag == null) {
            log.debug("Didn't find one, take the pageContextTag");
            contextTag = pageContextTag;
            if (contextTag == null) {
                throw new RuntimeException("Did not find pageContextTag!");
            }
            if (contextid != null) {
                if(! contextid.equals(contextTag.getId())) {
                    throw new JspTagException("Could not find contex tag with id " + contextid + " (page context has id " + contextTag.getId() + ")");
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("found a context with ID= " + contextTag.getId());
        }
        return contextTag;
    }

    /**
     * Gets an object from the Context.
     *
     */

    protected Object getObject(String key) throws JspTagException {
        // does the key contain '.', then start searching on pageContextTag, otherwise in parent.
        return getContextTag().getContainerObject(key);
    }
    /**
     * Gets an object from the Context, and returns it as a
     * String. This is not always a simple 'toString'. For example a
     * getString on a Node will return the number, which also uniquely
     * identifies it.
     *
     * If the object is 'not present' then it returns an empty string.
     */

    protected String getString(String key) throws JspTagException {
        Object o = getObject(key);
        if (o == null) return "";
        if (o instanceof Node) {
            Node n = (Node) o;
            return "" + n.getNumber();
        }
        return o.toString();
    }


    // --------------------------------------------------------------------------------
    // utils

    /**
     * Simple util method to split comma separated values
     * to a vector. Useful for attributes.
     * @param string the string to split
     * @param delimiter
     * @return a Vector containing the elements, the elements are also trimed
     */

    static public Vector stringSplitter(String attribute, String delimiter) {
        Vector retval = new Vector();
        StringTokenizer st = new StringTokenizer(attribute, delimiter);
        while(st.hasMoreTokens()){
            retval.addElement(st.nextToken().trim());
        }
        return retval;
    }

    static public Vector stringSplitter(String string) {
        return stringSplitter(string, ",");
    }



}
