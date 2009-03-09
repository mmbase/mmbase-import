/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.storage.search.*;
//import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9.1
 * @version $Id: QueryAddConstraintTag.java,v 1.1 2009-03-09 18:05:50 michiel Exp $
 */
public class QueryAddConstraintTag extends CloudReferrerTag implements QueryContainerReferrer {

    protected Attribute container  = Attribute.NULL;
    protected Constraint constraint = null;
    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }
    public void setConstraint(Constraint c) throws JspTagException {
        constraint = c;
    }


    private Constraint addConstraint(Query query) throws JspTagException {
        Constraint newConstraint = constraint;
        QueryCompositeConstraintTag cons = findParentTag(QueryCompositeConstraintTag.class, (String) container.getValue(this), false);
        if (cons != null) {
            cons.addChildConstraint(constraint);
        } else {
            newConstraint = Queries.addConstraint(query, constraint);
        }
        return newConstraint;
    }

    public int doStartTag() throws JspTagException {
        Query query = getQuery(container);
        addConstraint(query);
        findWriter(false);
        return SKIP_BODY;
    }

}
