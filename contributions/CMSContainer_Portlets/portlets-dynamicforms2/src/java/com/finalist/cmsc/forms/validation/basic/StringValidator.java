package com.finalist.cmsc.forms.validation.basic;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.ValueField;

public class StringValidator extends FieldValidator {

   public StringValidator() {
      super("validator.string.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (!field.isRequired() && "".equals(field.getStringValue())) return true;
      String value = field.getStringValue();
      if (value == null) {
         if (field.getMinlength() > 0) {
            return false;
         }
      }
      else {
         if (field.getMinlength() > value.length()) {
            return false;
         }
         if (field.getMaxlength() < value.length()) {
            return false;
         }
      }
      return true;
   }

   public ValidationError getErrorMessage(ValueField valueField) {
      ValidationError error = super.getErrorMessage();
      error.addParam(valueField.getMinlength());
      error.addParam(valueField.getMaxlength());
      return error;
   }

}
