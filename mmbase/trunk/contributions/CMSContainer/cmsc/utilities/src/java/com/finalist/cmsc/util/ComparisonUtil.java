package com.finalist.cmsc.util;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComparisonUtil implements Comparator {
   private static Log log = LogFactory.getLog(DateUtil.class);

   private static final String GET = "get";
   /*
    * @param the Comparator ,a javaBean or a map that get itself.
    */
   private String[] fields_user = null;

   public String[] getFields_user() {
      return fields_user;
   }

   public void setFields_user(String[] fields_user) {
      this.fields_user = fields_user;
   }

   /**
    * If the definition of the rules of order in accordance with more than one attribute this sort of property in
    * accordance with the order of ranking, is that as long as the sql order by comparison with the location of the
    * property to stop
    */
   public int compare(Object obj1, Object obj2) {
      // No property, no ranking
      if (fields_user == null || fields_user.length <= 0) {
         return 2;// No comparison
      }
      for (int i = 0; i < fields_user.length; i++) {
         if (compareField(obj1, obj2, fields_user[i])) {
            return 1;
         } else {
            return -1;
         }
      }
      return 0;
   }

   /**
    * @param fieldName
    *           According to the sort of property
    */
   private static boolean compareField(Object o1, Object o2, String fieldName) {
      try {
         Object fieldObj1 = getFieldValueByName(fieldName, o1);
         Object fieldObj2 = getFieldValueByName(fieldName, o2);
         if (fieldObj1 == null || fieldObj2 == null) return false;
         if (!(fieldObj1 instanceof Integer)) {
            String value1 = fieldObj1.toString();
            String value2 = fieldObj2.toString();
            /*
             * Collator myCollator = Collator.getInstance(java.util.Locale.ENGLISH); will make sort by Chinese?
             */
            if (value1.compareTo(value2) > 0) {
               return true;
            }
         } else {
            Integer value1 = (Integer) fieldObj1;
            Integer value2 = (Integer) fieldObj2;
            if (value1.intValue() - value2.intValue() > 0) {
               return true;
            }
         }
      } catch (Exception e) {
         log
               .info("The properties of the object does not exist or does not allow this level of security on the reflection properties");
         e.printStackTrace();
      }
      return false;
   }

   /**
    * @param fieldName
    *           object attribute there are could get two type (HashMap,javaBean)
    */
   private static Object getFieldValueByName(String fieldName, Object obj) {
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
         log.info(fieldName + "attribute does not exist" + e);
         return null;
      }
   }
}
