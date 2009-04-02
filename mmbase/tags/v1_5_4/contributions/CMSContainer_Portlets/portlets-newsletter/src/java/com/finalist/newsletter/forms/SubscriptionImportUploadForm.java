package com.finalist.newsletter.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class SubscriptionImportUploadForm extends ActionForm {
   private FormFile datafile;

   public FormFile getDatafile() {
      return datafile;
   }

   public void setDatafile(FormFile datafile) {
      this.datafile = datafile;
   }
}