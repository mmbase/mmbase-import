/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.Node;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.servlet.http.HttpServletRequest;

/**
 * If you want to have attributes which obtain the value from a
 * parameter or other context variable, or if you want to be able to
 * refer to other tags, then your tag can extend from this one.
 *
 *
 * @author Michiel Meeuwissen 
 * @see    ContextTag
 */

public abstract class ContextReferrerTag extends BodyTagSupport {

    private static Logger log = Logging.getLoggerInstance(ContextReferrerTag.class.getName());

    public final static String PAGE_CATEGORY = "org.mmbase.PAGE";      // the category for info about the page (stop / start)
    private static Logger pageLog = Logging.getLoggerInstance(PAGE_CATEGORY);

    protected ContextTag pageContextTag = null;

    protected  Attribute  contextId = Attribute.NULL; // context to which this tagg is referring to.
    protected  Attribute  referid=   Attribute.NULL;

    private String       thisPage = null;

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
            log.debug("No pageContextTag found in pagecontext, creating..");
            if (pageLog.isServiceEnabled()) {
                thisPage = ((HttpServletRequest)pageContext.getRequest()).getRequestURI();
                String queryString = ((HttpServletRequest)pageContext.getRequest()).getQueryString();
                pageLog.service("Parsing JSP page: " + thisPage + (queryString != null ? "?" + queryString : ""));
            }
            pageContextTag = new ContextTag();
            pageContextTag.setId(null);

            // page context has no id, this also avoids that it tries
            // registering itself in the parent (which it is itself)
            // so don't change this!

            pageContextTag.setPageContextOnly(pageContext);

            // set the pageContextTag, before fillVars otherwise the page is not set in the fillVars
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
        referid = getAttribute(r);
    }

    protected String getReferid() throws JspTagException {
        return (String) referid.getValue(this);
    }

    /*
     * The writerreferrer functionality is here, though not every conterreferrer is a writerreferrer.
     * So contextreferrer does not implement Writerreferrer. It is enough tough to 'implement WriterReferrer' 
     * for every ContextReferrer to become a real WriterReferrer;
     */
    /**
     * Which writer to use. 
     */
    protected String writerid = null;
    
    /**
     * Find the parent writer tag.
     */
    public Writer findWriter() throws JspTagException {
        Writer w;        
        w = (Writer) findParentTag("org.mmbase.bridge.jsp.taglib.Writer", writerid);
        w.haveBody();
        return w;
    }
    /**
     * Sets the writer attribute.
     */
    public void setWriter(String w) throws JspTagException {
        writerid = getAttributeValue(w);
        
    }

    

    /**
     * Release all allocated resources.
     */
    public void release() {        
        super.release();
        if (log.isDebugEnabled()) {
            log.debug("releasing context-referrer " + this.getClass().getName());
        }
        if (thisPage != null) {
            pageLog.debug("END Parsing JSP page: " + thisPage);
            thisPage = null;
        }
        id = null;
        referid   = Attribute.NULL;
        contextId = Attribute.NULL;
        pageContextTag = null;
    }


    /**
     * Refer to a specific context. With this you can refer to, and
     * write in, another context then the direct parent (but is must
     * be an ancestor).  This is for analogy with other attributes
     * like this.
     * 
     */

    public void setContext(String c) throws JspTagException {
        contextId = getAttribute(c);
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
     * @deprecated Call getAttribute in the set-method and 'toString(tag)' when using
     *             it. This is better for perfomrnace and makes sure the impl. works in all servlet
     *             containers. 
     */

    public String getAttributeValue(String attribute) throws JspTagException {        
        if (attribute == null) return null;
        return getAttribute(attribute).getString(this);        
    }
    /**
     * @since MMBase-1.6.1
     */
    public Attribute getAttribute(String attribute) throws JspTagException {
        return Attribute.getAttribute(attribute);
    }

    /**
     * Like getAttributeValue but converts the result to a Boolean,
     * and throws an exception if this cannot be done.
     **/

    protected Boolean getAttributeBoolean(String b) throws JspTagException {
        String r = getAttributeValue(b).toLowerCase();
        if ("true".equals(r)) {
            return Boolean.TRUE;
        } else if ("false".equals(r)) {
            return Boolean.FALSE;
        } else {
            throw new JspTagException("'" + r + "' cannot be converted to a boolean");
        }
    }
    /**
     * Like getAttributeValue but converts the result to an Integer,
     * and throws an exception if this cannot be done. It the incoming string evaluates to an empty string, then 
     * it will return 0, unless the second optional parameter specifies another default value;
     **/

    protected Integer getAttributeInteger(String i) throws JspTagException {
        return getAttributeInteger(i, 0);
    }
    protected Integer getAttributeInteger(String i, int def) throws JspTagException {
        try {
            i = getAttributeValue(i);
            if (i.equals("")) return new Integer(def);
            return new Integer(i);
        } catch (NumberFormatException e) { // try first if it was a float
            try {
                return new Integer(new java.math.BigDecimal(i).intValue());
            } catch (NumberFormatException e2) {
                throw new JspTagException(i + " is not an integer value ");
            }
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

    public ContextTag getContextTag() throws JspTagException {
        return getContextTag(contextId.getString(this));
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

    public Object getObject(String key) throws JspTagException {
        // does the key contain '.', then start searching on pageContextTag, otherwise in parent.
        if (log.isDebugEnabled()) { 
            log.debug("Getting object '" + key + "' from '" + getContextTag().getId() + "'");
        }
        Object r = getContextTag().getContainerObject(key);
        if (r == null) { 
            log.debug("Not found, returning empty string");
            return "";
        } else {
            if (log.isDebugEnabled()) log.debug("found: '" + r + "'");
        }
        return r;
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
        if (o instanceof Node) {
            Node n = (Node) o;
            return "" + n.getNumber();
        }
        return o.toString();
    }

}
