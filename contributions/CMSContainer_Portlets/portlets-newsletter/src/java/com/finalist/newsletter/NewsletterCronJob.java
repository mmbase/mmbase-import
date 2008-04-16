package com.finalist.newsletter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.time.DateUtils;
import org.mmbase.applications.crontab.CronEntry;
import org.mmbase.applications.crontab.CronJob;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.publish.Publish;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterCronJob implements CronJob {

   private static Logger log = Logging.getLoggerInstance(NewsletterCronJob.class.getName());

   private List<Node> getNewslettersToPublish() {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager manager = cloud.getNodeManager(NewsletterUtil.NEWSLETTER);
      NodeQuery query = manager.createQuery();
      NodeList newsletters = manager.getList(query);
      List<Node> newslettersToPublish = new ArrayList<Node>();
      for (int i = 0; i < newsletters.size(); i++) {
         Node newsletter = newsletters.getNode(i);
         if (!Publish.isPublished(newsletter)) {
            Object schedule = newsletter.getValue("schedule");
            Date lastCreateDateTime = newsletter.getDateValue("lastcreate");
            if (schedule != null) {
               shouldPublish(newslettersToPublish, newsletter, schedule,
                     lastCreateDateTime);
            } 
         } 
      }
      return (newslettersToPublish);
   }

   private void shouldPublish(List<Node> newslettersToPublish, Node newsletter,
         Object schedule, Date lastCreateDateTime) {
      String expression = (String)schedule;
      String[] expressions = expression.split("\\|");
      if(isShouldPublish(expressions,lastCreateDateTime)) {
         newslettersToPublish.add(newsletter);
      }
   }
   
   private boolean isShouldPublish(String[] expressions,Date lastCreateDateTime) {
      boolean isPublish = false;
      DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
      Date minDate =  null;
      try {
         minDate =  df.parse("01-01-1970 00:00");
      } catch (ParseException e1) {
         log.debug("--> parse date Exception");;
      }
      //only once  pattern :
      if(expressions[0].equals("1")) { 
         String datetime = expressions[1]+" "+expressions[2]+":"+expressions[3];
         Date now = new Date();
         try {
            Date date = df.parse(datetime);
            if(now.after(date) && (lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime))) {
               isPublish = true;
            }
         } 
         catch (ParseException e) {
            log.debug("--> parse date Exception");
         }
      }
      else if (expressions[0].equals("2")) {
         String datetime = expressions[1]+" "+expressions[2]+":"+expressions[3];
         Date now = new Date();
         Calendar calender = Calendar.getInstance();
         try {
            Date date = df.parse(datetime);
            if(now.after(date)) {
               if(expressions[4].equals("0")) {
                  isPublish = compareDate(lastCreateDateTime, isPublish,
                        minDate, now);
               }
               else if(expressions[4].equals("1")) {
                  if(calender.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calender.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                     isPublish = compareDate(lastCreateDateTime, isPublish,
                           minDate, now);
                  }
               }
               else if(expressions[4].equals("2")) {
                  int interval = Integer.parseInt(expressions[5]);
                  if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)) {
                     if(DateUtils.isSameDay(DateUtils.addDays(date, interval),now)) {
                        isPublish = true;
                     }
                  }
                  else {
                     if(DateUtils.isSameDay(DateUtils.addDays(lastCreateDateTime, interval),now)) {
                        isPublish = true;
                     }
                  }
               }
            }
         } 
         catch (ParseException e) {
            log.debug("--> parse date Exception");
         }
      }
      else if(expressions[0].equals("3")) {
         Calendar createTime = Calendar.getInstance();
         createTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(expressions[1]));
         createTime.set(Calendar.MINUTE, Integer.parseInt(expressions[2]));
         
         Calendar calender = Calendar.getInstance();
         char[] weeks = expressions[4].toCharArray();
       
         for(int j = 0 ; j < weeks.length; j++) {

            String week = String.valueOf(weeks[j]);
            if((calender.get(Calendar.DAY_OF_WEEK) != 1 && calender.get(Calendar.DAY_OF_WEEK) == (Integer.parseInt(week)+1)) || (calender.get(Calendar.DAY_OF_WEEK) == 1 && Integer.parseInt(week) == 7)) {
               if(calender.after(createTime)) {
                  try {
                     if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)){
                        isPublish = true;
                        break;
                     }
                     else {
                        int interval = Integer.parseInt(expressions[3]);
                        Date beCreate = DateUtils.addWeeks(lastCreateDateTime, interval);
                        if(DateUtils.isSameDay(new Date(),beCreate )) {
                           isPublish = true;
                           break; 
                        }
                     }
                  }
                  catch (NumberFormatException e) {
                     log.debug("-->NumberFormatException "+e.getMessage());
                  } 
               }
            }
         }
      }
      else if(expressions[0].equals("4")) {
         Calendar createTime = Calendar.getInstance();
         createTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(expressions[1]));
         createTime.set(Calendar.MINUTE, Integer.parseInt(expressions[2]));
         Calendar calender = Calendar.getInstance();
         if(expressions[3].equals("0")) {
            String day = expressions[4];
            char[] months = expressions[5].toCharArray();
            for(int j = 0 ; j < months.length ; j++) {
               String month = String.valueOf(months[j]);
               System.out.println("month="+month);
               System.out.println(Arrays.toString(months));
               if(!month.equals("a") && !month.equals("b") && (Integer.parseInt(month) == calender.get(Calendar.MONTH)) || (month.equals("b") && calender.get(Calendar.MONTH) == 11) || (month.equals("a") && calender.get(Calendar.MONTH) == 10)) {
                  if(calender.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day)) {
                     if(calender.after(createTime)) {
                           if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)){
                              isPublish = true;
                              break;
                           }
                           else {
                              if(!DateUtils.isSameDay(new Date(),lastCreateDateTime )) {
                                 isPublish = true;
                                 break; 
                              }
                           }
                     }
                  }
               }
            }
         }
         else if(expressions[3].equals("1")) {
            String whichWeek = expressions[4];
            String week = expressions[5];
            
            char[] months = expressions[6].toCharArray();
            for(int j = 0 ; j < months.length ; j++) {
               String month = String.valueOf(months[j]);
               if(!month.equals("a") && !month.equals("b") && (Integer.parseInt(month) == calender.get(Calendar.MONTH)) || (month.equals("a") && calender.get(Calendar.MONTH) == 10) || (month.equals("b") && calender.get(Calendar.MONTH) == 11)) {
                  if(calender.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(whichWeek)) {
                     if(calender.get(Calendar.DAY_OF_WEEK)!= 1 && calender.get(Calendar.DAY_OF_WEEK)== (Integer.parseInt(week)+1)) {
                        if(calender.after(createTime)) {
                              if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)){
                                 isPublish = true;
                                 break;
                              }
                              else {
                                 if(!DateUtils.isSameDay(new Date(),lastCreateDateTime )) {
                                    isPublish = true;
                                    break; 
                                 }
                              }
                        }
                     }
                  }
               }
            }
         }
      }
      return isPublish;
   }

   private boolean compareDate(Date lastCreateDateTime, boolean isPublish,
         Date minDate, Date now) {
      if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)) {
         isPublish = true;
      }
      else  {
         if(!DateUtils.isSameDay(now, lastCreateDateTime)) {
            isPublish = true;
         }
      }
      return isPublish;
   }

   public void init(CronEntry arg0) {
      log.info("Initializing Newsletter CronJob");

   }
   
   public void run() { 
      List<Node> newslettersToPublish = getNewslettersToPublish();
      for (int newsletterIterator = 0; newsletterIterator < newslettersToPublish.size(); newsletterIterator++) {
         Node newsletterNode = newslettersToPublish.get(newsletterIterator);
         newsletterNode.setDateValue("lastcreate", new Date());
         newsletterNode.commit();
         int newsletterNumber = newsletterNode.getNumber();
         log.info("Running Newsletter CronJob for newsletter " + newsletterNumber);
         //NewsletterPublicationUtil.createPublication(newsletterNumber, true);
         Node publicationNode = NewsletterPublicationUtil.createPublication(newsletterNumber, true);
         Publish.publish(publicationNode);
      }
   }

   public void stop() {
      log.info("Stopping Newsletter CronJob");
   }
   
   
}