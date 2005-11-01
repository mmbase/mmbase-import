/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.query;

import org.w3c.dom.*;

/**
 * A QueryConfigurer has the task to instantiate {@link QueryDefinition}s (wrappers around Query's)
 * and {@link FieldDefinition}s (wrappers around StepFields).
 *
 * @author Pierre van Rooden
 * @version $Id: QueryConfigurer.java,v 1.4 2005-11-01 18:01:44 michiel Exp $
 * @since MMBase-1.8
 **/
public class QueryConfigurer {

    public static QueryConfigurer defaultConfigurer = new QueryConfigurer();

    public QueryDefinition getQueryDefinition() {
        return new QueryDefinition(this); // using 'this' is ugly.
    }

    public FieldDefinition getFieldDefinition(QueryDefinition queryDefinition) {
        return new FieldDefinition(this, queryDefinition);
    }

    public static QueryConfigurer getDefaultConfigurer() {
        return defaultConfigurer;
    }

}
