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

import org.mmbase.storage.database.UnsupportedDatabaseOperationException;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Marcel Maatkamp
 * @version $Id: MMMckoiNode.java,v 1.13 2004-01-27 12:04:48 pierre Exp $
 */
public class MMMckoiNode extends BaseJdbc2Node implements MMJdbc2NodeInterface {

    /**
    * Logging instance
    */
    private static Logger log = Logging.getLoggerInstance(MMMckoiNode.class.getName());

    //does the database support keys?
    private boolean keySupported=false;
    public String name="mckoi";
    XMLDatabaseReader parser;
    Hashtable typeMapping = new Hashtable();
    Hashtable disallowed2allowed;
    Hashtable allowed2disallowed;
    private String numberString;
    private String otypeString;
    private String ownerString;
    private boolean bdm=false;
    private String datapath="/tmp/data/";

    MMBase mmb;

    public MMMckoiNode() {
    }

    public void init(MMBase mmb,XMLDatabaseReader parser) {
        this.mmb=mmb;
        this.parser=parser;

        datapath=parser.getBlobDataDir();
        if (datapath!=null && !datapath.equals("")) {
            bdm=true;
        }

        typeMapping=parser.getTypeMapping();
        disallowed2allowed=parser.getDisallowedFields();
        allowed2disallowed=getReverseHash(disallowed2allowed);
        // map the default types
        mapDefaultFields(disallowed2allowed);

        // Instantiate and initialize sql handler.
        super.init(disallowed2allowed, parser);

        // Check if the numbertable exists, if not one will be created.
        checkNumberTable();
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

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i) {
        return(decodeDBnodeField(node,fieldname,rs,i,""));
    }

    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {

        try {

        // is this fieldname disallowed ? ifso map it back
        if (allowed2disallowed.containsKey(fieldname)) {
            fieldname=(String)allowed2disallowed.get(fieldname);
        }

        // hack for bug in jdbc driver in mckoi's jdbc driver
        if(fieldname.lastIndexOf(".")!=-1)
            fieldname = fieldname.substring(fieldname.lastIndexOf(".")+1);

        // int type=node.getDBType(prefix+fieldname);
        int type=node.getDBType(prefix+fieldname);
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
                node.setValue(prefix+fieldname,(Integer)rs.getObject(i));
                break;
            case FieldDefs.TYPE_LONG:
                node.setValue(prefix+fieldname,(Long)rs.getObject(i));
                break;
            case FieldDefs.TYPE_FLOAT:
                // who does this now work ????
                //node.setValue(prefix+fieldname,((Float)rs.getObject(i)));
                node.setValue(prefix+fieldname,new Float(rs.getFloat(i)));
                break;
            case FieldDefs.TYPE_DOUBLE:
                node.setValue(prefix+fieldname,(Double)rs.getObject(i));
                break;
            case FieldDefs.TYPE_BYTE:
                node.setValue(prefix+fieldname,"$SHORTED");
                break;
            }
            return (node);
        } catch(SQLException e) {
            log.error("mmObject->"+fieldname+" node="+node.getIntValue("number"));
            log.error(Logging.stackTrace(e));
        }
        return(node);
    }


    public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul) {
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
        return(result);
    }


    public String parseFieldPart(String fieldname,int dbtype,String part) {
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
        } else if (dbtype==FieldDefs.TYPE_LONG || dbtype==FieldDefs.TYPE_INTEGER || dbtype==FieldDefs.TYPE_NODE) {
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
            ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where "+getNumberString()+"="+number+";");
            if (rs.next()) {
                result=getDBText(rs,1);
            }
            stmt.close();
            con.close();
            return(result);
        } catch (Exception e) {
            log.error("MMObjectBuilder : trying to load text");
            log.error(Logging.stackTrace(e));
        }
        return(null);
    }


    /**
    * get byte of a database blob
    */
    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        if (!bdm) {
            try {
                byte[] result=null;
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where "+getNumberString()+"="+number+";");
                if (rs.next()) {
                    result=getDBByte(rs,1);
                }
                stmt.close();
                con.close();
                return(result);
            } catch (Exception e) {
                log.error("MMObjectBuilder : trying to load bytes");
                log.error(Logging.stackTrace(e));
            }
        } else {
            MMObjectNode tn=mmb.getTypeDef().getNode(number);
            String stype=mmb.getTypeDef().getValue(tn.getIntValue("otype"));
                byte[] result=readBytesFile(datapath+stype+"/"+number+"."+fieldname);
            return(result);
        }
        return(null);
    }


    /**
    * get byte of a database blob
    */
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
            log.error("MMObjectBuilder -> MMMysql byte  exception "+e);
            log.error(Logging.stackTrace(e));
        }
        return(bytes);
    }

    /**
    * get text of a database blob
    */
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
                //log.debug("MMObjectBuilder -> MMysql42Node DBtext no ascii "+inp);
                 return("");
            }
            if (rs.wasNull()) {
                log.trace("MMObjectBuilder -> MMysql42Node DBtext wasNull "+inp);
                return("");
            }
            siz=inp.available(); // DIRTY
            if (siz==0 || siz==-1) return("");
            input=new DataInputStream(inp);
            isochars=new byte[siz];
            input.readFully(isochars);
            str=new String(isochars,"ISO-8859-1");
            input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("MMObjectBuilder -> MMMysql text  exception "+e);
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
    public int insert(MMObjectBuilder bul,String owner, MMObjectNode node) {
        return(insert_real(bul,owner,node,bul.getTableName()));
    }

    public int insert_real(MMObjectBuilder bul,String owner, MMObjectNode node,String tableName) {
        int number=node.getIntValue("number");
        // did the user supply a number allready, ifnot try to obtain one
        if (number==-1) number=getDBKey();
        // did it fail ? ifso exit
        if (number == -1) return(-1);

        // moved this to before so i have it in blob save
        node.setValue("number",number);

        // Create a String that represents the amount of DB fields to be used in the insert.
        // First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
        String fieldAmounts="?";

        // Append the DB elements to the fieldAmounts String.
        for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
            String key = (String)e.nextElement();
            int DBState = node.getDBState(key);
            if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
              || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
                if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", adding key: "+key);

                    // hack for blobs to disk
                    int dbtype=node.getDBType(key);
                    if (!bdm || dbtype!=FieldDefs.TYPE_BYTE) {
                        fieldAmounts+=",?";
                    } else {
                    }
            } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", skipping key: "+key);
            } else {

                if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                        fieldAmounts+=",?";
                } else {
                    log.error("Insert: DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
                }
            }
        }

        MultiConnection con=null;
        PreparedStatement stmt=null;
        try {
            // Prepare the statement using the amount of fields found.
            if (log.isDebugEnabled()) log.trace("Insert: Preparing statement "+mmb.baseName+"_"+tableName+" using fieldamount String: "+fieldAmounts);
            con=bul.mmb.getConnection();
            stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values("+fieldAmounts+");");
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        try {
            stmt.setEscapeProcessing(false);
            // First add the 'number' field to the statement since it's not in the sortedDBLayout vector.
            stmt.setInt(1,number);

            int j=2;
            for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
                String key = (String)e.nextElement();
                int DBState = node.getDBState(key);
                if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
                  || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
                    if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", setValuePreparedStatement for key: "+key+", at pos:"+j);
                    if (setValuePreparedStatement( stmt, node, key, j )) j++;

                } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", skipping setValuePreparedStatement for key: "+key);
                } else {
                    if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                        setValuePreparedStatement( stmt, node, key, j );
                        j++;
                    } else {
                        log.error("Insert: DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
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

        if (node.parent!=null && (node.parent instanceof InsRel) && !tableName.equals("insrel")) {
            try {
                con=mmb.getConnection();
                // Note : this routine assumes a certain order in which the field in InsRel exist
                // deviating from that order may cause problems
                if (InsRel.usesdir) {
                    // add a dir field if directionality is supported
                    stmt=con.prepareStatement("insert into "+mmb.baseName+"_insrel values(?,?,?,?,?,?,?);");
                    stmt.setInt(1,number);
                    stmt.setInt(2,node.getIntValue("otype"));
                    stmt.setString(3,node.getStringValue("owner"));
                    stmt.setInt(4,node.getIntValue("snumber"));
                    stmt.setInt(5,node.getIntValue("dnumber"));
                    stmt.setInt(6,node.getIntValue("rnumber"));
                    stmt.setInt(7,node.getIntValue("dir"));
                } else {
                    stmt=con.prepareStatement("insert into "+mmb.baseName+"_insrel values(?,?,?,?,?,?);");
                    stmt.setInt(1,number);
                    stmt.setInt(2,node.getIntValue("otype"));
                    stmt.setString(3,node.getStringValue("owner"));
                    stmt.setInt(4,node.getIntValue("snumber"));
                    stmt.setInt(5,node.getIntValue("dnumber"));
                    stmt.setInt(6,node.getIntValue("rnumber"));
                }
                stmt.executeUpdate();
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error("Error on : "+number+" "+owner+" fake");
                log.error(Logging.stackTrace(e));
                return(-1);
            }
        }


        try {
            con=mmb.getConnection();
            stmt=con.prepareStatement("insert into "+mmb.baseName+"_object values(?,?,?);");
            stmt.setInt(1,number);
            stmt.setInt(2,node.getIntValue("otype"));
            stmt.setString(3,node.getStringValue("owner"));
            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("Error on : "+number+" "+owner+" fake");
            log.error(Logging.stackTrace(e));
            return(-1);
        }


        //bul.signalNewObject(tableName,number);
        if (bul.broadcastChanges) {
            if (bul instanceof InsRel) {
                bul.mmb.mmc.changedNode(node.getIntValue("number"),tableName,"n");
                // figure out tables to send the changed relations
                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                n1.delRelationsCache();
                n2.delRelationsCache();
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getName(),"r");
            } else {
                mmb.mmc.changedNode(node.getIntValue("number"),tableName,"n");
            }
        }
        node.clearChanged();
        return(number);
    }



    /**
    * set text array in database
    */
    public void setDBText(int i, PreparedStatement stmt,String body) {
        byte[] isochars=null;
        try {
            isochars=body.getBytes("ISO-8859-1");
        } catch (Exception e) {
            log.error("MMObjectBuilder -> String contains odd chars");
            log.error(body);
            log.error(Logging.stackTrace(e));
        }
        try {
            ByteArrayInputStream stream=new ByteArrayInputStream(isochars);
            stmt.setAsciiStream(i,stream,isochars.length);
            stream.close();
        } catch (Exception e) {
            log.error("MMObjectBuilder : Can't set ascii stream");
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
            log.error("MMObjectBuilder : Can't set byte stream");
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * commit this node to the database
     */
    public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
        //  precommit call, needed to convert or add things before a save
    bul.preCommit(node);

    // commit the object
    String builderFieldSql = null;
    boolean changeObjectFields = false;
    boolean changeInsrelFields = false;
    boolean isInsrelSubTable = node.parent!=null && node.parent instanceof InsRel && !bul.tableName.equals("insrel");

    // create the prepared statement
        for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
        String key=(String)e.nextElement();
        // a extra check should be added to filter temp values
            // like properties

            // is this key disallowed ? ifso map it back
            if (disallowed2allowed.containsKey(key)) key=(String)disallowed2allowed.get(key);

            // add the fieldname,.. and do smart ',' mapping
            if (builderFieldSql == null) builderFieldSql = key + "=?";
            else builderFieldSql += ", " + key+ "=?";

            // check if the fields are also in the object table...
        if(key.equals("number") || key.equals("otype") || key.equals("owner")) {

        // not allowed as far as im concerned...
        if(key.equals("number")) {
            log.fatal("trying to change the 'number' field");
                throw new RuntimeException("trying to change the 'number' field");
        }

        // hmm i dont like the idea of changing the otype..
                if(key.equals("otype")) log.error("changing the otype field, is this really needed? i dont think so, but hey i dont care..");


        // change the status, that object should be updated..
        changeObjectFields = true;

        // if it is a relation, then also adjust the insrel table if it is a sub table of that one..
                if(isInsrelSubTable) {

            // change the status, that insrel should be updated..
            changeInsrelFields = true;

            // give a warning that we are changing more then 1 table..
                log.warn("changing the '"+key+"' field, could give probems due to update on 3 tables without locking(builder, insrel, object)");
        }
        else log.warn("changing the '"+key+"' field, could give probems due to update on 2 tables without locking(builder, object)");
            } // object fields

        // if it is a relation, then also adjust the insrel table if it is a sub table of that one.. then adjust also fields from insrel
        if(isInsrelSubTable && ( key.equals("snumber") || key.equals("dnumber") || key.equals("rnumber") || key.equals("dir")) ) {
                // change the status, that insrel should be updated..
        changeInsrelFields = true;

        log.warn("changing the '"+key+"' field, could give probems due to update on 2 tables without locking(builder, insrel)");
        } // insrel fields
    } // add all changed fields...

        // when we had a update...
        if(builderFieldSql != null) {
        String sql = "UPDATE "+mmb.baseName+"_"+bul.tableName+" SET " + builderFieldSql + " WHERE "+getNumberString()+" = "+node.getValue("number")+";";
        log.debug("Temporary SQL statement, which will be filled with parameters : " + sql);

        try {
                // to do it chronological.. process till te reach the object builder...maybe the most smart thing todo...
            // start with the update of builder itselve first..
        MultiConnection con=mmb.getConnection();
                PreparedStatement stmt=con.prepareStatement(sql);

            // fill the '?' thingies with the values from the nodes..
                Enumeration changedFields = node.getChanged().elements();
        int currentParameter = 1;
        while(changedFields.hasMoreElements()) {
                    String key = (String) changedFields.nextElement();


        int type = node.getDBType(key);



            // for the right type call the right method..
            if (type==FieldDefs.TYPE_INTEGER) stmt.setInt(currentParameter,node.getIntValue(key));
            else if (type==FieldDefs.TYPE_NODE) stmt.setInt(currentParameter,node.getIntValue(key));
            else if (type==FieldDefs.TYPE_FLOAT) stmt.setFloat(currentParameter,node.getFloatValue(key));
            else if (type==FieldDefs.TYPE_DOUBLE) stmt.setDouble(currentParameter,node.getDoubleValue(key));
            else if (type==FieldDefs.TYPE_LONG) stmt.setLong(currentParameter,node.getLongValue(key));
            else if (type==FieldDefs.TYPE_STRING) setDBText(currentParameter,stmt,node.getStringValue(key));
            else if (type==FieldDefs.TYPE_XML) setDBText(currentParameter,stmt,node.getStringValue(key));
            else if (type==FieldDefs.TYPE_BYTE) setDBByte(currentParameter,stmt,node.getByteValue(key));
            else stmt.setString(currentParameter,node.getStringValue(key));
                    currentParameter++;
        }
            stmt.executeUpdate();
            stmt.close();

        // also change the insrel table, when it was a field from there..
        if (changeInsrelFields) {
            // the fields from object...
                    String insrelSql = "UPDATE  "+mmb.baseName+"_insrel SET otype="+node.getIntValue("otype")+", owner='"+node.getStringValue("owner")+"'";
            // the actual insrel fields..
                    insrelSql += ", snumber=" + node.getIntValue("snumber")+", dnumber="+node.getStringValue("dnumber")+", rnumber=" + node.getIntValue("rnumber");
            if(InsRel.usesdir) {
                // when we have directionality....
            insrelSql += ", dir=" + node.getIntValue("dir");
            }
            // condition..
                    insrelSql += " WHERE " + getNumberString() + "=" + node.getValue("number")+";";
                    log.debug(insrelSql);
                stmt=con.prepareStatement(insrelSql);
                    stmt.executeUpdate();
                    stmt.close();
        }

        // also change the object table, when it was a field from there..
        if (changeObjectFields) {
                    String objectSql = "UPDATE  "+mmb.baseName+"_object SET otype="+node.getIntValue("otype")+", owner='"+node.getStringValue("owner")+"'";
                    objectSql += " WHERE " + getNumberString() + "=" + node.getValue("number")+";";
                    log.debug(objectSql);
                stmt=con.prepareStatement(objectSql);
                    stmt.executeUpdate();
                    stmt.close();
        }
                con.close();
        }
        catch (SQLException e) {
            // give big warning bout data inconsitent thingie..
            if(changeObjectFields || changeInsrelFields) {
            log.fatal("update on multiple tables failed, database can be inconsisted !!");
            if(changeObjectFields) {
                log.error("we had to update the object table");
            }
            if(changeInsrelFields) {
                log.error("we had to update the insrel table");
            }
        }
                log.error(Logging.stackTrace(e));
        return false;
        }
    }
    else {
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
        mmb.mmc.changedNode(n1.getIntValue("number"),n1.getName(),"r");
        mmb.mmc.changedNode(n2.getIntValue("number"),n2.getName(),"r");
        }
        else {
                mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
        }
    }

    // done !
        return true;
    }

    /**
    * removeNode
    */
    public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
        int number=node.getIntValue("number");
        if(log.isDebugEnabled()) {
            log.trace("MMObjectBuilder -> delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number);
            log.trace("SAVECOPY "+node.toString());
        }
        Vector rels=bul.getRelations_main(number);
        if (rels!=null && rels.size()>0) {
            log.error("MMObjectBuilder ->PROBLEM! still relations attachched : delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number);
        } else {
        if (number!=-1) {
            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                stmt.executeUpdate("delete from "+mmb.baseName+"_"+bul.tableName+" where "+getNumberString()+"="+number+";");
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error(Logging.stackTrace(e));
            }
            if (node.parent!=null && (node.parent instanceof InsRel) && !bul.tableName.equals("insrel")) {
                try {
                    MultiConnection con=mmb.getConnection();
                    Statement stmt=con.createStatement();
                    stmt.executeUpdate("delete from "+mmb.baseName+"_insrel where "+getNumberString()+"="+number+";");
                    stmt.close();
                    con.close();
                } catch (SQLException e) {
                    log.error(Logging.stackTrace(e));
                }
            }

            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                stmt.executeUpdate("delete from "+mmb.baseName+"_object where "+getNumberString()+"="+number+";");
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

    /**
     * checks if numberTable exists.
     * If not this method will create one.
     * And inserts the DBKey retrieve by getDBKeyOld
     */
    public void checkNumberTable() {
        if (log.isDebugEnabled()) log.trace("checks if table numberTable exists.");
        if(!created(mmb.baseName+"_numberTable")) {
            // We want the current number of object, not next number (that's the -1)
            int number = getDBKeyOld()-1;

            if (log.isDebugEnabled()) log.trace("Creating table numberTable and inserting row with number "+number);
            String createStatement = getMatchCREATE("numberTable")+"( "+getNumberString()+" integer not null);";
            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                stmt.executeUpdate(createStatement);
                stmt.executeUpdate("insert into "+mmb.baseName+"_numberTable ("+getNumberString()+") values("+number+");");
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error("Wasn't able to create numberTable table.");
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * gives an unique number
     * this method will work with multiple mmbases
     * @return unique number
     */
    public synchronized int getDBKey() {
        int number =-1;
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            // not part of sql92, please find new trick (daniel)
            //stmt.executeUpdate("lock tables "+mmb.baseName+"_numberTable WRITE;");
            stmt.executeUpdate("update "+mmb.baseName+"_numberTable set "+getNumberString()+" = "+getNumberString()+"+1;");
            ResultSet rs=stmt.executeQuery("select "+getNumberString()+" from "+mmb.baseName+"_numberTable;");
            while(rs.next()) {
                        number=rs.getInt(1);
            }
            // not part of sql92, please find new trick (daniel)
            // stmt.executeUpdate("unlock tables;");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("SERIOUS ERROR, Problem with retrieving DBNumber from databse");
            log.error(Logging.stackTrace(e));
        }
        if (log.isDebugEnabled()) log.trace("retrieving number "+number+" from the database");
        return (number);
    }

    public synchronized int getDBKeyOld() {
        int number=-1;
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select max("+getNumberString()+") from "+mmb.getBaseName()+"_object;");
            if (rs.next()) {
                number=rs.getInt(1);
                number++;
            } else {
                number=1;
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("MMBase -> Error getting a new key number");
            return(1);
        }
        return(number);
    }

    /**
     * tells if a table already exists
     * @return true if table exists
     * @return false if table doesn't exists
     */
    public boolean created(String tableName) {
        if (size(tableName)==-1) {
            return(false);
        } else {
            return(true);
        }
    }


    /**
     * return number of entries consisting in given table
     * @param tableName the table that has to be counted
     * @return the number of items the table has
     */
    public int size(String tableName) {
        MultiConnection con=null;
        Statement stmt=null;
        try {
            con=mmb.getConnection();
            stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT count(*) FROM "+tableName+";");
            int i=-1;
            while(rs.next()) {
                i=rs.getInt(1);
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
    private boolean setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
        throws SQLException
    {

        // hack for jdbc bug in mckoi's jdbc driver
                if(key.lastIndexOf(".")!=-1)
                        key = key.substring(key.lastIndexOf(".")+1);
        int type = node.getDBType(key);
        if (type==FieldDefs.TYPE_INTEGER) {
            stmt.setInt(i, node.getIntValue(key));
        } if (type==FieldDefs.TYPE_NODE) {
            stmt.setInt(i, node.getIntValue(key));
        } else if (type==FieldDefs.TYPE_FLOAT) {
            stmt.setFloat(i, node.getFloatValue(key));
        } else if (type==FieldDefs.TYPE_DOUBLE) {
            stmt.setDouble(i, node.getDoubleValue(key));
        } else if (type==FieldDefs.TYPE_LONG) {
            stmt.setLong(i, node.getLongValue(key));
        } else if (type==FieldDefs.TYPE_STRING || type==FieldDefs.TYPE_XML) {
            String tmp=node.getStringValue(key);
            if (tmp!=null) {
                setDBText(i, stmt,tmp);
            } else {
                setDBText(i, stmt,"");
            }
        } else if (type==FieldDefs.TYPE_BYTE) {
                if (!bdm) {
                    setDBByte(i, stmt, node.getByteValue(key));
                } else {
                    String stype=mmb.getTypeDef().getValue(node.getIntValue("otype"));
                    File file = new File(datapath+stype);
                    try {
                        file.mkdirs();
                    } catch(Exception e) {
                        log.error("Can't create dir : "+datapath+stype);
                    }
                    byte[] value=node.getByteValue(key);
                    saveFile(datapath+stype+"/"+node.getIntValue("number")+"."+key,value);
                    return(false);
                }
        } else {
            String tmp=node.getStringValue(key);
            if (tmp!=null) {
                stmt.setString(i, tmp);
            } else {
                stmt.setString(i, "");
            }
        }
        return(true);
    }



    public boolean create(MMObjectBuilder bul) {
        return(create_real(bul,bul.getTableName()));
    }

    /**
    * will be removed once the xml setup system is done
    */
    public boolean create_real(MMObjectBuilder bul,String tableName) {

        // use the builder to get the fields are create a
        // valid create SQL string
        String result=null;

        Vector sfields=bul.sortedDBLayout;

        if (sfields!=null) {
            for (Enumeration e=sfields.elements();e.hasMoreElements();) {
                String name=(String)e.nextElement();
                FieldDefs def=bul.getField(name);
                if (def.getDBState() != org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    if (!bdm || def.getDBType()!=FieldDefs.TYPE_BYTE) {
                        String part=convertXMLType(def);
                        if (result==null) {
                            result=part;
                        } else {
                            result+=", "+part;
                        }
                    } else {
                    }
                }
            }
        }
        if (keySupported) {
            result=getMatchCREATE(tableName)+"( "+getNumberString()+" integer not null, "+parser.getPrimaryKeyScheme()+" ( "+getNumberString()+" ), "+result+" );";
        } else {
            result=getMatchCREATE(tableName)+"( "+getNumberString()+" integer not null, "+result+" );";
        }

        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            stmt.executeUpdate(result);
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("can't create table "+tableName);
            log.error("XMLCREATE="+result);
            log.error(Logging.stackTrace(e));
            return(false);
        }
        return(true);
    }


    public boolean drop(MMObjectBuilder bul) {
        return(drop_real(bul,bul.getTableName()));
    }

    /**
    * will be removed once the xml setup system is done
    */
    public boolean drop_real(MMObjectBuilder bul,String tableName) {

        if (tableSizeProtection(bul)) return(false);

        String result="drop table "+mmb.baseName+"_"+tableName+";";
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            stmt.executeUpdate(result);
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("can't create table "+tableName);
            log.error("XMLCREATE="+result);
            log.error(Logging.stackTrace(e));
            return(false);
        }
        return(true);
    }

    public boolean addField(MMObjectBuilder bul,String fieldname) {
        if (tableSizeProtection(bul)) return(false);

        log.info("Starting a addField : "+bul.getTableName()+" field="+fieldname);
        String tableName=bul.getTableName();
        if (create_real(bul,tableName+"_tmp")) {
            log.info("created tmp  table : "+tableName+"_tmp");
            copyJDBCTable(tableName,tableName+"_tmp",bul);
            if (checkJDBCTable(tableName,tableName+"_tmp",bul,fieldname,true)) {
                log.info("checked tmp table passed");
            } else {
                log.error("checked tmp table failed");
                drop_real(bul,tableName+"_tmp");
                return(false);
            }
        } else {
            log.error("tmp create table failed");
            return(false);
        }
        if (drop(bul)) {
            log.info("drop of old table done : "+bul.getTableName());
            if (create(bul)) {
                log.info("create of new table done : "+bul.getTableName());
                copyJDBCTable(tableName+"_tmp",tableName,bul);
                if (checkJDBCTable(tableName+"_tmp",tableName,bul)) {
                    if (drop_real(bul,tableName+"_tmp")) {
                        log.info("dropping tmp  table : "+tableName+"_tmp");
                    } else {
                        log.error("tmp table  drop failed");
                        return(false);
                    }
                } else {
                    log.error("checked new table failed");
                    return(false);
                }
                return(true);
            } else {
                log.error("create of new table failed : "+bul.getTableName());
                return(false);
            }
        }  else {
            log.error("drop of old table failed : "+bul.getTableName());
            return(false);
        }
    }

    public boolean removeField(MMObjectBuilder bul,String fieldname) {
        if (tableSizeProtection(bul)) return(false);

        log.info("Starting a removefield : "+bul.getTableName()+" field="+fieldname);

        String tableName=bul.getTableName();
        if (create_real(bul,tableName+"_tmp")) {
            log.info("created tmp  table : "+tableName+"_tmp");
            copyJDBCTable(tableName,tableName+"_tmp",bul);
            if (checkJDBCTable(tableName,tableName+"_tmp",bul,fieldname,false)) {
                log.info("checked tmp table passed");
            } else {
                log.error("checked tmp table failed");
                drop_real(bul,tableName+"_tmp");
                return(false);
            }
        } else {
            log.error("tmp create table failed");
            return(false);
        }
        if (drop(bul)) {
            log.info("drop of old table done : "+bul.getTableName());
            if (create(bul)) {
                log.info("create of new table done : "+bul.getTableName());
                copyJDBCTable(tableName+"_tmp",tableName,bul);
                if (checkJDBCTable(tableName+"_tmp",tableName,bul)) {
                    if (drop_real(bul,tableName+"_tmp")) {
                        log.info("dropping tmp  table : "+tableName+"_tmp");
                    } else {
                        log.error("tmp table  drop failed");
                        return(false);
                    }
                } else {
                    log.error("checked new table failed");
                    return(false);
                }
                return(true);
            } else {
                log.error("create of new table failed : "+bul.getTableName());
                return(false);
            }
        }  else {
            log.error("drop of old table failed : "+bul.getTableName());
            return(false);
        }
    }

    public boolean changeField(MMObjectBuilder bul,String fieldname) {
        if (tableSizeProtection(bul)) return(false);

        log.info("Starting a changeField : "+bul.getTableName()+" field="+fieldname);
        String tableName=bul.getTableName();
        if (create_real(bul,tableName+"_tmp")) {
            log.info("created tmp  table : "+tableName+"_tmp");
            copyJDBCTable(tableName,tableName+"_tmp",bul);
            if (checkJDBCTable(tableName,tableName+"_tmp",bul)) {
                log.info("checked tmp table passed");
            } else {
                log.error("checked tmp table failed");
                drop_real(bul,tableName+"_tmp");
                return(false);
            }
        } else {
            log.error("tmp create table failed");
            return(false);
        }
        if (drop(bul)) {
            log.info("drop of old table done : "+bul.getTableName());
            if (create(bul)) {
                log.info("create of new table done : "+bul.getTableName());
                copyJDBCTable(tableName+"_tmp",tableName,bul);
                if (checkJDBCTable(tableName+"_tmp",tableName,bul)) {
                    if (drop_real(bul,tableName+"_tmp")) {
                        log.info("dropping tmp  table : "+tableName+"_tmp");
                    } else {
                        log.error("tmp table  drop failed");
                        return(false);
                    }
                } else {
                    log.error("checked new table failed");
                    return(false);
                }
                return(true);
            } else {
                log.error("create of new table failed : "+bul.getTableName());
                return(false);
            }
        }  else {
            log.error("drop of old table failed : "+bul.getTableName());
            return(false);
        }
    }

    public boolean updateTable(MMObjectBuilder bul) {
        return(false);
    }



    public boolean createObjectTable(String baseName) {
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            if (keySupported) {
                stmt.executeUpdate("create table "+baseName+"_object ("+getNumberString()+" integer not null, otype integer not null, owner VARCHAR(12) not null, "+parser.getPrimaryKeyScheme()+" ("+getNumberString()+"));");
            } else {
                stmt.executeUpdate("create table "+baseName+"_object ("+getNumberString()+" integer not null, otype integer not null, owner VARCHAR(12) not null);");
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("can't create table "+baseName+"_object");
            log.error(Logging.stackTrace(e));
        }
        return(true);
    }

    public String convertXMLType(FieldDefs def) {

        // get the wanted mmbase type
        int type=def.getDBType();

        // get the wanted mmbase type
        String name=def.getDBName();

        // get the wanted size
        int size=def.getDBSize();

        // get the wanted notnull
        boolean notnull=def.getDBNotNull();

        //get the wanted key
        boolean iskey=def.isKey();

        if (name.equals("otype")) {
            return("otype integer "+parser.getNotNullScheme());
        } else {
            if (disallowed2allowed.containsKey(name)) {
                name=(String)disallowed2allowed.get(name);
            }
            String result=name+" "+matchType(type,size,notnull);
            if (notnull) result+=" "+parser.getNotNullScheme();
            if (keySupported) {
                if (iskey) result+=" "+parser.getNotNullScheme()+" ,"+parser.getKeyScheme()+ "("+name+")";
            }

            return(result);
        }
    }


    String matchType(int type, int size, boolean notnull) {
        String result=null;
        if (typeMapping!=null) {
            dTypeInfos  typs=(dTypeInfos)typeMapping.get(new Integer(type));
            if (typs!=null) {
                for (Enumeration e=typs.maps.elements();e.hasMoreElements();) {
                     dTypeInfo typ = (dTypeInfo)e.nextElement();
                    // needs smart mapping code
                    if (size==-1) {
                        result=typ.dbType;
                    } else if (typ.minSize!=-1) {
                        if (size>=typ.minSize) {
                            if (typ.maxSize!=-1) {
                                if (size<=typ.maxSize) {
                                    result=mapSize(typ.dbType,size);
                                }
                            } else {
                                result=mapSize(typ.dbType,size);
                            }
                        }
                    } else if (typ.maxSize!=-1) {
                        if (size<=typ.maxSize) {
                            result=mapSize(typ.dbType,size);
                        }
                    } else {
                        result=typ.dbType;
                    }
                }
            }
        }
        return(result);
    }

    String mapSize(String line, int size) {
        int pos=line.indexOf("size");
        if (pos!=-1) {
            String tmp=line.substring(0,pos)+size+line.substring(pos+4);
            return(tmp);
        }
        return(line);
    }

    /**
     * gets the sytax of the create statement for current database.
     */
    public String getMatchCREATE(String tableName) {
        return(parser.getCreateScheme()+" "+mmb.baseName+"_"+tableName+" ");
    }

    public Hashtable getReverseHash(Hashtable in) {
        Hashtable out=new Hashtable();
        for (Enumeration e=in.keys();e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = in.get(key);
            out.put(value,key);
        }
        return(out);
    }


    public String getDisallowedField(String allowedfield) {
        if (allowed2disallowed.containsKey(allowedfield)) {
            allowedfield=(String)allowed2disallowed.get(allowedfield);
        }
        return(allowedfield);
    }


    public String getAllowedField(String disallowedfield) {
        if (disallowed2allowed.containsKey(disallowedfield)) {
            disallowedfield=(String)disallowed2allowed.get(disallowedfield);
        }
        return(disallowedfield);
    }

    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
        MultiConnection con=jdbc.getConnection(jdbc.makeUrl());
        return(con);
    }

    public void mapDefaultFields(Hashtable disallowed2allowed) {
        if (disallowed2allowed.containsKey("number")) {
            numberString=(String)disallowed2allowed.get("number");
        } else {
            numberString="number";
        }
        if (disallowed2allowed.containsKey("otype")) {
            otypeString=(String)disallowed2allowed.get("otype");
        } else {
            otypeString="otype";
        }
        if (disallowed2allowed.containsKey("owner")) {
            ownerString=(String)disallowed2allowed.get("owner");
        } else {
            ownerString="owner";
        }
    }

    public String getNumberString() {
        return(numberString);
    }

    public String getOTypeString() {
        return(otypeString);
    }

    public String getOwnerString() {
        return(ownerString);
    }


    static boolean saveFile(String filename,byte[] value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.write(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return(true);
    }


    byte[] readBytesFile(String filename) {
    File bfile = new File(filename);
    int filesize = (int)bfile.length();
    byte[] buffer=new byte[filesize];
    try {
            FileInputStream scan = new FileInputStream(bfile);
        int len=scan.read(buffer,0,filesize);
        scan.close();
    } catch(FileNotFoundException e) {
        log.error("error getfile : "+filename);
    } catch(IOException e) {}
    return(buffer);
    }

    private boolean checkJDBCTable(String tablename,String tmptable,MMObjectBuilder bul) {
    return(checkJDBCTable(tablename,tmptable,bul,null,false));
    }

    private boolean checkJDBCTable(String tablename,String tmptable,MMObjectBuilder bul,String ignorefield,boolean add) {

    // performs several checks to make sure our copy is valid

    // first check the size of each table using a count
    try {
        // get result 1
        int size1=-1;
        MultiConnection con=mmb.getConnection();
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("select count(*) from "+mmb.baseName+"_"+tablename+";");
        if (rs.next()) {
            size1=rs.getInt(1);
        }
        stmt.close();
        con.close();

        // get result 2
        int size2=-1;
        con=mmb.getConnection();
        stmt=con.createStatement();
        rs=stmt.executeQuery("select count(*) from "+mmb.baseName+"_"+tmptable+";");
        if (rs.next()) {
            size2=rs.getInt(1);
        }
        stmt.close();
        con.close();

        if (size1!=size2) {
            log.error("tmp table have same number of rows !!!");
        }


        // first check the size of each table using a count
        // get result 1
        con=mmb.getConnection();
        stmt=con.createStatement();
        rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tablename+";");
        ResultSetMetaData rsmd = rs.getMetaData();
        int colsize1=rsmd.getColumnCount();
        stmt.close();
        con.close();

        // first check the size of each table using a count
        // get result 2
        con=mmb.getConnection();
        stmt=con.createStatement();
        rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tmptable+";");
        rsmd = rs.getMetaData();
        int colsize2=rsmd.getColumnCount();
        stmt.close();
        con.close();

        if (ignorefield==null) {
            if (colsize1!=colsize2) {
                log.error("tmp table have same number of cols !!!");
                return(false);
            }
        } else {
            if (add) {
                if (colsize1!=(colsize2-1)) {
                    log.error("tmp table should have one more cols then org !!!");
                    return(false);
                }
            } else {
                if (colsize1!=(colsize2+1)) {
                    log.error("tmp table should have one less cols then org !!!");
                    return(false);
                }
            }
        }

        // now lets check the data in the table compare the two

        // get the first data set...
        con=mmb.getConnection();
        stmt=con.createStatement();
        rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tablename+";");
        rsmd = rs.getMetaData();
        int colcount1=rsmd.getColumnCount();

        MultiConnection con2=mmb.getConnection();
        Statement stmt2=con2.createStatement();
        ResultSet rs2=stmt2.executeQuery("select * from "+mmb.baseName+"_"+tmptable+";");
        ResultSetMetaData rsmd2 = rs2.getMetaData();
        int colcount2=rsmd2.getColumnCount();

        while (rs.next()) {
            Hashtable values1=new Hashtable(colcount1);
            for (int i=1;i<colcount1+1;i++) {
                Object o=rs.getObject(i);
                if (o instanceof byte[]) {
                    o=rs.getString(i);
                }
                String colname=rsmd.getColumnName(i);
                colname=colname.toLowerCase();
                values1.put(colname,o);
            }

            rs2.next();
            Hashtable values2=new Hashtable(colcount1);
            for (int i=1;i<colcount2+1;i++) {
                Object o=rs2.getObject(i);
                if (o instanceof byte[]) {
                    o=rs2.getString(i);
                }
                String colname=rsmd2.getColumnName(i);
                colname=colname.toLowerCase();
                values2.put(colname,o);
            }

            for (Enumeration e=values1.keys();e.hasMoreElements();) {
                String key1=(String)e.nextElement();
                if (ignorefield==null || !key1.equals(ignorefield)) {
                    Object value1=values1.get(key1);
                    Object value2=values2.get(key1);
                    /*
                    log.debug("SET1="+value1.toString());
                    log.debug("SET2="+value2.toString());
                    */
                    if (!(value1.toString()).equals((value2.toString()))) {
                        log.error("data check error on field : "+key1);
                        return(false);
                    }
                } else {
                    // log.debug("IGNORED="+key1);
                }
            }

        }
        stmt2.close();
        con2.close();
        stmt.close();
        con.close();
    } catch(SQLException e) {
        log.error(Logging.stackTrace(e));
        return(false);
    }
    return(true);
    }

    private boolean copyJDBCTable(String tablename,String tmptable,MMObjectBuilder bul) {
    log.info("Starting JBDC copy to temp table");
    try {
        MultiConnection con=mmb.getConnection();
        Statement stmt=con.createStatement();
        ResultSet rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tablename+";");
        ResultSetMetaData rsmd = rs.getMetaData();
        int colcount=rsmd.getColumnCount();
        String fieldAmounts="?";

        Vector newfields=bul.getFields();
        for (int i=1;i<newfields.size();i++) {
            fieldAmounts+=",?";
        }

        while(rs.next()) {
            MultiConnection con2=mmb.getConnection();
            PreparedStatement stmt2=con2.prepareStatement("insert into "+mmb.baseName+"_"+tmptable+" values("+fieldAmounts+");");
            stmt2.setEscapeProcessing(false);
            Hashtable oldvalues=new Hashtable(colcount);
            for (int i=1;i<colcount+1;i++) {
                Object o=rs.getObject(i);
                String colname=rsmd.getColumnName(i);
                colname=colname.toLowerCase();
                oldvalues.put(colname,o);
            }
            newfields=bul.getFields();

            Object o=oldvalues.get(getAllowedField("number"));
            stmt2.setInt(1,((Integer)o).intValue());
            for (Enumeration e=newfields.elements();e.hasMoreElements();) {
                FieldDefs def=(FieldDefs)e.nextElement();
                String newname=def.getDBName();
                int dbpos=def.getDBPos();

                o=oldvalues.get(getAllowedField(newname));

                if (o==null) {
                    int type=def.getDBType();
                    switch (type) {
                        case FieldDefs.TYPE_BYTE:
                            setDBByte(dbpos,stmt2,new byte[0]);
                            break;
                        case FieldDefs.TYPE_XML:
                        case FieldDefs.TYPE_STRING:
                            setDBText(dbpos,stmt2,new String());
                            break;
                        case FieldDefs.TYPE_NODE:
                        case FieldDefs.TYPE_INTEGER:
                            stmt2.setInt(dbpos,-1);
                            break;
                        case FieldDefs.TYPE_DOUBLE:
                            stmt2.setDouble(dbpos,-1);
                            break;
                        case FieldDefs.TYPE_FLOAT:
                            stmt2.setFloat(dbpos,-1);
                            break;
                        case FieldDefs.TYPE_LONG:
                            stmt2.setLong(dbpos,-1);
                            break;
                    }
                } else {
                    int type=def.getDBType();
                    switch (type) {
                        case FieldDefs.TYPE_BYTE:
                            setDBByte(dbpos,stmt2,(byte[])o);
                            break;
                        case FieldDefs.TYPE_XML:
                        case FieldDefs.TYPE_STRING:
                            if (o instanceof byte[]) {
                                String s=new String((byte[])o);
                                setDBText(dbpos,stmt2,s);
                            } else {
                                setDBText(dbpos,stmt2,o.toString());
                            }
                            break;
                        case FieldDefs.TYPE_NODE:
                        case FieldDefs.TYPE_INTEGER:
                            stmt2.setInt(dbpos,((Number)o).intValue());
                            break;
                        case FieldDefs.TYPE_DOUBLE:
                            stmt2.setDouble(dbpos,((Number)o).doubleValue());
                            break;
                        case FieldDefs.TYPE_FLOAT:
                            stmt2.setFloat(dbpos,((Number)o).floatValue());
                            break;
                        case FieldDefs.TYPE_LONG:
                            stmt2.setLong(dbpos,((Number)o).longValue());
                            break;
                    }
                }
            }
            stmt2.executeUpdate();
            stmt2.close();
            con2.close();
        }
        stmt.close();
        con.close();
    } catch(SQLException e) {
        log.error(Logging.stackTrace(e));
        return(false);
    }
    return(true);
    }

    public boolean tableSizeProtection(MMObjectBuilder bul) {
        int size=bul.size();
        if (size>parser.getMaxDropSize()) {
            log.debug("Database table changed not allowed on : "+bul.getTableName());
            log.debug("check <maxdropsize> in your database.xml");
            log.debug("defined max is : "+parser.getMaxDropSize()+" table was : "+size);
            return(true);
        }
        return(false);
     }
}
