/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.forms.validation;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;


public class ValidationError {

   private String key;
   private String message;
   private List<Object> params = new ArrayList<Object>();

   public ValidationError(String key) {
      this.key = key;
   }
   
   public ValidationError(String key, String message) {
      this.key = key;
      this.message = message;
   }
   
   public void setMessage(String message) {
      this.message = message;
   }

   public void addParam(Object param) {
      params.add(param);
   }
   
   public String getErrorMessage(ResourceBundle bundle) {
      String pattern;
      if (StringUtils.isNotBlank(message)) {
         pattern = message;
      }
      else {
         pattern = bundle.getString(key);
      }
      if (params.isEmpty()) {
         return pattern;
      }
      return MessageFormat.format(pattern, params);
   }
   
}
