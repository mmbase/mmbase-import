/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.*;

import java.util.List;
import javax.servlet.jsp.JspTagException;

/**
 * A NodeList Container can be used around node-list Tags. Basicly, it adminstrates a Query object.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListContainer.java,v 1.4 2003-08-07 17:17:40 michiel Exp $
 */
public interface NodeListContainer extends TagIdentifier, NodeListContainerOrListProvider {

    /**
     * Returns the currently by the container defined query object. Subtags can use this query
     * object to change it or to use it. 
     */
    Query getQuery();



    Cloud getCloud() throws JspTagException;

}
