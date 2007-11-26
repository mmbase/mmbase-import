/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.*;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class Page extends NavigationItem {

   private boolean inmenu;
   private boolean secure;
   private String externalurl;

   private Map<String, Integer> portlets = new HashMap<String, Integer>();
   private int layout;
   private List<Integer> stylesheet = new ArrayList<Integer>();
   private Map<String, String> pageImages = new LinkedHashMap<String, String>();


   public boolean isInmenu() {
      return inmenu;
   }


   public boolean getInmenu() {
      return inmenu;
   }


   public void setInmenu(boolean inmenu) {
      this.inmenu = inmenu;
   }


   public int getLayout() {
      return layout;
   }


   public void setLayout(int layout) {
      this.layout = layout;
   }


   public List<Integer> getStylesheet() {
      return stylesheet;
   }


   public void addStylesheet(int stylesheet) {
      this.stylesheet.add(new Integer(stylesheet));
   }


   public String getPageImage(String name) {
      return pageImages.get(name);
   }


   public void addPageImage(String name, String image) {
      pageImages.put(name, image);
   }


   public void addPortlet(String layoutId, Integer p) {
      if (p != null) {
         portlets.put(layoutId, p);
      }
   }


   public Integer getPortlet(String layoutId) {
      if (portlets.containsKey(layoutId)) {
         return portlets.get(layoutId);
      }
      return -1;
   }


   public Map<String, Integer> getPortletsWithNames() {
      return portlets;
   }


   public Collection<Integer> getPortlets() {
      return portlets.values();
   }


   public boolean isSecure() {
      return secure;
   }


   public void setSecure(boolean secure) {
      this.secure = secure;
   }


   public String getExternalurl() {
      return externalurl;
   }


   public void setExternalurl(String externalurl) {
      this.externalurl = externalurl;
   }


   public Set<Map.Entry<String, String>> getPageImages() {
      return pageImages.entrySet();
   }


   public List<String> getImages() {
      List<String> images = new ArrayList<String>();
      for (String image : pageImages.values()) {
         images.add(image);
      }
      return images;
   }

}
