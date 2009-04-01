package com.finalist.cmsc.forms.validation.basic;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.value.ValueField;

public class RegexValidator extends FieldValidator {

   private final static Log Log = LogFactory.getLog(RegexValidator.class);

   private String regexp;

   public RegexValidator(String datapattern) {
      super("validator.regex.message");
      if (datapattern == null) {
         Log.warn("No regular expression passed to validator!");
      }
      this.regexp = datapattern;
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      // it's also possible to set the pattern as a property (defined in GuiField)
      if (regexp == null || "".equals(regexp)) {
         Log.error("Regular expression for validation is empty");
      }

      if (!field.isRequired()) {
         if (field.getStringValue() == null || "".equals(field.getStringValue())) {
            return true;
         }
      }
      else {
         if (field.getStringValue() == null || "".equals(field.getStringValue())) {
            return false;
         }
      }

      if (Log.isDebugEnabled()) {
         Log.debug("Regular expression for validation: " + regexp);
         Log.debug("String for validation: " + field.getStringValue());
      }
      try {
         boolean result = field.getStringValue().matches(regexp);
         Log.debug("Validation result: " + result);
         return result;
      }
      catch (PatternSyntaxException e) {
         Log.error("Regularexpression is not valid: " + regexp, e);
      }
      return false;
   }

}
