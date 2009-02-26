package com.finalist.cmsc.dataconversion.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.dataconversion.service.Constants;
import com.finalist.cmsc.dataconversion.service.Data;

/**
 * used for Data accessing 
 * @author Neil_Gong
 *
 */
public class DataAccessor {
   
   private static final Logger log = Logging.getLoggerInstance(DataAccessor.class.getName());
   
   public static String encoding;
   private DataSource dataSource;   
   private ElementMeta elementMeta;   
   private Query query;   
   private static final String URL_PROTOCOL = "http";
   
   public static final String DATATYPE_NUMBER = "datatype";
   public static final String SELFREL_NUMBER = "selfrelate";
   public static final String RELACTION_NUMBER = "relation";
   public static final String INT_TO_DATA = "intToData";
   public static final String BLOB_TO_STRING = "blobToString";
   
   public DataAccessor(DataSource dataSource,ElementMeta elementMeta) {
      
      this.dataSource = dataSource;
      this.elementMeta = elementMeta;
      query = new Query();
   }   
   
   public DataAccessor(DataSource dataSource) {
      this.dataSource = dataSource;
      query = new Query();
   }

   /**
    *  get  numbers 
    * @param type 
    * @return key number
    * @throws Exception
    */
   public HashMap<Integer,Integer> getNumbers(String type) throws Exception {
      Connection connection = null;
      Statement statement = null;
      ResultSet rs = null;
      String sql = null;
      HashMap<Integer,Integer> numbers =  new HashMap<Integer,Integer>();
      try {
         connection = dataSource.getConnection();
         statement = connection.createStatement();
         if(type.equals(DATATYPE_NUMBER)) {
            sql = query.getPrimaryKeysQueryString(elementMeta);
         }
         else if(type.equals(SELFREL_NUMBER)) {
            sql = query.getSelfRelQueryString(elementMeta);
         }
         else if(type.equals(RELACTION_NUMBER)) {
            if(elementMeta.getDesTableName().equalsIgnoreCase("article")) {
               sql = query.getNumbersQueryOfArticle(elementMeta);
            }
            else {
               sql = query.getNumbersQueryString(elementMeta);
            }
         }
         rs = statement.executeQuery(sql);
         while(rs.next()) {
            numbers.put(rs.getInt(1),null);
         }
      } 
      catch (SQLException e) {
         log.error(" get numbers failure!"+e.getMessage());
      }
      finally {
         release(statement,connection);
      }
      return numbers;
   } 
   
   public HashMap<Integer,Integer> addChildRelation() throws Exception {
      Connection connection = null;
      Statement statement = null;
      ResultSet rs = null;
      HashMap<Integer,Integer> numbers =  new HashMap<Integer,Integer>();
      try {
         connection = dataSource.getConnection();
         statement = connection.createStatement();
         rs = statement.executeQuery(query.getRootChildNodeQuery(elementMeta));
         while(rs.next()) {
            numbers.put(rs.getInt(1),null);
         }
      } 
      catch (SQLException e) {
         log.error(" addChildRelation failure!"+e.getMessage());
      }
      finally {
         release(statement,connection);
      }
      return numbers;
   }
      
   public DataHolder getElementByPrimaryKey(Integer key,Byte type) throws Exception {
      Connection connection = null;
      Statement statement = null;
      ResultSet rs = null;
      ResultSet relRs = null;
      DataHolder holder = new DataHolder();
      List<Elements> list = new ArrayList<Elements>();
    
      try {
         connection = dataSource.getConnection();
         statement = connection.createStatement();
         if(type == Constants.ENTITY_TYPE) {
            rs = statement.executeQuery(query.getQueryString(elementMeta,key));
            parseResultSet(rs,list,elementMeta);
            holder.setCollection(list);
            holder.setTableName(elementMeta.getDesTableName());

         }
         else if(type == Constants.RELATION_TYPE||type==Constants.RELATION_DATA_TYPE){//add new type
            if(StringUtils.isNotEmpty(query.getRelateFieldsString(elementMeta,key))) {
               relRs = statement.executeQuery(query.getRelateFieldsString(elementMeta,key));
               parse(relRs,list,elementMeta,holder);
               holder.setCollection(list);
               holder.setTableName(elementMeta.getDestinationRelTableName());
            }
         }
         else if(type == Constants.SELF_RELATION_TYPE){
            relRs = statement.executeQuery(query.getSelfRelFieldString(elementMeta,key));
            parse(relRs,list,elementMeta,holder);
            holder.setCollection(list);
            holder.setTableName(elementMeta.getSelfRelDesTableName());
         }
         else {
            relRs = statement.executeQuery(query.getSelfRelFieldString(elementMeta,key));
            parse(relRs,list,elementMeta,holder);
            holder.setCollection(list);
            holder.setTableName(elementMeta.getSelfRelDesTableName()); 
         }
      } 
      catch (SQLException e) {
         log.error("get element failure!"+e.getMessage());
      }
      finally {
         release(statement,connection);
      }
      
      return holder;
   }
   
   private void parseResultSet(ResultSet rs,List<Elements> list,ElementMeta elementMeta) throws SQLException {
      
      if(rs == null) {
         return;
      }
      while(rs.next()) {
         Elements element = new Elements();
         for(String fieldName : elementMeta.getFieldNames()) {
            if(StringUtils.isNotEmpty(fieldName)) {
               if(StringUtils.isEmpty(elementMeta.getStyle(fieldName))&&StringUtils.isEmpty(elementMeta.getPrefix(fieldName)) || (encoding(rs, fieldName) != null && encoding(rs, fieldName).toString().startsWith(URL_PROTOCOL))) {
                  element.setValue(elementMeta.getDesFieldName(fieldName),encoding(rs, fieldName));
               }
               else if(!StringUtils.isEmpty(elementMeta.getPrefix(fieldName))){
                  element.setValue(elementMeta.getDesFieldName(fieldName),elementMeta.getPrefix(fieldName)+encoding(rs, fieldName));
               }else if(!StringUtils.isEmpty(elementMeta.getStyle(fieldName))){
                  String changeMethod=elementMeta.getStyle(fieldName);
                  Object o=changeStyle(changeMethod,rs,fieldName);
                  if(null!=o){
                     element.setValue(elementMeta.getDesFieldName(fieldName),o);
                  }
               }
            }
         }
         list.add(element);
      }
   }

   private Object encoding(ResultSet rs, String fieldName) throws SQLException {
      Object obj=rs.getObject(fieldName);
      if (obj instanceof String&&StringUtils.isNotBlank(encoding)) {
         try {
            String untrimmedResult = new String(((String) obj).getBytes(encoding), "utf-8");
            return untrimmedResult;
         } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      return obj;
   }
   
   private Object changeStyle(String changeMethod, ResultSet rs, String fieldName) {
      try {
         if (INT_TO_DATA.equals(changeMethod)) {
            int i=rs.getInt(fieldName);
            if (i>0) {
               String redate = String.valueOf(i)+"000";
               Long l=Long.parseLong(redate);
               return new java.sql.Date(l);
            }
            return new java.sql.Date(i);
         } else if (BLOB_TO_STRING.equals(changeMethod)) {
            Blob blob = rs.getBlob(fieldName);
            InputStream inStream = blob.getBinaryStream();
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            try {
               for (int n; (n = inStream.read(b)) != -1;) {
                  out.append(new String(b, 0, n, "utf-8"));
               }
            } catch (IOException e) {
               log.error("changeMethod failure!"+changeMethod+"can't make datatype chnge!"+e.getMessage());
               e.printStackTrace();
            }
            String s=out.toString();
            if("0".equals(s.trim())||"-".equals(s.trim())){
               return false;
            }
            if("1".equals(s.trim())){
               return true;
            }
            return s;
         }
      } catch (SQLException e) {
         log.error("changeMethod failure!"+changeMethod+"can't make datatype chnge!"+e.getMessage());
      }
      return null;
   }

   private void parse(ResultSet rs,List<Elements> list,ElementMeta elementMeta,DataHolder holder) throws SQLException {

      if(rs == null) {
         return;
      }
      while(rs.next()) {
         Elements element = new Elements();
         for(String fieldName : elementMeta.getRelateFields()) {
            if(StringUtils.isNotEmpty(fieldName)) {
               if(StringUtils.isEmpty(elementMeta.getPrefix(fieldName))  || (encoding(rs, fieldName) != null && encoding(rs, fieldName).toString().startsWith(URL_PROTOCOL))) {
                  element.setValue(elementMeta.getDesFieldName(fieldName),encoding(rs, fieldName));
               }
               else {
                  element.setValue(elementMeta.getDesFieldName(fieldName),elementMeta.getPrefix(fieldName)+encoding(rs, fieldName));
               }
            }
         }
         list.add(element);
         holder.setSnumber(rs.getInt("snumber"));
         holder.setDnumber(rs.getInt("dnumber"));
      }
   }
   
   private void release(Statement statement, Connection connection) {
      if(statement != null) {
         try {
            statement.close();
         } 
         catch (SQLException e) {
            log.error("Statement close failure!"+e.getMessage());
         }
      }
      if(connection != null) {
         try {
            connection.close();
         } 
         catch (SQLException e) {
            log.error("Connection close failure!"+e.getMessage());
         }
      }
   }

   public List<String> getResOfRelation(Data relData) {
      Connection connection = null;
      Statement statement = null;
      ResultSet rs = null;
      String sql = null;
      int sv = 0, dv = 0;
      List<String> numbers = new ArrayList<String>();
      try {
         connection = dataSource.getConnection();
         statement = connection.createStatement();
         if ("sourcetype".equals(relData.getReverse())) {
            sql = "select * from " + " " + relData.getSourceRelationType() + " "
                  + "where snumber in (select number from " + relData.getTableName()
                  + ") and dnumber in (select number from " +  relData.getRelateTable()+ ")";
         }else{
            sql = "select * from " + " " + relData.getSourceRelationType() + " "
            + "where snumber in (select number from " + relData.getRelateTable()
            + ") and dnumber in (select number from " + relData.getTableName() + ")";
         }
         rs = statement.executeQuery(sql);
         while (rs.next()) {
            sv = rs.getInt("snumber");
            dv = rs.getInt("dnumber");
            if (sv != 0 && dv != 0) {
               String s = String.valueOf(sv);
               String d = String.valueOf(dv);
               numbers.add(s + "," + d);
            }
         }
      } catch (SQLException e) {
         log.error(" get Rel numbers failure!" + e.getMessage());
      } finally {
         release(statement, connection);
      }
      return numbers;
   }

   public ArrayList<Integer> getPrimerKeyList(String sourcetype) {
      Connection connection = null;
      Statement statement = null;
      ResultSet rs = null;
      String sql = null;
      int sv = 0;
      ArrayList<Integer> numbers = new ArrayList<Integer>();
      try {
         connection = dataSource.getConnection();
         statement = connection.createStatement();
         sql = "select * from " + " " + sourcetype;
         rs = statement.executeQuery(sql);
         while (rs.next()) {
            sv = rs.getInt("number");
            numbers.add(sv);
         }
      } catch (SQLException e) {
         log.error(" get Rel numbers failure!" + e.getMessage());
      } finally {
         release(statement, connection);
      }
      return numbers;
   }
}
