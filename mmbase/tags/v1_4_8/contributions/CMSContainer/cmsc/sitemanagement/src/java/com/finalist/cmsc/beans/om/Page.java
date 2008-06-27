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

   private String externalurl;

   private Map<String, Integer> portlets = new HashMap<String, Integer>();
   private int layout;
   private List<Integer> stylesheet = new ArrayList<Integer>();
   private Map<String,List<Integer>> pageImages = new HashMap<String,List<Integer>>();


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


    public List<Integer> getPageImage(String name) {
    	return pageImages.get(name);
    }
    


    public void addPageImage(String name, int image) {
        List<Integer> images = pageImages.get(name);
        if (images == null) {
            images = new ArrayList<Integer>();
            pageImages.put(name, images);
        }
    	images.add(image);
    }

    public void setPageImages(Map<String, List<Integer>> images) {
        this.pageImages = images;
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


   public String getExternalurl() {
      return externalurl;
   }


   public void setExternalurl(String externalurl) {
      this.externalurl = externalurl;
   }

    public Set<Map.Entry<String, List<Integer>>> getPageImages() {
        return pageImages.entrySet();
    }

    public List<Integer> getImages() {
        List<Integer> images = new ArrayList<Integer>();
        for (List<Integer> image : pageImages.values()) {
            images.addAll(image);
        }
        return images;
    }

}
