package com.finalist.cmsc.community.forms;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class ReferenceImportUploadForm extends ActionForm {

   private FormFile datafile;
   private FormFile file;
   private String level;
   private String group;
   private String groups;
   

   public String getGroups() {
      return groups;
   }

   public void setGroups(String groups) {
      this.groups = groups;
   }

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

   public String getGroup() {
      return group;
   }

   public void setGroup(String group) {
      this.group = group;
   }

   public FormFile getFile() {
      return file;
   }

   public void setFile(FormFile file) {
      this.file = file;
   }
}