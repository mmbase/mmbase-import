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

import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class DescriptionSetImpl extends AbstractSupportSet implements DescriptionSet, Serializable, Support {

   // DescriptionSet implemenation.
   public Description get(Locale locale) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         Description desc = (Description) iterator.next();
         if (desc.getLocale().equals(locale)) {
            return desc;
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
         buffer.append(((DescriptionImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }
}
