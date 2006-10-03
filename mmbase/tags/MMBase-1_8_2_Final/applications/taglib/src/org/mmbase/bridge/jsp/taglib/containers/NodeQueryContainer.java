/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.NodeQuery;

/**
 * A NodeQuery Container can be used around node-list Tags. Basicly, it adminstrates a NodeQuery object.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeQueryContainer.java,v 1.3 2006-07-04 12:16:09 michiel Exp $
 */
public interface NodeQueryContainer extends QueryContainer {

    /**
     * Returns the currently by the container defined query object. Subtags can use this query
     * object to change it or to use it. 
     */
    NodeQuery getNodeQuery();


}
