/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.w3c.dom.*;

import org.mmbase.bridge.*;
import org.mmbase.module.lucene.query.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: IndexConfigurer.java,v 1.1 2005-04-21 14:28:43 pierre Exp $
 **/
public class IndexConfigurer implements QueryConfigurer {

    Set allIndexedFieldsSet = null;
    boolean storeText = false;
    boolean mergeText = false;

    IndexConfigurer(Set allIndexedFieldsSet, boolean storeText, boolean mergeText) {
        this.allIndexedFieldsSet = allIndexedFieldsSet;
        this.storeText = storeText;
        this.mergeText = mergeText;
    }

    public QueryDefinition getQueryDefinition(Element queryElement) {
        return new IndexDefinition(queryElement);
    }

    public FieldDefinition getFieldDefinition(QueryDefinition queryDefinition, Element fieldElement) {
        IndexFieldDefinition fieldDefinition = new IndexFieldDefinition(queryDefinition, fieldElement, storeText, mergeText);
        if (!fieldDefinition.keyWord) {
            if (fieldDefinition.alias != null) {
                allIndexedFieldsSet.add(fieldDefinition.alias);
            } else {
                allIndexedFieldsSet.add(fieldDefinition.fieldName);
            }
        }
        return fieldDefinition;
    }

}


