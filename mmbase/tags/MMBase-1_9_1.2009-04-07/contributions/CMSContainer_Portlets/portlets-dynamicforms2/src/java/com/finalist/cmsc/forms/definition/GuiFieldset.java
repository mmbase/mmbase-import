package com.finalist.cmsc.forms.definition;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.finalist.cmsc.forms.validation.FieldsetValidator;
import com.finalist.cmsc.forms.value.ValueObject;
import com.finalist.cmsc.util.XmlUtil;

public final class GuiFieldset {

   private List<GuiField> fields = new ArrayList<GuiField>();

   private String guitype;

   private String title;

   private List<FieldsetValidator> validators = new ArrayList<FieldsetValidator>();


   public void addField(GuiField field) {
      fields.add(field);
   }

   public void addValidator(FieldsetValidator validator) {
      validators.add(validator);
   }

   public void postLoad(DataObject dataObject) {
      for (GuiField field : fields) {
         field.postLoad(dataObject);
      }
   }

   public boolean isValid(ValueObject object) {
      boolean valid = true;
      for (FieldsetValidator validator : validators) {
         valid = validator.validate(fields, object);
      }
      for (GuiField field : fields) {
         if (!field.isValid(object)) {
            valid = false;
         }
      }
      return valid;
   }

   public void render(Element root, ValueObject object, String namePath) {
      Element fieldset = toXml(root);
      for (GuiField field : fields) {
         field.render(fieldset, object, namePath);
      }
   }

   public void setGuitype(String guitype) {
      this.guitype = guitype;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Element toXml(Element root) {
      Element fieldset = XmlUtil.createChild(root, "fieldset");
      XmlUtil.createAttribute(fieldset, "guitype", guitype);
      XmlUtil.createAttribute(fieldset, "title", title);
      return fieldset;
   }

}
