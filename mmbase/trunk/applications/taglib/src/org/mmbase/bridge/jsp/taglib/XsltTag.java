/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Has to live in a formatter tag.
 * 
 * @author Michiel Meeuwissen 
 */

public class XsltTag extends ContextReferrerTag  {
    
  
    private static Logger log = Logging.getLoggerInstance(XsltTag.class.getName()); 
    
    private String ext;   
    private FormatterTag formatter;
       
    public void setExtends(String e) throws JspTagException {
        ext = getAttributeValue(e);
    }

    public int doStartTag() throws JspTagException{
        
        // Find the parent formatter.
        formatter = (FormatterTag) findParentTag("org.mmbase.bridge.jsp.taglib.FormatterTag", null, false);
        if (formatter == null && getId() == null) {
            throw new JspTagException("No parent formatter found");
        }
        return EVAL_BODY_TAG;
    }

    /**
     * Write the value of the fieldinfo.
     */
    public int doAfterBody() throws JspTagException {
        String xsltString;
        if (getReferid() == null) {
            xsltString = bodyContent.getString();

        } else {
            xsltString = getString(getReferid());
            if (! "".equals(bodyContent.getString())) {
                throw new JspTagException("Cannot use body when using 'referid' attribute'.");
            }
        }
        log.debug("Found xslt: " + xsltString);
        if (getId() != null) {
            getContextTag().register(getId(), xsltString);
        }
        if (formatter != null) {
            String totalString;
            if (xsltString.startsWith("<xsl:stylesheet")) {
                totalString = xsltString;
            } else {
                totalString = 
                    "<xsl:stylesheet xmlns:xsl = \"http://www.w3.org/1999/XSL/Transform\" version = \"1.0\" >\n" +
                    xsltString + 
                    "\n</xsl:stylesheet>";
            }
            formatter.setXsltSource(new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(totalString.getBytes())), xsltString);
        }
        return SKIP_BODY;
    }
}
