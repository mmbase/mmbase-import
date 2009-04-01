package com.finalist.cmsc.forms.validation.basic;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.value.ValueField;

public class PostCodeValidator extends FieldValidator {

   private static final String POSTCODE_REGEXP = "^[123456789]\\d{3}\\s*[ABCDEGHJKLMNPRSTVWXZabcdeghjklmnprstvwxz]{2}$";

   private final static Log Log = LogFactory.getLog(PostCodeValidator.class);

   public PostCodeValidator() {
      super("validator.postcode.message");
   }
   
   /** 
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (!field.isRequired() && "".equals(field.getStringValue())) return true;
      boolean result = false;
      try {
         result = field.getStringValue().matches(POSTCODE_REGEXP);
         Log.debug("Validation result: " + result);
         return result;
      }
      catch (PatternSyntaxException e) {
         Log.error("Regular expression is not valid: " + POSTCODE_REGEXP, e);
      }
      return false;
   }
}
