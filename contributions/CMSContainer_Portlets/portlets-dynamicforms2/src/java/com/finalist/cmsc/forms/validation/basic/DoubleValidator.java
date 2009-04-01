package com.finalist.cmsc.forms.validation.basic;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.ValueField;

public class DoubleValidator extends FieldValidator {

   public DoubleValidator() {
      super("validator.double.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (!field.isRequired() && "".equals(field.getStringValue())) return true;
      double value = field.getDoubleValue();
      return field.getMin() <= value && value <= field.getMax();
   }

   @Override
   public ValidationError getErrorMessage(ValueField valueField) {
      ValidationError error = super.getErrorMessage();
      error.addParam(valueField.getMin());
      error.addParam(valueField.getMax());
      return error;
   }
}
