/*
        This software is OSI Certified Open Source Software.
        OSI Certified is a certification mark of the Open Source Initiative.

        The license (Mozilla version 1.0) can be read at the MMBase site.
        See http://www.MMBase.org/license
 */
package org.mmbase.module.database.support;

import java.util.*;
import java.sql.*;

import org.mmbase.storage.database.UnsupportedDatabaseOperationException;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Generic node handler for database's with the information stored on one location.
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Eduard Witteveen
 * @version $Id: Sql92SingleFields.java,v 1.4 2004-03-11 23:25:03 eduard Exp $
 */
public abstract class Sql92SingleFields extends BaseJdbc2Node implements MMJdbc2NodeInterface {
    private static Logger log = Logging.getLoggerInstance(Sql92SingleFields.class.getName());
    protected MMBase mmb;

    // conversion related..
    protected HashMap disallowed2allowed;
    protected HashMap allowed2disallowed;

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

        // Instantiate and initialize sql handler.
        super.init(disallowed2allowed, parser);
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
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
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
     * @return alway's true
     */
    public boolean isAllowedParentBuilder(MMObjectBuilder builder) {
        return true;
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
    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child) throws UnsupportedDatabaseOperationException {
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

            // retrieve the table info..
            ResultSet rs = meta.getTables(null, null, tableName, null);
            boolean exists = false;
            if (rs.next()) {
                // yipee we found something...
                log.debug("the tablename found is :" + rs.getString(3) + " looking for("+tableName+")");
                exists = true;
            }
            else {
                log.info("the tablename '" + tableName +"' was not found");
            }
            // meta.close();
            con.close();
            return exists;
        }
        catch (SQLException sqle) {
            log.error("error, could not check if table :"+tableName + " did exist");
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
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

    /** is next function nessecary? */
    public boolean createObjectTable(String notUsed) {
        log.debug("createObjectTable");
        log.fatal("This function is not implemented !!");
        throw new UnsupportedOperationException("createObjectTable");
    }

    abstract protected boolean createSequence();

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

    protected String sequenceTableName() {
        return mmb.baseName + "_autoincrement";
    }

    protected String numberCheckNameName() {
        return mmb.baseName + "_check_number";
    }

    protected String objectTableName() {
        return mmb.baseName + "_object";
    }

    public abstract boolean create(MMObjectBuilder bul);

    /** get the table that we inherit from */
    protected MMObjectBuilder getInheritBuilder(MMObjectBuilder bul) {
        // object table _must_ always be the top builder....
        if(bul.getTableName().equals("object")) return null;

        // builder extends something,... return it....
        if(bul.getParentBuilder() != null) {
            return bul.getParentBuilder();
        }

        // fallback to the old code...
        log.warn("falling back to old inherit code: define a object.xml, and use <builder ... extends=\"object\"> in " + bul.getTableName() + ".xml");

        if(bul instanceof InsRel && !bul.getTableName().equals("insrel")) return mmb.getBuilder("insrel");

        return mmb.getBuilder("object");
    }

    /** get the table that we inherit from */
    protected String getInheritTableName(MMObjectBuilder bul) {
        MMObjectBuilder builder = getInheritBuilder(bul);
        if(builder == null) {
            return null;
        }
        return builder.getTableName();
    }

    protected boolean isBuilderField(MMObjectBuilder bul, String fieldname) {
        // look if it really an field of us,..
        return bul.getField(fieldname) != null;
    }
    /** check if it is a field of this builder, or that it is inherited */
    protected boolean isInheritedField(MMObjectBuilder bul, String fieldname) {
        if(getInheritTableName(bul) == null) {
            // our top table, all fields must be created
            return false;
        }

        if(bul.getParentBuilder() != null) {
            // if parent builder has the field, it is inherited..
            return bul.getParentBuilder().getField(fieldname) != null;
        }

        // old fallback code...
        log.warn("falling back to old inherit code: define a object.xml, and use <builder ... extends=\"object\"> in " + bul.getTableName() + ".xml");

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

    protected boolean isReferenceField(FieldDefs def, MMObjectBuilder bul) {
        // only integer references the number table???
        if((def.getDBType() == FieldDefs.TYPE_INTEGER) || (def.getDBType() == FieldDefs.TYPE_NODE)) {
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

    abstract protected String getDbFieldDef(FieldDefs def, MMObjectBuilder bul);

    protected String getDbFieldType(FieldDefs fieldDef, int fieldSize, boolean fieldRequired) {
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

        int number = node.getIntValue("number");
        // did the user supply a number allready,...
        if (number == -1) {
            // if not try to obtain one
            number = getDBKey();
            if(number < 1) {
                throw new RuntimeException("invalid node number retrieved: #"+number);
            }
            node.setValue("number", number);
        }

        // do the actual insert..
        number = insertRecord(bul, owner, node);

        if(number < 1) {
            throw new RuntimeException("invalid node number stored: #" + number);
        }

        //bul.signalNewObject(bul.tableName,number);
        if (bul.broadcastChanges) {
            mmb.mmc.changedNode(number, bul.tableName, "n");

            if (bul instanceof InsRel) {

                // figure out tables to send the changed relations
                MMObjectNode n1 = bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2 = bul.getNode(node.getIntValue("dnumber"));
                n1.delRelationsCache();
                n2.delRelationsCache();
                mmb.mmc.changedNode(n1.getIntValue("number"), n1.parent.getTableName(), "r");
                mmb.mmc.changedNode(n2.getIntValue("number"), n2.parent.getTableName(), "r");
            }
        }
        node.clearChanged();
        log.debug("inserted with number #"+number+" the node :" + node);
        return number;
    }

    protected abstract int insertRecord(MMObjectBuilder bul,String owner, MMObjectNode node);

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i) {
        return decodeDBnodeField(node,fieldname,rs,i,"");
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
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
        }
        catch(SQLException sqle) {
            log.error("could not retieve the field("+fieldname+") value of the node from the database("+node+")");
            log.error(Logging.stackTrace(sqle));
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
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
    public abstract boolean commit(MMObjectBuilder bul, MMObjectNode node);

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
            for(SQLException e = sqle;e != null; e = e.getNextException()) {
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
        else if (dbtype==FieldDefs.TYPE_LONG || dbtype==FieldDefs.TYPE_NODE || dbtype==FieldDefs.TYPE_INTEGER) {
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

    public abstract boolean addField(MMObjectBuilder bul,String fieldname);
    public abstract boolean removeField(MMObjectBuilder bul,String fieldname);
    public abstract boolean changeField(MMObjectBuilder bul,String fieldname);

    protected boolean changeMetaData(MMObjectBuilder bul, String sql) {
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
        }
        catch (SQLException sqle) {
            log.error("could not execute the query:"+sql+" on builder: " + bul.getTableName());
            log.error(sqle);
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
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
     * Retrieves a new unique number, which can be used to inside objectTableName() table
     * @return a new unique number for new nodes or -1 on failure
     */
    public abstract int getDBKey();

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
                stmt.setBytes(i, node.getByteValue(key));
                log.trace("added bytes for field with name: " + key + " with with a length of #"+ node.getByteValue(key).length+"bytes");
                break;
            default:
                log.warn("unknown type for field with name : " + key);
                log.trace("added string for field with name: " + key + " with value: " + node.getStringValue(key));
                break;
        }
        return true;
    }

    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        MultiConnection con = null;
        Statement stmt = null;

        try {
            log.debug("retrieving the field :" + fieldname +" of object("+number+") of type : " + tableName);
            con = mmb.getConnection();

            // TODO: use prepare statement, can be cached..
            stmt = con.createStatement();

            String sql = "SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" WHERE "+getNumberString()+" = "+number;
            log.debug("gonna excute the followin query: " + sql);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs == null) throw new RuntimeException("Error retrieving the result set for query:"+sql);

            byte[] data=null;
            if(rs.next()) {
                data = rs.getBytes(1);
                log.debug("data was read from the database(#"+data.length+" bytes)");
            }
            rs.close();
            stmt.close();

            con.close();

            if (data != null) {
                log.debug("retrieved "+data.length+" bytes of data");
            }
            else {
                log.error("retrieved NO data for node #" + number + " it's field: " + fieldname + "\n" + sql);
            }
            return data;
        }
        catch (SQLException sqle) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName);
            log.error(Logging.stackTrace(sqle));
            for(SQLException se = sqle;se != null; se = se.getNextException()) {
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
            return null;
        }
        catch (Exception e) {
            log.error("could not retrieve the field :" + fieldname +" of object("+number+") of type : " + tableName +" (possible IOError)");
            log.error(Logging.stackTrace(e));
            try {
                if(stmt!=null) stmt.close();
                con.close();
            }
            catch(Exception other) {}
            return null;
        }
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
