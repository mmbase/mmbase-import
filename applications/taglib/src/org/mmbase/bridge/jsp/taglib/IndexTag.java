/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * The index of current item of a list.
 *
 *
 * @author Michiel Meeuwissen 
 *
 */

public class IndexTag extends ListReferrerTag implements Writer {
    
    private static Logger log = Logging.getLoggerInstance(IndexTag.class.getName());
    // Writer implementation:
    protected WriterHelper helper = new WriterHelper();
    public void setVartype(String t) throws JspTagException { 
        helper.setVartype(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getValue() {
        return helper.getValue();
    }

    private int offset = 1; // start counting at 1 on default.

    public void setOffset(String o) throws JspTagException {
        offset = getAttributeInteger(o).intValue();
    }    
    
    public int doStartTag() throws JspTagException{
        helper.setValue(new Integer(getList().getIndex() + offset)); 
        helper.setJspvar(pageContext);  
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_TAG;
    }
    
    /**
     * 
     **/
    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }

}
