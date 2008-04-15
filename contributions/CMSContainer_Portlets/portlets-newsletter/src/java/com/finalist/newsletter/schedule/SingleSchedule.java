package com.finalist.newsletter.schedule;

import java.util.Date;


public class SingleSchedule  extends AbstractSchedule {

   /**
    * the expression is 1|datetime
    */
   @Override
   public  String chansfer() {

      StringBuffer target = new StringBuffer("1");
      if(getParameters() != null && getParameters().containsKey("date")) {
         target.append("|"+getParameters().get("date"));
      }
      appendHourAndMin(target);
      return target.toString();
   }
}
