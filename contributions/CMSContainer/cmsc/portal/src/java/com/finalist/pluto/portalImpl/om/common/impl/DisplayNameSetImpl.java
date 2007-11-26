/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;

import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class DisplayNameSetImpl extends AbstractSupportSet implements DisplayNameSet, Serializable, Support {
   // DisplayNameSet implementation.
   public DisplayName get(Locale locale) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         DisplayName displayName = (DisplayName) iterator.next();
         if (displayName.getLocale().equals(locale)) {
            return displayName;
         }
      }
      return null;
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(": ");
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         buffer.append(((DisplayNameImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }

}
