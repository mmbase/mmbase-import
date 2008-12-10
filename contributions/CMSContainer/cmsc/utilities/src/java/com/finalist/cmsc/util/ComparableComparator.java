package com.finalist.cmsc.util;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComparableComparator implements Comparator {
   private static Log log = LogFactory.getLog(DateUtil.class);
   private static final String GET = "get";
   private static final int ASC = 1;
   private static final int DESC = -1;
   private static final int GREATER = 1;
   private static final int LESS = -1;
   private static final int EQUAL = 0;

   private String[] fields = null;
   private Integer[] directions = null;

   /**
    * @param fields
    * @param directions
    */
   public ComparableComparator(String[] fields, Integer[] directions) {
      this.fields = fields;
      this.directions = directions;
   }

   /**
    * @param fields
    */
   public ComparableComparator(String[] fields) {
      this.fields = fields;
      for (int i = 0; i < fields.length; i++) {
         directions[i] = ASC;
      }
   }

   private ComparableComparator() {
   }

   /**
    * @param o1
    *           the first object to be compared.
    * @param o2
    *           the second object to be compared.
    * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
    *         than the second.
    */
   public int compare(Object o1, Object o2) {
      // if no field, o1 is equal to o2 defaut.
      if (fields == null || fields.length <= 0) {
         return EQUAL;
      }
      for (int i = 0; i < fields.length; i++) {
         int result = compareField(o1, o2, fields[i], directions[i]);
         if (result != EQUAL) {
            return result;
         }
      }
      return EQUAL;
   }

   /**
    * @param o1
    *           the first object to be compared.
    * @param o2
    *           the second object to be compared.
    * @param field
    *           the field to be compared.
    * @param direction
    *           TODO
    * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
    *         than the second.
    */
   private int compareField(Object o1, Object o2, String field, int direction) {
      Object fieldObj1 = getFieldValueByName(o1, field);
      Object fieldObj2 = getFieldValueByName(o2, field);
      if (o1 == null && o2 == null) return EQUAL;
      if (o1 == null) return direction == ASC ? LESS : GREATER;
      if (o2 == null) return direction == ASC ? GREATER : LESS;
      if ((fieldObj1 instanceof Integer) && (fieldObj2 instanceof Integer)) {
         Integer value1 = (Integer) fieldObj1;
         Integer value2 = (Integer) fieldObj2;
         return direction == ASC ? (value1 - value2) : (value2 - value1);
      } else {
         String value1 = fieldObj1.toString();
         String value2 = fieldObj2.toString();
         return direction == ASC ? value1.compareTo(value2) : value2.compareTo(value1);
      }
   }

   /**
    * @param obj
    *           the object.
    * @param fieldName
    *           field name of either HashMap or JavaBean.
    * @return field value
    */
   private Object getFieldValueByName(Object obj, String fieldName) {
      if (obj == null || StringUtil.isEmptyOrWhitespace(fieldName)) return null;
      int length = fieldName.length();
      String methodStr = GET;
      Object value = null;
      try {
         if (!(obj instanceof Map)) {
            String headLetter = fieldName.substring(0, 1).toUpperCase();
            methodStr = GET + headLetter + (length > 1 ? fieldName.substring(1) : "");
            Method method = obj.getClass().getMethod(methodStr);
            value = method.invoke(obj);
         } else {
            Method method = obj.getClass().getMethod(methodStr, String.class);
            value = method.invoke(obj, fieldName);
         }
         return value;
      } catch (Exception e) {
         log.info(fieldName + " attribute does not exist in the " + obj.getClass());
         return null;
      }
   }

}
