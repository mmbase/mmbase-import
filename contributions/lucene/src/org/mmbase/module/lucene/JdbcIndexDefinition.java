/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import javax.sql.DataSource;
import java.sql.*;

import org.w3c.dom.*;
import org.mmbase.util.*;
import org.mmbase.bridge.Cloud;
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
 * @version $Id: JdbcIndexDefinition.java,v 1.8 2006-04-18 13:25:40 michiel Exp $
 **/
public class JdbcIndexDefinition implements IndexDefinition {

    static private final Logger log = Logging.getLoggerInstance(JdbcIndexDefinition.class);

    private static final int CACHE_SIZE = 10 * 1024;
    protected static Cache nodeCache = new Cache(CACHE_SIZE) {
            {
                putCache();
            }

            public final String getName() {
                return "LuceneJdbcNodes";
            }
            public final String getDescription() {
                return "Node identifier -> Map";
            }
        };

    private final DataSource dataSource;
    private final String sql;
    private final String key;
    private final String find;
    private final Analyzer analyzer;

    private final Set keyWords = new HashSet();

    JdbcIndexDefinition(DataSource ds, Element element,
                        Set allIndexedFields,
                        boolean storeText,
                        boolean mergeText, Analyzer a) {
        this.dataSource = ds;
        sql = element.getAttribute("sql");
        key = element.getAttribute("key");
        find = element.getAttribute("find");
        NodeList childNodes = element.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if ("field".equals(childElement.getLocalName())) {
                    if (childElement.getAttribute("keyword").equals("true")) {
                        keyWords.add(childElement.getAttribute("name"));
                    }
                }
            }
        }
        this.analyzer = a;
    }

    /**
     * Jdbc connection pooling of MMBase would kill the statement if too duratious. This produces a
     * 'direct connection' in that case, to circumvent that problem (Indexing queries _may_ take a while).
     */
    protected Connection getDirectConnection() throws SQLException {
        if (dataSource instanceof GenericDataSource) {
            return ((GenericDataSource) dataSource).getDirectConnection();
        } else {
            return dataSource.getConnection();
        }
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    protected String getSql(String identifier) {
        if (find == null) throw new RuntimeException("No find query defined");
        if (identifier == null) throw new RuntimeException("No find query defined");
        String s = find.replaceAll("\\[KEY\\]", identifier);
        return s;
    }

    protected CloseableIterator getCursor(String s) {
        try {
            long start = System.currentTimeMillis();
            log.debug("About to execute " + s);
            final Connection con = getDirectConnection();
            final Statement statement = con.createStatement();
            final ResultSet results = statement.executeQuery(s);
            if (log.isDebugEnabled()) {
                log.debug("Executed " + s + " in " + (System.currentTimeMillis() - start) + " ms");
            }
            final ResultSetMetaData meta = results.getMetaData();
            return new CloseableIterator() {
                int i = 0;

                public boolean hasNext() {
                    try {
                        return ! results.isLast();
                    } catch (java.sql.SQLException sqe) {
                        log.warn(sqe);
                        return false;
                    }
                }

                public Object next() {
                    try {
                        if (! results.next()) {
                            throw new NoSuchElementException();
                        }
                    } catch (java.sql.SQLException sqe) {
                        throw new NoSuchElementException(sqe.getMessage());
                    }
                    JdbcEntry entry = new JdbcEntry(meta, results);
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
            throw new RuntimeException(e);
        }
    }

    public org.mmbase.bridge.Node getNode(Cloud userCloud, String identifier) {
        Map map = (Map) nodeCache.get(identifier);
        if (map == null) {
            try {
                final Connection connection = dataSource.getConnection();
                final Statement statement = connection.createStatement();
                long start = System.currentTimeMillis();
                String s = getSql(identifier);
                log.debug("About to execute " + s);
                ResultSet results = statement.executeQuery(s);
                ResultSetMetaData meta = results.getMetaData();
                if (results.next()) {
                    map = new HashMap();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        String value = org.mmbase.util.Casting.toString(results.getString(i));
                        map.put(meta.getColumnName(i).toLowerCase(), value);
                    }
                } else {
                    map = null;
                }
                long duration = (System.currentTimeMillis() - start);
                if (duration > 500) {
                    log.warn("Executed " + s + " in " + duration + " ms");
                } else if (duration > 100) {
                    log.debug("Executed " + s + " in " + duration + " ms");
                } else {
                    log.trace("Executed " + s + " in " + duration + " ms");
                }
                if (results != null) results.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                if(map == null)return null;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            nodeCache.put(identifier, map);
        }
        if (log.isDebugEnabled()) {
            log.debug("Returning node for "+ map);
        }
        return new org.mmbase.bridge.implementation.VirtualNode(map, userCloud);

    }


    public CloseableIterator getCursor() {
        return getCursor(sql);
    }

    public CloseableIterator getSubCursor(String identifier) {
        return getCursor(getSql(identifier));
    }

    public IndexEntry getParent() {
        return null;
    }

    public String toString() {
        return sql;
    }

    class JdbcEntry implements IndexEntry {
        final ResultSetMetaData meta;
        final ResultSet results;

        JdbcEntry(ResultSetMetaData m, ResultSet r) {
            log.trace("new JDBC Entry");
            meta = m;
            results = r;
        }

        public void index(Document document) {
            if (log.isDebugEnabled()) {
                log.trace("Indexing "+ results + " with " + keyWords);
            }
            document.add(new Field("builder", "VIRTUAL BUILDER", Field.Store.YES, Field.Index.UN_TOKENIZED)); // keyword
            document.add(new Field("number",  getIdentifier(),   Field.Store.YES, Field.Index.UN_TOKENIZED)); // keyword
            try {
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String value = org.mmbase.util.Casting.toString(results.getString(i));
                    if(log.isTraceEnabled()) {
                        log.trace("Indexing " + value + " for " + meta.getColumnName(i) + " on " + getIdentifier());
                    }
                    String fieldName = meta.getColumnName(i);
                    if (keyWords.contains(fieldName)) {
                        document.add(new Field(fieldName,  value,   Field.Store.YES, Field.Index.UN_TOKENIZED)); // keyword
                    } else {
                        document.add(new Field(fieldName,   value,   Field.Store.YES, Field.Index.TOKENIZED));
                        document.add(new Field("fulltext",  value,   Field.Store.YES, Field.Index.TOKENIZED));
                    }
                }
            } catch (SQLException sqe) {
                log.error(sqe);
            }
        }

        public Collection getSubDefinitions() {
            return Collections.EMPTY_LIST;
        }

        public String getIdentifier() {
            try {
                return results.getString(JdbcIndexDefinition.this.key);
            } catch (SQLException sqe) {
                log.error(sqe);
                return "";
            }
        }
        public Set getIdentifiers() {
            Set ids = new HashSet();
            ids.add(getIdentifier());
            return ids;
        }

    }

}
