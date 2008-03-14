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

package com.finalist.pluto.portalImpl.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.services.information.PortalContextProvider;

import com.finalist.pluto.portalImpl.services.config.Config;

public class PortalContextProviderImpl implements PortalContextProvider {

   /** Portal information */
   private String info;
   /** supported portlet modes by this portal */
   private Vector<PortletMode> modes;

   /** supported window states by this portal */
   private Vector<WindowState> states;

   /** portal properties */
   private HashMap<String, String> properties = new HashMap<String, String>();


   public PortalContextProviderImpl() {
      // these are the minimum modes that the portal needs to support
      modes = getDefaultModes();
      // these are the minimum states that the portal needs to support
      states = getDefaultStates();
      // set info
      info = Config.getParameters().getString("portaldriver.info");
   }


   // PortalContextProvider implementation.

   public java.lang.String getProperty(java.lang.String name) {
      if (name == null) {
         throw new IllegalArgumentException("Property name == null");
      }

      return properties.get(name);
   }


   public java.util.Collection<String> getPropertyNames() {
      return properties.keySet();
   }


   public java.util.Collection<PortletMode> getSupportedPortletModes() {
      return modes;
   }


   public java.util.Collection<WindowState> getSupportedWindowStates() {
      return states;
   }


   public String getPortalInfo() {
      return info;
   }


   // internal methods.

   private Vector<PortletMode> getDefaultModes() {

      Vector<PortletMode> m = new Vector<PortletMode>();

      String[] supportedModes = Config.getParameters().getStrings("supported.portletmode");

      for (String element : supportedModes) {
         m.add(new PortletMode(element.toString().toLowerCase()));
      }

      return m;
   }


   private Vector<WindowState> getDefaultStates() {
      Vector<WindowState> s = new Vector<WindowState>();

      String[] supportedStates = Config.getParameters().getStrings("supported.windowstate");

      for (String element : supportedStates) {
         s.add(new WindowState(element.toString().toLowerCase()));
      }

      return s;
   }


   // additional methods.

   // methods used container internally to set

   public void setProperty(String name, String value) {
      if (name == null) {
         throw new IllegalArgumentException("Property name == null");
      }

      properties.put(name, value);
   }


   // expects enumeration of PortletMode objects
   public void setSupportedPortletModes(Enumeration<PortletMode> portletModes) {
      Vector<PortletMode> v = new Vector<PortletMode>();
      while (portletModes.hasMoreElements()) {
         v.add(portletModes.nextElement());
      }
      modes = v;
   }


   // expects enumeration of WindowState objects
   public void setSupportedWindowStates(Enumeration<WindowState> windowStates) {
      Vector<WindowState> v = new Vector<WindowState>();
      while (windowStates.hasMoreElements()) {
         v.add(windowStates.nextElement());
      }
      states = v;
   }


   /**
    * reset all values to default portlet modes and window states; delete all
    * properties and set the given portlet information as portlet info string.
    * 
    * @param portalInfo
    *           portal information string that will be returned by the
    *           <code>getPortalInfo</code> call.
    */
   public void reset(String portalInfo) {
      info = new String(portalInfo);

      // these are the minimum modes that the portal needs to support
      modes = getDefaultModes();
      // these are the minimum states that the portal needs to support
      states = getDefaultStates();

      properties.clear();
   }

}
