package com.finalist.cmsc.repository.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

@SuppressWarnings("serial")
public class AssetUploadForm extends ActionForm {

   private String assetType = "attachment";
   private String parentchannel;
   private FormFile file;

   public String getAssetType() {
      return assetType;
   }

   public void setAssetType(String assetType) {
      this.assetType = assetType;
   }

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
