package com.finalist.cmsc.resources.forms;

import org.apache.struts.action.ActionForm;

@SuppressWarnings("serial")
public class DeleteSecondaryContentForm extends ActionForm {

   private String objectnumber;
   private String returnurl;


   public String getObjectnumber() {
      return objectnumber;
   }


   public void setObjectnumber(String objectnumber) {
      this.objectnumber = objectnumber;
   }


   public String getReturnurl() {
      return returnurl;
   }


   public void setReturnurl(String returnurl) {
      this.returnurl = returnurl;
   }

}
