/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.*;

import com.finalist.cmsc.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class Layout extends NodeBean implements Comparable<Layout> {

   private String title;

   private String description;

   private String resource;
   private Map<String, List<Integer>> allowedDefinitions = new HashMap<String, List<Integer>>();


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


   public String getResource() {
      return resource;
   }


   public void setResource(String resource) {
      this.resource = resource;
   }


   public Set<String> getNames() {
      return allowedDefinitions.keySet();
   }


   public void addDefinition(String name, int number) {
      List<Integer> definitions = null;
      if (allowedDefinitions.containsKey(name)) {
         definitions = allowedDefinitions.get(name);
      }
      else {
         definitions = new ArrayList<Integer>();
         allowedDefinitions.put(name, definitions);
      }
      Integer nr = Integer.valueOf(number);
      if (!definitions.contains(nr)) {
         definitions.add(nr);
      }
   }


   public List<Integer> getAllowedDefinitions(String name) {
      if (allowedDefinitions.containsKey(name)) {
         return allowedDefinitions.get(name);
      }
      return new ArrayList<Integer>();
   }


   public int compareTo(Layout o) {
      return title.compareTo(o.title);
   }
}
