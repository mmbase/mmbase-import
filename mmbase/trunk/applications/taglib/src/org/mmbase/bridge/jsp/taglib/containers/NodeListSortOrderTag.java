/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Applies a maxnumber to the surrounding query.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListSortOrderTag.java,v 1.2 2003-08-27 21:33:39 michiel Exp $
 */
public class NodeListSortOrderTag extends CloudReferrerTag implements NodeListContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(NodeListSortOrderTag.class);

    protected Attribute container  = Attribute.NULL;
    protected Attribute direction  = Attribute.NULL;
    protected Attribute field      = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setDirection(String d) throws JspTagException {
        direction = getAttribute(d);
    }
    public void setField(String f) throws JspTagException {
        field = getAttribute(f);
    }


    public int doStartTag() throws JspTagException { 
        NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this));
        
        String dir = direction.getString(this).toUpperCase();
        
        Query query = c.getQuery();
        
        int order;
        
        if (dir.equals("")) {
            order = SortOrder.ORDER_ASCENDING;
        } else if (dir.equals("UP")) {
            order = SortOrder.ORDER_ASCENDING;
        } else if (dir.equals("DOWN")) {
            order = SortOrder.ORDER_DESCENDING;
        } else if (dir.equals("ASCENDING")) {
            order = SortOrder.ORDER_ASCENDING;
        } else if (dir.equals("DESCENDING")) {
            order = SortOrder.ORDER_DESCENDING;
        } else {
            throw new JspTagException("Unknown sort-order '" + dir + "'");
        }
        
        StepField stepField = query.createStepField(field.getString(this));
                   
        query.addSortOrder(stepField, order);
        return SKIP_BODY;
    }

}
