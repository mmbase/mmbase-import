/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;

import org.apache.lucene.document.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Sort;
import org.apache.lucene.queryParser.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: Searcher.java,v 1.1 2004-12-17 12:13:40 pierre Exp $
 **/
public class Searcher {

    private static final Logger log = Logging.getLoggerInstance(Searcher.class);

    private String index;
    private MMBase mmbase;
    private String[] allIndexedFields;
    private boolean mergeText = false;

    Searcher(String index, String[] allIndexedFields, boolean mergeText, MMBase mmbase) {
        this.index = index;
        this.allIndexedFields = allIndexedFields;
        this.mergeText = mergeText;
        this.mmbase = mmbase;
    }

    public List search(String value) {
        return search(value, null, null, new StopAnalyzer(), allIndexedFields);
    }

    public List search(String value, Filter filter, Sort sort, Analyzer analyzer, String[] fields) {
        MMObjectBuilder root = mmbase.getRootBuilder();
        List list = new LinkedList();
        try {
            IndexSearcher searcher = new IndexSearcher(index);
            Query query;
            if (mergeText) {
                query = QueryParser.parse(value,"fulltext",analyzer);
            } else {
                query = MultiFieldQueryParser.parse(value,fields,analyzer);
            }
            Hits hits = searcher.search(query);
            for (int i = 0; i < hits.length(); i++) {
                MMObjectNode node = root.getNode(hits.doc(i).get("number"));
                if (node != null) {
                    list.add(node);
                }
            }
        } catch (Exception e) {
            log.error("Cannot run search :"+e.getMessage());
        }
        return list;
    }

}
