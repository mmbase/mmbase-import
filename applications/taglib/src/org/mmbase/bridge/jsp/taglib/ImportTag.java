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
    protected int     from         = ContextTag.LOCATION_NOTSET;

    protected String  externid      = null;

    private   boolean found = false;
    private   boolean reset = false;
    private   String  useId = null;


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


    /**
     * If 'required' then the variable must be available in the
     * external source, otherwise exception.
     *
     */
    public void setReset(boolean b) {
        reset = b;
    }

    /**
     * From which external source
     */

    public void setFrom(String s) throws JspTagException {
        from = ContextTag.stringToLocation(getAttributeValue(s));
    }

    public int doStartTag() throws JspTagException {
        Object value = null;
        log.trace("dostarttag of import");
        if (getId() == null) {
            log.trace("No id was given, using externid ");
            useId = externid;
        } else {
            useId = id;
            if (log.isDebugEnabled()) log.trace("An id was given (" + id + ")");
        }
        if (reset) { // should this be more general? Also in other contextwriters?
            if (log.isDebugEnabled()) log.trace("Resetting variable " + useId);
            getContextTag().unRegister(useId);
        }

        if (externid != null) {            
            log.trace("Externid was given " + externid);
            if (from == ContextTag.LOCATION_NOTSET) {
                found = (getContextTag().findAndRegister(externid, useId) != null);
            } else {
                found = (getContextTag().findAndRegister(from, externid, useId) != null);
            }

            if (! found && required) {
                throw new JspTagException("Required parameter '" + externid + "' not found in " + ContextTag.locationToString(from));
            }
            if (found) {
                value = getObject(useId);
                if (log.isDebugEnabled()) {
                    log.debug("found value for " + useId + " " + value);
                }
            }
        }
        if (found) {
            helper.setValue(value);
            if (useId != null) {
                getContextTag().reregister(useId, helper.getValue());
            }
            return SKIP_BODY;
        } else {
            helper.setValue(null);
            return EVAL_BODY_BUFFERED;
        }

    }

    public int doEndTag() throws JspTagException {
        if (log.isDebugEnabled()) log.debug("endtag of import with id:" + id + " externid: " + externid);
        if (externid != null) {
            if (! found ) {
                if (log.isDebugEnabled()) log.debug("External Id " + externid + " not found");
                // try to find a default value in the body.
                Object body = bodyContent != null ? bodyContent.getString() : "";
                if (! "".equals(body)) { // hey, there is a body content!
                    if (log.isDebugEnabled()) {
                        log.debug("Found a default in the body (" + body + ")");
                    }
                    helper.setValue(body);
                    getContextTag().reregister(useId, helper.getValue());
                }
            }
        } else { // get value from the body of the tag.
            helper.setValue(bodyContent != null ? bodyContent.getString() : "");
            if (useId != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Setting " + useId + " to " + helper.getValue());
                }
                getContextTag().register(useId, helper.getValue());
            } else {
                if (helper.getJspvar() == null) {
                    found = false; // for use next time
                    useId = null;
                    throw new JspTagException("Attributes externid, id and jspvar cannot be all missing");
                }
            }
        }
        helper.setJspvar(pageContext);
        found = false; // for use next time
        useId = null;
        log.debug("end of importag");
        return EVAL_PAGE;
    }


}
