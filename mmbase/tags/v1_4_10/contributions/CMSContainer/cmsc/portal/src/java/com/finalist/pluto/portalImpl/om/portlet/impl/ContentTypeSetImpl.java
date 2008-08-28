/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.pluto.portalImpl.om.portlet.impl;

import java.io.Serializable;
import java.util.Iterator;

import javax.portlet.PortletMode;

import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.AbstractSupportSet;
import com.finalist.pluto.portalImpl.om.common.Support;

public class ContentTypeSetImpl extends AbstractSupportSet implements ContentTypeSet, Serializable, Support {

   private ContentType anyContentType;


   public ContentType get(String contentType) {
      Iterator iterator = this.iterator();
      while (iterator.hasNext()) {
         ContentType _contentType = (ContentType) iterator.next();
         if (_contentType.getContentType().equals(contentType)) {
            return _contentType;
         }
      }
      return null;
   }


   public boolean supportsPortletMode(PortletMode mode) {
      // Always support "VIEW". Some portlet vendors do not indicate view
      // in the deployment descriptor.
      if (mode.equals(PortletMode.VIEW)) {
         return true;
      }

      Iterator itr = this.iterator();
      while (itr.hasNext()) {
         ContentType p = (ContentType) itr.next();
         if (p.supportsPortletMode(mode)) {
            return true;
         }
      }

      return false;
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
         buffer.append(((ContentTypeImpl) iterator.next()).toString(indent + 2));
      }
      return buffer.toString();
   }

}
