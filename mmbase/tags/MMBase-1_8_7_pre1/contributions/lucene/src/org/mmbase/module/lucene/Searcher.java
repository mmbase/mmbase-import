/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.io.*;
import java.util.*;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Filter.*;
import org.apache.lucene.search.Sort;
import org.apache.lucene.queryParser.*;

import org.mmbase.core.event.EventManager;
import org.mmbase.util.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.util.AnnotatedNode;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * A wrapper around Lucene's {@link org.apache.lucene.search.IndexSearcher}. Every {@link Indexer} has its own Searcher.
 *
 * @author Pierre van Rooden
 * @version $Id: Searcher.java,v 1.55 2009-02-19 10:22:01 michiel Exp $
 * @todo  Should the StopAnalyzers be replaced by index.analyzer? Something else?
 **/
public class Searcher implements NewSearcher.Listener, FullIndexEvents.Listener {
    private static final Logger log = Logging.getLoggerInstance(Searcher.class);


    // Search actions are logged on org.mmbase.lucene.SEARCH
    // So by configuring log4j, you can easily track what people are searching for.
    private final Logger searchLog;
    private final Indexer index;
    final String[] allIndexedFields;

    // for peformance reasons there should only be one searcher opened (see javadoc of IndexSearcher)
    private IndexSearcher searcher;

    private long producedNodes = 0;
    private boolean needsNewSearcher = false;

    private final Timer timer = new Timer();
    private int closingSearchers = 0;

    private FullIndexEvents.Status status = FullIndexEvents.Status.IDLE;
    private int intermediateSize = -1;

    /**
     * @param index The index where this Search is for
     */
    Searcher(Indexer index, String[] allIndexedFields) {
        this.index = index;
        searchLog = Logging.getLoggerInstance("org.mmbase.lucene.SEARCH." + index.getName());
        this.allIndexedFields = allIndexedFields;
        EventManager.getInstance().addEventListener(this);
    }

    public void notify(NewSearcher.Event event) {
        if (event.getIndex().equals(index.getName())) {
            log.debug("Received " + event);
            needsNewSearcher = true;
        }
    }
    public void notify(FullIndexEvents.Event event) {
        if (event.getIndex().equals(index.getName())) {
            log.debug("Received " + event);
            status = event.getStatus();
            intermediateSize = event.getIndexed();
        }
    }
    public int getFullIndexSize() {
        return status != FullIndexEvents.Status.IDLE ? intermediateSize : -1;
    }


    protected synchronized IndexSearcher getSearcher(boolean copy) throws IOException {
        if (copy) return  new IndexSearcher(index.getDirectoryForFullIndex());
        if (searcher != null && needsNewSearcher) {
            // for existing searches, leave existing searcher open for 10 seconds, then close it (searches still not finished in 10 seconds, get an IO exception)
            closingSearchers++;
            final IndexSearcher s = searcher;
            searcher = null;
            timer.schedule(new TimerTask() {
                    public void run() {
                        try {
                            log.debug("Shutting down a searcher for " + index);
                            s.close();
                        } catch (IOException ioe) {
                            log.error("Can't close index searcher: " + ioe.getMessage());
                        } finally {
                            closingSearchers--;
                        }
                    }
                }, 10000);
        }
        if (searcher == null) {
            try {
                searcher = new IndexSearcher(index.getDirectory());
                needsNewSearcher = false;
                return searcher;
            } catch (CorruptIndexException ci) {
                index.addError(ci.getMessage());
                index.repare(ci, copy);
                throw ci;
            }
        } else {
            return searcher;
        }
    }

    public void shutdown() {
        EventManager.getInstance().removeEventListener(this);
        if (searcher != null) {
            try {
                log.service("Shutting down searcher for " + index.getName());
                searcher.close();
                searcher = null;
            } catch (IOException ioe) {
                log.error("Can't close index searcher: " + ioe.getMessage());
            }
        }
        while (closingSearchers > 0) {
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException ie) {
                break;
            }
        }
        timer.cancel();
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
    public NodeList search(Cloud cloud, String value, Filter filter, Query extraQuery, int offset, int max) throws ParseException {
        return search(cloud, value, filter, null, new StopAnalyzer(), extraQuery, allIndexedFields, offset, max);
    }

    public NodeList search(Cloud cloud, String value, String[] sortFields, Query extraQuery, int offset, int max) throws ParseException {
        return search(cloud, value, null, getSort(sortFields), new StopAnalyzer(), extraQuery, allIndexedFields, offset, max);
    }

    public static Sort getSort(String... sortFields) {
        Sort sort = null;
        if (sortFields != null && sortFields.length > 0) {
            if (sortFields.length == 0 || (sortFields.length == 1 &&
                                           (sortFields[0].equals("RELEVANCE") || sortFields[0].equals("")))){
                log.debug("implicitely sorting on RELEVANCE");
                sort = Sort.RELEVANCE;
            } else if (sortFields.length == 1 && sortFields[0].equals("INDEXORDER")) {
                sort = Sort.INDEXORDER;
            } else {
                SortField[] sorts = new SortField[sortFields.length];
                for (int i = 0; i < sortFields.length; i++) {
                    boolean reverse = false;
                    String sortField = sortFields[i];
                    if (sortField.startsWith("REVERSE:")) {
                        sortField = sortField.substring(8);
                        reverse = true;
                    }
                    int fieldType = SortField.AUTO;
                    if (sortField.startsWith("INT:")) {
                        sortField =sortField.substring(4);
                        fieldType =  SortField.INT;
                    } else if (sortField.startsWith("FLOAT:")) {
                        sortField = sortField.substring(6);
                        fieldType =  SortField.FLOAT;
                    } else if (sortField.startsWith("STRING:")) {
                        sortField = sortField.substring(7);
                        fieldType =  SortField.STRING;
                    }
                    sorts[i] = new SortField(sortField, fieldType, reverse);
                }
                sort = new Sort(sorts);
            }
        }
        return sort;
    }


    public List<AnnotatedNode> searchAnnotated(final Cloud cloud, String value, Filter filter, Sort sort,
                                               Analyzer analyzer, Query extraQuery, String[] fields, final int offset, final int max) throws ParseException  {
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
        List<AnnotatedNode> list;
        if (value != null && !value.equals("")) {
            final Hits hits;
            try {
                hits = getHits(value, filter, sort, analyzer, extraQuery, fields, false);
            } catch (java.io.FileNotFoundException fnfe) {
                log.warn(fnfe + " returning empty list");
                needsNewSearcher = true;
                return Collections.emptyList();
            } catch (java.io.IOException ioe) {
                log.warn(ioe + " returning empty list", ioe);
                needsNewSearcher = true;
                return Collections.emptyList();
            }
            if (log.isTraceEnabled()) {
                log.trace("hits " + hits + (hits != null ? " (" + hits.length() + " results)" : ""));
            }

            /// lazy loading of all that stuff!
            list = new AbstractList<AnnotatedNode>() {
                private int size = -1;

                public int size() {
                    if (size == -1) {
                        int h = hits.length() - offset;
                        if (h < 0) h = 0;
                        size = (max > 0 && max < h ? max : h);
                    }
                    return size;

                }
                public AnnotatedNode get(int i) {
                    try {
                        Document doc = hits.doc(i + offset);
                        Node node = Searcher.this.index.getNode(cloud, doc);
                        if (log.isDebugEnabled()) {
                            log.debug("Found " + doc);
                            log.debug("Found " + node);
                            log.trace("Because " + Logging.stackTrace(10));
                        }
                        Searcher.this.producedNodes++;
                        AnnotatedNode anode = new AnnotatedNode(node);
                        anode.putAnnotation("score", hits.score(i + offset));
                        return anode;
                    } catch (IOException ioe) {
                        log.error(ioe);
                        return null;
                    }
                }
            };
        } else {
            list = Collections.emptyList();
        }
        return list;

    }

    public NodeList search(final Cloud cloud, String value, Filter filter, Sort sort,
                           Analyzer analyzer, Query extraQuery, String[] fields, final int offset, final int max) throws ParseException  {
        return new org.mmbase.bridge.util.CollectionNodeList(searchAnnotated(cloud, value, filter, sort, analyzer, extraQuery, fields, offset, max), cloud);
    }

    public int searchSize(Cloud cloud, String value) {
        return searchSize(cloud, value, null, new StopAnalyzer(), null, allIndexedFields, false);
    }

    public int searchSize(Cloud cloud, String value, Query extraQuery) {
        return searchSize(cloud, value, null, new StopAnalyzer(), extraQuery, allIndexedFields, false);
    }

    public int searchSize(Cloud cloud, String value, Filter filter, Analyzer analyzer, Query extraQuery, String[] fields, boolean copy) {
        if (value == null || "".equals(value)) {
            IndexReader reader = null;
            try {
                reader = IndexReader.open(copy ? index.getDirectoryForFullIndex() : index.getDirectory());
                return reader.numDocs();
            } catch ( FileNotFoundException nfe) {
                log.debug(nfe + " returning -1");
                return -1;
            } catch (CorruptIndexException ci) {
                index.addError(ci.getMessage());
                index.repare(ci, copy);
                log.warn(ci + " returning -1");
                return -1;
            } catch (IOException ioe) {
                log.service(ioe + " returning -1");
                return -1;
            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ioe) {};
                }
            }
        }
        try {
            Hits hits = getHits(value, filter, null, analyzer, extraQuery, fields, copy);
            return hits.length();
        } catch (ParseException pe) {
            log.error(pe);
            return 0;
        } catch (IOException ioe) {
            log.error(ioe);
            return 0;
        }
    }

    protected Hits getHits(String value, Filter filter, Sort sort, Analyzer analyzer, Query extraQuery, String[] fields, boolean copy) throws IOException, ParseException {
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
            log.debug("Parsing with " + fields + " " + analyzer + " " + value + " -> " + query);
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
        IndexSearcher s = getSearcher(copy);
        if (s == null) throw new IOException("No IndexSearcher found for " + this);
        return s.search(query, filter, sort);
    }

    static final String QUERY_SYNTAX =
        "&lt;field&gt;:[ | EQ | GT | GTE | LT | LTE | NE]:&lt;value&gt; | " +
        "&lt;field&gt;:[IN | INC]:&lt;value1&gt;:&lt;value2&gt;";

    static final String FILTER_SYNTAX =
        QUERY_SYNTAX + " |  &lt;field&gt;:[IN_SET | NIN_SET]:&lt;value&gt;[,&lt;value2&gt;[,..]]";


    static public Filter createFilter(String constraintsText) {
        if (constraintsText == null || "".equals(constraintsText)) return null;

        if (constraintsText == null) return null;
        constraintsText = constraintsText.trim();
        if ("".equals(constraintsText)) return null;
        Filter filter = null;
        StringTokenizer constraints = new StringTokenizer(constraintsText, "\t\n\r ", false);
        while (constraints.hasMoreTokens()) {
            String constraint = constraints.nextToken();
            StringTokenizer tokens = new StringTokenizer(constraint, ":", true);
            if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + FILTER_SYNTAX);
            String field = tokens.nextToken();
            if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + FILTER_SYNTAX);
            tokens.nextToken(); // colon
            if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + FILTER_SYNTAX);
            String type = tokens.nextToken().toUpperCase();
            if (type.equals(":")) {
                type = "EQ";
            } else {
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + FILTER_SYNTAX);
                tokens.nextToken(); // colon
            }
            String value = ""; // should use stringbuffer?
            String value2 = "";
            if (type.equals("IN") || type.equals("INC")) {
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("For IN and INC operators you need more values. In constraint '"  + constraint + "'");
                value += tokens.nextToken();
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("Missing colon in '" + constraint + "'");
                tokens.nextToken(); // colon
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("For IN and INC operators you need more values. In constraint '" + constraint + "'");
                value2 += tokens.nextToken();
            } else {
                // eh, should there no be appended comma's or spaces or so?
                while (tokens.hasMoreTokens()) value += tokens.nextToken();
            }
            Filter subFilter = null;
            if (type.equals("IN_SET") || type.equals("NIN_SET")) {
                subFilter = new TermsFilter();
                for (String v: value.split(",")) {
                    ((TermsFilter)subFilter).addTerm(new Term(field, v));
                }
            } else if (type.equals("EQ") || type.equals("NE")) {
                subFilter = new TermsFilter();
                ((TermsFilter)subFilter).addTerm(new Term(field, value));
            } else if (type.equals("GT")|| type.equals("GTE")) {
                subFilter = new RangeFilter(field, value, null, type.equals("GTE"), false);
            } else if (type.equals("LT") || type.equals("LTE")) {
                if (log.isDebugEnabled()) {
                    log.debug("Instantatiating rangfilter NULL->" + value);
                }
                subFilter = new RangeFilter(field, null,value, false, type.equals("LTE"));
            } else if (type.equals("IN") || type.equals("INC")) {
                subFilter = new RangeFilter(field, value, value2, type.equals("INC"), type.equals("INC"));
            } else {
                throw new IllegalArgumentException("Unknown operator '" + type + "'");
            }
            if (subFilter !=null) {
                if (filter == null) {
                    if (type.equals("NE") || type.equals("NIN_SET")) {
                        BooleanFilter booleanFilter = new BooleanFilter();
                        booleanFilter.add(new FilterClause(filter, BooleanClause.Occur.MUST_NOT));
                        filter = booleanFilter;
                    } else {
                      filter = subFilter;
            }
                } else {
                    BooleanFilter booleanFilter = new BooleanFilter();
                    booleanFilter.add(new FilterClause(filter, BooleanClause.Occur.MUST));
                    BooleanClause.Occur occur =
                        (type.equals("NE") ||
                         type.equals("NIN_SET"))
                        ? BooleanClause.Occur.MUST_NOT : BooleanClause.Occur.MUST;
                    // no support for 'SHOULD'.
                    booleanFilter.add(new FilterClause(subFilter, occur));
                    filter = booleanFilter;
                }
            }
        }
        return new CachingWrapperFilter(filter);
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
            if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + QUERY_SYNTAX);

            String field = tokens.nextToken();
            if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + QUERY_SYNTAX);
            tokens.nextToken(); // colon
            if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + QUERY_SYNTAX);
            String type = tokens.nextToken().toUpperCase();
            if (type.equals(":")) {
                type = "EQ";
            } else {
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("The constraint '" + constraint + "' is not of the form " + QUERY_SYNTAX);
                tokens.nextToken(); // colon
            }
            String value = ""; // should use stringbuffer?
            String value2 = "";
            if (type.equals("IN") || type.equals("INC")) {
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("For IN and INC operators you need more values. In constraint '"  + constraint + "'");
                value += tokens.nextToken();
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("Missing colon in '" + constraint + "'");
                tokens.nextToken(); // colon
                if (! tokens.hasMoreTokens()) throw new IllegalArgumentException("For IN and INC operators you need more values. In constraint '" + constraint + "'");
                value2 += tokens.nextToken();
            } else {
                // eh, should there no be appended comma's or spaces or so?
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
                throw new IllegalArgumentException("Unknown operator '" + type + "'");
            }
            if (subQuery !=null) {
                if (query == null) {
                    if (type.equals("NE")) {
                        throw new IllegalArgumentException("The operator NE cannot be used first");
                    }
                    query = subQuery;
                } else {
                    BooleanQuery booleanQuery = new BooleanQuery();
                    booleanQuery.add(query, BooleanClause.Occur.MUST);
                    BooleanClause.Occur occur =
                        type.equals("NE")
                         ? BooleanClause.Occur.MUST_NOT : BooleanClause.Occur.MUST;
                    // no support for 'SHOULD'.
                    booleanQuery.add(subQuery, occur);
                    query = booleanQuery;
                }
            }
        }
        return query;
    }
    /**
     * @since MMBase-1.9
     */
    public long getNumberOfProducedNodes() {
        return producedNodes;
    }

    public String toString() {
        return "searcher[" + index.getName() + "]";
    }

}
