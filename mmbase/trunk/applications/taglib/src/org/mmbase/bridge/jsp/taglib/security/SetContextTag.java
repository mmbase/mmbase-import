/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* A very simple tag to change the context of the node
* 
* @author Michiel Meeuwissen
*/
public class SetContextTag extends NodeReferrerTag {

    private static Logger log = Logging.getLoggerInstance(SetContextTag.class.getName());
    private String name = null;

    public void setName(String n) throws JspTagException {
        name = getAttributeValue(n);
    }

    public int doAfterBody() throws JspTagException {        
        if (name == null) {
            name = bodyContent.getString();
        }
        if (log.isDebugEnabled()) log.debug("Setting context to " + name);
        getNode().setContext(name);
        name = null;
        return EVAL_PAGE;
    }   

}
