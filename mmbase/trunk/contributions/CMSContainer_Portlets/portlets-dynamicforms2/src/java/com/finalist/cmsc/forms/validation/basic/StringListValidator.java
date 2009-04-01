package com.finalist.cmsc.forms.validation.basic;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.value.ValueField;

public class StringListValidator extends FieldValidator {

   public StringListValidator() {
      super("validator.stringlist.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (field.isRequired()) {
         Object value = field.getValue();
         if (value == null) {
            return false;
         }
         else {
            return true;
         }
      }
      else {
         return true;
      }
   }

}
