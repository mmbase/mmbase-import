/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * The size of a list.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SizeTag.java,v 1.10 2003-06-06 10:03:09 pierre Exp $ 
 */

public class SizeTag extends ListReferrerTag implements Writer {

    private static Logger log = Logging.getLoggerInstance(SizeTag.class.getName());

    public int doStartTag() throws JspTagException{
        helper.setTag(this);
        helper.setValue(new Integer(getList().size()));

        if (getId() != null) {
            getContextProvider().getContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     *
     **/
    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }

}
