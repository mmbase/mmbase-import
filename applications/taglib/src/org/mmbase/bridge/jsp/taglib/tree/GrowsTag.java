/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;


import javax.servlet.jsp.JspTagException;


import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;



/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: GrowsTag.java,v 1.1 2003-12-18 23:05:44 michiel Exp $
 */
public class GrowsTag extends TreeReferrerTag implements Condition {

    Attribute inverse = Attribute.NULL;
    Attribute to      = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }


    public void setTo(String t) throws JspTagException {
        to = getAttribute(t);
    }



    protected boolean compare(int previousLevel, int level) {
        return previousLevel < level;
    }

    protected boolean match() throws JspTagException {
        TreeProvider tp = findTreeProvider();
        
        int level = tp.getDepth();

        if (! compare(tp.getPreviousDepth(), level)) return false;
        
        if (to == Attribute.NULL) return true;

        if (to.getInt(this, 0) == level) return true;
        return false;

    }
    
    public int doStartTag() throws JspTagException {
        if (match() != getInverse()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }
    
    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) {
            try{
                if(bodyContent != null)
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch(java.io.IOException e){
                throw new JspTagException("IO Error: " + e.getMessage());
            }
        }
        return EVAL_PAGE;
    }

}

