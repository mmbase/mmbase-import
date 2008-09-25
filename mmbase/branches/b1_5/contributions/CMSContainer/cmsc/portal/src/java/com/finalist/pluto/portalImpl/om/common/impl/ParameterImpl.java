/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;
import java.util.Locale;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.Parameter;
import org.apache.pluto.om.common.ParameterCtrl;
import org.apache.pluto.util.StringUtils;

public class ParameterImpl implements Parameter, ParameterCtrl, Serializable {

   private String name;

   private String value;

   private DescriptionSet descriptions;


   public ParameterImpl() {
      descriptions = new DescriptionSetImpl();
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }


   public String getValue() {
      return value;
   }


   public void setValue(String value) {
      this.value = value;
   }


   public Description getDescription(Locale locale) {
      return descriptions.get(locale);
   }


   public void setDescriptionSet(DescriptionSet descriptions) {
      this.descriptions = descriptions;
   }


   // digester
   public void addDescription(Description description) {
      ((DescriptionSetImpl) descriptions).add(description);
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
      buffer.append(value);
      buffer.append("', descriptions='");
      buffer.append(((DescriptionSetImpl) descriptions).toString());
      buffer.append("'");
      return buffer.toString();
   }
}
