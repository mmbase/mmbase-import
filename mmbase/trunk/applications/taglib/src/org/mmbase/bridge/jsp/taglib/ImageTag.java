/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

/**
 * Produces a url to 'img.db'. Using this tag makes your pages more
 * portable to other system, and hopefully less sensitive for future
 * changes in how the image servlet works.
 * 
 * @author Michiel Meeuwissen 
 **/

public class ImageTag extends NodeReferrerTag  implements Writer {


    private String template = null;
    protected WriterHelper helper = new WriterHelper(); 
    // sigh, we would of course prefer to extend, but no multiple inheritance possible in Java..

    public void setVartype(String t) throws JspTagException { 
        throw new JspTagException("Image tag can only produces Strings");
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getValue() throws JspTagException {
        return helper.getValue();
    }
           
    public void setTemplate(String t) throws JspTagException {
        template = getAttributeValue(t);
    }
    
    public int doStartTag() throws JspTagException {  
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest)pageContext.getRequest();
        String page = req.getContextPath() + "/img.db?" + getNode().getNumber() + (template != null ? "+" + template : "");
        helper.setValue(page);
        helper.setJspvar(pageContext);  
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }

}

