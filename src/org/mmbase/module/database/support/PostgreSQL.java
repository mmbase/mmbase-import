/*
	This software is OSI Certified Open Source Software.
	OSI Certified is a certification mark of the Open Source Initiative.

	The license (Mozilla version 1.0) can be read at the MMBase site.
	See http://www.MMBase.org/license
*/
package org.mmbase.module.database.support;

import java.sql.*;
import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.MultiConnection;
import org.mmbase.util.logging.*;

/**
 * 7.2 is the first release of the JDBC Driver that supports the byte data type.
 * The introduction of this functionality in 7.2 has introduced a change in behavior as compared to previous
 * releases. In 7.2 the methods getBytes(), setBytes(), getBinaryStream(), and setBinaryStream() operate
 * on the bytea data type. In 7.1 these methods operated on the OID data type associated with Large Objects.
 * It is possible to revert the driver back to the old 7.1 behavior by setting the compatible property on the
 * Connection to a value of 7.1<br />
 * To use the bytea data type you should simply use the getBytes(), setBytes(), getBinaryStream(), or
 * setBinaryStream() methods.<br />
 * To use the Large Object functionality you can use either the LargeObject API provided by the PostgreSQL JDBC
 * Driver, or by using the getBLOB() and setBLOB() methods.<br />
 *
 * Important: For PostgreSQL, you must access Large Objects within an SQL transaction. You would open a
 * transaction by using the setAutoCommit() method with an input parameter of false.<br />
 *
 * Note: In a future release of the JDBC Driver, the getBLOB() and setBLOB() methods may no longer interact
 * with Large Objects and will instead work on bytea data types. So it is recommended that you use the LargeObject
 * API if you intend to use Large Objects.<br />
 *
 * More info for differences between Postgresql versions:
 * http://www.postgresql.org/idocs/index.php?jdbc-binary-data.html
 *
 *
 * Postgresql driver for MMBase
 * @author Eduard Witteveen
 * @version $Id: PostgreSQL.java,v 1.3 2004-01-08 16:30:02 robmaris Exp $
 */
public class PostgreSQL extends Sql92SingleFields implements MMJdbc2NodeInterface   {
    private static Logger log = Logging.getLoggerInstance(PostgreSQL72.class.getName());


    protected boolean createSequence() {
        //  CREATE SEQUENCE autoincrement INCREMENT 1
        MultiConnection con = null;
        Statement stmt = null;
        String sql =  "CREATE SEQUENCE "+sequenceTableName()+" INCREMENT 1 START 1";
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
            } 
            try {
                if(stmt!=null) stmt.close();
                // con.rollback();
                con.close();
            } 
            catch(Exception other) {}
            return false;
        }
        return true;
    }

    protected boolean createNumberCheck() {
        // TODO: has to be generated, when isnt there...
        // CREATE FUNCTION ${BASENAME}_check_number (integer)
        //    RETURNS boolean AS 
        //        'SELECT CASE WHEN (( SELECT COUNT(*) 
        //         FROM ${BASENAME}_object
        //         WHERE ${BASENAME}_object.number = $1 ) > 0 ) THEN 1::boolean ELSE 0::boolean 
        //         END;'
        // LANGUAGE 'sql';
        MultiConnection con = null;
        Statement stmt = null;
        String sql = 
            "CREATE FUNCTION " + numberCheckNameName() +  " (integer) RETURNS boolean AS " + 
            "'SELECT CASE WHEN (( SELECT COUNT(*) FROM " + objectTableName() + 
            " WHERE " + objectTableName() + "." + getNumberString() + " = $1 ) > 0 ) " + 
            " THEN true ELSE false END;' LANGUAGE 'sql';";
        try {
            log.debug("gonna execute the following sql statement: " + sql);
            con = mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
        } catch (SQLException sqle) {
            log.error("error, create number check.."+sql);
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if(stmt != null) stmt.close();
                // con.rollback();
                con.close();
            } 
            catch(Exception other) {}
            return false;
        }
        return true;
    }

    public boolean create(MMObjectBuilder bul) {
        log.debug("create");


        Vector sfields = (Vector) bul.getFields(FieldDefs.ORDER_CREATE);
        if(sfields == null) {
            log.error("sfield was null for builder with name :" + bul);
            return false;
        }

        String fieldList=null;
        // process all the fields..
        for (Enumeration e = sfields.elements();e.hasMoreElements();) {
            String name=((FieldDefs)e.nextElement()).getDBName();
            FieldDefs def = bul.getField(name);
            if (def.getDBState() != org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                // also add explicit the number string to extending table's, this way an index _could_ be created on extending stuff..
                if(!isInheritedField(bul, name) || name.equals(getNumberString())) {
                    log.trace("trying to retrieve the part for field : " + name);
                    String part = getDbFieldDef(def, bul);
                    log.trace("adding field " + name + " with SQL-subpart: " + part);
                    if (fieldList==null) {
                        fieldList = part;
                    } else {
                        fieldList+=", " + part;
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.trace("field: '" + name + "' from builder: '" + bul.getTableName() + "' is inherited field");
                    }
                }
            }
        }
        //if all fields are inherited the field list can be empty
        if (fieldList == null) {
            fieldList = "";
        }

        StringBuffer sql = new StringBuffer("CREATE TABLE " + mmb.baseName+"_"+bul.getTableName() + "(" + fieldList + ")");

        // create the sql statement...
        if(getInheritTableName(bul) != null) {
            sql.append(" INHERITS ( " + mmb.baseName+"_"+getInheritTableName(bul)+" ) ;");
        } else {
            // this one doesnt inherit anything, thus must be the object table?? :p
            if(!createSequence()) return false;
            sql.append(";");
        }
        log.debug("creating a new table with statement: " + sql);

        MultiConnection con=null;
        Statement stmt=null;
        try {
            con = mmb.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.close();
            con.close();
            if(getInheritTableName(bul) != null) return true;
            // when we created the numbertable, we also need to create the numbercheck
            log.info("we created the object table, also creating number check");
            return createNumberCheck();
        } catch (SQLException sqle) {
            log.error("error, could not create table for builder " + bul.getTableName());
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                // con.rollback();
                con.close();
            } catch(Exception other) {}
        }
        return false;
    }

    protected String getDbFieldDef(FieldDefs def, MMObjectBuilder bul) {
        // create the creation line of one field...
        // would be something like : fieldname FIELDTYPE NOT NULL KEY "
        // first get our thingies...
        String  fieldName = getAllowedField(def.getDBName());

        // again an hack for number field thing...
        if(getNumberString().equals(fieldName)) {
            return getNumberString()+" INTEGER PRIMARY KEY \t-- the unique identifier for objects\n";
        }
        boolean fieldRequired = def.getDBNotNull();
        boolean fieldUnique = def.isKey();
        boolean fieldIsReferer = isReferenceField(def, bul);
        String  fieldType = getDbFieldType(def, def.getDBSize(), fieldRequired);
        String result = fieldName + " " + fieldType;
        if(fieldRequired) {
            result += " NOT NULL ";
        }
        if(fieldUnique) {
            result += " UNIQUE ";
        }
        if(fieldIsReferer) {
            // due to bug in postgreslq
            if(getInheritTableName(bul) == null) {
                // we dont inherit anything, save to create a foreign key... for more info see else part
                result += " REFERENCES " + objectTableName() + " ON DELETE CASCADE ";
            } else {
                /**
                 http://www.postgresql.org/idocs/index.php?inherit.html :
                 A limitation of the inheritance feature is that indexes (including unique constraints) and foreign key constraints only apply to single tables, not to their inheritance children. Thus, in the above example, specifying that another table's column REFERENCES cities(name)  would allow the other table to contain city names but not capital names. This deficiency will probably be fixed in some future release. 
                 Workaround (i still need more time ;))
                 Jon Obuchowski <jon_obuchowski@terc.edu>
                 2001-11-05 15:03:25-05 Here's a manual method for implementing a foreign key constraint across inherited tables - instead of using the "references" syntax within the dependent table's "CREATE TABLE" statement, specify a custom "CHECK" constraint - this "CHECK" constraint can use the result of a stored procedure/function to verify the existence of a given value for a specific field of a specific table.
                 note 1: I have performed no benchmarking on this approach, so YMMV.
                 note 2: this does not implement the "cascade" aspect of foreign keys, but this may be done using triggers (this is more complex and not covered here).
                 Here's the example (a table "foo" needs a foreign-key reference to the field "test_id" which is inherited across the tables "test", "test_1", "test_2", etc...)
                 first, a simple function is needed to verify that a given value exists in a specific field "test_id" in a specific table "test" (or in any of this inherited tables). this function will return a boolean indicating that the value exists/does not exist in the table, as required by the "CHECK" constraint syntax.
                 CREATE FUNCTION check_test_id (integer)
                 RETURNS boolean AS 'SELECT CASE WHEN (( SELECT COUNT(*) FROM test WHERE test.test_id = $1 ) > 0 ) THEN 1::boolean ELSE 0::boolean END;'
                 LANGUAGE 'sql';
                 now the dependent table can be created. it must include a constraint (in this case, "test_id_foreign_key") which will use the just-created function to verify the integrity of the field's new value.
                 CREATE TABLE foo
                 (
                 test_id INTEGER CONSTRAINT test_id_foreign_key CHECK (check_test_id(test_a.test_id)) ,
                 foo_val VARCHAR (255) NOT NULL
                 );
                 That's it!
                 A useful (if potentially slowly-performing) expansion of this approach would be to use a function able to dynamically perform an existence check for any value on any field in any table, using the field and table names, and the given value. This would ease maintenance by allowing any foreign-key using table to use a single function, instead of creating a custom function for each foreign key referenced.
                 */
                // Still not fixed in postgresql, using this workaround...
                // TODO: triggers for cascading stuff?
                result += " CONSTRAINT " + mmb.baseName + "_" + bul.getTableName() + "_" + fieldName + "_references CHECK ("+numberCheckNameName()+"("+ mmb.baseName + "_" + bul.getTableName()+"."+fieldName+"))";
            }
        }
        // add in comment the gui stuff... nicer when reviewing database..
        result += "\t-- " + def.getGUIName("en")+"(name: '"+def.getGUIName()+"' gui-type: '"+def.getGUIType()+"')\n";
        return result;
    }

    protected int insertRecord(MMObjectBuilder bul,String owner, MMObjectNode node) {
        String tableName = bul.getTableName();
        String sql = insertPreSQL(tableName, ((Vector) bul.getFields(FieldDefs.ORDER_CREATE)).elements(), node);
        MultiConnection con=null;
        PreparedStatement preStmt=null;

        // Insert statements, with fields still empty..
        try {
            // Create the DB statement with DBState values in mind.
            log.debug("executing following insert : " + sql);
            con=bul.mmb.getConnection();

            // support for larger objects...
            con.setAutoCommit(false);
            preStmt=con.prepareStatement(sql);
        } catch (SQLException sqle) {
            log.error("error, could not create table for builder " + tableName);
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
                log.error("\tSQL      : " + sql);
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode: " + se.getErrorCode());
                log.error("\tMessage  : " + se.getMessage());
            }
            try {
                if(preStmt!=null) preStmt.close();
                con.rollback();
                con.setAutoCommit(true);
                con.close();
            } catch(Exception other) {}
            throw new RuntimeException(sqle.toString());
        }


        // when an error occures, we know our field-state info...
        FieldDefs currentField = null;
        int current = 1;
        
        // Now fill the fields
        try {
            preStmt.setEscapeProcessing(false);
            Enumeration enum = ((Vector) bul.getFields(FieldDefs.ORDER_CREATE)).elements();
            while (enum.hasMoreElements()) {
                currentField = (FieldDefs) enum.nextElement();
                String key = currentField.getDBName();
                int DBState = node.getDBState(key);
                if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT) || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
                    if (log.isDebugEnabled()) log.trace("DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+current);
                    setValuePreparedStatement( preStmt, node, key, current);
                    log.trace("we did set the value for field " + key + " with the number " + current);
                    current++;
                } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    log.trace("DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
                } 
                else {
                    if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                        setValuePreparedStatement( preStmt, node, key, current );
                        log.debug("we did set the value for field " + key + " with the number " + current );
                        current++;
                    } else {
                        log.warn("DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
                    }
                }
            }
            preStmt.executeUpdate();
            preStmt.close();
            con.commit();
            con.setAutoCommit(true);
            con.close();
        } catch (SQLException sqle) {
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
                con.rollback();
                con.setAutoCommit(true);
                con.close();
            } 
            catch(Exception other) {}
            throw new RuntimeException(sqle.toString());
        }
        return node.getIntValue("number");
    }

    private String insertPreSQL(String tableName, Enumeration fieldLayout, MMObjectNode node) {
        String fieldNames = null;
        String fieldValues = null;

        // Append the DB elements to the fieldAmounts String.
        while(fieldLayout.hasMoreElements()) {
            String key = ((FieldDefs) fieldLayout.nextElement()).getDBName();
            String fieldName = getAllowedField(key);
            int DBState = node.getDBState(key);
            if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT) || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
                log.trace("Insert: DBState = "+DBState+", adding key: "+key);

                // add the values to our lists....
                if (fieldNames == null) {
                    fieldNames = fieldName;
                } else {
                    fieldNames += ", " + fieldName;
                }
                if (fieldValues == null) {
                    fieldValues = "?";
                } else {
                    fieldValues += ", ?";
                }
            } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                log.trace("Insert: DBState = "+DBState+", skipping key: "+key);
            } else {
                if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                    // add the values to our lists....
                    if (fieldNames == null) {
                        fieldNames = fieldName;
                    } else {
                        fieldNames += ", " + fieldName;
                    }
                    if(fieldValues == null) {
                        fieldValues = "?";
                    } else {
                        fieldValues += ", ?";
                    }
                } else {
                    log.error("Insert: DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
                }
            }
        }
        // WHY DID THIS BEHAVIOUR CHANGE??
        //        String sql = "INSERT INTO "+mmb.baseName+"_"+tableName+" ("+ getNumberString() +", "+ fieldNames+") VALUES ("+node.getIntValue("number")+", "+fieldValues+")";
        String sql = "INSERT INTO "+mmb.baseName+"_"+tableName+" ("+ fieldNames+") VALUES ("+fieldValues+")";
        log.trace("created pre sql: " + sql);
        return sql;
    }

    /**
     * commit this node to the database
     * Has been overridden, since postgresql needs a transaction for storing binaries
     * TODO: verify that this is also needed for 72!
     */
    public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
        //  precommit call, needed to convert or add things before a save
        bul.preCommit(node);

        // log.warn("is it needed, to override this method, and do we want to review this code?");

        // commit the object
        String builderFieldSql = null;
        // boolean isInsrelSubTable = node.parent!=null && node.parent instanceof InsRel && !bul.tableName.equals("insrel");

        // create the prepared statement
        for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
            String key=(String)e.nextElement();
            // a extra check should be added to filter temp values
            // like properties

            // is this key disallowed ? ifso map it back
            key = getAllowedField(key);

            // add the fieldname,.. and do smart ',' mapping
            if (builderFieldSql == null) 
            {
                builderFieldSql = key + "=?";
            } 
            else 
            {
                builderFieldSql += ", " + key+ "=?";
            }
            // not allowed as far as im concerned...
            if(key.equals("number")) 
            {
                log.fatal("trying to change the 'number' field");
                throw new RuntimeException("trying to change the 'number' field");
            } 
            else if(key.equals("otype")) 
            {
                // hmm i dont like the idea of changing the otype..
                log.error("changing the otype field, is this really needed? i dont think so, but hey i dont care..");
            }
        } // add all changed fields...

        // when we had a update...
        if(builderFieldSql != null) {
            String sql = "UPDATE "+mmb.baseName+"_"+bul.tableName+" SET " + builderFieldSql + " WHERE "+getNumberString()+" = "+node.getValue("number");
            log.debug("Temporary SQL statement, which will be filled with parameters : " + sql);

            MultiConnection con = null;
            PreparedStatement stmt = null;
            try {
                // start with the update of builder itselve first..
                con=mmb.getConnection();

                // support binairies
                con.setAutoCommit(false);

                stmt = con.prepareStatement(sql);

                // fill the '?' thingies with the values from the nodes..
                Enumeration changedFields = node.getChanged().elements();
                int currentParameter = 1;
                while(changedFields.hasMoreElements()) {
                    String key = (String) changedFields.nextElement();
                    int type = node.getDBType(key);

                    // for the right type call the right method..
                    setValuePreparedStatement(stmt, node, key, currentParameter);
                    currentParameter++;
                }
                stmt.executeUpdate();
                stmt.close();
                con.commit();
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException sqle) {
                for(SQLException e = sqle;e != null; e = e.getNextException()) {
                    log.error("\tSQLState : " + e.getSQLState());
                    log.error("\tErrorCode : " + e.getErrorCode());
                    log.error("\tMessage : " + e.getMessage());
                }
                log.error(Logging.stackTrace(sqle));
                try {
                    if(stmt != null) stmt.close();
                    con.rollback();
                    con.setAutoCommit(true);
                    con.close();
                } 
                catch(Exception other) {}
                return false;
            }
        } else {
            // tried to update a node without any changes,..
            return true;
        }

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
            else 
            {
                mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
            }
        }

        // done !
        return true;
    }

    public boolean addField(MMObjectBuilder bul,String fieldname) {
        log.info("add field for builder with name: " + bul.getTableName() + " with field with name: " + fieldname);
        
        FieldDefs def = bul.getField(fieldname);
        if(def == null) 
        {
            log.error("could not find field definition for field: "+fieldname+" for builder :" + bul.getTableName());
            return false;
        }
        if (def.getDBState() == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) 
        {
            log.error("could not add field, was defined as an virtual field for field: "+fieldname+" for builder :" + bul.getTableName());
            return false;
        }
        log.debug("trying to retrieve the part for field : " + def);
        String fieldtype = getDbFieldDef(def, bul);
        return changeMetaData(bul, "ALTER TABEL " +mmb.baseName+"_"+bul.getTableName() + " ADD COLUMN "+fieldname+" "+fieldtype);
    }

    public boolean removeField(MMObjectBuilder bul,String fieldname) {
        log.info("remove field for builder with name: " + bul.getTableName() + " with field with name: " + fieldname);
        return changeMetaData(bul, "ALTER TABEL " +mmb.baseName+"_"+bul.getTableName() + " DROP COLUMN "+fieldname);
    }

    public boolean changeField(MMObjectBuilder bul,String fieldname) {
        log.info("change field for builder with name: " + bul.getTableName() + " with field with name: " + fieldname);
        if(removeField(bul, fieldname)) return addField(bul, fieldname);
        return false;
    }

    /**
     * Retrieves a new unique number, which can be used to inside objectTableName() table
     * @return a new unique number for new nodes or -1 on failure
     */
    public int getDBKey() {
        MultiConnection con=null;
        Statement stmt=null;
        String sql = "SELECT NEXTVAL ('"+  sequenceTableName() + "')";
        int number = -1;
        try {
            log.debug("gonna execute the following sql statement: " + sql);
            con = mmb.getConnection();
            stmt=con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                number=rs.getInt("NEXTVAL");
            } else {
                log.warn("could not retieve the number for new node");
            }
            stmt.close();
            con.close();
        } 
        catch (SQLException sqle) {
            log.error("error, could not retrieve new object number:"+sql);
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                con.close();
            } 
            catch(Exception other) {}
            throw new RuntimeException(sqle.toString());
        }
        log.debug("new object id #"+number);
        return number;
    }

    public boolean createObjectTable(String notUsed) {
        log.warn("create object table is depricated!");
        
        // first create the auto update thingie...
        if(!createSequence()) return false;        

        MultiConnection con = null;
        Statement stmt = null;

        // now update create the object table, with the auto update thignie
        String sql = "CREATE TABLE "+objectTableName()+" (";
        // primary key will mean that and unique and not null...
        // create this one also in a generic way ! --> is now done, this method should be tagged depricated...
        sql += getNumberString()+" INTEGER PRIMARY KEY, \t-- the unique identifier for objects\n";
        sql += getOTypeString()+" INTEGER NOT NULL REFERENCES " + objectTableName() + " ON DELETE CASCADE, \t-- describes the type of object this is\n";
        //is text the right type of field? the size can be broken down to 12 chars
        sql += getOwnerString()+" TEXT NOT NULL  \t-- field for security information\n";
        sql += ")";
        try {
            log.debug("gonna execute the following sql statement: " + sql);
            con = mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
            return createNumberCheck();
        } 
        catch (SQLException sqle) {
            log.error("error, could not create object table.."+sql);
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                // con.rollback();
                con.close();
            } 
            catch(Exception other) {}
            return false;
        }
    }
}
