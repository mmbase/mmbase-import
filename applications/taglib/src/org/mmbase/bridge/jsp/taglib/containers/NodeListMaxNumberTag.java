/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Query;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 * Applies a maxnumber to the surrounding query.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListMaxNumberTag.java,v 1.3 2003-08-29 12:12:25 keesj Exp $
 */
public class NodeListMaxNumberTag extends CloudReferrerTag implements NodeListContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(NodeListMaxNumberTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute max     = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setValue(String a) throws JspTagException {
        max = getAttribute(a);
    }


    public int doStartTag() throws JspTagException { 
        NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this));
        Query query = c.getQuery();
        query.setMaxNumber(max.getInt(this, -1));
        return SKIP_BODY;
    }

}
