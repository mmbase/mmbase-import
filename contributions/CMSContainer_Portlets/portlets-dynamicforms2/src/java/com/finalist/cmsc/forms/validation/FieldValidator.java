package com.finalist.cmsc.forms.validation;

import com.finalist.cmsc.forms.value.ValueField;


public abstract class FieldValidator {

   private String errorKey;
   private String errorMessage;

   public FieldValidator(String key) {
      this.errorKey = key;
   }

   /**
    * Validates the submitted values based on the specified configuration.
    * @param field field to validate
    * @return true when validation succeeds, false otherwise.
    */
   public abstract boolean validate(ValueField field);

   public ValidationError getErrorMessage() {
      return new ValidationError(errorKey, errorMessage);
   }

   /**
    * Get error message for field
    * @param valueField value field
    * @return error message
    */
   public ValidationError getErrorMessage(ValueField valueField) {
      return getErrorMessage();
   }

   /**
    * @param errorMessage
    *           The errorMessage to set.
    */
   public final void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

}
