/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.*;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.Iterator;

/**
 * The importtag puts things in the context. It can find them from the
 * environment or from its body.
 *
 * @author Michiel Meeuwissen
 * @see    ContextTag
 * @version $Id: ImportTag.java,v 1.40 2003-11-19 16:57:42 michiel Exp $
 */

public class ImportTag extends ContextReferrerTag {
    private static final Logger log = Logging.getLoggerInstance(ImportTag.class);

    protected Attribute required = Attribute.NULL;
    protected Attribute from     = Attribute.NULL;

    protected Attribute externid = Attribute.NULL;
    private   Attribute reset    = Attribute.NULL;

    private   boolean found = false;
    private   String  useId = null;

    /**
     * The extern id it the identifier in some external source.
     */

    public void setExternid(String e) throws JspTagException {
        externid = getAttribute(e);
    }

    /**
     * If 'required' then the variable must be available in the
     * external source, otherwise exception.
     *
     */
    public void setRequired(String b) throws JspTagException {
        required = getAttribute(b);
    }


    /**
     * If 'required' then the variable must be available in the
     * external source, otherwise exception.
     *
     */
    public void setReset(String b) throws JspTagException {
        reset = getAttribute(b);
    }

    /**
     * From which external source
     */

    public void setFrom(String s) throws JspTagException {
        from = getAttribute(s);
    }


    public int doStartTag() throws JspTagException {
        Object value = null;
        log.trace("dostarttag of import");
        
        if (getId() == null) {
            log.trace("No id was given, using externid ");
            useId = (String) externid.getValue(this);
        } else {
            useId = getId();
            if (log.isDebugEnabled()) log.trace("An id was given (" + id + ")");
        }

        
        if (externid != Attribute.NULL) {

            boolean res = reset.getBoolean(this, false);
            if (log.isDebugEnabled()) { 
                log.trace("Externid was given " + externid.getString(this));
            }
            if (from.getString(this).equals("")) {
                found = (getContextProvider().getContextContainer().findAndRegister(pageContext, externid.getString(this), useId, ! res) != null);
            } else {
                Iterator froms = from.getList(this).iterator();
                ContextContainer cc = getContextProvider().getContextContainer();
                while (froms.hasNext() && ! found) {
                    int from = ContextContainer.stringToLocation((String) froms.next());
                    found = (cc.findAndRegister(pageContext, from, externid.getString(this), useId, !res) != null);
                    if (!found && froms.hasNext()) {
                        cc.unRegister(useId);                        
                    }
                }
            }

            if (! found && required.getBoolean(this, false)) {
                throw new JspTagException("Required parameter '" + externid.getString(this) + "' not found in " + from.getString(this));
            }
            if (found) {
                value = getObject(useId);
                if (log.isDebugEnabled()) {
                    log.debug("found value for " + useId + " " + value);
                }
            }
        }
        if (found) {
            helper.setValue(value, WriterHelper.NOIMPLICITLIST);
            if (useId != null) {
                getContextProvider().getContextContainer().reregister(useId, getValue());
            }
            return SKIP_BODY;
        } else {
            helper.setValue(null);
            return EVAL_BODY_BUFFERED;
        }

    }

    /**
     * Retrieves the value from the writer-helper, but escapes if necessary (using 'escape' attribute)
     * @since MMBase-1.7
     */
    protected Object getValue() throws JspTagException {
        Object value = helper.getValue();
        if (helper.getEscape() != null) {
            CharTransformer escaper  = ContentTag.getCharTransformer(helper.getEscape());
            value = escaper.transform((String) value);
        }
        return value;
    }

    public int doEndTag() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("endtag of import with id:" + id + " externid: " + externid.getString(this));
        }
        if (externid != Attribute.NULL) {
            if (! found ) {
                if (log.isDebugEnabled()) log.debug("External Id " + externid.getString(this) + " not found");
                // try to find a default value in the body.
                Object body = bodyContent != null ? bodyContent.getString() : "";
                if (! "".equals(body)) { // hey, there is a body content!
                    if (log.isDebugEnabled()) {
                        log.debug("Found a default in the body (" + body + ")");
                    }
                    helper.setValue(body);
                    getContextProvider().getContextContainer().reregister(useId, getValue());
                }  else {
                    //  might be vartype="list" or so, still need to set
                    helper.setValue(null);
                    getContextProvider().getContextContainer().reregister(useId, getValue());
                }
            }
        } else { // get value from the body of the tag.
            helper.setValue(bodyContent != null ? bodyContent.getString() : "");
            if (useId != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Setting " + useId + " to " + helper.getValue());
                }
                boolean res = reset.getBoolean(this, false); // should this be more general? Also in other contextwriters?
                getContextProvider().getContextContainer().register(useId, getValue(), !res);
            } else {
                if (helper.getJspvar() == null) {
                    found = false; // for use next time
                    useId = null;
                    throw new JspTagException("Attributes externid, id and jspvar cannot be all missing");
                }
            }
        }
        found = false; // for use next time
        useId = null;
        bodyContent = null;
        helper.release();
        log.debug("end of importag");
        return EVAL_PAGE;
    }


}
