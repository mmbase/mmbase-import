/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* The importtag puts things in the context. It can find them from the
* environment or from its body.
* 
* @author Michiel Meeuwissen 
*/

public class ImportTag extends WriteTag {

    private static Logger log = Logging.getLoggerInstance(ImportTag.class.getName()); 

    protected boolean required     = false;
    //protected String  defaultValue = null;
    protected int     from         = ContextTag.TYPE_NOTSET;

    protected String externid      = null;
    
    private   Object value = null;


    /**
     * Release all allocated resources.
     */
    public void release() {   
        log.debug("releasing" );
        super.release();       
        externid = null;
        value = null;
        id = null;
    }

    /**
     * The extern id it the identifier in some external source.
     */

    public void setExternid(String e) throws JspTagException {
        externid = getAttributeValue(e);
    }

    /**
     * If 'required' then the variable must be available in the
     * external source, otherwise exception.
     * 
     */
    public void setRequired(boolean b) {
        required = b;
    }

    /*
    public void setDefault(String d) {
        defaultValue = d;
    }
    */

    /**
     * From which external source
     */

    public void setFrom(String s) throws JspTagException {
        if ("parent".equalsIgnoreCase(s)) {
            from = ContextTag.TYPE_PARENT;
        } else if ("page".equalsIgnoreCase(s)) {
            from = ContextTag.TYPE_PAGE;
        } else if ("session".equalsIgnoreCase(s)) {
            from = ContextTag.TYPE_SESSION;
        } else if ("parameters".equalsIgnoreCase(s)) {
            from = ContextTag.TYPE_PARAMETERS;
        } else if ("postparameters".equalsIgnoreCase(s)) { // backward compatible
            from = ContextTag.TYPE_MULTIPART;
        } else if ("multipart".equalsIgnoreCase(s)) {
            from = ContextTag.TYPE_MULTIPART;
        } else {
            throw new JspTagException("Unknown context-type " + s);
        }
    }

    public int doStartTag() throws JspTagException {
        
        return EVAL_BODY_TAG;
    }

    /**
     * The body of the import tag can contain a (default) value for the context variable to be created.
     *
     */

    private Object getFromBodyContent() throws JspTagException {
        Object res;
        log.debug("type: " + type);
        
        if (type == null || "Object".equalsIgnoreCase(type) || "String".equalsIgnoreCase(type)) {
            res = bodyContent.getString();
        } else if ("Node".equalsIgnoreCase(type)) {
            throw new JspTagException("Type Node not (yet) supported for this Tag");
        } else if ("Integer".equalsIgnoreCase(type)) {
            log.debug("integer");
            res = new Integer(bodyContent.getString());
        } else if ("Vector".equalsIgnoreCase(type)) {
            String bod = bodyContent.getString();
            if (! "".equals(bod)) {
                res = stringSplitter(bod);
            } else { res = ""; }
        } else {
            throw new JspTagException("Unknown type '" + type + "'");
        }
        return res;
    }

    public int doAfterBody() throws JspTagException{        
        value = null;
        
        if (externid != null) {
            log.trace("Externid was given " + externid);
            if (id == null) {
                log.trace("No id was given, using externid ");
                id = externid;                    
            } else {
                log.trace("An id was given (" + id + ")");
            }
                    
            boolean found;
            if (from == ContextTag.TYPE_NOTSET) {
                found = getContextTag().findAndRegister(externid, id);
            } else {
                found = getContextTag().findAndRegister(from, externid, id);
            }

            if (! found) {
                log.debug("External Id " + externid + " not found");
                // try to find a default value in the body.
                Object body = getFromBodyContent();
                if (! "".equals(body)) { // hey, there is a body content!
                    log.debug("Found a default in the body (" + body + ")");
                    getContextTag().unRegister(id); // first unregister the empty value;
                    getContextTag().register(id, body);
                    found = true;
                }                
            }
            if (! found && required) {
                throw new JspTagException("Required parameter '" + externid + "' not found");
            } 
            if (found) {
                value = getContextTag().getObject(id);
                log.debug("found value for " + id + " " + value);
            }
        } else { // get value from the body of the tag.
            if (id == null) {
                throw new JspTagException("Attributes referid and id cannot be both missing");
            }
            value = getFromBodyContent();
            log.debug("Setting " + id + " to " + value);
            getContextTag().register(id, value);            
        }

        return SKIP_BODY;
    }

    public int doEndTag() throws JspTagException {
        if (value != null && jspvar != null) {
            log.debug("Setting variable " + jspvar + " to " + value);
            pageContext.setAttribute(jspvar, value);
        }
        id = null;
        return EVAL_PAGE;
    }

}
