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

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
/**
 * Postgresql driver for MMBase, only works with Postgresql 7.1 + that supports inheritance on default.
 *@author Eduard Witteveen
 */
public class PostgreSQL71 implements MMJdbc2NodeInterface  {
    private static Logger log = Logging.getLoggerInstance(PostgreSQL71.class.getName());
    private MMBase mmb;
    
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
	}
	catch(SQLException sqle) {
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
    
    public boolean created(String tableName) {
    	MultiConnection con=null;
	DatabaseMetaData meta;
	try {
    	    con=mmb.getConnection();
	    meta = con.getMetaData();

	    // retrieve the table info..
	    ResultSet rs = meta.getTables(null, null, tableName, null);
	    boolean exists = false;
    	    if (rs.next()) {	    	
	    	// yipee we found something...
		log.debug("the tablename found is :" + rs.getString(3) + " looking for("+tableName+")");
		exists = true;
	    }
    	    // meta.close();	    
    	    con.close();
	    return exists;
	} 
	catch (SQLException sqle) {
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
	    } 
	    catch(Exception t) {}
	    // hmm, what shall we do with the exception?
	    throw new RuntimeException(sqle.toString());
    	}
    }
    
    public boolean createObjectTable(String notUsed) {
    	// first create the auto update thingie...
	//  CREATE SEQUENCE autoincrement INCREMENT 1
    	MultiConnection con = null;
	Statement stmt = null;
        String sql =  "CREATE SEQUENCE "+sequenceTableName()+" INCREMENT 1 START 1";        
    	try {
	    log.info("gonna execute the following sql statement: " + sql);        
	    con = mmb.getConnection();
    	    stmt=con.createStatement();
    	    stmt.executeUpdate(sql);
    	    stmt.close();
    	    con.close();
	} 
	catch (SQLException sqle) {
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
            } 
	    catch(Exception other) {}
	    return false;
	}	

	// now update create the object table, with the auto update thignie    
        sql = "CREATE TABLE "+objectTableName()+" (";
	// primary key will mean that and unique and not null...
	// TODO : create this one also in a generic way !
	sql += getNumberString()+" INTEGER PRIMARY KEY, \t-- the unique identifier for objects\n";
	sql += getOTypeString()+" INTEGER NOT NULL REFERENCES " + objectTableName() + " ON DELETE CASCADE, \t-- describes the type of object this is\n";
	sql += getOwnerString()+" TEXT NOT NULL  \t-- field for security information\n";
	sql += ")";
    	try {
	    log.info("gonna execute the following sql statement: " + sql);        
	    con = mmb.getConnection();
    	    stmt=con.createStatement();
    	    stmt.executeUpdate(sql);
    	    stmt.close();
    	    con.close();
	    return true;
	} 
	catch (SQLException sqle) {
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
            } 
	    catch(Exception other) {}	    
	    return false;
	}
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
    
    private String objectTableName() {
        return mmb.baseName + "_object";    
    }    
    
    public boolean create(MMObjectBuilder bul) {
	log.debug("create");

    	String fieldList=null;
    	Vector sfields=bul.sortedDBLayout;
	if(sfields == null) {
	    log.error("sfield was null for builder with name :" + bul);
	    return false;
	}

    	// process all the fields..
	for (Enumeration e=sfields.elements();e.hasMoreElements();) {
	    String name=(String)e.nextElement();
            FieldDefs def = bul.getField(name);
    	    if (def.getDBState() != org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
	    	if(!isInheritedField(bul, name)) {
		    log.trace("trying to retrieve the part for field : " + name);
    	    	    String part = getDbFieldDef(def, bul);
    	    	    log.trace("gonna add field " + name + " with SQL-subpart: " + part);
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
        if (fieldList == null){
            fieldList="";
        }
	// create the sql statement...
	String sql = "CREATE TABLE " + mmb.baseName+"_"+bul.getTableName() + "(" + fieldList + ") INHERITS ( " + mmb.baseName+"_"+getInheritTableName(bul)+" ) ;";	
	log.info("gonna create a new table with statement: " + sql);

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
            } 
	    catch(Exception other) {}	    
    	}
    	return false;
    }
    
    /** get the table that we inherit from */
    private String getInheritTableName(MMObjectBuilder bul) {
    	if(bul instanceof InsRel && !bul.getTableName().equals("insrel")) return "insrel";
    	return "object";
    }
    
    /** check if it is a field of this builder, or that it is inherited */
    private boolean isInheritedField(MMObjectBuilder bul, String fieldname) {
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
        if(def.getDBType() == FieldDefs.TYPE_INTEGER) {
            String fieldname = def.getDBName();
            if(fieldname.equals("otype")) return true;
            if(bul instanceof InsRel) {
                if(fieldname.equals("snumber")) return true;
                if(fieldname.equals("dnumber")) return true;
                if(fieldname.equals("rnumber")) return true;                
            }
            else if(bul instanceof org.mmbase.module.builders.ImageCaches) {
                if(fieldname.equals("id")) return true;
            }
            else if(bul instanceof OAlias) {
                if(fieldname.equals("destination")) return true;
            }
            else if(bul instanceof RelDef) {
                if(fieldname.equals("builder")) return true;
            }
            else if(bul.getTableName().equals("syncnodes")) {
                if(fieldname.equals("localnumber")) return true;
            }
            else if(bul instanceof TypeRel) {
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
	boolean fieldRequired = def.getDBNotNull();
	boolean fieldUnique = def.isKey();
        boolean fieldIsReferer = isReferenceField(def, bul);
	String  fieldType = getDbFieldType(def, def.getDBSize(), fieldRequired);
    	String result = fieldName + " " + fieldType;
	if(fieldRequired) {
	    //TODO : parser.getNotNullScheme();
	    result += " NOT NULL ";
	}
	if(fieldUnique) {
	    //TODO : parser.getKeyScheme()+ "("+name+") so make a 
	    result += " UNIQUE ";
	}
        if(fieldIsReferer) {
            /**
               TODO: research if this code is better...
               http://www.postgresql.org/idocs/index.php?inherit.html 
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
            // next line doesnt work due to ?bug? in postgresql with inhertitance
            // result += " REFERENCES " + mmb.baseName + "_object" + " ON DELETE CASCADE ";
        }
        // add in comment the gui stuff... nicer when reviewing database..
        result += "\t-- " + def.getGUIName("us")+"(name: '"+def.getGUIName()+"' gui-type: '"+def.GUIType+"')\n";        
	return result;
    }
    
    private String getDbFieldType(FieldDefs fieldDef, int fieldSize, boolean fieldRequired) {
    	if (typeMapping==null) {
	    String msg = "typeMapping was null";
	    log.error(msg);
	    throw new RuntimeException(msg);
	}
    	dTypeInfos  typs=(dTypeInfos)typeMapping.get(new Integer(fieldDef.getDBType()));
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
	    }
	    else if (typ.minSize==-1) {	    	
    	    	if (fieldSize<=typ.maxSize) closedMatch = typ.dbType;
	    } 
	    else if (typ.maxSize==-1) {
    	    	if (typ.minSize <= fieldSize) closedMatch = typ.dbType;	    	
	    }
	    else if (typ.minSize <= fieldSize && fieldSize <= typ.maxSize) {
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
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
            } 
	    else {
            	mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"n");
            }
        }
	node.clearChanged();
        log.info("inserted with number #"+number+" the node :" + node);
        return number;
    }    
    
    private int insertRecord(MMObjectBuilder bul,String owner, MMObjectNode node) {
	String tableName = bul.getTableName();
        String sql = insertPreSQL(tableName, bul.sortedDBLayout.elements(), node);     
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
        } 
	catch (SQLException sqle) {
	    log.error("error, could not create table for builder " + tableName);
	    for(SQLException se = sqle;se != null; se = se.getNextException()){
    		log.error("\tSQLState : " + se.getSQLState());
    		log.error("\tErrorCode : " + se.getErrorCode());
    		log.error("\tMessage : " + se.getMessage());			
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
        
	// Now fill the fields
        try {
            preStmt.setEscapeProcessing(false);

    	    // First add the 'number' field to the statement since it's not in the sortedDBLayout vector.
    	    int j=1;
    	    for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
                String key = (String)e.nextElement();
                int DBState = node.getDBState(key);
                if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT) || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) )  {
                    if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+j);
                    setValuePreparedStatement( preStmt, node, key, j );
                    log.debug("we did set the value for field " + key + " with the number " + j );			
                    j++;
                } 
		else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    log.trace("insert(): DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
              	} 
		else {
                    if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                    	setValuePreparedStatement( preStmt, node, key, j );
		    	log.debug("we did set the value for field " + key + " with the number " + j );
                        j++;
    	    	    } 
		    else {
    	    	    	log.warn("insert(): DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
    	    	    }
    	    	}
            }
	    preStmt.executeUpdate();
	    preStmt.close();
	    con.commit();
    	    con.setAutoCommit(true);
            con.close();
    	} 
	catch (SQLException sqle) {
	    log.error("error, could not insert record for builder " + bul.getTableName());
    	    log.error(Logging.stackTrace(sqle));	    
	    for(SQLException se = sqle;se != null; se = se.getNextException()){
    		log.error("\tSQLState : " + se.getSQLState());
    		log.error("\tErrorCode : " + se.getErrorCode());
    		log.error("\tMessage : " + se.getMessage());			
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
            String key = (String)fieldLayout.nextElement();
	    String fieldName = getAllowedField(key);
            int DBState = node.getDBState(key);
            if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT) || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
            	log.debug("Insert: DBState = "+DBState+", adding key: "+key);

		// add the values to our lists....
		if(fieldNames == null) fieldNames = fieldName;
		else fieldNames += ", " + fieldName;
                if(fieldValues == null) fieldValues = "?";
                else fieldValues += ", ?";				
            } 
	    else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
            	log.trace("Insert: DBState = "+DBState+", skipping key: "+key);
            } 
	    else {
    	    	if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
    		    // add the values to our lists....
		    if(fieldNames == null) fieldNames = fieldName;
		    else fieldNames += ", " + fieldName;
		    if(fieldValues == null) fieldValues = "?";
		    else fieldValues += ", ?";				
                } 
		else {
                    log.error("Insert: DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
                }
            }
     	}    
	String sql = "INSERT INTO "+mmb.baseName+"_"+tableName+" ("+ getNumberString() +", "+ fieldNames+") VALUES ("+node.getIntValue("number")+", "+fieldValues+")";
	log.trace("created pre sql: " + sql);
        return sql;	
    }    
    
    protected boolean setValuePreparedStatement(PreparedStatement stmt, MMObjectNode node, String key, int i) throws SQLException {
    	switch(node.getDBType(key)) {
        case FieldDefs.TYPE_INTEGER:
            stmt.setInt(i, node.getIntValue(key));
            log.debug("added integer for field with name: " + key + " with value: " + node.getIntValue(key));		    		    	
            break;
        case FieldDefs.TYPE_FLOAT:
            stmt.setFloat(i, node.getFloatValue(key));
            log.debug("added float for field with name: " + key + " with value: " + node.getFloatValue(key));	    			
            break;
        case FieldDefs.TYPE_DOUBLE:
            stmt.setDouble(i, node.getDoubleValue(key));
            log.debug("added double for field with name: " + key + " with value: " + node.getDoubleValue(key));
            break;
        case FieldDefs.TYPE_LONG:
            stmt.setLong(i, node.getLongValue(key));
            log.debug("added long for field with name: " + key + " with value: " + node.getLongValue(key));		    
            break;
        case FieldDefs.TYPE_XML:
        case FieldDefs.TYPE_STRING:
            stmt.setString(i, node.getStringValue(key));
            log.debug("added string for field with name: " + key + " with value: " + node.getStringValue(key));		    
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
                log.debug("added bytes for field with name: " + key + " with with a length of #"+bytes.length+"bytes");
            } catch (Exception e) {
                log.error("Can't set byte stream");
                log.error(Logging.stackTrace(e));
            }
            // was setDBByte(i, stmtstmt, node.getByteValue(key))
            break;		
        default:
            log.warn("unknown type for field with name : " + key);
            log.debug("added string for field with name: " + key + " with value: " + node.getStringValue(key));		    
            break;
	}
    	return true;
    }
    
    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i) {
    	return(decodeDBnodeField(node,fieldname,rs,i,""));
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
                } 
                else {
                    node.setValue(prefix+fieldname,tmp);
                }
                break;
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
	} 
	catch(SQLException sqle) {
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
    	    if (builderFieldSql == null) builderFieldSql = key + "=?";
    	    else builderFieldSql += ", " + key+ "=?";

    	    // not allowed as far as im concerned... 
    	    if(key.equals("number")) {
    	    	log.fatal("trying to change the 'number' field");
    		throw new RuntimeException("trying to change the 'number' field");
	    }    	    
	    // hmm i dont like the idea of changing the otype..
    	    else if(key.equals("otype")) {
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
		
	    }
	    catch (SQLException sqle) {
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
		    
		}
		catch(Exception other) {}
		return false;
	    }
	} 
	else {
	    log.warn("tried to update a node without any changes,..");
	    return false;
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
		mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
		mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
	    } 
	    else {
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
	} 
	catch (SQLException sqle) {
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
	    }
    	    catch(Exception other) {}
	    return;
	}
    	if (bul.broadcastChanges) {
    	    mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"d");
    	    if (bul instanceof InsRel) {
    	    	MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
		MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
		mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
		mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
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
		    } 
		    else {
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
                } 
                else {
                    result+="lower("+fieldname+") LIKE '%"+value+"%'";
                }
                break;
	    }
	} 
	else if (dbtype==FieldDefs.TYPE_LONG || dbtype==FieldDefs.TYPE_INTEGER) {
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
	    log.info("gonna excute the followin query: " + sql);
    	    ResultSet rs=stmt.executeQuery(sql);
	    java.io.DataInputStream is = null;
	    
	    byte[] data=null;
	    if(rs.next()) {
		log.debug("found a record, now trying to retieve the stream..with loid #" + rs.getInt(fieldname));
		Blob b = rs.getBlob(1);
		data = b.getBytes(0, (int)b.length());
    	    	log.debug("data was read from the database(#"+data.length+" bytes)");
	    }
	    rs.close();
    	    stmt.close();	    
	    // a get doesnt make changes !!
	    con.commit();
	    con.setAutoCommit(true);	    
    	    con.close();	    
	    
	    if(data != null)log.debug("retrieved "+data.length+" bytes of data");
	    else log.error("retrieved NO data");
	    return data;
    	} 
	catch (SQLException sqle) {
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
            } 
	    catch(Exception other) {}
	    return null;
	}
	catch (Exception e) {
    	    log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName +" (possible IOError)");
	    log.error(e);	    
    	    log.error(Logging.stackTrace(e));
    	    try {
            	if(stmt!=null) stmt.close();
	    	con.rollback();
	    	con.setAutoCommit(true);		
                con.close();
            } 
	    catch(Exception other) {}	    
	    return null;	    
	}	    
    }
	    
    public boolean drop(MMObjectBuilder bul) {
    	log.debug("drop");
    	return changeMetaData(bul, "DROP TABLE " +mmb.baseName+"_"+bul.getTableName());
    }
	
    public boolean updateTable(MMObjectBuilder bul) {
    	// dont know what this function SHOULD do...
    	log.debug("updateTable");
    	log.fatal("This function is not implemented !!");
    	throw new UnsupportedOperationException("updateTable");
    }

    public boolean addField(MMObjectBuilder bul,String fieldname) {
    	log.debug("addField");

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
    	return changeMetaData(bul, "ALTER TABEL " +mmb.baseName+"_"+bul.getTableName() + " DROP COLUMN "+fieldname);
    }

    public boolean changeField(MMObjectBuilder bul,String fieldname) {
    	if(removeField(bul, fieldname)) return addField(bul, fieldname);
	return false;
    }    
    
    private boolean changeMetaData(MMObjectBuilder bul, String sql) {
    	// are we allowed to change the metadata?
    	int size=bul.size();
	// return when we are allowed...
	if (size > maxDropSize) {    
	    log.error("chang of metadata not allowed on : "+bul.getTableName());
    	    log.debug("check <maxdropsize> in your database.xml(in xml:"+maxDropSize+" and records#"+size+")");
	    return false;
	}

    	// do the update/drop whatever...
	MultiConnection con = null;
	Statement stmt = null;
    	try {
	    con = mmb.getConnection();
    	    stmt = con.createStatement();
	    log.info("gonna excute the followin query: " + sql);
    	    stmt.close();	    
    	    con.close();	    
	    return true;
    	} 
	catch (SQLException sqle) {
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
            } 
	    catch(Exception other) {}
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
     * Retrieves a new unique number, which can be used to inside _object table
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
            }
	    else {
                log.warn("could not retieve the number for new node");
            }
    	    stmt.close();
	    con.close();            
        } 
	catch (SQLException sqle) {
	    log.error("error, could not retrieve new object number:"+sql);
	    for(SQLException se = sqle;se != null; se = se.getNextException()){
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
