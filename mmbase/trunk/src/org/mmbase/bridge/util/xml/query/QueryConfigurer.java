/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml.query;

import org.w3c.dom.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: QueryConfigurer.java,v 1.1 2005-06-30 12:37:54 pierre Exp $
 **/
public class QueryConfigurer {

    public static QueryConfigurer defaultConfigurer = new QueryConfigurer();

    public QueryDefinition getQueryDefinition(Element queryElement) {
        return new QueryDefinition(queryElement);
    }

    public FieldDefinition getFieldDefinition(QueryDefinition queryDefinition, Element fieldElement) {
        return new FieldDefinition(queryDefinition, fieldElement);
    }

    public static QueryConfigurer getDefaultConfigurer() {
        return defaultConfigurer;
    }

}
