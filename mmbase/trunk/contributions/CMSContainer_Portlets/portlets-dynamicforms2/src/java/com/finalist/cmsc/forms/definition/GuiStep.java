package com.finalist.cmsc.forms.definition;

import java.util.*;

import org.w3c.dom.Element;

import com.finalist.cmsc.forms.dataproviders.DataProvider;
import com.finalist.cmsc.forms.stepprocessors.ProcessorResult;
import com.finalist.cmsc.forms.stepprocessors.StepProcessor;
import com.finalist.cmsc.forms.value.*;
import com.finalist.cmsc.util.XmlUtil;

public final class GuiStep {

   private boolean completedobjects;
   private List<DataProvider> dataProviders = new ArrayList<DataProvider>();
   private boolean displayonly;
   private boolean finalstep;
   private String guitype;
   private String name;
   private GuiNavigation navigation;
   private String path;
   private StepProcessor processor;
   private String elements;
   private List<Object> stepelements = new ArrayList<Object>();
   private GuiStepInfo stepInfo;

   public void addList(GuiList list) {
      stepelements.add(list);
   }

   public void addSection(GuiSection section) {
      stepelements.add(section);
   }

   public void postLoad(DataObject data) {
      DataObject dataObject;
      if (path != null && !"".equals(path)) {
         dataObject = DataObject.getObjectFromPath(data, path);
      }
      else {
         dataObject = data;
      }

      for (Object element : stepelements) {
         if (element instanceof GuiSection){
            ((GuiSection) element).postLoad(dataObject);
         }
         if (element instanceof GuiList){
            ((GuiList) element).postLoad(dataObject);
         }
      }
   }

   public void createData(ValueObject object, String editPath, Map<String, String[]> params) {
      ValueObject valueObject;
      if (editPath != null && !"".equals(editPath)) {
         valueObject = ValuePathUtil.getObjectFromPath(object, editPath);
      }
      else {
         valueObject = object;
      }

      for (DataProvider provider : dataProviders) {
         provider.createData(valueObject, params);
      }
   }

   public String createDataObjects(ValueObject object) {
      String createdPath = "";
      if (!this.completedobjects) {
         ValueObject valueObject;
         if (path != null && !"".equals(path)) {
            createdPath = ValuePathUtil.createPath(object, path);
            valueObject = ValuePathUtil.getObjectFromPath(object, path);
         }
         else {
            valueObject = object;
         }
         String addedPath = null;
         for (int i = 0; i < stepelements.size(); i++) {
            Object stepelement = stepelements.get(i);
            if (stepelement instanceof GuiList) {
               addedPath = ((GuiList) stepelement).createDataObjects(valueObject);
            }
         }
         if (addedPath != null) {
            if (addedPath.startsWith(createdPath))
               createdPath = addedPath;
            else {
               createdPath = ValuePathUtil.getParent(createdPath);
               createdPath = ValuePathUtil.concat(createdPath, addedPath);
            }
         }
      }
      return createdPath;
   }

   public ProcessorResult executeStepProcessor(ValueObject object, Map<String, List<String>> parameters,
         ValueForm valueForm) {
      ValueObject valueObject = null;
      ProcessorResult result = null;

      if (processor != null) {
         String editpath = null;
         List<String> editpatharray = parameters.get("editpath");
         if (editpatharray != null && editpatharray.size() > 0) {
            editpath = editpatharray.get(0);
         }

         if (editpath != null && !"".equals(editpath)) {
            valueObject = ValuePathUtil.getObjectFromPath(object, editpath);
         }
         else {
            // If there is no editpath then the stepprocessor can
            // perform an action on the full
            // valuestructure. Eg, a stepprocessor could be used to
            // delete multiple objects in the
            // valuestructure.
            valueObject = object;
         }
         result = processor.processStep(valueObject, parameters, valueForm);
      }

      return result;
   }

   public boolean getCompletedObjects() {
      return completedobjects;
   }

   public String getGuitype() {
      return guitype;
   }

   public String getName() {
      return name;
   }

   public GuiNavigation getNavigation() {
      return navigation;
   }

   public String getPath() {
      return path;
   }

   public StepProcessor getProcessor() {
      return processor;
   }

   public GuiStepInfo getStepInfo() {
      return stepInfo;
   }

   public boolean isCompletedobjects() {
      return completedobjects;
   }

   public boolean isDisplayonly() {
      return displayonly;
   }


   public boolean isDisplayOnly() {
      return displayonly;
   }


   public boolean isFinalstep() {
      return finalstep;
   }


   public boolean isValid(ValueObject object, String editpath) {
      ValueObject valueObject;
      if (editpath != null && !"".equals(editpath)) {
         valueObject = ValuePathUtil.getObjectFromPath(object, editpath);
      }
      else {
         if (path != null && !"".equals(path)) {
            valueObject = ValuePathUtil.getObjectFromPath(object, path);
         }
         else {
            valueObject = object;
         }
      }
      boolean valid = true;
      for (int i = 0; i < stepelements.size(); i++) {
         Object stepelement = stepelements.get(i);
         if (stepelement instanceof GuiSection) {
            if (!((GuiSection) stepelement).isValid(valueObject)) {
               valid = false;
            }
         }
         if (stepelement instanceof GuiList) {
            if (!((GuiList) stepelement).isValid(valueObject)) {
               valid = false;
            }
         }
      }
      return valid;
   }


   public void render(Element root, ValueObject object, String namePath, String editPath,
         String processorError, int expectedRenderSequence) {

      Element step = toXml(root);
      XmlUtil.createAttribute(step, "active", true);
      XmlUtil.createAttribute(step, "editpath", editPath);
      XmlUtil.createAttribute(step, "guitype", guitype);
      XmlUtil.createAttribute(step, "sequence", expectedRenderSequence);

      // include if there were processor problems to allow for different xsl translation
      boolean errors = processorError != null && !("".equals(processorError));
      XmlUtil.createAttribute(step, "processor-errors", errors);

      if (stepInfo != null) {
         stepInfo.render(step);
      }

      ValueObject valueObject = null;
      if (path != null && !"".equals(path)) {
         if (path.equals(namePath))
            valueObject = object;
         else {
            if (editPath == null) {
               valueObject = ValuePathUtil.getObjectFromPath(object, path);
               namePath = ValuePathUtil.createNamePath(namePath, path);
            }
            else {
               valueObject = ValuePathUtil.getObjectFromPath(object, editPath);
               namePath = ValuePathUtil.createNamePath(namePath, editPath);
            }
         }
      }
      else {
         valueObject = object;
      }

      for (int i = 0; i < stepelements.size(); i++) {
         Object stepelement = stepelements.get(i);
         if (stepelement instanceof GuiSection) {
            ((GuiSection) stepelement).render(step, valueObject, namePath, this.completedobjects);
         }
         if (stepelement instanceof GuiList) {
            ((GuiList) stepelement).render(step, valueObject, namePath, this.completedobjects);
         }
      }
      if (navigation != null) {
         navigation.render(step);
      }
   }


   public void setCompletedobjects(boolean completedobjects) {
      this.completedobjects = completedobjects;
   }


   public void setDisplayonly(boolean displayonly) {
      this.displayonly = displayonly;
   }


   public void setFinalstep(boolean finalstep) {
      this.finalstep = finalstep;
   }


   public void setGuitype(String guitype) {
      this.guitype = guitype;
   }


   public void setName(String name) {
      this.name = name;
   }


   public void setNavigation(GuiNavigation navigation) {
      this.navigation = navigation;
   }


   public void setPath(String path) {
      this.path = path;
   }


   public void setProcessor(StepProcessor processor) {
      this.processor = processor;
   }


   public void setStepInfo(GuiStepInfo stepInfo) {
      this.stepInfo = stepInfo;
   }

   public String getElements() {
      return elements;
   }

   public void setElements(String sections) {
      this.elements = sections;
   }

   public List<Object> getStepelements() {
      return stepelements;
   }

   public void setStepelements(List<Object> stepelements) {
      this.stepelements = stepelements;
   }

   public Element toXml(Element root) {
      Element step = XmlUtil.createChild(root, "formstep");
      XmlUtil.createAttribute(step, "name", name);
      XmlUtil.createAttribute(step, "path", path);
      return step;
   }

}
