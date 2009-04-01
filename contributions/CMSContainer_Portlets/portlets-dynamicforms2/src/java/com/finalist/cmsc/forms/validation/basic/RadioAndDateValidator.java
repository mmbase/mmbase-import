package com.finalist.cmsc.forms.validation.basic;

import java.util.List;

import com.finalist.cmsc.forms.definition.GuiField;
import com.finalist.cmsc.forms.validation.FieldsetValidator;
import com.finalist.cmsc.forms.value.ValueField;
import com.finalist.cmsc.forms.value.ValueObject;


public class RadioAndDateValidator extends FieldsetValidator {

   /** 
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(List<GuiField> fields, ValueObject object) {
      GuiField radio = fields.get(0);
      GuiField date = fields.get(1);
      ValueField activateField = object.getField(radio.getName());
      if ("now".equals(activateField.getStringValue())) {
         ValueField dateField = object.getField(date.getName());
         dateField.setValue("");
         return true;
      }
      else
         if ("from".equals(activateField.getStringValue())) {
            ValueField dateField = object.getField(date.getName());
            boolean valid = dateField.getStringValue() != null
            && !"".equals(dateField.getStringValue());
            if (!valid) {
               dateField.setValidationError(getErrorMessage());
            }
            return valid;
         }
         else {
            return true;
         }
   }

}
