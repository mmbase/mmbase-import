package com.finalist.cmsc.tasks.forms;

import com.finalist.cmsc.struts.MMBaseForm;

@SuppressWarnings("serial")
public class ShowTaskForm extends MMBaseForm {

   private String taskShowType = "";
   private String showFinishedTask = "no";

   public String getTaskShowType() {
      return taskShowType;
   }

   public void setTaskShowType(String taskShowType) {
      this.taskShowType = taskShowType;
   }

   public String getShowFinishedTask() {
      return showFinishedTask;
   }

   public void setShowFinishedTask(String showFinishedTask) {
      this.showFinishedTask = showFinishedTask;
   }

}
