package com.finalist.cmsc.forms.formprocessors;

import java.util.List;
import java.util.Map;

import com.finalist.cmsc.forms.value.*;


public abstract class FormProcessor {

   @SuppressWarnings("unused") 
   public String processForm(ValueObject valueObject, Map<String, List<String>> parameters) {
      return processForm(valueObject);
   }
   
   public String processForm(ValueObject valueObject) {
      processObject(valueObject);
      return null;
   }
   
   protected void processObject(ValueObject valueObject) {
      processObject(valueObject, valueObject.getName());
   }
   
   protected void processObject(ValueObject valueObject, String namePath) {
      for (ValueField valueField : valueObject.getDataFields().values()) {
         String path = ValuePathUtil.createNamePath(namePath, valueField.getName());
         String value = valueField.getStringValue();
         processField(path, value);
      }
      for (Map.Entry<String, List<ValueObject>> childObjects : valueObject.getObjectLists().entrySet()) {
         for (int j = 0; j < childObjects.getValue().size(); j++) {
            ValueObject childObject = childObjects.getValue().get(j);
            String childPath = ValuePathUtil.createNamePath(namePath, childObjects.getKey(), j);
            processObject(childObject, childPath);
         }
      }
   }
   
   @SuppressWarnings("unused")
   protected void processField(String path, String value) {
      // nothing
   }
}
