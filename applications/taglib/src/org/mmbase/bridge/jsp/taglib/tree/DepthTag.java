/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;


import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.jsp.taglib.Writer;


/**
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: DepthTag.java,v 1.1 2003-12-18 23:05:44 michiel Exp $
 */
public class DepthTag extends TreeReferrerTag implements Writer {

    public int doStartTag() throws JspTagException{
        helper.setValue(new Integer(findTreeProvider().getDepth()));
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }    

}

