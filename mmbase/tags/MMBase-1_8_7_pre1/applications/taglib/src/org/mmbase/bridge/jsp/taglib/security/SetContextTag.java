/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.security;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A very simple tag to change the context of the node
 * 
 * @author Michiel Meeuwissen
 * @version $Id: SetContextTag.java,v 1.9 2005-03-14 19:02:35 michiel Exp $
 */

public class SetContextTag extends NodeReferrerTag {

    private static final Logger log = Logging.getLoggerInstance(SetContextTag.class);
    private Attribute name = Attribute.NULL;

    public void setName(String n) throws JspTagException {
        name = getAttribute(n);
    }

    public int doEndTag() throws JspTagException {        
        String n;
        if (name == Attribute.NULL) {
            n = bodyContent.getString();
        } else {
            n = name.getString(this);
        }
        if (log.isDebugEnabled()) log.debug("Setting context to " + n);
        getNode().setContext(n);
        return super.doEndTag();
    }   

}
