package com.finalist.newsletter.schedule;

import java.util.Date;
import java.util.Map;


public class DailySchedule extends AbstractSchedule  {

   /**
    * the Expression is 2|datetime|approach[|interval]
    * approach {0|1|2}
    */
   @Override
   public  String chansfer() {
      StringBuffer target = new StringBuffer("2");
      String approach = null;
      if(getParameters() != null && getParameters().containsKey("date")) {
         target.append("|"+getParameters().get("date"));
      }
      appendHourAndMin(target);
      if(getParameters() != null && getParameters().containsKey("approach")) {
         approach = (String)getParameters().get("approach");
      }
      if(approach != null) {
         target.append("|"+approach);
         
         if(approach.equals("2")) {
            if(getParameters().containsKey("interval")) {
               target.append("|"+getParameters().get("interval"));
            }
         }
      }
      return target.toString();
   }
}
