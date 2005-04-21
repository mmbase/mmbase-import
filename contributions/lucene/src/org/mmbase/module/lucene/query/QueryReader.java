/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene.query;

import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: QueryReader.java,v 1.1 2005-04-21 14:28:43 pierre Exp $
 **/
public class QueryReader {

    static protected void addFields(NodeList fieldElements, QueryDefinition queryDefinition, QueryConfigurer configurer) {
        queryDefinition.fields = new ArrayList();
        for (int k = 0; k < fieldElements.getLength(); k++) {
            Element fieldElement = (Element) fieldElements.item(k);
            if (fieldElement.hasAttribute("name")) {
                FieldDefinition fieldDefinition = configurer.getFieldDefinition(queryDefinition, fieldElement);
                queryDefinition.fields.add(fieldDefinition);
                if (queryDefinition.isMultiLevel) {
                    // have to add field for multilevel queries
                    queryDefinition.query.addField(fieldDefinition.fieldName);
                }
            } else {
                 throw new IllegalArgumentException("field tag has no 'name' attribute");
            }
        }
    }

    static protected void addConstraints(NodeList constraintsElements, QueryDefinition queryDefinition) throws SearchQueryException {
        for (int k = 0; k < constraintsElements.getLength(); k++) {
            Element constraintElement = (Element) constraintsElements.item(k);
            String fieldName = constraintElement.getAttribute("field");
            Object value = constraintElement.getAttribute("value");
            int operator = FieldCompareConstraint.EQUAL;
            if (constraintElement.hasAttribute("operator")) {
                String sOperator = constraintElement.getAttribute("operator");
                operator = Queries.getOperator(sOperator);
            }
            Constraint constraint = Queries.createConstraint(queryDefinition.query, fieldName, operator, value, null, false, -1);
            if (constraintElement.hasAttribute("inverse")) {
                queryDefinition.query.setInverse(constraint, "true".equals(constraintElement.getAttribute("inverse")));
            }
            Queries.addConstraint(queryDefinition.query, constraint);
        }
    }

    static public QueryDefinition parseQuery(Element queryElement, QueryConfigurer configurer, Cloud cloud) throws SearchQueryException {
        if (queryElement.hasAttribute("type") || queryElement.hasAttribute("name") || queryElement.hasAttribute("path")) {

            String element = null;
            String path = null;
            String searchDirs = null;

            if (queryElement.hasAttribute("type")) {
                path = queryElement.getAttribute("type");
                element = path;
            } else if (queryElement.hasAttribute("name")) {
                path = queryElement.getAttribute("name");
                element = path;
            } else{
                path = queryElement.getAttribute("path");
                searchDirs = queryElement.getAttribute("searchdirs");
                if (queryElement.hasAttribute("element")) {
                  element = queryElement.getAttribute("element");
                } else {
                    List builders  = StringSplitter.split(path);
                    element = (String)builders.get(builders.size()-1);
                }
            }

            QueryDefinition queryDefinition = configurer.getQueryDefinition(queryElement);

            queryDefinition.isMultiLevel = !element.equals(path);
            queryDefinition.elementManager = cloud.getNodeManager(element);

            if (queryDefinition.isMultiLevel) {
                queryDefinition.query = cloud.createQuery();
                Queries.addPath(queryDefinition.query,path, searchDirs);
            } else {
                queryDefinition.query = queryDefinition.elementManager.createQuery();
            }

            addFields(queryElement.getElementsByTagName("field"), queryDefinition, configurer);
            addConstraints(queryElement.getElementsByTagName("constraint"), queryDefinition);
            return queryDefinition;
        } else {
            throw new IllegalArgumentException("constraints tag has no 'path' or 'type' attribute");
        }
    }

}


