package com.finalist.cmsc.resources.forms;

@SuppressWarnings("serial")
public class UrlForm extends SearchForm {

   private String name;
   private String description;
   private String url;
   private String valid;


   public UrlForm() {
      super("urls", "name");
   }


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


   public String getUrl() {
      return url;
   }


   public void setUrl(String url) {
      this.url = url;
   }


   public String getValid() {
      return valid;
   }


   public void setValid(String valid) {
      this.valid = valid;
   }

}
