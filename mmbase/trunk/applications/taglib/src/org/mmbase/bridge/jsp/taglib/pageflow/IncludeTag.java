/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;
import javax.servlet.jsp.JspTagException;

/**
 * Like UrlTag, but does not spit out an URL, but the page itself.
 * 
 * @author Michiel Meeuwissen
 */
public class IncludeTag extends UrlTag {
    public int doAfterBody() throws JspTagException {

        if (page == null) {
            throw new JspTagException("Attribute 'page' was not specified");
        }
        try {
            bodyContent.clear(); // newlines and such must be removed            
            pageContext.include(getUrl(false)); // false: don't write &amp; tags but real &.
            if (getId() != null) {
                getContextTag().register(getId(), bodyContent.getString());
            }
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        } catch (javax.servlet.ServletException e) {
            throw new JspTagException (e.toString());        
        }
        
        return SKIP_BODY;
    }

}
