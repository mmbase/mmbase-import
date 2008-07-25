/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.*;

import com.finalist.cmsc.beans.NodeBean;

@SuppressWarnings("serial")
public class PortletDefinition extends NodeBean implements Comparable<PortletDefinition> {

   private String title;
   private String description;
   private String definition;
   private String type;
   private List<String> contenttypes = new ArrayList<String>();
   private List<Integer> allowedViews = new ArrayList<Integer>();
   private int rank;
   private int expirationcache = -1; // when field is not present or virtual then this is the default value

   public String getTitle() {
      return title;
   }


   public void setTitle(String title) {
      this.title = title;
   }


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getDefinition() {
      return definition;
   }


   public void setDefinition(String definition) {
      this.definition = definition;
   }


   public String getType() {
      return type;
   }


   public void setType(String type) {
      this.type = type;
   }


   public List<String> getContenttypes() {
      return Collections.unmodifiableList(contenttypes);
   }


   public void addContenttype(String contenttypes) {
      this.contenttypes.add(contenttypes);
   }


   public List<Integer> getAllowedViews() {
      return allowedViews;
   }


   public void addView(Integer view) {
      this.allowedViews.add(view);
   }


   public boolean isSingle() {
      return "single".equals(type);
   }


   public boolean isMultiple() {
      return "multiple".equals(type);
   }


   public int compareTo(PortletDefinition o) {
      return title.compareTo(o.title);
   }


   public int getRank() {
      return rank;
   }


   public void setRank(int rank) {
      this.rank = rank;
   }



   public int getExpirationcache() {
      return expirationcache;
   }



   public void setExpirationcache(int expirationcache) {
      this.expirationcache = expirationcache;
   }


}
