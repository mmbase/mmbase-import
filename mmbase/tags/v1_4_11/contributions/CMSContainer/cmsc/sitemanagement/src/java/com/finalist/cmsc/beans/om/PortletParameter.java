/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.*;

import net.sf.mmapps.commons.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class PortletParameter extends NodeBean {

   private String key;

   private List<String> values;


   public String getKey() {
      return key;
   }


   public void setKey(String key) {
      this.key = key;
   }


   public String getValue() {
      if (values != null && values.size() > 0) {
         return values.get(0);
      }
      return null;
   }


   public void setValue(String value) {
      if (values == null) {
         values = new ArrayList<String>();
      }
      if (values.size() > 0) {
         values.clear();
      }
      values.add(value);
   }


   public void addValue(String value) {
      if (values == null) {
         values = new ArrayList<String>();
      }
      values.add(value);
   }


   public void setValues(String[] valuesArray) {
      if (valuesArray == null) {
         values = new ArrayList<String>();
      }
      else {
         values = Arrays.asList(valuesArray);
      }
   }


   public List<String> getValues() {
      return values;
   }
}
