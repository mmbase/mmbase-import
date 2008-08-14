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
 * @version $Id: DepthTag.java,v 1.8 2008-08-14 13:58:54 michiel Exp $
 */
public class DepthTag extends TreeReferrerTag implements Writer {

    public int doStartTag() throws JspTagException {
        initTag();
        DepthProvider dp =  findDepthProvider();
        helper.setValue(dp.getDepth());
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();
    }

}

