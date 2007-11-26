package com.finalist.cmsc.beans.om;

import net.sf.mmapps.commons.beans.NodeBean;

/**
 * @author Wouter Heijke
 */
@SuppressWarnings("serial")
public class ContentChannel extends NodeBean {

   private String name;

   private String description;


   public String getDescription() {
      return description;
   }


   public void setDescription(String description) {
      this.description = description;
   }


   public String getName() {
      return name;
   }


   public void setName(String name) {
      this.name = name;
   }

}
