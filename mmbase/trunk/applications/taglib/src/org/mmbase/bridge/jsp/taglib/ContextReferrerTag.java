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

    private ContextTag contextTag;
    private String contextId = null;

    /**
     * Refer to a specific context.
     */

    public void setContext(String c) {
        contextId = c;
    }
    
    /**
     * Call this function in your set-attribute function. It makes it
     * possible for the user of the taglib to prefix the attribute
     * value with things like 'session:', 'param:' and of course with
     * 'context:'.
     *
     */

    protected String getAttributeValue(String attribute) throws JspTagException {
        String attributeValue = attribute;
        if (attribute.startsWith("param:")) {        // interpret as parameter
            String param = attribute.substring(6);
            attributeValue = pageContext.getRequest().getParameter(param);
            if (attributeValue == null) {
                throw new JspTagException("Parameter " + param + " could not be found");
            }
        } else if (attribute.startsWith("session:")){ // interpret as key from session
            String param = attribute.substring(8);
            javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();
            attributeValue = (String) req.getSession().getAttribute(param);
            if (attributeValue == null) {
                throw new JspTagException("Session attribute " + param + " could not be found");
            }
        } else if (attribute.startsWith("context:")) { // interpret as key from context
            String param = attribute.substring(8);
            attributeValue = getContextTag().getString(param);
            if (attributeValue == null) {
                throw new JspTagException("Context attribute " + param + " could not be found");
            }
        } else if (attribute.startsWith("key:")) {    // general, as a key, so from context.
            String param = attribute.substring(4);
            attributeValue = getContextTag().getString(param);
            if (attributeValue == null) {
                throw new JspTagException("Context attribute " + param + " could not be found");
            }
        }

        return attributeValue;        
    }

    /**
     * Finds a parent tag by class and id.
     *
     * @param classname the classname of the Tag to find.
     * @param id        the id of the Tag to find.
     */
   
    protected TagSupport findParentTag(String classname, String id) throws JspTagException {
        
        Class clazz ;
        try {
            clazz = Class.forName(classname);
        } catch (java.lang.ClassNotFoundException e) {
            throw new JspTagException ("Could not find " + classname + " class");  
        }

        TagSupport cTag = (TagSupport) findAncestorWithClass((Tag)this, clazz); 
        if (cTag == null) {
            throw new JspTagException ("Could not find parent");  
        }
        
        if (id != null) { // search further, if necessary
            while (cTag.getId() != id) {
                cTag = (TagSupport) findAncestorWithClass((Tag)cTag, clazz);
                if (cTag == null) {
                    throw new JspTagException ("Could not find parent Tag with id " + id);  
                }
            }
            
        }
        return cTag;

    }

    /**
     * Finds the parent context tag.
     */

    protected ContextTag getContextTag() throws JspTagException {
        if (contextTag == null) {
            contextTag = (ContextTag) findParentTag("org.mmbase.bridge.jsp.taglib.ContextTag", contextId);
        }
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
