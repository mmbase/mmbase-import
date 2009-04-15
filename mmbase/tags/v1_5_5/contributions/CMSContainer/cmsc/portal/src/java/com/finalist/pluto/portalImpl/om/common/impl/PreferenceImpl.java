/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceCtrl;
import org.apache.pluto.util.StringUtils;

public class PreferenceImpl implements Preference, PreferenceCtrl, Serializable {

   private String name;

   private List<Object> values = new ArrayList<Object>();

   private boolean readOnly;


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public boolean isReadOnly() {
      return readOnly;
   }


   public void setReadOnly(boolean readOnly) {
      this.readOnly = readOnly;
   }


   public void setValues(List values) {
      this.values = values;
   }


   public void setValue(String value) {
      this.values.add(value);
   }


   public Iterator<Object> getValues() {
      return values.iterator();
   }


   public boolean isValueSet() {
      return values.size() > 0;
   }


   public void setReadOnly(final String readOnly) {
      this.readOnly = Boolean.valueOf(readOnly).booleanValue();
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(": name='");
      buffer.append(name);
      buffer.append("', value='");

      if (values == null) {
         buffer.append("null");
      }
      else {
         StringUtils.newLine(buffer, indent);
         buffer.append("{");
         Iterator<Object> iterator = values.iterator();
         if (iterator.hasNext()) {
            StringUtils.newLine(buffer, indent);
            buffer.append((String) iterator.next());
         }
         while (iterator.hasNext()) {
            StringUtils.indent(buffer, indent + 2);
            buffer.append((String) iterator.next());
         }
         StringUtils.newLine(buffer, indent);
         buffer.append("}");
      }

      buffer.append("', isReadOnly='");
      buffer.append(isReadOnly());
      buffer.append("'");
      return buffer.toString();
   }

}
