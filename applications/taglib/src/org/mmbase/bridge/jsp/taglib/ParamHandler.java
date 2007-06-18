/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
/**
 * ParamHandlers can have the &lt;mm:param&gt; tag as subtag
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @see    org.mmbase.bridge.jsp.taglib.pageflow.UrlTag
 * @version $Id: ParamHandler.java,v 1.3 2007-06-18 17:29:21 michiel Exp $
 */

public interface ParamHandler {
    public void addParameter(String key, Object value) throws JspTagException;

    /**
     * @throws UnsupportedOperationException if the tag cannot handle framework parameters.
     * @since MMBase-1.9
     */
    public void addFrameworkParameter(String key, Object value) throws JspTagException;
}
