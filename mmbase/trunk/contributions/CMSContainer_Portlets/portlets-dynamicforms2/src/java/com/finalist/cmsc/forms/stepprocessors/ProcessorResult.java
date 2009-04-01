package com.finalist.cmsc.forms.stepprocessors;

public class ProcessorResult {

   protected String activeStep;

   protected String editPath;

   public ProcessorResult() {
      // nothing
   }

   public ProcessorResult(String activeStep, String editPath) {
      this.activeStep = activeStep;
      this.editPath = editPath;
   }

   public String getActiveStep() {
      return activeStep;
   }

   public void setActiveStep(String activeStep) {
      this.activeStep = activeStep;
   }

   public String getEditPath() {
      return editPath;
   }

   public void setEditPath(String editPath) {
      this.editPath = editPath;
   }

}
