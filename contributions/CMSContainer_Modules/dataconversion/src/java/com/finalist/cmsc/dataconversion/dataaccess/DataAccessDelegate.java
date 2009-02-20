package com.finalist.cmsc.dataconversion.dataaccess;

import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;
import org.w3c.dom.Element;

import com.finalist.cmsc.dataconversion.service.Data;

public class DataAccessDelegate {
   
   public static HashMap<Integer,Integer> getNumbersOfDataType(Element element,DataSource dataSource) throws Exception {
      return getDataAccessor(element,dataSource).getNumbers(DataAccessor.DATATYPE_NUMBER);
   }
   
   public static HashMap<Integer,Integer> getNumbersOfRelation(Element element,DataSource dataSource) throws Exception {
      return getDataAccessor(element,dataSource).getNumbers(DataAccessor.RELACTION_NUMBER);
   }
   
   public static HashMap<Integer,Integer> getNumbersOfSelfRelation(Element element,DataSource dataSource) throws Exception {
      return getDataAccessor(element,dataSource).getNumbers(DataAccessor.SELFREL_NUMBER);
   }   
   
   public static HashMap<Integer,Integer> addChildRelation(Element element,DataSource dataSource) throws Exception {
      return getDataAccessor(element,dataSource).addChildRelation();
   } 
   
   public static DataHolder getElementByPrimaryKey(Element element,DataSource dataSource,Integer key,Byte type) throws Exception {
      return getDataAccessor(element,dataSource).getElementByPrimaryKey(key,type);
   }
   
   public static DataAccessor getDataAccessor(Element element,DataSource dataSource) {
      ElementMeta elementMeta = new ElementMeta(element); 
      return new DataAccessor(dataSource,elementMeta);
   }

   public static List<String> getResOfRelation(Data reldata, DataSource dataSource) {
      return new DataAccessor(dataSource).getResOfRelation(reldata);
   }
}
