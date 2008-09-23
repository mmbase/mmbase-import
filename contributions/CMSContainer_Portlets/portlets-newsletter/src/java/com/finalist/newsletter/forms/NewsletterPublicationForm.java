package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

/**
 * Formbean of NewsletterPublication
 * 
 * @author Lisa
 * @version
 */
public class NewsletterPublicationForm extends ActionForm {

   private String title;
   private String description;
   private String subject;
   private String intro;

   /**
    * @return the title
    */
   public String getTitle() {
      return title;
   }

   /**
    * @param title
    *           the title to set
    */
   public void setTitle(String title) {
      this.title = title;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }

   /**
    * @param description
    *           the description to set
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return the subject
    */
   public String getSubject() {
      return subject;
   }

   /**
    * @param subject
    *           the subject to set
    */
   public void setSubject(String subject) {
      this.subject = subject;
   }

   /**
    * @return the intro
    */
   public String getIntro() {
      return intro;
   }

   /**
    * @param intro
    *           the intro to set
    */
   public void setIntro(String intro) {
      this.intro = intro;
   }

}
