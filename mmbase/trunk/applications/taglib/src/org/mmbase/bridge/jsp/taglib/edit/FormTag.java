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
 * @version $Id: FormTag.java,v 1.2 2006-02-10 18:05:13 michiel Exp $
 * @since MMBase-1.8
 */

public class FormTag extends TransactionTag implements Writer {

    protected boolean valid = true;
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public int doStartTag() throws JspTagException {
        valid = true;
        return super.doStartTag();
    }

    public int doEndTag() throws JspTagException {
        if (! transaction.isCanceled() && ! transaction.isCommitted()) {
            if (commit.getBoolean(this, getDefaultCommit())) {
                transaction.commit();
            } else {
                transaction.cancel();
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
