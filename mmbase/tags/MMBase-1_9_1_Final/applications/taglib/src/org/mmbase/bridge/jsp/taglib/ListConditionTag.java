/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.jstl.core.*;
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
 * @version $Id$ 
 */

public class ListConditionTag extends ListReferrerTag implements Condition {

    private static final Logger log = Logging.getLoggerInstance(ListConditionTag.class);

    private Attribute  value        = Attribute.NULL;
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
        LoopTag list = getLoopTag();


        boolean i = getInverse();
        boolean result = false;
        int j = getValue();
        //
        // One would expect a switch, but for some odd reason, that does not work in my resin 3.0.6
        //
        switch(j) {
        case CONDITION_LAST:   result = list.getLoopStatus().isLast() != i; break;
        case CONDITION_FIRST:  result = list.getLoopStatus().isFirst() != i; break;
        case CONDITION_EVEN:   result = ((list.getLoopStatus().getIndex() + 1) % 2 == 0) != i; break;
        case CONDITION_ODD:    result = ((list.getLoopStatus().getIndex() + 1) % 2 != 0) != i; break;
        case CONDITION_CHANGED: 
            if (list instanceof ListProvider) {
                result = ((ListProvider) list).isChanged() != i; 
            } else {
                result = ! i;
            }

            break;
        default:
            throw new JspTagException("Don't know what to do (" + getValue() + ")");
        }
        if (log.isDebugEnabled()) {
            log.debug("result " + (result ? "EVAL BODY" : "SKIP BODY"));
        }
        return (result ? EVAL_BODY : SKIP_BODY);
    }


    /**
     *
     **/
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            try{
                if(bodyContent != null) { 
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch(java.io.IOException e){
                throw new TaglibException(e);
            }
            
        }
        return EVAL_PAGE;
    }

}
