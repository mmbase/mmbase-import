/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;


import org.mmbase.bridge.jsp.taglib.util.StringSplitter;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;



/**
* The importtag puts things in the context. It can find them from the
* environment or from its body.
* 
* @author Michiel Meeuwissen 
* @see    ContextTag
*/

public class ImportTag extends WriteTag {

    private static Logger log = Logging.getLoggerInstance(ImportTag.class.getName()); 

    protected boolean required     = false;
    //protected String  defaultValue = null;
    protected int     from         = ContextTag.LOCATION_NOTSET;

    protected String externid      = null;

    private boolean found = false;
    

    /**
     * Release all allocated resources.
     */
    public void release() {   
        log.debug("releasing" );
        super.release();       
        externid = null;
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
        from = ContextTag.stringToLocation(getAttributeValue(s));
    }

    public int doStartTag() throws JspTagException {
        Object value = null;
        log.trace("dostarttag of import");
        if (externid != null) {
            log.trace("Externid was given " + externid);
            if (id == null) {
                log.trace("No id was given, using externid ");
                id = externid;                    
            } else {
                log.trace("An id was given (" + id + ")");
            }
                    
            if (from == ContextTag.LOCATION_NOTSET) {
                found = (getContextTag().findAndRegister(externid, id) != null);
            } else {
                found = (getContextTag().findAndRegister(from, externid, id) != null);
            }

            if (! found && required) {
                throw new JspTagException("Required parameter '" + externid + "' not found in " + ContextTag.locationToString(from));
            } 
            if (found) {
                value = getObject(id);
                if (log.isDebugEnabled()) {
                    log.debug("found value for " + id + " " + value);
                }
            }
        } 
        if (found) {
            helper.setValue(value);
            return SKIP_BODY;        
        } else {
            return EVAL_BODY_TAG;
        }

    }

    /**
     * The body of the import tag can contain a (default) value for the context variable to be created.
     *
     */

    private Object getFromBodyContent() throws JspTagException {
        Object res;
        switch(helper.getVartype()) {
        case WriterHelper.TYPE_NODE:
            throw new JspTagException("Type Node not (yet) supported for this Tag");
        case WriterHelper.TYPE_DECIMAL:
            res = new java.math.BigDecimal(bodyContent.getString());
            break;
        case WriterHelper.TYPE_INTEGER:
            res = new Integer(bodyContent.getString());
            break;
        case WriterHelper.TYPE_FLOAT:
            res = new Float(bodyContent.getString());
            break;
        case WriterHelper.TYPE_LONG:
            res = new Long(bodyContent.getString());
            break;
        case WriterHelper.TYPE_DOUBLE:
            res = new Double(bodyContent.getString());
            break;
        case WriterHelper.TYPE_VECTOR:
        case WriterHelper.TYPE_LIST:
            String bod = bodyContent.getString();
            if (! "".equals(bod)) {
                res = StringSplitter.split(bod);
            } else { res = ""; }
            break;
        default:
            res = bodyContent.getString();
        }
        return res;
    }

    public int doAfterBody() throws JspTagException {  
        Object value;
        if (externid != null) {
            if (! found ) {
                if (log.isDebugEnabled()) log.debug("External Id " + externid + " not found");
                // try to find a default value in the body.
                Object body = getFromBodyContent();
                if (! "".equals(body)) { // hey, there is a body content!
                    if (log.isDebugEnabled()) {
                        log.debug("Found a default in the body (" + body + ")");
                    }
                    getContextTag().unRegister(id); // first unregister the empty value;
                    getContextTag().register(id, body);
                    value = body;
                    helper.setValue(value);
                    found = true;
                }                
            }
        } else { // get value from the body of the tag.
            if (id == null) {
                throw new JspTagException("Attributes referid and id cannot be both missing");
            }
            value = getFromBodyContent();
            helper.setValue(value);
            if (log.isDebugEnabled()) {
                log.debug("Setting " + id + " to " + value);
            }
            getContextTag().register(id, value);                        
        }
        found = false; // for use next time
        return SKIP_BODY;
    }

    public int doEndTag() throws JspTagException {
        helper.setJspvar(pageContext);
        id = null;
        return EVAL_PAGE;
    }

}
