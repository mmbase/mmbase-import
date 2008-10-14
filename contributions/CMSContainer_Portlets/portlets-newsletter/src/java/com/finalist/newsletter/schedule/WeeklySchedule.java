package com.finalist.newsletter.schedule;


public class WeeklySchedule extends AbstractSchedule {

   /**
    * the expression 3|hour|minute|interval|week
    * week is a String ,e.g. 1000001 ,"1" express being selected,"0" opposite
    */
   @Override
   public String transform() {

      StringBuffer expression = new StringBuffer("3");
      if (getParameters() != null) {
         appendHourAndMin(expression);

         if (getParameters().containsKey("interval")) {
            expression.append("|" + getParameters().get("interval"));
         }

         if (getParameters().containsKey("weeks")) {
            expression.append("|" + getParameters().get("weeks"));
         }
      }
      return expression.toString();
   }
}
