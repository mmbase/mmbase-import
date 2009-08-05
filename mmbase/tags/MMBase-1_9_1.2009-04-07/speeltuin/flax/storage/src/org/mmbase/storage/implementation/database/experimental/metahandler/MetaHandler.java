/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.storage.implementation.database.experimental.metahandler;

import java.sql.*;
import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;

import org.mmbase.storage.*;
import org.mmbase.storage.implementation.database.experimental.*;

import org.mmbase.util.logging.*;
/**
 * This class is the baseclass for all Metadata Manipulations on the database
 * @author Eduard Witteveen
 * @since MMBase-1.8
 * @version $Id: MetaHandler.java,v 1.1 2005-02-16 13:01:47 eduard Exp $
 */
public class MetaHandler {
    private static final Logger log = Logging.getLoggerInstance(MetaHandler.class);
    protected ViewDatabaseStorageManager parent = null;
            
    public MetaHandler(ViewDatabaseStorageManager caller) {
        parent = caller;
    }
    public String  getSequenceName(String basename) {
        return basename + "_autoincrement";
    }
    public String  getTableName(String viewname) {
        return viewname + "_TABLE";
    }
    public String  getViewName(String viewname) {
        return viewname;
    }
    public boolean environmentExists() {
        // always create enviroment,...
        // untill we know how to detect a sequence,..
        // dont try to get a new sequnce number here
        // this will lead to a never ending loop of "Found broken connection, will try to fix it."
        return false;
    }    
    public boolean environmentCreate() {
        //  CREATE SEQUENCE autoincrement INCREMENT BY 1 START WITH 1
        return parent.executeCommand("CREATE SEQUENCE "+ parent.getSequenceName() +" INCREMENT BY 1 START WITH 1");
    }
    public boolean viewExists(MMObjectBuilder builder) {
        String viewname = parent.getMMObjectBuilderViewName(builder);
        Connection con = null;
        try {
            con = parent.getConnection();
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet res = metaData.getTables(null, null, viewname, null);
            try {
                boolean result = res.next();
                return result;
            } 
            finally {
                res.close();
            }
        } 
        catch (SQLException e) {
            throw new StorageException(e.getMessage());
        } 
        finally {
            if(con != null) {
                try {
                    con.close();
                }
                catch(SQLException e) {
                }
            }
        }
    }
    protected String getNewConstrainName(FieldDefs field, String type) {
        // we need an unique name,...
        // return parent.getMMObjectBuilderViewName(builder) + "_CONTRAIN_" + parent.createKey();
        return field.getParent().getTableName() + "_" + field.getDBName() + "_" + type;
    }
    protected String getFieldType(FieldDefs field) {
        //return ((TypeMapping)typeMappings.get(found)).getType(field.getDBSize());
        return "VARCHAR(22)";
    }
    public boolean viewCreate(MMObjectBuilder builder) {
        MMObjectBuilder inheritedBuilder = builder.getParentBuilder();
        // create the inherited builder first
        if(inheritedBuilder != null) {
            // first check if parent builder exists!
            // this is more an assertion then a real create, altrough i(ew) do think 
            // that this should be the place where the check should be, since i already
            // need to know the builder from which we inherit.
            // furthermore, since the other implementations DO NOT require the order of creation
            // to be the same as the line of inheritance, it is not a bad idea to check this,..
            if(!viewExists(inheritedBuilder)) {
                // create the builder we inherit from
                if(!viewCreate(inheritedBuilder)) {
                    // we could not start create everyting!
                    return false;
                }
            }
        }
        // Create the table (we have )
        String tablename = parent.getMMObjectBuilderTableName(builder);
        
        // no stringbuffer, since not called very often and this is far more clear
        String sql = "CREATE TABLE " + tablename + "(\n";
        List fields = builder.getFields(FieldDefs.ORDER_CREATE);
        
        boolean firstfield = true;
        for (Iterator f = fields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs)f.next();
            // is it a database field, and not of the parent(except the number field)?
            if (field.inStorage()  && (!parent.isInheritedField(field) || field.getDBName().equals(parent.getNumberField().getDBName()))) {
                if(!firstfield) {
                    sql += ",\n";
                }
                sql += "\t";
                sql += parent.getFieldName(field) + "\t" + getFieldType(field);
                if (field.getDBNotNull()) {
                    sql += ("\tNOT NULL");
                }
                // also add comments for this field!
                firstfield = false;
            }            
        }
        sql += "\n)";
        parent.executeCommand(sql);
        
        // create the contrains
        sql = "ALTER TABLE " + tablename + " ADD CONSTRAINT\n";
        for (Iterator f = fields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs)f.next();
            // is it a database field, and not of the parent?
            // attention, 1 field could trigger multiple contrians, so there is the need 
            // to execute them directly
            if (field.inStorage()  && (!parent.isInheritedField(field) || field.getDBName().equals(parent.getNumberField().getDBName()))) {
                // is this a primary key field? (number field)
                if(!parent.isInheritedField(field) && field.getDBName().equals(parent.getNumberField().getDBName())){
                    String command = sql + getNewConstrainName(field, "PK") + " PRIMARY KEY ( " + parent.getFieldName(field) + " )";
                    parent.executeCommand(command);
                }                
                else {
                    // a unique field
                    if(field.isKey()) {
                        //String command = sql + getNewConstrainName(builder) + " UNIQUE ( " + parent.getFieldName(field) + " );";
                        String command = sql + getNewConstrainName(field, "UNIQUE") + " UNIQUE ( " + parent.getFieldName(field) + " )";
                        parent.executeCommand(command);
                    }
                    // Why is the otype builder.xml, since it needs to be also of type TYPE_NODE
                    if(field.getDBType() == FieldDefs.TYPE_NODE || field.getDBName().equals("otype"))
                    {
                        String command = sql + getNewConstrainName(field,"FK") + " FOREIGN KEY ( " + parent.getFieldName(field) + " )\n";
                        command += "REFERENCES " + parent.getMMObjectBuilderTableName(parent.getObjectBuilder()) +  " (" + parent.getFieldName(parent.getNumberField()) + ") ON DELETE CASCADE";
                        parent.executeCommand(command);
                    }
                }
                // maybe we even want to do some other things in a later stage,..
            }            
        }
            
        // create the view
        sql = "CREATE OR REPLACE VIEW " + parent.getMMObjectBuilderViewName(builder) + " (\n";
        firstfield = true;
        for (Iterator f = fields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs)f.next();
            if (field.inStorage()) {
                if(!firstfield) {
                    sql += ",";
                }
                sql += parent.getFieldName(field);
                firstfield = false;
            }
        }
        // inheritedViewName = parent.getMMObjectBuilderViewName(inheritedBuilder);
        sql += "\n)\nAS SELECT ";
        firstfield = true;        
        for (Iterator f = fields.iterator(); f.hasNext();) {
            FieldDefs field = (FieldDefs)f.next();
            if (field.inStorage()) {
                if(!firstfield) {
                    sql += ",";
                }
                if(parent.isInheritedField(field)) {
                    if(inheritedBuilder == null) throw new StorageError("Cannot have a inherited field while we dont extend inherit from a builder!");
                    sql += parent.getMMObjectBuilderViewName(inheritedBuilder) + "." + parent.getFieldName(field);
                }
                else {
                    sql += tablename + "." + parent.getFieldName(field);
                }
                firstfield = false;
            }
        }
        sql += "\nFROM " + tablename;
        if(inheritedBuilder != null) {
                sql += ", " +  parent.getMMObjectBuilderViewName(inheritedBuilder);
                sql += "\nWHERE " + tablename + "." +  parent.getFieldName(parent.getNumberField()) + " =  " + parent.getMMObjectBuilderViewName(inheritedBuilder) + "." + parent.getFieldName(parent.getNumberField());
        }
        return parent.executeCommand(sql);
    }
}