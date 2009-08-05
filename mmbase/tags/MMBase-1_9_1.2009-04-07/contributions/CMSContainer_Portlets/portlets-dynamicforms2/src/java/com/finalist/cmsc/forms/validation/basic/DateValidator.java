package com.finalist.cmsc.forms.validation.basic;


import org.apache.commons.validator.GenericValidator;

import com.finalist.cmsc.forms.definition.DataType;
import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.ValueField;

public class DateValidator extends FieldValidator {

   public DateValidator() {
      super("validator.date.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      return GenericValidator.isDate(field.getStringValue(), DataType.DATE_PATTERN, true);
   }

   @Override
   public ValidationError getErrorMessage() {
      ValidationError error = super.getErrorMessage();
      error.addParam(DataType.DATE_PATTERN);
      return error;
   }

}
