/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.util.logging.*;

import org.w3c.dom.*;

/**
 * Class for creating builder configuration documents.
 * Use this class to create a DOM document for a given builder.
 * The document can then be used internally or serialized using a number of
 * utility methods.
 * This writer takes extension of builders (inheritance) into account.
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id: BuilderWriter.java,v 1.2 2002-03-26 15:19:22 pierre Exp $
 */
public class BuilderWriter extends DocumentWriter  {

    // logger
    private static Logger log = Logging.getLoggerInstance(BuilderWriter.class.getName());

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
        super("builder", "//MMBase - builder//","http://www.mmbase.org/dtd/builder.dtd");
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
        // names.singularname
        Map datamap=builder.getSingularNames();
        addComment("builder.singularname",root);
        for (Iterator i=datamap.keySet().iterator(); i.hasNext();) {
            String language=(String)i.next();
            String name=(String)datamap.get(language);
            if ((parent==null) || !(name.equals(parent.getSingularName(language)))) {
                Element elm=addContentElement("singularname",name,names);
                elm.setAttribute("xml:lang",language);
            }
        }
        // names.pluralname
        datamap=builder.getPluralNames();
        addComment("builder.pluralname",root);
        for (Iterator i=datamap.keySet().iterator(); i.hasNext();) {
            String language=(String)i.next();
            String name=(String)datamap.get(language);
            if ((parent==null) || !(name.equals(parent.getPluralName(language)))) {
                Element elm=addContentElement("pluralname",name,names);
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
        for (Iterator i=datamap.keySet().iterator(); i.hasNext();) {
            String language=(String)i.next();
            String description=(String)datamap.get(language);
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
        for (Iterator i=datamap.keySet().iterator(); i.hasNext();) {
            String propname=(String)i.next();
            String propvalue=(String)datamap.get(propname);
            if ((parent==null) || !(propvalue.equals(parent.getInitParameter(propname)))) {
                Element elm=addContentElement("property",propvalue,properties);
                elm.setAttribute("name",propname);
            }
        }
        // fieldlist
        Element fieldlist=document.createElement("fieldlist");
        addComment("builder.fieldlist",root);
        root.appendChild(fieldlist);
        // field
        List fields=builder.getFields();
        Collections.sort(fields);
        for (Iterator f=fields.iterator(); f.hasNext();) {
            FieldDefs fielddef=(FieldDefs)f.next();
            // skip otype, cannot occur in a buidler xml file (doh)
            String fieldname=fielddef.getDBName();
            if (fieldname.equals("otype")) continue;
            FieldDefs parentField=null;
            if (parent!=null) {
                parentField=parent.getField(fieldname);
            }
            // check guidata
            Element guielm=null;
            datamap=fielddef.getGUINames();
            for (Iterator i=datamap.keySet().iterator(); i.hasNext();) {
                String language=(String)i.next();
                String name=(String)datamap.get(language);
                if ((parentField==null) || !(name.equals(parentField.getGUIName(language)))) {
                    if (guielm==null) guielm=document.createElement("gui");
                    Element elm=addContentElement("guiname",name,guielm);
                    elm.setAttribute("xml:lang",language);
                }
            }
            String guitype=fielddef.getGUIType();
            if ((parentField==null) || !(guitype.equals(parentField.getGUIType()))) {
                if (guielm==null) guielm=document.createElement("gui");
                addContentElement("guitype",guitype,guielm);
            }
            Element poselm=null;

            // positions
            // input
            int pos=fielddef.getGUIPos();
            if ((parentField==null) || (pos!=parentField.getGUIPos())) {
                if (poselm==null) poselm=document.createElement("positions");
                addComment("builder.field.editor.pos.input",poselm);
                addContentElement("input",""+pos,poselm);
            }
            // list
            pos=fielddef.getGUIList();
            if ((parentField==null) || (pos!=parentField.getGUIList())) {
                if (poselm==null) poselm=document.createElement("positions");
                addComment("builder.field.editor.pos.list",poselm);
                addContentElement("list",""+pos,poselm);
            }
            // search
            pos=fielddef.getGUISearch();
            if ((parentField==null) || (pos!=parentField.getGUISearch())) {
                if (poselm==null) poselm=document.createElement("positions");
                addComment("builder.field.editor.pos.search",poselm);
                addContentElement("search",""+pos,poselm);
            }

            if ((parentField==null) || (guielm!=null) || (poselm!=null)) {
                addComment("builder.field",fieldname,""+fielddef.getDBPos(),fieldlist);
                Element field=document.createElement("field");
                fieldlist.appendChild(field);
                if (guielm!=null) {
                    addComment("builder.field.gui",field);
                    field.appendChild(guielm);
                }
                if (poselm!=null) {
                    addComment("builder.field.editor",field);
                    Element editor=document.createElement("editor");
                    editor.appendChild(poselm);
                    field.appendChild(editor);
                }
                Element db=document.createElement("db");
                addComment("builder.field.db",field);
                field.appendChild(db);
                addComment("builder.field.db.name",db);
                addContentElement("name",fielddef.getDBName(),db);
                if (parentField==null) {
                    String sType = FieldDefs.getDBTypeDescription(fielddef.getDBType());
                    addComment("builder.field.db.type",db);
                    Element dbtype=addContentElement("type",sType,db);
                    String sState = FieldDefs.getDBStateDescription(fielddef.getDBState());
                    dbtype.setAttribute("state",sState);
                    int size=fielddef.getDBSize();
                    if (size>-1) {
                        dbtype.setAttribute("size",""+size);
                    }
                    dbtype.setAttribute("notnull",""+fielddef.getDBNotNull());
                    dbtype.setAttribute("key",""+fielddef.isKey());
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
