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
 * MMSQL92Node implements the MMJdbc2NodeInterface for
 * sql92 types of database this is the class used to abstact the query's
 * needed for mmbase for each database.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @author Kees Jongenburger
 * @version $Id: MMSQL92Node.java,v 1.75.2.3 2004-01-08 16:35:29 robmaris Exp $
 */
public class MMSQL92Node implements MMJdbc2NodeInterface {

    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(MMSQL92Node.class.getName());

    //does the database support keys?
    private boolean keySupported=false;

    /**
     * @javadoc
     * @scope private
     */
    public String name="sql92";
    /**
     * @javadoc
     * @scope private
     */
    protected XMLDatabaseReader parser;
    /**
     * @javadoc
     * @scope private
     */
    protected Hashtable typeMapping = new Hashtable();
    /**
     * @javadoc
     * @scope private
     */
    protected Hashtable disallowed2allowed;
    /**
     * @javadoc
     * @scope private
     */
    protected Hashtable allowed2disallowed;
    /**
     * @javadoc
     */
    private String numberString;
    private String otypeString;
    private String ownerString;
    private boolean bdm=false;
    private String datapath="/tmp/data/";

    /**
     * @javadoc
     * @scope private
     */
    protected MMBase mmb;

    public MMSQL92Node() {
    }

    /**
     * @javadoc
     */
    public void init(MMBase mmb,XMLDatabaseReader parser) {
        this.mmb=mmb;
        this.parser=parser;

        datapath=parser.getBlobDataDir();
        if (datapath!=null && !datapath.equals("")) {
            bdm=true;
        }

        typeMapping=parser.getTypeMapping();
        disallowed2allowed = parser.getDisallowedFields();
        allowed2disallowed = getReverseHash(disallowed2allowed);
        // map the default types
        mapDefaultFields(disallowed2allowed);
        // Check if the numbertable exists, if not one will be created.
        checkNumberTable();
    }

    /**
     * @javadoc
     */
    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i) {
        return decodeDBnodeField(node, fieldname, rs, i, "");
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
    public void registerParentBuilder(MMObjectBuilder parent, MMObjectBuilder child)
        throws UnsupportedDatabaseOperationException {
        if (!isAllowedParentBuilder(parent)) {
            throw new UnsupportedDatabaseOperationException("Cannot extend the builder with name "+parent.getTableName());
        }
    }


    /**
     * Some Database implementations want to fake encoding, and can override this function.
     * @since MMBase-1.6
     */

    protected String decodeStringField(ResultSet rs, int i) throws SQLException {
        return rs.getString(i);
    }

    /**
     * @javadoc
     */
    public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs, int i, String prefix) {
        try {
            // is this fieldname disallowed ? ifso map it back
            if (allowed2disallowed.containsKey(fieldname)) {
                fieldname = (String)allowed2disallowed.get(fieldname);
            }
            if (node == null) {
                log.warn("Cannot decode field " + fieldname + " because given node is null");
            }
            int type=node.getDBType(prefix+fieldname);
            switch (type) {
            case FieldDefs.TYPE_XML:
            case FieldDefs.TYPE_STRING: {
                String tmp = decodeStringField(rs, i);

                if (tmp == null) {
                    node.setValue(prefix + fieldname, "");
                } else {
                    node.setValue(prefix + fieldname, tmp);
                }

                
                break;
            }
            case FieldDefs.TYPE_NODE:
            case FieldDefs.TYPE_INTEGER:
                node.setValue(prefix + fieldname, (Integer)rs.getObject(i));
                break;
            case FieldDefs.TYPE_LONG:
                node.setValue(prefix + fieldname, (Long)rs.getObject(i));
                break;
            case FieldDefs.TYPE_FLOAT:
                // who does this now work ????
                //node.setValue(prefix+fieldname,((Float)rs.getObject(i)));
                node.setValue(prefix + fieldname, new Float(rs.getFloat(i)));
                break;
            case FieldDefs.TYPE_DOUBLE:
                node.setValue(prefix+fieldname,(Double)rs.getObject(i));
                break;
            case FieldDefs.TYPE_BYTE:
                node.setValue(prefix+fieldname,"$SHORTED");
                break;
            }
            return node;
        } catch(SQLException e) {
            log.error("MMSQL92Node mmObject->"+fieldname+" node="+node.getIntValue("number"));
            log.error(Logging.stackTrace(e));
        }
        return node;
    }

    /**
     * @javadoc
     */
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
        return result;
    }

    /**
     * @javadoc
     */
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

    /**
     * Get text from blob
     * @javadoc
     */
    public String getShortedText(String tableName,String fieldname,int number) {
        try {
            String result=null;
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where "+getNumberString()+"="+number);
            try {
                if (rs.next()) {
                    result=getDBText(rs,1);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
            return result;
        } catch (Exception e) {
            log.error("MMObjectBuilder : trying to load text");
            log.error(Logging.stackTrace(e));
        }
        return null;
    }


    /**
     * Get byte of a database blob
     * @javadoc
     */
    public byte[] getShortedByte(String tableName,String fieldname,int number) {
        if (!bdm) {
            try {
                byte[] result=null;
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where "+getNumberString()+"="+number);
                try {
                    if (rs.next()) {
                        result=getDBByte(rs,1);
                    }
                } finally {
                    rs.close();
                }
                stmt.close();
                con.close();
                return result;
            } catch (Exception e) {
                log.error("MMObjectBuilder : trying to load bytes");
                log.error(Logging.stackTrace(e));
            }
        } else {
            MMObjectNode tn=mmb.getTypeDef().getNode(number);
            String stype=mmb.getTypeDef().getValue(tn.getIntValue("otype"));
            byte[] result=readBytesFile(datapath+stype+"/"+number+"."+fieldname);
            return result;
        }
        return null;
    }


    /**
     * Get byte of a database blob
     * @javadoc
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
        return bytes;
    }

    /**
     * Get text of a database blob
     * @javadoc
     */
    public String getDBText(ResultSet rs,int idx) {
        String str=null;
        InputStream inp;
        DataInputStream input;
        byte[] rawchars;
        int siz;

        if (0==1) return("");
        try {
            //inp = rs.getAsciiStream(idx);
            inp = rs.getBinaryStream(idx);
            if (inp==null) {
                //log.debug("MMObjectBuilder -> MMysql42Node DBtext no ascii "+inp);
                return("");
            }
            if (rs.wasNull()) {
                log.trace("MMObjectBuilder -> MMysql42Node DBtext wasNull " + inp);
                return("");
            }
            siz=inp.available(); // DIRTY
            if (siz==0 || siz==-1) return("");
            input   =new DataInputStream(inp);
            rawchars = new byte[siz];
            input.readFully(rawchars);
            str = new String(rawchars, mmb.getEncoding());
            input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("MMObjectBuilder -> MMMysql text  exception "+e);
            log.error(Logging.stackTrace(e));
            return "";
        }
        return str;
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
        return insert_real(bul,owner,node,bul.getTableName());
    }

    /**
     * @javadoc
     */
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
        StringBuffer fieldAmounts = new StringBuffer();

        // Append the DB elements to the fieldAmounts String.
        for (Enumeration e= ((Vector) bul.getFields(FieldDefs.ORDER_CREATE)).elements();e.hasMoreElements();) {
            String key = ((FieldDefs) e.nextElement()).getDBName();
            int DBState = node.getDBState(key);
            if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
                 || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {
                if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", adding key: "+key);

                // hack for blobs to disk
                int dbtype=node.getDBType(key);
                if (!bdm || dbtype!=FieldDefs.TYPE_BYTE) {
                    fieldAmounts.append(",?");
                } else {
                }
            } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", skipping key: "+key);
            } else {

                if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
                    fieldAmounts.append(",?");
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
            stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values("+fieldAmounts.substring(1)+")");
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
        }
        try {
            stmt.setEscapeProcessing(false);
            int j = 1;
            for (Enumeration e=((Vector) bul.getFields(FieldDefs.ORDER_CREATE)).elements();e.hasMoreElements();) {
                String key = ((FieldDefs)e.nextElement()).getDBName();
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
            return -1;
        }

        if (node.parent!=null && (node.parent instanceof InsRel) && !tableName.equals("insrel")) {
            try {
                con=mmb.getConnection();
                // Note : this routine assumes a certain order in which the field in InsRel exist
                // deviating from that order may cause problems
                if (InsRel.usesdir) {
                    // add a dir field if directionality is supported
                    stmt=con.prepareStatement("insert into "+mmb.baseName+"_insrel values(?,?,?,?,?,?,?)");
                    stmt.setInt(1,number);
                    stmt.setInt(2,node.getIntValue("otype"));
                    stmt.setString(3,node.getStringValue("owner"));
                    stmt.setInt(4,node.getIntValue("snumber"));
                    stmt.setInt(5,node.getIntValue("dnumber"));
                    stmt.setInt(6,node.getIntValue("rnumber"));
                    stmt.setInt(7,node.getIntValue("dir"));
                } else {
                    stmt=con.prepareStatement("insert into "+mmb.baseName+"_insrel values(?,?,?,?,?,?)");
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
                return -1;
            }
        }

        // update the object table unless this is the 'object' builder
        // since MMbase-1.6 'object' can also appear as a builder
        if (!tableName.equals("object")) {
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
                log.error("Error on : "+number+" "+owner+" fake");
                log.error(Logging.stackTrace(e));
                return -1;
            }
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
        return number;
    }

    /**
     * Set text array in database
     * @javadoc
     */
    public void setDBText(int i, PreparedStatement stmt,String body) {
        byte[] rawchars=null;
        try {
            rawchars=body.getBytes(mmb.getEncoding());
        } catch (Exception e) {
            log.error("MMObjectBuilder -> String contains odd chars");
            log.error(body);
            log.error(Logging.stackTrace(e));
        }
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(rawchars);
            stmt.setBinaryStream(i,stream,rawchars.length);
            stream.close();
        } catch (Exception e) {
            log.error("MMObjectBuilder : Can't set ascii stream");
            log.error(Logging.stackTrace(e));
        }
    }


    /**
     * Set byte array in database
     * @javadoc
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
     * Commit this node to the database
     * @javadoc
     */
    public boolean commit(MMObjectBuilder bul,MMObjectNode node) {
        //  precommit call, needed to convert or add things before a save
        bul.preCommit(node);

        // commit the object
        String builderFieldSql = null;
        boolean changeObjectFields = false;
        boolean changeInsrelFields = false;
        boolean isInsrelSubTable = node.parent!=null && node.parent instanceof InsRel && !bul.getTableName().equals("insrel");

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
                if(key.equals("otype")) {
                    log.warn("Changing the otype field - should not be allowed?");
                }

                // change the status, that object should be updated, unless this is the object builder itself
                changeObjectFields = !bul.getTableName().equals("object");
                if (changeObjectFields) {
                    // if it is a relation, then also adjust the insrel table if it is a sub table of that one..
                    changeInsrelFields =isInsrelSubTable;
                    // log.warn("changing the '"+key+"' field, could give probems due to update on 2 tables without locking(builder, object)");
                }
            } // object fields

            // if it is a relation, then also adjust the insrel table if it is a sub table of that one.. then adjust also fields from insrel
            if(isInsrelSubTable && ( key.equals("snumber") || key.equals("dnumber") || key.equals("rnumber") || key.equals("dir")) ) {
                // change the status, that insrel should be updated..
                changeInsrelFields = true;
                // log.warn("changing the '"+key+"' field, could give problems due to update on 2 tables without locking(builder, insrel)");
            } // insrel fields
        } // add all changed fields...

        // when we had a update...
        if(builderFieldSql != null) {
            String sql = "UPDATE "+mmb.baseName+"_"+bul.getTableName()+" SET " + builderFieldSql + " WHERE "+getNumberString()+" = "+node.getValue("number");
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
                    if (type==FieldDefs.TYPE_INTEGER) {
                        stmt.setInt(currentParameter,node.getIntValue(key));
                    } else if (type==FieldDefs.TYPE_NODE) {
                        stmt.setInt(currentParameter,node.getIntValue(key));
                    } else if (type==FieldDefs.TYPE_FLOAT) {
                        stmt.setFloat(currentParameter,node.getFloatValue(key));
                    } else if (type==FieldDefs.TYPE_DOUBLE) {
                        stmt.setDouble(currentParameter,node.getDoubleValue(key));
                    } else if (type==FieldDefs.TYPE_LONG) {
                        stmt.setLong(currentParameter,node.getLongValue(key));
                    } else if (type==FieldDefs.TYPE_STRING) {
                        setDBText(currentParameter,stmt,node.getStringValue(key));
                    } else if (type==FieldDefs.TYPE_XML) {
                        setDBText(currentParameter,stmt,node.getStringValue(key));
                    } else if (type==FieldDefs.TYPE_BYTE) {
                        setDBByte(currentParameter,stmt,node.getByteValue(key));
                    } else {
                        stmt.setString(currentParameter,node.getStringValue(key));
                    }
                    currentParameter++;
                }
                stmt.executeUpdate();
                stmt.close();

                // also change the insrel table, when it was a field from there..
                if (changeInsrelFields) {
                    // the fields from object...
                    String insrelSql = "UPDATE "+mmb.baseName+"_insrel SET otype="+node.getIntValue("otype")+", owner='"+node.getStringValue("owner")+"'";
                    // the actual insrel fields..
                    insrelSql += ", snumber=" + node.getIntValue("snumber")+", dnumber="+node.getStringValue("dnumber")+", rnumber=" + node.getIntValue("rnumber");
                    if(InsRel.usesdir) {
                        // when we have directionality....
                        insrelSql += ", dir=" + node.getIntValue("dir");
                    }
                    // condition..
                    insrelSql += " WHERE " + getNumberString() + "=" + node.getValue("number");
                    log.debug(insrelSql);
                    stmt=con.prepareStatement(insrelSql);
                    stmt.executeUpdate();
                    stmt.close();
                }

                // also change the object table, when it was a field from there..
                // update the object table unless this is the 'object' builder
                // since MMbase-1.6 'object' can also appear as a builder
                if (changeObjectFields) {
                    String objectSql = "UPDATE  "+mmb.baseName+"_object SET otype="+node.getIntValue("otype")+", owner='"+node.getStringValue("owner")+"'";
                    objectSql += " WHERE " + getNumberString() + "=" + node.getValue("number");
                    log.debug(objectSql);
                    stmt=con.prepareStatement(objectSql);
                    stmt.executeUpdate();
                    stmt.close();
                }
                con.close();
            } catch (SQLException e) {
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
        } else {
            // tried to update a node without any changes,..
            return true;
        }

        // done database update, so clear changed flags..
        node.clearChanged();

        // broadcast the changes, if nessecary...
        if (bul.broadcastChanges) {
            if (bul instanceof InsRel) {
                bul.mmb.mmc.changedNode(node.getIntValue("number"),bul.getTableName(),"c");
                // figure out tables to send the changed relations
                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getName(),"r");
            } else {
                mmb.mmc.changedNode(node.getIntValue("number"),bul.getTableName(),"c");
            }
        }
        // done !
        return true;
    }

    /**
     * removeNode
     * @javadoc
     */
    public void removeNode(MMObjectBuilder bul,MMObjectNode node) {
        int number=node.getIntValue("number");
        if(log.isDebugEnabled()) {
            log.trace("MMObjectBuilder -> delete from "+mmb.baseName+"_"+bul.getTableName()+" where "+getNumberString()+"="+number);
            log.trace("SAVECOPY "+node.toString());
        }
        Vector rels=bul.getRelations_main(number);
        if (rels!=null && rels.size()>0) {
            log.error("MMObjectBuilder ->PROBLEM! still relations attachched : delete from "+mmb.baseName+"_"+bul.getTableName()+" where "+getNumberString()+"="+number);
        } else {
            if (number!=-1) {
                //first alway's remove the "requested" object from the table it belongs to
                try {
                    MultiConnection con=mmb.getConnection();
                    Statement stmt=con.createStatement();
                    stmt.executeUpdate("delete from "+mmb.baseName+"_"+bul.getTableName()+" where "+getNumberString()+"="+number);
                    stmt.close();
                    con.close();
                } catch (SQLException e) {
                    log.error(Logging.stackTrace(e));
                }
                //during the OO->relational mapping it wat decided that all relations should remain
                //in the insrel table. If the node requested to delete is a relation node and it is not insrel
                //we need to also remove it from the insrel table
                if (node.parent!=null && (node.parent instanceof InsRel) && !bul.getTableName().equals("insrel")) {
                    try {
                        MultiConnection con=mmb.getConnection();
                        Statement stmt=con.createStatement();
                        stmt.executeUpdate("delete from "+mmb.baseName+"_insrel where "+getNumberString()+"="+number);
                        stmt.close();
                        con.close();
                    } catch (SQLException e) {
                        log.error(Logging.stackTrace(e));
                    }
                }
                //due to optimalisation when one requests the "relations" that a node has the layer
                //returns relations of type insrel. If you then call "delete" on that insrel it might
                //be that case that at this point in the stage of delete that there are still fields
                //in for example a posrel tables. We need to find out if the object we are trying to delete
                //is keeps it's information somewhere else so this only happens when the builder when the builder
                //is InsRel and the table is insrel but the real tables is not insrel

                //if insrel but otype != insrel
                String otypeString = mmb.getTypeDef().getValue(node.getOType());
                if (node.parent!=null && (node.parent instanceof InsRel) && bul.getTableName().equals("insrel") &&
                    !otypeString.equals("insrel")) {
                    log.debug("deleting row in subtable of insrel the subtable is of type("+ otypeString +") and the object number is="+ number);
                    try {
                        MultiConnection con=mmb.getConnection();
                        Statement stmt=con.createStatement();
                        stmt.executeUpdate("delete from "+mmb.baseName+"_"+otypeString+" where "+getNumberString()+"="+number);
                        stmt.close();
                        con.close();
                    } catch (SQLException e) {
                        log.error(Logging.stackTrace(e));
                    }
                }

                //during the OO->relational mapping the object table is not anymore "automaticatly" updated
                //so we also need to remove the object from the object table
                // update the object table unless this is the 'object' builder
                // since MMbase-1.6 'object' can also appear as a builder
                if (!bul.getTableName().equals("object")) {
                    try {
                        MultiConnection con=mmb.getConnection();
                        Statement stmt=con.createStatement();
                        stmt.executeUpdate("delete from "+mmb.baseName+"_object where "+getNumberString()+"="+number);
                        stmt.close();
                        con.close();
                    } catch (SQLException e) {
                        log.error(Logging.stackTrace(e));
                    }
                }
            }
        }
        if (bul.broadcastChanges) {
            mmb.mmc.changedNode(node.getIntValue("number"),bul.getTableName(),"d");
            if (bul instanceof InsRel) {
                MMObjectNode n1=bul.getNode(node.getIntValue("snumber"));
                MMObjectNode n2=bul.getNode(node.getIntValue("dnumber"));
                mmb.mmc.changedNode(n1.getIntValue("number"),n1.getName(),"r");
                mmb.mmc.changedNode(n2.getIntValue("number"),n2.getName(),"r");
            }
        }
    }

    /**
     * Checks if the numberTable exists.
     * If not this method will create one,
     * and inserts the DBKey retrieve by getDBKeyOld
     */
    private void checkNumberTable() {
        if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> checks if table numberTable exists.");
        if (!created(mmb.baseName + "_numberTable")) {
            // We want the current number of object, not next number (that's the -1)
            int number = getDBKeyOld() - 1;
            

            if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> Creating table numberTable and inserting row with number "+number);
            String createStatement = getMatchCREATE("numberTable")+"( "+getNumberString()+" integer not null);";
            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                stmt.executeUpdate(createStatement);
                stmt.executeUpdate("insert into "+mmb.baseName+"_numberTable ("+getNumberString()+") values("+number+");");
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error("MMSQL92NODE -> Wasn't able to create numberTable table.");
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Gives an unique number for a node to be inserted.
     * This method will work with multiple mmbases
     * @return unique number
     */
    public synchronized int getDBKey() {
        int number =-1;
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            // not part of sql92, please find new trick (daniel)
            //stmt.executeUpdate("lock tables "+mmb.baseName+"_numberTable WRITE;");
            stmt.executeUpdate("update "+mmb.baseName+"_numberTable set "+getNumberString()+" = "+getNumberString()+"+1");
            ResultSet rs=stmt.executeQuery("select "+getNumberString()+" from "+mmb.baseName+"_numberTable;");
            try {
                while(rs.next()) {
                    number=rs.getInt(1);
                }
            } finally {
                rs.close();
            }
            // not part of sql92, please find new trick (daniel)
            // stmt.executeUpdate("unlock tables;");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("MMSQL92NODE -> SERIOUS ERROR, Problem with retrieving DBNumber from databse");
            log.error(Logging.stackTrace(e));
        }
        if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> retrieving number "+number+" from the database");
        return number;
    }

    /**
     * Get a new object key without using numberTable, that is, by getting the max number of mm_object.
     * If object table does not exist (yet), it returns 1.
     * This is only used when creating the numberTable. 
     * @deprecated Can be replaced by '1'. Because new installations create the the numberTable when there are not objects yet.
     */
    protected synchronized int getDBKeyOld() {
        int number = -1;
        if(created(mmb.getBaseName() + "_object")) {
            try {
                MultiConnection con=mmb.getConnection();
                Statement stmt=con.createStatement();
                ResultSet rs=stmt.executeQuery("select max("+getNumberString()+") from "+mmb.getBaseName()+"_object");
                try {
                    if (rs.next()) {
                        number=rs.getInt(1);
                        number++;
                    } else {
                        // no objects yet
                        number = 1;
                    }
                } finally {
                    rs.close();
                }
                stmt.close();
                con.close();
            } catch (SQLException e) {
                log.error("MMBase -> Error getting a new key number:" + e.toString());
                return 1; // try something.
            }
        } else {
            // no object table yet.
            number = 1;
        }
        return number;
    }

    /**
     * Tells if a table already exists
     * @return true if table exists, false if table doesn't exists
     */
    public boolean created(String tableName) {
        return size(tableName)!=-1;
    }


    /**
     * Return number of entries consisting in given table
     * @param tableName the table that has to be counted
     * @return the number of items the table has
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
            return-1;
        }
    }

    /**
     * Set prepared statement field i with value of key from node
     * @javadoc
     */
    private boolean setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
        throws SQLException {
        int type = node.getDBType(key);
        if (type==FieldDefs.TYPE_INTEGER) {
            stmt.setInt(i, node.getIntValue(key));
        } else if (type==FieldDefs.TYPE_NODE) {
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
                return false;
            }
        } else {
            String tmp=node.getStringValue(key);
            if (tmp!=null) {
                stmt.setString(i, tmp);
            } else {
                stmt.setString(i, "");
            }
        }
        return true;
    }

    /**
     * @javadoc
     */
    public boolean create(MMObjectBuilder bul) {
        return create_real(bul,bul.getTableName());
    }

    /**
     * Will be removed once the xml setup system is done (?)
     * @javadoc
     * @duplicate move to create()
     */
    public boolean create_real(MMObjectBuilder bul,String tableName) {

        // use the builder to get the fields are create a
        // valid create SQL string
        String result=null;

        Vector sfields= (Vector) bul.getFields(FieldDefs.ORDER_CREATE);

        if (sfields!=null) {
            for (Enumeration e=sfields.elements();e.hasMoreElements();) {
                String name=((FieldDefs)e.nextElement()).getDBName();
                FieldDefs def=bul.getField(name);
                if (def.getDBState() != org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
                    if (!bdm || def.getDBType()!=FieldDefs.TYPE_BYTE) {
                        String part=convertXMLType(def);
                        if (result==null) {
                            result=part;
                        } else {
                            result+=", "+part;
                        }
                    }
                }
            }
        }
        if (keySupported) {
            result=getMatchCREATE(tableName) + "( " + parser.getPrimaryKeyScheme()+" ( "+getNumberString()+" ), "+result+" );";
        } else {
            result=getMatchCREATE(tableName)+"( " + result + " );";
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
            return false;
        }
        return true;
    }


    /**
     * @javadoc
     */
    public boolean drop(MMObjectBuilder bul) {
        return(drop_real(bul,bul.getTableName()));
    }

    /**
     * Will be removed once the xml setup system is done (?)
     * @javadoc
     * @duplicate move to create()
     */
    public boolean drop_real(MMObjectBuilder bul,String tableName) {

        if (tableSizeProtection(bul)) return(false);

        String result="drop table "+mmb.baseName+"_"+tableName;
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            stmt.executeUpdate(result);
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("can't drop table "+tableName);
            log.error("XMLDROP="+result);
            log.error(Logging.stackTrace(e));
            return false;
        }
        return true;
    }

    /**
     * @javadoc
     */
    public boolean addField(MMObjectBuilder bul,String fieldname) {
        if (tableSizeProtection(bul)) {
            log.service("Cannot add field to " + bul.getTableName() + " because of tableSizeProtection");
            return false;
        }
        log.info("Starting a addField : "+bul.getTableName()+" field="+fieldname);
        String tableName=bul.getTableName();

	if (created(mmb.baseName+"_"+tableName+"_tmp")) {
        	drop_real(bul,tableName+"_tmp");
	}

        if (create_real(bul,tableName+"_tmp")) {
            log.info("created tmp  table : "+tableName+"_tmp");
            copyJDBCTable(tableName,tableName+"_tmp",bul);
            if (checkJDBCTable(tableName,tableName+"_tmp",bul,fieldname,true)) {
                log.info("checked tmp table passed");
            } else {
                log.error("checked tmp table failed");
                drop_real(bul,tableName+"_tmp");
                return false;
            }
        } else {
            log.error("tmp create table failed");
            return false;
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
                    return false;
                }
                return true;
            } else {
                log.error("create of new table failed : "+bul.getTableName());
                return false;
            }
        }  else {
            log.error("drop of old table failed : "+bul.getTableName());
            return false;
        }
    }

    /**
     * @javadoc
     */
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
                return false;
            }
        } else {
            log.error("tmp create table failed");
            return false;
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
                        return false;
                    }
                } else {
                    log.error("checked new table failed");
                    return false;
                }
                return true;
            } else {
                log.error("create of new table failed : "+bul.getTableName());
                return false;
            }
        }  else {
            log.error("drop of old table failed : "+bul.getTableName());
            return false;
        }
    }

    /**
     * @javadoc
     */
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
                return false;
            }
        } else {
            log.error("tmp create table failed");
            return false;
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
                        return false;
                    }
                } else {
                    log.error("checked new table failed");
                    return false;
                }
                return true;
            } else {
                log.error("create of new table failed : "+bul.getTableName());
                return false;
            }
        }  else {
            log.error("drop of old table failed : "+bul.getTableName());
            return false;
        }
    }

    /**
     * @javadoc
     */
    public boolean updateTable(MMObjectBuilder bul) {
        return false;
    }

    /**
     * @javadoc
     */
    public boolean createObjectTable(String baseName) {
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            if (keySupported) {
                stmt.executeUpdate("create table "+baseName+"_object ("+getNumberString()+" integer not null, otype integer not null, owner VARCHAr(12) not null, "+parser.getPrimaryKeyScheme()+" ("+getNumberString()+"));");
            } else {
                stmt.executeUpdate("create table "+baseName+"_object ("+getNumberString()+" integer not null, otype integer not null, owner VARCHAr(12) not null);");
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.error("can't create table "+baseName+"_object");
            log.error(Logging.stackTrace(e));
        }
        return true;
    }

    /**
     * @javadoc
     */
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
            return "otype integer "+parser.getNotNullScheme();
        } else {
            if (disallowed2allowed.containsKey(name)) {
                name=(String)disallowed2allowed.get(name);
            }
            String result=name+" "+matchType(type,size,notnull);
            if (notnull) result+=" "+parser.getNotNullScheme();
            if (keySupported) {
                if (iskey) result+=" "+parser.getNotNullScheme()+" ,"+parser.getKeyScheme()+ "("+name+")";
            }

            return result;
        }
    }

    /**
     * @javadoc
     */
    protected String matchType(int type, int size, boolean notnull) {
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
        return result;
    }

    /**
     * @javadoc
     */
    String mapSize(String line, int size) {
        int pos=line.indexOf("size");
        if (pos!=-1) {
            String tmp=line.substring(0,pos)+size+line.substring(pos+4);
            return tmp;
        }
        return line;
    }

    /**
     * gets the sytax of the create statement for current database.
     * @javadoc
     */
    public String getMatchCREATE(String tableName) {
        return parser.getCreateScheme()+" "+mmb.baseName+"_"+tableName+" ";
    }

    /**
     * @javadoc
     */
    public Hashtable getReverseHash(Hashtable in) {
        Hashtable out=new Hashtable();
        for (Enumeration e=in.keys();e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = in.get(key);
            out.put(value,key);
        }
        return out;
    }

    /**
     * @javadoc
     */
    public String getDisallowedField(String allowedfield) {
        if (allowed2disallowed.containsKey(allowedfield)) {
            allowedfield=(String)allowed2disallowed.get(allowedfield);
        }
        return allowedfield;
    }

    /**
     * @javadoc
     */
    public String getAllowedField(String disallowedfield) {
        if (disallowed2allowed.containsKey(disallowedfield)) {
            disallowedfield=(String)disallowed2allowed.get(disallowedfield);
        }
        return disallowedfield;
    }

    /**
     * @javadoc
     */
    public MultiConnection getConnection(JDBCInterface jdbc) throws SQLException {
        MultiConnection con=jdbc.getConnection(jdbc.makeUrl());
        return con;
    }

    /**
     * @javadoc
     */
    private void mapDefaultFields(Hashtable disallowed2allowed) {
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

    /**
     * @javadoc
     */
    public String getNumberString() {
        return numberString;
    }

    /**
     * @javadoc
     */
    public String getOTypeString() {
        return otypeString;
    }

    /**
     * @javadoc
     */
    public String getOwnerString() {
        return ownerString;
    }

    /**
     * @javadoc
     */
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
        return true;
    }

    /**
     * @javadoc
     */
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
        return buffer;
    }

    /**
     * @javadoc
     */
    private boolean checkJDBCTable(String tablename,String tmptable,MMObjectBuilder bul) {
        return checkJDBCTable(tablename,tmptable,bul,null,false);
    }

    /**
     * @javadoc
     */
    private boolean checkJDBCTable(String tablename,String tmptable,MMObjectBuilder bul,
                                   String ignorefield,boolean add) {
        // performs several checks to make sure our copy is valid
        // first check the size of each table using a count
        try {
            // get result 1
            int size1=-1;
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select count(*) from "+mmb.baseName+"_"+tablename);
            try {
                if (rs.next()) {
                    size1=rs.getInt(1);
                }
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();

            // get result 2
            int size2=-1;
            con=mmb.getConnection();
            stmt=con.createStatement();
            rs=stmt.executeQuery("select count(*) from "+mmb.baseName+"_"+tmptable);
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
            rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tablename);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colsize1=rsmd.getColumnCount();
            stmt.close();
            con.close();

            // first check the size of each table using a count
            // get result 2
            con=mmb.getConnection();
            stmt=con.createStatement();
            rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tmptable);
            rsmd = rs.getMetaData();
            int colsize2=rsmd.getColumnCount();
            stmt.close();
            con.close();

            if (ignorefield==null) {
                if (colsize1!=colsize2) {
                    log.error("tmp table have same number of cols !!!");
                    return false;
                }
            } else {
                if (add) {
                    if (colsize1!=(colsize2-1)) {
                        log.error("tmp table should have one more cols then org !!!");
                        return false;
                    }
                } else {
                    if (colsize1!=(colsize2+1)) {
                        log.error("tmp table should have one less cols then org !!!");
                        return false;
                    }
                }
            }

            // now lets check the data in the table compare the two
            // get the first data set...
            con=mmb.getConnection();
            stmt=con.createStatement();
            rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tablename);
            rsmd = rs.getMetaData();
            int colcount1=rsmd.getColumnCount();

            MultiConnection con2=mmb.getConnection();
            Statement stmt2=con2.createStatement();
            ResultSet rs2=stmt2.executeQuery("select * from "+mmb.baseName+"_"+tmptable);
            try {
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
                            if (!(value1.toString()).equals((value2.toString()))) {
                                log.error("data check error on field : "+key1);
                                return(false);
                            }
                        }
                    }
                }
            } finally {
                rs.close();
            }
            stmt2.close();
            con2.close();
            stmt.close();
            con.close();
        } catch(SQLException e) {
            log.error(Logging.stackTrace(e));
            return false;
        }
        return true;
    }

    /**
     * @javadoc
     */
    private boolean copyJDBCTable(String tablename,String tmptable,MMObjectBuilder bul) {
        log.info("Starting JBDC copy to temp table");
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from "+mmb.baseName+"_"+tablename);
            try {
                ResultSetMetaData rsmd = rs.getMetaData();
                int colcount=rsmd.getColumnCount();
                StringBuffer fieldAmounts = new StringBuffer();

                Vector newfields= (Vector) bul.getFields(FieldDefs.ORDER_CREATE);
                for (int i=0;i<newfields.size();i++) {
                    fieldAmounts.append(",?");
                }

                while(rs.next()) {
                    MultiConnection con2=mmb.getConnection();
                    PreparedStatement stmt2=con2.prepareStatement("insert into "+mmb.baseName+"_"+tmptable+" values("+fieldAmounts.substring(1)+")");
                    stmt2.setEscapeProcessing(false);
                    Hashtable oldvalues=new Hashtable(colcount);
                    for (int i=1;i<colcount+1;i++) {
                        Object o=rs.getObject(i);
                        String colname=rsmd.getColumnName(i);
                        colname=colname.toLowerCase();
                        oldvalues.put(colname,o);
                    }
                    newfields= (Vector) bul.getFields(FieldDefs.ORDER_CREATE);

                    Object o=oldvalues.get(getAllowedField("number"));
                    stmt2.setInt(1,((Integer)o).intValue());
                    for (Enumeration e=newfields.elements();e.hasMoreElements();) {
                        FieldDefs def=(FieldDefs) e.nextElement();
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
            } finally {
                rs.close();
            }
            stmt.close();
            con.close();
        } catch(SQLException e) {
            log.error(Logging.stackTrace(e));
            return false;
        }
        return true;
    }

    /**
     * @javadoc
     */
    public boolean tableSizeProtection(MMObjectBuilder bul) {
        int size=bul.size();
        if (size>parser.getMaxDropSize()) {
            log.debug("Database table changed not allowed on : "+bul.getTableName());
            log.debug("check <maxdropsize> in your database.xml");
            log.debug("defined max is : "+parser.getMaxDropSize()+" table was : "+size);
            return true;
        }
        return false;
    }
}
