/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.io.IOException;
import java.util.*;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Sort;
import org.apache.lucene.queryParser.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: Searcher.java,v 1.3 2004-12-21 12:07:25 pierre Exp $
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
        return search(value, null, null, new StopAnalyzer(), null, allIndexedFields, 0, -1);
    }

    public List search(String value, int offset, int max) {
        return search(value, null, null, new StopAnalyzer(), null, allIndexedFields, offset, max);
    }

    public List search(String value, Query extraQuery, int offset, int max) {
        return search(value, null, null, new StopAnalyzer(), extraQuery, allIndexedFields, offset, max);
    }

    public List search(String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields, int offset, int max) {
        List list = new LinkedList();
        if (value!=null && !value.equals("")) {
            try {
                Hits hits = getHits(value, filter, sort, analyzer, extraQuery, fields);
                if (hits != null) {
                    MMObjectBuilder root = mmbase.getRootBuilder();
                    for (int i = offset; (i < offset+max || max < 0) && i < hits.length(); i++) {
                        MMObjectNode node = root.getNode(hits.doc(i).get("number"));
                        if (node != null) {
                            list.add(node);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Cannot run search :"+e.getMessage());
            }
        }
        return list;
    }

    public int searchSize(String value) {
        return searchSize(value, null, null, new StopAnalyzer(), null, allIndexedFields);
    }

    public int searchSize(String value, Query extraQuery) {
        return searchSize(value, null, null, new StopAnalyzer(), extraQuery, allIndexedFields);
    }

    public int searchSize(String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields) {
        if (value!=null && !value.equals("")) {
            try {
                Hits hits = getHits(value, filter, sort, analyzer, extraQuery, fields);
                return hits.length();
            } catch (Exception e) {
                log.error("Cannot run searchSize :"+e.getMessage());
            }
        }
        return 0;
    }

    protected Hits getHits(String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields) throws IOException, ParseException {
        IndexSearcher searcher = new IndexSearcher(index);
        Query query;
        if (mergeText) {
            query = QueryParser.parse(value,"fulltext",analyzer);
        } else {
            query = MultiFieldQueryParser.parse(value,fields,analyzer);
        }
        if (extraQuery != null) {
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(query, true, false);
            booleanQuery.add(extraQuery, true, false);
            query = booleanQuery;
        }
log.info("Final query:"+query);
        return searcher.search(query,filter,sort);
    }

    /**
     * Parses a constraint into a query.
     * Constraints are separated by whitespace and of the format:
     *
     *   field:[ EQ | GT | GTE | LT | LTE | NE ]:value[:value]
     */
    static public Query createQuery(String constraintsText) {
        Query query = null;
        StringTokenizer constraints = new StringTokenizer(constraintsText, "\t\n\r ", false);
        while (constraints.hasMoreTokens()) {
            String constraint = constraints.nextToken();
            StringTokenizer tokens = new StringTokenizer(constraint, ":", true);
            String field = tokens.nextToken();
            tokens.nextToken(); // colon
            String type = tokens.nextToken().toUpperCase();
            if (type.equals(":")) {
                type = "EQ";
            } else {
                tokens.nextToken(); // colon
            }
            String value = "";
            String value2 = "";
            if (type.equals("IN") || type.equals("INC")) {
                value += tokens.nextToken();
                tokens.nextToken(); // colon
                value2 += tokens.nextToken();
            } else {
                while (tokens.hasMoreTokens()) value += tokens.nextToken();
            }
            Query subQuery = null;
            if (type.equals("EQ") || type.equals("NE")) {
                subQuery = new TermQuery(new Term(field,value));
            } else if (type.equals("GT")|| type.equals("GTE")) {
                subQuery = new RangeQuery(new Term(field,value), null, type.equals("GTE"));
            } else if (type.equals("LT") || type.equals("LTE")) {
                subQuery = new RangeQuery(null, new Term(field,value), type.equals("LTE"));
            } else if (type.equals("IN") || type.equals("INC")) {
                subQuery = new RangeQuery(new Term(field,value), new Term(field,value2), type.equals("INC"));
            }
            if (subQuery !=null) {
                boolean required = !type.equals("NE");
                if (query == null) {
                    query = subQuery;
                } else {
                    BooleanQuery booleanQuery = new BooleanQuery();
                    booleanQuery.add(query, true, false);
                    booleanQuery.add(subQuery, required, !required);
                    query = booleanQuery;
                }
            }
        }
        return query;
    }

    class CombinedFilter extends Filter {

        static final int AND = 0;
        static final int OR  = 1;

        Filter filterOne;
        Filter filterTwo;
        int operation;

        CombinedFilter(Filter filterOne, Filter filterTwo, int operation) {
            this.filterOne = filterOne;
            this.filterTwo = filterTwo;
            this.operation = operation;
        }

        public BitSet bits(IndexReader reader) throws IOException {
            BitSet bitSet = filterOne.bits(reader);
            if (operation == AND) {
                bitSet.and(filterTwo.bits(reader));
            } else {
                bitSet.or(filterTwo.bits(reader));
            }
            return bitSet;
        }

    }

}
