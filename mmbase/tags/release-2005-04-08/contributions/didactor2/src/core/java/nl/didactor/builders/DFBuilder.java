package nl.didactor.builders;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import java.util.List;
import java.util.Vector;
import java.util.Enumeration;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Builder for objects with dynamic fields, contains wrappers for adding/removing
 * fields. Dynamic fields are defined by a DTD in the 'typedef' builder, and values
 * are stored in a XML snippet.
 * The API of 'MMObjectBuilder' does not really facilitate these kinds of extensions,
 * so rather large pieces of code need to be duplicated. 
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class DFBuilder extends org.mmbase.module.core.MMObjectBuilder {
    private static Logger log=Logging.getLoggerInstance(DFBuilder.class.getName());
    //private XML dynfields;
    private MMObjectNode builder;
    boolean initialized = false;

    private Vector dynamicFields = new Vector();

    private int maxSearchPos = -1;
    private int maxInputPos = -1;
    private static org.mmbase.util.XMLBasicReader br; 

    /**
     * Initialize this builder
     */
    public boolean init() {
        if (super.init()) {
            return initialize();
        }
        return false;
    }

    /**
     * Initialize this builder
     */
    public boolean initialize() {
        try {
            br = new org.mmbase.util.XMLBasicReader(getConfigFile().getCanonicalPath());
        } catch (IOException e) {
            log.error("Exception while initializing XML basic reader: " + e);
        }
        org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
        String tablename = getTableName();
        //builder = mmb.getBuilder(tablename);
        TypeDef typedef = mmb.getTypeDef();
        Vector nodes = typedef.searchVector("WHERE name='" + tablename + "'");
        if (nodes.size() != 1) {
            log.error("Found " + nodes.size() + " builders with name '" + tablename + "' in typedef builder!");
            return false;
        }
        builder = (MMObjectNode)nodes.get(0);
        initialized = true;
        return true;
    }

    /**
     * Move all the content from the dynamic fields into
     * the XML document.
     */
    private MMObjectNode fillXML(MMObjectNode node) {
        log.debug("fillXML()");
        Document doc = getXMLDocument(node.getStringValue("dynfields"), "dynfields");
        if (doc == null) {
            return node;
        }
        Element rootElement = doc.getDocumentElement();
        log.debug("Found root element, now looping over all dynamic fields");

        for (int i=0; i<dynamicFields.size(); i++) {
            String fieldname = (String)dynamicFields.get(i);
            Object newVal = node.values.get(fieldname);
            if (newVal == null)
                continue;
                
            log.debug("New value for field '" + fieldname + "' has type " + newVal.getClass());
            String newValue = newVal.toString();
            boolean changed = false;
            log.debug("doing enumeration ...");
            for (Enumeration ns = br.getChildElements(rootElement, "field"); ns.hasMoreElements(); ) {
                Element field = (Element)ns.nextElement();
                String name = field.getAttribute("name");
                //String value = br.getElementValue(field);
                if (name.equals(fieldname)) {
                    try {
                        field.removeChild(field.getFirstChild());
                    } catch (Exception e) {}
                    field.appendChild(doc.createTextNode(newValue));
                    changed = true;
                    log.debug("I found a node! done some removal and stuff");
                    continue;
                }
            }
            if (!changed) {
                log.debug("Hmm, no node found, never mind, we will add it");
                Element field = doc.createElement("field");
                field.setAttribute("name", fieldname);
                field.appendChild(doc.createTextNode(newValue));
                rootElement.appendChild(field);
            }
            log.debug("finished this one, going on to the next");
        }
        log.debug("About to set the value...");
        node.setValue("dynfields", xml2string(doc));
        log.debug("Done!");
        return node;
    }

    // javadoc inherited
    public int insert(String owner, MMObjectNode node) {
        if (log.isDebugEnabled()) log.debug("insert(" + owner + "," + node + ")");
        return super.insert(owner, fillXML(node));
    }

    // javadoc inherited
    public MMObjectNode preCommit(MMObjectNode node) {
        if (log.isDebugEnabled()) log.debug("precommit(" + node + ")");
        return fillXML(node);
    }

    // javadoc inherited
    public Object getValue(MMObjectNode node, String field) {
        if (!initialized) {
            // We are not yet initialized, so all calls go
            // to the superclass
            return super.getValue(node, field);
        }

        if (field.equals("dynfields"))
            return super.getValue(node, field);

        if (log.isDebugEnabled()) 
            log.debug("Retrieving field '" + field + "' from node '" + node + "'");
        
        Document doc = getXMLDocument(node.getStringValue("dynfields"), "dynfields");
        if (doc == null) {
            return super.getValue(node, field);
        }
        Element rootElement = doc.getDocumentElement();

        for (Enumeration ns = br.getChildElements(rootElement, "field"); ns.hasMoreElements(); ) {
            Element xfield = (Element)ns.nextElement();
            String name = xfield.getAttribute("name");
            if (name.equals(field)) {
                if (log.isDebugEnabled()) 
                    log.debug("Found value in field: " + br.getElementValue(xfield));
                return br.getElementValue(xfield);
            }
        }

        if (log.isDebugEnabled())
            log.debug("No value found, asking value from parent");
                
        return super.getValue(node, field);
    }
        
    
    // javadoc inherited
    // this method is extended to create FieldDefs for the 
    // dynamic fields
    public void setXMLValues(Vector xmlfields) {
        log.debug("setXMLValues(" + xmlfields + ")");
        dynamicFields = new Vector();
        if (!initialized) {
            if (builder == null) {
                // In this case MMBase is just starting up for the first time
                // on an empty database. Since there are no dynamic fields
                // defined, we skip all now because it'll fail anyway.
                super.setXMLValues(xmlfields);
                return;
            }
        }
        int dbPos = xmlfields.size() + 1;
        Document doc = getXMLDocument(builder.getStringValue("dynfielddef"), "dynfielddef");
        if (doc == null) {
            super.setXMLValues(xmlfields);
            return;
        }
        log.debug("We got some dynamic fielddefs too: we will set them also!");

        Element rootElement = doc.getDocumentElement();
        for (Enumeration ns = br.getChildElements(rootElement, "field"); ns.hasMoreElements(); ) {
            Element field = (Element)ns.nextElement();
            FieldDefs def = decodeFieldDef(field);
            def.setDBPos(dbPos++);
            def.setDBState(FieldDefs.DBSTATE_VIRTUAL);
            xmlfields.add(def);
            dynamicFields.add(def.getDBName());
        }
        log.debug("Calling setXMLValues(" + xmlfields + ")");
        super.setXMLValues(xmlfields);
    }

    /**
     * Add a field to the dynamic field XML structure
     */
    public void addField(FieldDefs def) {
        if (def.getDBState() != FieldDefs.DBSTATE_PERSISTENT) {
            super.addField(def);
            return;
        }

        if (!initialized) {
            // we are not initialized, so all calls go to our superclass
            super.addField(def);
            return;
        }
        Document doc = getXMLDocument(builder.getStringValue("dynfielddef"), "dynfielddef");
        if (doc == null) {
            log.error("Document == null, returning");
            return;
        }
        Element rootElement = doc.getDocumentElement();

        Element field = doc.createElement("field");
        rootElement.appendChild(field);

        Element gui = doc.createElement("gui");
          Element guiname = doc.createElement("guiname");
            guiname.setAttribute("xml:lang", "nl");
            guiname.appendChild(doc.createTextNode(def.getGUIName()));
            gui.appendChild(guiname);
          Element guitype = doc.createElement("guitype");
            guitype.appendChild(doc.createTextNode(def.getGUIType()));
            gui.appendChild(guitype);
          field.appendChild(gui);

        Element editor = doc.createElement("editor");
          Element positions = doc.createElement("positions");
            Element input = doc.createElement("input");
              input.appendChild(doc.createTextNode("" + def.getGUIPos()));
              positions.appendChild(input);
            Element list = doc.createElement("list");
              list.appendChild(doc.createTextNode("" + def.getGUIList()));
              positions.appendChild(list);
            Element search = doc.createElement("search");
              search.appendChild(doc.createTextNode("-1"));
              positions.appendChild(search);
            editor.appendChild(positions);
          field.appendChild(editor);
        
        Element db = doc.createElement("db");
          Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(def.getDBName()));
            db.appendChild(name);
          Element type = doc.createElement("type");
            type.setAttribute("state", "persistent");
            type.setAttribute("notnull", "false");
            type.setAttribute("key", "false");
            type.setAttribute("size", "" + def.getDBSize());
            type.appendChild(doc.createTextNode("" + def.getDBTypeDescription()));
            db.appendChild(type);
          field.appendChild(db);

        builder.setValue("dynfielddef", xml2string(doc));
        builder.commit();
        super.addField(def);
        dynamicFields.add(def.getDBName());
    }

    public void removeField(String fieldname) {
        log.error("removeField(" + fieldname + ")");
        log.error("Not yet implemented!");
    }

    /**
     * Convert an org.w3c.dom.Document to a string.
     */
    private String xml2string(Document document) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(new DOMSource(document),new StreamResult(sw));
            return sw.toString();
        } catch  (Exception e){
        }
        return "";
    }

    /**
     * Method copied from org.mmbase.util.XMLBuilderReader
     */
    private FieldDefs decodeFieldDef(Element field) {
        FieldDefs def = new FieldDefs();

        Element db = br.getElementByPath(field, "field.db");
        def.setDBName(br.getElementValue(br.getElementByPath(db, "db.name")));
        def.setDBType(getDBType(br.getElementByPath(db, "db.type"), def));
        decodeFieldDef(field, def);
        return def;
    }

    /**
    * Set database type information for a specified, named FieldDef object
    * using information obtained from the buidder configuration.
    * Method copied from org.mmbase.util.XMLBuilderReader
    * @param elm The element containing the field type information acc. to the buidler xml format
    * @param def The field definition to set the type for
    */
    private int getDBType(Element dbtype,FieldDefs def) {
        String val = br.getElementValue(dbtype);
        def.setDBType(val);
        String size = br.getElementAttributeValue(dbtype,"size");
        try {
            def.setDBSize(Integer.parseInt(size));
        } catch (Exception e) {}
        String notnull = br.getElementAttributeValue(dbtype,"notnull");
        def.setDBNotNull(notnull != null && notnull.equalsIgnoreCase("true"));
        String key = br.getElementAttributeValue(dbtype,"key");
        def.setDBKey(key != null && key.equalsIgnoreCase("true"));
        String state = br.getElementAttributeValue(dbtype,"state");
        def.setDBState(state);
        String doctype = null;
        Attr doctypeattr= dbtype.getAttributeNode("doctype");
        if (doctypeattr!=null) {
            doctype = doctypeattr.getValue();
        }
        def.setDBDocType(doctype);
        return def.getDBType();
    }

    /**
    * Alter a specified, named FieldDef object using information obtained from the buidler configuration.
    * Only GUI information is retrieved and stored (name and type of the field sg=hould already be specified).
    * Method copied from org.mmbase.util.XMLBuilderReader
    * @since MMBase-1.6
    * @param elm The element containing the field information acc. to the buidler xml format
    * @param def The field definition to alter
    */
    private void decodeFieldDef(Element field, FieldDefs def) {
        Element tmp;
        String lang;
        // Gui
        Enumeration enum;

        Element descriptions = br.getElementByPath(field,"field.descriptions");
        if (descriptions!=null) {
            for (enum = br.getChildElements(descriptions,"description"); enum.hasMoreElements(); ) {
                tmp = (Element)enum.nextElement();
                lang = br.getElementAttributeValue(tmp,"xml:lang");
                def.setDescription(lang,br.getElementValue(tmp));
            }
        }

        Element gui = br.getElementByPath(field,"field.gui");
        if (gui!=null) {
            for (enum = br.getChildElements(gui,"guiname"); enum.hasMoreElements(); ) {
                tmp = (Element)enum.nextElement();
                lang = br.getElementAttributeValue(tmp,"xml:lang");
                def.setGUIName(lang,br.getElementValue(tmp));
            }
            // XXX: deprecated tag 'name'
            for(enum = br.getChildElements(gui,"name"); enum.hasMoreElements(); ) {
                tmp = (Element)enum.nextElement();
                lang = br.getElementAttributeValue(tmp,"xml:lang");
                def.setGUIName(lang,br.getElementValue(tmp));
            }
        }

        tmp = br.getElementByPath(gui,"gui.guitype");
        // XXX: deprecated tag 'type'
        if (tmp == null) {
            tmp = br.getElementByPath(gui,"gui.type");
        }
        if (tmp != null) {
            def.setGUIType(br.getElementValue(tmp));
        } else {
            def.setGUIType(""); // it may not be null.
        }
        // Editor
        Element editorpos = br.getElementByPath(field,"field.editor.input");
        if (editorpos != null) {
            int inputPos = getEditorPos(editorpos);
            if (inputPos > -1 && inputPos > maxInputPos) maxInputPos = inputPos;
            def.setGUIPos(inputPos);
        } else {
            def.setGUIPos(++maxInputPos);
        }
        editorpos = br.getElementByPath(field,"field.editor.list");
        if (editorpos!=null) {
            def.setGUIList( getEditorPos(editorpos));
        }
        editorpos = br.getElementByPath(field,"field.editor.search");
        if (editorpos != null) {
            int searchPos = getEditorPos(editorpos);
        if (searchPos > -1 && searchPos > maxSearchPos) maxSearchPos = searchPos;
            def.setGUISearch(searchPos);
        } else {
            def.setGUISearch(++maxSearchPos);
        }
    }


    /**
    * Determine an integer value from an elements body.
    * Used for the List, Search, and Edit position values.
    * Method copied from org.mmbase.util.XMLBuilderReader
    * @param elm The element containing the value.
    * @return the parsed integer
    */
    private int getEditorPos(Element elm) {
        try {
            int val=Integer.parseInt(br.getElementValue(elm));
            return val;
        } catch(Exception e) {
            return -1;
        }
    }

    /**
     * Returns a org.w3c.xml.Document, that contains a root element
     * with the given name. If the XML string that is provided cannot
     * be parsed, a new document is returned.
     */
    private Document getXMLDocument(String xml, String rootelement) {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = dfactory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            log.error("Error while creating new XML Document: " + e);
            return null;
        }
        
        Document doc = null;
        Element rootElement = null;

        if (xml != null && !xml.equals("")) {
            try {
                doc = documentBuilder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            } catch (Exception e) {
                log.warn("Cought exception " + e + " while parsing '" + xml + "', continuing with empty document");
            }
        }

        if (doc != null) {
            NodeList nl = doc.getElementsByTagName(rootelement);
            if (nl.getLength() == 1) {
                rootElement = (Element)nl.item(0);
            }
        } 

        if (rootElement == null || doc == null) {
            doc = documentBuilder.newDocument();
            rootElement = doc.createElement(rootelement);
            doc.appendChild(rootElement);
        } 

        return doc;
    }
}
