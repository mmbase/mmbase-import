/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import org.mmbase.module.corebuilders.*;

/**
 * @version $Id: XMLBuilderReader.java,v 1.16 2000-08-18 19:42:13 case Exp $
 *
 * $Log: not supported by cvs2svn $
 */
public class XMLBuilderReader extends XMLBasicReader {

    Document document;
    DOMParser parser;


    public XMLBuilderReader(String filename) {
        super(filename);
    }

    /**
    * get the status of this builder
    */
    public String getStatus() {
        Element e = getElementByPath("builder.status");
        return getElementValue(e);
    }

    /**
    * get the Search Age
    */
    public int getSearchAge() {
        Element e = getElementByPath("builder.searchage");
        String s = getElementValue(e);
        int val=30;
        try {
            val=Integer.parseInt(s);
        } catch(Exception f) {}


        return(val);
    }

    /**
    * get the classfile of this builder
    */
    public String getClassFile() {
        Element e = getElementByPath("builder.classfile");
        return getElementValue(e);
    }


    /**
    * get the fieldDefs of this builder
    */
    public Vector getFieldDefs() {
        Element e = getElementByPath("builder.fieldlist");
        Enumeration fieldEnum = getChildElements(e);
        Vector defs=new Vector();
        int pos=1;
        Element field;
        while (fieldEnum.hasMoreElements()) {
            field = (Element)fieldEnum.nextElement();
            FieldDefs def=decodeFieldDef(field);
            def.DBPos=pos++;
            defs.addElement(def);
        }
        return defs;
    }


    /**
    * decode one fielddef 
    */
    public FieldDefs decodeFieldDef(Element field) {
        Element tmp;

        // create a new FieldDefs we need to fill
        FieldDefs def=new FieldDefs();

        // Gui
        Element gui = getElementByPath(field,"field.gui");
        Enumeration enum = getChildElements(gui,"guiname");
        String lang;
        while (enum.hasMoreElements()) {
            tmp = (Element)enum.nextElement();
            lang = getElementAttributeValue(tmp,"xml:lang");
            def.GUINames.put(lang,getElementValue(tmp));
        }
        /* --- Deal with legacy "name" tag --- */
        enum = getChildElements(gui,"name");
        while (enum.hasMoreElements()) {
            tmp = (Element)enum.nextElement();
            lang = getElementAttributeValue(tmp,"xml:lang");
            def.GUINames.put(lang,getElementValue(tmp));
        }

        tmp = getElementByPath(gui,"gui.guitype");
        if (tmp == null) {
            tmp = getElementByPath(gui,"gui.type");
        }
        def.GUIType = getElementValue(tmp);

        // Editor
        Element editor = getElementByPath(field,"field.editor");
        def.GUIPos = getEditorInputPos(getElementByPath(editor,"editor.input"));
        def.GUIList = getEditorInputPos(getElementByPath(editor,"editor.list"));
        def.GUISearch = getEditorInputPos(getElementByPath(editor,"editor.search"));

        // DB
        Element db = getElementByPath(field,"field.db");
        def.DBName=getElementValue(getElementByPath(db,"db.name"));
        def.DBType=getDBType(getElementByPath(db,"db.type"),def);

        return(def);
    }



    public int getDBType(Element dbtype,FieldDefs def) {
        String val = getElementValue(dbtype);

        if (val.equals("VARCHAR")) def.DBType=FieldDefs.TYPE_STRING;
        if (val.equals("STRING")) def.DBType=FieldDefs.TYPE_STRING;
        if (val.equals("INTEGER")) def.DBType=FieldDefs.TYPE_INTEGER;
        if (val.equals("TEXT")) def.DBType=FieldDefs.TYPE_TEXT;
        if (val.equals("BYTE")) def.DBType=FieldDefs.TYPE_BYTE;
        if (val.equals("FLOAT")) def.DBType=FieldDefs.TYPE_FLOAT;
        if (val.equals("DOUBLE")) def.DBType=FieldDefs.TYPE_DOUBLE;
        if (val.equals("LONG")) def.DBType=FieldDefs.TYPE_LONG;

        String size = getElementAttributeValue(dbtype,"size");
        try {
            def.DBSize = Integer.parseInt(size);
        } catch (Exception e) {}


        String notnull = getElementAttributeValue(dbtype,"notnull");
        if (notnull == null || !notnull.equalsIgnoreCase("true")) {
            def.DBNotNull = false;
        } else {
            def.DBNotNull = true;
        }

        String key = getElementAttributeValue(dbtype,"key");
        if (key == null || !key.equalsIgnoreCase("true")) {
            def.isKey = false;
        } else {
            def.isKey = true;
        }

        String state = getElementAttributeValue(dbtype,"state");
        if (state == null) {
            def.DBState = FieldDefs.DBSTATE_UNKNOWN;
        } else if (state.equals("persistent")) {
            def.DBState=FieldDefs.DBSTATE_PERSISTENT;
        } else if (state.equals("virtual")) {
            def.DBState=FieldDefs.DBSTATE_VIRTUAL;
        } else if (state.equals("system")) {
            def.DBState=FieldDefs.DBSTATE_SYSTEM;
        } else {
            def.DBState=FieldDefs.DBSTATE_UNKNOWN;
        }

        return(def.DBType);
    }

    public int getEditorInputPos(Node n1) {
        try {
            Node n2=n1.getFirstChild();
            int val=Integer.parseInt(n2.getNodeValue());
            return(val);
        } catch(Exception e) {
            return(-1);
        }
    }


    public int getEditorListPos(Node n1) {
        try {
            Node n2=n1.getFirstChild();
            int val=Integer.parseInt(n2.getNodeValue());
            return(val);
        } catch(Exception e) {
            return(-1);
        }
    }


    public int getEditorSearchPos(Node n1) {
        try {
            Node n2=n1.getFirstChild();
            int val=Integer.parseInt(n2.getNodeValue());
            return(val);
        } catch(Exception e) {
            return(-1);
        }
    }

    /**
    * get the properties of this builder
    *
    * XXX Eh, is this in the builder format? -cjr
    */
    public Hashtable getProperties() {
        Hashtable hash=new Hashtable();
        Element e = getElementByPath("builder.names");
        Node n3=e.getFirstChild();
        while (n3!=null) {
            String val=n3.getNodeValue();
            if (n3.getNodeName().equals("property")) {
                Node n4=n3.getFirstChild();
                NamedNodeMap nm=n3.getAttributes();
                if (nm!=null) {
                    Node n5=nm.getNamedItem("name");
                    hash.put(n5.getNodeValue(),n4.getNodeValue());
                }
            }
            n3=n3.getNextSibling();
        }
        return(hash);
    }


    /**
    * get the descriptions of this builder
    */
    public Hashtable getDescriptions() {
        Element e = getElementByPath("builder.descriptions");
        Enumeration enum = getChildElements(e);

        Hashtable hash = new Hashtable();
        Element tmp;
        String lang;
        while (enum.hasMoreElements()) {
            tmp = (Element)enum.nextElement();
            if (tmp.getTagName().equalsIgnoreCase("description")) {
                lang = getElementAttributeValue(tmp,"xml:lang");
                hash.put(lang,getElementValue(tmp));
            }
        }
        return(hash);
    }



    /**
    * get the pluralnames of this builder
    */
    public Hashtable getPluralNames() {
        Hashtable hash=new Hashtable();
        Element e = getElementByPath("builder.names");
        Enumeration enum = getChildElements(e);
        Element tmp;
        String lang;
        while (enum.hasMoreElements()) {
            tmp = (Element)enum.nextElement();
            if (tmp.getTagName().equalsIgnoreCase("plural")) {
                lang = getElementAttributeValue(tmp,"xml:lang");
                hash.put(lang,getElementValue(tmp));
            }
        }
        return(hash);
    }

    /**
    */
    public void getGUIName(Node n1,FieldDefs def) {
        Node n2=n1.getFirstChild();
        if (n2!=null) {
            NamedNodeMap nm=n1.getAttributes();
            if (nm!=null) {
                Node n3=nm.getNamedItem("xml:lang");
                def.GUINames.put(n3.getNodeValue(),n2.getNodeValue());
            }
        }
    }


    /**
    * get the pluralnames of this builder
    */
    public Hashtable getSingularNames() {
        Element e = getElementByPath("builder.names");
        Enumeration enum = getChildElements(e);
        Hashtable hash=new Hashtable();
        Element tmp;
        String lang;
        while (enum.hasMoreElements()) {
            tmp = (Element)enum.nextElement();
            if (tmp.getTagName().equalsIgnoreCase("singular")) {
                lang = getElementAttributeValue(tmp,"xml:lang");
                hash.put(lang,getElementValue(tmp));
            }
        }
        return(hash);
    }


    /**
    * get the version of this application
    */
    public int getBuilderVersion() {
        Element e = getElementByPath("builder");
        String version = getElementAttributeValue(e,"version");
        int n = 0;
        if (version == null) {
            return n;
        } else {
            try {
                n = Integer.parseInt(version);
            } catch (Exception f) {
                n = 0;
            }
            return n;
        }
    }


    /**
    * get the version of this application
    */
    public String getBuilderMaintainer() {
        Element e = getElementByPath("builder");
        return getElementAttributeValue(e,"maintainer");
    }
}

