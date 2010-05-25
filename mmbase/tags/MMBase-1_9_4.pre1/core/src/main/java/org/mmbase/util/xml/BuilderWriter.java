/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.util.Map.Entry;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.datatypes.DataType;
import org.mmbase.util.xml.EntityResolver;

import org.w3c.dom.*;

/**
 * Class for creating builder configuration documents.
 * Use this class to create a DOM document for a given builder.
 * The document can then be used internally or serialized using a number of
 * utility methods.
 * This writer takes extension of builders (inheritance) into account.
 *
 * XXX This class actually transforms nice builder XML's to stupid automatic XML's in deprecated format, forgetting all comments, alls datatype extensions.
 * TODO: This is broken!
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BuilderWriter extends DocumentWriter  {

    /**
     * If true, the builder will expand when writing.
     * Expanding means that all data of a builder is written, including non-overriden data
     * from parent builders.
     */
    private boolean expandBuilder = false;
    /**
     * Hold a reference to the builder for which to create an XML document.
     */
    private MMObjectBuilder builder;

    /**
     * Constructs the document writer.
     * The constructor calls its super to  create a basic document, based on the builder document type.
     * @param builder the builder for which to create an XML document.
     */
    public BuilderWriter(MMObjectBuilder builder) throws DOMException {
        super("builder", BuilderReader.PUBLIC_ID_BUILDER,
                        EntityResolver.DOMAIN + EntityResolver.DTD_SUBPATH + BuilderReader.DTD_BUILDER);
        this.builder=builder;
        getMessageRetriever("org.mmbase.util.xml.resources.builderwriter");
    }

    /**
     * Generates the document. Can only be called once.
     * @throws DOMException when an error occurred during generation
     */
    protected void generate() throws DOMException {
        Element root=document.getDocumentElement();
        addComment("builder.configuration",builder.getTableName(),root);
        root.setAttribute("name",""+builder.getTableName());
        root.setAttribute("maintainer",builder.getMaintainer());
        root.setAttribute("version",""+builder.getVersion());
        MMObjectBuilder parent=null;
        if (!expandBuilder) {
            parent=builder.getParentBuilder();
            if (parent!=null) {
                root.setAttribute("extends",parent.getTableName());
            }
        }
        // status
        addComment("builder.status",root);
        addContentElement("status","active",root);
        // classfile
        String data=builder.getClass().getName();
        if ((parent==null) || (!parent.getClass().getName().equals(data))) {
            // strip - do we want it like this?
            if (data.startsWith("org.mmbase.module.builders.")) {
                data=data.substring(27);
            }
            addComment("builder.classfile",root);
            addContentElement("classfile",data,root);
        }
        //searchage
        data=builder.getSearchAge();
        if ((parent==null) || (!parent.getSearchAge().equals(data))) {
            addComment("builder.searchage",root);
            addContentElement("searchage",data,root);
        }
        // names
        Element names=document.createElement("names");
        addComment("builder.names",root);
        root.appendChild(names);
        // names.singular
        Map<String,String> datamap=builder.getSingularNames();
        addComment("builder.singular",names);
        for (Entry<String, String> em : datamap.entrySet()) {
            String language = em.getKey();
            String name = em.getValue();
            if ((parent==null) || !(name.equals(parent.getSingularName(language)))) {
                Element elm=addContentElement("singular",name,names);
                elm.setAttribute("xml:lang",language);
            }
        }
        // names.plural
        datamap=builder.getPluralNames();
        addComment("builder.plural",names);
        for (Entry<String, String> em : datamap.entrySet()) {
            String language = em.getKey();
            String name = em.getValue();
            if ((parent==null) || !(name.equals(parent.getPluralName(language)))) {
                Element elm=addContentElement("plural",name,names);
                elm.setAttribute("xml:lang",language);
            }
        }
        // descriptions
        Element descriptions=document.createElement("descriptions");
        addComment("builder.descriptions",root);
        root.appendChild(descriptions);
        // names.description
        datamap=builder.getDescriptions();
        addComment("builder.description",root);
        for (Entry<String, String> em : datamap.entrySet()) {
            String language = em.getKey();
            String description = em.getValue();
            if ((parent==null) || !(description.equals(parent.getDescription(language)))) {
                Element elm=addContentElement("description",description,descriptions);
                elm.setAttribute("xml:lang",language);
            }
        }
        // properties
        Element properties=document.createElement("properties");
        addComment("builder.properties",root);
        root.appendChild(properties);
        // properties.property
        datamap=builder.getInitParameters();
        for (Entry<String, String> em : datamap.entrySet()) {
            String propname= em.getKey();
            String propvalue = em.getValue();
            if ((parent==null) || !(propvalue.equals(parent.getInitParameter(propname)))) {
                Element elm=addContentElement("property",propvalue,properties);
                elm.setAttribute("name",propname);
            }
        }
        // fieldlist
        Element fieldlist=document.createElement("fieldlist");
        addComment("builder.fieldlist",root);
        root.appendChild(fieldlist);
        // obtain all fields defined in the builder
        List<CoreField> fields=builder.getFields(NodeManager.ORDER_CREATE);
        for (CoreField fielddef : fields) {
            // skip otype, cannot occur in a builder xml file (doh)
            String fieldname=fielddef.getName();
            if (fieldname.equals("otype")) continue;
            CoreField parentField = null;
            if (parent != null) {
                parentField = parent.getField(fieldname);
            }
            // check guidata
            Element descriptionsElm = null;
            Map<Locale, String> descriptionsmap = fielddef.getLocalizedDescription().asMap();
            for (Entry<Locale, String> em : descriptionsmap.entrySet()) {
                Locale locale = em.getKey();
                String description = em.getValue();
                if ((parentField==null) || !(description.equals(parentField.getDescription(locale)))) {
                    if (descriptionsElm == null) descriptionsElm = document.createElement("descriptions");
                    Element elm=addContentElement("description", description, descriptionsElm);
                    elm.setAttribute("xml:lang", locale.toString());
                }
            }
            Element guiElm = null;
            Map<Locale, String> guinamemap = fielddef.getLocalizedGUIName().asMap();
            for (Entry<Locale, String> em : guinamemap.entrySet()) {
                Locale locale = em.getKey();
                String name = em.getValue();
                if ((parentField==null) || !(name.equals(parentField.getGUIName(locale)))) {
                    if (guiElm == null) guiElm = document.createElement("gui");
                    Element elm=addContentElement("guiname", name, guiElm);
                    elm.setAttribute("xml:lang", locale.toString());
                }
            }
            Element positionsElm = null;

            // positions
            // input
            int pos=fielddef.getEditPosition();
            if ((parentField==null) || (pos!=parentField.getEditPosition())) {
                if (positionsElm == null) positionsElm = document.createElement("positions");
                addComment("builder.field.editor.positions.input", positionsElm);
                addContentElement("input", "" + pos, positionsElm);
            }
            // list
            pos=fielddef.getListPosition();
            if ((parentField==null) || (pos!=parentField.getListPosition())) {
                if (positionsElm == null) positionsElm = document.createElement("positions");
                addComment("builder.field.editor.positions.list", positionsElm);
                addContentElement("list", "" + pos, positionsElm);
            }
            // search
            pos=fielddef.getSearchPosition();
            if ((parentField==null) || (pos!=parentField.getSearchPosition())) {
                if (positionsElm == null) positionsElm = document.createElement("positions");
                addComment("builder.field.editor.positions.search", positionsElm);
                addContentElement("search", "" + pos, positionsElm);
            }

            Element dataTypeElm = null;
            DataType dataType = fielddef.getDataType();
            if ((parentField == null) || !dataType.equals(parentField.getDataType())) {
                dataTypeElm = (Element) document.importNode(dataType.toXml(), true);
            }

            if ((parentField == null) || (dataTypeElm != null) || (descriptionsElm != null) || (guiElm != null) ||  (positionsElm != null)) {
                addComment("builder.field", fieldname, ""+fielddef.getStoragePosition(), fieldlist);
                Element field=document.createElement("field");
                fieldlist.appendChild(field);
                if (descriptionsElm != null) {
                    addComment("builder.field.descriptions",field);
                    field.appendChild(descriptionsElm);
                }
                if (guiElm != null) {
                    addComment("builder.field.gui",field);
                    field.appendChild(guiElm);
                }
                if (positionsElm != null) {
                    addComment("builder.field.editor",field);
                    Element editor=document.createElement("editor");
                    editor.appendChild(positionsElm);
                    field.appendChild(editor);
                }
                if (dataTypeElm != null) {
                    addComment("builder.field.datatype",field);
                    field.appendChild(dataTypeElm);
                }
                Element db = document.createElement("db");
                addComment("builder.field.db",field);
                field.appendChild(db);
                addComment("builder.field.db.name",db);
                addContentElement("name",fielddef.getName(),db);
                if (parentField==null) {
                    String sType = Fields.getTypeDescription(fielddef.getType());
                    addComment("builder.field.db.type",db);
                    Element dbtype=addContentElement("type",sType,db);
                    String sState = Fields.getStateDescription(fielddef.getState());
                    dbtype.setAttribute("state",sState);
                    int size = fielddef.getMaxLength();
                    if (size>-1) {
                        dbtype.setAttribute("size",""+size);
                    }
                    dbtype.setAttribute("notnull",""+fielddef.isRequired());
                    dbtype.setAttribute("key",""+fielddef.isUnique());
                }
            }
        }
    }

    /**
     * Sets whether the builder will expand when writing.
     * Expanding means that all data of a builder is written, including non-overriden data
     * from parent builders.
     * @param value if true, the buidler will expand
     */
    public void setExpandBuilder(boolean value) {
        expandBuilder=value;
    }

    /**
     * Gets whether the builder will expand when writing.
     * Expanding means that all data of a builder is written, including non-overriden data
     * from parent builders.
     * @return  if true, the buidler will expand
     */
    public boolean getExpandBuilder() {
        return expandBuilder;
    }
}
