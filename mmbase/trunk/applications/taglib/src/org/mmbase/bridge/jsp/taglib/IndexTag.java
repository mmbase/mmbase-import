/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * The index of current item of a list.
 *
 * @author Michiel Meeuwissen
 * @version $Id: IndexTag.java,v 1.12 2003-08-05 18:42:56 michiel Exp $ 
 */

public class IndexTag extends ListReferrerTag implements Writer {

    private static Logger log = Logging.getLoggerInstance(IndexTag.class.getName());

    private Attribute offset = Attribute.NULL; 

    public void setOffset(String o) throws JspTagException {
        offset = getAttribute(o);
    }
    protected int getOffset() throws JspTagException {
        return offset.getInt(this, getList().getOffset()); // start counting at list's offset on default (normally 1)
    }

    public int doStartTag() throws JspTagException{
        helper.setTag(this);
        helper.setValue(new Integer(getList().getIndex() + getOffset()));
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
