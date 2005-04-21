/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene.query;

import org.w3c.dom.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: QueryConfigurer.java,v 1.1 2005-04-21 14:28:43 pierre Exp $
 **/
public interface QueryConfigurer {

    public QueryDefinition getQueryDefinition(Element queryElement);

    public FieldDefinition getFieldDefinition(QueryDefinition queryDefinition, Element fieldElement);

}


