/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* 

 */

package com.finalist.pluto.portalImpl.om.portlet.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.portlet.PortletMode;

import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.util.StringUtils;

import com.finalist.pluto.portalImpl.om.common.Support;

public class ContentTypeImpl implements ContentType, Serializable, Support {

   private String contentType;

   private Collection<PortletMode> portletModes;


   public ContentTypeImpl() {
      portletModes = new HashSet<PortletMode>();
   }


   public String getContentType() {
      return contentType;
   }


   public void setContentType(String contentType) {
      this.contentType = contentType;
   }


   public Iterator<PortletMode> getPortletModes() {
      return portletModes.iterator();
   }


   public boolean supportsPortletMode(PortletMode portletMode) {
      return portletModes.contains(portletMode);
   }


   public void addPortletMode(PortletMode mode) {
      portletModes.add(mode);
   }


   public void addPortletMode(String mode) {
      portletModes.add(new PortletMode(mode));
   }


   public void postLoad(Object parameter) throws Exception {
      if (!portletModes.contains(javax.portlet.PortletMode.VIEW)) {
         portletModes.add(javax.portlet.PortletMode.VIEW);
      }
   }


   public String toString() {
      return toString(0);
   }


   public String toString(int indent) {
      StringBuffer buffer = new StringBuffer(50);
      StringUtils.newLine(buffer, indent);
      buffer.append(getClass().toString());
      buffer.append(":");
      StringUtils.newLine(buffer, indent);
      buffer.append("{");
      StringUtils.newLine(buffer, indent);
      buffer.append("contentType='");
      buffer.append(contentType);
      buffer.append("'");
      int i = 0;
      Iterator<PortletMode> iterator = portletModes.iterator();
      while (iterator.hasNext()) {
         StringUtils.newLine(buffer, indent);
         buffer.append("portletMode[");
         buffer.append(i++);
         buffer.append("]='");
         buffer.append(iterator.next());
         buffer.append("'");
      }
      StringUtils.newLine(buffer, indent);
      buffer.append("}");
      return buffer.toString();
   }

}
