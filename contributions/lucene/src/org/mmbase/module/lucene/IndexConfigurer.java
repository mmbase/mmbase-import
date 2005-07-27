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
 *
 * @author Pierre van Rooden
 * @version $Id: IndexConfigurer.java,v 1.2 2005-07-27 13:59:58 pierre Exp $
 **/
public class IndexConfigurer extends QueryConfigurer {

    Set allIndexedFieldsSet = null;
    boolean storeText = false;
    boolean mergeText = false;

    IndexConfigurer(Set allIndexedFieldsSet, boolean storeText, boolean mergeText) {
        this.allIndexedFieldsSet = allIndexedFieldsSet;
        this.storeText = storeText;
        this.mergeText = mergeText;
    }

    public QueryDefinition getQueryDefinition() {
        return new IndexDefinition(this);
    }

    public FieldDefinition getFieldDefinition(QueryDefinition queryDefinition) {
        return new IndexFieldDefinition(this, queryDefinition, storeText, mergeText);
    }

}


