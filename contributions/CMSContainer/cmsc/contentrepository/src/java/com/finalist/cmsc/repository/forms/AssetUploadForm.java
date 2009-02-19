package com.finalist.cmsc.repository.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

@SuppressWarnings("serial")
public class AssetUploadForm extends ActionForm {

   private String parentchannel;
   private String insertAsset;
   private FormFile file;
   private String strict;

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

   public void setInsertAsset(String insertAsset) {
      this.insertAsset = insertAsset;
   }

   public String getInsertAsset() {
      return insertAsset;
   }

   public String getStrict() {
      return strict;
   }

   public void setStrict(String strict) {
      this.strict = strict;
   }
}
