package com.finalist.cmsc.forms.definition;

import java.util.*;

import com.finalist.cmsc.forms.formprocessors.FormProcessor;
import com.finalist.cmsc.forms.stepprocessors.ProcessorResult;
import com.finalist.cmsc.forms.value.ValueForm;
import com.finalist.cmsc.forms.value.ValueObject;

public final class FormDefinition {

   private DataObject dataObject;

   private GuiForm form;

   private List<FormProcessor> processors = new ArrayList<FormProcessor>();

   public FormDefinition() {
      // nothing
   }

   public void addDataObject(DataObject object) {
      this.dataObject = object;
   }

   public void postLoad() {
      dataObject.postLoad();
      form.postLoad(dataObject);
   }

   public void createData(String activeStep, ValueObject valueObject, String editPath, Map<String, String[]> params) {
      form.createData(activeStep, valueObject, editPath, params);
   }

   public String createDataObjects(String activeStep, ValueObject valueObject) {
      return form.createDataObjects(activeStep, valueObject);
   }

   public ValueForm createValueForm() {
      return new ValueForm(this);
   }

   @Override
   public boolean equals(Object oth) {
      if (this == oth) {
         return true;
      }

      if (oth == null) {
         return false;
      }

      if (oth.getClass() != getClass()) {
         return false;
      }

      FormDefinition other = (FormDefinition) oth;
      if (this.dataObject == null) {
         if (other.dataObject != null) {
            return false;
         }
      }
      else {
         if (!this.dataObject.equals(other.dataObject)) {
            return false;
         }
      }
      if (this.form == null) {
         if (other.form != null) {
            return false;
         }
      }
      else {
         if (!this.form.equals(other.form)) {
            return false;
         }
      }

      return true;
   }

   public ProcessorResult executeStepProcessor(String activeStep, ValueObject valueObject,
         Map<String, List<String>> parameters, ValueForm valueForm) {
      return form.executeStepProcessor(activeStep, valueObject, parameters, valueForm);
   }

   public DataObject getDataObject() {
      return dataObject;
   }

   public GuiForm getForm() {
      return form;
   }

   public List<FormProcessor> getProcessors() {
      return processors;
   }

   @Override
   public int hashCode() {
      final int PRIME = 1000003;
      int result = 0;
      if (dataObject != null) {
         result = PRIME * result + dataObject.hashCode();
      }
      if (form != null) {
         result = PRIME * result + form.hashCode();
      }

      return result;
   }

   public void setForm(GuiForm form) {
      this.form = form;
   }

   public void addProcessor(FormProcessor processor) {
      this.processors.add(processor);
   }

   @Override
   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("[FormDefinition:");
      buffer.append(" dataObject: ");
      buffer.append(dataObject);
      buffer.append(" form: ");
      buffer.append(form);
      buffer.append("]");
      return buffer.toString();
   }

}
