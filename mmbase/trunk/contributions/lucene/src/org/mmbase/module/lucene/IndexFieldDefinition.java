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
import org.mmbase.module.lucene.query.*;
import org.mmbase.storage.search.*;

/**
 * Defines options for a field to index.
 *
 * @author Pierre van Rooden
 * @version $Id: IndexFieldDefinition.java,v 1.1 2005-04-21 14:28:43 pierre Exp $
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

    public IndexFieldDefinition(QueryDefinition queryDefinition, Element fieldElement,
                                boolean storeTextDefault, boolean mergeTextDefault) {
        super(queryDefinition, fieldElement);
        if (fieldElement.hasAttribute("keyword")) {
            keyWord = "true".equals(fieldElement.getAttribute("keyword"));
        } else {
            int type = Field.TYPE_UNKNOWN;
            if (stepField != null) type = stepField.getType();
            keyWord = (type == Field.TYPE_DATETIME) || (type == Field.TYPE_BOOLEAN) ||
                      (type == Field.TYPE_LONG) || (type == Field.TYPE_INTEGER) ||
                      (type == Field.TYPE_DOUBLE) || (type == Field.TYPE_FLOAT) ||
                      (type == Field.TYPE_NODE);
        }
        if (fieldElement.hasAttribute("alias")) {
            alias = fieldElement.getAttribute("alias");
        } else if (mergeTextDefault && !keyWord) {
            alias = "fulltext";
        }
        if (fieldElement.hasAttribute("store")) {
            storeText = "true".equals(fieldElement.getAttribute("store"));
        } else {
            storeText = !keyWord && storeTextDefault;
        }
        if (fieldElement.hasAttribute("password")) {
            decryptionPassword = fieldElement.getAttribute("password");
        }
    }

}

