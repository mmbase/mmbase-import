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

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.module.corebuilders.InsRel;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* MMOORel2Node implements the MMJdbc2NodeInterface for
* OO-rel types of databbases  this is the class used to abstact the query's
* needed for mmbase for each database.
*
* @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
*             StorageManager implementation.
* @author Daniel Ockeloen
* @version $Id: MMOORel2Node.java,v 1.11 2004-01-27 12:04:48 pierre Exp $
* @version $Id: MMOORel2Node.java,v 1.11 2004-01-27 12:04:48 pierre Exp $
*/
public class MMOORel2Node extends MMSQL92Node implements MMJdbc2NodeInterface {

    private static Logger log = Logging.getLoggerInstance(MMOORel2Node.class.getName());

        MMBase mmb;
        static Vector nameCache=null;
        private int currentdbkey=-1;
        private int currentdbkeyhigh=-1;

        public MMOORel2Node() {
        }

        public void init(MMBase mmb) {
                this.mmb=mmb;
        }

        public boolean create(MMObjectBuilder builder,String tableName) {
                // Note that builder is null when tableName='object'

                        // get us a propertie reader
                        ExtendedProperties Reader=new ExtendedProperties();

                        // load the properties file of this server

                        String root = MMBaseContext.getConfigPath();

                        if (log.isDebugEnabled()) {
                log.debug("create(): reading defines from '" + root + "/defines/" + tableName + ".def'");
            }

                        Hashtable prop = Reader.readProperties(root+"/defines/"+tableName+".def");

                        String createtype=(String)prop.get("CREATETYPE_INFORMIX");
                        String createtable=(String)prop.get("CREATETABLE_INFORMIX");

                        if (createtype!=null && !createtype.equals("")) {
                        createtype = Strip.DoubleQuote(createtype,Strip.BOTH);
                                try {
                                        MultiConnection con=mmb.getConnection();
                                        Statement stmt=con.createStatement();
                                        if (tableName.equals("object")) {
                                                stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype);
                                        } else if (builder instanceof InsRel && !tableName.equals("insrel")) {
                                                stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+" under "+mmb.baseName+"_insrel_t");
                                        } else {
                                                stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+" under "+mmb.baseName+"_object_t");
                                        }
                                        log.info("Created type "+tableName);
                                        stmt.close();
                                        con.close();
                                } catch (SQLException e) {
                                        log.error("can't create type "+tableName);
                                        log.error("create row type "+mmb.baseName+"_"+tableName+"_t "+createtype+" under "+mmb.baseName+"_object_t");
                                        log.error(Logging.stackTrace(e));
                                }
                        }
                        else {
                                log.error("create(): Can't create table no CREATETABLE_ defined");
                        }

                        if (createtable!=null && !createtable.equals("")) {
                        createtable = Strip.DoubleQuote(createtable,Strip.BOTH);
                                try {
                                        MultiConnection con=mmb.getConnection();
                                        Statement stmt=con.createStatement();
                                        if (tableName.equals("object")) {
                                                stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t "+createtable);
                                        } else if (builder instanceof InsRel && !tableName.equals("insrel")) {
                                                stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t "+createtable+" under "+mmb.baseName+"_insrel");
                                        } else {
                                                stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t "+createtable+" under "+mmb.baseName+"_object");
                                        }
                                        log.info("create table "+mmb.baseName+"_"+tableName+" "+createtable+";");
                                        stmt.close();
                                        con.close();
                                } catch (SQLException e) {
                                        log.error("can't create table "+tableName);
                                        log.error(Logging.stackTrace(e));
                                }
                        } else {
                                log.error("create(): Can't create table no CREATETABLE_ defined");
                        }
                return(true);
        }

        public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i) {
                return(decodeDBnodeField(node,fieldtype,fieldname,rs,i,""));
        }

        public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i,String prefix) {
                        try {
                                if (fieldtype.equals("varchar")) {
                                        String tmp=rs.getString(i);
                                        if (tmp==null) {
                                                node.setValue(prefix+fieldname,"");
                                        } else {
                                                node.setValue(prefix+fieldname,tmp);
                                        }
                                } else if (fieldtype.equals("varchar_ex")) {
                                        String tmp=rs.getString(i);
                                        if (tmp==null) {
                                                node.setValue(prefix+fieldname,"");
                                        } else {
                                                node.setValue(prefix+fieldname,tmp);
                                        }
                                } else if (fieldtype.equals("char")) {
                                        String tmp=rs.getString(i);
                                        if (tmp==null) {
                                                node.setValue(prefix+fieldname,"");
                                        } else {
                                                node.setValue(prefix+fieldname,tmp.trim());
                                        }
                                } else if (fieldtype.equals("lvarchar")) {
                                        String tmp=rs.getString(i);
                                        if (tmp==null) {
                                                node.setValue(prefix+fieldname,"");
                                        } else {
                                                node.setValue(prefix+fieldname,tmp.trim());
                                        }
                                } else if (fieldtype.equals("int")) {
                                        node.setValue(prefix+fieldname,rs.getInt(i));
                                } else if (fieldtype.equals("text") || fieldtype.equals("clob")) {
                                        //node.setValue(prefix+fieldname,getDBText(rs,i));
                                        node.setValue(prefix+fieldname,"$SHORTED");
                                } else if (fieldtype.equals("byte")) {
                                        //node.setValue(prefix+fieldname,getDBByte(rs,i));
                                        node.setValue(prefix+fieldname,"$SHORTED");
                                } else {
                                        //log.debug("MMOORel2Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
                                }
                        } catch(SQLException e) {
                                log.error("OORel2Node mmObject->"+fieldname+"="+fieldtype+" node="+node.getIntValue("number"));
                                log.error(Logging.stackTrace(e));
                        }
                        return(node);
        }


        public String getDBText(ResultSet rs,int idx) {
                String str=null;
                InputStream inp;
                DataInputStream input;
                byte[] isochars;
                int siz;

                if (0==1) return("");
                try {
                        inp=rs.getAsciiStream(idx);
                        if (inp==null) {
                                log.warn("MMOORel2Node DBtext no ascii "+inp);
                                 return("");
                        }
                        if (rs.wasNull()) {
                                log.warn("MMOORel2Node DBtext wasNull "+inp);
                                return("");
                        }
                        siz=inp.available(); // DIRTY
            //	log.debug("MMOORel2Node DBtext SIZE="+siz);
                        if (siz==0 || siz==-1) return("");
                        input=new DataInputStream(inp);
                        isochars=new byte[siz];
                        input.readFully(isochars);
                        str=new String(isochars,"ISO-8859-1");
                        input.close(); // this also closes the underlying stream
                } catch (Exception e) {
                        log.error("MMOORel2Node text  exception "+e);
                        log.error(Logging.stackTrace(e));
                        return("");
                }
                return(str);
                //return("temp test");
        }


        public byte[] getDBByte(ResultSet rs,int idx) {
                String str=null;
                InputStream inp;
                DataInputStream input;
                byte[] bytes=null;
                int siz;
                try {
                        inp=rs.getBinaryStream(idx);
                        siz=inp.available(); // DIRTY
                        input=new DataInputStream(inp);
                        bytes=new byte[siz];
                        input.readFully(bytes);
                        input.close(); // this also closes the underlying stream
                } catch (Exception e) {
                        log.error("MMOORel2Node byte  exception "+e);
                        log.error(Logging.stackTrace(e));
                }
                return(bytes);
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
                if (dbtype.equals("varchar")) {
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
                } else if (dbtype.equals("varchar_ex")) {
                        switch (operatorChar) {
                        case '=':
                        case 'E':
                                // EQUAL
                                result+="etx_contains("+fieldname+",Row('"+value+"','SEARCH_TYPE=PROX_SEARCH(5)'))";
                                if (log.isDebugEnabled()) {
                    log.debug("etx_contains(" + fieldname + ",Row('" + value + "','SEARCH_TYPE=PROX_SEARCH(5)'))");
                }
                                break;
                        }

                } else if (dbtype.equals("int")) {
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
        * get byte of a database blob
        */
        public byte[] getShortedByte(String tableName,String fieldname,int number) {
                try {
                        byte[] result=null;
                        MultiConnection con=mmb.getConnection();
                        Statement stmt=con.createStatement();
                        if (log.isDebugEnabled()) {
                log.debug("SELECT " + fieldname + " FROM " + mmb.baseName + "_" + tableName + " where number=" + number);
            }
                        ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where number="+number);
                        if (rs.next()) {
                                result=getDBByte(rs,1);
                        }
                        stmt.close();
                        con.close();
                        return(result);
                } catch (Exception e) {
                        log.error("getShortedByte(): trying to load bytes");
                        log.error(Logging.stackTrace(e));
                }

                return(null);
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
                        if (rs.next()) {
                                result=getDBText(rs,1);
                        }
                        stmt.close();
                        con.close();
                        return(result);
                } catch (Exception e) {
                        log.error("getShortedText(): trying to load text");
                        log.error(Logging.stackTrace(e));
                }
                // return "" instead of null, not sure if this is oke
                return("");
        }


        public int insertRootNode(MMObjectBuilder bul) {
                try {
                        MultiConnection con=bul.mmb.getConnection();
                        PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_typedef values(?,?,?,?,?)");
                        stmt.setInt(1,0);
                        stmt.setInt(2,0);
                        stmt.setString(3,"system");
                        stmt.setString(4,"typedef");
                        stmt.setString(5,"Type definition builder");
                        stmt.executeUpdate();
                        stmt.close();
                        con.close();
                } catch (SQLException e) {
                        log.error("Error on root node");
                        log.error(Logging.stackTrace(e));
                        return(-1);
                }
                return(0);
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
        /* temp removed daniel.
        public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
                int number=node.getIntValue("number");
                if (number==-1) number=getDBKey();
                if (number==-1) return(-1);
                if (number==0) return(insertRootNode(bul));

                try {
                        // Create a String that represents the amount of DB fields to be used in the insert.
                        // First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
                        String fieldAmounts="?";

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
                                                log.debug("Insert: Error DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
                                        }
                                }
                        }

                        // Create the DB statement with DBState values in mind.
                        MultiConnection con=bul.mmb.getConnection();
                        PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+bul.tableName+" values("+fieldAmounts+")");
                        // First add the 'number' field to the statement since it's not in the sortedDBLayout vector.
                        stmt.setInt(1,number);

                        // Prepare the statement for the DB elements to the fieldAmounts String.
                        // log.debug("Insert: Preparing statement using fieldamount String: "+fieldAmounts);
                        int j=2;
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
                                                log.debug("Insert: Error DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
                                        }
                                }
                        }
                        stmt.executeUpdate();
                        stmt.close();
                        con.close();
                } catch (SQLException e) {
                        log.debug("Error on : "+number+" "+owner+" fake");
                        log.error(Logging.stackTrace(e));
                        return(-1);
                }


                bul.signalNewObject(bul.tableName,number);

                node.setValue("number",number);
                if (log.debug) log.debug("INSERTED="+node);
                return(number);
        }
        */


        /**
        * set text array in database
        */
        public void setDBText(int i, PreparedStatement stmt,String body) {
                byte[] isochars=null;
                try {
                        isochars=body.getBytes("ISO-8859-1");
                } catch (Exception e) {
                        log.error("setDBText(): String contains odd chars");
            log.error(body);
                        log.error(Logging.stackTrace(e));
                }
                try {
                        ByteArrayInputStream stream=new ByteArrayInputStream(isochars);
                        stmt.setAsciiStream(i,stream,isochars.length);
                        stream.close();
                } catch (Exception e) {
                        log.error("setDBText(): Can't set ascii stream");
                        log.error(Logging.stackTrace(e));
                }
        }

        /**
        * set byte array in database
        */
        public void setDBByte(int i, PreparedStatement stmt,byte[] bytes) {
                try {
                        ByteArrayInputStream stream=new ByteArrayInputStream(bytes);
                        stmt.setBinaryStream(i,stream,bytes.length);
                        stream.close();
                } catch (Exception e) {
                        log.error("getDBByte(): Can't set byte stream");
                        log.error(Logging.stackTrace(e));
                }
        }

        /**
        * set prepared statement field i with value of key from node
        */
        /* temp removed daniel
        private void setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
                throws SQLException
        {
                String type = node.getDBType(key);
                if (type.equals("int") || type.equals("integer")) {
                        stmt.setInt(i, node.getIntValue(key));
                } else if (type.equals("text") || type.equals("clob")) {
                        String tmp=node.getStringValue(key);
                        if (tmp!=null) {
                                setDBText(i, stmt,tmp);
                        } else {
                                setDBText(i, stmt,"");
                        }
                } else if (type.equals("byte")) {
                                setDBByte(i, stmt, node.getByteValue(key));
                } else {
                        String tmp=node.getStringValue(key);
                        if (tmp!=null) {
                                stmt.setString(i, tmp);
                        } else {
                                stmt.setString(i, "");
                        }
                }
        }
        */


        /**
        * commit this node to the database
        */
        /* temp removed daniel.
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
                                                setValuePreparedStatement( stmt, node, key, i );
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
                                int num = node.getIntValue("number");
                                if (log.debug) log.debug("commit(): changed(insrel,"+bul.tableName+","+num+")");
                                mmb.mmc.changedNode(num,bul.tableName,"c");

                                // figure out tables to send the changed relations
                                // -----------------------------------------------
                                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getName(),"r");
                                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getName(),"r");
                        } else {
                                int num = node.getIntValue("number");
                                if (log.debug) log.debug("commit(): changed("+bul.tableName+","+num+")");
                                if (mmb!=null && mmb.mmc!=null) {
                                        mmb.mmc.changedNode(num,bul.tableName,"c");
                                } else {
                                        log.error("commit(): can't send change("+bul.tableName+","+num+"), mmb or mmb.mmc is null");
                                }
                        }
                }
                return(true);
        }
        */


        /**
        * removeNode
        */
        public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
                java.util.Date d=new java.util.Date();
                int number=node.getIntValue("number");
                // temp removed (daniel) despre. if (debug) log.debug("removeNode(): delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number+" at "+d.toGMTString());
                if (log.isDebugEnabled()) {
            log.debug("removeNode(): SAVECOPY " + node.toString());
        }
                Vector rels=bul.getRelations_main(number);
                if (rels!=null && rels.size()>0) {
                        log.warn("removeNode("+bul.tableName+","+number+"): still relations attachched : delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
                } else {
                        if (number!=-1) {
                                try {
                                        MultiConnection con=mmb.getConnection();
                                        Statement stmt=con.createStatement();
                                        stmt.executeUpdate("delete from "+mmb.baseName+"_"+bul.tableName+" where number="+number);
                                        stmt.close();
                                        con.close();
                                } catch (SQLException e) {
                                        log.error("removeNode("+bul.tableName+","+number+"): " + Logging.stackTrace(e));
                                }
                        }
                        else {
                                log.error("removeNode("+bul.tableName+","+number+"): number not valid(-1)!");
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
                        else {
                                log.warn("removeNode("+bul.tableName+","+number+"): want to remove it, but not an insrel (not implemented).");
            }
                }

        }

        public synchronized int getDBKey() {
                // get a new key

                if (currentdbkey!=-1) {
                        currentdbkey++;
                        if (currentdbkey<=currentdbkeyhigh) {
                                if (log.isDebugEnabled()) {
                    log.debug("GETDBKEY="+currentdbkey);
                }
                                return(currentdbkey);
                        }
                }
            int number=-1; // not 100% sure if function returns 1 first time
                while (number==-1) {
                        try {
                                MultiConnection con=mmb.getConnection();
                                Statement stmt=con.createStatement();
                                ResultSet rs=stmt.executeQuery("execute function fetchrelkey(10)");
                                while (rs.next()) {
                                        number=rs.getInt(1);
                                }
                                stmt.close();
                                con.close();
                        } catch (SQLException e) {
                                log.error("getDBKey(): while getting a new key number");
                                log.error(Logging.stackTrace(e));
                                try {
                                        Thread.sleep(2000);
                                } catch (InterruptedException re){
                                        log.info("getDBKey(): Waiting 2 seconds to allow database to unlock fetchrelkey()");
                                }
                                log.info("getDBKey(): got key("+currentdbkey+")");
                                return(-1);
                        }
                }
                currentdbkey=number; // zeg 10
                currentdbkeyhigh=(number+9); // zeg 19 dus indien hoger dan nieuw
                if (log.isDebugEnabled()) {
            log.debug("getDBKey(): got key("+currentdbkey+")");
        }
                return(number);
        }

        public boolean created(String tableName) {
                if (nameCache==null) {
                        nameCache=getAllNames();
                }
                if (nameCache.contains(tableName)) {
                        return(true);
                } else {
                        if (tableName.length()>0) {
                                if (log.isDebugEnabled()) { // michiel: hmm, strange these are debug mesages _and_ errors?
                    log.debug("created("+tableName+"): ERROR: Not Found '"+tableName+"'");
                }
                                return(false);
                        } else {
                                if (log.isDebugEnabled()) {
                   log. debug("created("+tableName+"): ERROR: Not Found '"+tableName+"'");
                }
                                return(true);
                        }
                }
        }


        public synchronized Vector getAllNames() {
                Vector results=new Vector();
                try {
                        MultiConnection con=mmb.getConnection();
                        Statement stmt=con.createStatement();
                        ResultSet rs=stmt.executeQuery("SELECT tabname FROM systables where tabid>99;");
                        String s;
                        while (rs.next()) {
                                s = rs.getString(1);
                                if (s!=null) s = s.trim();
                                results.addElement(s);
                        }
                        stmt.close();
                        con.close();
                        return(results);
                } catch (Exception e) {
                        //log.error(Logging.stackTrace(e));
                        return(results);
                }
        }


        /**
        * insert a new object, normally not used (only subtables are used)
        */
        public int fielddefInsert(String baseName, int oType, String owner,MMObjectNode node) {
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
                        log.error("Error on : "+number+" "+owner+" fake");
            log.error(Logging.stackTrace(e));
                        return(-1);
                }

                return(number);
        }
}
