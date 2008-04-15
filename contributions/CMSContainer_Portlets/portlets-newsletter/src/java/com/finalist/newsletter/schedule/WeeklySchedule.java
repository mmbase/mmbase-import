package com.finalist.newsletter.schedule;


public class WeeklySchedule extends AbstractSchedule  {

   /**
    * the expression 3|hour|minute|interval|week
    * week is a String ,e.g. 1000001 ,"1" express being selected,"0" opposite
    */
   @Override
   public  String chansfer() {
      
      StringBuffer target = new StringBuffer("3");
      if(getParameters() != null) {
         appendHourAndMin(target);
         
         if(getParameters().containsKey("interval")) {
            target.append("|"+getParameters().get("interval"));
         }
        
         if(getParameters().containsKey("weeks")) {
            target.append("|"+getParameters().get("weeks"));
         }
      }
      return target.toString();
   }
}
