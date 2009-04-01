package com.finalist.cmsc.forms.definition;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;

import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.basic.*;

public final class DataType {

   public static final String DATE_PATTERN = "dd-MM-yyyy";

   private final static Log Log = LogFactory.getLog(DataType.class);

   public static final String TYPE_BOOLEAN = "BOOLEAN";

   public static final String TYPE_DATE = "DATE";

   public static final String TYPE_DOUBLE = "DOUBLE";

   public static final String TYPE_EMAIL = "EMAIL";

   public static final String TYPE_FLOAT = "FLOAT";

   public static final String TYPE_INT = "INT";

   public static final String TYPE_IMAGE = "IMAGE";

   public static final String TYPE_LONG = "LONG";

   public static final String TYPE_MOBILENUMBER = "MOBILENUMBER";

   public static final String TYPE_PHONENUMBER = "PHONENUMBER";

   public static final String TYPE_POSTCODE = "POSTCODE";

   public static final String TYPE_STRING = "STRING";

   public static final String TYPE_STRINGLIST = "STRINGLIST";

   public static final String TYPE_YESNO = "YESNO";

   public static final String TYPE_YES = "YES";


   private static Map<String, DataType> types = new HashMap<String, DataType>();
   static {
      types.put(TYPE_STRING, new DataType(TYPE_STRING));
      types.put(TYPE_STRINGLIST, new DataType(TYPE_STRINGLIST));
      types.put(TYPE_INT, new DataType(TYPE_INT));
      types.put(TYPE_FLOAT, new DataType(TYPE_FLOAT));
      types.put(TYPE_LONG, new DataType(TYPE_LONG));
      types.put(TYPE_DOUBLE, new DataType(TYPE_DOUBLE));
      types.put(TYPE_BOOLEAN, new DataType(TYPE_BOOLEAN));
      types.put(TYPE_PHONENUMBER, new DataType(TYPE_PHONENUMBER));
      types.put(TYPE_MOBILENUMBER, new DataType(TYPE_MOBILENUMBER));
      types.put(TYPE_DATE, new DataType(TYPE_DATE));
      types.put(TYPE_EMAIL, new DataType(TYPE_EMAIL));
      types.put(TYPE_POSTCODE, new DataType(TYPE_POSTCODE));

      types.put(TYPE_YESNO, new DataType(TYPE_YESNO));
      types.put(TYPE_YES, new DataType(TYPE_YES));
      types.put(TYPE_IMAGE, new DataType(TYPE_IMAGE));
   }

   public static DataType valueOf(String type) {
      if (types.containsKey(type.toUpperCase())) {
         return types.get(type.toUpperCase());
      }
      throw new IllegalArgumentException("DataType not found for '" + type.toUpperCase() + "'");
   }

   private String type;

   private FieldValidator validator;

   private DataType(String type) {
      this.type = type;

      if (TYPE_STRING.equals(type)) {
         validator = new StringValidator();
      }
      if (TYPE_STRINGLIST.equals(type)) {
         validator = new StringListValidator();
      }
      if (TYPE_DATE.equals(type)) {
         validator = new DateValidator();
      }
      if (TYPE_INT.equals(type)) {
         validator = new LongValidator();
      }
      if (TYPE_FLOAT.equals(type)) {
         validator = new DoubleValidator();
      }
      if (TYPE_LONG.equals(type)) {
         validator = new LongValidator();
      }
      if (TYPE_DOUBLE.equals(type)) {
         validator = new DoubleValidator();
      }
      if (TYPE_PHONENUMBER.equals(type)) {
         validator = new PhoneNumberValidator();
      }
      if (TYPE_MOBILENUMBER.equals(type)) {
         validator = new MobileNumberValidator();
      }
      if (TYPE_EMAIL.equals(type)) {
         validator = new EmailValidator();
      }
      if (TYPE_POSTCODE.equals(type)) {
         validator = new PostCodeValidator();
      }
   }

   /**
    * Convert values to correct type
    * @param value value to convert
    * @return converted value
    * @throws DataTypeConversionException
    * @throws NumberFormatException
    * @throws NullPointerException
    */
   public Object convertValue(List<String> value) throws DataTypeConversionException {
      if (value.size() < 2) {
         Log.debug("single value");
         if (value != null) {
            String firstValue = value.get(0);
            if (TYPE_STRING.equals(type) || TYPE_EMAIL.equals(type)
                  || TYPE_PHONENUMBER.equals(type) || TYPE_DATE.equals(type)
                  || TYPE_MOBILENUMBER.equals(type) || TYPE_POSTCODE.equals(type)) {
               return firstValue;
            }
            if (TYPE_STRINGLIST.equals(type)) {
               return value;
            }
            if (!"".equals(firstValue.trim())) {
               if (TYPE_INT.equals(type)) {
                  if (GenericValidator.isInt(firstValue))
                     return Integer.valueOf(firstValue);
                  else
                     throw new DataTypeConversionException(firstValue,
                     "De ingevulde waarde moet een heel getal zijn.");
               }
               if (TYPE_FLOAT.equals(type)) {
                  if (GenericValidator.isFloat(firstValue))
                     return Float.valueOf(firstValue);
                  else
                     throw new DataTypeConversionException(firstValue,
                     "De ingevulde waarde moet een getal zijn.");
               }
               if (TYPE_LONG.equals(type)) {
                  if (GenericValidator.isLong(firstValue))
                     return Long.valueOf(firstValue);
                  else
                     throw new DataTypeConversionException(firstValue,
                     "De ingevulde waarde moet een heel getal zijn.");
               }
               if (TYPE_DOUBLE.equals(type)) {
                  if (GenericValidator.isDouble(firstValue))
                     return Double.valueOf(firstValue);
                  else
                     throw new DataTypeConversionException(firstValue,
                     "De ingevulde waarde moet een getal zijn.");
               }
               if (TYPE_BOOLEAN.equals(type)) {
                  return Boolean.valueOf(firstValue);
               }
            }
         }
      }
      else {
         Log.debug("non single value");
         return value;
      }
      return null;
   }

   public FieldValidator getValidator() {
      return validator;
   }

   @Override
   public String toString() {
      return type;
   }
}
