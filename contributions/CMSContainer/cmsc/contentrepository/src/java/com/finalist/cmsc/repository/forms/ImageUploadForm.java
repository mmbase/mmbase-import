package com.finalist.cmsc.repository.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

@SuppressWarnings("serial")
public class ImageUploadForm extends ActionForm {

   private String parentchannel="723";
   private FormFile file;

   public String getParentchannel() {
      return parentchannel;
   }

   public void setParentchannel(String parentchannel) {
      this.parentchannel = parentchannel;
   }

   public FormFile getFile() {
      return file;
   }

   public void setFile(FormFile file) {
      this.file = file;
   }

}
