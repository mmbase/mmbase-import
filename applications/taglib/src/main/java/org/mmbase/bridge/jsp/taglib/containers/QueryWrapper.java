/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.CloudProvider;
import javax.servlet.jsp.PageContext;


/**
 * A simple wrapper around a query object, which is used to be put on the request and such.
 * The query object can simply be cloned, without having to reput it and such.
 * @since MMBase-1.9.2
 * @author Michiel Meeuwissen
 */
public class QueryWrapper<Q extends Query> {
    public Q query;
    public QueryWrapper(Q q) {
        query = q;
    }
    public void cloneQuery() {
        query = (Q) query.clone();
    }

    public String toString() {
        return query.toSql();
    }
    public String getSql() {
        return query.toSql();
    }
    public Q getQuery() {
        return query;
    }

}
