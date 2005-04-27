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
 * @version $Id: QueryReader.java,v 1.3 2005-04-27 12:50:33 pierre Exp $
 **/
public class QueryReader {

    static protected void addField(Element fieldElement, QueryDefinition queryDefinition, QueryConfigurer configurer) {
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

    static protected void addConstraint(Element constraintElement, QueryDefinition queryDefinition) throws SearchQueryException {
        String fieldName = constraintElement.getAttribute("field");
        Object value = null;
        if (constraintElement.hasAttribute("value")) {
            if (constraintElement.hasAttribute("field2")) {
                throw new IllegalArgumentException("Can only have one of 'value' or 'field2'");
            }
            value = constraintElement.getAttribute("value");
        } else if (constraintElement.hasAttribute("field2")) {
            value = queryDefinition.query.createStepField(constraintElement.getAttribute("field2"));
        }
        int operator = FieldCompareConstraint.EQUAL;
        if (constraintElement.hasAttribute("operator")) {
            String sOperator = constraintElement.getAttribute("operator");
            operator = Queries.getOperator(sOperator);
        }
        int part = -1;
        if (constraintElement.hasAttribute("part")) {
            String sPart = constraintElement.getAttribute("part");
            part = Queries.getDateTimePart(sPart);
        }
        Object value2 = null;
        if (constraintElement.hasAttribute("value2")) {
            if (operator != Queries.OPERATOR_BETWEEN) {
                throw new IllegalArgumentException("Can only use 'value2' attribute with operator BETWEEN");
            }
            value2 = constraintElement.getAttribute("value2");
        }
        if (operator == Queries.OPERATOR_BETWEEN && value2 == null) {
            throw new IllegalArgumentException("Operator BETWEEN requires attribute 'value2'");
        }
        if (operator == Queries.OPERATOR_IN && (value instanceof String)) {
            value = Casting.toList(value);
        }
        boolean caseSensitive = false;
        if (constraintElement.hasAttribute("casesensitive")) {
            caseSensitive = "true".equals(constraintElement.getAttribute("casesensitive"));
        }
        Constraint constraint = Queries.createConstraint(queryDefinition.query, fieldName, operator, value, value2, caseSensitive, part);
        if (constraintElement.hasAttribute("inverse")) {
            queryDefinition.query.setInverse(constraint, "true".equals(constraintElement.getAttribute("inverse")));
        }
        Queries.addConstraint(queryDefinition.query, constraint);
    }

    static public QueryDefinition parseQuery(Element queryElement, QueryConfigurer configurer, Cloud cloud, String relateFrom) throws SearchQueryException {
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
            if (relateFrom != null) {
                path = relateFrom + "," + path;
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
            if (queryDefinition.fields == null) queryDefinition.fields = new ArrayList();
            NodeList childNodes = queryElement.getChildNodes();
            for (int k = 0; k < childNodes.getLength(); k++) {
                if (childNodes.item(k) instanceof Element) {
                    Element childElement = (Element) childNodes.item(k);
                    if ("field".equals(childElement.getTagName())) {
                        addField(childElement, queryDefinition, configurer);
                    } else if ("constraint".equals(childElement.getTagName())) {
                        addConstraint(childElement, queryDefinition);
                    }
                }
            }
            return queryDefinition;
        } else {
            throw new IllegalArgumentException("query has no 'path' or 'type' attribute");
        }
    }

}


