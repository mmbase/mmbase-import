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
import org.mmbase.bridge.util.xml.query.*;

/**
 * Defines options for a field to index.
 *
 * @author Pierre van Rooden
 * @version $Id$
 **/
public class IndexFieldDefinition extends FieldDefinition {

    /**
     * If <code>true</code>, the field's value is stored as a keyword.
     */
    public boolean keyWord = false;

    /**
     * The escaper to use when including the field's value.
     */
    public String escaper = null;

    /**
     * If <code>true</code>, the field's value is stored and can be returned
     * when search results are given.
     */
    public boolean storeText = false;

    /**
     * If not <code>null</code>, this is the fieldname under which the value is indexed.
     * Fieldnames with similar values are pooled together.
     */
    public String alias = null;

    /**
     * Password for unlocking the content of binary fields that may contain encrypted pdf documents.
     */
    public String decryptionPassword = "";

    // default for storing text
    private final boolean storeTextDefault;

    //d efault for merging text
    private final boolean mergeTextDefault;

    private final Set<String> allIndexedFieldsSet;

    public Indexer.Multiple multiple = Indexer.Multiple.ADD;

    public float boost = 1.0f;

    public String split = "";

    IndexFieldDefinition(boolean storeTextDefault, boolean mergeTextDefault, Set<String> allIndexedFieldsSet) {
        super();
        this.storeTextDefault = storeTextDefault;
        this.mergeTextDefault = mergeTextDefault;
        this.allIndexedFieldsSet = allIndexedFieldsSet;
    }

    @Override
    public void configure(Element fieldElement) {
        if (QueryReader.hasAttribute(fieldElement, "keyword")) {
            keyWord = "true".equals(QueryReader.getAttribute(fieldElement,"keyword"));
        } else {
            int type = Field.TYPE_UNKNOWN;
            if (stepField != null) type = stepField.getType();
            keyWord = (type == Field.TYPE_DATETIME) || (type == Field.TYPE_BOOLEAN) ||
                      (type == Field.TYPE_LONG) || (type == Field.TYPE_INTEGER) ||
                      (type == Field.TYPE_DOUBLE) || (type == Field.TYPE_FLOAT) ||
                      (type == Field.TYPE_NODE);
        }
        if (QueryReader.hasAttribute(fieldElement, "escape")) {
        	escaper = QueryReader.getAttribute(fieldElement, "escape");
        } else if (mergeTextDefault && !keyWord) {
        	escaper = null;
        }
        if (QueryReader.hasAttribute(fieldElement, "alias")) {
            alias = QueryReader.getAttribute(fieldElement, "alias");
        } else if (mergeTextDefault && !keyWord) {
            alias = "fulltext";
        }
        if (QueryReader.hasAttribute(fieldElement,"store")) {
            storeText = "true".equals(QueryReader.getAttribute(fieldElement, "store"));
        } else {
            storeText = !keyWord && storeTextDefault;
        }
        if (QueryReader.hasAttribute(fieldElement,"password")) {
            decryptionPassword = QueryReader.getAttribute(fieldElement, "password");
        }
        if (QueryReader.hasAttribute(fieldElement,"multiple")) {
            multiple = Indexer.Multiple.valueOf(QueryReader.getAttribute(fieldElement, "multiple").toUpperCase());
        }
        if (QueryReader.hasAttribute(fieldElement,"boost")) {
            boost = Float.valueOf(QueryReader.getAttribute(fieldElement, "boost"));
        }
        split = QueryReader.getAttribute(fieldElement, "split");

        if (!keyWord) {
            if (alias != null) {
                allIndexedFieldsSet.add(alias);
            } else {
                allIndexedFieldsSet.add(fieldName);
            }
        }
    }

    @Override
    public String toString() {
        return "IndexField" + allIndexedFieldsSet + (alias != null ? "(" + alias + ")" : "");
    }

}

