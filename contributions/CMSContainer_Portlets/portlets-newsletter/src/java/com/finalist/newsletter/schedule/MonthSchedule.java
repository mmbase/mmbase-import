package com.finalist.newsletter.schedule;

import java.util.Date;


public class MonthSchedule extends AbstractSchedule {

   /**
    * the expression is 4|hour|minute|strategy|day or whichweek and week|month
    * whichweek {1|2|3|4|5}
    * week   {1|2|3|4|5|6|7}
    * month  e.g. 1000000000000 12 bit ,"1" ,selected,"0" no selected
    */
   @Override
   public String chansfer() {
      
      StringBuffer target = new StringBuffer("4");
      
      if(getParameters() != null) {
         appendHourAndMin(target);
         
         if(getParameters().containsKey("strategy")) {
            target.append("|"+getParameters().get("strategy"));
            
            if(getParameters().get("strategy").equals("0")) {
               target.append("|"+getParameters().get("day"));
            }
            else if(getParameters().get("strategy").equals("1")) {
               target.append("|"+getParameters().get("whichweek"));
               target.append("|"+getParameters().get("week"));
            }
         }
         if(getParameters().containsKey("month")) {
            target.append("|"+getParameters().get("month"));
         }
      }
      
      return target.toString();
   }

}
