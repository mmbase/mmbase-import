/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.io.IOException;
import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

import org.w3c.dom.*;
import org.mmbase.util.*;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.util.*;
import org.mmbase.storage.implementation.database.GenericDataSource;
import org.mmbase.cache.Cache;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.Analyzer;


import org.mmbase.util.logging.*;

/**
 * If for some reason you also need to do Queries next to MMBase.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public class JdbcIndexDefinition implements IndexDefinition {

    static private final Logger log = Logging.getLoggerInstance(JdbcIndexDefinition.class);

    private static int directConnections = 0;

    private static final int CACHE_SIZE = 10 * 1024;
    protected static Cache<String, LazyMap> nodeCache = new Cache<String, LazyMap>(CACHE_SIZE) {
            {
                putCache();
            }

        @Override
            public final String getName() {
                return "LuceneJdbcNodes";
            }
        @Override
            public final String getDescription() {
                return "Node identifier -> Map";
            }
        };

    private final DataSource dataSource;
    private final String key;
    private final String identifier;
    private final String indexSql;
    private final String findSql;
    private final Analyzer analyzer;

    private final Set<String> keyWords    = new HashSet<String>();
    private final Map<String, Indexer.Multiple> nonDefaultMultiples = new HashMap<String, Indexer.Multiple>();
    private final Map<String, Float> boosts = new HashMap<String, Float>();

    private final Collection<IndexDefinition> subQueries = new ArrayList<IndexDefinition>();

    private final boolean isSub;

    private String id;

    JdbcIndexDefinition(DataSource ds,
                        Element element,
                        Set allIndexedFields,
                        boolean storeText,
                        boolean mergeText,
                        Analyzer a,
                        boolean isSub) {
        this.dataSource = ds;
        indexSql = element.getAttribute("sql");
        key = element.getAttribute("key");
        String elementId = element.getAttribute("identifier");
        identifier = "".equals(elementId) ? key : elementId;
        findSql = element.getAttribute("find");
        NodeList childNodes = element.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if ("field".equals(childElement.getLocalName())) {
                    if (childElement.getAttribute("keyword").equals("true")) {
                        keyWords.add(childElement.getAttribute("name"));
                    }
                    String m = childElement.getAttribute("multiple");
                    if ("".equals(m)) m = "add";
                    if (! m.equals("add")) {
                        nonDefaultMultiples.put(childElement.getAttribute("name"),  Indexer.Multiple.valueOf(m.toUpperCase()));
                    }
                    String b = childElement.getAttribute("boost");
                    if (! b.equals("")) {
                        boosts.put(childElement.getAttribute("name"), Float.valueOf(b));
                    }
                } else if ("related".equals(childElement.getLocalName())) {
                    subQueries.add(new JdbcIndexDefinition(ds, childElement, allIndexedFields, storeText, mergeText, a, true));
                }
            }
        }
        this.analyzer = a;
        this.isSub = isSub;
        assert ! isSub  || "".equals(findSql);
    }

    public void setId(String i) {
        id = i;
    }
    public String getId() {
        return id;
    }

    /**
     * Jdbc connection pooling of MMBase would kill the statement if too duratious. This produces a
     * 'direct connection' in that case, to circumvent that problem (Indexing queries _may_ take a while).
     */
    protected Connection getDirectConnection() throws SQLException {
        directConnections++;
        try {
            if (dataSource instanceof GenericDataSource) {
                return ((GenericDataSource) dataSource).getDirectConnection();
            } else {
                return dataSource.getConnection();
            }
        } catch (SQLException sqe) {
            log.error("With direct connection #" + directConnections + ": " +  sqe.getMessage());
            throw sqe;
        } catch (Throwable t) {
            throw new RuntimeException("direct connection #" + directConnections, t);
        }
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    protected String getFindSql(String identifier) {
        assert ! isSub;
        if (findSql== null || "".equals(findSql)) throw new RuntimeException("No find query defined");
        if (identifier == null) throw new RuntimeException("No find query defined");
        String s = findSql.replaceAll("\\[IDENTIFIER\\]", identifier);
        s = s.replaceAll("\\[KEY\\]", identifier); // deprecated
        return s;
    }

    public boolean inIndex(String identifier) {
        CloseableIterator<JdbcEntry> i = getSqlCursor(getFindSql(identifier));
        boolean result = i.hasNext();
        try {
            i.close();
        } catch (IOException ex) {
            log.warn(ex);
        }
        return result;
    }

    protected String getSql(String identifier) {
        if (indexSql == null || "".equals(indexSql)) throw new RuntimeException("No sql defined");
        if (identifier == null) throw new RuntimeException("No query defined");
        String s = indexSql.replaceAll("\\[PARENTKEY\\]", identifier);
        s = s.replaceAll("\\[KEY\\]", identifier); // deprecated
        return s;
    }

    CloseableIterator<JdbcEntry> getSqlCursor(final String sql) {
        try {
            long start = System.currentTimeMillis();
            final Connection con = getDirectConnection();
            log.debug("About to execute " + sql + " (" + directConnections + ")");
            final Statement statement = con.createStatement();
            final ResultSet results = statement.executeQuery(sql);
            if (log.isDebugEnabled()) {
                log.debug("Executed " + sql + " in " + (System.currentTimeMillis() - start) + " ms");
            }
            final ResultSetMetaData meta = results.getMetaData();

            return new CloseableIterator<JdbcEntry>() {
                boolean hasNext = results.isBeforeFirst();
                int i = 0;

                public boolean hasNext() {
                    return hasNext;
                }

                public JdbcEntry next() {
                    if (! hasNext) {
                        throw new NoSuchElementException();
                    }
                    try {
                        results.next();
                        hasNext = ! results.isLast();
                    } catch (java.sql.SQLException sqe) {
                        log.error(sqe);
                        hasNext = false;
                    }
                    JdbcEntry entry = new JdbcEntry(meta, results, sql);
                    i++;
                    if (log.isServiceEnabled()) {
                        if (i % 100 == 0) {
                            log.service("jdbc cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                        } else if (log.isDebugEnabled()) {
                            log.trace("jdbc cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                        }
                    }
                    return entry;
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void close() {
                    log.debug("Closing " + con);
                    try {
                        if (results != null) results.close();
                        if (statement != null) statement.close();
                        if (con != null) con.close();
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * A map representing a row in a database. But only filled when actually used. So, only on first
     * use, a query is done. And not before that.
     * @since MMBase-1.9
     */
    protected class LazyMap extends AbstractMap<String, String> {
        private  Map<String, String> map = null;
        private final Map<String, String> keys ;
        private final String identifier;
        LazyMap(String identifier, Map<String, String> keys) {
            this.identifier = identifier;
            this.keys = keys;
        }
        protected void check() {
            if (map == null) {
                Connection connection = null;
                Statement statement = null;
                ResultSet results = null;
                try {
                    connection = dataSource.getConnection();
                    statement = connection.createStatement();
                    long start = System.currentTimeMillis();
                    String s = getFindSql(identifier);
                    if (log.isTraceEnabled()) {
                        log.trace("About to execute " + s + " because " , new Exception());
                    }
                    results = statement.executeQuery(s);
                    ResultSetMetaData meta = results.getMetaData();
                    map = new HashMap<String, String>();
                    if (results.next()) {
                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                            String value = org.mmbase.util.Casting.toString(results.getString(i));
                            map.put(meta.getColumnName(i).toLowerCase(), value);
                        }
                    }
                    long duration = (System.currentTimeMillis() - start);
                    if (duration > 500) {
                        log.warn("Executed " + s + " in " + duration + " ms");
                    } else if (duration > 100) {
                        log.debug("Executed " + s + " in " + duration + " ms");
                    } else {
                        log.trace("Executed " + s + " in " + duration + " ms");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                } finally {
                    if (results != null)    try { results.close();   } catch (Exception e) {}
                    if (statement != null)  try { statement.close(); } catch (Exception e) {}
                    if (connection != null) try { connection.close();} catch (Exception e) {}
                }
            }
        }
        public Set<Map.Entry<String, String>> entrySet() {
            check();
            return map.entrySet();
        }
        @Override
        public int size() {
            check();
            return map.size();
        }
        @Override
        public String get(Object key) {
            if(JdbcIndexDefinition.this.identifier.equals(key)) return identifier;
            if(keys.containsKey(key)) return keys.get(key);
            check();
            return map.get(key);
        }
        @Override
        public boolean containsKey(Object key) {
            if(JdbcIndexDefinition.this.identifier.equals(key)) return true;
            if(keys.containsKey(key)) return true;
            check();
            return map.containsKey(key);
        }
        @Override
        public String toString() {
            if (map != null) {
                return map.toString();
            } else {
                return "[LAZY node " + identifier + "]";
            }
        }
    }

    public org.mmbase.bridge.Node getNode(final Cloud userCloud, final Document doc) {
        String docId = doc.get("number");
        if (docId == null) {
            throw new IllegalArgumentException("No number found in " + doc);
        }
        LazyMap m =  nodeCache.get(docId); //
        if (m == null) {
            Map<String, String> keys = new HashMap<String, String>();
            for (String keyWord : keyWords) {
                keys.put(keyWord, doc.get(keyWord));
            }
            m = new LazyMap(docId, keys);
            nodeCache.put(docId, m);
        }
        org.mmbase.bridge.Node node = new MapNode(m, new MapNodeManager(userCloud, m) {
            @Override
                public boolean hasField(String name) {
                    if (JdbcIndexDefinition.this.key.equals(name)) return true;
                    return super.hasField(name);
                }
            @Override
                public org.mmbase.bridge.Field getField(String name) {
                    if (map == null && JdbcIndexDefinition.this.key.equals(name)) {
                        org.mmbase.core.CoreField fd = org.mmbase.core.util.Fields.createField(name, org.mmbase.core.util.Fields.classToType(Object.class),
                                                                                               org.mmbase.bridge.Field.TYPE_UNKNOWN,
                                                                                               org.mmbase.bridge.Field.STATE_VIRTUAL, null);
                        return new org.mmbase.bridge.implementation.BasicField(fd, this);
                    } else {
                        return super.getField(name);
                    }
                }
            });
        if (log.isDebugEnabled()) {
            log.debug("Returning node for "+ node);
        }
        return node;

    }


    public CloseableIterator<JdbcEntry> getCursor() {
        assert ! isSub;
        return getSqlCursor(indexSql);
    }

    public CloseableIterator<JdbcEntry> getSubCursor(String identifier) {
        if (isSub) {
            log.debug("Using getSubCursor for " + identifier);
            return getSqlCursor(getSql(identifier));
        } else {
            return  getSqlCursor(getFindSql(identifier));
        }
    }

    @Override
    public String toString() {
        return indexSql;
    }

    public class JdbcEntry implements IndexEntry {
        final ResultSetMetaData meta;
        final ResultSet results;
        final String sql;

        JdbcEntry(ResultSetMetaData m, ResultSet r, String s) {
            log.trace("new JDBC Entry");
            meta = m;
            results = r;
            sql = s;
        }

        public void index(Document document) {
            if (log.isTraceEnabled()) {
                log.trace("Indexing " + sql + " id=" + JdbcIndexDefinition.this.identifier + ", key = " + JdbcIndexDefinition.this.key);
            }
            String id  = getIdentifier();
            if (id != null) {
                document.add(new Field("builder", "VIRTUAL BUILDER", Field.Store.YES, Field.Index.NOT_ANALYZED)); // keyword
                document.add(new Field("number",  getIdentifier(),   Field.Store.YES, Field.Index.NOT_ANALYZED)); // keyword
            }
            try {
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String value = org.mmbase.util.Casting.toString(results.getString(i));
                    if(log.isTraceEnabled()) {
                        log.trace("Indexing " + value + " for " + meta.getColumnName(i) + " on " + getIdentifier());
                    }
                    String fieldName = meta.getColumnName(i);
                    if (keyWords.contains(fieldName)) {
                        Indexer.addField(document, new Field(fieldName,  value,   Field.Store.YES, Field.Index.NOT_ANALYZED), nonDefaultMultiples.get(fieldName)); // keyword
                    } else {
                        Field field = new Field(fieldName,   value,   Field.Store.YES, Field.Index.ANALYZED);
                        Float boost = boosts.get(fieldName);
                        if (boost != null) {
                            field.setBoost(boost);
                        }
                        Indexer.addField(document, field, nonDefaultMultiples.get(fieldName));
                        Field fullText = new Field("fulltext",  value,   Field.Store.YES, Field.Index.ANALYZED);
                        if (boost != null) {
                            fullText.setBoost(boost);
                        }
                        document.add(fullText);
                    }
                }
            } catch (SQLException sqe) {
                log.error(sqe.getMessage(), sqe);
            }
        }

        public Collection<IndexDefinition> getSubDefinitions() {
            return JdbcIndexDefinition.this.subQueries;
        }

        public String getIdentifier() {
            if (JdbcIndexDefinition.this.identifier != null && ! JdbcIndexDefinition.this.identifier.equals("")) {
                try {
                    return results.getString(JdbcIndexDefinition.this.identifier);
                } catch (SQLException sqe) {
                    log.error(meta + " " + sqe.getMessage(), sqe);
                    return "";
                }
            } else {
                return null;
            }
        }
        public String getKey() {
            if (JdbcIndexDefinition.this.key != null && ! JdbcIndexDefinition.this.key.equals("")) {
                try {
                    return results.getString(JdbcIndexDefinition.this.key);
                } catch (SQLException sqe) {
                    log.error(sqe.getMessage(), sqe);
                    return "";
                }
            } else {
                return null;
            }
        }
        public Set<String> getIdentifiers() {
            Set<String> ids = new HashSet<String>();
            ids.add(getIdentifier());
            return ids;
        }

    }

}
