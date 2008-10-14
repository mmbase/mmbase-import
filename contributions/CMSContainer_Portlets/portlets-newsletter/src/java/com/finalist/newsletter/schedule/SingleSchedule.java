package com.finalist.newsletter.schedule;


public class SingleSchedule extends AbstractSchedule {

   /**
    * the expression is 1|date|hour|minute
    */
   @Override
   public String transform() {

      StringBuffer expression = new StringBuffer("1");
      if (getParameters() != null && getParameters().containsKey("date")) {
         expression.append("|" + getParameters().get("date"));
      }
      appendHourAndMin(expression);
      return expression.toString();
   }
}
