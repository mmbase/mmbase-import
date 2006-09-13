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
 * IndexConfigurer is a specialized QueryConfigurer, which has several extra options. Most
 * noticeably, it produces fields with a few new attributes.
 * 
 * @author Pierre van Rooden
 * @version $Id: IndexConfigurer.java,v 1.5 2006-09-13 09:51:14 michiel Exp $
 **/
public class IndexConfigurer extends QueryConfigurer {

    final Set<String> allIndexedFieldsSet;
    final boolean storeText;
    final boolean mergeText;

    IndexConfigurer(Set<String> allIndexedFieldsSet, boolean storeText, boolean mergeText) {
        this.allIndexedFieldsSet = allIndexedFieldsSet;
        this.storeText = storeText;
        this.mergeText = mergeText;
    }

    public QueryDefinition getQueryDefinition() {
        return new MMBaseIndexDefinition();
    }

    public FieldDefinition getFieldDefinition() {
        return new IndexFieldDefinition(storeText, mergeText, allIndexedFieldsSet);
    }

}


