/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.query;


/**
 * A QueryConfigurer has the task to instantiate {@link QueryDefinition}s (wrappers around Query's)
 * and {@link FieldDefinition}s (wrappers around StepFields).
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8
 **/
public class QueryConfigurer {

    private static final QueryConfigurer DEFAULT_CONFIGURER = new QueryConfigurer();

    public QueryDefinition getQueryDefinition() {
        return new QueryDefinition();
    }

    public FieldDefinition getFieldDefinition() {
        return new FieldDefinition();
    }

    public static QueryConfigurer getDefaultConfigurer() {
        return DEFAULT_CONFIGURER;
    }

}
