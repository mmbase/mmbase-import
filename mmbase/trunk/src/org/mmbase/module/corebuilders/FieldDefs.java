/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;


/**
 * One of the core objects, Defines one field of a object type / builder, has its
 * own builder called FieldDef (hitlisted)
 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @author Pierre van Rooden
 * @$Revision: 1.20 $ $Date: 2002-02-22 12:17:07 $
 */
public class FieldDefs  {
    public final static int DBSTATE_MINVALUE = 0;
    public final static int DBSTATE_VIRTUAL = 0;
    public final static int DBSTATE_PERSISTENT = 2;
    public final static int DBSTATE_SYSTEM = 3;
    public final static int DBSTATE_MAXVALUE = 3;
    public final static int DBSTATE_UNKNOWN = -1;

    public final static int TYPE_MINVALUE = 1;   
    public final static int TYPE_STRING = 1;
    public final static int TYPE_INTEGER = 2;
    public final static int TYPE_BYTE = 4;
    public final static int TYPE_FLOAT = 5;
    public final static int TYPE_DOUBLE = 6;
    public final static int TYPE_LONG = 7;
    public final static int TYPE_XML = 8;
    public final static int TYPE_MAXVALUE = 8;   
    public final static int TYPE_UNKNOWN = -1;

    // logger
    private static Logger log = Logging.getLoggerInstance(FieldDefs.class.getName());

    private final static String[] DBSTATES =
        { "UNKNOWN", "VIRTUAL", "UNKNOWN", "PERSISTENT", "SYSTEM" };

    private final static String[] DBTYPES =
        { "UNKNOWN", "STRING", "INTEGER", "UNKNOWN", "BYTE", "FLOAT", "DOUBLE", "LONG", "XML" };

    /**
     * @scope private
     */
    public String GUIName;
    public Hashtable GUINames = new Hashtable();
    public String GUIType;
    public int GUISearch;
    public int GUIList;
    public String DBName;
    public int DBType = TYPE_UNKNOWN;
    public int GUIPos;
    public int DBState = DBSTATE_UNKNOWN;
    public boolean DBNotNull=false;
    public boolean isKey=false;
    public int DBPos;
    public int DBSize=-1;
    public MMObjectBuilder parent = null;

    /**
     * Constructor for default FieldDef.
     */
    public FieldDefs() {
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param GUIName the default GUIName for a field
     * @param GUIType  the GUI type (i.e. "integer' or 'field')
     * @param GUISearch position in the editor for this field when searching
     * @param GUIList position in the editor for this field when listing
     * @param DBName the actual name of the field in the database
     * @param DBType the basic MMBase type of the field
     */
    public FieldDefs(String GUIName, String GUIType, int GUISearch, int GUIList, String DBName,
                     int DBType) {
        this.GUIName=GUIName;
        this.GUIType=GUIType;
        this.GUISearch=GUISearch;
        this.GUIList=GUIList;
        this.DBName=DBName;
        this.DBType=DBType;
        this.GUIPos=2;
        this.DBState=DBSTATE_PERSISTENT;
    }

    /**
     * Constructor for FieldDefs with partially initialized fields.
     * @param GUIName the default GUIName for a field
     * @param GUIType  the GUI type (i.e. "integer' or 'field')
     * @param GUISearch position in the editor for this field when searching
     * @param GUIList position in the editor for this field when listing
     * @param DBName the actual name of the field in the database
     * @param DBType the basic MMBase type of the field
     * @param GUIPos position in the editor for this field when editing
     * @param DBState the state of the field (persistent, virtual, etc.)
     */
    public FieldDefs(String GUIName, String GUIType, int GUISearch, int GUIList, String DBName,
                     int DBType, int GUIPos, int DBState) {
        this.GUIName=GUIName;
        this.GUIType=GUIType;
        this.GUISearch=GUISearch;
        this.GUIList=GUIList;
        this.DBName=DBName;
        this.DBType=DBType;
        this.GUIPos=GUIPos;
        this.DBState=DBState;
    }

    /**
     * Provide a description for the specified type.
     * Useful for debugging, errors or presenting GUI info.
     * @param type the type to get the description of
     * @return the description of the type.
     */
    public static String getDBTypeDescription(int type) {
       if (type<TYPE_MINVALUE || type>TYPE_MAXVALUE) {
            return DBTYPES[0];
       }
       return DBTYPES[type-TYPE_MINVALUE+1];
    }

    /**
     * Provide a description for the specified state.
     * Useful for debugging, errors or presenting GUI info.
     * @param state the state to get the description of
     * @return the description of the state.
     */
    public static String getDBStateDescription(int state) {
       if (state<DBSTATE_MINVALUE || state>DBSTATE_MAXVALUE) {
            return DBSTATES[0];
       }
       return DBSTATES[state-DBSTATE_MINVALUE+1];
    }

    /**
     * Provide an id for the specified mmbase type description
     * @param type the type description to get the id of
     * @return the id of the type.
     */
    public static int getDBTypeId(String type) {
        if (type == null) return DBSTATE_UNKNOWN;
        // XXX: deprecated VARCHAR
        if (type.equals("VARCHAR")) return TYPE_STRING;
        if (type.equals("STRING"))  return TYPE_STRING;
        if (type.equals("XML"))     return TYPE_XML;
        if (type.equals("INTEGER")) return TYPE_INTEGER;
        if (type.equals("BYTE"))    return TYPE_BYTE;
        if (type.equals("FLOAT"))   return TYPE_FLOAT;
        if (type.equals("DOUBLE"))  return TYPE_DOUBLE;
        if (type.equals("LONG"))    return TYPE_LONG;
        return TYPE_UNKNOWN;
    }

    /**
     * Provide an id for the specified mmbase state description
     * @param type the state description to get the id of
     * @return the id of the state.
     */
    public static int getDBStateId(String state) {
        if (state == null) return DBSTATE_UNKNOWN;
        if (state.equals("persistent"))  return DBSTATE_PERSISTENT;
        if (state.equals("virtual")) return DBSTATE_VIRTUAL;
        if (state.equals("system")) return DBSTATE_SYSTEM;
        return DBSTATE_UNKNOWN;
    }

    /**
     * Provide a description for the current type.
     * @return the description of the type.
     */
    public String getDBTypeDescription() {
        return FieldDefs.getDBTypeDescription(DBType);
    }

    /**
     * Provide a description for the current state.
     * @return the description of the state.
     */
    public String getDBStateDescription() {
        return FieldDefs.getDBStateDescription(DBState);
    }

    /**
     * Retrieve the GUI name of the field depending on specified langauge.
     * If the language is not available, the "us" value is returned instead.
     * If that one is unavailable a 'default' is returned.
     * @param lang the language to return the name in
     * @return the GUI Name
     */
    public String getGUIName(String lang) {
        String tmp=(String)GUINames.get(lang);
        if (tmp!=null) {
            return tmp;
        } else {
            return getGUIName();
        }
    }

    /**
     * Retrieve the GUI name of the field.
     * If possible, the "us" value is returned.
     * If that one is unavailable a 'default' is returned.
     * @param lang the language to return the name in
     * @return the GUI Name
     */
    public String getGUIName() {
        String tmp=(String)GUINames.get("us");
        if (tmp!=null) return tmp;
        return GUIName;
    }

    /**
     * Retrieve a Hashtable with all GUI names for this field,
     * accessible by language.
     */
    public Hashtable getGUINames() {
        return GUINames;
    }

    /**
     * Retrieve the GUI type of the field.
     */
    public String getGUIType() {
        return GUIType;
    }

    /**
     * Retrieve the database name of the field.
     */
    public String getDBName() {
        return DBName;
    }

    /**
     * Retrieve the basic MMBase type of the field.
     */
    public int getDBType() {
        return DBType;
    }

    /**
     * Retrieve size of the field.
     * This may not be specified for some field types.
     */
    public int getDBSize() {
        return DBSize;
    }

    /**
     * Retrieve whether the field can be left blank.
     */
    public boolean getDBNotNull() {
        return DBNotNull;
    }

    /**
     * Retrieve the state of the field (persistent, system, or virtual).
     */
    public int getDBState() {
        return DBState;
    }

    /**
     * Retrieve whether the field is a key and thus need be unique.
     */
    public boolean isKey() {
        return isKey;
    }

    /**
     * Retrieve the position of the field when searching.
     * A value of -1 indicates teh field is unavailable during search.
     */
    public int getGUISearch() {
        return GUISearch;
    }

    /**
     * Retrieve the position of the field when listing.
     * A value of -1 indicates the field is unavailable in a list.
     */
    public int getGUIList() {
        return GUIList;
    }

    /**
     * Retrieve the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     */
    public int getGUIPos() {
        return GUIPos;
    }

    /**
     * Retrieve the position of the field in the database table.
     */
    public int getDBPos() {
        return DBPos;
    }

    /**
     * Set the GUI name of the field for a specified langauge.
     * @param lang the language to set the name for
     * @param value the value to set
     */
    public void setGUIName(String country,String value) {
        GUINames.put(country,value);
    }

    /**
     * Set the GUI type of the field.
     * @param value the value to set
     */
    public void setGUIType(String value) {
        GUIType=value;
    }

    /**
     * Set the database name of the field.
     * @param value the value to set
     */
    public void setDBName(String value) {
        DBName=value;
    }

    /**
     * Set the position of the field when listing.
     * A value of -1 indicates teh field is unavailable in a list.
     * @param value the value to set
     */
    public void setGUIList(int value) {
        GUIList=value;
    }

    /**
     * Set the position of the field when editing.
     * A value of -1 indicates the field cannot be edited.
     * @param value the value to set
     */
    public void setGUIPos(int value) {
        GUIPos=value;
    }

    /**
     * Set the position of the field when searching.
     * A value of -1 indicates teh field is unavailable during search.
     * @param value the value to set
     */
    public void setGUISearch(int value) {
        GUISearch=value;
    }

    /**
     * Set size of the field.
     * @param value the value to set
     */
    public void setDBSize(int value) {
        DBSize=value;
    }

    /**
     * Set the basic MMBase type of the field.
     * @param value the id of the type
     */
    public void setDBType(int value) {
        DBType=value;
    }

    /**
     * Set the basic MMBase type of the field, using the type description
     * @param value the name of the type
     */
    public void setDBType(String value) {
        DBType=getDBTypeId(value);
    }

    /**
     * Set the position of the field in the database table.
     * @param value the value to set
     */
    public void setDBPos(int value) {
        DBPos=value;
    }

    /**
     * Set the state of the field (persistent, system, or virtual).
     * @param value the value to set
     */
    public void setDBState(int value) {
        DBState=value;
    }

    /**
     * Set the basic MMBase state of the field, using the state description
     * @param value the name of the state
     */
    public void setDBState(String value) {
        DBState=getDBStateId(value);
    }

    /**
     * Set whether the field is a key and thus need be unique.
     * @param value the value to set
     */
    public void setDBKey(boolean value) {
        isKey=value;
    }

    /**
     * Set whether the field can be left blank.
     * @param value the value to set
     */
    public void setDBNotNull(boolean value) {
        DBNotNull=value;
    }

    /**
     * Retrieves the parent builder for this field
     */
    public MMObjectBuilder getParent() {
        return parent;
    }

    /**
     * Set the parent builder for this field
     * @param parent the fielddefs parent
     */
    public void setParent(MMObjectBuilder parent) {
        this.parent=parent;
    }

    /**
     * Returns a description for this field.
     */
    public String toString() {
        return("DEF GUIName="+getGUIName()+" GUIType="+GUIType+
               " Input="+GUIPos+" Search="+GUISearch+" List="+GUIList+
               " DBname="+DBName+
               " DBType="+getDBTypeDescription()+
               " DBSTATE="+getDBTypeDescription()+
               " DBNOTNULL="+DBNotNull+" DBPos="+DBPos+" DBSIZE="+DBSize+
               " isKey="+isKey);
    }
}
