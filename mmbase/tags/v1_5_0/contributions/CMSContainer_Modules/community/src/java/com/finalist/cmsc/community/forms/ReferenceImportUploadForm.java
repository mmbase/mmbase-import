package com.finalist.cmsc.community.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class ReferenceImportUploadForm extends ActionForm {

   private FormFile datafile;
   private String level;

   public String getLevel() {
      return level;
   }

   public void setLevel(String level) {
      this.level = level;
   }

   public FormFile getDatafile() {
      return datafile;
   }

   public void setDatafile(FormFile datafile) {
      this.datafile = datafile;
   }
}