/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This tag can be used inside the list tag. The body will be
 * evaluated depending on the value of the index of the list.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: ListConditionTag.java,v 1.18 2003-09-10 11:16:08 michiel Exp $ 
 */

public class ListConditionTag extends ListReferrerTag implements Condition {

    private static final Logger log = Logging.getLoggerInstance(ListConditionTag.class.getName());

    private Attribute  value        = Attribute.NULL;
    private Attribute  parentListId = Attribute.NULL;
    private Attribute  inverse      = Attribute.NULL;

    protected final static int CONDITION_FIRST = 1;
    protected final static int CONDITION_LAST  = 2;
    protected final static int CONDITION_EVEN  = 3;
    protected final static int CONDITION_ODD    = 4;
    protected final static int CONDITION_CHANGED  = 5;

    public void setValue(String v) throws JspTagException {
        value = getAttribute(v);
    }

    protected int getValue() throws JspTagException {
        String v = value.getString(this).toLowerCase();
        if (v.equals("first")) {
            return CONDITION_FIRST;
        } else if (v.equals("last")) {
            return CONDITION_LAST;
        } else if (v.equals("even")) {
            return CONDITION_EVEN;
        } else if (v.equals("odd")) {
            return CONDITION_ODD;
        } else if (v.equals("changed")) {
            return CONDITION_CHANGED;
        } else {
            throw new JspTagException ("Unknown condiation value (" + v +")");
        }
    }

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }

    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspTagException{
        // find the parent list:
        ListProvider list = getList();

        boolean i = getInverse();
        boolean result;
        switch(getValue()) {
        case CONDITION_FIRST: result = (list.getIndex() == 0 ) != i;              break;
        case CONDITION_LAST:  result = (list.getIndex() == list.size()-1 )  != i; break;
        case CONDITION_EVEN:  result = ((list.getIndex() + 1) % 2 == 0) != i;     break;
        case CONDITION_ODD:   result = ((list.getIndex() + 1) % 2 != 0) != i;     break;
        case CONDITION_CHANGED:  result = list.isChanged() != i;                  break;
        default: throw new JspTagException("Don't know what to do (" + getValue() + ")");
        }    
        return result ? EVAL_BODY_BUFFERED : SKIP_BODY;
    }

    /**
    *
    **/
    public int doAfterBody() throws JspTagException {
        try{
            if(bodyContent != null) {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }
        } catch(java.io.IOException e){
            throw new TaglibException(e);
        }
        return EVAL_PAGE;
    }

}
