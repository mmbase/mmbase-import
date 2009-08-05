package com.finalist.cmsc.forms.definition;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DataObject {

   private final static Log Log = LogFactory.getLog(DataObject.class);

   public static DataObject getObjectFromPath(DataObject data, String searchpath) {
      StringTokenizer tokenizer = new StringTokenizer(searchpath, "/");
      if (tokenizer.hasMoreTokens()) {
         String name = tokenizer.nextToken();
         if (name.equals(data.getName())) {
            DataObject result = data;
            while (tokenizer.hasMoreTokens()) {
               name = tokenizer.nextToken();
               result = result.getObject(name);
               if (result == null) {
                  throw new IllegalArgumentException("Object with path '" + searchpath
                        + "' not found");
               }
            }
            return result;
         }
      }
      throw new IllegalArgumentException("Path '" + searchpath + "' does not start with '"
            + data.getName() + "'");
   }

   private String name;
   private Map<String, DataField> datafields = new LinkedHashMap<String, DataField>();
   private Map<String, DataObject> objectLists = new LinkedHashMap<String, DataObject>();


   public void addField(DataField field) {
      String name = field.getName();
      if (datafields.containsKey(name)) {
         Log.error("field with name '" + name + "' already defined");
      }
      else {
         datafields.put(name, field);
      }
   }

   public void addObject(DataObject object) {
      String name = object.getName();
      if (objectLists.containsKey(name)) {
         Log.error("object with name '" + name + "' already defined");
      }
      else {
         objectLists.put(name, object);
      }
   }

   @Override
   public boolean equals(Object oth) {
      if (this == oth) {
         return true;
      }

      if (oth == null) {
         return false;
      }

      if (oth.getClass() != getClass()) {
         return false;
      }

      DataObject other = (DataObject) oth;
      if (this.name == null) {
         if (other.name != null) {
            return false;
         }
      }
      else {
         if (!this.name.equals(other.name)) {
            return false;
         }
      }
      if (this.datafields == null) {
         if (other.datafields != null) {
            return false;
         }
      }
      else {
         if (!this.datafields.equals(other.datafields)) {
            return false;
         }
      }
      if (this.objectLists == null) {
         if (other.objectLists != null) {
            return false;
         }
      }
      else {
         if (!this.objectLists.equals(other.objectLists)) {
            return false;
         }
      }

      return true;
   }

   public DataField getField(String fieldname) {
      return datafields.get(fieldname);
   }

   public List<DataField> getFields() {
      return new ArrayList<DataField>(datafields.values());
   }

   public String getName() {
      return name;
   }

   public DataObject getObject(String objectname) {
      return objectLists.get(objectname);
   }

   @Override
   public int hashCode() {
      final int PRIME = 1000003;
      int result = 0;
      if (name != null) {
         result = PRIME * result + name.hashCode();
      }
      if (datafields != null) {
         result = PRIME * result + datafields.hashCode();
      }
      if (objectLists != null) {
         result = PRIME * result + objectLists.hashCode();
      }

      return result;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void postLoad() {
      for (DataField field : datafields.values()) {
         field.portLoad();
      }
      for (DataObject object : objectLists.values()) {
         object.postLoad();
      }
   }
}
