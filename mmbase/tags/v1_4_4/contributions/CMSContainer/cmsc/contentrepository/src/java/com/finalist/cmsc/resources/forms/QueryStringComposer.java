package com.finalist.cmsc.resources.forms;

public class QueryStringComposer {

   private StringBuffer queryString = null;


   public void addParameter(String key, String value) {
      if (value == null || key == null) {
         return;
      }

      if (queryString == null) {
         queryString = new StringBuffer("?");
      }
      else {
         queryString.append("&");
      }

      queryString.append(key);
      queryString.append("=");
      queryString.append(value);
   }


   public String getQueryString() {
      if (queryString != null) {
         return queryString.toString();
      }
      else {
         return "";
      }
   }
}
