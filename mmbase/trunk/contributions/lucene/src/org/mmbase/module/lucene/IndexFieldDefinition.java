/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.Collection;
import org.w3c.dom.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.xml.query.*;
import org.mmbase.storage.search.*;

/**
 * Defines options for a field to index.
 *
 * @author Pierre van Rooden
 * @version $Id: IndexFieldDefinition.java,v 1.2 2005-07-27 13:59:58 pierre Exp $
 **/
public class IndexFieldDefinition extends FieldDefinition {

    /**
     * If <code>true</code>, the field's value is stored as a keyword.
     */
    public boolean keyWord = false;

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
    private boolean storeTextDefault = false;

    //d efault for merging text
    private boolean mergeTextDefault = false;

    public IndexFieldDefinition(QueryConfigurer configurer, QueryDefinition queryDefinition, boolean storeTextDefault, boolean mergeTextDefault) {
        super(configurer, queryDefinition);
        this.storeTextDefault = storeTextDefault;
        this.mergeTextDefault = mergeTextDefault;
    }

    public void configure(Element fieldElement) {
        if (QueryReader.hasAttribute(fieldElement,"keyword")) {
            keyWord = "true".equals(QueryReader.getAttribute(fieldElement,"keyword"));
        } else {
            int type = Field.TYPE_UNKNOWN;
            if (stepField != null) type = stepField.getType();
            keyWord = (type == Field.TYPE_DATETIME) || (type == Field.TYPE_BOOLEAN) ||
                      (type == Field.TYPE_LONG) || (type == Field.TYPE_INTEGER) ||
                      (type == Field.TYPE_DOUBLE) || (type == Field.TYPE_FLOAT) ||
                      (type == Field.TYPE_NODE);
        }
        if (QueryReader.hasAttribute(fieldElement,"alias")) {
            alias = QueryReader.getAttribute(fieldElement,"alias");
        } else if (mergeTextDefault && !keyWord) {
            alias = "fulltext";
        }
        if (QueryReader.hasAttribute(fieldElement,"store")) {
            storeText = "true".equals(QueryReader.getAttribute(fieldElement,"store"));
        } else {
            storeText = !keyWord && storeTextDefault;
        }
        if (QueryReader.hasAttribute(fieldElement,"password")) {
            decryptionPassword = QueryReader.getAttribute(fieldElement,"password");
        }
        if (!keyWord) {
            if (alias != null) {
                ((IndexConfigurer)configurer).allIndexedFieldsSet.add(alias);
            } else {
                ((IndexConfigurer)configurer).allIndexedFieldsSet.add(fieldName);
            }
        }
    }

}

