/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.util.Vector;
import java.util.StringTokenizer;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.LocalContext;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * If you want to have attributes which obtain the value from a
 * parameter, extend from this.
 * It also contains a few other 'utily' functions, which can be handy when constructing tags.
 *
 * @author Michiel Meeuwissen
 */

public abstract class ContextReferrerTag extends BodyTagSupport {

    private static Logger log = Logging.getLoggerInstance(ContextReferrerTag.class.getName());

    private ContextTag pageContextTag = null;

    private String     contextId = null;
    protected String   referid = null;


    public void setPageContext(PageContext pc) {
        super.setPageContext(pc);
        if (pageContextTag == null) {
            pageContextTag=(ContextTag)pc.getAttribute("context");
        }
        if (pageContextTag == null) {
            log.debug("making the pageContextTag.");
            pageContextTag = new ContextTag();
            pc.setAttribute("context",pageContextTag);
            pageContextTag.setPageContext(pc);
            pageContextTag.setId("context");
            // there is one implicit ContextTag in every page.
            // it's id is 'context'.
            // it is called pageContext, because it is similar to the pageContext, but not the same.
        }
    }

    public void setReferid(String r) throws JspTagException {
        referid = getReferIdValue(r);
    }

    protected String getReferid() throws JspTagException {
        return referid;
    }

    /**
     * Release all allocated resources.
     */
    public void release() {
        super.release();
        id = null;
        referid = null;
        contextId = null;
        pageContextTag = null;
    }


    /**
     * Refer to a specific context.
     */

    public void setContext(String c) {
        log.debug("setting contextid to " + c);
        //contextTag = null;
        contextId = c;
    }

    protected String getReferIdValue(String attribute) {
        // also possible to indicate context in id:
        StringTokenizer tk = new StringTokenizer(attribute,".");
        attribute = tk.nextToken();
        if (tk.hasMoreTokens()) {
            setContext(attribute);
            attribute = tk.nextToken();
        }
        return attribute;
    }

    /**
     * Call this function in your set-attribute function. It makes it
     * possible for the user of the taglib to include ids of values stored in
     * the context.
     * The method replaces all occurrences of ${x}, where x is a reference to
     * a attribute value, possibly prefixed with a context name.
     */
    protected String getAttributeValue(String attribute) throws JspTagException {
        String result="";
        int beginpos = attribute.indexOf("${");
        int endpos=0;
        while (beginpos>=0) {
            result+=attribute.substring(endpos,beginpos);
            endpos = attribute.indexOf("}",beginpos);
            if (endpos>=0) {
                String varname=attribute.substring(beginpos+2,endpos);
                StringTokenizer tk = new StringTokenizer(varname,".");
                String context=contextId;
                String varid = tk.nextToken();
                if (tk.hasMoreTokens()) {
                    context = varid;
                    varid = tk.nextToken();
                }
                ContextTag ct = getContextTag(context);
                String varValue = ct.getObjectAsString(varid);
                if (varValue == null) {
                    throw new JspTagException(varid + " could not be found in " + context);
                }
                result+=varValue;
            }
            endpos+=1;
            beginpos = attribute.indexOf("${",endpos);
        }
        result+=attribute.substring(endpos);
        return result;
    }

    /**
     * Finds a parent tag by class and id.
     *
     * @param classname the classname of the Tag to find.
     * @param id        the id of the Tag to find.
     * @param exception if it has to throw an exception if the parent can not be found (default: yes).
     */

    final protected TagSupport findParentTag(String classname, String id, boolean exception) throws JspTagException {
        log.debug("finding " + classname);
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
     */

    protected ContextTag getContextTag() throws JspTagException {
        return getContextTag(contextId);
    }

    protected ContextTag getContextTag(String contextid) throws JspTagException {
        log.debug("searching in context " + contextid);
        //if (contextTag == null) {
        log.debug("searching context " + contextid);
        ContextTag contextTag = (ContextTag) findParentTag("org.mmbase.bridge.jsp.taglib.ContextTag", contextid, false);
        if (contextTag == null) {
            contextTag = pageContextTag;
            if (contextid != null) {
                if(! contextTag.getId().equals(contextid)) {
                    throw new JspTagException("Could not find contex tag with id " + contextid + " (page context has id " + contextTag.getId() + ")");
                }
            }
        }
        log.debug("found a context with ID= " + contextTag.getId());
        //}
        return contextTag;
    }


    // --------------------------------------------------------------------------------
    // utils

    /**
    * Simple util method to split comma separated values
    * to a vector. Usefull for attributes.
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
