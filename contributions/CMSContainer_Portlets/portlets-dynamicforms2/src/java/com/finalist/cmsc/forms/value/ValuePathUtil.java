/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.forms.value;

import java.util.List;
import java.util.StringTokenizer;

import com.finalist.cmsc.forms.definition.DataObject;


public class ValuePathUtil {

   private static final char ITEM_END = ']';

   private static final char ITEM_START = '[';

   private static final char PATH_SEP = '/';

   private static final char NAME_SEP = '|';
   
   public static ValueObject getObjectFromPath(ValueObject data, String searchpath) {
      return getObjectFromPath(data, searchpath, PATH_SEP);
   }

   private static ValueObject getObjectFromPath(ValueObject data, String searchpath, char separator) {
      StringTokenizer tokenizer = new StringTokenizer(searchpath, String.valueOf(separator));
      if (tokenizer.hasMoreTokens()) {
         String name = tokenizer.nextToken();
         if (name.indexOf(ITEM_START) > -1) name = name.substring(0, name.indexOf(ITEM_START));
         if (name.equals(data.getName())) {
            if (!tokenizer.hasMoreTokens()) return data;

            ValueObject result = data;
            while (tokenizer.hasMoreTokens()) {
               name = tokenizer.nextToken();
               int position = 0;
               if (name.endsWith(String.valueOf(ITEM_END))) {
                  String positionstring = name.substring(name.indexOf(ITEM_START) + 1, name.indexOf(ITEM_END));
                  position = Integer.parseInt(positionstring);
                  name = name.substring(0, name.indexOf(ITEM_START));
               }

               result = result.getObject(name, position);
               if (result == null) {
                  throw new IllegalArgumentException("Object with path '" + searchpath
                        + "' not found");
               }
               if (!tokenizer.hasMoreTokens()) {
                  return result;
               }
            }
         }
      }
      throw new IllegalArgumentException("Path '" + searchpath + "' does not start with '"
            + data.getName() + "'");
   }

   public static ValueField getFieldFromNamePath(ValueObject data, String formname) {
      return getFieldFromPath(data, formname, NAME_SEP);
   }

   public static ValueField getFieldFromPath(ValueObject data, String formname) {
      return getFieldFromPath(data, formname, PATH_SEP);
   }
   
   private static ValueField getFieldFromPath(ValueObject data, String formname, char separator) {
      int fieldIndex = formname.lastIndexOf(separator);
      if(fieldIndex > -1) {
         String objectPath = formname.substring(0, fieldIndex);
         String fieldName = formname.substring(fieldIndex + 1);
         ValueObject child = getObjectFromPath(data, objectPath, separator);
         if (child != null) {
            return child.getOrCreateField(fieldName);
         }
      }
      throw new IllegalArgumentException("Failed to get field '" + formname + "' not found");
   }
   
   public static void deleteObjectFromPath(ValueObject data, String searchpath) {
      StringTokenizer tokenizer = new StringTokenizer(searchpath, String.valueOf(PATH_SEP));
      if (tokenizer.hasMoreTokens()) {
         String name = tokenizer.nextToken();
         if (name.indexOf(ITEM_START) > -1) name = name.substring(0, name.indexOf(ITEM_START));
         if (name.equals(data.getName())) {
            if (!tokenizer.hasMoreTokens()) {
               data.deleteObjects();
            }

            ValueObject result = data;
            List<ValueObject> resultList = null;
            while (tokenizer.hasMoreTokens()) {
               name = tokenizer.nextToken();
               int position = 0;
               if (name.endsWith(String.valueOf(ITEM_END))) {
                  String positionstring = name.substring(name.indexOf(ITEM_START) + 1, name
                        .indexOf(ITEM_END));
                  position = Integer.parseInt(positionstring);
                  name = name.substring(0, name.indexOf(ITEM_START));
               }

               resultList = result.getList(name);
               if (resultList == null || resultList.size() == 0) {
                  throw new IllegalArgumentException("Object with path '" + searchpath
                        + "' not found");
               }
               result = resultList.get(position);
               if (!tokenizer.hasMoreTokens() && result != null) {
                  resultList.remove(position);
               }
            }
         }
      }
   }

   public static List<ValueObject> getListFromPath(ValueObject data, String searchpath) {
      StringTokenizer tokenizer = new StringTokenizer(searchpath, String.valueOf(PATH_SEP));
      if (tokenizer.hasMoreTokens()) {
         String name = tokenizer.nextToken();
         if (name.equals(data.getName())) {
            ValueObject result = data;

            List<ValueObject> resultList = null;
            while (tokenizer.hasMoreTokens()) {
               name = tokenizer.nextToken();
               resultList = result.getList(name);
               if (resultList == null || resultList.size() == 0) {
                  return null;
               }
               if (tokenizer.hasMoreTokens()) {
                  result = resultList.get(0);
               }
            }
            if (resultList != null) {
               return resultList;
            }
            throw new IllegalArgumentException("List with path '" + searchpath + "' not found");

         }
      }
      throw new IllegalArgumentException("Path '" + searchpath + "' does not start with '"
            + data.getName() + "'");
   }

   public static String createPath(ValueObject object, String searchpath) {
      StringTokenizer tokenizer = new StringTokenizer(searchpath, String.valueOf(PATH_SEP));
      String createdPath = "";
      if (tokenizer.hasMoreTokens()) {

         String name = tokenizer.nextToken();
         if (name.equals(object.getName())) {
            ValueObject result = object;
            DataObject dataObject = object.getDataObject();
            createdPath = name + ITEM_START + "0" + ITEM_END;
            while (tokenizer.hasMoreTokens()) {
               name = tokenizer.nextToken();
               DataObject dataObject2 = dataObject.getObject(name);
               if (dataObject2 == null) {
                  throw new IllegalArgumentException("DataObject with name '" + name
                        + "' in path '" + searchpath + "' not found");
               }
               ValueObject newValueObject = new ValueObject(dataObject2);
               result.addObject(newValueObject);
               createdPath += PATH_SEP + name + ITEM_START + (result.getList(name).size() - 1) + ITEM_END;
               result = newValueObject;
               dataObject = dataObject2;
            }
         }
      }
      return createdPath;
   }

   public static String createNamePath(String namePath, String path) {
      if (path == null || "".equals(path)) {
         return namePath;
      }
      if (!namePath.endsWith(String.valueOf(ITEM_END))) {
         namePath += ITEM_START + "0" + ITEM_END;
      }
      if (path.startsWith(namePath)) {
         return path.replace(PATH_SEP, NAME_SEP);
      }
      else {
         return namePath + NAME_SEP + path.replace(PATH_SEP, NAME_SEP);
      }
   }

   public static String createNamePath(String namePath, String path, int i) {
      return createNamePath(namePath, path) + ITEM_START + i + ITEM_END;
   }

   public static String getParent(String path) {
      return  path.substring(0, path.lastIndexOf(ValuePathUtil.PATH_SEP));
   }

   public static String concat(String firstPath, String secondPath) {
      return firstPath + ValuePathUtil.PATH_SEP + secondPath;
   }


   
}
