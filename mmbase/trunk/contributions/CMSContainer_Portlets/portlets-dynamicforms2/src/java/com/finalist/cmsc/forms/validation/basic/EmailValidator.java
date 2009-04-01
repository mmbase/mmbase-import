package com.finalist.cmsc.forms.validation.basic;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.value.ValueField;

public class EmailValidator extends FieldValidator {

   private final static Log Log = LogFactory.getLog(EmailValidator.class);

   public EmailValidator() {
      super("validator.email.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (!field.isRequired() && "".equals(field.getStringValue())) return true;
      boolean result = false;
      result = GenericValidator.isEmail(field.getStringValue());
      Log.debug("Validation result: " + result);
      return result;
   }

}
