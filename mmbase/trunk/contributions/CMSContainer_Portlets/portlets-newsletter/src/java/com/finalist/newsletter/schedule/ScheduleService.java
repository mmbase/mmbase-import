package com.finalist.newsletter.schedule;

import java.util.Map;


public class ScheduleService {

   private AbstractSchedule schedule;

   public ScheduleService(AbstractSchedule schedule) {
      this.schedule = schedule;
   }

   public void setRequestParameters(Map<String, Object> requestParameters) {
      schedule.setRequestParameters(requestParameters);
   }

   public String transform() {
      return schedule.transform();
   }
}
