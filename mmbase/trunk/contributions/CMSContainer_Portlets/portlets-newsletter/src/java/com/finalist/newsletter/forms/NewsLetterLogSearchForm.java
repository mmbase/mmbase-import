package com.finalist.newsletter.forms;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public class NewsLetterLogSearchForm extends org.apache.struts.action.ActionForm {

   private String newsletters;

   private String startDate;

   private String endDate;

   private String detailOrSum;

   public String getDetailOrSum() {

      return detailOrSum;
   }

   public void setDetailOrSum(String detailorsum) {

      this.detailOrSum = detailorsum;
   }

   public String getNewsletters() {

      return newsletters;
   }

   public void setNewsletters(String newsletters) {

      this.newsletters = newsletters;
   }

   @Override
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

      ActionErrors errors = new ActionErrors();

      if (startDate == "" && endDate != "") {
         this.startDate = "2007-12-30";

      } else if (startDate != "" && endDate == "") {
         Long time = System.currentTimeMillis();
         Date date = new Date(time);
         this.endDate = DateFormat.getDateInstance().format(date);

      }
      return errors;
   }

   public String getStartDate() {

      return startDate;
   }

   public void setStartDate(String startDate) {

      this.startDate = startDate;
   }

   public String getEndDate() {

      return endDate;
   }

   public void setEndDate(String endDate) {

      this.endDate = endDate;
   }

}
