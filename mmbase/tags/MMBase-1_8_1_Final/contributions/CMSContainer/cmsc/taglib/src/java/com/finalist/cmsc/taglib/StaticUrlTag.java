/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.jsp.taglib.pageflow.UrlTag;

public class StaticUrlTag extends UrlTag {

    protected void doAfterBodySetValue() throws JspTagException {
       helper.setValue(getUrl(escapeAmps.getBoolean(this, true), encode.getBoolean(this, false)));
    }
}
