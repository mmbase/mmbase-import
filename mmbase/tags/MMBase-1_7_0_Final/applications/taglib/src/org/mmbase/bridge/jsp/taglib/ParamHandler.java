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
 * @version $Id: ParamHandler.java,v 1.2 2003-09-01 13:29:43 pierre Exp $
 */

public interface ParamHandler {
    public void addParameter(String key, Object value) throws JspTagException;
}
