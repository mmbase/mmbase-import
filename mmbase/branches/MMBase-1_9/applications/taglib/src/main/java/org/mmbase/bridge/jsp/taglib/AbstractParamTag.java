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
 * @version $Id$
 * @since MMBase-1.9
 */

abstract class AbstractParamTag extends ContextReferrerTag {
    private static final Logger log = Logging.getLoggerInstance(AbstractParamTag.class);

    protected List<Entry<String, Object>>       entries      = null;

    protected Attribute name    = Attribute.NULL;
    protected Attribute value   = Attribute.NULL;
    protected Attribute referid = Attribute.NULL;
    protected boolean handled;

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
        if (entries == null) entries = new ArrayList<Entry<String, Object>>();
        entries.add(new Entry<String, Object>(key, value));
        if (log.isDebugEnabled()) {
            log.debug("entries " + entries);
        }
    }



    public int doStartTag() throws JspException {
        findWriter(false); // just to call haveBody, mainly for mm:link.
        handled = false;
        return super.doStartTag();
    }

    /**
     * @since MMBase-1.9
     */
    abstract protected void addParameter(Object value) throws JspTagException;


    public int doAfterBody() throws JspException {
        if (value == Attribute.NULL && referid == Attribute.NULL && entries == null) {
            if (bodyContent != null) {
                // the value is the body context.
                helper.setValueOnly(bodyContent.getString(), WriterHelper.IMPLICITLIST); // to deal with 'vartype' casting
                addParameter(helper.getValue());
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
                addParameter(helper.getValue());

            } else if (referid != Attribute.NULL) {
                if (entries != null) throw new JspTagException("Must specify either 'value', 'referid' or sub-param-tags, not both");
                if (name == Attribute.NULL) {
                    addParameter(referid.getString(this), getObject(referid.getString(this)));
                } else {
                    addParameter(getObject(referid.getString(this)));
                }
            } else if (entries != null) {
                addParameter(entries);
                entries = null;
            } else {
                addParameter("");
            }
        }
        return super.doEndTag();
    }

    public void doFinally() {
        entries = null;
        super.doFinally();
    }
}
