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
//import org.mmbase.util.logging.*;

/**
 * Make query distinct (or not)
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: QueryDistinctTag.java,v 1.3 2008-06-27 09:07:10 michiel Exp $
 */
public class QueryDistinctTag extends CloudReferrerTag implements QueryContainerReferrer {

    //private static final Logger log = Logging.getLoggerInstance(NodeListDistinctTag.class);

    protected Attribute container  = Attribute.NULL;

    protected Attribute distinct   = Attribute.NULL;


    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    public void setValue(String a) throws JspTagException {
        distinct = getAttribute(a);
    }


    public int doStartTag() throws JspTagException {
        Query query = getQuery(container);
        query.setDistinct(distinct.getBoolean(this, true));
        return SKIP_BODY;
    }

}
