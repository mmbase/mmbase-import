/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.*;

import javax.servlet.jsp.JspTagException;


/**
 * FormTag can be used to generate the 'action' attribute of an HTML Form. But more importantly, it
 * collects 'validation' from mm:fieldinfo type="check" or type="errors".
 *
 * The result can be reported with mm:valid.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FormTag.java,v 1.6 2006-07-09 14:15:23 michiel Exp $
 * @since MMBase-1.8
 */

public class FormTag extends TransactionTag implements Writer {

    private Attribute mode = Attribute.NULL;
    private int m;

    private Attribute page = Attribute.NULL;
    private Attribute clazz = Attribute.NULL;

    public static final int MODE_HTML_FORM       = 0;
    public static final int MODE_URL             = 1;
    public static final int MODE_VALIDATE        = 2;


    protected boolean valid = true;
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public boolean isValid() {
        return valid;
    }

    public void setMode(String m) throws JspTagException {
        mode = getAttribute(m);
    }

    public void setStyleClass(String c) throws JspTagException {
        clazz = getAttribute(c);
    }

    private int getMode() throws JspTagException {
        String m = mode.getString(this).toLowerCase();
        if (m.equals("") || m.equals("form")) {
            return MODE_HTML_FORM;
        } else if (m.equals("url")) {
            return MODE_URL;
        } else if (m.equals("validate")) {
            return MODE_VALIDATE;
        } else {
            throw new JspTagException("Value '" + m + "' not known for 'mode' attribute");
        }
    }


    public void setPage(String p) throws JspTagException {
        page = getAttribute(p);
    }


    public int doStartTag() throws JspTagException {
        m = getMode();
        switch(m) {
        case MODE_URL:
            helper.setValue(page.getString(this));
            break;
        case MODE_HTML_FORM:
            String url = page.getString(this);
            String id = getId();
            String c  = clazz.getString(this);
            try {
                pageContext.getOut().write("<form " + (id != null ? "id=\"" + id + "\" " : "") +
                                           "action=\"" + url + "\" method=\"post\" enctype=\"multipart/form-data\" class=\"mm_form" +
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
        try {
            if (! transaction.isCanceled() && ! transaction.isCommitted()) {
                if (commit.getBoolean(this, getDefaultCommit())) {
                    transaction.commit();
                } else {
                    transaction.cancel();
                }
            }
        } catch (Throwable t) {
            try {
                // should not happen, but if it happens, don't fail the complete page, this probably is
                // an editor!
                pageContext.getOut().write(t.getMessage());
            } catch (java.io.IOException ioe) {
                throw new TaglibException(ioe);
            }
        }
        switch(m) {
        case MODE_HTML_FORM:
            try {
                pageContext.getOut().write("</form>");
            } catch (java.io.IOException ioe) {
                throw new TaglibException(ioe);
            }
        }
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }
        transaction = null;
        return super.doEndTag();
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
