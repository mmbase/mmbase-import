package com.finalist.newsletter.forms;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.apache.struts.actions.DispatchAction;

import com.finalist.newsletter.domain.Schedule;
import com.finalist.newsletter.schedule.*;
import com.finalist.newsletter.util.NewsletterUtil;

public class ScheduleAction  extends DispatchAction{


   public ActionForward transform(ActionMapping mapping, ActionForm form,
         HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      Map<String,Object> requestParameters = new HashMap<String,Object>();
      AbstractSchedule schedule = null;
      //type calendar type , 1 once ,2 daily ,3 weekly ,4 monthly
      String type = request.getParameter("type");
      String hour = request.getParameter("hour");
      String minute = request.getParameter("minute");
      if(type != null) {
         requestParameters.put("hour", hour);
         requestParameters.put("minute", minute);
         if(type.equals("1")) {
            String date = request.getParameter("date");

            requestParameters.put("date", date);
            schedule = new SingleSchedule();
         }
         else if(type.equals("2")) {
            String date = request.getParameter("date");
            requestParameters.put("date", date);

            String strategy = request.getParameter("strategy");
            requestParameters.put("approach", strategy);

            if(strategy != null && strategy.equals("2")) {
               String interval = request.getParameter("interval");
               requestParameters.put("interval", interval);
            }
            schedule = new DailySchedule();

         }
         else if(type.equals("3")) {
            String interval = request.getParameter("interval");
            requestParameters.put("interval", interval);
            String[] weeks = request.getParameterValues("weeks");
            requestParameters.put("weeks", getWeeks(weeks));
            schedule = new WeeklySchedule();
         }
         else {
            String strategy = request.getParameter("strategy");
            requestParameters.put("strategy", strategy);

            if(strategy != null) {
               if(strategy.equals("0")) {
                  String day = request.getParameter("day");
                  requestParameters.put("day", day);
               }
               else if (strategy.equals("1")) {
                  String whichweek = request.getParameter("whichweek");
                  requestParameters.put("whichweek", whichweek);

                  String week = request.getParameter("week");
                  requestParameters.put("week", week);
               }
            }
            String[] months = request.getParameterValues("month");
            requestParameters.put("month", getWeeks(months));
            schedule = new MonthSchedule();
         }

         ScheduleService Service = new ScheduleService(schedule);
         Service.setRequestParameters(requestParameters);
         String expression = Service.transform();
         response.setContentType("text/xml");

         response.getWriter().print("<expression>"+expression+"</expression>");
      }
      return null;
   }

   public ActionForward getSchedules(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      response.setContentType("text/xml");
      response.setCharacterEncoding("utf-8");
      StringBuffer sb = new StringBuffer();
      String newsletterId = request.getParameter("newsletterid");
      Locale language=(Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
      List<Schedule> schedules = NewsletterUtil.getSchedulesBynewsletterId(Integer.valueOf(newsletterId),language);
      for(Schedule schedule : schedules) {
         sb.append("<schedule>");
         sb.append("<number>"+schedule.getId()+"</number>");
         sb.append("<expression>"+schedule.getExpression()+"</expression>");
         sb.append("<description>"+schedule.getScheduleDescription()+"</description>");
         sb.append("</schedule>");
      }
      response.getWriter().print("<schedules>"+sb.toString()+"</schedules>");
      return null;
   }

   public ActionForward deleteSchedule(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      String scheduleId = request.getParameter("scheduleid");
      NewsletterUtil.deleteSchedule(Integer.valueOf(scheduleId));
      return null;
   }

   public ActionForward restoreSchedule(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
         throws Exception {
      String scheduleId = request.getParameter("scheduleid");
      NewsletterUtil.getSchedulesBynewsletterId(Integer.valueOf(scheduleId));
      return null;
   }

   /**
    * from array to String
    * @param args
    * @return
    */
   private String getWeeks(String[] weeks) {
      StringBuilder sb = new StringBuilder();
      Arrays.sort(weeks);
      for (String week : weeks) {
         sb.append(week);
      }
      return sb.toString();
   }

}
