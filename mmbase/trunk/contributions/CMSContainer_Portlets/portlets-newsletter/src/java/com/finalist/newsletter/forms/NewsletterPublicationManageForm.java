package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;

/**
 * ActionForm Bean of NewsletterPublicationManage
 * 
 * @author Lisa
 */

public class NewsletterPublicationManageForm extends ActionForm {
   private String title;
   private String subject;
   private String period;

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
    * @return the period
    */
   public String getPeriod() {
      return period;
   }

   /**
    * @param period
    *           the period to set
    */
   public void setPeriod(String period) {
      this.period = period;
   }

}
