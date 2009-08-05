package com.finalist.cmsc.forms.validation;

import java.util.List;

import com.finalist.cmsc.forms.definition.GuiField;
import com.finalist.cmsc.forms.value.ValueField;
import com.finalist.cmsc.forms.value.ValueObject;


public abstract class FieldsetValidator extends FieldValidator {

   public FieldsetValidator() {
      super("validator.fieldset.message");
   }

   /**
    * Validates the submitted values based on the specified configuration.
    * @param fields list of fields in the fieldset
    * @param objects list of objects to validate
    * @return true when validation succeeds, false otherwise.
    */
   public abstract boolean validate(List<GuiField> fields, ValueObject objects);

   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   @SuppressWarnings("unused")
   public boolean validate(ValueField field) {
      throw new UnsupportedOperationException("Fieldset Validators only validate a set of fields");
   }

}
