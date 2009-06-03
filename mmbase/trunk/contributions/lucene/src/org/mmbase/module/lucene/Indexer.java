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
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * An indexer object represents one Index in the MMBase lucene module. It contains the functionality
 * for creating and updating the indices by talking to the Lucene interfaces.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public class Indexer {

    static private final Logger log = Logging.getLoggerInstance(Indexer.class);

    /**
     *  An empty index definition that can be used to obtain nodes from the cloud.
     */
    static final MMBaseIndexDefinition nodeLoader = new MMBaseIndexDefinition();

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
        if (multiple == null) {
          multiple = Multiple.ADD;
        }
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

    private final Analyzer analyzer;

    private final String lucenePath;
    private final String path;
    // Name of the index
    private final String index;
    private final LocalizedString description;

    // Collection with queries to run
    private final List<IndexDefinition> queries;


    private boolean fullIndexing = false;

    // of course life would be easier if we could used BoundedFifoBuffer of jakarta or so, but
    // actually it's ont very hard to simulate it:
    private final int ERRORBUFFER_MAX = 100;
    private int errorBufferSize = 0;
    private int errorBufferCursor = -1;
    private long errorCount = 0;
    private final String[] errors = new String[ERRORBUFFER_MAX];
    protected  List<String> errorBuffer = new AbstractList<String>() {
            public int size() { return errorBufferSize; }
            public String get(int index) { return errors[(errorBufferSize + errorBufferCursor - index) % errorBufferSize]; }
        };
    protected void addError(String string) {
        errorCount++;
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
     */
    Indexer(String path, String index, List<IndexDefinition> queries, Analyzer analyzer, boolean readOnly) {
        this.index = index;
        this.lucenePath = path;
        this.path =  path + File.separator + index;
        if (! readOnly) {
            try {
                File d = new File(this.path);
                if (d.exists()) {
                    if (d.isDirectory()) {
                        if (IndexWriter.isLocked(this.path)) {
                            log.info("The directory " + this.path + " is locked! Trying to unlock.");
                            Directory dir = getDirectory();
                            IndexWriter.unlock(dir);
                            log.service("Unlocked lucene index directory " + dir);
                        }
                    } else {
                        log.warn("" + this.path + " is not a directory !");
                    }
                } else {
                    log.info("The directory " + this.path + " does not exist!");
                    d.mkdirs();
                }
            } catch (IOException ioe) {
                addError(ioe.getMessage());
                log.warn(ioe.getMessage(), ioe);
            } catch (SecurityException  se) {
                addError(se.getMessage());
                log.warn(se.getMessage(), se);
            }
        }
        this.queries = queries;
        if (analyzer == null) {
            this.analyzer = new StandardAnalyzer();
        } else {
            this.analyzer = analyzer;
        }
        description = new LocalizedString(index);
    }

    protected Directory getDirectory() throws IOException {
        return FSDirectory.getDirectory(path);
    }
    protected Directory getDirectoryForFullIndex() throws IOException {
        return FSDirectory.getDirectory(lucenePath + File.separator + index + ".new");
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
            if (n != null) {
              return n;
            }
        }
        return nodeLoader.getNode(userCloud, doc);
    }

    protected Properties loadLastFullIndexTimes() {
        Properties lastIndexed = new Properties();
        try {
            lastIndexed.load(new FileInputStream(lucenePath + java.io.File.separator + "lastIndexed.properties"));
        } catch (FileNotFoundException fnfe) {
            log.debug(fnfe);
        } catch (IOException ioe) {
            log.service(ioe);
        }
        return lastIndexed;
    }
    protected void storeLastFullIndexTimes(Properties lastIndexed) {
        try {
            lastIndexed.store(new FileOutputStream(lucenePath + java.io.File.separator + "lastIndexed.properties"), "Saved by " + getClass());
        } catch (IOException ioe) {
            log.warn(ioe);
        }
    }

    public Date getLastFullIndex() {
        Properties lastIndexes = loadLastFullIndexTimes();
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            return format.parse(lastIndexes.getProperty(index, "1970-01-01 00:00:00"));
        } catch (java.text.ParseException pe) {
            log.warn(pe);
            return new Date(0);
        }
    }
    public long getLastFullIndexDuration() {
        Properties lastIndexes = loadLastFullIndexTimes();
        String dur = lastIndexes.getProperty(index + ".duration");
        if (dur != null && org.mmbase.datatypes.StringDataType.LONG_PATTERN.matcher(dur).matches()) {
            return Long.parseLong(lastIndexes.getProperty(index + ".duration"));
        } else {
            return -1L;
        }
    }
    protected Date setLastFullIndex(long startTime) {
        Properties lastIndexes = loadLastFullIndexTimes();
        Date lastFullIndex = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        lastIndexes.setProperty(index, format.format(lastFullIndex.getTime()));
        lastIndexes.setProperty(index + ".duration", "" + (System.currentTimeMillis() - startTime));
        storeLastFullIndexTimes(lastIndexes);
        return lastFullIndex;
    }

    /**
     * Delete the index for the main element node with the given number.
     * @return The number of deleted lucene documents.
     * @param number the number of the node whose index to delete
     * @param klass The indexes to be deleted can be restricted to a certain class of IndexDefinition's.
     */
    int deleteIndex(String number, Class<? extends IndexDefinition> klass) {
        int deleted = 0;
        int updated = 0;
        OUTER:
        for (IndexDefinition indexDefinition : queries) {
            if (klass.isAssignableFrom(indexDefinition.getClass())) {
                IndexReader reader = null;
                Set<String> mains = new HashSet<String>();
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
                        if (Thread.currentThread().isInterrupted()) {
                            log.service("Interrupted");
                            break OUTER;
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
            if (Thread.currentThread().isInterrupted()) {
                log.service("Interrupted");
            }
        }
        return deleted;

    }

    int update(IndexDefinition indexDefinition, Set<String> mains) {
        int updated = 0;
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(getDirectory(), analyzer, false, IndexWriter.MaxFieldLength.UNLIMITED);
            for (String mainNumber : mains) {
                CloseableIterator<? extends IndexEntry> j = indexDefinition.getSubCursor(mainNumber);
                if (log.isDebugEnabled()) {
                    log.debug(getName() + ": Updating index " + indexDefinition + " for " + mainNumber);
                }
                updated += index(j, writer, indexDefinition.getId());
                j.close();
                if (Thread.currentThread().isInterrupted()) {
                    log.service("Interrupted");
                    break;
                }
            }
        } catch (FileNotFoundException fnfe) {
            log.debug(fnfe);
        } catch (IOException ioe) {
            addError(ioe.getMessage());
            log.error(ioe);
        } finally {
            if (writer != null) try { writer.close();} catch (IOException ioe) { log.error(ioe); }
        }
        EventManager.getInstance().propagateEvent(new NewSearcher.Event(getName()));
        return updated;
    }

    /**
     * Update the index for the main element node with the given number.
     * @param number the number of the node whose index to update
     */
    int updateIndex(final String number, final Class<? extends IndexDefinition> klass) {
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
                IndexReader reader = null;
                try {
                    reader = IndexReader.open(path);
                    Term term = new Term("number", number);
                    TermDocs docs = reader.termDocs(term);
                    if (log.isDebugEnabled()) {
                        int num = reader.docFreq(term);
                        if (num > 0) {
                            log.debug(getName() + ": Will find " + num + " documents for number=" + number);
                        }
                    }
                    while(docs.next()) {
                        int i = docs.doc();
                        Document doc = reader.document(i);
                        String main = doc.get("number");
                        String indexId = doc.get("indexId");
                        log.debug("Found main number " + main + " for subindex " + indexId + " in " + doc);
                        if (indexId != null && indexId.equals(indexDefinition.getId())) {
                            log.debug("Deleted #" + i + " from " + indexId);
                            mains.add(main);
                            reader.deleteDocument(i);
                        } else {
                            log.debug("Retained #" + i + " from " + indexId + " (!= " + indexDefinition.getId());
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            log.service("Interrupted");
                            break;
                        }
                    }
                    docs.close();
                } catch (FileNotFoundException fnfe) {
                    // ignore, indices were simply not y et build.
                    log.debug(fnfe);
                } catch (IOException ioe) {
                    addError(ioe.getMessage());
                    log.error(ioe);
                } finally {
                    if (reader != null) {
                      try {
                        reader.close();
                      } catch  (IOException ioe) {
                        log.error(ioe);
                      }
                  }
                }
                if (mains.size() > 0) {
                    log.debug("Found lucene documents " + mains + " for node " + number + " which must be updated now");
                    updated += update(indexDefinition, mains);
                } else {
                    // perhaps the object changed such, that it now would be in the index.
                    if (indexDefinition.inIndex(number)) {
                        mains.add(number);
                        updated += update(indexDefinition, mains);
                    }
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                log.service("Interrupted");
                break;
            }
        }
        if (updated > 0) {
            log.service(getName() + ": Updated " + updated + " documents for '" + number + "'");
        } else if (log.isDebugEnabled()) {
            log.debug(getName() + ": Updated " + updated + " documents for '" + number + "'");
        }
        return updated;
    }

    /**
     * Update the index for the main element node with the given number.
     * @param number the number of the node whose index to update
     */
     int newIndex(final String number, final Class<? extends IndexDefinition> klass) {
        int updated = 0;
        assert klass != null;
        // process all queries
        for (IndexDefinition indexDefinition :  queries) {
            if (indexDefinition == null) {
                log.warn("Found empty index definition in " + this);
                continue;
            }
            if (klass.isAssignableFrom(indexDefinition.getClass())) {
                if (indexDefinition.inIndex(number)) {
                    Set<String> mains = new HashSet<String>();
                    mains.add(number);
                    updated += update(indexDefinition, mains);
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                log.service("Interrupted");
                break;
            }
        }
        if (updated > 0) {
            log.service(getName() + ": " + updated + " new documents for '" + number + "'");
        } else if (log.isDebugEnabled()) {
            log.debug(getName() + ": " + updated + " new documents for '" + number + "'");
        }
        return updated;
    }

    /**
     * Drop all data in the index and create a new index by running all queries in this set
     * and indexing the results.
     */
    public void fullIndex() {
        if (! fullIndexing) {
            synchronized(this) {
                log.service("Doing full index for " + toString());
                long errorCountBefore = errorCount;
                EventManager.getInstance().propagateEvent(new FullIndexEvents.Event(getName(), FullIndexEvents.Status.START, 0));
                IndexWriter writer = null;
                Directory fullIndex = null;
                try {
                    fullIndexing = true;
                    clear(true);
                    fullIndex = getDirectoryForFullIndex();
                    writer = new IndexWriter(fullIndex, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
                    long startTime = System.currentTimeMillis();
                    // process all queries
                    int subIndexNumber = 0;
                    for (IndexDefinition indexDefinition : queries) {
                        subIndexNumber++;
                        log.service("full index for " + indexDefinition);
                        CloseableIterator<? extends IndexEntry> j = indexDefinition.getCursor();
                        try {
                            index(j, writer, indexDefinition.getId());
                        } finally {
                            j.close();
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            log.info("Interrupted");
                            return;
                        }
                    }
                    writer.optimize();
                    writer.commit();
                    writer.close();
                    if (errorCountBefore == errorCount) {
                        // first clean up, to remove possible mess
                        clear(false);
                        Directory.copy(fullIndex, getDirectory(), true);
                        Date lastFullIndex = setLastFullIndex(startTime);
                        log.info("Full index finished at " + lastFullIndex + ". Copied " + fullIndex + " to " +
                                getDirectory() + " Total nr documents in index '" + getName() + "': " + writer.maxDoc());
                        log.info("Full index finished at " + lastFullIndex );
                        if (log.isDebugEnabled()) log.debug("Copied " + fullIndex);
                        if (log.isDebugEnabled()) log.debug("    To " + getDirectory());
                        log.info("Total nr documents in index '" + getName() + "': " + writer.maxDoc());
                    } else if (Thread.currentThread().isInterrupted()) {
                        addError("Interrupted, will not update the index");
                    } else {
                        addError((errorCount - errorCountBefore) + " errors during full index. Will not update the index.");
                    }
                    EventManager.getInstance().propagateEvent(new FullIndexEvents.Event(getName(), FullIndexEvents.Status.IDLE, writer.maxDoc()));
                } catch (Exception e) {
                    addError("" + fullIndex + ": " + e.getMessage());
                    log.error("Cannot run FullIndex: " + e.getMessage(), e);
                } finally {
                    if (writer != null) { try { writer.close(); } catch (IOException ioe) { log.error("Can't close index writer: " + ioe.getMessage()); } }
                    fullIndexing = false;

                }
                EventManager.getInstance().propagateEvent(new NewSearcher.Event(getName()));
            }
        } else {
            log.info("Refusing to full-index, because already busy");
        }
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
                document.add(new Field("indexId", indexId,  Field.Store.YES, Field.Index.NOT_ANALYZED));
                indexed++;
                if (indexed % 100 == 0) {
                    log.service("Indexed " + indexed + " documents");
                    EventManager.getInstance().propagateEvent(new FullIndexEvents.Event(getName(), FullIndexEvents.Status.BUSY, indexed));
                } else if (log.isDebugEnabled()) {
                    log.debug("Indexed " + indexed + " documents");
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                log.service("Interrupted");
                break;
            }
            index(entry, document);

            lastIdentifier = newIdentifier;
        }
        if (document != null) {
            if (log.isDebugEnabled()) {
                log.debug("New document " + document.getBoost() + " " + document);
            }
            writer.addDocument(document);
        }
        EventManager.getInstance().propagateEvent(new NewSearcher.Event(getName()));
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
            CloseableIterator<? extends IndexEntry> i = subDef.getSubCursor(entry.getKey());
            try {
                while(i.hasNext()) {
                    IndexEntry subEntry = i.next();
                    index(subEntry, document);
                    if (Thread.currentThread().isInterrupted()) {
                        log.debug("Interrupted");
                        return;
                    }
                }
            } finally {
                i.close();
            }
        }
    }

    void clear(boolean copy) {
        try {
            Directory dir = copy ? getDirectoryForFullIndex(): getDirectory();
            log.debug("dir: " + dir);
            for (String file : dir.list()) {
                if (file != null) {
                    try {
                        log.service("Deleting " + file);
                        dir.deleteFile(file);
                    } catch (Exception e) {
                        log.warn(e);
                    }
                }
            }
            if (dir instanceof FSDirectory) {
                File fsdir = ((FSDirectory) dir).getFile();
                for (File file : fsdir.listFiles()) {
                    try {
                        log.service("Deleting " + file);
                        file.delete();
                    } catch (Exception e) {
                        log.warn(e);
                    }
                }
            }
            if (! copy)  EventManager.getInstance().propagateEvent(new NewSearcher.Event(index));
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    protected void repare(final CorruptIndexException ci, final boolean copy) {
        if (! fullIndexing) {
            synchronized(this) {
                org.mmbase.util.ThreadPools.jobsExecutor.execute(new Runnable() {
                        public void run() {
                            log.info("Reparing index " + Indexer.this + " because " + ci.getMessage());
                            Indexer.this.clear(true);
                            if (! copy) {
                                Indexer.this.clear(false);
                                Indexer.this.fullIndex();
                            }
                        }
                    });
            }
        }
    }

    @Override
    public String toString() {
        return getName() + queries;
    }



}
