/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.net.*;
import java.sql.*;

import org.mmbase.storage.database.UnsupportedDatabaseOperationException;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Postgresql driver for MMBase, only works with Postgresql 7.1 + that supports inheritance on default.
 * @author Eduard Witteveen
 * @version $Id: PostgreSQL71.java,v 1.21.2.2 2004-01-08 16:35:29 robmaris Exp $
 */
public class PostgreSQL71 implements MMJdbc2NodeInterface  {
    private static Logger log = Logging.getLoggerInstance(PostgreSQL71.class.getName());
    protected MMBase mmb;

    // conversion related..
    private HashMap disallowed2allowed;
    private HashMap allowed2disallowed;

    // how to create new fields?
    private HashMap typeMapping;
    private int maxDropSize=0;

    public void init(MMBase mmb, XMLDatabaseReader parser) {
        // the mmmbase module
        this.mmb=mmb;

        this.typeMapping=new HashMap(parser.getTypeMapping());
        maxDropSize = parser.getMaxDropSize();

        // from a specific word to the new ones...
        disallowed2allowed = new HashMap(parser.getDisallowedFields());
        // we also need the info how to convert them back:
        allowed2disallowed = new HashMap(parser.getDisallowedFields());
        Iterator iter = disallowed2allowed.keySet().iterator();
        while(iter.hasNext()) {
            Object item = iter.next();
            allowed2disallowed.put(disallowed2allowed.get(item), item);
        }
    }

    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
        try {
            // Connection connection = DriverManager.getConnectio(url, uid, pass);
            String jdbcUrl = jdbc.makeUrl();
            String jdbcUser = jdbc.getUser();
            String jdbcPassword = jdbc.getPassword();
            log.trace("trying to get a connction with request: " + jdbcUrl + " with user: '"+jdbcUser+"' and password: '"+jdbcPassword+"'");
            MultiConnection con=jdbc.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
            return(con);
        } catch(SQLException sqle) {
            log.error("error retrieving new connection, following error occured:");
            for(SQLException e = sqle;e != null; e = e.getNextException()){
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            log.error("Throwing the exception again.");
            throw sqle;
        }
    }

    /**
     * Returns whether this database support layer allows for buidler to be a parent builder
     * (that is, other builders can 'extend' this builder and its database tables).
     *
     * @since MMBase-1.6
     * @param builder the builder to test
     * @return true if the builder can be extended
     */
    public boolean isAllowedParentBuilder(MMObjectBuilder builder) {
        String buildername=builder.getTableName();
        return buildername.equals("object") || buildername.equals("insrel");
    }

    /**
     * Registers a builder as a parent builder (that is, other buidlers can 'extend' this
     * builder and its database tables).
     * At the least, this code should check whether the builder is allowed as a parent builder,
     * and throw an exception if this is not possible.
     * This method can be overridden to allow for optimization of code regarding such builders.
     *
     * @since MMBase-1.6
     * @param parent the parent builder to register
     * @param child the builder to register as the parent's child
     * @throws UnsupportedDatabaseOperationException when the databse layer does not allow extension of this builder
     */
    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child)
        throws UnsupportedDatabaseOperationException {
        if (!isAllowedParentBuilder(parent)) {
            throw new UnsupportedDatabaseOperationException("Cannot extend the builder with name "+parent.getTableName());
        }
    }

    public boolean created(String tableName) {
        MultiConnection con=null;
        DatabaseMetaData meta;
        try {
            con=mmb.getConnection();
            meta = con.getMetaData();
            boolean exists = false;

            // retrieve the table info..
            ResultSet rs = meta.getTables(null, null, tableName, null);
            try {
                if (rs.next()) {
                    // yipee we found something...
                    log.debug("the tablename found is :" + rs.getString(3) + " looking for("+tableName+")");
                    exists = true;
                }
            } finally {
                rs.close();
            }
            // meta.close();
            con.close();
            return exists;
        } catch (SQLException sqle) {
            log.error("error, could not check if table :"+tableName + " did exist");
            for(SQLException e = sqle;e != null; e = e.getNextException()){
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                // try to close them, no matter what..
                // meta.close();
                con.close();
            } catch(Exception t) {}
            // hmm, what shall we do with the exception?
            throw new RuntimeException(sqle.toString());
        }
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
        } catch (SQLException sqle) {
            log.error("error, could not create object table.."+sql);
            for(SQLException e = sqle;e != null; e = e.getNextException()){
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                // con.rollback();
                con.close();
            } catch(Exception other) {}
            return false;
        }
    }

    private boolean createSequence() {
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
            for(SQLException e = sqle;e != null; e = e.getNextException()){
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                // con.rollback();
                con.close();
            } catch(Exception other) {}
            return false;
        }
        return true;
    }

    private boolean createNumberCheck() {
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
        String sql =  "CREATE FUNCTION " + numberCheckNameName() +  " (integer) \n\tRETURNS boolean AS \n\t\t'SELECT CASE WHEN (( SELECT COUNT(*)\n\t\tFROM "+objectTableName()+"\n\t\tWHERE " + objectTableName() + "." + getNumberString()+ " = $1 ) > 0 ) THEN 1::boolean ELSE 0::boolean\n\t\tEND;'\nLANGUAGE 'sql';";
        try {
            log.debug("gonna execute the following sql statement: " + sql);
            con = mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
        } catch (SQLException sqle) {
            log.error("error, create number check.."+sql);
            for(SQLException e = sqle;e != null; e = e.getNextException()){
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                // con.rollback();
                con.close();
            } catch(Exception other) {}
            return false;
        }
        return true;
    }


    public String getDisallowedField(String allowedfield) {
        log.trace(allowedfield);
        if (allowed2disallowed.containsKey(allowedfield)) {
            allowedfield=(String)allowed2disallowed.get(allowedfield);
        }
        return allowedfield;
    }

    public String getAllowedField(String disallowedfield) {
        log.trace(disallowedfield);
        if (disallowed2allowed.containsKey(disallowedfield)) {
            disallowedfield=(String)disallowed2allowed.get(disallowedfield);
        }
        return disallowedfield;
    }

    public String getNumberString() {
        return getAllowedField("number");
    }

    public String getOTypeString() {
        return getAllowedField("otype");
    }

    public String getOwnerString() {
        return getAllowedField("owner");
    }

    private String sequenceTableName() {
        return mmb.baseName + "_autoincrement";
    }

    private String numberCheckNameName() {
        return mmb.baseName + "_check_number";
    }

    private String objectTableName() {
        return mmb.baseName + "_object";
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
                    log.trace("gonna add field " + name + " with SQL-subpart: " + part);
                    if (fieldList==null) {
                        fieldList = part;
                    } else {
                        fieldList+=", " + part;
                    }
                }
                else {
                    if (log.isDebugEnabled()) {
                        log.trace("field: '" + name + "' from builder: '" + bul.getTableName() + "' is inherited field");
                    }
                }
            }
        }
        //if all fields are inherited the field list can be empty
        if (fieldList == null){
            fieldList="";
        }

        String sql = "CREATE TABLE " + mmb.baseName+"_"+bul.getTableName() + "(" + fieldList + ")";

        // create the sql statement...
        if(getInheritTableName(bul) != null) {
            sql += " INHERITS ( " + mmb.baseName+"_"+getInheritTableName(bul)+" ) ;";
        }
        else {
            // this one doesnt inherit anything, thus must be the object table?? :p
            if(!createSequence()) return false;
            sql += ";";
        }
        log.debug("gonna create a new table with statement: " + sql);

        MultiConnection con=null;
        Statement stmt=null;
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
            if(getInheritTableName(bul) != null) return true;
            // when we created the numbertable, we also need to create the numbercheck
            log.info("we created the object table, also creating number check");
            return createNumberCheck();
        } catch (SQLException sqle) {
            log.error("error, could not create table for builder " + bul.getTableName());
            for(SQLException e = sqle;e != null; e = e.getNextException()){
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

    /** get the table that we inherit from */
    private String getInheritTableName(MMObjectBuilder bul) {
        // object table _must_ always be the top builder....
        if(bul.getTableName().equals("object")) return null;

        // builder extends something,... return it....
        if(bul.getParentBuilder() != null) {
            return bul.getParentBuilder().getTableName();
        }

        // fallback to the old code...
        log.warn("falling back to old inherit code for postgreslq, define a object.xml, and use <builder ... extends=\"object\"> in " + bul.getTableName() + ".xml");

        if(bul instanceof InsRel && !bul.getTableName().equals("insrel")) return "insrel";

        return "object";
    }

    /** check if it is a field of this builder, or that it is inherited */
    private boolean isInheritedField(MMObjectBuilder bul, String fieldname) {
        if(getInheritTableName(bul) == null) {
            // our top table, all fields must be created
            return false;
        }

        if(bul.getParentBuilder() != null) {
            // if parent builder has the field, it is inherited..
            return bul.getParentBuilder().getField(fieldname) != null;
        }

        // old fallback code...
        log.warn("falling back to old inherit code for postgreslq, define a object.xml, and use <builder ... extends=\"object\"> in " + bul.getTableName() + ".xml");

        // normally we inherited from object..
        if(fieldname.equals("number")) return true;
        if(fieldname.equals("owner")) return true;
        if(fieldname.equals("otype")) return true;

        // if we are something to do with relations...
        if(bul instanceof InsRel && !bul.getTableName().equals("insrel")) {
            if(fieldname.equals("snumber")) return true;
            if(fieldname.equals("dnumber")) return true;
            if(fieldname.equals("rnumber")) return true;
            if(fieldname.equals("dir")) return true;
        }
        return false;
    }

    private boolean isReferenceField(FieldDefs def, MMObjectBuilder bul) {
        // only integer references the number table???
        if((def.getDBType() == FieldDefs.TYPE_INTEGER) || (def.getDBType() == FieldDefs.TYPE_NODE)) {
            String fieldname = def.getDBName();
            if(fieldname.equals("otype")) return true;
            if(bul instanceof InsRel) {
                if(fieldname.equals("snumber")) return true;
                if(fieldname.equals("dnumber")) return true;
                if(fieldname.equals("rnumber")) return true;
            } else if(bul instanceof org.mmbase.module.builders.ImageCaches) {
                if(fieldname.equals("id")) return true;
            } else if(bul instanceof OAlias) {
                if(fieldname.equals("destination")) return true;
            } else if(bul instanceof RelDef) {
                if(fieldname.equals("builder")) return true;
            } else if(bul.getTableName().equals("syncnodes")) {
                if(fieldname.equals("localnumber")) return true;
            } else if(bul instanceof TypeRel) {
                if(fieldname.equals("snumber")) return true;
                if(fieldname.equals("dnumber")) return true;
                if(fieldname.equals("rnumber")) return true;
            }
            // this are the core-builders from the NOS, maybe people
            // wanna add their builders.
            // THIS IS ONLY ALLOWED FOR CORE-BUIILDERS !!
        }
        return false;
    }

    private String getDbFieldDef(FieldDefs def, MMObjectBuilder bul) {
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
            }
            else {
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

    private String getDbFieldType(FieldDefs fieldDef, int fieldSize, boolean fieldRequired) {
        if (typeMapping==null) {
            String msg = "typeMapping was null";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        dTypeInfos typs=(dTypeInfos)typeMapping.get(new Integer(fieldDef.getDBType()));
        if (typs==null) {
            String msg = "Could not find the typ mapping for the field with the value: " + fieldDef.getDBType();
            log.error(msg);
            throw new RuntimeException(msg);
        }
        Enumeration e=typs.maps.elements();
        // look if we can find our info thingie...
        String closedMatch = null;
        while(e.hasMoreElements()) {
            dTypeInfo typ = (dTypeInfo)e.nextElement();
            if(fieldSize == -1 || (typ.minSize==-1 && typ.maxSize==-1)  ) {
                closedMatch = typ.dbType;
            } else if (typ.minSize==-1) {
                if (fieldSize<=typ.maxSize) closedMatch = typ.dbType;
            } else if (typ.maxSize==-1) {
                if (typ.minSize <= fieldSize) closedMatch = typ.dbType;
            } else if (typ.minSize <= fieldSize && fieldSize <= typ.maxSize) {
                // we have the proper match !!
                // if there is a size thingie.. then make it our size...
                int pos=typ.dbType.indexOf("size");
                if (pos!=-1) {
                    return typ.dbType.substring(0,pos)+fieldSize+typ.dbType.substring(pos+4);
                }
                return typ.dbType;
            }
        }
        if(closedMatch == null) {
            String msg = "not field def found !!";
            throw new RuntimeException("not field def found !!");
        }
        int pos=closedMatch.indexOf("size");
        if (pos!=-1) {
            return closedMatch.substring(0,pos)+fieldSize+closedMatch.substring(pos+4);
        }
        return closedMatch;
    }

    public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
        String tableName = bul.getTableName();

        int number=node.getIntValue("number");
        // did the user supply a number allready,...
        if (number==-1) {
            // if not try to obtain one
            number=getDBKey();
            if(number < 1) {
                throw new RuntimeException("invalid node number retrieved: #"+number);
            }
            node.setValue("number",number);
        }

        // do the actual insert..
        number = insertRecord(bul, owner, node);

        //bul.signalNewObject(bul.tableName,number);
        if (bul.broadcastChanges) {
            if (bul instanceof InsRel) {
                bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"n");
                // figure out tables to send the changed relations
                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                n1.delRelationsCache();
                n2.delRelationsCache();
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.parent.getTableName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.parent.getTableName(),"r");
            } else {
                mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"n");
            }
        }
        node.clearChanged();
        log.debug("inserted with number #"+number+" the node :" + node);
        return number;
    }

    private int insertRecord(MMObjectBuilder bul,String owner, MMObjectNode node) {
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
            for(SQLException se = sqle;se != null; se = se.getNextException()){
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
                if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT) || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) )  {
                    if (log.isDebugEnabled()) log.trace("DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+current);
                    setValuePreparedStatement( preStmt, node, key, current);
                    log.trace("we did set the value for field " + key + " with the number " + current);
                    current++;
                } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    log.trace("DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
                } else {
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
            for(SQLException se = sqle;se != null; se = se.getNextException()){
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

    protected boolean setValuePreparedStatement(PreparedStatement stmt, MMObjectNode node, String key, int i) throws SQLException {
        switch(node.getDBType(key)) {
            case FieldDefs.TYPE_NODE:
            case FieldDefs.TYPE_INTEGER:
                stmt.setInt(i, node.getIntValue(key));
                log.trace("added integer for field with name: " + key + " with value: " + node.getIntValue(key));
                break;
            case FieldDefs.TYPE_FLOAT:
                stmt.setFloat(i, node.getFloatValue(key));
                log.trace("added float for field with name: " + key + " with value: " + node.getFloatValue(key));
                break;
            case FieldDefs.TYPE_DOUBLE:
                stmt.setDouble(i, node.getDoubleValue(key));
                log.trace("added double for field with name: " + key + " with value: " + node.getDoubleValue(key));
                break;
            case FieldDefs.TYPE_LONG:
                stmt.setLong(i, node.getLongValue(key));
                log.trace("added long for field with name: " + key + " with value: " + node.getLongValue(key));
                break;
            case FieldDefs.TYPE_XML:
            case FieldDefs.TYPE_STRING:
                stmt.setString(i, node.getStringValue(key));
                log.trace("added string for field with name: " + key + " with value: " + node.getStringValue(key));
                break;
            case FieldDefs.TYPE_BYTE:
                // arrg...
                try {
                    byte[] bytes = node.getByteValue(key);
                    java.io.InputStream stream=new java.io.ByteArrayInputStream(bytes);
                    if (log.isDebugEnabled()) log.trace("in setDBByte ... before stmt");
                    stmt.setBinaryStream(i, stream, bytes.length);
                    if (log.isDebugEnabled()) log.trace("in setDBByte ... after stmt");
                    stream.close();
                    log.trace("added bytes for field with name: " + key + " with with a length of #"+bytes.length+"bytes");
                } catch (Exception e) {
                    log.error("Can't set byte stream");
                    log.error(Logging.stackTrace(e));
                }
                // was setDBByte(i, stmtstmt, node.getByteValue(key))
                break;
            default:
                log.warn("unknown type for field with name : " + key);
                log.trace("added string for field with name: " + key + " with value: " + node.getStringValue(key));
                break;
        }
        return true;
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i) {
        return decodeDBnodeField(node,fieldname,rs,i,"");
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
        fieldname = getDisallowedField(fieldname);
        int type=node.getDBType(prefix+fieldname);

        try {
            switch (type) {
                case FieldDefs.TYPE_XML:
                case FieldDefs.TYPE_STRING:
                    String tmp=rs.getString(i);
                    if (tmp==null) {
                        node.setValue(prefix+fieldname,"");
                    } else {
                        node.setValue(prefix+fieldname,tmp);
                    }
                    break;
                case FieldDefs.TYPE_NODE:
                case FieldDefs.TYPE_INTEGER:
                    // node.setValue(prefix+fieldname,(Integer)rs.getObject(i));
                    node.setValue(prefix+fieldname, rs.getInt(i));
                    break;
                case FieldDefs.TYPE_LONG:
                    // node.setValue(prefix+fieldname,(Long)rs.getObject(i));
                    node.setValue(prefix+fieldname,rs.getLong(i));
                    break;
                case FieldDefs.TYPE_FLOAT:
                    // who does this now work ????
                    //node.setValue(prefix+fieldname,((Float)rs.getObject(i)));
                    node.setValue(prefix+fieldname, rs.getFloat(i));
                    break;
                case FieldDefs.TYPE_DOUBLE:
                    // node.setValue(prefix+fieldname,(Double)rs.getObject(i));
                    node.setValue(prefix+fieldname, rs.getDouble(i));
                    break;
                case FieldDefs.TYPE_BYTE:
                    node.setValue(prefix+fieldname,"$SHORTED");
                    break;
            }
        } catch(SQLException sqle) {
            log.error("could not retieve the field("+fieldname+") value of the node from the database("+node+")");
            log.error(Logging.stackTrace(sqle));
            for(SQLException se = sqle;se != null; se = se.getNextException()){
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
        }
        return node;
    }

    /**
     * commit this node to the database
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
            if (builderFieldSql == null) {
                builderFieldSql = key + "=?";
            } else {
                builderFieldSql += ", " + key+ "=?";
            }
            // not allowed as far as im concerned...
            if(key.equals("number")) {
                log.fatal("trying to change the 'number' field");
                throw new RuntimeException("trying to change the 'number' field");
            } else if(key.equals("otype")) {
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
                for(SQLException e = sqle;e != null; e = e.getNextException()){
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
                } catch(Exception other) {}
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
            } else {
                mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
            }
        }

        // done !
        return true;
    }

    public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
        int number=node.getIntValue("number");
        if(log.isDebugEnabled()) {
            log.debug("delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number+"("+node+")");
        }

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
        MultiConnection con = null;
        Statement stmt = null;
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            stmt.executeUpdate("delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number);
            stmt.close();
            con.close();
        } catch (SQLException sqle) {
            log.error("delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number +"("+node+") failed");
            for(SQLException e = sqle;e != null; e = e.getNextException()){
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            log.error(Logging.stackTrace(sqle));
            try {
                if(stmt != null) stmt.close();
                // con.rollback();
                con.close();
            } catch(Exception other) {}
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

    public String getMMNodeSearch2SQL(String where, MMObjectBuilder bul) {
        log.warn("still have to review!!");
        String result="";
        where=where.substring(7);
        StringTokenizer parser = new StringTokenizer(where, "+-\n\r",true);
        while (parser.hasMoreTokens()) {
            String part=parser.nextToken();
            String cmd=null;
            if (parser.hasMoreTokens()) {
                cmd=parser.nextToken();
            }
            // do we have a type prefix (example episodes.title==) ?
            int pos=part.indexOf('.');
            if (pos!=-1) {
                part=part.substring(pos+1);
            }
            // remove fieldname  (example title==) ?
            pos=part.indexOf('=');
            if (pos!=-1) {
                String fieldname=part.substring(0,pos);
                int dbtype=bul.getDBType(fieldname);
                result+=parseFieldPart(fieldname,dbtype,part.substring(pos+1));
                if (cmd!=null) {
                    if (cmd.equals("+")) {
                        result+=" AND ";
                    } else {
                        result+=" AND NOT ";
                    }
                }
            }
        }
        log.debug("the node search for where: "+ where +" on builder: "+bul.getTableName()+" was : " + result);
        return result;
    }

    private String parseFieldPart(String fieldname,int dbtype,String part) {
        log.warn("still have to review!!");
        String result="";
        boolean like=false;
        char operatorChar = part.charAt(0);
        // added mapping daniel, 24 Nov 2000
        fieldname=getAllowedField(fieldname);
        String value=part.substring(1);
        int pos=value.indexOf("*");
        if (pos!=-1) {
            value=value.substring(pos+1,value.length()-1);
            like=true;
        }
        if (dbtype==FieldDefs.TYPE_STRING || dbtype==FieldDefs.TYPE_XML) {
            switch (operatorChar) {
                case '=':
                case 'E':
                    // EQUAL
                    if (like) {
                        result+="lower("+fieldname+") LIKE '%"+value+"%'";
                    } else {
                        result+="lower("+fieldname+") LIKE '%"+value+"%'";
                    }
                    break;
            }
        } else if (dbtype==FieldDefs.TYPE_LONG || dbtype==FieldDefs.TYPE_NODE || dbtype==FieldDefs.TYPE_INTEGER) {
            switch (operatorChar) {
                case '=':
                case 'E':
                    // EQUAL
                    result+=fieldname+"="+value;
                    break;
                case 'N':
                    // NOTEQUAL;
                    result+=fieldname+"<>"+value;
                    break;
                case 'G':
                    // GREATER;
                    result+=fieldname+">"+value;
                    break;
                case 'g':
                    // GREATEREQUAL;
                    result+=fieldname+">="+value;
                    break;
                case 'S':
                    // SMALLER;
                    result+=fieldname+"<"+value;
                    break;
                case 's':
                    // SMALLEREQUAL;
                    result+=fieldname+"<="+value;
                    break;
            }
        }
        return result;
    }

    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        MultiConnection con = null;
        Statement stmt = null;
        try {
            log.debug("retrieving the field :" + fieldname +" of object("+number+") of type : " + tableName);
            con = mmb.getConnection();

            // support for larger objects...
            con.setAutoCommit(false);

            stmt = con.createStatement();

            String sql = "SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" WHERE "+getNumberString()+" = "+number;
            log.debug("gonna excute the followin query: " + sql);
            java.io.DataInputStream is = null;
            byte[] data=null;
            ResultSet rs=stmt.executeQuery(sql);

            try {
                if(rs.next()) {
                    log.debug("found a record, now trying to retieve the stream..with loid #" + rs.getInt(fieldname));
                    Blob b = rs.getBlob(1);
                    data = b.getBytes(0, (int)b.length());
                    log.debug("data was read from the database(#"+data.length+" bytes)");
                }
            } finally {
                rs.close();
            }
            stmt.close();
            // a get doesnt make changes !!
            con.commit();
            con.setAutoCommit(true);
            con.close();

            if (data != null) {
                log.debug("retrieved "+data.length+" bytes of data");
            } else {
                log.error("retrieved NO data for node #" + number + " it's field: " + fieldname + "\n" + sql);
            }
            return data;
        } catch (SQLException sqle) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName);
            log.error(sqle);
            for(SQLException se = sqle;se != null; se = se.getNextException()){
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
            log.error(Logging.stackTrace(sqle));
            try {
                if(stmt!=null) stmt.close();
                con.rollback();
                con.setAutoCommit(true);
                con.close();
            } catch(Exception other) {}
            return null;
        } catch (Exception e) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName +" (possible IOError)");
            log.error(e);
            log.error(Logging.stackTrace(e));
            try {
                if(stmt!=null) stmt.close();
                con.rollback();
                con.setAutoCommit(true);
                con.close();
            } catch(Exception other) {}
            return null;
        }
    }

    public boolean drop(MMObjectBuilder bul) {
        log.info("drop table for builder with name: " + bul.getTableName());
        return changeMetaData(bul, "DROP TABLE " +mmb.baseName+"_"+bul.getTableName());
    }

    public boolean updateTable(MMObjectBuilder bul) {
        log.info("update table for builder with name: " + bul.getTableName());
        // dont know what this function SHOULD do...
        log.debug("updateTable");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("updateTable");
    }

    public boolean addField(MMObjectBuilder bul,String fieldname) {
        log.info("add field for builder with name: " + bul.getTableName() + " with field with name: " + fieldname);
        
        FieldDefs def = bul.getField(fieldname);
        if(def == null) {
            log.error("could not find field definition for field: "+fieldname+" for builder :" + bul.getTableName());
            return false;
        }
        if (def.getDBState() == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
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

    private boolean changeMetaData(MMObjectBuilder bul, String sql) {
        log.info("change meta data for builder with name: " + bul.getTableName() + " with sql: " + sql);
        
        // are we allowed to change the metadata?
        int size=bul.size();        
        // return when we are allowed...
        if (size > maxDropSize) {
            log.error("change of metadata not allowed on : "+bul.getTableName());
            log.error("check <maxdropsize> in your database.xml(in xml:"+maxDropSize+" and records#"+size+")");
            return false;
        }

        // do the update/drop whatever...
        MultiConnection con = null;
        Statement stmt = null;
        try {
            con = mmb.getConnection();
            stmt = con.createStatement();
            log.debug("gonna excute the followin query: " + sql);
            stmt.executeUpdate(sql);
            stmt.close();
            con.close();
            return true;
        } catch (SQLException sqle) {
            log.error("could not execute the query:"+sql+" on builder: " + bul.getTableName());
            log.error(sqle);
            for(SQLException se = sqle;se != null; se = se.getNextException()){
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
            log.error(Logging.stackTrace(sqle));
            try {
                if(stmt!=null) stmt.close();
                con.close();
            } catch(Exception other) {}
            return false;
        }
    }

    /** is next function nessecary? */
    public byte[] getDBByte(ResultSet rs,int idx) {
        log.debug("getDBByte");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("getDBText");
    }

    /** is next function nessecary? */
    public void setDBByte(int i, PreparedStatement stmt, byte[] bytes) {
        log.debug("setDBByte");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("setDBByte");
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
            try {
                if (rs.next()) {
                    number=rs.getInt("NEXTVAL");
                } else {
                    log.warn("could not retieve the number for new node");
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
        } catch (SQLException sqle) {
            log.error("error, could not retrieve new object number:"+sql);
            for(SQLException se = sqle;se != null; se = se.getNextException()){
                log.error("\tSQLState : " + se.getSQLState());
                log.error("\tErrorCode : " + se.getErrorCode());
                log.error("\tMessage : " + se.getMessage());
            }
            try {
                if(stmt!=null) stmt.close();
                con.close();
            } catch(Exception other) {}
            throw new RuntimeException(sqle.toString());
        }
        log.debug("new object id #"+number);
        return number;
    }

    /** is next function nessecary? */
    public String getShortedText(String tableName, String fieldname, int number) {
        log.debug("getShortedText");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("getShortedText");
    }

    /** is next function nessecary? */
    public String getDBText(ResultSet rs,int idx) {
        log.debug("getDBText");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("getDBText");
    }
}
