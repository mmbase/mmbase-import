/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

import org.w3c.dom.*;

import org.mmbase.module.corebuilders.FieldDefs;

import org.mmbase.util.logging.*;

/**
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @version $Id: XMLBuilderReader.java,v 1.22 2002-03-19 20:47:39 eduard Exp $
 */
public class XMLBuilderReader extends XMLBasicReader {

    // logger
    private static Logger log = Logging.getLoggerInstance(XMLBuilderReader.class.getName());

    public XMLBuilderReader(String filename) {
        super(filename);
    }

    /**
    * get the status of this builder
    */
    public String getStatus() {
        return getElementValue("builder.status");
    }

    /**
    * get the Search Age
    */
    public int getSearchAge() {
        String s = getElementValue("builder.searchage");
        int val=30;
        try {
            val=Integer.parseInt(s);
        } catch(Exception f) {}


        return val;
    }

    /**
    * get the classfile of this builder
    */
    public String getClassFile() {
        return getElementValue("builder.classfile");
    }

    /**
    * get the fieldDefs of this builder
    */
    public Vector getFieldDefs() {
        Vector results=new Vector();
        int pos=1;
        for(Enumeration ns= getChildElements("builder.fieldlist","field"); ns.hasMoreElements(); ) {
            Element field = (Element)ns.nextElement();
            FieldDefs def=decodeFieldDef(field);
            def.setDBPos(pos++);
            results.addElement(def);
        }
        return results;
    }

    // Determine an integer value from an elements body.
    // Used for the List, Search, and Edit position values
    private int getEditorPos(Element elm) {
        try {
            int val=Integer.parseInt(getElementValue(elm));
            return val;
        } catch(Exception e) {
            return -1;
        }
    }

    // Construct a FieldDef object using a field Element
    private FieldDefs decodeFieldDef(Element field) {
        Element tmp;
        String lang;

        // create a new FieldDefs we need to fill
        FieldDefs def=new FieldDefs();

        // Gui
        Enumeration enum;
        Element gui = getElementByPath(field,"field.gui");
        for (enum = getChildElements(gui,"guiname"); enum.hasMoreElements(); ) {
            tmp = (Element)enum.nextElement();
            lang = getElementAttributeValue(tmp,"xml:lang");
            def.setGUIName(lang,getElementValue(tmp));
        }
        // XXX: deprecated tag 'name'
        for(enum = getChildElements(gui,"name"); enum.hasMoreElements(); ) {
            tmp = (Element)enum.nextElement();
            lang = getElementAttributeValue(tmp,"xml:lang");
            def.setGUIName(lang,getElementValue(tmp));
        }

        tmp = getElementByPath(gui,"gui.guitype");
        // XXX: deprecated tag 'type'
        if (tmp == null) {
            tmp = getElementByPath(gui,"gui.type");
        }
        def.setGUIType(getElementValue(tmp));

        // Editor
        Element editor = getElementByPath(field,"field.editor");
        def.setGUIPos( getEditorPos(getElementByPath(editor,"editor.input")) );
        def.setGUIList( getEditorPos(getElementByPath(editor,"editor.list")) );
        def.setGUISearch( getEditorPos(getElementByPath(editor,"editor.search")) );

        // DB
        Element db = getElementByPath(field,"field.db");
        def.setDBName(getElementValue(getElementByPath(db,"db.name")));
        def.setDBType(getDBType(getElementByPath(db,"db.type"),def));

        return def;
    }

    // Fill db type info into a FieldDefs object using a dbtype Element
    private int getDBType(Element dbtype,FieldDefs def) {
        String val = getElementValue(dbtype);
        def.setDBType(val);
        String size = getElementAttributeValue(dbtype,"size");
        try {
            def.setDBSize(Integer.parseInt(size));
        } catch (Exception e) {}
        String notnull = getElementAttributeValue(dbtype,"notnull");
        def.setDBNotNull(notnull != null && notnull.equalsIgnoreCase("true"));
        String key = getElementAttributeValue(dbtype,"key");
        def.setDBKey(key != null && key.equalsIgnoreCase("true"));
        String state = getElementAttributeValue(dbtype,"state");
        def.setDBState(state);
        String doctype = getElementAttributeValue(dbtype,"doctype");
        if(doctype.equals("")) {
            // i want to be null, when not specified inside the builder file..
            // but getElementAttributeValue makes it automatic ""... 
            // you will never know difference between empty attribute and not specified            
            doctype = null;
        }
        def.setDBDocType(doctype);
        return def.getDBType();
    }


    /**
     * Get the properties of this builder
     */
    public Hashtable getProperties() {
        Hashtable results=new Hashtable();
        Element p;
        String name, value;
        for(Enumeration enum = getChildElements("builder.properties","property");
                        enum.hasMoreElements(); ) {
            p = (Element)enum.nextElement();
            name = getElementAttributeValue(p,"name");
            value = getElementValue(p);
            results.put(name,value);
        }
        return results;
    }


    /**
     * get the descriptions of this builder
     */
    public Hashtable getDescriptions() {
        Hashtable results = new Hashtable();
        Element tmp;
        String lang;
        for (Enumeration enum = getChildElements("builder.descriptions","description");
             enum.hasMoreElements(); ) {
            tmp = (Element)enum.nextElement();
            lang = getElementAttributeValue(tmp,"xml:lang");
            results.put(lang,getElementValue(tmp));
        }
        return results;
    }

    /**
     * get the pluralnames of this builder
     */
    public Hashtable getPluralNames() {
        Hashtable results=new Hashtable();
        Element tmp;
        String lang;
        for (Enumeration enum = getChildElements("builder.names","plural");
             enum.hasMoreElements(); ) {
            tmp = (Element)enum.nextElement();
            lang = getElementAttributeValue(tmp,"xml:lang");
            results.put(lang,getElementValue(tmp));
        }
        return results;
    }

    /**
     * get the pluralnames of this builder
     */
    public Hashtable getSingularNames() {
        Hashtable results=new Hashtable();
        Element tmp;
        String lang;
        for (Enumeration enum = getChildElements("builder.names","singular");
             enum.hasMoreElements(); ) {
            tmp = (Element)enum.nextElement();
            lang = getElementAttributeValue(tmp,"xml:lang");
            results.put(lang,getElementValue(tmp));
        }
        return results;
    }


    /**
     * get the version of this application
     */
    public int getBuilderVersion() {
        String version = getElementAttributeValue("builder","version");
        int n = 0;
        if (!version.equals("")) {
            try {
                n = Integer.parseInt(version);
            } catch (Exception f) {}
        }
        return n;
    }


    /**
     * get the version of this application
     */
    public String getBuilderMaintainer() {
        String tmp=getElementAttributeValue("builder","maintainer");
        if (tmp!=null && !tmp.equals("")) {
            return tmp;
        } else {
            return "mmbase.org";
        }
    }
}
