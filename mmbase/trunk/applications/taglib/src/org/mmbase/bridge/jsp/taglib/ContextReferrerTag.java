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
     * The method replaces all occurrences of ${x}, where x is a reference to
     * a attribute value, possibly prefixed with context names.
     *
     */
    protected String getAttributeValue(String attribute) throws JspTagException {
        String result = "";
        int beginpos  = attribute.indexOf("${");
        int endpos    = 0;
        while (beginpos >= 0) {
            result += attribute.substring(endpos, beginpos);
            endpos = attribute.indexOf("}", beginpos);
            if (endpos>=0) {
                String varName  = attribute.substring(beginpos + 2, endpos);
                String varValue = getString(varName);
                if (varValue == null) { 
                    // This means that the variable was registered, but is not 'present'.
                    varValue = ""; // don't whine too much.
                }
                result += varValue;
            }
            endpos += 1;
            beginpos = attribute.indexOf("${", endpos);
        }
        result += attribute.substring(endpos);
        return result;
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
        log.debug("Finding a tag " + classname);
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
            log.debug(" with id ("  + id + ")");
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
     */

    protected String getString(String key) throws JspTagException {
        Object o = getObject(key);
        if (o == null) return null;
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
