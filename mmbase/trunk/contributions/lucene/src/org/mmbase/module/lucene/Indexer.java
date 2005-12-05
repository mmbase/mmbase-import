/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.text.*;
import java.util.*;
import java.io.*;

import org.apache.lucene.document.*;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;

import org.pdfbox.encryption.DocumentEncryption;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;

import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import org.textmining.text.extraction.WordExtractor;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.HugeNodeListIterator;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * The Lucene Indexer creates an index based on one or more query results from MMBase.
 * It is initialized with a set of queries, which it runs in the background in batches
 * until all results have been found.
 * All fields returned by the query are used to create an index for an 'element'.
 * Queries can run over multiple MMBase builders, but only one builder is designated as the actual
 * 'element builder' - that is, as the builder whose nodes are associated with the indexed content, and
 * which are eventually returned by the Searcher.
 *
 * @author Pierre van Rooden
 * @version $Id: Indexer.java,v 1.10 2005-12-05 20:54:47 michiel Exp $
 **/
public class Indexer {

    /** identifies a standard query */
    static public final int STANDARD_QUERY = 0;
    /** identifies a limited query */
    static public final int LIMITED_QUERY = 1;
    /** identifies a subquery */
    static public final int SUBQUERY = 2;

    static private final Logger log = Logging.getLoggerInstance(Indexer.class);
    // format for dates to index
    static private final DateFormat simpleFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    // Name of the index
    private String index;
    // Collection with queries to run
    private Collection queries;
    // reference to the cloud
    private Cloud cloud;

    /**
     * Instantiates an Indexer for a specified collection of queries and options.
     * @param index Name of the index
     * @param queries a collection of IndexDefinition objects that select the nodes to index, and contain options on the fields to index.
     * @param cloud The Cloud to use for querying
     */
    Indexer(String index, Collection queries, Cloud cloud) {
        this.index = index;
        this.queries = queries;
        this.cloud = cloud;
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
            log.error("Cannot delete Index:"+e.getMessage());
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
            writer = new IndexWriter(index, new StandardAnalyzer(), false);
            // for XML fields, try to 'unXMLize' before indexing.
            // That should go about like this (works for 'mmxf' fields at least)
            cloud.setProperty(Cloud.PROP_XMLMODE, "flat");
            Node node = cloud.getNode(number);
            if (node != null) {
                // process all queries
                for (Iterator i = queries.iterator(); i.hasNext();) {
                    IndexDefinition indexDefinition = (IndexDefinition)i.next();
                    if (indexDefinition.elementManager.equals(node.getNodeManager())) {
                        IndexCursor cursor = new IndexCursor(indexDefinition, writer);
                        cursor.nodeNumber = node.getNumber();
                        indexQuery(cursor, LIMITED_QUERY);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Cannot update Index:"+e.getMessage());
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
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(index, new StandardAnalyzer(), true);
            // process all queries
            for (Iterator i = queries.iterator(); i.hasNext();) {
                IndexDefinition indexDefinition = (IndexDefinition)i.next();
                IndexCursor cursor = new IndexCursor(indexDefinition, writer);
                indexQuery(cursor, STANDARD_QUERY);
            }
            writer.optimize();
            if (log.isDebugEnabled()) {
                log.debug("Total nr documents in index: "+writer.docCount());
            }
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
     * Runs a query for the given cursor, an returns a NodeIterator to run over the nodes.
     * This implementation uses a HugeNodeListIterator.
     * @param cursor the cursor with query and offset information
     * @param queryType the type of query
     * @return the query result as a NodeIterator object
     * @throws SearchQueryException is the query to create the index out of failed
     */
    protected NodeIterator getNodeIterator(IndexCursor cursor, int queryType) throws SearchQueryException {
        Query query = (Query)cursor.query.clone();
        String elementNumberFieldName = "number";
        String numberFieldName = "number";
        if (cursor.isMultiLevel) {
            elementNumberFieldName = cursor.elementManager.getName()+".number";
            numberFieldName = elementNumberFieldName;
            if (queryType == SUBQUERY) {
                numberFieldName = ((Step)cursor.query.getSteps().get(0)).getAlias()+".number";
            }
        }
        if (queryType == LIMITED_QUERY || queryType == SUBQUERY) {
            Constraint constraint = Queries.createConstraint(query, numberFieldName, FieldCompareConstraint.EQUAL, new Integer(cursor.nodeNumber));
            Queries.addConstraint(query,constraint);
        }
        StepField elementNumberField = query.createStepField(elementNumberFieldName);
        query.addSortOrder(elementNumberField,SortOrder.ORDER_ASCENDING);
        return new HugeNodeListIterator(query, cursor.maxNodesInQuery);
    }

    public void runSubCursors(IndexCursor cursor) throws SearchQueryException, IOException {
        for (Iterator j = cursor.subQueries.iterator(); j.hasNext(); ) {
            IndexDefinition queryDefinition = (IndexDefinition) j.next();
            IndexCursor localCursor = new IndexCursor(queryDefinition);
            localCursor.nodeNumber = cursor.nodeNumber;
            indexQuery(localCursor, SUBQUERY);
            cursor.subCursors.add(localCursor);
        }
    }

    /**
     * Runs the queries for the given cursor, and indexes all nodes that are returned.
     * @param cursor the cursor with query and offset information
     * @param queryType the type of query
     * @throws SearchQueryException is the query to create the index out of failed
     * @throws IOException if the Lucene index could not be written to
     */
    public void indexQuery(IndexCursor cursor, int queryType) throws SearchQueryException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("index builder "+cursor.elementManager.getName());
        }
        boolean cursorEmpty = true;
        for (NodeIterator i = getNodeIterator(cursor,queryType); i.hasNext();) {
            if (cursorEmpty) {
                if (queryType == LIMITED_QUERY) {
                    runSubCursors(cursor);
                }
                cursorEmpty = false;
            }
            Node node = i.nextNode();
            int nodeNumber = -1;
            if (cursor.isMultiLevel) {
                nodeNumber = node.getIntValue(cursor.elementManager.getName() +".number");
            } else {
                nodeNumber = node.getNumber();
            }
            if (nodeNumber != cursor.nodeNumber) {
                if (queryType != SUBQUERY) {
                    indexData(cursor);
                }
                cursor.init(nodeNumber, queryType != SUBQUERY);
                runSubCursors(cursor);
            }
            storeData(node, cursor);
        }
        if (!cursorEmpty && queryType != SUBQUERY) {
            indexData(cursor);
        }
    }

    /**
     * Index the data from the cursor.
     * @param cursor the cursor that holds the data
     * @throws IOException if the Lucene index could not be written to
     */
    public void indexData(IndexCursor cursor) throws IOException {
        if (cursor.nodeNumber != -1) {
            Document document = new Document();
            document.add(Field.Keyword("builder", cursor.elementManager.getName()));
            document.add(Field.Keyword("number", "" + cursor.nodeNumber));
            log.debug("Index node " + cursor.nodeNumber);
            indexData(cursor,document);
            cursor.writer.addDocument(document);
        }
    }

    /**
     * Index the data from the cursor.
     * @param cursor the cursor that holds the data
     * @throws IOException if the Lucene index could not be written to
     */
    public void indexData(IndexCursor cursor, Document document) throws IOException {
        for (Iterator i = cursor.fields.iterator(); i.hasNext(); ) {
            IndexFieldDefinition fieldDefinition = (IndexFieldDefinition)i.next();
            String fieldName = fieldDefinition.alias;
            if (fieldName == null)  fieldName = fieldDefinition.fieldName;
            if (document.getField(fieldName) == null || !fieldDefinition.keyWord) {
                String value = cursor.getFieldDataAsString(fieldName);
                if (fieldDefinition.keyWord) {
                    if (log.isDebugEnabled()) {
                        log.debug("add " + fieldName + " text, keyword" + value);
                    }
                    document.add(Field.Keyword(fieldName, value));
                } else if (fieldDefinition.storeText) {
                    if (log.isDebugEnabled()) {
                        log.trace("add " + fieldName + " text, store");
                    }
                    document.add(Field.Text(fieldName, value));
                } else {
                    if (log.isDebugEnabled()) {
                        log.trace("add " + fieldName + " text, no store");
                    }
                    document.add(Field.UnStored(fieldName, value));
                }
            }
        }
        for (Iterator i = cursor.subCursors.iterator(); i.hasNext(); ) {
            IndexCursor subCursor = (IndexCursor)i.next();
            indexData(subCursor, document);
        }
    }

    boolean shouldIndex(Node node, IndexCursor cursor, String fieldName, String alias) {
        // determine number
        int pos = fieldName.indexOf(".");
        if (pos != -1) {
            node = node.getNodeValue(fieldName.substring(0,pos));
        }
        int number = (node == null) ? -1 : node.getNumber();
        if (!cursor.isIndexed(number, fieldName, alias)) {
            cursor.addToIndexed(number, fieldName, alias);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Store data from field in a node into the cursor
     * @param node the Node to copy data from
     * @param cursor the cursor to hold the data
     */
    public void storeData(Node node, IndexCursor cursor) {
        for (Iterator i = cursor.fields.iterator(); i.hasNext(); ) {
            IndexFieldDefinition fieldDefinition = (IndexFieldDefinition)i.next();
            String fieldName = fieldDefinition.fieldName;
            String alias = fieldDefinition.alias;
            if (alias == null)  alias = fieldDefinition.fieldName;
            String decryptionPassword = fieldDefinition.decryptionPassword;
            if (shouldIndex(node, cursor, fieldName, alias)) {
                // some hackery
                int type = org.mmbase.bridge.Field.TYPE_UNKNOWN;
                if (fieldDefinition.stepField != null) type = fieldDefinition.stepField.getType();
                String documentText = null;
                switch (type) {
                    case org.mmbase.bridge.Field.TYPE_DATETIME : {
                        try {
                            documentText = simpleFormat.format(node.getDateValue(fieldName));
                        } catch (Exception e) {
                            // can't index dates prior to 1970, pretty dumb if you ask me
                        }
                        break;
                    }
                    case org.mmbase.bridge.Field.TYPE_BOOLEAN : {
                        if (log.isDebugEnabled()) {
                            log.trace("add " + alias + " keyword:" + node.getIntValue(fieldName));
                        }
                        documentText = "" + node.getIntValue(fieldName);
                        break;
                    }
                    case org.mmbase.bridge.Field.TYPE_NODE :
                    case org.mmbase.bridge.Field.TYPE_INTEGER :
                    case org.mmbase.bridge.Field.TYPE_LONG :
                    case org.mmbase.bridge.Field.TYPE_DOUBLE :
                    case org.mmbase.bridge.Field.TYPE_FLOAT : {
                        documentText =  node.getStringValue(fieldName);
                        break;
                    }
                    case org.mmbase.bridge.Field.TYPE_UNKNOWN : // unknown field may be binary
                    case org.mmbase.bridge.Field.TYPE_BYTE : {
                        String mimeType = "unknown";
                        if (cursor.isMultiLevel) {
                            int pos = fieldName.indexOf(".");
                            Node subNode = node.getNodeValue(fieldName.substring(0,pos));
                            mimeType = subNode.getStringValue("mimetype");
                        } else {
                            mimeType = node.getStringValue("mimetype");
                        }
                        if (mimeType.equalsIgnoreCase("application/pdf")) {
                            if (log.isDebugEnabled()) {
                                log.trace("index " + alias + " as pdf document");
                            }
                            byte[] rawPdf = node.getByteValue(fieldName);
                            PDDocument pdfDocument = null;
                            try {
                                ByteArrayInputStream input = new ByteArrayInputStream(rawPdf);
                                PDFParser parser = new PDFParser(input);
                                parser.parse();
                                pdfDocument = parser.getPDDocument();
                                if (pdfDocument.isEncrypted()) {
                                    DocumentEncryption decryptor = new DocumentEncryption(pdfDocument);
                                    decryptor.decryptDocument(decryptionPassword); //  configure password?
                                }
                                StringWriter out = new StringWriter();
                                PDFTextStripper stripper = new PDFTextStripper();
                                stripper.writeText(pdfDocument, out);
                                out.close();
                                documentText = out.toString();
                            } catch (InvalidPasswordException e) {
                                log.warn("Incorrect password for encrypted document: "+e.getMessage());
                            } catch (CryptographyException e) {
                                log.warn("Cannot open encrypted document: "+e.getMessage());
                            } catch (IOException e) {
                                log.warn("Cannot read document: "+e.getMessage());
                            } finally {
                                // cleanup to return clean
                                if (pdfDocument != null) {
                                    try {
                                        pdfDocument.close();
                                    } catch (IOException e) {
                                        log.warn("Failed to close document: "+e.getMessage());
                                    }
                                }
                            }
                        } else if (mimeType.equalsIgnoreCase("application/msword")) {
                            if (log.isDebugEnabled()) {
                                log.trace("index " + alias + " as Word document");
                            }
                            byte[] rawDoc = node.getByteValue(fieldName);
                            ByteArrayInputStream input = new ByteArrayInputStream(rawDoc);
                            try {
                                WordExtractor extractor = new WordExtractor();
                                documentText = extractor.extractText(input);
                            } catch (Exception e) {
                                log.warn("Cannot read document: "+e.getMessage());
                            }
                        } else if (mimeType.startsWith("text/")) {
                            if (log.isDebugEnabled()) {
                                log.trace("index " + alias + " as text document");
                            }
                            documentText = node.getStringValue(fieldName);
                        } else {
                            log.warn("Cannot read document: unknown mimetype");
                        }
                        break;
                    }
                    default: {
                        if (log.isDebugEnabled()) {
                            log.trace("index " + alias + " as text");
                        }
                        documentText = node.getStringValue(fieldName);
                    }
                }
                if (documentText != null) {
                    if (fieldDefinition.keyWord) {
                        cursor.storeFieldData(alias, documentText);
                    } else {
                        cursor.storeFieldTextData(alias, documentText);
                    }
                }
            }
        }
    }

    /**
     * Defines a 'cursor' with which to run through the results of a query, and to
     * collect data to index.
     */
    class IndexCursor extends IndexDefinition {

       // map with data to index for each field
        private Map data = new HashMap();

        // set with numbers of nodes indexed so far - used to prevent the indexing
        // of fields already indexed
        private Set indexed = new HashSet();

        // set with subcursors with ersults from subqueries
        private Set subCursors = new HashSet();

        /**
         * Current number of the main element node to index
         */
        int nodeNumber = -1;

        /**
         * Current writer into the index
         */
        IndexWriter writer = null;

        IndexCursor(IndexDefinition indexDefinition) {
            super(indexDefinition);
        }

        IndexCursor(IndexDefinition indexDefinition, IndexWriter writer) {
            super(indexDefinition);
            this.writer = writer;
        }

        /**
         * Initialize the cursor to accept data for a specified (main element node) number.
         */
        void init(int number, boolean clearData) {
            nodeNumber = number;
            if (clearData) {
                data = new HashMap();
                indexed = new HashSet();
                subCursors = new HashSet();
           }
        }

        /**
         * Add a name of a node with the specified number as having been indexed (so it won't be attempted to index it again)
         * @param number the number of the node
         * @param fieldName the name of the field
         * @param alias the alias under which the field is indexed
         */
        void addToIndexed(int number, String fieldName, String alias) {
            indexed.add(number + "_" + fieldName + "_" + alias);
        }

        /**
         * Returns <code>true</code> if a field of a node indicated by the number has already been indexed.
         * @param number the number of the node
         * @param fieldName the name of the field
         * @param alias the alias under which the field is indexed
         */
        boolean isIndexed(int number, String fieldName, String alias) {
            return indexed.contains(number + "_" + fieldName + "_" + alias);
        }

        /**
         * Store textual data for a field to index.
         * The data is merged with any text already stored for indexing if appropriate.
         * @param fieldname the name of the field used for indexing (the 'as' name of a field where appropriate)
         * @param value the textual value to index
         */
        void storeFieldTextData(String fieldName, String value) {
            StringBuffer sb = null;
            try {
                sb = (StringBuffer)data.get(fieldName);
            } catch (ClassCastException cce) {
                log.warn("Tried to store data of '" + fieldName + "' as a standard index, but data was already stored as a special index");
            }
            if (sb == null) {
                sb = new StringBuffer();
            } else {
                sb.append(" ");
            }
            sb.append(value);
            data.put(fieldName, sb);
        }

        /**
         * Store data for a field to index.
         * Data is only stored if it doesn't exist yet for this field.
         * @param fieldname the name of the field used for indexing (the 'as' name of a field where appropriate)
         * @param value the value to index
         */
        void storeFieldData(String fieldName, Object value) {
            Object o = data.get(fieldName);
            if (o == null)  {
                data.put(fieldName, value);
            }
        }

        /**
         * Return the data of a field as a string.
         * @param fieldname the name of the field used for indexing (the 'as' name of a field where appropriate)
         */
        String getFieldDataAsString(String fieldName) {
            Object o = data.get(fieldName);
            if (o != null)  {
                return o.toString();
            } else {
                return "";
            }
        }

    }
}
