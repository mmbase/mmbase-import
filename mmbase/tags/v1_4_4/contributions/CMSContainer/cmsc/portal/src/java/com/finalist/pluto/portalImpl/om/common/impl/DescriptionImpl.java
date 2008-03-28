/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.util.Locale;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.Support;

public class DescriptionImpl implements Description, java.io.Serializable, Support {

   private String description;

   private Locale locale;


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public Locale getLocale() {
      return locale;
   }


   public void setLocale(Locale locale) {
      this.locale = locale;
   }


   // digester methods
   public void setLanguage(String lang) {
      this.locale = new Locale(lang);
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(": description='");
      buffer.append(description);
      buffer.append("', locale='");
      buffer.append(locale);
      buffer.append("'");
      return buffer.toString();
   }


   public void postLoad(Object parameter) throws Exception {
      if (locale == null) {
         locale = Locale.ENGLISH;
      }
   }
}
