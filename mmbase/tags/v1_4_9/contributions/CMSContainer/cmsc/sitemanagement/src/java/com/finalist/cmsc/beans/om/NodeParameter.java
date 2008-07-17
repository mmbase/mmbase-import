/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.ArrayList;
import java.util.List;

import net.sf.mmapps.commons.beans.NodeBean;

import org.mmbase.bridge.Node;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class NodeParameter extends NodeBean {

   private String key;

   private List<String> values;


   public String getKey() {
      return key;
   }


   public void setKey(String key) {
      this.key = key;
   }


   public String getValueAsString() {
      if (values != null && values.size() > 0) {
         return values.get(0);
      }
      return null;
   }


   public void setValue(Node value) {
      if (value != null) {
         if (values == null) {
            values = new ArrayList<String>();
         }
         if (values.size() > 0) {
            values.clear();
         }
         values.add(String.valueOf(value.getNumber()));
      }
   }


   public void addValue(Node value) {
      if (value != null) {
         if (values == null) {
            values = new ArrayList<String>();
         }
         values.add(String.valueOf(value.getNumber()));
      }
   }


   public void addValue(String value) {
      if (value != null) {
         if (values == null) {
            values = new ArrayList<String>();
         }
         values.add(value);
      }
   }


   public List<String> getValues() {
      return values;
   }
}
