package com.finalist.cmsc.forms.value;

import java.util.List;
import java.util.Map;

import javax.activation.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.finalist.cmsc.forms.definition.DataObject;
import com.finalist.cmsc.forms.definition.FormDefinition;
import com.finalist.cmsc.forms.formprocessors.FormProcessor;
import com.finalist.cmsc.forms.stepprocessors.ProcessorResult;

public class ValueForm {

   private final static Log Log = LogFactory.getLog(ValueForm.class);

   /**
    * Definition of form. Contains data structure and gui steps (viewports over the datastructue)
    */
   private FormDefinition definition;

   /**
    * Structure where user data is stored. The structure us based on the data object structure in
    * the definition
    */
   private ValueObject object;

   public ValueForm(FormDefinition definition) {
      this.definition = definition;
      DataObject dataObject = definition.getDataObject();
      object = new ValueObject(dataObject);
   }

   public void render(Document doc, String activeStep, String editpath, String formProcessorError,
         int expectedRenderSequence) {
      definition.getForm().render(doc, object, activeStep, object.getName(), editpath,
            formProcessorError, expectedRenderSequence);
   }

   public FormDefinition getDefinition() {
      return definition;
   }

   public String definitionToString() {
      return definition.toString();
   }

   public ValueObject getObject() {
      return object;
   }

   public String createDataObjects(String activeStep) {
      return definition.createDataObjects(activeStep, object);
   }

   public void createData(String activeStep, String editPath, Map<String, String[]> params) {
      definition.createData(activeStep, object, editPath, params);
   }

   public ProcessorResult executeStepProcessor(String activeStep, Map<String, List<String>> parameters) {
      return definition.executeStepProcessor(activeStep, object, parameters, this);
   }

   public void storeField(String formname, List<String> formvalue) {
      if (formname.startsWith(object.getName())) {
         ValueField field = ValuePathUtil.getFieldFromNamePath(object, formname);
         field.setStringValue(formvalue);
      }
      else {
         Log.debug("Skipping formname '" + formname + "'");
      }
   }

   public void storeField(String formname, DataSource formvalue) {
      if (formname.startsWith(object.getName())) {
         ValueField field = ValuePathUtil.getFieldFromNamePath(object, formname);
         field.setValue(formvalue);
      }
      else {
         Log.debug("Skipping formname '" + formname + "'");
      }
   }
   
   public boolean isValidStep(String lastRenderedStep, String editpath) {
      return definition.getForm().isValidStep(lastRenderedStep, object, editpath);
   }

   public String getStepPath(String activestep) {
      return definition.getForm().getStepPath(activestep);
   }

   public boolean isFinalStep(String validateStep) {
      return definition.getForm().isFinalStep(validateStep);
   }

   public boolean isDisplayonly(String aStep) {
      return definition.getForm().isDisplayOnly(aStep);
   }

   public String processForm(Map<String, List<String>> parameters) {
      StringBuilder sb = new StringBuilder();
      List<FormProcessor> processor = definition.getProcessors();
      for (FormProcessor formProcessor : processor) {
         String result = formProcessor.processForm(object, parameters);
         if (StringUtils.isNotEmpty(result)) {
            sb.append(result);
         }
      }
      return sb.toString();
   }


}
