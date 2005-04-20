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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;

import org.pdfbox.encryption.DecryptDocument;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import org.textmining.text.extraction.WordExtractor;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
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
 * @version $Id: Indexer.java,v 1.3 2005-04-20 14:32:12 pierre Exp $
 **/
public class Indexer {

    private static final Logger log = Logging.getLoggerInstance(Indexer.class);

    // Name of the index
    private String index;
    // Collection with queries to run
    private Collection queries;
    // refernce to mmbase
    private MMBase mmbase;
    // format for dates to index
    static private final DateFormat simpleFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * Instantiates an Indexer for a specified collection of queries and options.
     * @param index Name of the index
     * @param queries a collection of QueryDefinitions that select the nodes to index, and contain options on the fields to index.
     * @param mmbase The MMBase instance
     */
    Indexer(String index, Collection queries, MMBase mmbase) {
        this.index = index;
        this.queries = queries;
        this.mmbase = mmbase;
    }

    /**
     * Delete the index for the main element node with the given number.
     * Used in iterative indexing.
     * @param number the numbe rof teh node whose index to delete
     */
    public void deleteIndex(String number) {
        try {
            IndexReader reader = IndexReader.open(index);
            Term term = new Term("number", number);
            reader.delete(term);
            reader.close();
        } catch (Exception e) {
            log.error("Cannot delete Index:"+e.getMessage());
        }
    }

    /**
     * Update the index for the main element node with the given number.
     * Used in iterative indexing.
     * @param number the number of the node whose index to update
     */
    public void updateIndex(String number) {
        deleteIndex(number);
        try {
            MMObjectBuilder root = mmbase.getRootBuilder();
            IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), false);
            MMObjectNode node = root.getNode(number);
            if (node != null) {
                // process all queries
                for (Iterator i = queries.iterator(); i.hasNext();) {
                    QueryDefinition queryDefinition = (QueryDefinition)i.next();
                    if (queryDefinition.elementBuilder == node.getBuilder()) {
                        IndexCursor cursor = new IndexCursor(queryDefinition, writer);
                        cursor.nodeNumber = node.getNumber();
                        indexQuery(cursor, true);
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            log.error("Cannot update Index:"+e.getMessage());
        }
    }

    /**
     * Drop all data in the index and create a new index by running all queries in this set
     * and indexing the results.
     */
    public void fullIndex() {
        try {
            IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), true);
            // process all queries
            for (Iterator i = queries.iterator(); i.hasNext();) {
                QueryDefinition queryDefinition = (QueryDefinition)i.next();
                IndexCursor cursor = new IndexCursor(queryDefinition, writer);
                indexQuery(cursor, false);
            }
            writer.optimize();
            if (log.isDebugEnabled()) {
                log.debug("Total nr documents in index: "+writer.docCount());
            }
            writer.close();
        } catch (Exception e) {
            log.error("Cannot run FullIndex:"+e.getMessage());
        }
    }

    /**
     * Runs a query for the given cursor, an returns the nodes resultering from the call.
     * Each call a maximum of 50 results are returned. The offset in the query is deternmined by teh cursor.
     * Each call to the method updates the cursor offset, so repeatedly calling the method with the
     * same cursor instance iterates through the entire qyery.<br />
     * Note: The method does not take into account any updates that will change the result of running the query,
     * so it is possible that some nodes are not indexed, or indexed multiple times, if changes are made to
     * the database that change the query result.
     * @param cursor the cursor with query and offset information
     * @param limited if <code>true</code>, the query should be limited to the node where the cursor is focused on
     * @return the query result as a list of MMObjectNodes
     * @throws SearchQueryException is the query to create the index out of failed
     */
    protected List getNodes(IndexCursor cursor, boolean limited) throws SearchQueryException {
        if (cursor.offset == cursor.END_OF_QUERY) return null;

        ModifiableQuery query = new ModifiableQuery(cursor.query);
        FieldDefs fieldDef = cursor.elementBuilder.getField(MMObjectBuilder.FIELD_NUMBER);
        StepField numberField = new BasicStepField(cursor.mainStep,fieldDef);
        if (!(cursor.query instanceof NodeSearchQuery)) {
            List fields = new ArrayList(query.getFields());
            fields.add(numberField);
            query.setFields(fields);
            query.setSortOrders(Collections.singletonList(new BasicSortOrder(numberField)));
        }
        if (limited) {
            Constraint constraint = new BasicFieldValueConstraint(numberField, new Integer(cursor.nodeNumber));
            Constraint originalConstraint = query.getConstraint();
            if (originalConstraint != null) {
                constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND).addChild(originalConstraint).addChild(constraint);
            }
            query.setConstraint(constraint);
        }
        query.setOffset(cursor.offset);
        query.setMaxNumber(cursor.maxNodesInQuery);

        if (log.isDebugEnabled()) {
            log.debug("get nodes:" + query);
        }

        List nodes;
        if (cursor.query instanceof NodeSearchQuery) {
            nodes = mmbase.getSearchQueryHandler().getNodes(query, cursor.elementBuilder);
        } else {
            nodes = mmbase.getSearchQueryHandler().getNodes(query, mmbase.getClusterBuilder());
        }
        if (nodes != null && nodes.size() > 0) {
            if (nodes.size() < cursor.maxNodesInQuery) {
                cursor.offset = cursor.END_OF_QUERY;
            } else {
                cursor.offset = cursor.offset + nodes.size();
            }
            return nodes;
        } else {
            cursor.offset = cursor.END_OF_QUERY;
            return null;
        }
    }

    /**
     * Runs the queries for the given cursor, and indexes all nodes that are returned.
     * @param cursor the cursor with query and offset information
     * @param limited if <code>true</code>, the query should be limited to the node where the cursor is focused on
     * @throws SearchQueryException is the query to create the index out of failed
     * @throws IOException if the Lucene index could not be written to
     */
    public void indexQuery(IndexCursor cursor, boolean limited) throws SearchQueryException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("index builder "+cursor.elementBuilder.getTableName());
        }
        List nodes = getNodes(cursor, limited);
        while (nodes != null) {
            if (log.isDebugEnabled()) {
                log.debug("index "+nodes.size()+" nodes.");
            }
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                MMObjectNode node = (MMObjectNode)i.next();
                int nodeNumber = -1;
                if (node instanceof ClusterNode) {
                    nodeNumber = node.getIntValue(cursor.elementBuilder.getTableName() +".number");
                } else {
                    nodeNumber = node.getNumber();
                }
                if (nodeNumber != cursor.nodeNumber) {
                    indexData(cursor);
                    cursor.init(nodeNumber);
                }
                storeData(node, cursor);
            }
            nodes = getNodes(cursor, limited);
        }
        indexData(cursor);
    }

    /**
     * Index the data from the cursor.
     * @param cursor the cursor that holds the data
     * @throws IOException if the Lucene index could not be written to
     */
    public void indexData(IndexCursor cursor) throws IOException {
        if (cursor.nodeNumber != -1) {
            Document document = new Document();
            document.add(Field.Keyword("builder", cursor.elementBuilder.getTableName()));
            document.add(Field.Keyword("number", ""+cursor.nodeNumber));
            for (Iterator i = cursor.fields.iterator(); i.hasNext(); ) {
                FieldDefinition fieldDefinition = (FieldDefinition)i.next();
                String fieldName = fieldDefinition.alias;
                if (fieldName == null)  fieldName = fieldDefinition.fieldName;
                if (document.getField(fieldName) == null) {
                    String value = cursor.getFieldDataAsString(fieldName);
                    if (fieldDefinition.keyWord) {
                        if (log.isDebugEnabled()) {
                            log.trace("add " + fieldName + " text, keyword" + value);
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
            cursor.writer.addDocument(document);
        }
    }

    boolean shouldIndex(MMObjectNode node, IndexCursor cursor, String fieldName) {
        // determine number
        int pos = fieldName.indexOf(".");
        if (pos != -1) {
            node = node.getNodeValue(fieldName.substring(0,pos));
        }
        int number = (node == null) ? -1 : node.getNumber();
        if (!cursor.isIndexed(number, fieldName)) {
            cursor.addToIndexed(number, fieldName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Store data from field in a node into the cursor
     * @param node the MMObjectNode to copy data from
     * @param cursor the cursor to hold the data
     */
    public void storeData(MMObjectNode node, IndexCursor cursor) {
        for (Iterator i = cursor.fields.iterator(); i.hasNext(); ) {
            FieldDefinition fieldDefinition = (FieldDefinition)i.next();
            String fieldName = fieldDefinition.fieldName;
            String alias = fieldDefinition.alias;
            if (alias == null)  alias = fieldDefinition.fieldName;
            String decryptionPassword = fieldDefinition.decryptionPassword;
            FieldDefs field = node.getBuilder().getField(fieldName);
            if (shouldIndex(node, cursor, fieldName)) {
                int type = field.getDBType();
                String documentText = null;
                switch (type) {
                    case FieldDefs.TYPE_DATETIME : {
                        try {
                            String value = simpleFormat.format(node.getDateValue(fieldName));
                            if (log.isDebugEnabled()) {
                                log.trace("add " + alias + " keyword:" + value);
                            }
                            cursor.storeFieldData(alias, value);
                        } catch (Exception e) {
                            // can't index dates prior to 1970, pretty dumb if you ask me
                        }
                        break;
                    }
                    case FieldDefs.TYPE_BOOLEAN : {
                        if (log.isDebugEnabled()) {
                            log.trace("add " + alias + " keyword:" + node.getIntValue(fieldName));
                        }
                        cursor.storeFieldData(alias, "" + node.getIntValue(fieldName));
                        break;
                    }
                    case FieldDefs.TYPE_INTEGER :
                    case FieldDefs.TYPE_LONG :
                    case FieldDefs.TYPE_DOUBLE :
                    case FieldDefs.TYPE_FLOAT : {
                        if (log.isDebugEnabled()) {
                            log.trace("add " + alias + " keyword:" + node.getStringValue(fieldName));
                        }
                        cursor.storeFieldData(alias, node.getStringValue(fieldName));
                        break;
                    }
                    case FieldDefs.TYPE_BYTE : {
                        String mimeType = node.getStringValue("mimetype");
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
                                    DecryptDocument decryptor = new DecryptDocument(pdfDocument);
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
                    cursor.storeFieldTextData(alias, documentText);
                }
            }
        }
    }

    /**
     * Defines options for a field to index.
     */
    static class FieldDefinition {

        /**
         * Name of the field
         */
        String fieldName = null;

        /**
         * If <code>true</code>, the field's value is stored as a keyword.
         */
        boolean keyWord = false;

        /**
         * If <code>true</code>, the field's value is stored and can be returned
         * when search results are given.
         */
        boolean storeText = false;

        /**
         * If not <code>null</code>, this is the fieldname under which the value is indexed.
         * Fieldnames with similar values are pooled together.
         */
        String alias = null;

        /**
         * Password for unlocking the content of binary fields that may contain encrypted pdf documents.
         */
        String decryptionPassword = "";

        FieldDefinition() {
        }

    }

    /**
     * Defines a query and possible options for the fields to index.
     */
    static class QueryDefinition {

        /**
         * The default maximum number of nodes that are returned by a call to the searchqueryhandler.
         */
        public static final int MAX_NODES_IN_QUERY = 50;

        /**
         * The builder whose nodes function as the main 'element' for the query.
         */
        MMObjectBuilder elementBuilder = null;

        /**
         * The builder who represents teh query and is used to resolve fieldnames and other query elements.
         */
        MMObjectBuilder builderResolver = null;

        /**
         * A collection of FieldDefinition objects, containing properties for the fields to index.
         */
        Collection fields = null;

        /**
         * The query to run
         */
        BasicSearchQuery query = null;

        /**
         * The step in the query that targets the main element
         */
        Step mainStep = null;

        /**
         * The maximum number of nodes that are returned by a call to the searchqueryhandler.
         */
        int maxNodesInQuery = MAX_NODES_IN_QUERY;

        QueryDefinition() {
        }

        /**
         * Constructor, copies all data from the specified QueryDefinition object.
         */
        QueryDefinition(QueryDefinition queryDefiniton) {
            this.elementBuilder = queryDefiniton.elementBuilder;
            this.builderResolver = queryDefiniton.builderResolver;
            this.fields = queryDefiniton.fields;
            this.query = queryDefiniton.query;
            this.mainStep = queryDefiniton.mainStep;
            this.maxNodesInQuery = queryDefiniton.maxNodesInQuery;
        }

    }

    /**
     * Defines a 'cursor' with which to run through the results of a query, and to
     * collect data to index.
     */
    class IndexCursor extends QueryDefinition {

        /**
         * Value for the cursor offset to indicate the end of the query.
         */
        public static final int END_OF_QUERY = -1;

       // map with data to index for each field
        private Map data = new HashMap();

        // set with numbers of nodes indexed so far - used to prevent the indexing
        // of fields already indexed
        private Set indexed = new HashSet();

        /**
         * Current number of the main element node to index
         */
        int nodeNumber = -1;

        /**
         * Current offset in the query
         */
        int offset = 0;

        /**
         * Current writer into the index
         */
        IndexWriter writer;

        IndexCursor(QueryDefinition queryDefinition, IndexWriter writer) {
            super(queryDefinition);
            this.writer = writer;
        }

        /**
         * Initialize the cursor to accept data for a specified (main element node) number.
         */
        void init(int number) {
            nodeNumber = number;
            data = new HashMap();
            indexed = new HashSet();
        }

        /**
         * Add a name of a node with the specified number as having been indexed (so it won't be attempted to index it again)
         * @param number the number of the node
         * @param fieldName the name of the field
         */
        void addToIndexed(int number, String fieldName) {
log.info("addToIndexed: "+ number + "_" + fieldName);
            indexed.add(number + "_" + fieldName);
        }

        /**
         * Returns <code>true</code> if a field ofg a node indicated by the number has already been indexed.
         * @param number the number of the node
         * @param fieldName the name of the field
         */
        boolean isIndexed(int number, String fieldName) {
            return indexed.contains(number + "_" + fieldName);
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
            if (sb == null) sb = new StringBuffer();
            sb.append(" ").append(value);
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
