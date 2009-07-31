/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.pageflow.*;
import org.mmbase.bridge.Transaction;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * FormTag can be used to generate the 'action' attribute of an HTML Form. But more importantly, it
 * collects 'validation' from mm:fieldinfo type="check" or type="errors".
 *
 * The result can be reported with mm:valid.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class FormTag extends TransactionTag implements Writer {
    private static final Logger log = Logging.getLoggerInstance(FormTag.class);

    public static final String KEY = "org.mmbase.bridge.jsp.taglib.form";
    public static final int SCOPE = PageContext.REQUEST_SCOPE;

    /**
     * Produces an HTML form, and (reuses) an MMBase transaction. Only explicit commit/cancel (with
     * mm:commit, mm:cancel, or 'commitonclose').
     */
    private static final int MODE_HTML_FORM       = 0;

    /**
     * Produces an URL for an HTML form only, and (reuses) an MMBase transaction. Only explicit commit/cancel.
     */
    private static final int MODE_URL             = 1;

    /**
     * Does not produce any content. Implicitely cancels the transaction if not committed.
     */
    private static final int MODE_VALIDATE        = 2;


    /**
     * Like, form but implicitely cancels if not explicitely commited.
     */
    private static final int MODE_HTML_FORM_VALIDATE        = 3;


    /**
     * Does not produce any content. Behaves like mm:transaction. Only difference is that on default
     * it does not commit on close.
     */
    private static final int MODE_TRANSACTION     = 4;


    private Attribute mode = Attribute.NULL;
    private Attribute method = Attribute.NULL;
    private int m;

    private Attribute page = Attribute.NULL;
    private Attribute clazz = Attribute.NULL;

    private Attribute absolute = Attribute.NULL;

    private Object previous;

    protected boolean valid = true;
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public boolean isValid() {
        return valid;
    }

    public void setMode(String m) throws JspTagException {
        mode = getAttribute(m, true);
    }

    public void setMethod(String m) throws JspTagException {
        method = getAttribute(m);
    }

    public void setStyleClass(String c) throws JspTagException {
        clazz = getAttribute(c);
    }

    private int getMode() throws JspTagException {
        String m = mode.getString(this).toLowerCase();
        if (m.length() == 0 || m.equals("form")) {
            return MODE_HTML_FORM;
        } else if (m.equals("url")) {
            return MODE_URL;
        } else if (m.equals("validate")) {
            return MODE_VALIDATE;
        } else if (m.equals("transaction")) {
            return MODE_TRANSACTION;
        } else if (m.equals("formvalidate")) {
            return MODE_HTML_FORM_VALIDATE;
        } else {
            throw new JspTagException("Value '" + m + "' not known for 'mode' attribute");
        }
    }


    public void setPage(String p) throws JspTagException {
        page = getAttribute(p);
    }

    public void setAbsolute(String a) throws JspTagException {
        absolute = getAttribute(a, true);
    }


    public int doStartTag() throws JspTagException {
        if (getReferid() != null) {
        }
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), this);
        }
        previous = pageContext.getAttribute(KEY, SCOPE);
        if (previous != null) {
            log.debug("Found previous form-tag " + previous);
        }
        pageContext.setAttribute(KEY, this, SCOPE);
        m = getMode();
        Url u = new Url(this, (CharSequence) page.getString(this), absolute.getString(this));
        u.setProcess();


        switch(m) {
        case MODE_URL:
            helper.setValue(u);
            break;
        case MODE_HTML_FORM:
        case MODE_HTML_FORM_VALIDATE:
            String url = u.toString();
            String id = getId();
            String c  = clazz.getString(this);
            try {
                String m = method == Attribute.NULL ? "post" : method.getString(this);
                pageContext.getOut().write("<form " + (id != null ? "id=\"" + id + "\" " : "") +
                                           "action=\"" + url + "\" method=\"" + m + "\" enctype=\"multipart/form-data\" class=\"mm_form" +
                                           ("".equals(c) ? "" : " " + c) +
                                           "\" >");
            } catch (java.io.IOException ioe) {
                throw new TaglibException(ioe);
            }
            break;
        }
        valid = true;
        return super.doStartTag();
    }

    public int doEndTag() throws JspTagException {
        pageContext.setAttribute(KEY, previous, SCOPE);
        previous = null;
        switch(m) {
        case MODE_HTML_FORM:
        case MODE_HTML_FORM_VALIDATE:
            try {
                pageContext.getOut().write("</form>");
            } catch (java.io.IOException ioe) {
                throw new TaglibException(ioe);
            }
            break;
        }
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }
        Transaction t = transaction;
        int result = super.doEndTag();
        switch(m) {
        case MODE_VALIDATE:
        case MODE_HTML_FORM_VALIDATE:;
            if (! t.isCommitted()) {
                t.cancel();
            }
        }
        return result;
    }


    // never commit on close, unless, explicitely requested, of course.
    protected boolean getDefaultCommit() {
        return false;
    }

    protected String getName() throws JspTagException {
        if (name == Attribute.NULL) return "org.mmbase.taglib.form";
        return (String) name.getValue(this);
    }


}
