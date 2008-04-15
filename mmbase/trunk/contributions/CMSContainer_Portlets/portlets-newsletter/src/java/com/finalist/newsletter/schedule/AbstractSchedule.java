package com.finalist.newsletter.schedule;

import java.util.Map;

public abstract class AbstractSchedule {

   public abstract String chansfer();
   
   private Map<String,Object> parameters;
   
   public void setParameters(Map<String,Object> parameters) {
      this.parameters = parameters;
   }
   protected Map<String,Object> getParameters() {
      return parameters;
   }
   
   protected void appendHourAndMin(StringBuffer sb) {
      if(getParameters() != null && getParameters().containsKey("hour")) {
         sb.append("|"+getParameters().get("hour"));
      }
      if(getParameters() != null && getParameters().containsKey("minute")) {
         sb.append("|"+getParameters().get("minute"));
      }
   }
   
}
