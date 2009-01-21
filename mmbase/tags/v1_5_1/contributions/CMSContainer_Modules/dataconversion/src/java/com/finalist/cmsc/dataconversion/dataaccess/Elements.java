package com.finalist.cmsc.dataconversion.dataaccess;

import java.util.HashMap;

/**
 * This class holds data  
 * @author kevin
 *
 */
public class Elements {

   /** a map holds data */
   private HashMap<String,Object> concurr;
   
   public Elements() {
      concurr = new HashMap<String,Object>(40); 
   }
   
   public Elements(int initialCapacity) {
      concurr = new HashMap<String,Object>(initialCapacity);
   }
   
   public Object getValue(String fieldName) {
      return concurr.get(fieldName);
   }
   
   public void setValue(String fieldName,Object value) {
      concurr.put(fieldName, value);
   }
   
   public HashMap<String,Object> getMap() {
      return concurr;
   }
}
