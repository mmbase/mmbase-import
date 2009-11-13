/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.query;

import java.util.*;

/**
 * A QueryConfigurer has the task to instantiate {@link QueryDefinition}s (wrappers around Query's)
 * and {@link FieldDefinition}s (wrappers around StepFields).
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8
 * @javadoc
 **/
public class QueryConfigurer {

    /**
     * @since MMBase-1.9.1
     */
    public final Map<String, Object> variables = new HashMap<String, Object>();

    public QueryDefinition getQueryDefinition() {
        QueryDefinition qd = new QueryDefinition();
        qd.setVariables(variables);
        return qd;
    }

    public FieldDefinition getFieldDefinition() {
        return new FieldDefinition();
    }

    public static QueryConfigurer getDefaultConfigurer() {
        return new QueryConfigurer();
    }

}
