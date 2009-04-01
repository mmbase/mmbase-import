package com.finalist.cmsc.forms.validation.basic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;

import com.finalist.cmsc.forms.definition.DataType;
import com.finalist.cmsc.forms.validation.FieldValidator;
import com.finalist.cmsc.forms.validation.ValidationError;
import com.finalist.cmsc.forms.value.ValueField;

public class DateRangeValidator extends FieldValidator {

   private static Log Log = LogFactory.getLog(DateRangeValidator.class);

   private String future;
   private String before;
   private String after;
   

   public DateRangeValidator() {
      super("validator.date.message");
   }
   
   /**
    * @see com.finalist.cmsc.forms.validation.FieldValidator#validate(com.finalist.cmsc.forms.value.ValueField)
    */
   @Override
   public boolean validate(ValueField field) {
      String fieldValueStr = field.getStringValue();
      if (!GenericValidator.isDate(field.getStringValue(), DataType.DATE_PATTERN, true)) {
         Log.debug("Date is not valid formatted (" + DataType.DATE_PATTERN + ") " + field.getName()
               + " " + fieldValueStr);
         return false;
      }

      try {
         SimpleDateFormat dateFormatter = new SimpleDateFormat(DataType.DATE_PATTERN);
         Date fieldDate = dateFormatter.parse(fieldValueStr);

         if (!isEmpty(future)) {
            if (!isEmpty(before) || !isEmpty(after)) {
               Log.info("future property is set. Ignoring before and after properties.");
            }

            Calendar now = Calendar.getInstance();
            if (Boolean.valueOf(future).booleanValue()) {
               // now.add(Calendar.DATE, -1);

               now.set(Calendar.HOUR_OF_DAY, 23);
               now.set(Calendar.MINUTE, 59);
               now.set(Calendar.SECOND, 59);
               now.set(Calendar.MILLISECOND, 999);

               return now.getTime().before(fieldDate);
            }
            else {
               now.add(Calendar.DATE, 1);

               now.set(Calendar.HOUR_OF_DAY, 0);
               now.set(Calendar.MINUTE, 0);
               now.set(Calendar.SECOND, 0);
               now.set(Calendar.MILLISECOND, 0);

               return now.getTime().after(fieldDate);
            }
         }
         else {
            Date beforeDate = null;
            Date afterDate = null;
            if (!isEmpty(before)) {
               beforeDate = dateFormatter.parse(before);
            }
            if (!isEmpty(after)) {
               afterDate = dateFormatter.parse(after);
            }
            if (beforeDate != null && afterDate != null && beforeDate.before(afterDate)) {
               Log.info("Impossible to meet requirements. before: " + before + " after: "
                     + after);
            }
            else {
               if (beforeDate != null) {
                  if (fieldDate.before(beforeDate)) {
                     return false;
                  }
               }
               if (afterDate != null) {
                  if (fieldDate.after(afterDate)) {
                     return false;
                  }
               }
            }
         }
      }
      catch (ParseException e) {
         Log.debug(field.getName() + " " + fieldValueStr + " " + e.getMessage(), e);
      }
      return true;
   }

   private boolean isEmpty(String futureStr) {
      return futureStr == null || "".equals(futureStr);
   }

   @Override
   public ValidationError getErrorMessage() {
      if (!isEmpty(future)) {
         if (Boolean.valueOf(future).booleanValue()) {
            return new ValidationError("validator.daterange.future"); 
         }
         else {
            return new ValidationError("validator.daterange.past");
         }
      }
      else {
         if (!isEmpty(before) && !isEmpty(after)) {
            ValidationError error = new ValidationError("validator.daterange.between");
            error.addParam(after);
            error.addParam(before);
            return error;
         }
         if (!isEmpty(before)) {
            ValidationError error = new ValidationError("validator.daterange.before");
            error.addParam(before);
            return error;
         }
         if (!isEmpty(after)) {
            ValidationError error = new ValidationError("validator.daterange.after");
            error.addParam(after);
            return error;
         }
      }
      ValidationError error = super.getErrorMessage();
      error.addParam(DataType.DATE_PATTERN);
      return error;
   }

   
   public void setFuture(String future) {
      this.future = future;
   }

   
   public void setBefore(String before) {
      this.before = before;
   }

   
   public void setAfter(String after) {
      this.after = after;
   }

}
