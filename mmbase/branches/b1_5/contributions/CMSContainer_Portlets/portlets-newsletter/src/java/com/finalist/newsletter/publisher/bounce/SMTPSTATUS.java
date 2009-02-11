package com.finalist.newsletter.publisher.bounce;

import java.util.Map;
import java.util.HashMap;

public enum SMTPSTATUS {
   INIT(1),
   /**
    * 1
    * State indicating we sent our '220' initial session instantiation message, and are now waiting for a HELO
    */
   HELO(2),
   /**
    * 2
    * State indicating we received a HELO and we are now waiting for a 'MAIL FROM:'
    */
   MAILFROM(3),
   /**
    * 3
    * State indicating we received a MAIL FROM and we are now waiting for a 'RCPT TO:'
    */
   RCPTTO(4),

   /**
    * State indicating we received a DATA and we are now processing the data
    */
   DATA(5),

   /**
    * State indicating we received a QUIT, and that we may close the connection
    */
   FINISHED(6);

   public static final Map<String, Map<String, Action>> statusMap = new HashMap<String, Map<String, Action>>();

   private int index;

   static {
      INIT.init("RSET", "*", MAILFROM, "250 Spontanious amnesia has struck me, I forgot everything!\r\n");
      INIT.init("EHLO", "<=HELO", MAILFROM, "250 Good day, how are you today?\r\n");
      INIT.init("EHLO", ">HELO", MAILFROM, "503 5.0.0 Duplicate HELO/EHLO\r\n");
      INIT.init("QUIT", "*", FINISHED, null);
      INIT.init("MAIL FROM:", "<MAILFROM", null, "503 That's not nice! Polite people say HELO first\r\n");
      INIT.init("MAIL FROM:", ">MAILFROM", null, "503 You cannot specify MAIL FROM after a RCPT TO\r\n");
      INIT.init("MAIL FROM:", "=MAILFROM", RCPTTO, "250 That address looks okay, I'll allow you to send mail.\r\n");
      INIT.init("RCPT TO:", "<RCPTTO", null,"503 You should say MAIL FROM first\r\n");
      INIT.init("RCPT TO:", ">=DATA", null,"503 You cannot use RCPT TO: at this state\r\n");
   }

   SMTPSTATUS(int index) {
      this.index = index;
   }

   private void init(String command, String condition, SMTPSTATUS targetStatus, String response) {
      if (null == statusMap.get(command)) {
         statusMap.put(command, new HashMap<String,Action>());
      }

      Map<String,Action> conditionMap = statusMap.get(command);

      conditionMap.put(condition, new SMTPSTATUS.Action(targetStatus, response));

   }

   public SMTPSTATUS.Action change(String command) {

      Map<String,Action> conditionMap = new HashMap<String,Action>();

      for (String key : statusMap.keySet()) {
         if (command.startsWith(key)) {
            conditionMap = statusMap.get(key);
            break;
         }
      }

      for (String condition : conditionMap.keySet()) {
         if (veryfyCondition(condition)) {
            
            Action action = conditionMap.get(condition);
            if (null == action.getStatus()) {
               action.setStatus(this);
            }
            return action;
         }
      }

      return null;
   }

   private boolean veryfyCondition(String condition) {

      if ("*".equals(condition)) {
         return true;
      }

      String element;
      if (condition.startsWith("<=")) {
         element = condition.replaceFirst("<=", "").trim();
         return index <= valueOf(element).index;
      }

      if (condition.startsWith(">=")) {
         element = condition.replaceFirst(">=", "").trim();
         return index >= valueOf(element).index;
      }
      if (condition.startsWith(">")) {
         element = condition.replaceFirst(">", "").trim();
         return index > valueOf(element).index;
      }

      if (condition.startsWith("<")) {
         element = condition.replaceFirst("<", "").trim();
         return index < valueOf(element).index;
      }


      if (condition.startsWith("=")) {
         element = condition.replaceFirst("=", "").trim();
         return index == valueOf(element).index;
      }

      if (condition.startsWith("!=")) {
         element = condition.replaceFirst("!=", "").trim();
         return index != valueOf(element).index;
      }


      return false;
   }


   class Action {
      SMTPSTATUS status;
      String response;

      Action(SMTPSTATUS status, String response) {
         this.status = status;
         this.response = response;
      }

      public SMTPSTATUS getStatus() {
         return status;
      }

      public void setStatus(SMTPSTATUS status) {
         this.status = status;
      }

      public String getResponse() {
         return response;
      }

      public void setResponse(String response) {
         this.response = response;
      }
   }

   public int getIndex() {
      return index;
   }
}