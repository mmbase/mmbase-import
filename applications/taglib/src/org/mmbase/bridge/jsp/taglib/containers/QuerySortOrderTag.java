/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Query;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.jsp.taglib.CloudReferrerTag;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.storage.search.*;

/**
 * Applies a sortorder to the surrounding query.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: QuerySortOrderTag.java,v 1.9 2008-06-27 09:07:10 michiel Exp $
 */
public class QuerySortOrderTag extends CloudReferrerTag implements QueryContainerReferrer {

    protected Attribute container = Attribute.NULL;
    protected Attribute direction = Attribute.NULL;
    protected Attribute field = Attribute.NULL;
    protected Attribute part  = Attribute.NULL;
    protected Attribute casesensitive = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setDirection(String d) throws JspTagException {
        direction = getAttribute(d);
    }

    public void setPart(String p) throws JspTagException {
        part = getAttribute(p);
    }

    public void setCasesensitive(String s) throws JspTagException {
        casesensitive = getAttribute(s);
    }

    public void setField(String f) throws JspTagException {
        field = getAttribute(f);
    }

    public int doStartTag() throws JspTagException {
        Query query = getQuery(container);
        int order = Queries.getSortOrder(direction.getString(this));
        int orderPart = Queries.getDateTimePart(part.getString(this));
        StepField stepField = query.createStepField(field.getString(this));

        query.addSortOrder(stepField, order, casesensitive.getBoolean(this, false), orderPart);

        return SKIP_BODY;
    }

}
