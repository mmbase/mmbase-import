/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database.support;

import com.informix.jdbc.IfxCblob;
import com.informix.jdbc.IfxLobDescriptor;
import com.informix.jdbc.IfxLocator;
import com.informix.jdbc.IfxSmartBlob;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.MultiConnection;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;

/**
 * MMInformix42Node extends MMSQL92Node and implements the MMJdbc2NodeInterface.
 * <p>This class overrides the methods which needed substitution to make mmbase
 * work with informix dynamic server.</p>
 * <p>
 * Tested on:
 * <ul>
 *     <li>ProductName: Informix Dynamic Server
 *     <li>ProductVersion: 9.21.UC3
 *     <li>JDBC-DriverName: Informix JDBC Driver for Informix Dynamic Server
 *     <li>JDBC-DriverVersion: 2.21.JC2
 * </ul>
 * </p>
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Daniel Ockeloen
 * @author Mark Huijser
 * @author Pierre van Rooden
 * @version $Id: MMInformix42Node.java,v 1.51 2004-01-27 12:04:47 pierre Exp $
 */
public class MMInformix42Node extends MMSQL92Node implements MMJdbc2NodeInterface {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(MMInformix42Node.class.getName());

    private boolean keySupported = true;

    private int currentdbkey = -1;
    private int currentdbkeyhigh = -1;

    /**
     *  Class Constructor
     */
    public MMInformix42Node() {
        // Call the constructor of the parent
        super();

        name = "informix";
    }

    /**
     * The createObjectTable method is used to create toplevel database object.
     * Only used for backwards compatibility with old builder-configs that
     * don't support builderinheritance (ages ago)</p>
     *
     * @param baseName  baseName
     * @return true if the creation succeeded
     */
    public boolean createObjectTable(String baseName) {
        if (log.isDebugEnabled()) log.trace(" ");
        try {
            MultiConnection con = mmb.getConnection();
            Statement stmt = con.createStatement();

            stmt.executeUpdate("create row type " + baseName + "_object_t (number integer not null, otype integer not null, owner nvarchar(12) not null);");
            log.debug("create row type " + baseName + "_object_t (number integer not null, otype integer not null, owner nvarchar(12) not null);");

            stmt.executeUpdate("create table " + baseName + "_object of type " + baseName + "_object_t ( PRIMARY KEY (number) );");
            log.debug("create table " + baseName + "_object of type " + baseName + "_object_t ( PRIMARY KEY (number) );");

            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("can't create table " + baseName + "_object");
            log.error(Logging.stackTrace(e));
        }
        return (true);
    }

    /**
     * Creates the database-table for the specified builder.
     *
     * @param bul  Builder which will be used to create the object-table
     * @return     true if the creation succeeded
     */
    public boolean create(MMObjectBuilder bul) {
        if (log.isDebugEnabled()) log.trace(" ");

        List fieldDefsList = bul.getFields(FieldDefs.ORDER_CREATE);
        String fieldList = null;

        for (Iterator fieldDefsIterator = fieldDefsList.iterator(); fieldDefsIterator.hasNext();) {
            FieldDefs def = (FieldDefs) fieldDefsIterator.next();
            String name = def.getDBName();

            if (def.getDBState() != org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                if (!isInheritedField(bul, name)) {
                    log.debug("trying to retrieve the part for field : " + name);
                    String part = getDbFieldDef(def, bul);
                    log.debug("gonna add field " + name + " with SQL-subpart: " + part);
                    if (fieldList == null) {
                        fieldList = part;
                    } else {
                        fieldList += ", " + part;
                    }
                } else {
                    log.debug("field: '" + name + "' from builder: '" + bul.getTableName() + "' is inherited field");
                }
            }
        }

        /* Here, we'll prepare the SQL-Strings we need for creating
         * the table in the database.
         */
        String sqlCreateRowType;

        if (fieldList != null) {
            sqlCreateRowType = "create row type " + mmb.baseName + "_" + bul.getTableName() + "_t (" + fieldList + ")";
        } else {
            sqlCreateRowType = "create row type " + mmb.baseName + "_" + bul.getTableName() + "_t";
        }

        String sqlCreateTable = "create table " + mmb.baseName + "_" + bul.getTableName() + " of type " + mmb.baseName + "_" + bul.getTableName() + "_t";

        // Add the inheritance part of the SQL-statement
        if (getInheritTableName(bul) != null) {
            sqlCreateRowType += " under " + mmb.baseName + "_" + getInheritTableName(bul) + "_t;";
            sqlCreateTable += " under " + mmb.baseName + "_" + getInheritTableName(bul) + ";";
        } else {
            // If the builder has no parent, this *must* be the object-table.
            // Well, lets add the key then ;-)
            sqlCreateTable += " ( PRIMARY KEY (number) );";
        }

        log.debug("Gonna create a new table with the following statements: ");
        log.debug(" - " + sqlCreateRowType);
        log.debug(" - " + sqlCreateTable);

        MultiConnection con = null;
        Statement stmt = null;
        try {
            con = mmb.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate(sqlCreateRowType);
            stmt.executeUpdate(sqlCreateTable);
            stmt.close();
            con.close();
            return true;
        } catch (SQLException sqle) {
            log.error("Not able to create table for builder " + bul.getTableName());
            for (SQLException e = sqle; e != null; e = e.getNextException()) {
                log.error("\tSQLState : " + e.getSQLState());
                log.error("\tErrorCode : " + e.getErrorCode());
                log.error("\tMessage : " + e.getMessage());
            }
            try {
                if (stmt != null) stmt.close();
                // con.rollback();
                con.close();
            } catch (Exception other) {
            }
        }
        return false;
    }

    /**
     * Returns the tablename of the specified builder's parent builder.
     * If there's no parent builder or the buildername is "object", null
     * will be returned.
     *
     * @param bul  Builder which we need to test
     * @return Tablename of the parent-builder
     * @since MMBase-1.6
     */
    private String getInheritTableName(MMObjectBuilder bul) {
        if (log.isDebugEnabled()) log.trace(" ");

        // object table _must_ always be the top builder....
        if (bul.getTableName().equals("object")) return null;

        // builder extends something,... return it....
        if (bul.getParentBuilder() != null) {
            return bul.getParentBuilder().getTableName();
        }

        // fallback to the old code...
        log.warn("Need to fall back to old inherit code. Define a object.xml, and use <builder ... extends=\"object\"> in " + bul.getTableName() + ".xml");

        if (bul instanceof InsRel && !bul.getTableName().equals("insrel")) return "insrel";

        return "object";
    }

    /**
     * Returns true if the specified builder/fieldname is a inherited field
     *
     * @param bul  Builder which we need to test
     * @param fieldname Fieldname which we need to test
     * @return true if the specified builder/fieldname is a inherited field
     * @since MMBase-1.6
     *
     */
    private boolean isInheritedField(MMObjectBuilder bul, String fieldname) {
        if (log.isDebugEnabled()) log.trace(" ");

        if (getInheritTableName(bul) == null) {
            // our top table, all fields must be created
            return false;
        }

        if (bul.getParentBuilder() != null) {
            // if parent builder has the field, it is inherited..
            return bul.getParentBuilder().getField(fieldname) != null;
        }

        // fallback to the old code
        log.warn("Need to fall back to old inherit code. Define a object.xml, and use <builder ... extends=\"object\"> in " + bul.getTableName() + ".xml");

        // normally we inherited from object..
        if (fieldname.equals("number")) return true;
        if (fieldname.equals("owner")) return true;
        if (fieldname.equals("otype")) return true;

        // if we are something to do with relations...
        if (bul instanceof InsRel && !bul.getTableName().equals("insrel")) {
            if (fieldname.equals("snumber")) return true;
            if (fieldname.equals("dnumber")) return true;
            if (fieldname.equals("rnumber")) return true;
            if (fieldname.equals("dir")) return true;
        }
        return false;
    }


    /**
     * <p>Maps the given FieldDefinition to the informix data type.</p>
     * <p>
     * The returned String can e.g. be used as a part of the column definition clause
     * in a "create table" or "create row type"  statement.
     * </p>
     * @param def  FieldDefinition
     * @param bul Builder
     * @return String fielddefinition as a string
     * @since MMBase-1.6
     *
     */
    private String getDbFieldDef(FieldDefs def, MMObjectBuilder bul) {
        if (log.isDebugEnabled()) log.trace(" ");
        // create the creation line of one field...
        // would be something like : fieldname FIELDTYPE NOT NULL KEY "
        // first get our thingies...
        String fieldName = getAllowedField(def.getDBName());

        // again an hack for number field thing...
        /* Lets hack it out .... (MArk)
        if(getNumberString().equals(fieldName)) {
            return getNumberString()+" INTEGER PRIMARY KEY \t-- the unique identifier for objects\n";
        }
         */
        boolean fieldRequired = def.getDBNotNull();
        boolean fieldUnique = def.isKey();

        String fieldType = getDbFieldType(def, def.getDBSize(), fieldRequired);
        String result = fieldName + " " + fieldType;
        if (fieldRequired) {
            result += " " + parser.getNotNullScheme();
        }
        if (fieldUnique) {
            //TODO : parser.getKeyScheme()+ "("+name+") so make a
            // result += " UNIQUE ";
        }

        // add in comment the gui stuff... nicer when reviewing database..
        // result += "\t-- " + def.getGUIName("us")+"(name: '"+def.getGUIName()+"' gui-type: '"+def.GUIType+"')\n";
        return result;
    }

    /**
     * <p>Maps the given FieldDefinition, Size and Requirement to the informix data type.</p>
     * <p>
     * The returned String can e.g. be used as a part of the column definition clause
     * in a "create table" or "create row type"  statement.</p>
     *
     * @param fieldDef  FieldDef
     * @param fieldSize Size of the field
     * @param fieldRequired True if this is a required field
     * @return String Informix type
     * @since MMBase-1.6
     *
     **/
    private String getDbFieldType(FieldDefs fieldDef, int fieldSize, boolean fieldRequired) {
        if (log.isDebugEnabled()) log.trace(" ");
        if (typeMapping == null) {
            String msg = "typeMapping was null";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        dTypeInfos typs = (dTypeInfos) typeMapping.get(new Integer(fieldDef.getDBType()));
        if (typs == null) {
            String msg = "Could not find the typ mapping for the field with the value: " + fieldDef.getDBType();
            log.error(msg);
            throw new RuntimeException(msg);
        }
        Enumeration e = typs.maps.elements();
        // look if we can find our info thingie...
        String closedMatch = null;
        while (e.hasMoreElements()) {
            dTypeInfo typ = (dTypeInfo) e.nextElement();
            if (fieldSize == -1 || (typ.minSize == -1 && typ.maxSize == -1)) {
                closedMatch = typ.dbType;
            } else if (typ.minSize == -1) {
                if (fieldSize <= typ.maxSize) closedMatch = typ.dbType;
            } else if (typ.maxSize == -1) {
                if (typ.minSize <= fieldSize) closedMatch = typ.dbType;
            } else if (typ.minSize <= fieldSize && fieldSize <= typ.maxSize) {
                // we have the proper match !!
                // if there is a size thingie.. then make it our size...
                int pos = typ.dbType.indexOf("size");
                if (pos != -1) {
                    return typ.dbType.substring(0, pos) + fieldSize + typ.dbType.substring(pos + 4);
                }
                return typ.dbType;
            }
        }

        if (closedMatch == null) {
            throw new RuntimeException("not field def found !!");
        }

        int pos = closedMatch.indexOf("size");

        if (pos != -1) {
            return closedMatch.substring(0, pos) + fieldSize + closedMatch.substring(pos + 4);
        }

        return closedMatch;
    }


    /**
     * Converts XML-field-definition to a string ???
     *
     *
     * @param def FieldDef
     * @return converted fieldDef
     */
    public String convertXMLType(FieldDefs def) {
        if (log.isDebugEnabled()) log.trace(" ");

        // get the wanted mmbase type
        int type = def.getDBType();
        // get the wanted mmbase type
        String name = def.getDBName();

        // get the wanted size
        int size = def.getDBSize();

        // get the wanted notnull
        boolean notnull = def.getDBNotNull();

        //get the wanted key
        boolean iskey = def.isKey();

        if (name.equals("otype")) {
            return ("otype integer " + parser.getNotNullScheme());
        } else {
            if (disallowed2allowed.containsKey(name)) {
                name = (String) disallowed2allowed.get(name);
            }
            String result = name + " " + matchType(type, size, notnull);
            if (notnull) result += " " + parser.getNotNullScheme();
            return (result);
        }
    }

    /**
     * This method inserts a new object, normally not used (only subtables are used)
     * Only fields with DBState value = DBSTATE_PERSISTENT or DBSTATE_SYSTEM are inserted.
     * Fields with DBstate values = DBSTATE_VIRTUAL or any other value are skipped.
     *
     * @param bul    The MMObjectBuilder.
     * @param owner  The nodes' owner.
     * @param node   The current node that's to be inserted.
     * @return       The DBKey number for this node, or -1 if an error occurs.
     */
    public int insert(MMObjectBuilder bul, String owner, MMObjectNode node) {
        if (log.isDebugEnabled()) log.trace("Inserting node : " + node.toString());

        // Figure out what number we need to commit
        int number = node.getIntValue("number");
        // did the user supply a number allready, ifnot try to obtain one
        if (number == -1) number = getDBKey();
        // did it fail ? ifso exit, ifnot setvalue to the node.
        if (number == -1) {
            return (-1);
        } else {
            node.setValue("number", number);
        }

        // Create a String that represents the amount of DB fields to be used in the insert.
        // First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
        String fieldAmounts = "";

        List fieldDefsList = bul.getFields(FieldDefs.ORDER_CREATE);

        // Append the DB elements to the fieldAmounts String.
        for (Iterator fieldDefsIterator = fieldDefsList.iterator(); fieldDefsIterator.hasNext();) {
            FieldDefs def = (FieldDefs) fieldDefsIterator.next();
            String key = def.getDBName();
            int DBState = node.getDBState(key);
            if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
                    || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM)) {
                if (log.isDebugEnabled()) log.trace("Insert: DBState = " + DBState + ", adding key: " + key);
                if (fieldAmounts == "") {
                    fieldAmounts = "?";
                } else {
                    fieldAmounts += ",?";
                }

            } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                if (log.isDebugEnabled()) log.trace("Insert: DBState = " + DBState + ", skipping key: " + key);
            } else {
                if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                    if (fieldAmounts == "") {
                        fieldAmounts = "?";
                    } else {
                        fieldAmounts += ",?";
                    }

                } else {
                    log.error("Insert: DBState = " + DBState + " unknown!, skipping key: " + key + " of builder:" + node.getName());
                }
            }
        }

        MultiConnection con = null;
        PreparedStatement stmt = null;

        try {
            // Create the DB statement with DBState values in mind.
            con = bul.mmb.getConnection();
            stmt = con.prepareStatement("insert into " + mmb.baseName + "_" + bul.tableName + " values(" + fieldAmounts + ")");
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }

        if (log.isDebugEnabled()) log.trace("insert(): Preparing statement using fieldamount String: " + fieldAmounts);
        if (log.isDebugEnabled()) log.trace("insert into " + mmb.baseName + "_" + bul.tableName + " values(" + fieldAmounts + ")");

        try {
            stmt.setEscapeProcessing(false);

            // Prepare the statement for the DB elements to the fieldAmounts String.
            if (log.isDebugEnabled()) log.trace("Insert: Preparing statement using fieldamount String: " + fieldAmounts);

            int j = 1;

            fieldDefsList = bul.getFields(FieldDefs.ORDER_CREATE);

            for (Iterator fieldDefsIterator = fieldDefsList.iterator(); fieldDefsIterator.hasNext();) {
                FieldDefs def = (FieldDefs) fieldDefsIterator.next();
                String key = def.getDBName();

                int DBState = node.getDBState(key);
                if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
                        || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM)) {
                    if (log.isDebugEnabled()) log.trace("Insert: DBState = " + DBState + ", setValuePreparedStatement for key: " + key + ", at pos:" + j);
                    setValuePreparedStatement(stmt, node, key, j);
                    j++;
                } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    if (log.isDebugEnabled()) log.trace("insert(): DBState = " + DBState + ", skipping setValuePreparedStatement for key: " + key);
                } else {
                    if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                        setValuePreparedStatement(stmt, node, key, j);
                        j++;
                    } else {
                        log.warn("insert(): DBState = " + DBState + " unknown!, skipping setValuePreparedStatement for key: " + key + " of builder:" + node.getName());
                    }
                }
            }

            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("insert(): Error on : " + number + " " + owner + " fake");
            log.debug(e.getMessage());
            log.debug(e.getSQLState());
            try {
                stmt.close();
                con.close();
            } catch (Exception t2) {
                log.error(Logging.stackTrace(t2));
            }
            log.error(Logging.stackTrace(e));
            return (-1);
        }

        node.setValue("number", number);

        //bul.signalNewObject(bul.tableName,number);
        if (bul.broadcastChanges) {
            if (bul instanceof InsRel) {
                bul.mmb.mmc.changedNode(node.getIntValue("number"), bul.tableName, "n");
                // figure out tables to send the changed relations
                MMObjectNode n1 = bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2 = bul.getNode(node.getIntValue("dnumber"));
                n1.delRelationsCache();
                n2.delRelationsCache();
                mmb.mmc.changedNode(n1.getIntValue("number"), n1.getName(), "r");
                mmb.mmc.changedNode(n2.getIntValue("number"), n2.getName(), "r");
            } else {
                mmb.mmc.changedNode(node.getIntValue("number"), bul.tableName, "n");
            }
        }

        node.clearChanged();

        if (log.isDebugEnabled()) log.trace("INSERTED=" + node);
        return (number);
    }

    /**
     * <p>Copies the value of the specified node / key to parameterIndex "i"
     * of the PreparedStatement
     * </p>
     * @param stmt Object that represents a precompiled SQL statement
     * @param node MMObjectNode that needs to be used to retreive the value from
     * @param key Fieldname of the sepecified node from which we have to retreive the value
     * @param i parameterIndex 1 is the first value, 2 is the second value, ...
     */
    private void setValuePreparedStatement(PreparedStatement stmt, MMObjectNode node, String key, int i) throws SQLException {
        switch (node.getDBType(key)) {
            case FieldDefs.TYPE_NODE:
            case FieldDefs.TYPE_INTEGER:
                stmt.setInt(i, node.getIntValue(key));
                break;
            case FieldDefs.TYPE_FLOAT:
                stmt.setFloat(i, node.getFloatValue(key));
                break;
            case FieldDefs.TYPE_DOUBLE:
                stmt.setDouble(i, node.getDoubleValue(key));
                break;
            case FieldDefs.TYPE_LONG:
                stmt.setLong(i, node.getLongValue(key));
                break;
            case FieldDefs.TYPE_XML:
            case FieldDefs.TYPE_STRING:
                // Getting dbType info in case were dealing with a clob
                ResultSetMetaData rd = stmt.getMetaData();
                String dbType = rd.getColumnTypeName(i);

                if (dbType.equals("clob")) {
                    setDBClob(i, stmt, node.getStringValue(key));
                } else {
                    setDBText(i, stmt, node.getStringValue(key));
                }
                break;
            case FieldDefs.TYPE_BYTE:
                setDBByte(i, stmt, node.getByteValue(key));
                break;
            default:
                // Here we've got a field-type we don't know. What shall we do?
                String tmp = node.getStringValue(key);
                if (tmp != null) {
                    stmt.setString(i, tmp);
                } else {
                    stmt.setString(i, "");
                }
                break;
        }
    }

    /*
    *
    * @param node MMobjectNode to decode
    * @param fieldname Fieldname to decode
    * @param rs ResultSet
    * @param i What the * is this int for?
    * @param prefix contains a prefix for the fieldname
    * @return MMObjectNode
    *
    */
    public MMObjectNode decodeDBnodeField(MMObjectNode node, String fieldname, ResultSet rs, int i, String prefix) {

        try {
            if (node == null) {
                log.error("decodeDBNodeField() node is null");
                return null;
            }

            fieldname = fieldname.toLowerCase();

            // is this fieldname disallowed ? ifso map it back
            if (allowed2disallowed.containsKey(fieldname)) {
                fieldname = (String) allowed2disallowed.get(fieldname);
            }

            //int type=((Integer)typesmap.get(fieldtype)).intValue();
            int type = node.getDBType(prefix + fieldname);

            String dbType = "";

            switch (type) {
                case FieldDefs.TYPE_XML:
                case FieldDefs.TYPE_STRING:
                    // First, figure out what kind of String we're dealing with
                    ResultSetMetaData rd = rs.getMetaData();
                    dbType = rd.getColumnTypeName(i);

                    String contentString = null;
                    String encodedString = null;
                    byte[] contentBytes = null;

                    try {
                        contentString = rs.getString(i);
                        if (contentString != null) {
                            contentBytes = contentString.getBytes();
                            encodedString = new String(contentBytes, mmb.getEncoding());
                        }
                    } catch (Exception e) {
                        log.error("Can't get String from resultset");
                        log.error(e.getMessage());
                        log.error(Logging.stackTrace(e));
                    }

                    if (dbType.equals("clob")) {
                        // If the data was retrieved from a clob we need to use the encoded string
                        if (encodedString == null) {
                            node.setValue(prefix + fieldname, "");
                        } else {
                            node.setValue(prefix + fieldname, encodedString.trim());
                        }
                    } else {
                        // Otherwise we can use the contentString
                        if (contentString == null) {
                            node.setValue(prefix + fieldname, "");
                        } else {
                            node.setValue(prefix + fieldname, contentString.trim());
                        }
                    }

                    break;
                case FieldDefs.TYPE_NODE:
                case FieldDefs.TYPE_INTEGER:
                    log.trace("Fieldtype is Integer / Node");
                    node.setValue(prefix + fieldname, rs.getInt(i));
                    break;
                case FieldDefs.TYPE_LONG:
                    log.trace("FieldType is long");
                    node.setValue(prefix + fieldname, rs.getObject(i));
                    break;
                case FieldDefs.TYPE_FLOAT:
                    log.trace("FieldType is float");
                    // who does this now work ????
                    //node.setValue(prefix+fieldname,((Float)rs.getObject(i)));
                    node.setValue(prefix + fieldname, new Float(rs.getFloat(i)));
                    break;
                case FieldDefs.TYPE_DOUBLE:
                    log.trace("FieldType is double");
                    node.setValue(prefix + fieldname, rs.getObject(i));
                    break;
                case FieldDefs.TYPE_BYTE:
                    log.trace("FieldType is byte");
                    node.setValue(prefix + fieldname, "$SHORTED");
                    break;
                default:
                    log.warn("decodeDBNodeField(): unknown type=" + type + " builder=" + node.getName() + " fieldname=" + fieldname);
                    break;
            }
        } catch (SQLException e) {
            log.error("mmObject->" + fieldname + " node=" + node.getIntValue("number"));
            log.error(Logging.stackTrace(e));
        }
        return (node);
    }

    /**
     * Returns whether this database support layer allows for builder to be a parent builder
     * (that is, other builders can 'extend' this builder and its database tables).
     *
     * @since MMBase-1.6
     * @param builder the builder to test
     * @return true if the builder can be extended
     */
    public boolean isAllowedParentBuilder(MMObjectBuilder builder) {
        // Since every builder is allowed to be a parent-builder, we may
        // allways return true.
        return true;
    }


    /*
    * Method: getDBText
    *
    */
    public String getDBText(ResultSet rs, int idx) {
        if (log.isDebugEnabled()) log.trace(" ");
        String str = null;
        byte[] isochars = null;
        int siz;

        try {
            //inp=rs.getAsciiStream(idx);
            //if (inp==null) {
            //        if (log.isDebugEnabled()) log.trace("Informix42Node DBtext no ascii "+inp);
            //         return("");
            //}
            //if (rs.wasNull()) {
            //       if (log.isDebugEnabled()) log.trace("Informix42Node DBtext wasNull "+inp);
            //       return("");
            //}
            //siz=inp.available(); // DIRTY
            isochars = rs.getBytes(idx);
            siz = isochars.length;
            log.debug("size of text is: " + siz);
            if (log.isDebugEnabled()) log.trace("Informix42Node DBtext SIZE=" + siz);
            if (siz == 0 || siz == -1) return ("");
            //input=new DataInputStream(inp);
            //isochars=new byte[siz];
            //input.readFully(isochars);
            str = new String(isochars, "UTF8");

            log.debug("string is: " + str);

            //input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("Informix42Node text  exception " + e);
            log.error(Logging.stackTrace(e));
            return ("");
        }
        return (str);
    }

    /*
    * Method: getDBByte
    *
    */
    public byte[] getDBByte(ResultSet rs, int idx) {
        if (log.isDebugEnabled()) log.trace(" ");

        InputStream inp;
        DataInputStream input;
        byte[] bytes = null;
        int siz;
        try {
            inp = rs.getBinaryStream(idx);
            siz = inp.available(); // DIRTY
            input = new DataInputStream(inp);
            bytes = new byte[siz];
            input.readFully(bytes);
            input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("Informix42Node byte  exception " + e);
            log.error(Logging.stackTrace(e));
        }
        return (bytes);
    }


    /*
    * Method: parseFieldPart
    *
    */
    public String parseFieldPart(String fieldname, String dbtype, String part) {
        if (log.isDebugEnabled()) log.trace(" ");

        String result = "";
        boolean like = false;
        char operatorChar = part.charAt(0);
        //if (log.isDebugEnabled()) log.trace("char="+operatorChar);
        String value = part.substring(1);
        int pos = value.indexOf("*");
        if (pos != -1) {
            value = value.substring(pos + 1, value.length() - 1);
            like = true;
        }
        if (dbtype.equals("varchar")) {
            switch (operatorChar) {
                case '=':
                case 'E':
                    // EQUAL
                    if (like) {
                        result += "lower(" + fieldname + ") LIKE '%" + value + "%'";
                    } else {
                        result += "lower(" + fieldname + ") LIKE '%" + value + "%'";
                    }
                    break;
            }
        } else if (dbtype.equals("varchar_ex")) {
            switch (operatorChar) {
                case '=':
                case 'E':
                    // EQUAL
                    result += "etx_contains(" + fieldname + ",Row('" + value + "','SEARCH_TYPE=PROX_SEARCH(5)'))";
                    if (log.isDebugEnabled()) log.trace("etx_contains(" + fieldname + ",Row('" + value + "','SEARCH_TYPE=PROX_SEARCH(5)'))");
                    break;
            }

        } else if (dbtype.equals("int")) {
            switch (operatorChar) {
                case '=':
                case 'E':
                    // EQUAL
                    result += fieldname + "=" + value;
                    break;
                case 'N':
                    // NOTEQUAL;
                    result += fieldname + "<>" + value;
                    break;
                case 'G':
                    // GREATER;
                    result += fieldname + ">" + value;
                    break;
                case 'g':
                    // GREATEREQUAL;
                    result += fieldname + ">=" + value;
                    break;
                case 'S':
                    // SMALLER;
                    result += fieldname + "<" + value;
                    break;
                case 's':
                    // SMALLEREQUAL;
                    result += fieldname + "<=" + value;
                    break;
            }
        }
        return (result);
    }

    /**
     * Method: getShortedByte
     *         get byte of a database blob
     */
    public byte[] getShortedByte(String tableName, String fieldname, int number) {
        if (log.isDebugEnabled()) log.trace(" ");
        try {
            byte[] result = null;
            MultiConnection con = mmb.getConnection();
            Statement stmt = con.createStatement();
            if (log.isDebugEnabled()) log.trace("SELECT " + fieldname + " FROM " + mmb.baseName + "_" + tableName + " where number=" + number);
            ResultSet rs = stmt.executeQuery("SELECT " + fieldname + " FROM " + mmb.baseName + "_" + tableName + " where number=" + number);
            try {
                if (rs.next()) {
                    result = getDBByte(rs, 1);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
            return (result);
        } catch (Exception e) {
            log.error("getShortedByte(): trying to load bytes");
            log.error(Logging.stackTrace(e));
        }

        return (null);
    }

    /**
     * Method: getShortedText
     *         get text from blob
     */

    public String getShortedText(String tableName, String fieldname, int number) {
        if (log.isDebugEnabled()) log.trace(" ");
        try {
            String result = null;
            MultiConnection con = mmb.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + fieldname + " FROM " + mmb.baseName + "_" + tableName + " where number=" + number);
            try {
                if (rs.next()) {
                    result = getDBText(rs, 1);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
            return (result);
        } catch (Exception e) {
            log.error("getShortedText(): trying to load text");
            log.error(Logging.stackTrace(e));
        }
        // return "" instead of null, not sure if this is oke
        return ("");
    }

    /**
     * Inserts the given string into the passed PreparedStatement using the
     * MMBase characterencoding.
     *
     * @param i parameterIndex - the first parameter is 1, the second is 2, ...
     * @param stmt PreparedStatement - holds a precompiled SQL statement
     * @param body String that needs to be added to the statement
     *
     */

    public void setDBText(int i, PreparedStatement stmt, String body) {
        if (body == null) body = "";

        try {
            // set the string
            stmt.setString(i, body);
        } catch (Exception e) {
            log.error("Can't commmit string to database");
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));
        }
    }


    /**
     * Creates a smart large object  in the database using
     * the MMBase character encoding, and sets the field using
     * PreparedStatement.setClob.
     *
     * @param i parameterIndex the first parameter is 1, the second one is 2, ...
     * @param stmt object that represents a precompiled SQL Statement
     * @param body value that needs to be inserted/updated
     */
    public void setDBClob(int i, PreparedStatement stmt, String body) {
        if (log.isDebugEnabled()) log.trace(" ");

        try {

            // copy string into bytearray using the mmbase char-encoding
            byte[] encodedBytes = body.getBytes(mmb.getEncoding());

            // copy the bytearray to a new string
            //String encodedByteString = new String(encodedBytes);

            // create a lob descriptor
            IfxLobDescriptor ifxLobDescr = new IfxLobDescriptor(stmt.getConnection());

            //create the locator (pointer to lob)
            IfxLocator ifxLobLoc = new IfxLocator();

            // create the smart large object
            IfxSmartBlob smartBlob = new IfxSmartBlob(stmt.getConnection());

            // Create the Smart Large Object in the database
            int loFd = smartBlob.IfxLoCreate(ifxLobDescr, smartBlob.LO_RDWR, ifxLobLoc);
            if (log.isDebugEnabled()) log.debug("Smartblob created");

            // now write the lob-data to the lob in the database
            if (!body.equals(null) && !body.equals("")) {
                int bytesWritten = smartBlob.IfxLoWrite(loFd, encodedBytes);
                if (log.isDebugEnabled()) log.debug("wrote data from byte array into database");
            }

            //Close the smart object
            smartBlob.IfxLoClose(loFd);

            // Create the informixClob-object using the locator
            IfxCblob ifxClob = new IfxCblob(ifxLobLoc);

            // ... And commit it to the prepared statement
            stmt.setClob(i, ifxClob);

        } catch (Exception e) {
            log.error("Can't commit Clob to database");
            log.error(e.getMessage());
            log.error(Logging.stackTrace(e));

        }
    }

    /**
     * Add a byte-array to the PreparedStatement
     *
     * @param i parameterIndex the first parameter is 1, the second one is 2, ...
     * @param stmt object that represents a precompiled SQL Statement
     * @param bytes bytearray that needs to be inserted/updated
     */
    public void setDBByte(int i, PreparedStatement stmt, byte[] bytes) {
        if (log.isDebugEnabled()) log.trace(" ");

        try {
            if (log.isDebugEnabled()) log.trace("in setDBByte ... just before creating ByteArrayInputStream()");

            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            if (log.isDebugEnabled()) log.trace("in setDBByte ... right after creating ByteArrayInputStream()");
            if (log.isDebugEnabled()) log.trace("in setDBByte ... before stmt");
            stmt.setBinaryStream(i, stream, bytes.length);
            if (log.isDebugEnabled()) log.trace("in setDBByte ... after stmt");
            stream.close();
        } catch (Exception e) {
            log.error("Can't set byte stream");
            log.error(Logging.stackTrace(e));
        }


        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            stmt.setBinaryStream(i, stream, bytes.length);
            stream.close();
        } catch (Exception e) {
            log.error("getDBByte(): Can't set byte stream");
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * Method: commit
     *         commit this node to the database
     */
    /* begin copy old method overiding new one, stolen from MMSQL92Node.java,v 1.50 2001/04/20 08:33:25, which has now multiple table support */
    public boolean commit(MMObjectBuilder bul, MMObjectNode node) {
        if (log.isDebugEnabled()) log.trace(" ");
        //  precommit call, needed to convert or add things before a save
        bul.preCommit(node);
        // commit the object
        String values = "";
        String key;
        // create the prepared statement
        for (Enumeration e = node.getChanged().elements(); e.hasMoreElements();) {
            key = (String) e.nextElement();
            // a extra check should be added to filter temp values
            // like properties

            // is this key disallowed ? ifso map it back
            if (disallowed2allowed.containsKey(key)) {
                key = (String) disallowed2allowed.get(key);
            }

            // check if its the first time for the ',';
            if (values.equals("")) {
                values += " " + key + "=?";
            } else {
                values += ", " + key + "=?";
            }
        }

        if (values.length() > 0) {
            values = "update " + mmb.baseName + "_" + bul.tableName + " set" + values + " WHERE " + getNumberString() + "=" + node.getValue("number");
            try {
                MultiConnection con = mmb.getConnection();
                PreparedStatement stmt = con.prepareStatement(values);

                int i = 1;
                for (Enumeration e = node.getChanged().elements(); e.hasMoreElements();) {
                    key = (String) e.nextElement();

                    switch (node.getDBType(key)) {
                        case FieldDefs.TYPE_NODE:
                        case FieldDefs.TYPE_INTEGER:
                            stmt.setInt(i, node.getIntValue(key));
                            break;
                        case FieldDefs.TYPE_FLOAT:
                            stmt.setFloat(i, node.getFloatValue(key));
                            break;
                        case FieldDefs.TYPE_DOUBLE:
                            stmt.setDouble(i, node.getDoubleValue(key));
                            break;
                        case FieldDefs.TYPE_LONG:
                            stmt.setLong(i, node.getLongValue(key));
                            break;
                        case FieldDefs.TYPE_STRING:
                        case FieldDefs.TYPE_XML:
                            // Getting dbType info in case were dealing with a clob
                            ResultSetMetaData rd = stmt.getMetaData();
                            if (rd.getColumnTypeName(i).equals("clob")) {
                                setDBClob(i, stmt, node.getStringValue(key));
                            } else {
                                setDBText(i, stmt, node.getStringValue(key));
                            }
                            break;
                        case FieldDefs.TYPE_BYTE:
                            setDBByte(i, stmt, node.getByteValue(key));
                            break;
                        default:
                            stmt.setString(i, node.getStringValue(key));
                            break;
                    }
                    i++;
                }
                stmt.executeUpdate();
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error(Logging.stackTrace(e));
                return (false);
            }
        }

        node.clearChanged();
        if (bul.broadcastChanges) {
            if (bul instanceof InsRel) {
                bul.mmb.mmc.changedNode(node.getIntValue("number"), bul.tableName, "c");
                // figure out tables to send the changed relations
                MMObjectNode n1 = bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2 = bul.getNode(node.getIntValue("dnumber"));
                mmb.mmc.changedNode(n1.getIntValue("number"), n1.getName(), "r");
                mmb.mmc.changedNode(n2.getIntValue("number"), n2.getName(), "r");
            } else {
                mmb.mmc.changedNode(node.getIntValue("number"), bul.tableName, "c");
            }
        }
        return (true);
    }
    /* end copy old method overiding new one, stolen from MMSQL92Node.java,v 1.50 2001/04/20 08:33:25, which has now mutliple table support */


    /**
     * Method: removeNode
     *
     */
    public void removeNode(MMObjectBuilder bul, MMObjectNode node) {
        if (log.isDebugEnabled()) log.trace(" ");

        int number = node.getIntValue("number");
        // temp removed (daniel) despr. if (log.isDebugEnabled()) log.trace("removeNode(): delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number+" at "+d.toGMTString());
        if (log.isDebugEnabled()) log.trace("removeNode(): SAVECOPY " + node.toString());
        Vector rels = bul.getRelations_main(number);
        if (rels != null && rels.size() > 0) {
            log.error("removeNode(" + bul.tableName + "," + number + "): still relations attached : delete from " + mmb.baseName + "_" + bul.tableName + " where number=" + number);
        } else {
            if (number != -1) {
                try {
                    MultiConnection con = mmb.getConnection();
                    Statement stmt = con.createStatement();
                    stmt.executeUpdate("delete from " + mmb.baseName + "_" + bul.tableName + " where number=" + number);
                    stmt.close();
                    con.close();
                } catch (SQLException e) {
                    log.error("removeNode(" + bul.tableName + "," + number + "): ");
                    log.error(Logging.stackTrace(e));
                }
            } else
                log.error("removeNode(" + bul.tableName + "," + number + "): number not valid(-1)!");
        }
        if (bul.broadcastChanges) {
            mmb.mmc.changedNode(node.getIntValue("number"), bul.tableName, "d");
            if (bul instanceof InsRel) {
                MMObjectNode n1 = bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2 = bul.getNode(node.getIntValue("dnumber"));
                mmb.mmc.changedNode(n1.getIntValue("number"), n1.getName(), "r");
                mmb.mmc.changedNode(n2.getIntValue("number"), n2.getName(), "r");
            } else
                log.warn("removeNode(" + bul.tableName + "," + number + "): want to remove it, but not an insrel (not implemented).");
        }

    }

    /**
     * Overriding getDBKeyOld because the one in MMSQL92Node doesn't really work
     *
     */
    public synchronized int getDBKeyOld() {
        if (log.isDebugEnabled()) log.trace("GetDBKeyOld");
        return getDBKey();
    }

    /**
     * getDBKey() uses a user defined routine (fetchrelkey()) at the database
     * side to get a number (10) of keys at once. The fetched keys will be
     * returned to the requester until all numbers are used, then getDBKey
     * will fetch a new bunch of keys ...
     *
     * See <a href="http://www.mmbase.org/">http://www.mmbase.org/</a> for
     * an sql-script that creates the User Defined Routine you need.
     *
     */
    public synchronized int getDBKey() {
        if (log.isDebugEnabled()) log.trace("GetDBKey");
        // get a new key

        if (currentdbkey != -1) {
            currentdbkey++;
            if (currentdbkey <= currentdbkeyhigh) {
                if (log.isDebugEnabled()) log.trace("GETDBKEY=" + currentdbkey);
                return (currentdbkey);
            }
        }

        int number = -1; // not 100% sure if function returns 1 first time
        while (number == -1) {
            try {
                MultiConnection con = mmb.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("execute function fetchrelkey(10)");
                try {
                    while (rs.next()) {
                        number = rs.getInt(1);
                    }
                } finally {
                    rs.close();
                }
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error("getDBKey(): while getting a new key number");
                log.error(Logging.stackTrace(e));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException re) {
                    if (log.isDebugEnabled()) log.trace("getDBKey(): Waiting 2 seconds to allow database to unlock fetchrelkey()");
                }
                if (log.isDebugEnabled()) log.trace("getDBKey(): got key(" + currentdbkey + ")");
                return (-1);
            }
        }

        currentdbkey = number; // zeg 10
        currentdbkeyhigh = (number + 9); // zeg 19 dus indien hoger dan nieuw
        if (log.isDebugEnabled()) log.trace("getDBKey(): got key(" + currentdbkey + ")");
        return (number);
    }


    /*
    * Method: getAllNames()
    *
    */
    public synchronized Vector getAllNames() {
        if (log.isDebugEnabled()) log.trace(" ");
        Vector results = new Vector();
        try {
            MultiConnection con = mmb.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT tabname FROM systables where tabid>99;");
            try {
                String s;
                while (rs.next()) {
                    s = rs.getString(1);
                    if (s != null) s = s.trim();
                    results.addElement(s);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
            return (results);
        } catch (Exception e) {
            // log.error(Logging.stackTrace(e));
            return (results);
        }
    }

    public boolean addField(MMObjectBuilder bul, String fieldname) {
        if (log.isDebugEnabled()) log.trace(" ");
        log.error("Database doesn't support table changes !");
        return (false);
    }


    public boolean removeField(MMObjectBuilder bul, String fieldname) {
        if (log.isDebugEnabled()) log.trace(" ");
        log.error("Database doesn't support table changes !");
        return (false);
    }

    public boolean changeField(MMObjectBuilder bul, String fieldname) {
        log.error("Database doesn't support table changes !");
        return (false);
    }

    public boolean drop_real(MMObjectBuilder bul, String tableName) {
        if (log.isDebugEnabled()) log.trace(" ");
        log.error("Database doesn't support table changes !");
        return (false);
    }
}
