/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This tag can be used inside the list tag. The body will be
 * evaluated depending on the value of the index of the list.
 *
 *
 * @author Michiel Meeuwissen 
 **/

public class ListConditionTag extends ListReferrerTag implements Condition {
    
    private static Logger log = Logging.getLoggerInstance(ListConditionTag.class.getName()); 
    
    private String  value;
    private String  parentListId;
    private boolean inverse = false;
    
    public void setValue(String v) throws JspTagException {
        value = getAttributeValue(v);
    }
    
    public void setInverse(Boolean b) {
        inverse = b.booleanValue();
    }
    
    public int doStartTag() throws JspTagException{
        // find the parent list:
        ListProvider list = getList();

        boolean result;
        if        ("first".equalsIgnoreCase(value)) {  
            result = (list.getIndex() == 0 ) != inverse;
        } else if ("last".equalsIgnoreCase(value))  {  
            result = (list.getIndex() == list.size()-1 )  != inverse;
        } else if ("even".equalsIgnoreCase(value)) {
            result = ((list.getIndex() + 1) % 2 == 0) != inverse;
        } else if ("odd".equalsIgnoreCase(value)) {
            result = ((list.getIndex() + 1) % 2 != 0) != inverse;
        } else if ("changed".equalsIgnoreCase(value)) {
            result = list.isChanged() != inverse;
        } else {
            throw new JspTagException ("Don't know what do (" + value +")");  
        }
        
        return result ? EVAL_BODY_TAG : SKIP_BODY;
    
    }
    
    /**
    * 
    **/
    public int doAfterBody() throws JspTagException {
        try{
            if(bodyContent != null)
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch(java.io.IOException e){
            throw new JspTagException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

}
