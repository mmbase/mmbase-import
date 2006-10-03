/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import java.io.*;
import org.apache.lucene.index.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.*;

import org.mmbase.core.event.EventManager;

import org.mmbase.bridge.*;
import org.mmbase.util.CloseableIterator;
import org.mmbase.util.LocalizedString;

import org.mmbase.util.logging.*;

/**
 * An indexer object represents one Index in the MMBase lucene module. It contains the functionality
 * for creating and updating the indices by talking to the Lucene interfaces.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Indexer.java,v 1.39 2006-10-03 20:52:19 michiel Exp $
 **/
public class Indexer {

    static private final Logger log = Logging.getLoggerInstance(Indexer.class);

    /**
     * @since MMBase-1.9
     */
    public enum Multiple {
        ADD, FIRST, LAST;
    };


    /**
     * Adds a Field to a Document considering also a 'multiple' setting.
     * This arranges what must happen if a certain field with the same name is already present in the Document.
     * @since MMBase-1.9
     */
    public static void addField(Document document, Field field, Multiple multiple) {
        if (multiple == null) multiple = Multiple.ADD;
        switch(multiple) {
        case FIRST:
            if (document.get(field.name()) == null) {
                document.add(field);
            }
            break;
        case LAST:
            document.removeFields(field.name());
            document.add(field);
            break;
        case ADD:
        default:
            document.add(field);
            break;
        }
    }

    // reference to the cloud
    private final Cloud cloud;
    private final Analyzer analyzer;

    private final String path;
    // Name of the index
    private final String index;
    private final LocalizedString description;

    // Collection with queries to run
    private final List<IndexDefinition> queries;

    private Date lastFullIndex = new Date(0);




    // of course life would be easier if we could used BoundedFifoBuffer of jakarta or so, but
    // actually it's ont very hard to simulate it:
    private final int ERRORBUFFER_MAX = 100;
    private int errorBufferSize = 0;
    private int errorBufferCursor = -1;
    private final String[] errors = new String[ERRORBUFFER_MAX];
    protected  List<String> errorBuffer = new AbstractList() {
            public int size() { return errorBufferSize; }
            public String get(int index) { return errors[(errorBufferSize + errorBufferCursor - index) % errorBufferSize]; }
        };
    protected void addError(String string) {
        errorBufferCursor++;
        if (errorBufferSize < ERRORBUFFER_MAX) {
            errorBufferSize++;
        } else {
            errorBufferCursor %= ERRORBUFFER_MAX;
        }
        errors[errorBufferCursor] = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()) + " : " + string;
    }

    /**
     * Instantiates an Indexer for a specified collection of queries and options.
     * @param index Name of the index
     * @param queries a collection of IndexDefinition objects that select the nodes to index, and contain options on the fields to index.
     * @param cloud The Cloud to use for querying
     */
    Indexer(String path, String index, List<IndexDefinition> queries, Cloud cloud, Analyzer analyzer, boolean readOnly) {
        this.index = index;
        this.path =  path + java.io.File.separator + index;
        if (! readOnly) {
            try {
                File d = new File(this.path);
                if (d.exists()) {
                    if (d.isDirectory()) {
                        if (IndexReader.isLocked(this.path)) {
                            log.info("The directory " + this.path + " is locked! Trying to unlock.");
                            Directory dir = FSDirectory.getDirectory(this.path, false);
                            IndexReader.unlock(dir);
                            log.service("Unlocked lucene index directory " + dir);
                        }
                    } else {
                        log.warn("" + this.path + " is not a directory !");
                    }
                } else {
                    log.info("The directory " + this.path + " does not exist!");
                    d.mkdirs();
                }
            } catch (java.io.IOException ioe) {
                addError(ioe.getMessage());
                log.warn(ioe.getMessage(), ioe);
            } catch (SecurityException  se) {
                addError(se.getMessage());
                log.warn(se.getMessage(), se);
            }
        }
        this.queries = queries;
        this.cloud = cloud;
        if (analyzer == null) {
            this.analyzer = new StandardAnalyzer();
        } else {
            this.analyzer = analyzer;
        }
        description = new LocalizedString(index);
    }

    public String getName() {
        return index;
    }
    public String getPath() {
        return path;
    }
    public LocalizedString getDescription() {
        return description;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }
    public List<String> getErrors() {
        return errorBuffer;
    }

    public Node getNode(Cloud userCloud, Document doc) {
        for (IndexDefinition id : queries) {
            Node n = id.getNode(userCloud, doc);
            if (n != null) return n;
        }
        return null;
    }

    public Date getLastFullIndex() {
        return lastFullIndex;
    }

    /**
     * Delete the index for the main element node with the given number.
     * @return The number of deleted lucene documents.
     * @param number the number of the node whose index to delete
     * @param klass The indexes to be deleted can be restricted to a certain class of IndexDefinition's.
     */
    public int deleteIndex(String number, Class<? extends IndexDefinition> klass) {
        int deleted = 0;
        int updated = 0;
        for (IndexDefinition indexDefinition : queries) {
            if (klass.isAssignableFrom(indexDefinition.getClass())) {
                IndexReader reader = null;
                Set<String> mains = new HashSet();
                int d = 0;
                int u = 0;

                try {
                    reader = IndexReader.open(path);
                    Term term = new Term("number", number);
                    TermDocs docs = reader.termDocs(term);
                    while(docs.next()) {
                        int i = docs.doc();
                        String main = reader.document(i).get("number");
                        reader.deleteDocument(i);
                        if (main.equals(number)) {
                            d++;
                        } else {
                            mains.add(main);
                        }
                    }
                } catch (Exception e) {
                    addError(e.getMessage());
                    log.error(e);
                } finally {
                    if (reader != null) { try { reader.close(); } catch (IOException ioe) { log.error("Can't close index reader: " + ioe.getMessage()); } }
                }
                if (mains.size() > 0) {
                    u += update(indexDefinition, mains);
                }
                if (d > 0 || u > 0) {
                    updated += u;
                    deleted += d;
                    log.service(getName() + ": Deleted " + d + " for '" + number + "', updated '" + u + "'");
                }

            }
        }
        return deleted;

    }

    protected int update(IndexDefinition indexDefinition, Set<String> mains) {
        int updated = 0;
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(path, analyzer, false);
            for (String mainNumber : mains) {
                CloseableIterator<? extends IndexEntry> j = indexDefinition.getSubCursor(mainNumber);
                if (log.isDebugEnabled()) {
                    log.debug(getName() + ": Updating index " + indexDefinition + " for " + mainNumber);
                }
                updated += index(j, writer, indexDefinition.getId());
                j.close();
            }
        } catch (IOException ioe) {
            addError(ioe.getMessage());
            log.error(ioe);
        } finally {
            if (writer != null) try { writer.close();} catch (IOException ioe) { log.error(ioe); }
        }
        EventManager.getInstance().propagateEvent(NewSearcher.EVENT);
        return updated;
    }

    /**
     * Update the index for the main element node with the given number.
     * @param number the number of the node whose index to update
     */
    public int updateIndex(final String number, final Class<? extends IndexDefinition> klass) {
        int updated = 0;
        assert klass != null;
        // process all queries
        for (IndexDefinition indexDefinition :  queries) {
            if (indexDefinition == null) {
                log.warn("Found empty index definition in " + this);
                continue;
            }
            if (klass.isAssignableFrom(indexDefinition.getClass())) {
                Set<String> mains = new HashSet<String>();
                mains.add(number); // at least the object itself must be tried, it may be 
                IndexReader reader = null;
                try {
                    reader = IndexReader.open(path);
                    Term term = new Term("number", number);
                    TermDocs docs = reader.termDocs(term);
                    if (log.isDebugEnabled()) {
                        log.debug(getName() + ": Will find " + reader.docFreq(term) + " for number=" + number);
                    }
                    while(docs.next()) {
                        int i = docs.doc();
                        Document doc = reader.document(i);
                        String main = doc.get("number");
                        String indexId = doc.get("indexId");
                        log.debug("Found main number " + main + " for subindex " + indexId + " in " + doc);
                        if (indexId != null && indexId.equals(indexDefinition.getId())) {
                            mains.add(main);
                            reader.deleteDocument(i);
                        }
                    }
                    docs.close();
                } catch (IOException ioe) {
                    addError(ioe.getMessage());
                    log.error(ioe);
                } finally {
                    if (reader != null) try {reader.close(); } catch (IOException ioe) { log.error(ioe);}
                }
                log.debug("Found lucene documents " + mains + " for node " + number + " which must be updated now");
                updated += update(indexDefinition, mains);
            }
        }
        if (updated > 0) {
            log.service(getName() + ": Updated " + updated + " for '" + number + "'");
        } else if (log.isDebugEnabled()) {
            log.debug(getName() + ": Updated " + updated + " for '" + number + "'");
        }
        return updated;
    }

    /**
     * Drop all data in the index and create a new index by running all queries in this set
     * and indexing the results.
     */
    public void fullIndex() {
        log.service("Doing full index for " + toString());
        lastFullIndex = new Date(0);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(path, analyzer, true);
            // process all queries
            int subIndexNumber = 0;
            for (IndexDefinition indexDefinition : queries) {
                subIndexNumber++;
                log.debug("full index for " + indexDefinition);
                CloseableIterator<? extends IndexEntry> j = indexDefinition.getCursor();
                index(j, writer, indexDefinition.getId());
                j.close();
                if (Thread.currentThread().isInterrupted()) {
                    log.info("Interrupted");
                    return;
                }
            }
            writer.optimize();
            lastFullIndex = new Date();
            log.service("Full index finished at " + lastFullIndex + ". Total nr documents in index: " + writer.docCount());
        } catch (Exception e) {
                addError(e.getMessage());
            log.error("Cannot run FullIndex: " + e.getMessage(), e);
        } finally {
            if (writer != null) { try { writer.close(); } catch (IOException ioe) { log.error("Can't close index writer: " + ioe.getMessage()); } }
        }
        EventManager.getInstance().propagateEvent(NewSearcher.EVENT);
    }

    /**
     * Runs the queries for the given cursor, and indexes all nodes that are returned.
     */
    protected int index(CloseableIterator<? extends IndexEntry> i, IndexWriter writer, String indexId) throws IOException {
        int indexed = 0;
        Document document = null;
        String   lastIdentifier = null;
        if (! i.hasNext()) {
            log.debug("Empty iterator given to update " + writer + " in " + this);
        } else {
            log.debug("Update " + writer + " in " + this);
        }
        while(i.hasNext()) {
            IndexEntry entry = i.next();
            String newIdentifier = entry.getIdentifier();
            log.debug("Indexing for " + newIdentifier);
            // This code depends on the fact that if the same nodes appear multipible times, they are at least queried like so, that they appear next to each other
            if (! newIdentifier.equals(lastIdentifier)) {
                if (document != null) {
                    writer.addDocument(document);
                }
                document = new Document();
                document.add(new Field("indexId", indexId,  Field.Store.YES, Field.Index.UN_TOKENIZED)); 
                indexed++;
            }
            index(entry, document);
            if (Thread.currentThread().isInterrupted()) {
                log.debug("Interrupted");
                break;
            }
            lastIdentifier = newIdentifier;
        }
        if (document != null) {
            if (log.isDebugEnabled()) {
                log.debug("New document " + document);
            }
            writer.addDocument(document);
        }
        EventManager.getInstance().propagateEvent(NewSearcher.EVENT);
        return indexed;
    }

    /**
     * Indexes an entry, and its sub-indexes (recursively).
     */
    protected void index(IndexEntry entry, Document document) throws IOException {
        // writes the entry itself:
        entry.index(document);

        // and it's sub entries (recursively).
        for (IndexDefinition subDef : entry.getSubDefinitions()) {
            if (subDef == null) {
                log.warn("Found a sub definition which is null for " + entry);
                continue;
            }
            Iterator<? extends IndexEntry> i = subDef.getSubCursor(entry.getKey());
            while(i.hasNext()) {
                IndexEntry subEntry = i.next();
                index(subEntry, document);
                if (Thread.currentThread().isInterrupted()) {
                    log.debug("Interrupted");
                    return;
                }
            }
        }
    }

    public String toString() {
        return getName() + queries;
    }

    

}
