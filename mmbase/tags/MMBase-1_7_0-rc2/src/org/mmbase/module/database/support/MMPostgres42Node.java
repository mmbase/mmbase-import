/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

// PostgreSQL largeobject support
// temp, removed by daniel (should be in a better packages and cvs) import postgresql.largeobject.*;


/**
 * MMPostgres42Node implements the MMJdbc2NodeInterface for
 * mysql this is the class used to abstact the query's
 * needed for mmbase for each database.
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Carlo E. Prelz
 * @version $Id: MMPostgres42Node.java,v 1.17 2004-01-27 12:04:48 pierre Exp $
 */
public class MMPostgres42Node extends MMSQL92Node implements MMJdbc2NodeInterface {

    private static Logger log = Logging.getLoggerInstance(MMPostgres42Node.class.getName());

    // temp removed, private LargeObjectManager lobj=null;
    MMBase mmb;

    public MMPostgres42Node() {
    }

        /* temp removed
        public void init(MMBase mmb) {
                Connection c=null;
                this.mmb=mmb;
                try {
                        c=mmb.getDirectConnection();
                        lobj=((postgresql.Connection)c).getLargeObjectAPI();
                } catch (Exception e) {
                        log.error("Can't get LargeObejctManager "+e);
                        log.error(Logging.stackTrace(e));
                }
                try {
                        c.close();
                } catch (SQLException e) {
                        log.error("Can't close connection");
                        log.error(Logging.stackTrace(e));
                }
        }
         */

    public boolean create(MMObjectBuilder bul,String tableName) {
        // Note that builder is null when tableName='object'
        // get us a propertie reader
        ExtendedProperties Reader=new ExtendedProperties();

        // load the properties file of this server

        String root = MMBaseContext.getConfigPath();
        Hashtable prop = Reader.readProperties(root+"/defines/"+tableName+".def");

        String createtable=(String)prop.get("CREATETABLE_PG");

        if (createtable!=null && !createtable.equals("")) {
            createtable = Strip.DoubleQuote(createtable,Strip.BOTH);
            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                // informix	stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t "+createtable+" under "+mmb.baseName+"_object");
                //log.error("create table "+mmb.baseName+"_"+tableName+" "+createtable+";");
                /*KP.dbg("GOD: "+"create table "+mmb.baseName+"_"+tableName+" "+createtable+";");*/
                stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" "+createtable+";");
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error("can't create table "+tableName);
                log.error(Logging.stackTrace(e));
            }
        } else {
            log.error("Can't create table no CREATETABLE_ defined");
        }
        return(true);
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i) {
        return(decodeDBnodeField(node,fieldtype,fieldname,rs,i,""));
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i,String prefix) {
        /*KP.dbg("//// decodeDBnodeField: field is <"+fieldname+"/"+fieldtype+">"); */
        try {
            if (fieldtype.equalsIgnoreCase("VARSTRING") || fieldtype.equalsIgnoreCase("STRING") ||
            fieldtype.equalsIgnoreCase("VARCHAR")) {
                String tmp=rs.getString(i);
                if (tmp==null) {
                    node.setValue(prefix+fieldname,"");
                } else {
                    node.setValue(prefix+fieldname,tmp);
                }
            } else if (fieldtype.equalsIgnoreCase("VARSTRING_EX")) {
                String tmp=rs.getString(i);
                if (tmp==null) {
                    node.setValue(prefix+fieldname,"");
                } else {
                    node.setValue(prefix+fieldname,tmp);
                }
            } else if (fieldtype.equalsIgnoreCase("lvarchar")) {
                String tmp=rs.getString(i);
                if (tmp==null) {
                    node.setValue(prefix+fieldname,"");
                } else {
                    node.setValue(prefix+fieldname,tmp);
                }
            } else if (fieldtype.equalsIgnoreCase("LONG") || fieldtype.equalsIgnoreCase("int4")) {
                node.setValue(prefix+fieldname,rs.getInt(i));
            } else if (fieldtype.equalsIgnoreCase("TEXT")) {
                //node.setValue(prefix+fieldname,getDBText(rs,i));
                node.setValue(prefix+fieldname,"$SHORTED");
            } else if (fieldtype.equalsIgnoreCase("BLOB")) {
                //node.setValue(prefix+fieldname,getDBByte(rs,i));
                node.setValue(prefix+fieldname,"$SHORTED");
            } else {
                log.info("mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
            }
        } catch(SQLException e) {
            log.error("MPostgres42Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
            log.error(Logging.stackTrace(e));
        }
        return(node);
    }

    /**
     * Not to be confused with {@link #parseFieldPart(String,int,String)
     * parseFieldPart(String,int,String)}.
     *
     * @deprecated This code no longer serves a purpose, and is called from
     *             nowhere.
     * @deprecated-now RvM: Can be removed safely.
     */
    public String parseFieldPart(String fieldname,String dbtype,String part) {
        String result="";
        boolean like=false;
        char operatorChar = part.charAt(0);
        //log.debug("char="+operatorChar);
        String value=part.substring(1);
        int pos=value.indexOf("*");
        if (pos!=-1) {
            value=value.substring(pos+1,value.length()-1);
            like=true;
        }
        // log.debug("fieldname="+fieldname+" type="+dbtype);
        if (dbtype.equals("var") || dbtype.equals("varchar")) {
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
        } else if (dbtype.equals("VARSTRING_EX")) {
            switch (operatorChar) {
                case '=':
                case 'E':
                    // EQUAL
                    result+="etx_contains("+fieldname+",Row('"+value+"','SEARCH_TYPE=PROX_SEARCH(5)'))";
                    break;
            }

        } else if (dbtype.equals("LONG") || dbtype.equals("int")) {
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
        return(result);
    }

    /**
     * get text from blob
     */
    public String getShortedText(String tableName,String fieldname,int number) {
        try {
            String result=null;
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
            try {
                if (rs.next()) {
                    result=getDBText(rs,1);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
            return(result);
        } catch (Exception e) {
            log.debug("MMObjectBuilder : trying to load text");
            log.error(Logging.stackTrace(e));
        }
        return(null);
    }


    /**
     * get byte of a database blob
     */
    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        try {
            byte[] result=null;
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
            try {
                if (rs.next()) {
                    result=getDBByte(rs,1);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
            return(result);
        } catch (Exception e) {
            log.debug("MMObjectBuilder : trying to load bytes");
            log.error(Logging.stackTrace(e));
        }
        return(null);
    }


    /**
     * get byte of a database blob
     */
        /* temp removed, daniel
        public byte[] getDBByte(ResultSet rs,int idx) {
                byte[] bytes=null;
                try {
                        int oid=rs.getInt(idx);
                        LargeObject obj=lobj.open(oid,LargeObjectManager.READ);
                        int size=obj.size();
                        bytes=obj.read(size);
                } catch (Exception e) {
                        log.debug("MMObjectBuilder -> MMPostgres byte  exception "+e);
                        log.error(Logging.stackTrace(e));
                }
                return(bytes);
        }
         */

    /**
     * get text of a database blob
     */
    public String getDBText(ResultSet rs,int idx) {
        String str=null;
        InputStream inp;
        DataInputStream input;
        byte[] isochars;
        int siz;

        try {
            isochars=rs.getBytes(idx);
            str=new String(isochars,"ISO-8859-1");
        } catch (Exception e) {
            log.debug("MMObjectBuilder -> MMPostgres text  exception "+e);
            log.error(Logging.stackTrace(e));
            return("");
        }
        return(str);
    }

    /**
     * Insert: This method inserts a new object, normally not used (only subtables are used)
     * Only fields with DBState value = DBSTATE_PERSISTENT or DBSTATE_SYSTEM are inserted.
     * Fields with DBstate values = DBSTATE_VIRTUAL or any other value are skipped.
     * @param bul The MMObjectBuilder.
     * @param owner The nodes' owner.
     * @param node The current node that's to be inserted.
     * @return The DBKey number for this node, or -1 if an error occurs.
     */
        /* temp removed, daniel
        public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
                int number=node.getIntValue("number");
                // did the user supply a number allready, ifnot try to obtain one
                if (number==-1) number=getDBKey();

                // did it fail ? ifso exit
                if (number==-1) return(-1);

                if (number==0) return(insertRootNode(bul));
                String fieldAmounts="";
                // String tmp="";
                MultiConnection con=null;
                PreparedStatement stmt=null;
                try {
                        con=bul.mmb.getConnection();
                } catch(Exception t) {
                        log.error(Logging.stackTrace(t));
                }
                if(bul.sortedDBLayout!=null) {
                        // Create a String that represents the amount of DB fields to be used in the insert.
                        // First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
                        fieldAmounts="?";

                        // Append the DB elements to the fieldAmounts String.
                        for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
                                String key = (String)e.nextElement();
                                int DBState = node.getDBState(key);
                                if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
                                  || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
                                        // log.debug("Insert: DBState = "+DBState+", adding key: "+key);
                                        fieldAmounts+=",?";
                                } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                                        // log.debug("Insert: DBState = "+DBState+", skipping key: "+key);
                                } else {
                                        if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                                                fieldAmounts+=",?";
                                        } else {
                                                log.error("Insert: Error DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
                                        }
                                }
                        }

                        try {
                                stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+bul.tableName+" values("+fieldAmounts+")");
                        } catch(Exception t2) {
                                log.error(Logging.stackTrace(t2));
                        }
                        try {
                                stmt.setEscapeProcessing(false);
                                // First add the 'number' field to the statement since it's not in the sortedDBLayout vector.
                                stmt.setInt(1,number);

                                // Prepare the statement for the DB elements to the fieldAmounts String.
                                // log.debug("Insert: Preparing statement using fieldamount String: "+fieldAmounts);
                                int j=2;
                                if(bul.sortedDBLayout!=null) {
                                        for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
                                                String key = (String)e.nextElement();
                                                int DBState = node.getDBState(key);
                                                if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
                                                  || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
                                                        // log.debug("Insert: DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+j);
                                                        setValuePreparedStatement( stmt, node, key, j );
                                                        j++;
                                                } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                                                        // log.debug("Insert: DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
                                                } else {
                                                        if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                                                                setValuePreparedStatement( stmt, node, key, j );
                                                                j++;
                                                        } else {
                                                                log.error("Insert: Error DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
                                                        }
                                                }
                                        }
                                }
                                stmt.executeUpdate();
                                stmt.close();
                                con.close();
                        } catch (SQLException e) {
                                log.error("Error on : "+number+" "+owner+" fake");
                                try {
                                        stmt.close();
                                        con.close();
                                } catch(Exception t2) {}
                                log.error(Logging.stackTrace(e));
                                return(-1);
                        }
                }

                if (node.parent!=null && (node.parent instanceof InsRel) && !bul.tableName.equals("insrel")) {
                        try {
                                con=mmb.getConnection();
                                stmt=con.prepareStatement("insert into "+mmb.baseName+"_insrel values(?,?,?,?,?,?)");
                                stmt.setInt(1,number);
                                stmt.setInt(2,node.getIntValue("otype"));
                                stmt.setString(3,node.getStringValue("owner"));
                                stmt.setInt(4,node.getIntValue("snumber"));
                                stmt.setInt(5,node.getIntValue("dnumber"));
                                stmt.setInt(6,node.getIntValue("rnumber"));
                                stmt.executeUpdate();
                                stmt.close();
                                con.close();
                        } catch (SQLException e) {
                                log.error(Logging.stackTrace(e));
                                log.error("Error on : "+number+" "+owner+" fake");
                                return(-1);
                        }
                }


                try {
                        con=mmb.getConnection();
                        stmt=con.prepareStatement("insert into "+mmb.baseName+"_object values(?,?,?)");
                        stmt.setInt(1,number);
                        stmt.setInt(2,node.getIntValue("otype"));
                        stmt.setString(3,node.getStringValue("owner"));
                        stmt.executeUpdate();
                        stmt.close();
                        con.close();
                } catch (SQLException e) {
                        log.error(Logging.stackTrace(e));
                        log.error("Error on : "+number+" "+owner+" fake");
                        return(-1);
                }


                //bul.signalNewObject(bul.tableName,number);
                if (bul.broadcastChanges) {
                        if (bul instanceof InsRel) {
                                bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
                                // figure out tables to send the changed relations
                                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                                n1.delRelationsCache();
                                n2.delRelationsCache();
                                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
                                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
                        } else {
                                mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
                        }
                }
                node.setValue("number",number);
                //log.debug("INSERTED="+node);
                return(number);
        }
         */


    public int insertRootNode(MMObjectBuilder bul) {
        try {
            log.trace("P4");
            MultiConnection con=bul.mmb.getConnection();
            PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_typedef values(?,?,?,?,?)");
            stmt.setEscapeProcessing(false);
            stmt.setInt(1,0);
            stmt.setInt(2,0);
            stmt.setString(3,"system");
            stmt.setString(4,"typedef");
            stmt.setString(5,"Type definition builder");
            stmt.executeUpdate();
            stmt.close();
            con.close();
            log.trace("P5");
        } catch (SQLException e) {
            log.error("Error on root node");
            log.error(Logging.stackTrace(e));
            return(-1);
        }

        try {
            MultiConnection con=mmb.getConnection();
            PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_object values(?,?,?)");
            stmt.setInt(1,0);
            stmt.setInt(2,0);
            stmt.setString(3,"system");
            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error(Logging.stackTrace(e));
            log.error("Error on root node");
            return(-1);
        }
        return(0);
    }


    /**
     * set text array in database
     */
    public void setDBText(int i, PreparedStatement stmt,String body) {
        byte[] isochars=null;
        try {
            isochars=body.getBytes("ISO-8859-1");
        } catch (Exception e) {
            log.error("String contains odd chars");
            log.error(body);
            log.error(Logging.stackTrace(e));
        }
        try {
            stmt.setBytes(i,isochars);
        } catch (Exception e) {
            log.error("Can't set ascii stream");
            log.error(Logging.stackTrace(e));
        }
    }


    /**
     * set byte array in database
     */
        /* temp removed, daniel
        public void setDBByte(int i, PreparedStatement stmt,byte[] bytes) {
                try {
                        int oid=lobj.create(LargeObjectManager.READ|LargeObjectManager.WRITE);
                        LargeObject obj=lobj.open(oid,LargeObjectManager.WRITE);
                        obj.write(bytes);
                        stmt.setInt(i,oid);
                } catch (Exception e) {
                        log.error("MMObjectBuilder : Can't set byte stream");
                        log.error(Logging.stackTrace(e));
                }
        }
         */

    /**
     * commit this node to the database
     */
        /* temp removed daniel
        public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
                //  precommit call, needed to convert or add things before a save
                bul.preCommit(node);
                // commit the object
                String values="";
                String key;
                // create the prepared statement
                for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
                                key=(String)e.nextElement();
                                // a extra check should be added to filter temp values
                                // like properties

                                // check if its the first time for the ',';
                                if (values.equals("")) {
                                        values+=" "+key+"=?";
                                } else {
                                        values+=", "+key+"=?";
                                }
                }
                if (values.length()>0) {
                        values="update "+mmb.baseName+"_"+bul.tableName+" set"+values+" WHERE number="+node.getValue("number");
                        try {
                                MultiConnection con=mmb.getConnection();
                                PreparedStatement stmt=con.prepareStatement(values);
                                String type;int i=1;
                                for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
                                                key=(String)e.nextElement();
                                                type=node.getDBType(key);
                                                if (type.equals("int") || type.equals("integer")) {
                                                        stmt.setInt(i,node.getIntValue(key));
                                                } else if (type.equals("text")) {
                                                        setDBText(i,stmt,node.getStringValue(key));
                                                } else if (type.equals("byte")) {
                                                        setDBByte(i,stmt,node.getByteValue(key));
                                                } else {
                                                        stmt.setString(i,node.getStringValue(key));
                                                }
                                                i++;
                                }
                                stmt.executeUpdate();
                                stmt.close();
                                con.close();
                        } catch (SQLException e) {
                                log.error(Logging.stackTrace(e));
                                return(false);
                        }
                }

                node.clearChanged();
                if (bul.broadcastChanges) {
                        if (bul instanceof InsRel) {
                                bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
                                // figure out tables to send the changed relations
                                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getTableName(),"r");
                                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getTableName(),"r");
                        } else {
                                mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
                        }
                }
                return(true);
        }
         */


    /**
     * removeNode
     */
    public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
        int number=node.getIntValue("number");
        if (log.isDebugEnabled()) {
            log.debug("delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
            log.debug("SAVECOPY "+node.toString());
        }
        Vector rels=bul.getRelations_main(number);
        if (rels!=null && rels.size()>0) {
            log.warn("still relations attachched : delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
        } else {
            if (number!=-1) {
                try {
                    MultiConnection con=mmb.getConnection();
                    Statement stmt=con.createStatement();
                    stmt.executeUpdate("delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
                    stmt.close();
                    con.close();
                } catch (SQLException e) {
                    log.error(Logging.stackTrace(e));
                }
                if (node.parent!=null && (node.parent instanceof InsRel) && !bul.tableName.equals("insrel")) {
                    try {
                        MultiConnection con=mmb.getConnection();
                        Statement stmt=con.createStatement();
                        stmt.executeUpdate("delete from "+mmb.baseName+"_insrel where number="+number);
                        stmt.close();
                        con.close();
                    } catch (SQLException e) {
                        log.error(Logging.stackTrace(e));
                    }
                }

                try {
                    MultiConnection con=mmb.getConnection();
                    Statement stmt=con.createStatement();
                    stmt.executeUpdate("delete from "+mmb.baseName+"_object where number="+number);
                    stmt.close();
                    con.close();
                } catch (SQLException e) {
                    log.error(Logging.stackTrace(e));
                }
            }
        }
        if (bul.broadcastChanges) {
            mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"d");
            if (bul instanceof InsRel) {
                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getName(),"r");
            }
        }

    }

    public synchronized int getDBKey() {
        // get a new key
        int number=-1;
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select max(number) from "+mmb.getBaseName()+"_object");
            try {
                if (rs.next()) {
                    number=rs.getInt(1);
                    number++;
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("Error getting a new key number");
            return(1);
        }
        return(number);
    }


    /**
     * return the number of relation types in this mmbase and table
     */
    public boolean created(String tableName) {
        if (size(tableName)==-1) {
            // log.warn("TABLE "+tableName+" NOT FOUND");
            return(false);
        } else {
            // log.warn("TABLE "+tableName+" FOUND");
            return(true);
        }
    }


    /**
     * return the number of relation types in this mmbase and table
     */
    public int size(String tableName) {
        MultiConnection con=null;
        Statement stmt=null;
        try {
            int i=-1;
            con=mmb.getConnection();
            stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT count(*) FROM "+tableName+";");
            try {
                while(rs.next()) {
                    i=rs.getInt(1);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
            return i;
        } catch (Exception e) {
            try {
                stmt.close();
                con.close();
            } catch(Exception t) {}
            return(-1);
        }
    }



    /**
     * set prepared statement field i with value of key from node
     */
        /* temp removed daniel.
        private void setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
                throws SQLException
        {
                String type = node.getDBType(key);
                if(type==null)
                        stmt.setString(i, "");
                else if (type.equals("int") || type.equals("integer"))
                        stmt.setInt(i, node.getIntValue(key));
                else if (type.equals("text") || type.equals("clob"))
                {
                        String tmp=node.getStringValue(key);
                        if (tmp!=null)
                                setDBText(i, stmt,tmp);
                        else
                                setDBText(i, stmt,"");
                }
                else if (type.equals("byte"))
                                setDBByte(i, stmt, node.getByteValue(key));
                else
                {
                        String tmp=node.getStringValue(key);
                        if (tmp!=null)
                                stmt.setString(i, tmp);
                        else
                                stmt.setString(i, "");
                }
        }
         */


    /**
     * insert a new object, normally not used (only subtables are used)
     */
    public int fielddefInsert(String baseName, int oType,String owner,MMObjectNode node) {
        int dbtable=node.getIntValue("dbtable");
        String dbname=node.getStringValue("dbname");
        String dbtype=node.getStringValue("dbtype");
        String guiname=node.getStringValue("guiname");
        String guitype=node.getStringValue("guitype");
        int guipos=node.getIntValue("guipos");
        int guilist=node.getIntValue("guilist");
        int guisearch=node.getIntValue("guisearch");
        int dbstate=node.getIntValue("dbstate");

        int number=getDBKey();
        try {
            MultiConnection con=mmb.getConnection();
            PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_fielddef values(?,?,?,?,?,?,?,?,?,?,?,?)");
            stmt.setInt(1,number);
            stmt.setInt(2,oType);
            stmt.setString(3,owner);
            stmt.setInt(4,dbtable);
            stmt.setString(5,dbname);
            stmt.setString(6,dbtype);
            stmt.setString(7,guiname);
            stmt.setString(8,guitype);
            stmt.setInt(9,guipos);
            stmt.setInt(10,guilist);
            stmt.setInt(11,guisearch);
            stmt.setInt(12,dbstate);
            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error(Logging.stackTrace(e));
            log.error("Error on : "+number+" "+owner+" fake");
            return(-1);
        }

        try {
            MultiConnection con=mmb.getConnection();
            PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_object values(?,?,?)");
            stmt.setInt(1,number);
            stmt.setInt(2,oType);
            stmt.setString(3,owner);
            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error(Logging.stackTrace(e));
            log.error("Error on : "+number+" "+owner+" fake");
            return(-1);
        }
        return(number);
    }
}
