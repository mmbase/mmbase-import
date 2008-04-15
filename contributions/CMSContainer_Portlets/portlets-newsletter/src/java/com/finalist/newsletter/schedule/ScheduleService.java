package com.finalist.newsletter.schedule;

import java.util.Map;


public class ScheduleService {

   private AbstractSchedule schedule;
   
   public ScheduleService(AbstractSchedule schedule) {
      this.schedule = schedule;
   }
   
   public void setParameters(Map<String,Object> parameters) {
      schedule.setParameters(parameters);
   }
   
   public String chansfer() {
      return schedule.chansfer();
   }
}
