package com.finalist.cmsc.forms.validation.basic;

import java.math.BigDecimal;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.ValueField;


public class LongValidator extends FieldValidator {

   public LongValidator() {
      super("validator.long.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (!field.isRequired() && "".equals(field.getStringValue())) return true;
      long value = field.getLongValue();
      return field.getMin() <= value && value <= field.getMax();
   }

   @Override
   public ValidationError getErrorMessage(ValueField valueField) {
      ValidationError error = super.getErrorMessage();
      BigDecimal min = new BigDecimal(valueField.getMin());
      BigDecimal max = new BigDecimal(valueField.getMax());
      error.addParam(min);
      error.addParam(max);
      return error;
   }

}
