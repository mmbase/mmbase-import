/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import  org.mmbase.bridge.jsp.taglib.util.Attribute;
import  org.mmbase.bridge.jsp.taglib.functions.Functions;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.xml.transform.stream.StreamSource;

/**
 * Has to live in a formatter tag, and can provide inline XSLT to it.
 *
 * @author Michiel Meeuwissen
 * @version $Id: XsltTag.java,v 1.19 2005-05-18 15:34:32 michiel Exp $ 
 */

public class XsltTag extends ContextReferrerTag  {


    private static final Logger log = Logging.getLoggerInstance(XsltTag.class);

    private Attribute ext = Attribute.NULL;
    private FormatterTag formatter;



    /**
     * If you use the extends attribute in stead of inline <xsl:import />
     * then the caches can be invalidated (without parsing of xslt beforehand)
     *
     * @todo This has to be implemented still
     */
    public void setExtends(String e) throws JspTagException {
        ext = getAttribute(e);
    }

    public int doStartTag() throws JspTagException{
        // Find the parent formatter.
        formatter = (FormatterTag) findParentTag(FormatterTag.class, null, false);
        if (formatter == null && getId() == null) {
            throw new JspTagException("No parent formatter found");
            // living outside a formatter tag can happen the xslttag has an id.
            // then it can be used as a 'constant'.
            // like this:
            //  <mm:xslt id="bla">....</mm:xslt>
            //  <mm:formatter>xxx <mm:xslt referid="bla" /></mm:formatter>
            //  <mm:formatter>yyy <mm:xslt referid="bla" /></mm:formatter>

        }
        return EVAL_BODY_BUFFERED;
    }

    /**
     * 
     */
    public int doEndTag() throws JspTagException {
        String xsltString;
        String body = bodyContent != null ? bodyContent.getString() : "";
        if (getReferid() == null) {
            xsltString = body.trim();
        } else {
            xsltString = getString(getReferid());
            if (! "".equals(body)) {
                throw new JspTagException("Cannot use body when using 'referid' attribute'.");
            }
        }
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), xsltString);
        }
        if (formatter != null) { 
            String totalString;
            if (xsltString.startsWith("<xsl:stylesheet")) {
                totalString = xsltString;
            } else {
                totalString =
                    "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\"" + 
                    " xmlns:taglib=\"" +  Functions.class.getName() + "\"" + 
                    " xmlns:mm=\"" +  Functions.class.getName() + "\"" + 
                    " xmlns:o=\"http://www.mmbase.org/objects\"" +
                    " xmlns:mmxf=\"http://www.mmbase.org/mmxf\"" +
                    " extension-element-prefixes=\"mm taglib\"" +
                    " exclude-result-prefixes=\"node mmxf o mm taglib\"" +
                    " >\n" +
                    xsltString +
                    "\n</xsl:stylesheet>";
            }
            StreamSource src = new StreamSource(new java.io.StringReader(totalString));
            String systemId = ((HttpServletRequest)pageContext.getRequest()).getRequestURL().append('/').append(((long) xsltString.hashCode() & 0xffff)).toString();
            src.setSystemId(systemId);
            if (log.isDebugEnabled()) log.debug("Found xslt " + systemId + ": " + totalString);
            formatter.setXsltSource(src);
        }
        formatter = null;
        return super.doEndTag();
    }
}
