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
* Remove an object from the Context.
* 
* @author Michiel Meeuwissen
*/
public class RemoveTag extends ImportTag {

    public int doAfterBody() throws JspTagException{
        findContext().unRegister(key);
        return SKIP_BODY;
    }

}
