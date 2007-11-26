/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.excel2menu;

import java.util.*;

public class ExcelConfig {

   private int maxLevel;
   private Site site = new Site();
   private Map<String, Layout> layouts = new HashMap<String, Layout>();
   private Map<String, Portlet> portlets = new HashMap<String, Portlet>();
   private Map<String, View> views = new HashMap<String, View>();

   private boolean createChannels = false;


   public ExcelConfig(Map<String, String> settings) {
      for (Map.Entry<String, String> entry : settings.entrySet()) {
         String key = entry.getKey();
         String value = entry.getValue();

         processEntry(key, value);
      }
   }


   public ExcelConfig(Properties settings) {
      for (Enumeration iter = settings.propertyNames(); iter.hasMoreElements();) {
         String key = (String) iter.nextElement();
         String value = settings.getProperty(key);
         processEntry(key, value);
      }
   }


   private void processEntry(String key, String value) {
      String[] keys = key.split("\\.");

      if ("maxLevel".equals(keys[0])) {
         maxLevel = Integer.parseInt(value);
      }

      if ("createChannels".equals(keys[0])) {
         createChannels = Boolean.valueOf(value);
      }

      if ("site".equals(keys[0])) {
         if ("title".equals(keys[1])) {
            site.title = value;
         }
         if ("path".equals(keys[1])) {
            site.path = value;
         }
         if ("livepath".equals(keys[1])) {
            site.livepath = value;
         }
      }
      if ("layout".equals(keys[0])) {
         String layoutName = keys[1];
         Layout layout = layouts.get(layoutName);
         if (layout == null) {
            layout = new Layout();
            layouts.put(layoutName, layout);
         }

         if ("title".equals(keys[2])) {
            layout.title = value;
         }
         if ("resource".equals(keys[2])) {
            layout.resource = value;
         }
         if ("level".equals(keys[2])) {
            layout.level = Integer.parseInt(value);
         }
         if ("pos".equals(keys[2])) {
            String position = keys[3];
            layout.positions.put(position, value);
         }
      }
      if ("portlet".equals(keys[0])) {
         String portletName = keys[1];
         Portlet portlet = portlets.get(portletName);
         if (portlet == null) {
            portlet = new Portlet();
            portlets.put(portletName, portlet);
         }

         if ("title".equals(keys[2])) {
            portlet.title = value;
         }
         if ("definition".equals(keys[2])) {
            portlet.definition = value;
         }
         if ("view".equals(keys[2])) {
            portlet.view = value;
         }
      }
      if ("view".equals(keys[0])) {
         String viewName = keys[1];
         View view = views.get(viewName);
         if (view == null) {
            view = new View();
            views.put(viewName, view);
         }

         if ("title".equals(keys[2])) {
            view.title = value;
         }
         if ("resource".equals(keys[2])) {
            view.resource = value;
         }
      }
   }

   class Site {
      int nodeNumber;
      String title;
      String path;
      String livepath;
   }

   class Layout {
      int nodeNumber;
      String title;
      String resource;
      int level;
      Map<String, String> positions = new HashMap<String, String>();
   }

   class Portlet {
      int nodeNumber;
      String title;
      String definition;
      String view;
   }

   class View {
      int nodeNumber;
      String title;
      String resource;
   }


   public boolean isCreateChannels() {
      return createChannels;
   }


   public int getMaxLevel() {
      return maxLevel;
   }


   public Site getSite() {
      return site;
   }


   public Collection<Layout> getLayouts() {
      return layouts.values();
   }


   public Collection<Portlet> getPortlets() {
      return portlets.values();
   }


   public Collection<View> getViews() {
      return views.values();
   }


   public Layout getLayout(String layout) {
      return layouts.get(layout);
   }


   public Portlet getPortlet(String portlet) {
      return portlets.get(portlet);
   }


   public View getView(String view) {
      return views.get(view);
   }


   public Layout findLayout(int level) {
      Layout layout = null;
      for (Layout l : layouts.values()) {
         if (level == l.level) {
            layout = l;
            break;
         }
         if (l.level < level && (layout == null || l.level > layout.level)) {
            layout = l;
         }
      }
      if (layout == null) {
         throw new IllegalStateException("No layout for level " + level);
      }
      return layout;
   }
}
