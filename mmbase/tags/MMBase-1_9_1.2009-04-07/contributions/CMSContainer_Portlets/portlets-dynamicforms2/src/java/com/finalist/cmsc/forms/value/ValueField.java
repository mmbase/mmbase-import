package com.finalist.cmsc.forms.value;

import java.util.*;

import com.finalist.cmsc.forms.definition.DataField;
import com.finalist.cmsc.forms.definition.DataTypeConversionException;
import com.finalist.cmsc.forms.validation.ValidationError;

public class ValueField {

   private DataField definition;

   private Object value;

   private ValidationError validationError;

   public ValueField(DataField definition) {
      if (definition == null) {
         throw new IllegalArgumentException("Field definition can't be null");
      }
      this.definition = definition;
   }

   public String getName() {
      return definition.getName();
   }

   @Override
   public int hashCode() {
      final int PRIME = 1000003;
      int result = 0;
      if (definition != null) {
         result = PRIME * result + definition.hashCode();
      }
      if (value != null) {
         result = PRIME * result + value.hashCode();
      }

      return result;
   }

   @Override
   public boolean equals(Object oth) {
      if (this == oth) {
         return true;
      }

      if (oth == null) {
         return false;
      }

      if (oth.getClass() != getClass()) {
         return false;
      }

      ValueField other = (ValueField) oth;
      if (this.definition == null) {
         if (other.definition != null) {
            return false;
         }
      }
      else {
         if (!this.definition.equals(other.definition)) {
            return false;
         }
      }
      if (this.value == null) {
         if (other.value != null) {
            return false;
         }
      }
      else {
         if (!this.value.equals(other.value)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("[ValueField:");
      buffer.append(" name: ");
      buffer.append(definition.getName());
      buffer.append(" value: ");
      buffer.append(value);
      buffer.append("]");
      return buffer.toString();
   }

   public Object getValue() {
      if (value == null) {
         return null;
      }
      return value;
   }

   public void setStringValue(List<String> formvalue) {
      try {
         value = definition.getType().convertValue(formvalue);
         setValidationError(null);
      }
      catch (DataTypeConversionException dtce) {
         setDataTypeError(dtce.getErrormessage());
         value = dtce.getValue();
      }
   }
   
   public void setValue(Object formvalue) {
      value = formvalue;
      setValidationError(null);
   }
   
   public void setValue(String formvalue) {
      try {
         if (formvalue != null) formvalue = formvalue.trim();
         List<String> values = new ArrayList<String>();
         values.add(formvalue);
         value = definition.getType().convertValue(values);
         setValidationError(null);
      }
      catch (DataTypeConversionException dtce) {
         setDataTypeError(dtce.getErrormessage());
         value = dtce.getValue();
      }
   }


   public Boolean isArray() {
      if (value == null) return null;
      return new Boolean(value.getClass().isArray());
   }

   public int getIntValue() {
      if (value instanceof Integer) {
         return ((Integer) value).intValue();
      }
      if (value instanceof Long) {
         return ((Long) value).intValue();
      }
      return 0;
   }

   public long getLongValue() {
      if (value instanceof Integer) {
         return ((Integer) value).longValue();
      }
      if (value instanceof Long) {
         return ((Long) value).longValue();
      }
      return 0L;
   }

   public double getDoubleValue() {
      if (value instanceof Float) {
         return ((Float) value).doubleValue();
      }
      if (value instanceof Double) {
         return ((Double) value).doubleValue();
      }
      return 0D;
   }

   public String getStringValue() {
      if (value == null) {
         return null;
      }
      return value.toString();
   }

   /**
    * @return Returns the validationError.
    */
   public ValidationError getValidationError() {
      return validationError;
   }

   /**
    * @param validationError
    *           The validationError to set.
    */
   public void setValidationError(ValidationError validationError) {
      this.validationError = validationError;
   }

   public void setDataTypeError(String errormessage) {
      ValidationError error = new ValidationError("datatype.conversion", errormessage);
      setValidationError(error);
   }
   
   public double getMax() {
      return definition.getMax();
   }

   public double getMaxlength() {
      return definition.getMaxlength();
   }

   public double getMin() {
      return definition.getMin();
   }

   public double getMinlength() {
      return definition.getMinlength();
   }

   public boolean isRequired() {
      return definition.isRequired();
   }

}
