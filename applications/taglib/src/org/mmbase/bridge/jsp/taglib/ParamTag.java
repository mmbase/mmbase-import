/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.Entry;
import javax.servlet.jsp.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Adds an extra parameter to the parent URL tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ParamTag.java,v 1.13 2006-07-17 15:38:47 johannes Exp $
 */

public class ParamTag extends ContextReferrerTag implements ParamHandler {
    private static final Logger log = Logging.getLoggerInstance(ParamTag.class);

    protected List       entries      = null;

    private Attribute name    = Attribute.NULL;
    private Attribute value   = Attribute.NULL;
    private Attribute referid = Attribute.NULL;
    private ParamHandler paramHandler;
    private boolean handled;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }
    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }
    /**
     * @since MMBase-1.8
     */
    public void setReferid(String r) throws JspTagException {
        referid = getAttribute(r);
    }

    public void addParameter(String key, Object value) throws JspTagException {
        if (entries == null) entries = new ArrayList();
        entries.add(new Entry(key, value));
        if (log.isDebugEnabled()) {
            log.debug("entries " + entries);
        }
    }

    public int doStartTag() throws JspException {
        findWriter(false); // just to call haveBody, mainly for mm:link.
        paramHandler = (ParamHandler) findParentTag(ParamHandler.class, null);
        handled = false;
        return super.doStartTag();
    }

    public int doAfterBody() throws JspException {
        if (value == Attribute.NULL && referid == Attribute.NULL && entries == null) {
            if (bodyContent != null) {
                // the value is the body context.
                helper.setValueOnly(bodyContent.getString(), WriterHelper.IMPLICITLIST); // to deal with 'vartype' casting
                paramHandler.addParameter(name.getString(this), helper.getValue());
                handled = true;
            }
        }
        return super.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        if (! handled) {
            if (value != Attribute.NULL) {
                if (referid != Attribute.NULL || entries != null) throw new JspTagException("Must specify either 'value', 'referid' or sub-param-tags, not both");
                helper.setValueOnly(value.getString(this), WriterHelper.IMPLICITLIST); // to deal with 'vartype' casting
                paramHandler.addParameter(name.getString(this), helper.getValue());

            } else if (referid != Attribute.NULL) {
                if (entries != null) throw new JspTagException("Must specify either 'value', 'referid' or sub-param-tags, not both");
                paramHandler.addParameter(name.getString(this), getObject(referid.getString(this)));
            } else if (entries != null) {
                paramHandler.addParameter(name.getString(this), entries);
                entries = null;
            } else {
                paramHandler.addParameter(name.getString(this), "");
            }
        }
        paramHandler = null;
        return super.doEndTag();
    }

    public void doFinally() {
        paramHandler = null;
        entries = null;
        super.doFinally();
    }
}
