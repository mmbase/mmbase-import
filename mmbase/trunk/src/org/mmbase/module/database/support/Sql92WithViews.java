/*
        This software is OSI Certified Open Source Software.
        OSI Certified is a certification mark of the Open Source Initiative.

        The license (Mozilla version 1.0) can be read at the MMBase site.
        See http://www.MMBase.org/license
 */
package org.mmbase.module.database.support;

import java.util.*;
import java.sql.*;


import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.util.logging.*;

/**
 * Database driver with views driver for MMBase
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Eduard Witteveen
 * @version $Id: Sql92WithViews.java,v 1.6 2004-03-11 23:25:03 eduard Exp $
 */
public class Sql92WithViews extends Sql92SingleFields implements MMJdbc2NodeInterface {
    private static Logger log = Logging.getLoggerInstance(Sql92WithViews.class.getName());

    protected boolean createSequence() {
        //  CREATE SEQUENCE autoincrement INCREMENT BY 1 START WITH 1
        MultiConnection con = null;
        Statement stmt = null;
        String sql =  "CREATE SEQUENCE "+sequenceTableName()+" INCREMENT BY 1 START WITH 1";
        try {
            log.debug("gonna execute the following sql statement: " + sql);
            con = mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
        } catch (SQLException sqle) {
            log.error("error, could autoincrement sequence.."+sql);
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            } try {
                if(stmt!=null) stmt.close();
                con.close();
            }
            catch(Exception other) {}
            return false;
        }
        return true;
    }

    private String getTableName(MMObjectBuilder bul) {
        boolean inheritedTable = (getInheritTableName(bul) != null);

        String tableName = mmb.baseName+"_"+bul.getTableName();

        // there are 2 types of table's:
        // when it is an inherited table, add the '_TABLE' extension,
        // the view will get the name that shows everything.
        if (inheritedTable) {
            tableName += "_TABLE";
        }
        return tableName;
    }

    public boolean create(MMObjectBuilder bul) {
        String tableName = getTableName(bul);
        log.info("create table definition for:'" + tableName + "'");

        // is this the top level superclass?
        boolean inheritedTable = (getInheritTableName(bul) != null);

        String sql = null;
        MultiConnection con=null;
        Statement stmt=null;

        // look if we really have to create the table
        // maybe only view is missing
        if(!created(tableName) ) {
            // first thing to do, when object table is not created, is creation of sequence,..
            if (!inheritedTable) createSequence();

            sql = "CREATE TABLE " + tableName + "(" + getFieldList(bul, false, true, false) + getFieldContrains(bul) +  ")";

            log.info("create table definition for:'" + tableName  + "' with sql:" + sql );
            try {
                con=mmb.getConnection();
                stmt=con.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                con.close();
            }
            catch (SQLException sqle) {
                log.error("error, could not create table for builder " + bul.getTableName() + " with sql:" + sql);
                for(SQLException e = sqle;e != null; e = e.getNextException()) {
                    log.error("\tSQLState : " + e.getSQLState());
                    log.error("\tErrorCode : " + e.getErrorCode());
                    log.error("\tMessage : " + e.getMessage());
                }
                try {
                    if(stmt!=null) stmt.close();
                    con.close();
                }
                catch(Exception other) {}
                return false;
            }
        }

        // when it is not the inherited table, no need to create an view of it,...
        // so we leave this method..
        if (!inheritedTable) return true;

        String viewName = mmb.baseName + "_" + bul.getTableName();
        String parentTable = mmb.baseName + "_" + getInheritTableName(bul);

        sql = "CREATE VIEW " + viewName + " ( " + getFieldList(bul, true, false, false) + ")";
        sql += " AS SELECT " + getFieldList(bul, true, false, true);
        sql += " FROM " + tableName + ", "  + parentTable;
        sql += " WHERE " + tableName + "." + getNumberString() + " = "  + parentTable + "." + getNumberString();

        log.info("create view definition for:'" + viewName + "' with sql:" + sql );

        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();

            // everything wend well,..
            log.debug("succes creating datastructure for: " + bul.getTableName());
            return true;
        } catch (SQLException sqle) {
            log.error("error, could not create view for builder " + bul.getTableName() + " with sql:" + sql);
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                con.close();
            } catch(Exception other) {}
        }
        return false;
    }

    private String getFieldList(MMObjectBuilder bul, boolean addInheritedFields, boolean addDeclaration, boolean numberReferencesObject) {
        // retrieve the field of the builder
        Vector sfields = (Vector) bul.getFields(FieldDefs.ORDER_CREATE);
        if(sfields == null) {
            log.error("sfield was null for builder with name :" + bul);
            return "";
        }

        String fieldList=null;
        // process all the fields..
        for (Enumeration e = sfields.elements();e.hasMoreElements();) {
            String name=((FieldDefs)e.nextElement()).getDBName();
            FieldDefs def = bul.getField(name);
            if (def.getDBState() != org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                // also add explicit the number string to extending table's,
                // this way an index _could_ be created on extending stuff...
                if(addInheritedFields || !isInheritedField(bul, name) || getNumberString().equals(getAllowedField(name)) ) {
                    String part;
                    if(addDeclaration) {
                        log.trace("trying to retrieve the part for field : " + name);
                        part = getDbFieldDef(def, bul);
                        log.trace("adding field " + name + " with SQL-subpart: " + part);
                    }
                    else {
                        if(addInheritedFields && getNumberString().equals(getAllowedField(name)) && numberReferencesObject) {
                            // numberstring occurs everywhere,... so be explicit from which table,..
                            part =  mmb.baseName + "_" + getInheritTableName(bul) + "." + getNumberString();
                        }
                        else part = getAllowedField(def.getDBName());
                    }
                    // add to the list
                    if (fieldList==null) {
                        fieldList = part;
                    }
                    else {
                        fieldList+=", " + part;
                    }
                }
            }
        }
        //if all fields are inherited the field list can be empty
        if (fieldList == null) fieldList="";

        // return the result
        return fieldList;
    }

    protected String getDbFieldDef(FieldDefs def, MMObjectBuilder bul) {
        // create the creation line of one field...
        // would be something like : fieldname FIELDTYPE NOT NULL KEY "
        // first get our thingies...
        String  fieldName = getAllowedField(def.getDBName());

        boolean fieldRequired = def.getDBNotNull();
        boolean fieldIsReferer = isReferenceField(def, bul);
        boolean fieldIsPrimaryKey = getNumberString().equals(fieldName);
        boolean inheritedTable = (getInheritTableName(bul) != null);

        String fieldType = getDbFieldType(def, def.getDBSize(), fieldRequired);
        String result = fieldName + " " + fieldType;

        if(fieldRequired) {
            result += " NOT NULL ";
        }
        return result;
    }

    private String getFieldContrains(MMObjectBuilder bul) {
        // retrieve the field of the builder
        Vector sfields = (Vector) bul.getFields(FieldDefs.ORDER_CREATE);
		
        if(sfields == null) {
            log.error("sfield was null for builder with name :" + bul);
            return "";
        }
        String constrains = "";
        // process all the fields..
        for (Enumeration e = sfields.elements();e.hasMoreElements();) {
            String name = ((FieldDefs)e.nextElement()).getDBName();
            FieldDefs def = bul.getField(name);
            if (def.getDBState() != org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                String  fieldName = getAllowedField(def.getDBName());
                // first get our thingies...
                boolean fieldIsPrimaryKey = getNumberString().equals(fieldName);
                if(fieldIsPrimaryKey || !isInheritedField(bul, fieldName)) {
                    boolean inheritedTable = getInheritBuilder(bul) != null;
                    boolean fieldIsReferer = isReferenceField(def, bul) && inheritedTable;
                    boolean fieldUnique = def.isKey();

                    if(fieldIsPrimaryKey) {
                        constrains += ",   CONSTRAINT " + constrainName(bul) + " PRIMARY KEY ( " + fieldName + " ) ";
                        // if this is the primary key, it reference's the main table,..
                        fieldIsReferer = inheritedTable;
                    }
                    // we cannot reference to itselve,incase of a reference
                    else if(fieldIsReferer) {
						constrains += ",   CONSTRAINT " +  constrainName(bul) +  " FOREIGN KEY (" + fieldName +")";
                	    constrains += " REFERENCES " + objectTableName() + " (" + getNumberString() + ") ON DELETE CASCADE ";
					}		    
					// is this field unique? (and not primary key, since that one always is
					// unique
                	else if(fieldUnique) {
                    	constrains += ",   CONSTRAINT " +  constrainName(bul) +  " UNIQUE (" + fieldName +") USING INDEX";
					}
                }
            }
        }
        // return the result
        log.debug("fieldcontrains for table:" + bul + " are:" + constrains);
        return constrains;
    }

    /**
     * @returns a unique contrain name
     */
    private String constrainName(MMObjectBuilder bul) {
        return mmb.baseName + "_CONTRAIN_" + getDBKey();
    }

    /**
     * @return a new unique number for new nodes or -1 on failure
     */
    public synchronized int getDBKey() {

        MultiConnection con=null;
        Statement stmt=null;
        // select test8_autoincrement.nextval from dual;
        String sql = "SELECT " + sequenceTableName() + ".NEXTVAL FROM DUAL";
        int number = -1;
        try {
            log.debug("gonna execute the following sql statement: " + sql);
            con = mmb.getConnection();
            stmt=con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                number=rs.getInt("NEXTVAL");
            }
            else {
                log.warn("could not retieve the number for new node");
            }
            stmt.close();
            con.close();
        }
        catch (SQLException sqle) {
            log.error("error, could not retrieve new object number:"+sql);
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
                log.error("\tSQL:" + sql);
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                con.close();
                log.info("gonna try to create a new sequence,... maybe missing,..");
                // try to create a new sequence,.. maybe wasnt there yet,..
                createSequence();
            }
            catch(Exception other) {}
            throw new RuntimeException(sqle.toString());
        }
        log.debug("new object id #"+number);
        return number;
    }

    public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
        int number=node.getIntValue("number");

        Vector rels=bul.getRelations_main(number);
        if (rels != null && rels.size() > 0) {
            // we still had relations ....
            log.error("still relations attachched : delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number);
            return;
        }
        if (number==-1) {
            // this is an undefined node...
            log.error("undefined node : delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number +"("+node+")");
            return;
        }
        // cascading delete should take care of all the references,..
        String sql = "DELETE FROM " + objectTableName() +" WHERE "+getNumberString()+" = "+number+"("+node+")";
        MultiConnection con = null;
        Statement stmt = null;
        log.debug("removing node with sql:" + sql);
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
        }
        catch (SQLException sqle) {
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
                log.error("\tSQL       : " + sql);
                log.error("\tSQLState  : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage   : " + e.getMessage());
            }
            log.error(Logging.stackTrace(sqle));
            try {
                if(stmt != null) stmt.close();
                con.close();
            }
            catch(Exception other) {}
            return;
        }
        if (bul.broadcastChanges) {
            mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"d");
            if (bul instanceof InsRel) {
                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.parent.getTableName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.parent.getTableName(),"r");
            }
        }
    }

    protected int insertRecord(MMObjectBuilder bul,String owner, MMObjectNode node) {
        // insert records recursive, starting with the highest table first,..
        MMObjectBuilder parent = getInheritBuilder(bul);
        if (parent != null) insertRecord(parent, owner, node);

        String tableName = getTableName(bul);
        String sql = insertPreSQL(bul, ((Vector) bul.getFields(FieldDefs.ORDER_CREATE)).elements(), node);
        MultiConnection con=null;
        PreparedStatement preStmt=null;

        // Insert statements, with fields still empty..
        try {
            // Create the DB statement with DBState values in mind.
            log.debug("executing following insert : " + sql);
            con=bul.mmb.getConnection();

            preStmt=con.prepareStatement(sql);
        }
        catch (SQLException sqle) {
            log.error("error, could not insert record for builder " + tableName + " with sql:" + sql);
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
                log.error("\tSQL      : " + sql);
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode: " + se.getErrorCode());
                log.error("\tMessage  : " + se.getMessage());
            }
            try {
                if(preStmt!=null) preStmt.close();
                con.close();
            }
            catch(Exception other) {}
            throw new RuntimeException(sqle.toString());
        }


        // when an error occures, we know our field-state info...
        FieldDefs currentField = null;
        int current = 1;

        // Now fill the fields
        try {
			// why did we do the next?
            // preStmt.setEscapeProcessing(false);
            Enumeration enumeration = ((Vector) bul.getFields(FieldDefs.ORDER_CREATE)).elements();
            while (enumeration.hasMoreElements()) {
                currentField = (FieldDefs) enumeration.nextElement();
                String key = currentField.getDBName();
                int DBState = node.getDBState(key);
                if (!isInheritedField(bul, key) || getNumberString().equals(getAllowedField(key))) {
                    if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
                    || ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM)
                    || ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")))
                    ) {
                        if (log.isDebugEnabled()) log.trace("DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+current+ " value:" + node.getStringValue(key));
                        setValuePreparedStatement( preStmt, node, key, current);
                        log.trace("we did set the value for field " + key + " with the number " + current);
                        current++;
                    }
                    else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                        log.trace("DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
                    }
                    else {
                        log.warn("DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
                    }
                }
				else {
					log.trace("DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
				}
            }
            preStmt.executeUpdate();
            preStmt.close();
            con.close();
        }
        catch (SQLException sqle) {
            log.error("error, could not insert record for builder " + bul.getTableName()+ " current field:("+current+")"+currentField);
            // log.error(Logging.stackTrace(sqle));
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
                log.error("\tSQL      : " + sql);
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode: " + se.getErrorCode());
                log.error("\tMessage  : " + se.getMessage());
            }
            try {
                if(preStmt!=null) preStmt.close();
                con.close();
            }
            catch(Exception other) {}
            throw new RuntimeException(sqle.toString());
        }
        return node.getIntValue("number");
    }

    private String insertPreSQL(MMObjectBuilder bul, Enumeration fieldLayout, MMObjectNode node) {
        String fieldNames = null;
        String fieldValues = null;

        // Append the DB elements to the fieldAmounts String.
        while(fieldLayout.hasMoreElements()) {
            String key = ((FieldDefs) fieldLayout.nextElement()).getDBName();
            String fieldName = getAllowedField(key);
            int DBState = node.getDBState(key);
            // only create for the fields that are of this builder,..
			log.trace("Insert: fieldname:" + key);
            if (!isInheritedField(bul, fieldName)) {
                if ( DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT 
				|| DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM ) {
                    // add the values to our lists....
                    if (fieldNames == null) {
                        fieldNames = fieldName;
                    }
                    else {
                        fieldNames += ", " + fieldName;
                    }
                    if (fieldValues == null) {
                        fieldValues = "?";
                    }
                    else {
                        fieldValues += ", ?";
                    }
                }
                else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    log.trace("Insert: DBState = "+DBState+", skipping vitual field: "+key);
                }
                else {
                    if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                        // add the values to our lists....
                        if (fieldNames == null) {
                            fieldNames = fieldName;
                        }
                        else {
                            fieldNames += ", " + fieldName;
                        }
                        if(fieldValues == null) {
                            fieldValues = "?";
                        }
                        else {
                            fieldValues += ", ?";
                        }
                    }
                    else {
                        log.error("Insert: DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
                    }
                }
            }
			else {
				log.trace("Insert: skipping inherited field" + key);
			}
        }
        String sql = "INSERT INTO " +  getTableName(bul) + " ("+ fieldNames+") VALUES ("+fieldValues+")";
        log.trace("created pre sql: " + sql);
        return sql;
    }

    private boolean commitTable(MMObjectBuilder bul,MMObjectNode node) {
        // commit records recursive, starting with the highest table first,..
        MMObjectBuilder parent = getInheritBuilder(bul);
        if (parent != null) commitTable(parent, node);


        // the update statement,...
        String builderFieldSql = null;

        // create the prepared statement
        for (Enumeration e = node.getChanged().elements();e.hasMoreElements();) {
            String key = (String)e.nextElement();
            if (isBuilderField(bul, key) && !isInheritedField(bul, key)) {
                // is this key disallowed ? ifso map it back
                key = getAllowedField(key);

                // add the fieldname,.. and do smart ',' mapping
                if (builderFieldSql == null) {
                    builderFieldSql = key + "=?";
                }
                else {
                    builderFieldSql += ", " + key+ "=?";
                }
                // not allowed as far as im concerned...
                if(key.equals("number")) {
                    log.fatal("trying to change the 'number' field");
                    throw new RuntimeException("trying to change the 'number' field");
                }
                else if(key.equals("otype")) {
                    // hmm i dont like the idea of changing the otype..
                    log.error("changing the otype field, is this really needed? i dont think so, but hey i dont care..");
                }
            }
        } // add all changed fields...

        // when we had a update...
        // it can happen that in this table, no updates are needed, so dont do it!
        if(builderFieldSql != null) {
            //  precommit call, needed to convert or add things before a save
            bul.preCommit(node);

            String sql = "UPDATE " + getTableName(bul);
            sql += " SET " + builderFieldSql + " WHERE " + getNumberString() + " = " + node.getValue("number");

            MultiConnection con = null;
            PreparedStatement stmt = null;
            try {
                // start with the update of builder itselve first..
                con=mmb.getConnection();

                stmt = con.prepareStatement(sql);

                // fill the '?' thingies with the values from the nodes..
                Enumeration changedFields = node.getChanged().elements();
                int currentParameter = 1;
                while(changedFields.hasMoreElements()) {
                    String key = (String) changedFields.nextElement();
                    if (isBuilderField(bul, key) && !isInheritedField(bul, key)) {
                        int type = node.getDBType(key);

                        // for the right type call the right method..
                        setValuePreparedStatement(stmt, node, key, currentParameter);
                        currentParameter++;
                    }
                }
                stmt.executeUpdate();
                stmt.close();
                con.close();
            }
            catch (SQLException sqle) {
                for(SQLException e = sqle;e != null; e = e.getNextException()) {
                    log.error("\tSQL      : " + sql);
                    log.error("\tSQLState : " + e.getSQLState());
                    log.error("\tErrorCode : " + e.getErrorCode());
                    log.error("\tMessage : " + e.getMessage());
                }
                log.error(Logging.stackTrace(sqle));
                try {
                    if(stmt != null) stmt.close();
                    con.close();
                }
                catch(Exception other) {}
                return false;
            }
        }
        return true;
    }

    public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
        // do the commit on the table,..
        // quit if fails
        if (!commitTable(bul, node)) return false;

        // done database update, so clear changed flags..
        node.clearChanged();

        // broadcast the changes, if nessecary...
        if (bul.broadcastChanges) {
            if (bul instanceof InsRel) {
                bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
                // figure out tables to send the changed relations
                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.parent.getTableName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.parent.getTableName(),"r");
            }
            else {
                mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
            }
        }
        // done !
        return true;
    }

    public boolean created(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        MultiConnection con=null;
        Statement stmt=null;
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
            return true;
        }
        catch (SQLException sqle) {
            try {
                // try to close everything
                stmt.close();
                con.close();
            }
            catch(Exception e) {}
            return false;
        }
    }

    public String getDisallowedField(String allowedfield) {
        log.trace(allowedfield);
		allowedfield = allowedfield.toLowerCase();
        if (allowed2disallowed.containsKey(allowedfield)) {
            allowedfield=(String)allowed2disallowed.get(allowedfield);
        }
        return allowedfield;
    }

    public String getAllowedField(String disallowedfield) {
        log.trace(disallowedfield);
		disallowedfield = disallowedfield.toLowerCase();
        if (disallowed2allowed.containsKey(disallowedfield)) {
            disallowedfield=(String)disallowed2allowed.get(disallowedfield);
        }
        return disallowedfield;
    }


    public boolean addField(MMObjectBuilder bul,String fieldname) {
        log.debug("addField");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("addField");
    }

    public boolean removeField(MMObjectBuilder bul,String fieldname) {
        log.debug("removeField");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("removeField");
    }

    public boolean changeField(MMObjectBuilder bul,String fieldname) {
        log.debug("changeField");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("changeField");
    }

    public boolean createObjectTable(String notUsed) {
        log.debug("createObjectTable");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("createObjectTable");
    }
}
