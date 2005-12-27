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

import org.mmbase.bridge.*;

import org.mmbase.util.logging.*;

/**
 **/
public class Indexer {

    static private final Logger log = Logging.getLoggerInstance(Indexer.class);


    // reference to the cloud
    private Cloud cloud;
    private Analyzer analyzer;

    // Name of the index
    private String index;

    // Collection with queries to run
    private Collection queries;


    /**
     * Instantiates an Indexer for a specified collection of queries and options.
     * @param index Name of the index
     * @param queries a collection of IndexDefinition objects that select the nodes to index, and contain options on the fields to index.
     * @param cloud The Cloud to use for querying
     */
    Indexer(String index, Collection queries, Cloud cloud, Analyzer analyzer) {
        this.index = index;
        this.queries = queries;
        this.cloud = cloud;
        if (analyzer == null) {
            this.analyzer = new StandardAnalyzer();
        } else {
            this.analyzer = analyzer;
        }
    }

    public String getName() {
        return index;
    }

    public Node getNode(Cloud userCloud, String identifier) {
        Iterator i = queries.iterator();
        while (i.hasNext()) {
            IndexDefinition id = (IndexDefinition) i.next();
            Node n = id.getNode(userCloud, identifier);
            if (n != null) return n;
        }
        return null;
    }

    /**
     * Delete the index for the main element node with the given number.
     * Used in iterative indexing.
     * @param number the numbe rof teh node whose index to delete
     */
    public void deleteIndex(String number) {
        IndexReader reader = null;
        try {
            reader = IndexReader.open(index);
            Term term = new Term("number", number);
            reader.delete(term);
        } catch (Exception e) {
            log.error("Cannot delete Index:" + e.getMessage());
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
    public void updateIndex(String number) {
        deleteIndex(number);
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(index, analyzer, false);
            // process all queries
            for (Iterator i = queries.iterator(); i.hasNext();) {
                IndexDefinition indexDefinition = (IndexDefinition)i.next();
                Iterator j = indexDefinition.getSubCursor(number);
                index(j, writer);
            }
        } catch (Exception e) {
            log.error("Cannot update Index:" + e.getMessage());
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
     * Drop all data in the index and create a new index by running all queries in this set
     * and indexing the results.
     */
    public void fullIndex() {
        log.service("Doing full index for " + toString());
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(index, analyzer, true);
            // process all queries
            for (Iterator i = queries.iterator(); i.hasNext();) {
                IndexDefinition indexDefinition = (IndexDefinition)i.next();
                log.service("full index for " + indexDefinition);
                Iterator j = indexDefinition.getCursor();
                index(j, writer);
            }
            writer.optimize();
            
            log.service("Full index finished. Total nr documents in index: " + writer.docCount());
        } catch (Exception e) {
            log.error("Cannot run FullIndex: "+e.getMessage());
            log.error(Logging.stackTrace(e));
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
    protected void index(Iterator i, IndexWriter writer) throws IOException {
        while(i.hasNext()) {
            IndexEntry entry = (IndexEntry) i.next();
            Document document = new Document();
            index(entry, document);
            writer.addDocument(document);
        }
    }

    /**
     * Indexes an entry, and its sub-indexes.
     */
    protected void index(IndexEntry entry, Document document) throws IOException {
        entry.index(document);
        Iterator j = entry.getSubDefinitions().iterator();
        while (j.hasNext()) {
            IndexDefinition subDef = (IndexDefinition) j.next();
            if (subDef == null) {
                log.warn("Found a sub definition which is null for " + entry);
                continue;
            }
            Iterator i = subDef.getSubCursor(entry.getIdentifier());
            while(i.hasNext()) {
                IndexEntry subEntry = (IndexEntry) i.next();
                index(subEntry, document);
            }
        }
        
    }



    public String toString() {
        return getName() + queries;
    }

}
