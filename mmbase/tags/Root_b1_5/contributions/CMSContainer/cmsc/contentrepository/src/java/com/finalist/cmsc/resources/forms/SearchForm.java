package com.finalist.cmsc.resources.forms;

import com.finalist.cmsc.struts.PagerForm;

@SuppressWarnings("serial")
public class SearchForm extends PagerForm {

   private String contenttypes;
   private String objectid;


   public SearchForm() {
      this(null, null);
   }


   public SearchForm(String contenttypes) {
      this(contenttypes, null);
   }


   public SearchForm(String contenttypes, String defaultOrder) {
      super(defaultOrder);
      this.contenttypes = contenttypes;
   }


   public String getContenttypes() {
      return contenttypes;
   }


   public void setContenttypes(String contenttypes) {
      this.contenttypes = contenttypes;
   }


   public String getObjectid() {
      return objectid;
   }


   public void setObjectid(String objectid) {
      this.objectid = objectid;
   }

}
