package com.finalist.newsletter.forms;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.newsletter.schedule.AbstractSchedule;
import com.finalist.newsletter.schedule.DailySchedule;
import com.finalist.newsletter.schedule.MonthSchedule;
import com.finalist.newsletter.schedule.ScheduleService;
import com.finalist.newsletter.schedule.SingleSchedule;
import com.finalist.newsletter.schedule.WeeklySchedule;

public class Schedule  extends MMBaseAction{
   
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response, Cloud cloud)
         throws Exception {
      Map<String,Object> parameters = new HashMap<String,Object>();
      AbstractSchedule schedule = null;
      String type = request.getParameter("type");
      String hour = request.getParameter("hour");
      String minute = request.getParameter("minute");
      if(type != null) {
         parameters.put("hour", hour);
         parameters.put("minute", minute);
         if(type.equals("1")) {
            String date = request.getParameter("date");

            parameters.put("date", date);
            schedule = new SingleSchedule();
         }
         else if(type.equals("2")) {
            String date = request.getParameter("date");
            parameters.put("date", date);

            String strategy = request.getParameter("strategy");
            parameters.put("approach", strategy);
            
            if(strategy != null && strategy.equals("2")) {
               String interval = request.getParameter("interval");
               parameters.put("interval", interval);
            }
            schedule = new DailySchedule();
            
         }
         else if(type.equals("3")) {
            String interval = request.getParameter("interval");
            parameters.put("interval", interval);
            String[] weeks = request.getParameterValues("weeks");
            parameters.put("weeks", getWeeks(weeks));
            schedule = new WeeklySchedule();
         }
         else {
            String strategy = request.getParameter("strategy");
            parameters.put("strategy", strategy);
            
            if(strategy != null) {
               if(strategy.equals("0")) {
                  String day = request.getParameter("day");
                  parameters.put("day", day); 
               }
               else if (strategy.equals("1")) {
                  String whichweek = request.getParameter("whichweek");
                  parameters.put("whichweek", whichweek); 
                  
                  String week = request.getParameter("week");
                  parameters.put("week", week);
               }
            }
            String[] months = request.getParameterValues("month");
            parameters.put("month", getWeeks(months));
            schedule = new MonthSchedule();
         }
        
         ScheduleService Service = new ScheduleService(schedule);
         Service.setParameters(parameters);
         String expression = Service.chansfer();
         response.setContentType("text/xml");
         
         response.getWriter().print("<expression>"+expression+"</expression>");
      }
      return null;
   }
   
   /**
    * from array to String
    * @param args
    * @return
    */
   private String getWeeks(String[] args) {
      StringBuilder sb = new StringBuilder();
      Arrays.sort(args);
      for(int i = 0 ; i < args.length ; i++) {
         sb.append(args[i]);
      }
      return sb.toString();
   }

}
