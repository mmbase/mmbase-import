package com.finalist.newsletter.domain;

public class Schedule {

   private int id;

   private String expression;

   private String scheduleDescription;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getExpression() {
      return expression;
   }

   public void setExpression(String expression) {
      this.expression = expression;
   }

   public String getScheduleDescription() {
      return scheduleDescription;
   }

   public void setScheduleDescription(String scheduleDescription) {
      this.scheduleDescription = scheduleDescription;
   }
}
