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
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This tag can be used inside the list tag, to evaluate the body depending on the index.
 *
 * @author Michiel Meeuwissen
 */
public class ListConditionTag extends BodyTagSupport {

    private static Logger log = Logging.getLoggerInstance(ListConditionTag.class.getName()); 

    private String value;
    private String parentListId;
    private String type = "LIST";
    private boolean inverse = false;

    public void setValue(String v){
        value = v;
    }

    public void setList(String l) {
        parentListId = l;
    }

    public void setInverse(Boolean b) {
        inverse = b.booleanValue();
    }
    
    public int doStartTag() throws JspException{
        ListTag listTag;
        Class listTagClass;
        try {
            listTagClass = Class.forName("org.mmbase.bridge.jsp.taglib.ListTag");
        } catch (java.lang.ClassNotFoundException e) {
            throw new JspException ("Could not find ListTag class");  
        }
        
        listTag = (ListTag) findAncestorWithClass((Tag)this, listTagClass); 
        if (listTag == null) {
            throw new JspException ("Could not find parentlist");  
        }
        if (parentListId != null) { // search further, if necessary
            while (listTag.getId() != parentListId) {
                listTag = (ListTag) findAncestorWithClass((Tag)listTag, listTagClass);            
                if (listTag == null) {
                    throw new JspException ("Could not find parent with id " + parentListId);  
                }
            }
        }

        if ("first".equalsIgnoreCase(value)) {
            if (listTag.isFirst() != inverse) {
                return EVAL_BODY_TAG;
            } else {
                return SKIP_BODY;
            }
        } else if ("last".equalsIgnoreCase(value)) {
            if (listTag.isLast() != inverse) {
                return EVAL_BODY_TAG;
            } else {
                return SKIP_BODY;
            }
        } else if ("even".equalsIgnoreCase(value)) {
            if ((listTag.getIndex() % 2 == 0) != inverse) {
                return EVAL_BODY_TAG;
            } else {
                return SKIP_BODY;
            }
        } else if ("odd".equalsIgnoreCase(value)) {
            if ((listTag.getIndex() % 2 != 0) != inverse) {
                return EVAL_BODY_TAG;
            } else {
                return SKIP_BODY;
            }
        } else {
            throw new JspException ("Don't know what do.");  
        }
    }
    
    /**
     * 
     **/
    public int doAfterBody() throws JspException {
        try{
            if(bodyContent != null)
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch(java.io.IOException e){
            throw new JspException("IO Error: " + e.getMessage());
        }
        return EVAL_PAGE;
    }

}
