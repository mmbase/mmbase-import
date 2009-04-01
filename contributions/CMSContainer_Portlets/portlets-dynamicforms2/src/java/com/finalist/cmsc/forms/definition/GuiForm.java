package com.finalist.cmsc.forms.definition;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.finalist.cmsc.forms.stepprocessors.ProcessorResult;
import com.finalist.cmsc.forms.value.ValueForm;
import com.finalist.cmsc.forms.value.ValueObject;
import com.finalist.cmsc.util.XmlUtil;

public final class GuiForm {

   private final static Log Log = LogFactory.getLog(GuiForm.class);

   private Map<String, GuiStep> steps = new LinkedHashMap<String, GuiStep>();
   private String title;

   public void addStep(GuiStep step) {
      String name = step.getName();
      if (steps.containsKey(name)) {
         Log.error("step with name '" + name + "' already defined");
      }
      else {
         steps.put(name, step);
      }
   }

   public GuiStep getFirstStep() {
      return steps.values().iterator().next();
   }
   
   public void postLoad(DataObject data) {
      for (GuiStep step : steps.values()) {
         step.postLoad(data);
      }
      for (GuiStep step : steps.values()) {
         String elements = step.getElements();
         if (elements != null && elements.length() > 0) {
            StringTokenizer tokenizer = new StringTokenizer(elements, ", \t\n\r\f");
            while (tokenizer.hasMoreTokens()) {
               String inheritStep = tokenizer.nextToken();
               GuiStep inheritGuiStep = steps.get(inheritStep);
               List<Object> inheritElements = inheritGuiStep.getStepelements();
               step.getStepelements().addAll(inheritElements);
            }
         }
      }
   }

   public void createData(String activeStep, ValueObject valueObject, String editPath, Map<String, String[]> params) {
      GuiStep step = steps.get(activeStep);
      if (step != null) {
         step.createData(valueObject, editPath, params);
      }
      else {
         throw new IllegalArgumentException("Step '" + activeStep + "' not found in form");
      }
   }

   public String createDataObjects(String activeStep, ValueObject valueObject) {
      GuiStep step = steps.get(activeStep);
      if (step != null) {
         return step.createDataObjects(valueObject);
      }
      else {
         throw new IllegalArgumentException("Step '" + activeStep + "' not found in form");
      }
   }

   public ProcessorResult executeStepProcessor(String activeStep, ValueObject valueObject,
         Map<String, List<String>> parameters, ValueForm valueForm) {
      GuiStep step = steps.get(activeStep);
      if (step != null) {
         return step.executeStepProcessor(valueObject, parameters, valueForm);
      }
      else {
         throw new IllegalArgumentException("Step '" + activeStep + "' not found in form");
      }
   }

   public String getStepPath(String activeStep) {
      GuiStep step = steps.get(activeStep);
      return step.getPath();
   }

   public String getTitle() {
      return title;
   }

   public boolean isDisplayOnly(String aStep) {
      return steps.get(aStep).isDisplayOnly();
   }

   public boolean isFinalStep(String finalStep) {
      return steps.get(finalStep).isFinalstep();
   }

   public boolean isValidStep(String validateStep, ValueObject object, String editpath) {
      GuiStep step = steps.get(validateStep);
      if (step != null) {
         return step.isValid(object, editpath);
      }
      throw new IllegalArgumentException("Step '" + validateStep + "' not found in form");
   }

   public void render(Document doc, ValueObject object, String activeStep, String namePath,
         String editPath, String processorError, int expectedRenderSequence) {
      Element form = toXml(doc);

      GuiStep step = steps.get(activeStep);
      if (step != null) {
         step.render(form, object, namePath, editPath, processorError, expectedRenderSequence);
      }
      else {
         throw new IllegalArgumentException("Step '" + activeStep + "' not found in form");
      }
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Element toXml(Document doc) {
      Element form = XmlUtil.createRoot(doc, "form");
      XmlUtil.createAttribute(form, "title", title);
      return form;
   }

}
