/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* The importtag takes its body, and writes it to the context.
* 
* @author Michiel Meeuwissen
*/
public class ImportTag extends CloudReferrerTag {

    private static Logger log = Logging.getLoggerInstance(ImportTag.class.getName()); 

    protected String key = null;
    
    public void setKey(String k) {
        key = k;
    }

    public int doAfterBody() throws JspTagException{        
        log.debug("Setting " + key + " to " + bodyContent.getString());
        getContextTag().register(key, bodyContent.getString());
        return SKIP_BODY;
    }

}
