/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
/**
 * ParamHandlers can have the &lt;mm:param&gt; ({@link ParamTag}) tag as subtag.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @see    org.mmbase.bridge.jsp.taglib.pageflow.UrlTag
 * @version $Id$
 */

public interface ParamHandler {

    /**
     * @since MMBase-1.9
     */
    public static final String KEY = "org.mmbase.paramhandler";
    /**
     * @since MMBase-1.9
     */
    public static final int SCOPE = PageContext.REQUEST_SCOPE;

    public void addParameter(String key, Object value) throws JspTagException;

}
