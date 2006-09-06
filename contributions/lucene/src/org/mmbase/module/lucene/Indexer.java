/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import java.io.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.*;

import org.mmbase.bridge.*;
import org.mmbase.util.LocalizedString;

import org.mmbase.util.logging.*;

/**
 * An indexer object represents one Index in the MMBase lucene module. It contains the functionality
 * for creating and updating the indices by talking to the Lucene interfaces.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Indexer.java,v 1.29 2006-09-06 18:13:36 michiel Exp $
 **/
public class Indexer {

    static private final Logger log = Logging.getLoggerInstance(Indexer.class);

    /**
     * @since MMBase-1.9
     */
    public enum Multiple {ADD, FIRST, LAST};


    /**
     * Adds a Field to a Document considering also a 'multiple' setting.
     * @since MMBase-1.8.2
     */
    public static void addField(Document document, Field field, Multiple multiple) {
        switch(multiple) {
        case ADD:
        default: 
            document.add(field);
            break;
        case FIRST: 
            if (document.get(field.name()) == null) {
                document.add(field);
            }
            break;
        case LAST:
            document.removeFields(field.name());
            document.add(field);
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
    private final Collection<IndexDefinition> queries;

    /**
     * Instantiates an Indexer for a specified collection of queries and options.
     * @param index Name of the index
     * @param queries a collection of IndexDefinition objects that select the nodes to index, and contain options on the fields to index.
     * @param cloud The Cloud to use for querying
     */
    Indexer(String path, String index, Collection<IndexDefinition> queries, Cloud cloud, Analyzer analyzer, boolean readOnly) {
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
                log.warn(ioe.getMessage(), ioe);
            } catch (SecurityException  se) {
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

    public Node getNode(Cloud userCloud, String identifier) {
        for (IndexDefinition id : queries) {
            Node n = id.getNode(userCloud, identifier);
            if (n != null) return n;
        }
        return null;
    }

    /**
     * Delete the index for the main element node with the given number.
     * Used in iterative indexing.
     * @return The number of deleted lucene documents.
     * @param number the number of the node whose index to delete
     * @param klass The indexes to be deleted can be restricted to a certain class of IndexDefinition's.
     */
    public int deleteIndex(String number, Class klass) {
        IndexReader reader = null;
        try {
            for (Iterator<IndexDefinition> i = queries.iterator(); i.hasNext();) {
                IndexDefinition indexDefinition = i.next();
                if (klass.isAssignableFrom(indexDefinition.getClass())) {
                    reader = IndexReader.open(path);
                    Term term = new Term("number", number);
                    int deleted = reader.deleteDocuments(term);
                    if (deleted > 0) {
                        log.service(getName() + ": Deleted " + deleted + " for '" + number + "'");
                    }
                    return deleted;
                }
            }
            return 0;
        } catch (Exception e) {
            log.error(getName() + ": Cannot delete Index:" + e.getMessage() + " for index entry '" + number + "'");
            return 0;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    log.error("Can't close index reader: " + ioe.getMessage());
                }
            }
        }
    }

    /**
     * Update the index for the main element node with the given number.
     * Used in iterative indexing.
     * @param number the number of the node whose index to update
     */
    public int updateIndex(String number, Class klass) {
        int updated = 0;
        int deleted = deleteIndex(number, klass);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(path, analyzer, false);
            // process all queries
            for (IndexDefinition indexDefinition :  queries) {
                if (klass.isAssignableFrom(indexDefinition.getClass())) {
                    Iterator j = indexDefinition.getSubCursor(number);
                    if (log.isDebugEnabled()) {
                        log.debug(getName() + ": Updating index " + indexDefinition + " for " + number);
                    }
                    updated += index(j, writer);
                }
            }
        } catch (Exception e) {
            log.error("Cannot update Index: " + e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe) {
                    log.error("Can't close index writer: " + ioe.getMessage());
                }
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
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(path, analyzer, true);
            // process all queries
            for (IndexDefinition indexDefinition : queries) {
                log.debug("full index for " + indexDefinition);
                Iterator j = indexDefinition.getCursor();
                index(j, writer);
                if (Thread.currentThread().isInterrupted()) {
                    log.info("Interrupted");
                    return;
                }
            }
            writer.optimize();

            log.service("Full index finished. Total nr documents in index: " + writer.docCount());
        } catch (Exception e) {
            log.error("Cannot run FullIndex: " + e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioe) {
                    log.error("Can't close index writer: " + ioe.getMessage());
                }
            }
        }
    }

    /**
     * Runs the queries for the given cursor, and indexes all nodes that are returned.
     */
    protected int index(Iterator<IndexEntry> i, IndexWriter writer) throws IOException {
        int indexed = 0;
        Document document = null;
        String   lastIdentifier = null;
        if (! i.hasNext()) {
            log.debug("Empty iterator given to update " + writer);
        } else {
            log.debug("Update " + writer);
        }
        while(i.hasNext()) {
            IndexEntry entry = i.next();
            String newIdentifier = entry.getIdentifier();
            log.debug("Indexing for " + newIdentifier);
            // This code depends on the fact that if the same nodes appear multipible times, they are at least queried like so, that they appear next to each other
            if (! newIdentifier.equals(lastIdentifier)) {
                if (document != null) writer.addDocument(document);
                document = new Document();
                indexed++;
            }
            index(entry, document);
            if (Thread.currentThread().isInterrupted()) {
                log.debug("Interrupted");
                return indexed;
            }
            lastIdentifier = newIdentifier;
        }
        if (document != null) {
            if (log.isDebugEnabled()) {
                log.debug("New document " + document);
            }
            writer.addDocument(document);
        }
        return indexed;
    }

    /**
     * Indexes an entry, and its sub-indexes.
     */
    protected void index(IndexEntry entry, Document document) throws IOException {
        entry.index(document);
        Iterator<IndexDefinition> j = entry.getSubDefinitions().iterator();
        while (j.hasNext()) {
            IndexDefinition subDef = j.next();
            if (subDef == null) {
                log.warn("Found a sub definition which is null for " + entry);
                continue;
            }
            Iterator<IndexEntry> i = subDef.getSubCursor(entry.getIdentifier());
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
