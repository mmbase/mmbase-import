package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

public class NewsletterTermForm extends ActionForm {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   private String name;
   
   private String number;
   
   private String offset;
   

   public String getOffset() {
      return offset;
   }

   public void setOffset(String offset) {
      this.offset = offset;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getNumber() {
      return number;
   }

   public void setNumber(String number) {
      this.number = number;
      
   }
   public void reset(){
      name = null;
   }
   public void clear(){
      name = null;
      offset = null;;
      number = null;
   }
}
