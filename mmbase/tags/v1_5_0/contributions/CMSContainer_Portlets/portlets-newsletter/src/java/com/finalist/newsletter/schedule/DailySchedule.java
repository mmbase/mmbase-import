package com.finalist.newsletter.schedule;



public class DailySchedule extends AbstractSchedule  {

   /**
    * the Expression is 2|datet|hour|minute|approach[|interval]
    * approach {0|1|2}
    */
   @Override
   public  String transform() {
      StringBuffer expression = new StringBuffer("2");
      String approach = null;
      if(getParameters() != null && getParameters().containsKey("date")) {
         expression.append("|"+getParameters().get("date"));
      }
      appendHourAndMin(expression);
      if(getParameters() != null && getParameters().containsKey("approach")) {
         approach = (String)getParameters().get("approach");
      }
      if(approach != null) {
         expression.append("|"+approach);

         if(approach.equals("2")) {
            if(getParameters().containsKey("interval")) {
               expression.append("|"+getParameters().get("interval"));
            }
         }
      }
      return expression.toString();
   }
}
