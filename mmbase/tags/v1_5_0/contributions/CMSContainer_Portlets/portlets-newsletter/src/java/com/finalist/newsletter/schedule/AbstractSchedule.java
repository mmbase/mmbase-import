package com.finalist.newsletter.schedule;

import java.util.Map;

public abstract class AbstractSchedule {

   public abstract String transform();
   
   private Map<String,Object> parameters;
   
   public void setRequestParameters(Map<String,Object> parameters) {
      this.parameters = parameters;
   }
   protected Map<String,Object> getParameters() {
      return parameters;
   }
   
   protected void appendHourAndMin(StringBuffer deliverPublication) {
      if(getParameters() != null && getParameters().containsKey("hour")) {
         deliverPublication.append("|"+getParameters().get("hour"));
      }
      if(getParameters() != null && getParameters().containsKey("minute")) {
         deliverPublication.append("|"+getParameters().get("minute"));
      }
   }
   
}
