/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

/**
* The exporttag can take a variable from the context and put it in a jsp variable.
* 
* @author Michiel Meeuwissen
*/
public class ExportTag extends CloudReferrerTag {

    private String jspvar = null;
    
    public void setType(String t) {
        // nothing to do, the type property is only used in the TEI.
    }    

    public void setJspvar(String j) {
        jspvar = j;
    }
    
    public int doStartTag() throws JspTagException{
        pageContext.setAttribute(jspvar, getObject(getId()));
        return SKIP_BODY;
    }

}
