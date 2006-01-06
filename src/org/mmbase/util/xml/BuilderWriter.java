/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.bridge.NodeManager;
import org.mmbase.core.CoreField;
import org.mmbase.core.util.Fields;
import org.mmbase.util.XMLEntityResolver;

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
 * @version $Id: BuilderWriter.java,v 1.21 2006-01-06 17:36:03 michiel Exp $
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
                        XMLEntityResolver.DOMAIN + XMLEntityResolver.DTD_SUBPATH + BuilderReader.DTD_BUILDER);
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
        Map datamap=builder.getSingularNames();
        addComment("builder.singular",names);
        for (Iterator i=datamap.entrySet().iterator(); i.hasNext();) {
            Map.Entry em= (Map.Entry)i.next();
            String language=(String)em.getKey();
            String name=(String)em.getValue();
            if ((parent==null) || !(name.equals(parent.getSingularName(language)))) {
                Element elm=addContentElement("singular",name,names);
                elm.setAttribute("xml:lang",language);
            }
        }
        // names.plural
        datamap=builder.getPluralNames();
        addComment("builder.plural",names);
        for (Iterator i=datamap.entrySet().iterator(); i.hasNext();) {
            Map.Entry em= (Map.Entry)i.next();
            String language=(String)em.getKey();
            String name=(String)em.getValue();
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
        for (Iterator i=datamap.entrySet().iterator(); i.hasNext();) {
            Map.Entry em= (Map.Entry)i.next();
            String language=(String)em.getKey();
            String description=(String)em.getValue();
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
        for (Iterator i=datamap.entrySet().iterator(); i.hasNext();) {
            Map.Entry em= (Map.Entry)i.next();
            String propname=(String)em.getKey();
            String propvalue=(String)em.getValue();
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
        List fields=builder.getFields(NodeManager.ORDER_CREATE);
        for (Iterator f=fields.iterator(); f.hasNext();) {
            CoreField fielddef=(CoreField)f.next();
            // skip otype, cannot occur in a builder xml file (doh)
            String fieldname=fielddef.getName();
            if (fieldname.equals("otype")) continue;
            CoreField parentField = null;
            if (parent != null) {
                parentField = parent.getField(fieldname);
            }
            // check guidata
            Element descelm=null;
            datamap = fielddef.getLocalizedDescription().asMap();
            for (Iterator i=datamap.entrySet().iterator(); i.hasNext();) {
                Map.Entry em= (Map.Entry)i.next();
                Locale locale = (Locale)em.getKey();
                String description=(String)em.getValue();
                if ((parentField==null) || !(description.equals(parentField.getDescription(locale)))) {
                    if (descelm==null) descelm=document.createElement("descriptions");
                    Element elm=addContentElement("description",description,descelm);
                    elm.setAttribute("xml:lang", locale.toString());
                }
            }
            Element guielm=null;
            datamap = fielddef.getLocalizedGUIName().asMap();
            for (Iterator i=datamap.entrySet().iterator(); i.hasNext();) {
                Map.Entry em= (Map.Entry)i.next();
                Locale locale = (Locale)em.getKey();
                String name=(String)em.getValue();
                if ((parentField==null) || !(name.equals(parentField.getGUIName(locale)))) {
                    if (guielm==null) guielm=document.createElement("gui");
                    Element elm=addContentElement("guiname",name,guielm);
                    elm.setAttribute("xml:lang", locale.toString());
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
            int pos=fielddef.getEditPosition();
            if ((parentField==null) || (pos!=parentField.getEditPosition())) {
                if (poselm==null) poselm=document.createElement("positions");
                addComment("builder.field.editor.pos.input",poselm);
                addContentElement("input",""+pos,poselm);
            }
            // list
            pos=fielddef.getListPosition();
            if ((parentField==null) || (pos!=parentField.getListPosition())) {
                if (poselm==null) poselm=document.createElement("positions");
                addComment("builder.field.editor.pos.list",poselm);
                addContentElement("list",""+pos,poselm);
            }
            // search
            pos=fielddef.getSearchPosition();
            if ((parentField==null) || (pos!=parentField.getSearchPosition())) {
                if (poselm==null) poselm=document.createElement("positions");
                addComment("builder.field.editor.pos.search",poselm);
                addContentElement("search",""+pos,poselm);
            }

            if ((parentField==null) || (descelm!=null) || (guielm!=null) ||  (poselm!=null)) {
                addComment("builder.field", fieldname, ""+fielddef.getStoragePosition(), fieldlist);
                Element field=document.createElement("field");
                fieldlist.appendChild(field);
                if (descelm!=null) {
                    addComment("builder.field.descriptions",field);
                    field.appendChild(descelm);
                }
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
