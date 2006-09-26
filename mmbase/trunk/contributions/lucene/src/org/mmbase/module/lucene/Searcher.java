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
 * @version $Id: Searcher.java,v 1.28 2006-09-26 09:22:32 michiel Exp $
 * @todo  Should the StopAnalyzers be replaced by index.analyzer? Something else?
 **/
public class Searcher {
    private static final Logger log = Logging.getLoggerInstance(Searcher.class);

    // Search actions are logged on org.mmbase.lucene.SEARCH
    // So by configuring log4j, you can easily track what people are searching for.
    private final Logger searchLog;
    private final Indexer index;
    private final String[] allIndexedFields;
    private IndexSearcher searcher;

    /**
     * @param index The index where this Search is for
     */
    Searcher(Indexer index, String[] allIndexedFields) {
        this.index = index;
        searchLog = Logging.getLoggerInstance("org.mmbase.lucene.SEARCH." + index.getName());
        this.allIndexedFields = allIndexedFields;
        try {
            searcher = new IndexSearcher(index.getPath());
        } catch (IOException ioe) {
            log.error("Can't close index searcher: " + ioe.getMessage());
        }
    }

    protected IndexSearcher getSearcher() {
        if (searcher != null) {
            return searcher;
        } else {
            try {
                searcher = new IndexSearcher(index.getPath());
            } catch (IOException ioe) {
                log.error("Can't close index searcher: " + ioe.getMessage());
            }
            return searcher;
        }
    }

    public void shutdown() {
        if (searcher != null) {
            try {
                log.service("Shutting down searcher for " + index):
                searcher.close();
            } catch (IOException ioe) {
                log.error("Can't close index searcher: " + ioe.getMessage());
            }
        }
    }

    public NodeList search(Cloud cloud, String value) throws ParseException {
        return search(cloud, value, null, null, new StopAnalyzer(), null, allIndexedFields, 0, -1);
    }

    public NodeList search(Cloud cloud, String value, int offset, int max) throws ParseException {
        return search(cloud, value, null, null, new StopAnalyzer(), null, allIndexedFields, offset, max);
    }

    public NodeList search(Cloud cloud, String value, Query extraQuery, int offset, int max) throws ParseException {
        return search(cloud, value, null, null, new StopAnalyzer(), extraQuery, allIndexedFields, offset, max);
    }

    public NodeList search(Cloud cloud, String value, String[] sortFields, Query extraQuery, int offset, int max) throws ParseException {
        Sort sort = null;
        if (sortFields != null && sortFields.length > 0) {
            if (sortFields.length == 1 && sortFields[0].equals("RELEVANCE")) {
                sort = Sort.RELEVANCE;
            } else if (sortFields.length == 1 && sortFields[0].equals("INDEXORDER")) {
                sort = Sort.INDEXORDER;
            } else {
                SortField[] sorts = new SortField[sortFields.length];
                for (int i = 0; i < sortFields.length; i++) {
                    if (sortFields[i].startsWith("REVERSE:")) {
                        sorts[i] = new SortField(sortFields[i].substring("REVERSE:".length()), true);
                    } else {
                        sorts[i] = new SortField(sortFields[i]);
                    }
                }
                sort = new Sort(sorts);
            }
        }
        return search(cloud, value, null, sort, new StopAnalyzer(), extraQuery, allIndexedFields, offset, max);
    }


    public NodeList search(final Cloud cloud, String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields, int offset, int max) throws ParseException  {
        // log the value searched
        if (searchLog.isServiceEnabled()) {
            if (extraQuery != null && ! extraQuery.equals("")) {
                searchLog.service("(" + extraQuery + ") " + value);
            } else {
                searchLog.service(value);
            }
        }


        if (log.isTraceEnabled()) {
            log.trace("Searching '" + value + "' in index " + index + " for " + sort + " " + analyzer + " " + extraQuery + " " + fields + " " + offset + " " + max);
        }
        List<Node> list;
        if (value != null && !value.equals("")) {
            Hits hits;
            try {
                hits = getHits(value, filter, sort, analyzer, extraQuery, fields);
            } catch (java.io.IOException ioe) {
                log.warn(ioe);
                return org.mmbase.bridge.util.BridgeCollections.EMPTY_NODELIST;
            }
            if (log.isTraceEnabled()) {
                log.trace("hits " + hits + (hits != null ? "(" + hits.length() + " results)" : ""));
            }

            /// lazy loading of all that stuff!
            final HitIterator hi = (HitIterator) hits.iterator();
            list = new AbstractSequentialList<Node>() {
                List<Node> previous = new ArrayList();

                public int size() {
                    return hi.length();
                }
                public ListIterator<Node> listIterator(final int index) {
                    return new ListIterator<Node>() {
                        int j = 0;
                        {
                            while (j < index) { next();}
                        }
                        public Node next() {
                            try {
                                j++;
                                if (previous.size() >= j) {
                                    return previous.get(j - 1);
                                } else {
                                    Hit hit = (Hit) hi.next();
                                    Node node = Searcher.this.index.getNode(cloud, hit.getDocument());
                                    if (log.isDebugEnabled()) {
                                        log.debug("Found " + hit);
                                        log.debug("Found " + hit.getDocument());
                                        log.debug("Found " + node);
                                    }
                                    previous.add(node);
                                    return node;
                                }
                            } catch (IOException ioe) {
                                log.error(ioe);
                                return null;
                            }
                        }
                        public boolean hasNext() {
                            return hi.hasNext();
                        }
                        public Node previous() {
                            return previous.get(--j);
                        }
                        public boolean hasPrevious() {
                            return j > 0;
                        }
                        public void add(Node node) {
                            throw new UnsupportedOperationException();
                        }
                        public void set(Node node) {
                            throw new UnsupportedOperationException();
                        }
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                        public int nextIndex() {
                            return j;
                        }
                        public int previousIndex() {
                            return j - 1;
                        }
                    };
                }
            };
        } else {
            list = Collections.EMPTY_LIST;
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
        if (value == null || "".equals(value)) {
            IndexReader reader = null;
            try {
                reader = IndexReader.open(index.getPath());
                return reader.numDocs();
            } catch (IOException ioe) {
                log.warn(ioe);
                return 0;
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ioe) {};
                }
            }
        }
        try {
            Hits hits = getHits(value, filter, sort, analyzer, extraQuery, fields);
            return hits.length();
        } catch (ParseException pe) {
            log.error(pe);
            return 0;
        } catch (IOException ioe) {
            log.error(ioe);
            return 0;
        }
    }

    protected Hits getHits(String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields) throws IOException, ParseException {
        if (analyzer == null) analyzer = index.getAnalyzer();
        Query query;

        if (fields == null || fields.length == 0) {
            QueryParser qp = new QueryParser("fulltext", analyzer);
            query = qp.parse(value);
        } else if (fields.length == 0) {
            QueryParser qp = new QueryParser(fields[0], analyzer);
            query = qp.parse(value);
        } else {
            MultiFieldQueryParser qp = new MultiFieldQueryParser(fields, analyzer);
            query = qp.parse(value);
        }
        if (extraQuery != null) {
            if (log.isDebugEnabled()) {
                log.debug("Found an extra query " + extraQuery + " for " + query + " " + BooleanQuery.getMaxClauseCount());
            }
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(query, BooleanClause.Occur.MUST);
            booleanQuery.add(extraQuery, BooleanClause.Occur.MUST);
            query = booleanQuery;
        }
        return getSearcher().search(query, filter, sort);
    }

    /**
     * Parses a constraint into a query.
     * Constraints are separated by whitespace and of the format:
     *
     *   field:[ EQ | GT | GTE | LT | LTE | NE ]:value[:value]
     */
    static public Query createQuery(String constraintsText) {
        if (constraintsText == null) return null;
        constraintsText = constraintsText.trim();
        if ("".equals(constraintsText)) return null;
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
                subQuery = new RangeQuery(new Term(field, value), null, type.equals("GTE"));
            } else if (type.equals("LT") || type.equals("LTE")) {
                if (log.isDebugEnabled()) {
                    log.debug("Instantatiating rangquery NULL->" + value);
                }
                subQuery = new RangeQuery(null, new Term(field, value), type.equals("LTE"));
            } else if (type.equals("IN") || type.equals("INC")) {
                subQuery = new RangeQuery(new Term(field, value), new Term(field, value2), type.equals("INC"));
            } else {
                throw new RuntimeException("Unknown operator '" + type + "'");
            }
            if (subQuery !=null) {
                if (query == null) {
                    if (type.equals("NE")) {
                        throw new RuntimeException("The operator NE cannot be used first");
                    }
                    query = subQuery;
                } else {
                    BooleanQuery booleanQuery = new BooleanQuery();
                    booleanQuery.add(query, BooleanClause.Occur.MUST);
                    BooleanClause.Occur occur = type.equals("NE") ? BooleanClause.Occur.MUST_NOT : BooleanClause.Occur.MUST;
                    // no support for 'SHOULD'.
                    booleanQuery.add(subQuery, occur);
                    query = booleanQuery;
                }
            }
        }
        return query;
    }


}
