/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.beans.om;

import java.util.*;

import net.sf.mmapps.commons.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class View extends NodeBean implements Comparable<View> {

   private String title;
   private String description;
   private String resource;
   private boolean detailsupport = true; // when field is not present or virtual then this is the default value
   private List<String> contenttypes = new ArrayList<String>();


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


   public List<String> getContenttypes() {
      return Collections.unmodifiableList(contenttypes);
   }


   public void addContenttype(String contenttypes) {
      this.contenttypes.add(contenttypes);
   }


   public int compareTo(View o) {
      return title.compareTo(o.title);
   }

    public boolean isDetailsupport() {
        return detailsupport;
    }
    
    public void setDetailsupport(boolean detailsupport) {
        this.detailsupport = detailsupport;
    }
}
