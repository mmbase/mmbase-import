package com.finalist.newsletter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.time.DateUtils;
import org.mmbase.applications.crontab.AbstractCronJob;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.services.community.ApplicationContextFactory;
import com.finalist.cmsc.services.publish.Publish;
import com.finalist.cmsc.util.ServerUtil;
import com.finalist.newsletter.publisher.bounce.BounceChecker;
import com.finalist.newsletter.services.NewsletterService;
import com.finalist.newsletter.util.NewsletterPublicationUtil;
import com.finalist.newsletter.util.NewsletterUtil;

public class NewsletterCronJob extends AbstractCronJob {

   private static Logger log = Logging.getLoggerInstance(NewsletterCronJob.class.getName());

   private List<Node> getNewslettersToPublish() {

      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeManager manager = cloud.getNodeManager(NewsletterUtil.NEWSLETTER);
      NodeQuery query = manager.createQuery();
      NodeList newsletters = manager.getList(query);
      List<Node> newslettersToPublish = new ArrayList<Node>();
      for (int i = 0; i < newsletters.size(); i++) {
         Node newsletter = newsletters.getNode(i);
         boolean isPaused = NewsletterUtil.isPaused(newsletter);
         if (isPaused) {
            continue;
         }
         if (!Publish.isPublished(newsletter)) {
            Object scheduleExpression = newsletter.getValue("schedule");
            Date lastCreatedDateTime = newsletter.getDateValue("lastcreateddate");
            if (scheduleExpression != null) {
               createPublication(newslettersToPublish, newsletter, scheduleExpression,
                        lastCreatedDateTime);
            }
         }
      }
      return (newslettersToPublish);
   }

   private void createPublication(List<Node> newslettersToPublish, Node newsletter,
                                  Object scheduleExpression, Date lastCreatedDateTime) {
      String expression = (String) scheduleExpression;
      String[] expressions = expression.split("\\|");
      if (isPublish(expressions, lastCreatedDateTime)) {
         newslettersToPublish.add(newsletter);
      }
   }

   private boolean isPublish(String[] expressions, Date lastCreatedDateTime) {
      boolean isPublish = false;
      DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
      Date minDate = null;
      try {
         minDate = df.parse("01-01-1970 00:00");
      } catch (ParseException e) {
         log.debug("--> Parse date Exception");
         ;
      }
      Date now = new Date();
      Calendar calender = Calendar.getInstance();
      //expressions[0] value: 1 once
      if (expressions[0].equals("1")) {
         String startDatetime = expressions[1] + " " + expressions[2] + ":" + expressions[3];
         try {
            Date startDate = df.parse(startDatetime);
            if (now.after(startDate) && (lastCreatedDateTime == null || DateUtils.isSameDay(minDate, lastCreatedDateTime))) {
               isPublish = true;
            }
         }
         catch (ParseException e) {
            log.debug("--> Parse date Exception");
         }
      }//expressions[0] : 2 daily
      else if (expressions[0].equals("2")) {
         String startDatetime = expressions[1] + " " + expressions[2] + ":" + expressions[3];

         try {
            Date startDate = df.parse(startDatetime);
            if (now.after(startDate)) {
               if (expressions[4].equals("0")) {
                  isPublish = compareDate(lastCreatedDateTime, isPublish,
                           minDate, now);
               } else if (expressions[4].equals("1")) {
                  if (calender.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calender.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                     isPublish = compareDate(lastCreatedDateTime, isPublish,
                              minDate, now);
                  }
               } else if (expressions[4].equals("2")) {
                  int interval = Integer.parseInt(expressions[5]);
                  if (lastCreatedDateTime == null || DateUtils.isSameDay(minDate, lastCreatedDateTime)) {
//                     if (DateUtils.isSameDay(DateUtils.addDays(startDate, interval), now)) {
                     isPublish = true;
//                     }
                  } else {
                     if (DateUtils.isSameDay(DateUtils.addDays(lastCreatedDateTime, interval), now)) {
                        isPublish = true;
                     }
                  }
               }
            }
         }
         catch (ParseException e) {
            log.debug("--> Parse date Exception");
         }
      }//expressions[0] : 3 weekly
      else if (expressions[0].equals("3")) {
         Calendar startTime = getStartCalendar(expressions);
         char[] weeks = expressions[4].toCharArray();

         for (char week2 : weeks) {

            String week = String.valueOf(week2);
            if ((calender.get(Calendar.DAY_OF_WEEK) != 1 && calender.get(Calendar.DAY_OF_WEEK) == (Integer.parseInt(week) + 1)) || (calender.get(Calendar.DAY_OF_WEEK) == 1 && Integer.parseInt(week) == 7)) {
               if (calender.after(startTime)) {
                  try {
                     if (lastCreatedDateTime == null || DateUtils.isSameDay(minDate, lastCreatedDateTime)) {
                        isPublish = true;
                        break;
                     } else {
                        int interval = Integer.parseInt(expressions[3]);
                        Date beCreate = DateUtils.addWeeks(lastCreatedDateTime, interval);
                        if (DateUtils.isSameDay(new Date(), beCreate)) {
                           isPublish = true;
                           break;
                        }
                     }
                  }
                  catch (NumberFormatException e) {
                     log.debug("-->NumberFormatException " + e.getMessage());
                  }
               }
            }
         }
      }//expressions[0] : 4 monthly
      else if (expressions[0].equals("4")) {
         Calendar startTime = getStartCalendar(expressions);
         if (expressions[3].equals("0")) {
            String dayOfMonth = expressions[4];
            char[] months = expressions[5].toCharArray();
            for (char month2 : months) {
               String month = String.valueOf(month2);
               if (!month.equals("a") && !month.equals("b") && (Integer.parseInt(month) == calender.get(Calendar.MONTH)) || (month.equals("b") && calender.get(Calendar.MONTH) == 11) || (month.equals("a") && calender.get(Calendar.MONTH) == 10)) {
                  if (calender.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(dayOfMonth)) {
                     if (calender.after(startTime)) {
                        if (lastCreatedDateTime == null || DateUtils.isSameDay(minDate, lastCreatedDateTime)) {
                           isPublish = true;
                           break;
                        } else {
                           if (!DateUtils.isSameDay(new Date(), lastCreatedDateTime)) {
                              isPublish = true;
                              break;
                           }
                        }
                     }
                  }
               }
            }
         } else if (expressions[3].equals("1")) {
            String weekOfMonth = expressions[4];
            String week = expressions[5];

            char[] months = expressions[6].toCharArray();
            for (char month2 : months) {
               String month = String.valueOf(month2);
               if (!month.equals("a") && !month.equals("b") && (Integer.parseInt(month) == calender.get(Calendar.MONTH)) || (month.equals("a") && calender.get(Calendar.MONTH) == 10) || (month.equals("b") && calender.get(Calendar.MONTH) == 11)) {
                  if (calender.get(Calendar.WEEK_OF_MONTH) == Integer.parseInt(weekOfMonth)) {
                     if (calender.get(Calendar.DAY_OF_WEEK) != 1 && calender.get(Calendar.DAY_OF_WEEK) == (Integer.parseInt(week) + 1)) {
                        if (calender.after(startTime)) {
                           if (lastCreatedDateTime == null || DateUtils.isSameDay(minDate, lastCreatedDateTime)) {
                              isPublish = true;
                              break;
                           } else {
                              if (!DateUtils.isSameDay(new Date(), lastCreatedDateTime)) {
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

   private Calendar getStartCalendar(String[] expressions) {
      Calendar startCalendar = Calendar.getInstance();
      startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(expressions[1]));
      startCalendar.set(Calendar.MINUTE, Integer.parseInt(expressions[2]));
      return startCalendar;
   }

   private boolean compareDate(Date lastCreatedDateTime, boolean isPublish,
                               Date minDate, Date now) {
      if (lastCreatedDateTime == null || DateUtils.isSameDay(minDate, lastCreatedDateTime)) {
         isPublish = true;
      } else {
         if (!DateUtils.isSameDay(now, lastCreatedDateTime)) {
            isPublish = true;
         }
      }
      return isPublish;
   }

   @Override
   public void init() {
      log.info("Start Newsletter CronJob");
      NewsletterService newsletterService = (NewsletterService) ApplicationContextFactory.getBean("newsletterServices");
      BounceChecker checker = new BounceChecker(newsletterService);
      if (!checker.isRunning() && (ServerUtil.isStaging() || ServerUtil.isSingle())) {
         checker.start();
      }
   }

   @Override
   public void run() {
      log.info("Running Newsletter CronJob for newsletter 1" );
      if(ServerUtil.isSingle() || ServerUtil.isStaging()) {
         List<Node> newslettersToPublish = getNewslettersToPublish();
         for (int newsletterIterator = 0; newsletterIterator < newslettersToPublish.size(); newsletterIterator++) {
            Node newsletterNode = newslettersToPublish.get(newsletterIterator);
            newsletterNode.setDateValue("lastcreateddate", new Date());
            newsletterNode.commit();
            int newsletterNumber = newsletterNode.getNumber();
            log.info("Running Newsletter CronJob for newsletter " + newsletterNumber);
            //NewsletterPublicationUtil.createPublication(newsletterNumber, true);
            Node publicationNode = NewsletterPublicationUtil.createPublication(newsletterNumber, true);
            NewsletterUtil.addNewsletterCreationChannel(newsletterNode.getNumber(),publicationNode.getNumber());
            try {
               NewsletterPublicationUtil.freezeEdition(publicationNode);
            } catch (MessagingException e) {
               log.error(e);
            }
            if(ServerUtil.isStaging() && !ServerUtil.isSingle()) {
               Publish.publish(publicationNode);
            }
         }
      }
   }

   @Override
   public void stop() {
      log.info("Stopping Newsletter CronJob");
   }


}