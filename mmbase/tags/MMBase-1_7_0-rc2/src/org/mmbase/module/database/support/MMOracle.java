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

import oracle.sql.BLOB;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * MMSQL92Node implements the MMJdbc2NodeInterface for
 * sql92 types of database this is the class used to abstact the query's
 * needed for mmbase for each database.
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: MMOracle.java,v 1.18 2004-01-27 12:04:48 pierre Exp $
 */
public class MMOracle extends MMSQL92Node implements MMJdbc2NodeInterface {

   /**
    * Single character string used to represent an empty string in the database.
    * Oracle does not distinguish between an empty string and NULL, so to
    * prevent an empty string from being interpreted as NULL, it is replaced
    * by this string when writing to the database, and viceversa when
    * reading from the database.
    * Without these measures Oracle will complain when trying to insert an
    * empty string into a fields that was declared NOT NULL.
    */
   private final static String EMPTY_STRING = " ";

   /**
    * Logging instance
    */
   private static Logger log = Logging.getLoggerInstance(MMOracle.class.getName());

   public String name="oracle";
   XMLDatabaseReader parser;
   Hashtable typeMapping = new Hashtable();
   Hashtable disallowed2allowed;
   Hashtable allowed2disallowed;
   //does the database support keys?
   private boolean keySupported=false;
   private String numberString;
   private String otypeString;
   private String ownerString;

   MMBase mmb;

   public MMOracle() {
   }

   public void init(MMBase mmb,XMLDatabaseReader parser) {
       this.mmb=mmb;
       this.parser=parser;

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

   public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i) {
      return(decodeDBnodeField(node,fieldname,rs,i,""));
   }

   public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldname, ResultSet rs,int i,String prefix) {
      try {
         // fix for oracle, can be done faster by mapping the allowed2dis		// in high caps
         fieldname=fieldname.toLowerCase();


         // is this fieldname disallowed ? ifso map it back
         if (allowed2disallowed.containsKey(fieldname)) {
            fieldname=(String)allowed2disallowed.get(fieldname);
         }

         int type=node.getDBType(prefix+fieldname);
         switch (type) {
            case FieldDefs.TYPE_XML:
            case FieldDefs.TYPE_STRING:
               String tmp;
               ResultSetMetaData metaData = rs.getMetaData();
               if (metaData.getColumnType(i) == Types.CLOB) {
                  // Retrieve from CLOB.
                  tmp = getDBText(rs, i);
               } else {
                  // Retrieve from textfield.
                  tmp = rs.getString(i);
               }
               if (tmp == null || tmp.equals(EMPTY_STRING)) {
                  node.setValue(prefix + fieldname,"");
               } else {
                  node.setValue(prefix + fieldname,tmp);
               }
               break;

            case FieldDefs.TYPE_NODE:
            case FieldDefs.TYPE_INTEGER:
               //node.setValue(prefix+fieldname,(Integer)rs.getObject(i));
               node.setValue(prefix+fieldname,(rs.getBigDecimal(i)).intValue());
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
         log.error("MMSQL92Node mmObject->"+fieldname+" node="+node.getIntValue("number"));
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
         // if (log.isDebugEnabled()) log.trace("CMD="+cmd+" PART="+part);
         // do we have a type prefix (example episodes.title==) ?
         int pos=part.indexOf('.');
         if (pos!=-1) {
            part=part.substring(pos+1);
         }
         // if (log.isDebugEnabled()) log.trace("PART="+part);

         // remove fieldname  (example title==) ?
         pos=part.indexOf('=');
         if (pos!=-1) {
            String fieldname=part.substring(0,pos);
            int dbtype=bul.getDBType(fieldname);
            //if (log.isDebugEnabled()) log.trace("TYPE="+dbtype);
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
         ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where "+getNumberString()+"="+number);
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
      try {
         byte[] result=null;
         MultiConnection con=mmb.getConnection();
         Statement stmt=con.createStatement();
         ResultSet rs=stmt.executeQuery("SELECT "+fieldname+" FROM "+mmb.baseName+"_"+tableName+" where "+getNumberString()+"="+number);
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
      return(null);
   }


   /**
    * get byte of a database blob
    */
   public byte[] getDBByte(ResultSet rs,int idx) {
      byte[] returndata = null;
      try {
         BufferedInputStream bis = null;
         ByteArrayOutputStream baos = null;
         try {
            // The ByteArrayOutputStream buffers all bytes written to it
            // until we call getBytes() which returns to us an
            // array of bytes:
            baos = new ByteArrayOutputStream();
            // Create an input stream from the BLOB column.
            // By default, rs.getBinaryStream()
            // returns a vanilla InputStream instance.
            // We override this for efficiency
            // but you don't have to:
            bis = new BufferedInputStream( rs.getBinaryStream(idx) );

            // Make sure its not a NULL value in the column:
            // rs.wasNull() reports whether the last column read had a
            // value of SQL NULL.
            if ( !rs.wasNull() ) {

               // A temporary buffer for the byte data:
               byte[] bindata= new byte[1024];
               // Used to return how many bytes are read with each read() of the input stream:
               int bytesread = 0;

               while ( (bytesread = bis.read(bindata,0,bindata.length)) != -1 ) {
                  // Write out 'bytesread' bytes to the writer instance:
                  baos.write(bindata,0,bytesread);
               }
               // When the read() method returns -1 we've hit the end of
               // the stream,
               // so now we can get our bytes out of the writer object:
               returndata = baos.toByteArray();
            }
         }
         finally {
            // Close the binary input stream:
            if (bis != null) {
               bis.close();
            }
            // Close the byte array output stream:
            if (baos != null) {
               baos.close();
            }
         }
      } catch (Exception e) {
         log.error("MMObjectBuilder -> MMMysql byte  exception "+e);
         log.error(Logging.stackTrace(e));
      }
      return returndata;
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

      try {
         Clob c=rs.getClob(idx);
         inp=c.getAsciiStream();
         if (inp==null) {
            log.warn("MMObjectBuilder -> MMysql42Node DBtext no ascii "+inp);
            return("");
         }
         if (rs.wasNull()) {
            log.warn("MMObjectBuilder -> MMysql42Node DBtext wasNull "+inp);
            return("");
         }
         //siz=inp.available(); // DIRTY
         siz=(int)c.length();

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

      ArrayList blobFields = new ArrayList();

      // Create a String that represents the amount of DB fields to be used in the insert.
      // First add an field entry symbol '?' for the 'number' field since it's not in the sortedDBLayout vector.
      String fieldAmounts="?";

      // Append the DB elements to the fieldAmounts String.
      for (Enumeration e=bul.sortedDBLayout.elements();e.hasMoreElements();) {
         String key = (String)e.nextElement();
         int DBState = node.getDBState(key);
         if ( (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_PERSISTENT)
         || (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_SYSTEM) ) {


            //blobs need special treatment in oracle see top of file
            if (node.getDBType(key) == FieldDefs.TYPE_BYTE) {
               fieldAmounts+=", EMPTY_BLOB() ";
               blobFields.add(key);
            }
            else {
               fieldAmounts+=",?";
            }
         } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
            if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", skipping key: "+key);
         } else {

            if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {
               //blobs need special treatment in oracle see top of file
               if (node.getDBType(key) == FieldDefs.TYPE_BYTE) {
                  fieldAmounts+=", EMPTY_BLOB() ";
                  blobFields.add(key);
               }
               else {
                  fieldAmounts+=",?";
               }
            } else {
               log.warn("Insert: DBState = "+DBState+" unknown!, skipping key: "+key+" of builder:"+node.getName());
            }
         }
      }

      MultiConnection con=null;
      PreparedStatement stmt=null;
      try {
         // Prepare the statement using the amount of fields found.
         if (log.isDebugEnabled()) log.trace("Insert: Preparing statement "+mmb.baseName+"_"+tableName+" using fieldamount String: "+fieldAmounts);
         con=bul.mmb.getConnection();
         stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values("+fieldAmounts+")");
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

               //blobs need special treatment in oracle see top of file
               if (node.getDBType(key) != FieldDefs.TYPE_BYTE) {
                  if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", adding key: "+key + " value " + node.getStringValue(key)+", at pos:"+j);
                  setValuePreparedStatement( stmt, node, key, j );
                  j++;
               }
               else {
                  if (log.isDebugEnabled()) log.trace("Skip blob: DBState = "+DBState+", for key: "+key);
               }
            } else if (DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_VIRTUAL) {
               if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", skipping setValue for key: "+key);
            } else {
               if ((DBState == org.mmbase.module.corebuilders.FieldDefs.DBSTATE_UNKNOWN) && node.getName().equals("typedef")) {

                  //blobs need special treatment in oracle see top of file
                  if (node.getDBType(key) != FieldDefs.TYPE_BYTE) {
                     if (log.isDebugEnabled()) log.trace("Insert: DBState = "+DBState+", adding key: "+key + " value " + node.getStringValue(key)+", at pos:"+j);
                     setValuePreparedStatement( stmt, node, key, j );
                     j++;
                  }
               } else {
                  log.warn("Insert: DBState = "+DBState+" unknown!, skipping setValuePreparedStatement for key: "+key+" of builder:"+node.getName());
               }
            }
         }
         stmt.executeUpdate();
         stmt.close();
         con.close();
      } catch (SQLException e) {
         log.error("Error on : "+number+" "+owner+" fake "+tableName+" NODE="+node);
         log.error(Logging.stackTrace(e));
         try {
            stmt.close();
            con.close();
         } catch(Exception t2) {}
         return(-1);
      }

      if ( !blobFields.isEmpty() ) {
         try {
            updateBlobFields(node, tableName, number, blobFields);
         } catch (SQLException e) {
            log.error("Error on blob: "+number+" fake");
            log.error(Logging.stackTrace(e));
            return(-1);
         }
      }

      if (node.parent!=null && (node.parent instanceof InsRel) && !tableName.equals("insrel")) {
         try {
            con=mmb.getConnection();
            if (InsRel.usesdir) {
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
      node.setValue("number",number);
      node.clearChanged();
      //if (log.isDebugEnabled()) log.trace("INSERTED="+node);
      return(number);
   }

   /**
    * update Blob Fields.
    * THis method updates the blob fields of a row in the DB.
    * The insert and update for the other fields than BLOBS should be done by
    * the calling method.
    * This method locks the row with an select sql statement and updates the blob
    *
    * @param node node with the blobs to update
    * @param tableName name of table where the blbos should be stored
    * @param number number of node
    * @param blobFields array of blob field names
    * @throws SQLException
    */
   private void updateBlobFields(
   MMObjectNode node,
   String tableName,
   int number,
   ArrayList blobFields)
   throws SQLException {
      MultiConnection con = null;
      PreparedStatement stmt = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean oldAutoCommit = true;
      try {
         con = mmb.getConnection();
         oldAutoCommit = con.getAutoCommit();
         con.setAutoCommit(false);

         String sqlBlobLock =
         "SELECT * FROM " + mmb.baseName + "_" + tableName;
         String sqlBlobUpdate =
         "UPDATE " + mmb.baseName + "_" + tableName + " SET ";

         Iterator blobIter = blobFields.iterator();
         while (blobIter.hasNext()) {
            String fieldname = ((String) blobIter.next()).toLowerCase();
            // is this fieldname disallowed ? ifso map it back
            if (allowed2disallowed.containsKey(fieldname)) {
               fieldname = (String) allowed2disallowed.get(fieldname);
            }
            sqlBlobUpdate = sqlBlobUpdate + fieldname + " = ? ";
            if (blobIter.hasNext()) {
               sqlBlobUpdate = sqlBlobUpdate + ", ";
            }
         }

         String numbername = getNumberString();
         sqlBlobLock =
         sqlBlobLock + " WHERE " + numbername + " = ? FOR UPDATE";
         sqlBlobUpdate = sqlBlobUpdate + " WHERE " + numbername + " = ?";

         if (log.isDebugEnabled()) log.trace("SQL Blob Lock = " + sqlBlobLock);

         stmt = con.prepareStatement(sqlBlobLock);
         stmt.setInt(1, number);

         rs = stmt.executeQuery();
         if (rs.next()) {
            if (log.isDebugEnabled()) log.trace("SQL Blob Update = " + sqlBlobUpdate);
            ps = con.prepareStatement(sqlBlobUpdate);

            int updateParam = 1;
            Iterator blob2Iter = blobFields.iterator();
            while (blob2Iter.hasNext()) {
               String key = ((String) blob2Iter.next());
               String fieldname = key.toLowerCase();
               // is this fieldname disallowed ? ifso map it back
               if (allowed2disallowed.containsKey(fieldname)) {
                  fieldname = (String) allowed2disallowed.get(fieldname);
               }
               //Use oracle.sql.BLOB because java.sql.Blob lacks setBytes()
               //JDBC3 java.sql.Blob adds the method setBytes(int,byte[])
               //Oracle JDBC uses the method putBytes(int,byte[])
               BLOB dbBlob =
               (BLOB) rs.getBlob(fieldname);
               if (log.isDebugEnabled()) log.trace("Blob Update = " + fieldname);
               //update blob
               dbBlob.putBytes(1, node.getByteValue(key));
               ps.setBlob(updateParam, dbBlob);

               updateParam++;
            }
            ps.setInt(updateParam, number);
            ps.executeUpdate();
         }
      }
      finally {
         if (rs != null) {
            rs.close();
         }
         if (ps != null) {
            ps.close();
         }
         if (stmt != null) {
            stmt.close();
         }
         if (con != null) {
            con.commit();
            con.setAutoCommit(oldAutoCommit);
            con.close();
         }
      }

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
         return;
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
      String values="";
      String key;

      int number=node.getIntValue("number");
      // did it fail ? ifso exit
      if (number == -1) return(false);

      ArrayList blobFields = new ArrayList();

      // create the prepared statement
      for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
         key=(String)e.nextElement();
         // a extra check should be added to filter temp values
         // like properties

         if (node.getDBType(key)!=FieldDefs.TYPE_BYTE) {
            // is this key disallowed ? ifso map it back
            if (disallowed2allowed.containsKey(key)) {
               key=(String)disallowed2allowed.get(key);
            }

            // check if its the first time for the ',';
            if (values.equals("")) {
               values+=" "+key+"=?";
            } else {
               values+=", "+key+"=?";
            }
         }
         else {
            blobFields.add(key);
         }
      }

      if (values.length()>0) {
         values="update "+mmb.baseName+"_"+bul.tableName+" set"+values+" WHERE "+getNumberString()+"="+number;
         try {
            MultiConnection con=mmb.getConnection();
            PreparedStatement stmt=con.prepareStatement(values);
            int type;int i=1;
            for (Enumeration e=node.getChanged().elements();e.hasMoreElements();) {
               key=(String)e.nextElement();
               if (log.isDebugEnabled()) log.trace("Update key: "+key + " value " + node.getStringValue(key)+", at pos:"+i);
               type=node.getDBType(key);
               if (type==FieldDefs.TYPE_INTEGER) {
                  stmt.setInt(i,node.getIntValue(key));
               } else if (type==FieldDefs.TYPE_NODE) {
                  stmt.setInt(i,node.getIntValue(key));
               } else if (type==FieldDefs.TYPE_FLOAT) {
                  stmt.setFloat(i,node.getFloatValue(key));
               } else if (type==FieldDefs.TYPE_DOUBLE) {
                  stmt.setDouble(i,node.getDoubleValue(key));
               } else if (type==FieldDefs.TYPE_LONG) {
                  stmt.setLong(i,node.getLongValue(key));
               } else if (type==FieldDefs.TYPE_XML) {
                  setDBText(i,stmt,node.getStringValue(key));
               } else if (type==FieldDefs.TYPE_STRING) {
                  setDBText(i,stmt,node.getStringValue(key));
               } else if (type==FieldDefs.TYPE_BYTE) {

                  // There are size restrictions on Oracle SQL statements
                  // the longest SQL expression is only about 64 kb which means
                  // you can only insert about 32 kb using SQL. I think.
                  // But you can always write the data using the LOB
                  // interface, but this is messier to make transparent.
                  // So lets do it with the LOB interface and forget we've been here

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

      //uodate the blobs with the LOB interface
      if ( !blobFields.isEmpty() ) {
         try {
            updateBlobFields(node, bul.tableName, number, blobFields);
         } catch (SQLException e) {
            log.error("Error on blob: "+number+" fake");
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
            mmb.mmc.changedNode(n1.getIntValue("number"),n1.getName(),"r");
            mmb.mmc.changedNode(n2.getIntValue("number"),n2.getName(),"r");
         } else {
            mmb.mmc.changedNode(node.getIntValue("number"),bul.tableName,"c");
         }
      }
      return(true);
   }

   /**
    * checks if numbertable exists.
    * If not this method will create one.
    * And inserts the DBKey retrieve by getDBKeyOld
    */
   private void checkNumberTable() {
      if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> checks if table numberTable exists.");
      if(!created(mmb.baseName+"_numbertable")) {
         // We want the current number of object, not next number (that's the -1)
         int number = getDBKeyOld()-1;

         if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> Creating table numbertable and inserting row with number "+number);
         String createStatement = getMatchCREATE("numbertable")+"( "+getNumberString()+" int not null)";
         if (log.isDebugEnabled()) log.trace("create="+createStatement);
         try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            stmt.executeUpdate(createStatement);
            stmt.executeUpdate("insert into "+mmb.baseName+"_numbertable ("+getNumberString()+") values("+number+")");
            stmt.close();
            con.close();
         } catch (SQLException e) {
            log.error("MMSQL92NODE -> Wasn't able to create numbertable table.");
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
         //stmt.executeUpdate("lock tables "+mmb.baseName+"_numbertable WRITE;");
         stmt.executeUpdate("update "+mmb.baseName+"_numbertable set "+getNumberString()+" = "+getNumberString()+"+1");
         ResultSet rs=stmt.executeQuery("select "+getNumberString()+" from "+mmb.baseName+"_numbertable");
         while(rs.next()) {
            number=rs.getInt(1);
         }
         // not part of sql92, please find new trick (daniel)
         // stmt.executeUpdate("unlock tables;");
         stmt.close();
         con.close();
      } catch (SQLException e) {
         log.error(Logging.stackTrace(e));
         log.error("MMSQL92NODE -> SERIOUS ERROR, Problem with retrieving DBNumber from databse");
      }
      if (log.isDebugEnabled()) log.trace("MMSQL92NODE -> retrieving number "+number+" from the database");
      return (number);
   }

   public synchronized int getDBKeyOld() {
      int number=-1;
      try {
         MultiConnection con=mmb.getConnection();
         Statement stmt=con.createStatement();
         ResultSet rs=stmt.executeQuery("select max("+getNumberString()+") from "+mmb.getBaseName()+"_object");
         if (rs.next()) {
            number=rs.getInt(1);
            number++;
         } else {
            number=1;
         }
         stmt.close();
         con.close();
      } catch (SQLException e) {
         log.error("MMBase -> while getting a new key number");
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
         ResultSet rs=stmt.executeQuery("SELECT count(*) FROM "+tableName);
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
   private void setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
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
      } else if (type==FieldDefs.TYPE_STRING) {
         String tmp=node.getStringValue(key);
         // replace null or empty string by single space,
         // an empty string is interpreted as NULL by Oracle.
         if (tmp == null || tmp.length() == 0) {
            tmp = EMPTY_STRING;
         }
         setDBText(i, stmt,tmp);
      } else if (type==FieldDefs.TYPE_BYTE) {

         // There are size restrictions on Oracle SQL statements
         // the longest SQL expression is only about 64 kb which means
         // you can only insert about 32 kb using SQL.
         // I think. But you can always write the data using the LOB
         // interface, but this is messier to make transparent.
         // So lets do it with the LOB interface and forget we've been here

         //    setDBByte(i, stmt, node.getByteValue(key));
      } else {
         String tmp=node.getStringValue(key);
         if (tmp == null || tmp.length() == 0) {
            tmp = EMPTY_STRING;
         }
         stmt.setString(i, tmp);
      }
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
               String part=convertXMLType(def);
               if (result==null) {
                  result=part;
               } else {
                  result+=", "+part;
               }
            }
         }
      }
      if (keySupported) {
         result=getMatchCREATE(tableName)+"( "+getNumberString()+" int not null, "+parser.getPrimaryKeyScheme()+" ( "+getNumberString()+" ), "+result+" )";
      } else {
         result=getMatchCREATE(tableName)+"( "+getNumberString()+" int not null, "+result+" )";
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

      int size=bul.size();
      if (size>0) {
         log.error("table not dropped, not empty : "+tableName);
         return(false);
      }

      String result="drop table "+mmb.baseName+"_"+tableName;
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

   public boolean updateTable(MMObjectBuilder bul) {
      log.info("Starting a updateTable on : "+bul.getTableName());

      String tableName=bul.getTableName();

      if (create_real(bul,tableName+"_tmp")) {
         log.info("created tmp  table : "+tableName+"_tmp");
            /*
            Enumeration e=bul.search("");
            while (e.hasMoreElements()) {
                MMObjectNode node=(MMObjectNode)e.nextElement();
                insert_real(bul,node.get
                if (log.isDebugEnabled()) log.trace("node="+node);
            }
             */
      } else {
      }

      if (drop(bul)) {
         log.info("drop of old table done : "+bul.getTableName());
         if (create(bul)) {
            log.info("create of new table done : "+bul.getTableName());
            if (drop_real(bul,tableName+"_tmp")) {
               log.info("dropping tmp  table : "+tableName+"_tmp");
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



   public boolean createObjectTable(String baseName) {
      try {
         MultiConnection con=mmb.getConnection();
         Statement stmt=con.createStatement();
         if (keySupported) {
            stmt.executeUpdate("create table "+baseName+"_object ("+getNumberString()+" int not null, otype int not null, owner VARCHAr(12) not null, "+parser.getPrimaryKeyScheme()+" ("+getNumberString()+"))");
         } else {
            stmt.executeUpdate("create table "+baseName+"_object ("+getNumberString()+" int not null, otype int not null, owner VARCHAr(12) not null)");
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
         return("otype int "+parser.getNotNullScheme());
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


   protected String matchType(int type, int size, boolean notnull) {
      String result=null;
      if (typeMapping!=null) {
         dTypeInfos  typs=(dTypeInfos)typeMapping.get(new Integer(type));
         if (typs!=null) {
            for (Enumeration e=typs.maps.elements();e.hasMoreElements();) {
               dTypeInfo typ = (dTypeInfo)e.nextElement();
               //if (log.isDebugEnabled()) log.trace("WWW="+size+" "+typ.minSize+" "+typ.maxSize+typ.dbType+" "+typs.maps.size());
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

   public String getNumberString() {
      return(numberString);
   }

   public String getOTypeString() {
      return(otypeString);
   }

   public String getOwnerString() {
      return(ownerString);
   }

}
