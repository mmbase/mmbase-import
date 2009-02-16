package com.finalist.cmsc.dataconversion.dataaccess;

import org.apache.commons.lang.StringUtils;

/**
 * create SQL query String
 * @author Neil_Gong
 *
 */
public class Query {

   private static final String SQL_SELECT = "select ";
   
   private static final String SQL_FROM = " from ";
   
   private static final String SQL_SELECT_COUNT = "select count(*) ";
   
   private static final String SQL_LIMIT = " limit ";
   
   private static final String SQL_OFFSET = " offset ";
   
   private static final String SQL_NUMBER_FIELD = " number ";
   
   private static final String SQL_WHERE = " where ";
   
   private static final String SQL_SNUMBER = "snumber";
   
   private static final String SQL_DNUMBER = "dnumber";
   private static final String SQL_AND = " and ";
   private static final String SQL_IN = " in ";
   
   public String getAllQueryString(ElementMeta elementMeta) throws Exception {
      
      if(elementMeta.getSourceTableName() == null ) {
         throw new NullPointerException("The table name is null");
      } 
      
      if(elementMeta.getFieldNames() == null || elementMeta.getFieldNames().length < 1) {
         throw new NullPointerException("The table "+elementMeta.getSourceTableName()+"'s field is null!");
      }
      
      String sql = "";
      StringBuffer sb = new StringBuffer();
      sb.append(SQL_SELECT);
      for(String fieldName : elementMeta.getFieldNames()) {
         if(StringUtils.isEmpty(fieldName)) {
            continue;
         }
         sb.append(elementMeta.getSourceTableName());
         sb.append(".");
         sb.append(fieldName);
         sb.append(",");
      }
      
      sql = sb.toString();
      sql = sql.substring(0,sql.length() - 1);
      sql += SQL_FROM + elementMeta.getSourceTableName();
      return sql; 
   }
   
   public String getQueryString(ElementMeta elementMeta,int offset,int limit) throws Exception {
      
      String sql = getAllQueryString(elementMeta);
      if(offset !=-1 && limit != -1)
      sql += SQL_LIMIT+limit+SQL_OFFSET+offset;
     // sql += SQL_LIMIT+limit+" ,"+offset;
      return sql; 
   }  
   
   public String getPrimaryKeysQueryString(ElementMeta elementMeta) throws Exception {
      
      if(elementMeta.getSourceTableName() == null ) {
         throw new NullPointerException("The table name is null");
      } 
      
      if(elementMeta.getFieldNames() == null || elementMeta.getFieldNames().length < 1) {
         throw new NullPointerException("The table "+elementMeta.getSourceTableName()+"'s field is null!");
      }
      StringBuilder sql = new StringBuilder();
      sql.append(SQL_SELECT);
      sql.append(SQL_NUMBER_FIELD);
      sql.append(SQL_FROM);
      sql.append(elementMeta.getSourceTableName());
      return sql.toString();
   }
   
   public String getNumbersQueryOfArticle(ElementMeta elementMeta) throws Exception {
      
      if(elementMeta.getSourceTableName() == null ) {
         throw new NullPointerException("The table name is null");
      }     

      StringBuilder sql = new StringBuilder();
      sql.append(SQL_SELECT);
      sql.append("min("+SQL_NUMBER_FIELD+")");
      sql.append(SQL_FROM);
      sql.append(elementMeta.getRelTableName());
      sql.append(" where "+elementMeta.getRelTableName()+".snumber in (select "+elementMeta.getRelDataType()+".number from "+elementMeta.getRelDataType()+") and "+elementMeta.getRelTableName()+".dnumber in (select "+elementMeta.getSourceTableName()+".number from "+elementMeta.getSourceTableName()+") group by dnumber");
      return sql.toString();
   }
   
   public String getNumbersQueryString(ElementMeta elementMeta) throws Exception {
      
      if(elementMeta.getSourceTableName() == null ) {
         throw new NullPointerException("The table name is null");
      }     

      StringBuilder sql = new StringBuilder();
      sql.append(SQL_SELECT);
      sql.append(SQL_NUMBER_FIELD);
      sql.append(SQL_FROM);
      sql.append(elementMeta.getRelTableName());
      sql.append(" where "+elementMeta.getRelTableName()+".snumber in (select "+elementMeta.getRelDataType()+".number from "+elementMeta.getRelDataType()+") and "+elementMeta.getRelTableName()+".dnumber in (select "+elementMeta.getSourceTableName()+".number from "+elementMeta.getSourceTableName()+") ");
      return sql.toString();
   }
   public String getSelfRelQueryString(ElementMeta elementMeta) throws Exception {
      
      if(elementMeta.getSourceTableName() == null ) {
         throw new NullPointerException("The table name is null");
      }     

      StringBuilder sql = new StringBuilder();
      sql.append(SQL_SELECT);
      sql.append(SQL_NUMBER_FIELD);
      sql.append(SQL_FROM);
      sql.append(elementMeta.getSelfRelTableName());
      sql.append(" where "+elementMeta.getSelfRelTableName()+".snumber in (select "+elementMeta.getSourceTableName()+".number from "+elementMeta.getSourceTableName()+") and "+elementMeta.getSelfRelTableName()+".dnumber in (select "+elementMeta.getSourceTableName()+".number from "+elementMeta.getSourceTableName()+") ");
      return sql.toString();
   }
   
   public String getRootChildNodeQuery(ElementMeta elementMeta) throws Exception {
      
      if(elementMeta.getSourceTableName() == null ) {
         throw new NullPointerException("The table name is null");
      }     

      StringBuilder sql = new StringBuilder();
      sql.append(SQL_SELECT);
      sql.append(SQL_NUMBER_FIELD);
      sql.append(SQL_FROM);
      sql.append(elementMeta.getSourceTableName());
      sql.append(" where number not in (select dnumber from "+elementMeta.getSelfRelTableName()+") and number in (select snumber from "+elementMeta.getSelfRelTableName()+")");
      return sql.toString();
   }
   
   public String getRelateQueryString(ElementMeta elementMeta,Integer relateId) throws Exception {
      
      if(elementMeta.getRelTableName() == null ) {
         throw new NullPointerException("The table name is null");
      } 
      
      if(elementMeta.getFieldNames() == null || elementMeta.getFieldNames().length < 1) {
         throw new NullPointerException("The table "+elementMeta.getSourceTableName()+"'s field is null!");
      }
      StringBuilder sql = new StringBuilder();
      sql.append(SQL_SELECT);
      sql.append(elementMeta.getRelTableName()+".");
      sql.append(SQL_NUMBER_FIELD);
      sql.append(",");
      sql.append(elementMeta.getRelTableName()+".");
      sql.append(SQL_DNUMBER);
      sql.append(SQL_FROM);
      sql.append(elementMeta.getRelTableName());
      sql.append(SQL_WHERE);
      sql.append(elementMeta.getRelTableName()+".");
      sql.append(SQL_SNUMBER);
      sql.append(" = "+relateId);
      sql.append(SQL_AND);
      sql.append(elementMeta.getRelTableName()+".");
      sql.append(SQL_DNUMBER);
      sql.append(SQL_IN);
      sql.append("(select "+elementMeta.getSourceTableName()+".number from "+elementMeta.getSourceTableName()+")");
      
      return sql.toString();
   }
   
   public String getCountQueryString(ElementMeta elementMeta) {
      StringBuilder sb = new StringBuilder();
      sb.append(SQL_SELECT_COUNT);
      sb.append(SQL_FROM);
      sb.append(elementMeta.getSourceTableName());
      return sb.toString();
   }
   
   public String getQueryString(ElementMeta elementMeta,Integer key) throws Exception {
      String sql = getAllQueryString(elementMeta);
      sql += SQL_WHERE+elementMeta.getSourceTableName()+".number="+key;
      return sql;
   }
   
   public String getQueryStringByRelateId(ElementMeta elementMeta,Integer key) throws Exception {
      String sql = getAllQueryString(elementMeta);
      sql += ","+elementMeta.getRelTableName()+SQL_WHERE+elementMeta.getRelTableName()+"."+SQL_DNUMBER+"="+elementMeta.getSourceTableName()+".number and "+elementMeta.getRelTableName()+".number="+key;
      return sql;
   }
   
   public String getSelfRelFieldString(ElementMeta elementMeta,Integer key) {
      if(elementMeta.getRelTableName() == null ) {
         return "";
      }       
     
      String sql = "";
      StringBuffer sb = new StringBuffer();
      sb.append(SQL_SELECT);
      sb.append(elementMeta.getSelfRelTableName());
      sb.append(".snumber,");
      sb.append(elementMeta.getSelfRelTableName());
      sb.append(".dnumber,");
      sql = sb.toString();
      if(sql.endsWith(",")) {
         sql = sql.substring(0,sql.length() - 1);
      }
      sql += SQL_FROM + elementMeta.getSelfRelTableName();
      sql += SQL_WHERE+elementMeta.getSelfRelTableName()+".number="+key;
      return sql; 
   }
   
   public String getRelateFieldsString(ElementMeta elementMeta,Integer key) {
      if(elementMeta.getRelTableName() == null ) {
         return "";
      }       
     
      String sql = "";
      StringBuffer sb = new StringBuffer();
      sb.append(SQL_SELECT);
      sb.append(elementMeta.getRelTableName());
      sb.append(".snumber,");
      sb.append(elementMeta.getRelTableName());
      sb.append(".dnumber,");
      for(String fieldName : elementMeta.getRelateFields()) {
         if(StringUtils.isEmpty(fieldName)) {
            continue;
         }
         sb.append(elementMeta.getRelTableName());
         sb.append(".");
         sb.append(fieldName);
         sb.append(",");
      }
      sql = sb.toString();
      if(sql.endsWith(",")) {
         sql = sql.substring(0,sql.length() - 1);
      }
      sql += SQL_FROM + elementMeta.getRelTableName();
      sql += SQL_WHERE+elementMeta.getRelTableName()+".number="+key;
      return sql; 
   }

}
