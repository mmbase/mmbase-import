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

import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: Indexer.java,v 1.2 2004-12-21 12:07:25 pierre Exp $
 **/
public class Indexer {

    private static final Logger log = Logging.getLoggerInstance(Indexer.class);

    private String index;
    private Map buildersToIndex;
    private MMBase mmbase;
    private boolean storeText = false;
    private boolean mergeText = false;
    static private final DateFormat simpleFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    Indexer(String index, Map buildersToIndex, boolean storeText, boolean mergeText, MMBase mmbase) {
        this.index = index;
        this.buildersToIndex = buildersToIndex;
        this.storeText = storeText;
        this.mergeText = mergeText;
        this.mmbase = mmbase;
    }

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

    public Set getFieldSet(MMObjectBuilder builder) {
        Map properties = (Map)buildersToIndex.get(builder.getTableName());
        while (properties == null && builder != null) {
            builder = builder.getParentBuilder();
            properties = (Map)buildersToIndex.get(builder.getTableName());
        }
        if (properties == null) {
            return null;
        } else {
            return (Set) properties.get("fieldset");
        }
    }

    public void updateIndex(String number) {
        deleteIndex(number);
        try {
            MMObjectBuilder root = mmbase.getRootBuilder();
            IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), false);
            MMObjectNode node = root.getNode(number);
            if (node != null) {
                indexNode(node, writer);
            }
            writer.close();
        } catch (Exception e) {
            log.error("Cannot delete Index:"+e.getMessage());
        }
    }

    public void fullIndex() {
        try {
            IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), true);
            // process all builders
            for (Iterator i = buildersToIndex.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry)i.next();
                String builderName = (String)entry.getKey();
                Map properties = (Map)entry.getValue();
                if (!Boolean.TRUE.equals(properties.get("specialization"))) {
                    MMObjectBuilder builder = mmbase.getBuilder(builderName);
                    indexBuilder(builder,writer);
                }
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

    protected List getNodes(MMObjectBuilder builder, int index) throws SearchQueryException {
        NodeSearchQuery query = new NodeSearchQuery(builder);
        FieldDefs fieldDef = builder.getField(MMObjectBuilder.FIELD_NUMBER);
        StepField numberField = query.getField(fieldDef);
        query.addSortOrder(numberField);
        query.setMaxNumber(50);
        BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(numberField, new Integer(index));
        constraint.setOperator(FieldValueConstraint.GREATER);
        query.setConstraint(constraint);
        if (log.isDebugEnabled()) {
            log.debug("get nodes:" + query);
        }
        List nodes = builder.getNodes(query);
        if (nodes!=null && nodes.size() > 0) {
            return nodes;
        } else {
            return null;
        }
    }

    public void indexBuilder(MMObjectBuilder builder, IndexWriter writer) throws SearchQueryException, IOException {
        log.debug("index builder "+builder.getTableName());
        int number = -1;
        List nodes = getNodes(builder,number);
        while (nodes!=null) {
            log.debug("index "+nodes.size()+" nodes.");
            for (Iterator i = nodes.iterator(); i.hasNext();) {
                MMObjectNode node = (MMObjectNode)i.next();
                number = indexNode(node, writer);
            }
            nodes = getNodes(builder,number);
        }
    }

    public int indexNode(MMObjectNode node, IndexWriter writer) throws SearchQueryException, IOException {
        int number = node.getNumber();
        MMObjectBuilder builder = node.getBuilder();
        Set fieldSet = getFieldSet(builder);
        if (fieldSet != null) {
            log.debug("index node "+number);
            Document document = new Document();
            document.add(Field.Keyword("builder", builder.getTableName()));
            document.add(Field.Keyword("number", ""+number));
            for (Iterator j = fieldSet.iterator(); j.hasNext();) {
                String fieldName = (String)j.next();
                FieldDefs field = builder.getField(fieldName);
                // index field
                StringBuffer fullText = null;
                if (field != null) {
                    String text = indexField(node,field, document);
                    if (text != null) {
                        if (fullText == null) fullText = new StringBuffer();
                        fullText = fullText.append(text).append(" ");
                    }
                }
                if (fullText != null) {
                    log.debug("add document full text, no store");
                    document.add(Field.UnStored("fulltext", fullText.toString()));
                }
            }
            writer.addDocument(document);
        }
        return number;
    }

    public String indexField(MMObjectNode node, FieldDefs field, Document document) throws IOException {
        String documentText = null;
        String fieldName = field.getDBName();
        log.debug("index field "+fieldName);
        int type = field.getDBType();
        switch (type) {
            case FieldDefs.TYPE_DATETIME : {
                try {
                    document.add(Field.Keyword(fieldName, simpleFormat.format(node.getDateValue(fieldName))));
                } catch (Exception e) {
                    // can't index dates prior to 1970, pretty dumb if you ask me
                }
                return null;
            }
            case FieldDefs.TYPE_BOOLEAN : {
                document.add(Field.Keyword(fieldName, ""+node.getIntValue(fieldName)));
                return null;
            }
            case FieldDefs.TYPE_INTEGER :
            case FieldDefs.TYPE_LONG :
            case FieldDefs.TYPE_DOUBLE :
            case FieldDefs.TYPE_FLOAT : {
                document.add(Field.Keyword(fieldName, node.getStringValue(fieldName)));
                return null;
            }
            case FieldDefs.TYPE_BYTE : {
                String mimeType = node.getStringValue("mimetype");
                if (mimeType.equalsIgnoreCase("application/pdf")) {
                    log.debug("index pdf document");
                    byte[] rawPdf = node.getByteValue(fieldName);
                    PDDocument pdfDocument = null;
                    try {
                        ByteArrayInputStream input = new ByteArrayInputStream(rawPdf);
                        PDFParser parser = new PDFParser(input);
                        parser.parse();
                        pdfDocument = parser.getPDDocument();
                        if (pdfDocument.isEncrypted()) {
                            DecryptDocument decryptor = new DecryptDocument(pdfDocument);
                            decryptor.decryptDocument("");
                        }
                        StringWriter out = new StringWriter();
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.writeText(pdfDocument, out);
                        out.close();
                        documentText = out.toString();
                    } catch (Exception e) {
                        log.warn("Cannot read document: "+e.getMessage());
                    } finally {
                        // cleanup to return clean
                        if (pdfDocument != null) {
                            pdfDocument.close();
                        }
                    }
                } else if (mimeType.equalsIgnoreCase("application/msword")) {
                    log.debug("index Word document");
                    byte[] rawDoc = node.getByteValue(fieldName);
                    ByteArrayInputStream input = new ByteArrayInputStream(rawDoc);
                    try {
                        WordExtractor extractor = new WordExtractor();
                        documentText = extractor.extractText(input);
                    } catch (Exception e) {
                        log.warn("Cannot read document: "+e.getMessage());
                    }
                } else if (mimeType.startsWith("text/")) {
                    log.debug("index text document");
                    documentText = node.getStringValue(fieldName);
                } else {
                    log.warn("Cannot read document: unknown mimetype");
                }
                break;
            }
            default: {
                //log.info("Field name: '"+name+"'");
                log.debug("index text field");
                documentText = node.getStringValue(fieldName);
            }
        }
        if (documentText != null && documentText.length() > 0) {
            if (mergeText) {
                return documentText;
            } else {
                if (storeText) {
                    log.debug("add document text, store");
                    document.add(Field.Text(fieldName, documentText));
                } else {
                    log.debug("add document text, no store");
                    document.add(Field.UnStored(fieldName, documentText));
                }
            }
        }
        return null;
    }
}
