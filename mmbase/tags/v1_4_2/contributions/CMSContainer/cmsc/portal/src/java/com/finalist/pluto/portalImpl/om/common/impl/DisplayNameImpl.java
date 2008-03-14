/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;
import java.util.Locale;

import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.Support;

public class DisplayNameImpl implements DisplayName, Serializable, Support {

   private String displayName;

   private Locale locale;


   public String getDisplayName() {
      return displayName;
   }


   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }


   public Locale getLocale() {
      return locale;
   }


   public void setLocale(Locale locale) {
      this.locale = locale;
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(": displayName='");
      buffer.append(displayName);
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
