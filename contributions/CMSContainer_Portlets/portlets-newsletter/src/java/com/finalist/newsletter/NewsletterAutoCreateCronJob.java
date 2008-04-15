package com.finalist.newsletter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.time.DateFormatUtils;
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

public class NewsletterAutoCreateCronJob implements CronJob {

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
               String expression = (String)schedule;
               String[] expressions = expression.split("\\|");
               DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
               //only once  pattern :
               if(expressions[0].equals("1")) { 
                  log.debug("---------->AutoCreateCronJob type1----------------");
                  String datetime = expressions[1]+" "+expressions[2]+":"+expressions[3];
                  Date now = new Date();
                  try {
                     Date minDate = df.parse("01-01-1970 00:00");
                     Date date = df.parse(datetime);
                     if(now.after(date) && (lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime))) {
                        newslettersToPublish.add(newsletter);
                     }
                  } 
                  catch (ParseException e) {
                     log.debug("--> parse date Exception");
                  }
               }
               else if (expressions[0].equals("2")) {
                  log.debug("---------->AutoCreateCronJob type2----------------");
                  String datetime = expressions[1]+" "+expressions[2]+":"+expressions[3];
                  Date now = new Date();
                  Calendar calender = Calendar.getInstance();
                  try {
                     Date minDate = df.parse("01-01-1970 00:00");
                     Date date = df.parse(datetime);
                     if(now.after(date)) {
                        if(expressions[4].equals("0")) {
                           if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)) {
                              newslettersToPublish.add(newsletter);
                           }
                           else  {
                              if(!DateUtils.isSameDay(now, lastCreateDateTime)) {
                                 newslettersToPublish.add(newsletter);
                              }
                           }
                        }
                        else if(expressions[4].equals("1")) {
                           if(calender.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calender.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                              if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)) {
                                 newslettersToPublish.add(newsletter);
                              }
                              else  {
                                 if(!DateUtils.isSameDay(now, lastCreateDateTime)) {
                                    newslettersToPublish.add(newsletter);
                                 }
                              }
                           }
                        }
                        else if(expressions[4].equals("2")) {
                           int interval = Integer.parseInt(expressions[5]);
                           if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)) {
                              if(DateUtils.isSameDay(DateUtils.addDays(date, interval),now)) {
                                 newslettersToPublish.add(newsletter);
                              }
                           }
                           else {
                              if(DateUtils.isSameDay(DateUtils.addDays(lastCreateDateTime, interval),now)) {
                                 newslettersToPublish.add(newsletter);
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
                  log.debug("---------->AutoCreateCronJob type3----------------");
                  Calendar createTime = Calendar.getInstance();
                  createTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(expressions[1]));
                  createTime.set(Calendar.MINUTE, Integer.parseInt(expressions[2]));
                  
                  Calendar calender = Calendar.getInstance();
                  boolean flag = false;
                  char[] weeks = expressions[4].toCharArray();
                
                  for(int j = 0 ; j < weeks.length; j++) {

                     String week = String.valueOf(weeks[j]);
                     if((calender.get(Calendar.DAY_OF_WEEK) != 1 && calender.get(Calendar.DAY_OF_WEEK) == (Integer.parseInt(week)+1)) || (calender.get(Calendar.DAY_OF_WEEK) == 1 && Integer.parseInt(week) == 7)) {
                        if(calender.after(createTime)) {
                           try {
                              Date minDate = df.parse("01-01-1970 00:00");
                              if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)){
                                 flag = true;
                                 break;
                              }
                              else {
                                 int interval = Integer.parseInt(expressions[3]);
                                 Date beCreate = DateUtils.addWeeks(lastCreateDateTime, interval);
                                 if(DateUtils.isSameDay(new Date(),beCreate )) {
                                    flag = true;
                                    break; 
                                 }
                              }
                           }
                           catch (NumberFormatException e) {
                              log.debug("-->NumberFormatException "+e.getMessage());
                           } 
                           catch (ParseException e) {
                              log.debug("--> parse date Exception "+e.getMessage());
                           }
                        }
                     }
                  }
                  if(flag) {
                     newslettersToPublish.add(newsletter);
                  }
               }
               else if(expressions[0].equals("4")) {
                  log.debug("---------->AutoCreateCronJob type4----------------");
                  Calendar createTime = Calendar.getInstance();
                  createTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(expressions[1]));
                  createTime.set(Calendar.MINUTE, Integer.parseInt(expressions[2]));
                  boolean flag = false;
                  Calendar calender = Calendar.getInstance();
                  if(expressions[3].equals("0")) {
                     String day = expressions[4];
                     char[] months = expressions[5].toCharArray();
                     for(int j = 0 ; j < months.length ; j++) {
                        String month = String.valueOf(months[j]);
                        if((Integer.parseInt(month) == calender.get(Calendar.MONTH)) ||(month.equals("a") && calender.get(Calendar.MONTH) == 10) || (month.equals("b") && calender.get(Calendar.MONTH) == 11)) {
                           if(calender.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(day)) {
                              if(calender.after(createTime)) {
                                 try {
                                    Date minDate = df.parse("01-01-1970 00:00");
                                    if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)){
                                       flag = true;
                                       break;
                                    }
                                    else {
                                       if(!DateUtils.isSameDay(new Date(),lastCreateDateTime )) {
                                          flag = true;
                                          break; 
                                       }
                                    }
                                 }
                                 catch (ParseException e) {
                                    log.debug("--> ParseException "+e.getMessage());
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
                        if((Integer.parseInt(month) == calender.get(Calendar.MONTH)) || (month.equals("a") && calender.get(Calendar.MONTH) == 10) || (month.equals("b") && calender.get(Calendar.MONTH) == 11)) {
                           if(calender.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(whichWeek)) {
                              if(calender.get(Calendar.DAY_OF_WEEK)!= 1 && calender.get(Calendar.DAY_OF_WEEK)== (Integer.parseInt(week)+1)) {
                                 if(calender.after(createTime)) {
                                    try {
                                       Date minDate = df.parse("01-01-1970 00:00");
                                       if(lastCreateDateTime == null || DateUtils.isSameDay(minDate, lastCreateDateTime)){
                                          flag = true;
                                          break;
                                       }
                                       else {
                                          if(!DateUtils.isSameDay(new Date(),lastCreateDateTime )) {
                                             flag = true;
                                             break; 
                                          }
                                       }
                                    } 
                                    catch (ParseException e) {
                                       log.debug("--> ParseException "+e.getMessage());
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
                  if(flag) {
                     newslettersToPublish.add(newsletter);
                  }
               }
            } 
         } 
      }
      return (newslettersToPublish);
   }

   public void init(CronEntry arg0) {
      log.info("Initializing Newsletter CronJob");

   }

   
   public void run() { 
      List<Node> newslettersToPublish = getNewslettersToPublish();
      log.info("---------->AutoCreateCronJob newsletters size ----------------["+newslettersToPublish == null?"0":newslettersToPublish.size()+"]");
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