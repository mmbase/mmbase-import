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
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.storage.search.*;
//import org.mmbase.util.logging.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9
 * @version $Id: QueryAddNodeTag.java,v 1.3 2008-07-31 08:14:23 michiel Exp $
 */
public class QueryAddNodeTag extends ContextReferrerTag implements QueryContainerReferrer {


    protected Attribute container  = Attribute.NULL;

    protected Attribute element      = Attribute.NULL;
    protected Attribute node         = Attribute.NULL;

    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c, true);
    }

    public void setElement(String e) throws JspTagException {
        element = getAttribute(e, true);
    }
    public void setNumber(String n) throws JspTagException {
        node = getAttribute(n);
    }

    public int doStartTag() throws JspTagException {
        Query query = getQuery(container);
        Step step;
        if (element == Attribute.NULL) {
            if (query instanceof NodeQuery) {
                step = ((NodeQuery) query).getNodeStep();
            } else {
                throw new TaglibException("No element specified, and the query is no node-query");
            }
        } else {
            step = query.getStep(element.getString(this));
        }

        for (String n : node.getList(this)) {
            query.addNode(step, query.getCloud().getNode(n).getNumber());
        }
        findWriter(false); // just to call haveBody.., because constraint is not officially a
                           // writerreferer (but e.g. _ can be used in attributes)
        return SKIP_BODY;
    }

}
