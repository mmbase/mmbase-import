/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import java.text.*;
import java.io.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.mmbase.module.lucene.extraction.*;


import org.mmbase.bridge.*;
import org.mmbase.bridge.util.xml.query.*;

import org.mmbase.util.logging.*;

/**
 * This defines how MMBase Nodes are added to Lucene documents. This also takes into account
 * 'related' nodes, by using the 'sub definitions'.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: MMBaseEntry.java,v 1.11 2006-09-11 10:47:36 michiel Exp $
 **/
public class MMBaseEntry implements IndexEntry {
    static private final Logger log = Logging.getLoggerInstance(MMBaseEntry.class);

    // format for dates to index
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    private final Collection<IndexFieldDefinition> fields;
    private final Node node;
    private final boolean multiLevel; // it this not the same as node instanceof VirtualNode?
    private final NodeManager elementManager;

    private final Collection<IndexDefinition> subQueries;

    // set with numbers of nodes indexed so far - used to prevent the indexing
    // of fields already indexed
    private final Set<String> indexed = new HashSet<String>();

    MMBaseEntry(Node node, Collection<IndexFieldDefinition> fields, boolean multiLevel, NodeManager elementManager, Collection<IndexDefinition> subQueries) {
        this.fields = fields;
        this.multiLevel = multiLevel;
        this.elementManager = elementManager;
        this.subQueries = subQueries;
        this.node = node;
    }

    public String getIdentifier() {
        if (multiLevel) {
            return "" + node.getIntValue(elementManager.getName());
        } else {
            return "" + node.getNumber();
        }
    }

    protected void addStandardKeys(Document document) {
        // always add the 'element' number first, because that ensures that document.get("number") returns 'the' node
        String id = getIdentifier();
        document.add(new Field("number",   id,  Field.Store.YES, Field.Index.UN_TOKENIZED)); 
        if (multiLevel) {
            document.add(new Field("builder", elementManager.getName(),    Field.Store.YES, Field.Index.UN_TOKENIZED)); // keyword
            for (org.mmbase.bridge.Field field : node.getNodeManager().getFields()) {
                if (field.getName().indexOf(".") >=0 ) continue;
                if (id.equals(field.getName())) continue; // was added already
                Node subNode = node.getNodeValue(field.getName());
                document.add(new Field("number",  "" + subNode.getNumber(), Field.Store.YES, Field.Index.UN_TOKENIZED)); // keyword
            }
        } else {
            document.add(new Field("builder",  node.getNodeManager().getName(),    Field.Store.YES, Field.Index.UN_TOKENIZED)); // keyword
        }

    }


    public void index(Document document) {
        Map data = new HashMap();
        if (log.isTraceEnabled()) {
            log.trace("Indexing " + getIdentifier() + "(" + node.getNodeManager().getName() + ")");
        }
        storeData(data);
        addStandardKeys(document);
        for (IndexFieldDefinition fieldDefinition : fields) {
            String fieldName = fieldDefinition.alias;
            if (fieldName == null)  fieldName = fieldDefinition.fieldName;
            if (document.getField(fieldName) == null || !fieldDefinition.keyWord) {
                String value = getFieldDataAsString(data, fieldName);
                if (fieldDefinition.keyWord) {
                    if (log.isDebugEnabled()) {
                        log.debug("add " + fieldName + " text, keyword" + value);
                    }
                    Indexer.addField(document, new Field(fieldName, value, Field.Store.YES, Field.Index.UN_TOKENIZED), fieldDefinition.multiple);
                } else if (fieldDefinition.storeText) {
                    if (log.isDebugEnabled()) {
                        log.trace("add " + fieldName + " text, store");
                    }
                    Indexer.addField(document, new Field(fieldName, value, Field.Store.YES, Field.Index.TOKENIZED), fieldDefinition.multiple);
                } else {
                    if (log.isDebugEnabled()) {
                        log.trace("add " + fieldName + " text, no store");
                    }
                    Indexer.addField(document, new Field(fieldName, value, Field.Store.NO, Field.Index.TOKENIZED), fieldDefinition.multiple);
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.trace("Indexed " + data + " --> " + document);
        }
    }

    public Collection<IndexDefinition> getSubDefinitions() {
        return subQueries != null ? subQueries : Collections.EMPTY_LIST;
    }

    protected boolean shouldIndex(String fieldName, String alias) {
        // determine number
        Node n = node;
        int pos = fieldName.indexOf(".");
        if (pos != -1) {
            n = node.getNodeValue(fieldName.substring(0,pos));
        }
        int number = (n == null) ? -1 : n.getNumber();
        if (! isIndexed(number, fieldName, alias)) {
            addToIndexed(number, fieldName, alias);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Store data from field in a node into the cursor
     * @param map The map of fieldName/value mappings
     */
    protected void storeData(Map map) {
        Cloud cloud = elementManager.getCloud();
        for (Iterator i = fields.iterator(); i.hasNext(); ) {
            IndexFieldDefinition fieldDefinition = (IndexFieldDefinition)i.next();
            String fieldName = fieldDefinition.fieldName;
            String alias = fieldDefinition.alias;
            if (alias == null)  alias = fieldDefinition.fieldName;
            String decryptionPassword = fieldDefinition.decryptionPassword;
            if (shouldIndex(fieldName, alias)) {
                // some hackery
                int type = org.mmbase.bridge.Field.TYPE_UNKNOWN;
                if (fieldDefinition.stepField != null) {
                    org.mmbase.storage.search.StepField sf = fieldDefinition.stepField;
                    org.mmbase.bridge.Field field = cloud.getNodeManager(sf.getStep().getTableName()).getField(sf.getFieldName());
                    type = field.getDataType().getBaseType();
                    // stepField.getType will not do, because this is the actual database type (when multilevel)
                    // changed especially because of datetimes...
                }
                String documentText = null;
                switch (type) {
                    case org.mmbase.bridge.Field.TYPE_DATETIME : {
                        try {
                            documentText = DATE_FORMAT.format(node.getDateValue(fieldName));
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
                    case org.mmbase.bridge.Field.TYPE_BINARY : {
                        String mimeType = "unknown";
                        if (multiLevel) {
                            int pos = fieldName.indexOf(".");
                            Node subNode = node.getNodeValue(fieldName.substring(0,pos));
                            mimeType = "" + subNode.getFunctionValue("mimetype", null);
                        } else {
                            mimeType = "" + node.getFunctionValue("mimetype", null);
                        }
                        Extractor extractor = ContentExtractor.getInstance().findExtractor(mimeType);
                        if (extractor != null) {
                            log.debug("Analyzing document of " + node.getNumber() + " with " + extractor);
                            InputStream input = node.getInputStreamValue(fieldName);
                            try {
                                documentText = extractor.extract(input);
                            } catch (Exception e) {
                                if (log.isDebugEnabled()) {
                                    log.error(e.getMessage(), e);
                                } else {
                                    log.error(e.getMessage());
                                }
                            }
                        } else  {
                            log.warn("Cannot read document: unknown mimetype, trying stringvalue");
                            documentText = node.getStringValue(fieldName);
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
                        storeFieldData(map, alias, documentText);
                    } else {
                        storeFieldTextData(map, alias, documentText);
                    }
                }
            }
        }
    }

    /**
     * Store textual data for a field to index.
     * The data is merged with any text already stored for indexing if appropriate.
     * @param fieldname the name of the field used for indexing (the 'as' name of a field where appropriate)
     * @param value the textual value to index
     * @param data The map of fieldName/value mappings
     */
    void storeFieldTextData(Map data, String fieldName, String value) {
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
    void storeFieldData(Map data, String fieldName, Object value) {
        Object o = data.get(fieldName);
        if (o == null)  {
            data.put(fieldName, value);
        }
    }



    /**
     * Return the data of a field as a string.
     * @param fieldname the name of the field used for indexing (the 'as' name of a field where appropriate)
     */
    String getFieldDataAsString(Map data, String fieldName) {
        Object o = data.get(fieldName);
        if (o != null)  {
            return o.toString();
        } else {
            return "";
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

}


