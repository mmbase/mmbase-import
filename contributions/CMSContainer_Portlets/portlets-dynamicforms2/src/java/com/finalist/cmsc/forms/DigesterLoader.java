/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.forms;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.finalist.cmsc.forms.definition.*;
import com.finalist.cmsc.forms.factories.*;

public class DigesterLoader {

   private static Log log = LogFactory.getLog(DigesterLoader.class);

   public FormDefinition load(String resource) {
      InputStream stream = this.getClass().getResourceAsStream(resource);
      return load(stream);
   }

   public FormDefinition load(InputStream stream) {
      try {
         Digester digester = new Digester();
         digester.setValidating(false);

         configure(digester);
         
         FormDefinition definition = (FormDefinition) digester.parse(stream);
         definition.postLoad();
         return definition;
      }
      catch (IOException e) {
         log.error("", e);
      }
      catch (SAXException e) {
         log.error("", e);
      }
      return null;
   }

   protected void configure(Digester digester) {
      digester.addSetProperty("*/property","name", "value");
      
      digester.addObjectCreate("definition", FormDefinition.class);
      digester.addFactoryCreate("definition/processor", FormProcessorFactory.class);
      digester.addSetNext("definition/processor", "addProcessor");

      digester.addObjectCreate("*/object", DataObject.class);
      digester.addSetProperties("*/object");
      digester.addSetNext("*/object", "addDataObject");
      
      digester.addObjectCreate("*/object/datafield", DataField.class);
      digester.addSetProperties("*/object/datafield");
      digester.addSetNext("*/object/datafield", "addField");

      digester.addObjectCreate("*/optionlist/option", Option.class);
      digester.addSetProperties("*/optionlist/option");
      digester.addSetNext("*/optionlist/option", "addOption");

      digester.addFactoryCreate("*/optionlist/provider", OptionProviderFactory.class);
      digester.addSetNext("*/optionlist/provider", "addOptionProvider");

      digester.addFactoryCreate("*/object/datafield/validator", FieldValidatorFactory.class);
      digester.addSetNext("*/object/datafield/validator", "addValidator");
      
      digester.addObjectCreate("definition/form", GuiForm.class);
      digester.addSetProperties("definition/form");
      digester.addSetNext("definition/form", "setForm");
      
      digester.addObjectCreate("definition/form/formstep", GuiStep.class);
      digester.addSetProperties("definition/form/formstep");
      digester.addSetNext("definition/form/formstep", "addStep");

      digester.addFactoryCreate("*/dataprovider", DataProviderFactory.class);
      digester.addSetNext("*/dataprovider", "addDataProvider");

      digester.addFactoryCreate("*/stepprocessor", StepProcessorFactory.class);
      digester.addSetNext("*/stepprocessor", "addStepProcessor");
      
      digester.addObjectCreate("definition/form/formstep/stepinfo", GuiStepInfo.class);
      digester.addSetProperties("definition/form/formstep/stepinfo");
      digester.addSetNext("definition/form/formstep/stepinfo", "setStepInfo");

      digester.addObjectCreate("*/description", GuiDescription.class);
      digester.addSetProperties("*/description");
      digester.addBeanPropertySetter("*/description", "value");
      digester.addSetNext("*/description", "setDescription");

      digester.addObjectCreate("*/image", GuiImage.class);
      digester.addSetProperties("*/image");
      digester.addSetNext("*/image", "addImage");
      
      digester.addObjectCreate("*/navigation", GuiNavigation.class);
      digester.addSetProperties("*/navigation");
      digester.addSetNext("*/navigation", "setNavigation");

      digester.addObjectCreate("*/navitem", GuiNavItem.class);
      digester.addSetProperties("*/navitem");
      digester.addSetNext("*/navitem", "addItem");

      digester.addObjectCreate("*/section", GuiSection.class);
      digester.addSetProperties("*/section");
      digester.addSetNext("*/section", "addSection");

      digester.addObjectCreate("*/list", GuiList.class);
      digester.addSetProperties("*/list");
      digester.addSetNext("*/list", "addList");

      digester.addObjectCreate("*/fieldset", GuiFieldset.class);
      digester.addSetProperties("*/fieldset");
      digester.addSetNext("*/fieldset", "addFieldset");

      digester.addObjectCreate("*/field", GuiField.class);
      digester.addSetProperties("*/field");
      digester.addBeanPropertySetter("*/field/tooltip", "tooltip");
      digester.addBeanPropertySetter("*/field/rows", "rows");
      digester.addBeanPropertySetter("*/field/cols", "cols");
      digester.addSetNext("*/field", "addField");
      
      digester.addFactoryCreate("*/fieldset/validator", FieldValidatorFactory.class);
      digester.addSetNext("*/fieldset/validator", "addValidator");

      digester.addFactoryCreate("*/field/validator", FieldValidatorFactory.class);
      digester.addSetNext("*/field/validator", "addValidator");
   }
}
