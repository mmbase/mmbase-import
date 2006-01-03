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
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Node;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * A wrapper around Lucene's {@link org.apache.lucene.search.IndexSearcher}. Every {@link Indexer} has its own Searcher.
 *
 * @author Pierre van Rooden
 * @version $Id: Searcher.java,v 1.15 2006-01-03 13:27:46 michiel Exp $
 * @TODO  Should the StopAnalyzers be replaced by index.analyzer? Something else?
 **/
public class Searcher {
    private static final Logger log = Logging.getLoggerInstance(Searcher.class);

    // Search actions are logged on org.mmbase.lucene.SEARCH
    // So by configuring log4j, you can easily track what people are searching for.
    private static final Logger searchLog = Logging.getLoggerInstance("org.mmbase.lucene.SEARCH");

    private final Indexer index;
    private final String[] allIndexedFields;

    /**
     * @param index The index where this Search is for
     */
    Searcher(Indexer index, String[] allIndexedFields) {
        this.index = index;
        this.allIndexedFields = allIndexedFields;
    }

    public NodeList search(Cloud cloud, String value) {
        return search(cloud, value, null, null, new StopAnalyzer(), null, allIndexedFields, 0, -1);
    }

    public NodeList search(Cloud cloud, String value, int offset, int max) {
        return search(cloud, value, null, null, new StopAnalyzer(), null, allIndexedFields, offset, max);
    }

    public NodeList search(Cloud cloud, String value, Query extraQuery, int offset, int max) {
        return search(cloud, value, null, null, new StopAnalyzer(), extraQuery, allIndexedFields, offset, max);
    }

    public NodeList search(Cloud cloud, String value, String[] sortFields, Query extraQuery, int offset, int max) {
        Sort sort = null;
        if (sortFields != null && sortFields.length > 0) {
            if (sortFields.length == 1 && sortFields[0].equals("RELEVANCE")) {
                sort = null;
            } else if (sortFields.length == 1 && sortFields[0].equals("INDEXORDER")) {
                sort = Sort.INDEXORDER;
            } else {
                sort = new Sort(sortFields);
            }
        }
        return search(cloud, value, null, sort, new StopAnalyzer(), extraQuery, allIndexedFields, offset, max);
    }


    public NodeList search(Cloud cloud, String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields, int offset, int max) {
        // log the value searched
        searchLog.service(value);
        List list = new LinkedList(); 
        if (log.isDebugEnabled()) {
            log.trace("Searching '" + value + "' in index " + index + " for " + sort + " " + analyzer + " " + extraQuery + " " + fields + " " + offset + " " + max);
        }
        if (value != null && !value.equals("")) {
            IndexSearcher searcher = null;
            try {
                searcher = new IndexSearcher(index.getPath());
                Hits hits = getHits(searcher, value, filter, sort, analyzer, extraQuery, fields);
                log.trace("hits " + hits);
                if (hits != null) {
                    log.trace("Found " + hits.length() + " results");
                    for (int i = offset; (i < offset + max || max < 0) && i < hits.length(); i++) {
                        String hit = hits.doc(i).get("number");
                        log.trace("Found " + hit);
                        list.add(index.getNode(cloud, hit));
                    }
                }
            } catch (Exception e) {
                log.error("Cannot run search: " + e.getMessage(), e);
            } finally {
                if (searcher != null) {
                    try {
                        searcher.close();
                    } catch (IOException ioe) {
                        log.error("Can't close index searcher: " + ioe.getMessage());
                    }
                }
            }
        }
        return new org.mmbase.bridge.util.CollectionNodeList(list, cloud);
    }

    public int searchSize(Cloud cloud, String value) {
        return searchSize(cloud, value, null, null, new StopAnalyzer(), null, allIndexedFields);
    }

    public int searchSize(Cloud cloud, String value, Query extraQuery) {
        return searchSize(cloud, value, null, null, new StopAnalyzer(), extraQuery, allIndexedFields);
    }

    public int searchSize(Cloud cloud, String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields) {
        IndexReader reader = null;
        IndexSearcher searcher = null;
        try {
            if (value == null || "".equals(value)) {
                reader = IndexReader.open(index.getPath());
                return reader.numDocs();
            }
            searcher = new IndexSearcher(index.getPath());
            Hits hits = getHits(searcher, value, filter, sort, analyzer, extraQuery, fields);
            return hits.length();
        } catch (Exception e) {
            log.error("Cannot run searchSize :" + e.getMessage());
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException ioe) {
                    log.error("Can't close index searcher: " + ioe.getMessage());
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    log.error("Can't close index reader: " + ioe.getMessage());
                }
            }
        }
        return -1;
    }

    protected Hits getHits(IndexSearcher searcher, String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields) throws IOException, ParseException {
        if (analyzer == null) analyzer = index.getAnalyzer();
        Query query;
        if (fields == null || fields.length == 0) {
            query = QueryParser.parse(value, "fulltext", analyzer);
        } else if (fields.length == 0) {
            query = QueryParser.parse(value, fields[0], analyzer);
        } else {
            query = MultiFieldQueryParser.parse(value, fields, analyzer);
        }
        if (extraQuery != null) {
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(query, true, false);
            booleanQuery.add(extraQuery, true, false);
            query = booleanQuery;
        }
        return searcher.search(query, filter, sort);
    }

    /**
     * Parses a constraint into a query.
     * Constraints are separated by whitespace and of the format:
     *
     *   field:[ EQ | GT | GTE | LT | LTE | NE ]:value[:value]
     */
    static public Query createQuery(String constraintsText) {
        if (constraintsText == null) return null;
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
                subQuery = new TermQuery(new Term(field, value));
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
