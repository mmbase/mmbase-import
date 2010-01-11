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
 * A Query container can be used around node-list Tags. Basicly, it adminstrates a Query object.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 */
public interface QueryContainer extends  CloudProvider {

    static final String KEY = "org.mmbase.query";
    static final int SCOPE = PageContext.REQUEST_SCOPE;
    /**
     * Returns the currently by the container defined query object. Subtags can use this query
     * object to change it or to use it.
     */
    Query getQuery();

    void setJspvar(String jv);



}
