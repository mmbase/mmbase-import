package com.finalist.cmsc.forms.definition;

public class DataTypeConversionException extends Exception {

   private static final long serialVersionUID = 1L;

   private String errormessage;

   private String value;

   DataTypeConversionException(String value, String errormessage) {
      this.value = value;
      this.errormessage = errormessage;
   }

   /**
    * @return Returns the errormessage.
    */
   public String getErrormessage() {
      return errormessage;
   }

   /**
    * @return Returns the value.
    */
   public String getValue() {
      return value;
   }

   /**
    * @param errormessage
    *           The errormessage to set.
    */
   public void setErrormessage(String errormessage) {
      this.errormessage = errormessage;
   }

   /**
    * @param value
    *           The value to set.
    */
   public void setValue(String value) {
      this.value = value;
   }
}
