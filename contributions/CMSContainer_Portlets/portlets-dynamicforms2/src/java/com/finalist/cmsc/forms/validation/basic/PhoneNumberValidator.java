package com.finalist.cmsc.forms.validation.basic;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.value.ValueField;

public class PhoneNumberValidator extends FieldValidator {

   private final static Log Log = LogFactory.getLog(PhoneNumberValidator.class);

   private static final String MSISDN_REGEXP = "^[()\\d -]*$";

   public PhoneNumberValidator() {
      super("validator.phone.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (!field.isRequired() && "".equals(field.getStringValue())) return true;
      boolean result = false;
      try {
         result = field.getStringValue().matches(MSISDN_REGEXP);
         Log.debug("Validation result: " + result);
         return result;
      }
      catch (PatternSyntaxException e) {
         Log.error("Regularexpression is not valid: " + MSISDN_REGEXP, e);
      }
      return false;
   }

}
