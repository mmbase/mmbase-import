package com.finalist.cmsc.forms.value;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.definition.DataField;
import com.finalist.cmsc.forms.definition.DataObject;

public class ValueObject {

   private final static Log Log = LogFactory.getLog(ValueObject.class);

   private DataObject definition;

   private boolean completed;

   private Map<String, ValueField> datafields;

   private Map<String, List<ValueObject>> objectLists;

   public Map<String, ValueField> getDataFields() {
      return datafields;
   }

   public ValueObject(DataObject definition) {
      if (definition == null) {
         throw new IllegalArgumentException("Object defintion can't be null");
      }

      this.definition = definition;
      datafields = new LinkedHashMap<String, ValueField>();
      objectLists = new LinkedHashMap<String, List<ValueObject>>();

      List<DataField> definitionFields = definition.getFields();
      for (DataField definitionField : definitionFields) {
         ValueField field = new ValueField(definitionField);
         datafields.put(definitionField.getName(), field);
      }
   }

   public void addField(ValueField field) {
      String name = field.getName();
      if (datafields.containsKey(name)) {
         Log.error("field with name '" + name + "' already defined");
      }
      else {
         datafields.put(name, field);
      }
   }

   public void addObject(ValueObject object) {
      String name = object.getName();
      List<ValueObject> objects;
      if (objectLists.containsKey(name)) {
         objects = objectLists.get(name);
      }
      else {
         objects = new ArrayList<ValueObject>();
         objectLists.put(name, objects);
      }
      objects.add(object);
   }

   public String getName() {
      return definition.getName();
   }

   public ValueField getField(String fieldname) {
      return datafields.get(fieldname);
   }

   public List<ValueObject> getList(String name) {
      return objectLists.get(name);
   }
   
   public ValueObject getObject(String objectname) {
      return getObject(objectname, 0);
   }

   public ValueObject getObject(String objectname, int pos) {
      if (objectLists.containsKey(objectname)) {
         List<ValueObject> objects = objectLists.get(objectname);
         if (pos < objects.size()) {
            return objects.get(pos);
         }
      }
      return null;
   }

   @Override
   public int hashCode() {
      final int PRIME = 1000003;
      int result = 0;
      if (definition != null) {
         result = PRIME * result + definition.hashCode();
      }
      if (datafields != null) {
         result = PRIME * result + datafields.hashCode();
      }
      if (objectLists != null) {
         result = PRIME * result + objectLists.hashCode();
      }

      return result;
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

      ValueObject other = (ValueObject) oth;
      if (this.definition == null) {
         if (other.definition != null) {
            return false;
         }
      }
      else {
         if (!this.definition.equals(other.definition)) {
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

   public DataObject getDataObject() {
      return definition;
   }

   public boolean isCompleted() {
      return completed;
   }

   public void setCompleted(boolean completed) {
      this.completed = completed;
   }

   public ValueField getOrCreateField(String fieldname) {
      ValueField field;
      if (datafields.containsKey(fieldname)) {
         field = datafields.get(fieldname);
      }
      else {
         DataField definitionField = definition.getField(fieldname);
         if (definitionField != null) {
            field = new ValueField(definitionField);
            datafields.put(fieldname, field);
         }
         else {
            throw new IllegalArgumentException("field '" + fieldname
                  + "' not found in definition");
         }
      }
      return field;
   }
   
   public Map<String, List<ValueObject>> getObjectLists() {
      return objectLists;
   }

   public void deleteObjects() {
      objectLists.clear();
   }

}
