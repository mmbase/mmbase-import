package com.finalist.newsletter.schedule;


public class MonthSchedule extends AbstractSchedule {

   /**
    * the expression is 4|hour|minute|strategy following |day or  |whichweek|week|month
    * which week {1|2|3|4|5}
    * week   {1|2|3|4|5|6|7}
    * month  e.g. 1000000000000 12 bit ,"1" ,selected,"0" no selected
    */
   @Override
   public String transform() {

      StringBuffer expression = new StringBuffer("4");

      if (getParameters() != null) {
         appendHourAndMin(expression);

         if (getParameters().containsKey("strategy")) {
            expression.append("|" + getParameters().get("strategy"));

            if (getParameters().get("strategy").equals("0")) {
               expression.append("|" + getParameters().get("day"));
            } else if (getParameters().get("strategy").equals("1")) {
               expression.append("|" + getParameters().get("whichweek"));
               expression.append("|" + getParameters().get("week"));
            }
         }
         if (getParameters().containsKey("month")) {
            expression.append("|" + getParameters().get("month"));
         }
      }

      return expression.toString();
   }

}
