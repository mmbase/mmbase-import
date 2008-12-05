package com.finalist.cmsc.dataconversion.dataaccess;

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

/**
 * used for Data accessing 
 * @author Neil_Gong
 *
 */
public class DataAccessor {
   
   private static final Logger log = Logging.getLoggerInstance(DataAccessor.class.getName());
     
   private DataSource dataSource;   
   private ElementMeta elementMeta;   
   private Query query;   
   private static final String URL_PROTOCOL = "http";
   
   public static final String DATATYPE_NUMBER = "datatype";
   public static final String SELFREL_NUMBER = "selfrelate";
   public static final String RELACTION_NUMBER = "relation";
   
   public DataAccessor(DataSource dataSource,ElementMeta elementMeta) {
      
      this.dataSource = dataSource;
      this.elementMeta = elementMeta;
      query = new Query();
   }   
   
   /**
    *  get  numbers 
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
         else if(type == Constants.RELATION_TYPE){
            if(StringUtils.isNotEmpty(query.getRelateFieldsString(elementMeta,key))) {
               relRs = statement.executeQuery(query.getRelateFieldsString(elementMeta,key));
               parse(relRs,list,elementMeta,holder,key);
               holder.setCollection(list);
               holder.setTableName(elementMeta.getDestinationRelTableName());
            }
         }
         else if(type == Constants.SELF_RELATION_TYPE){
            relRs = statement.executeQuery(query.getSelfRelFieldString(elementMeta,key));
            parse(relRs,list,elementMeta,holder,key);
            holder.setCollection(list);
            holder.setTableName(elementMeta.getSelfRelDesTableName());
         }
         else {
            relRs = statement.executeQuery(query.getSelfRelFieldString(elementMeta,key));
            parse(relRs,list,elementMeta,holder,key);
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
      
      if(rs == null) return;
      while(rs.next()) {
         Elements element = new Elements();
         for(String fieldName : elementMeta.getFieldNames()) {
            if(StringUtils.isNotEmpty(fieldName)) {
               if(StringUtils.isEmpty(elementMeta.getPrefix(fieldName)) || (rs.getObject(fieldName) != null && rs.getObject(fieldName).toString().startsWith(URL_PROTOCOL))) {
                  element.setValue(elementMeta.getDesFieldName(fieldName),rs.getObject(fieldName));
               }
               else {
                  element.setValue(elementMeta.getDesFieldName(fieldName),elementMeta.getPrefix(fieldName)+rs.getObject(fieldName));
               }
            }
         }
         list.add(element);
      }
   }
   
   private void parse(ResultSet rs,List<Elements> list,ElementMeta elementMeta,DataHolder holder,Integer key) throws SQLException {

      if(rs == null) return;
      while(rs.next()) {
         Elements element = new Elements();
         for(String fieldName : elementMeta.getRelateFields()) {
            if(StringUtils.isNotEmpty(fieldName)) {
               if(StringUtils.isEmpty(elementMeta.getPrefix(fieldName))  || (rs.getObject(fieldName) != null && rs.getObject(fieldName).toString().startsWith(URL_PROTOCOL))) {
                  element.setValue(elementMeta.getDesFieldName(fieldName),rs.getObject(fieldName));
               }
               else {
                  element.setValue(elementMeta.getDesFieldName(fieldName),elementMeta.getPrefix(fieldName)+rs.getObject(fieldName));
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
}
