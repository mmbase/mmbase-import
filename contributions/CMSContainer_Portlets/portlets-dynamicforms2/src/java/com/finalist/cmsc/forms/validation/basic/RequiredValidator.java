package com.finalist.cmsc.forms.validation.basic;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.value.ValueField;

public class RequiredValidator extends FieldValidator {

   public RequiredValidator() {
      super("validator.required.message");
   }

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      if (field == null) {
         return false;
      }
      if (field.getValue() != null) {
         if (field.getValue().getClass().isInstance(new String())) {
            return !"".equals(field.getValue());
         }
         else
            return true;

      }
      else
         return false;
   }

}
