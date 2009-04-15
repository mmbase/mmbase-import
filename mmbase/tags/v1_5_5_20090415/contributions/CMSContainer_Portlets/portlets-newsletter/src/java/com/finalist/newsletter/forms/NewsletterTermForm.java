package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

/**
  *
  * newsletter term form
  * @author Lisa
  */
public class NewsletterTermForm extends ActionForm {

   private static final long serialVersionUID = 1L;

   private String name;

   private String number;

   private String offset;

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name
    *           the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the number
    */
   public String getNumber() {
      return number;
   }

   /**
    * @param number
    *           the number to set
    */
   public void setNumber(String number) {
      this.number = number;
   }

   /**
    * @return the offset
    */
   public String getOffset() {
      return offset;
   }

   /**
    * @param offset
    *           the offset to set
    */
   public void setOffset(String offset) {
      this.offset = offset;
   }

   /** reset the name */
   public void reset() {
      name = null;
   }

   /** reset all */
   public void clear() {
      name = null;
      offset = null;
      number = null;
   }
}
