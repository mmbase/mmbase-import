package nl.leocms.evenementen;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.evenementen.forms.SubscribeAction;
import javax.servlet.*;
import nl.mmatch.NatMMConfig;

/**
 * Created by Henk Hangyi (MMatch)
 */

public class EventNotifier implements Runnable {

   private static final Logger log = Logging.getLoggerInstance(EventNotifier.class);
   
   public static void updateAppAttributes(Cloud cloud) {
      // *** updating the application attributes
      MMBaseContext mc = new MMBaseContext();
      ServletContext application = mc.getServletContext();
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      long lDateSearchFrom = (cal.getTime().getTime()/1000);
      String eventUrl = "from_day="+cal.get(Calendar.DAY_OF_MONTH)+"&from_month="+(cal.get(Calendar.MONTH)+1)+"&from_year="+cal.get(Calendar.YEAR);      
      cal.add(Calendar.YEAR,1); // cache events for one year from now
      long lDateSearchTill = (cal.getTime().getTime()/1000);
      eventUrl += "&till_day="+cal.get(Calendar.DAY_OF_MONTH)+"&till_month="+(cal.get(Calendar.MONTH)+1)+"&till_year="+cal.get(Calendar.YEAR);

      application.setAttribute("events",Evenement.getEvents(cloud,lDateSearchFrom,lDateSearchTill));
      application.setAttribute("events_url",eventUrl);
      application.setAttribute("events_till",new Long(lDateSearchTill));

      log.info("updated applications attributes");
   }

   public String checkOnEventsWithoutLocation(Cloud cloud) {
      // *** updating the application attributes
      String logMessage = "";
      int nUpdated = 0;
      int nSuccesfullUpdated = 0;
      NodeIterator iNodes= cloud.getList(null,"evenement","evenement.number","evenement.lokatie =',-1,'", null, null, null, false).nodeIterator();
      while(iNodes.hasNext()) {
          Node nextNode = iNodes.nextNode();
          String thisEvent = nextNode.getStringValue("evenement.number");
          cloud.getNode(thisEvent).commit();
          nUpdated++;
          if(!cloud.getNode(thisEvent).getStringValue("lokatie").equals(",-1,")) {
            nSuccesfullUpdated++;
          }
      }
      logMessage += "\n<br>Number of succesfull updated locations " + nSuccesfullUpdated + " / " + nUpdated;
      return logMessage;
   }

   public String notifyParticipants(Cloud cloud, String requestUrl, String liveUrl) { 
   
      String logMessage = "";
      int nEmailSend = 0;
      
      try {
         
         // use liveUrl to make sure that notifications are only send from live server
         if(requestUrl.indexOf(liveUrl)>-1) {

            // list all the subscription:
            // - who booked for an event more than one month ago,
            // - and the event is now less than one week ahead,
            // - the participants did not receive a reminder email.
            long now = (new Date().getTime())/1000;
            long one_day = 24*60*60;
            long one_week = 7*one_day;
            long one_month = 31*one_day;
            NodeIterator iNodes= cloud.getList(null
               , "evenement,posrel,inschrijvingen"
               , "inschrijvingen.number"
               , "inschrijvingen.datum_inschrijving < '" + (now - one_month) + "' AND evenement.begindatum > '" + now + "' AND evenement.begindatum < '" + (now + one_week) + "'"
               , null, null, null, false).nodeIterator();
            while(iNodes.hasNext()) {
                Node nextNode = iNodes.nextNode();
                String thisSubscription = nextNode.getStringValue("inschrijvingen.number");
                if(cloud.getList(thisSubscription
                      , "inschrijvingen,related,email"
                      , null
                      , "email.subject LIKE 'Herinnering aanmelding %'"
                      , null, null, null, false).isEmpty()) {
                  SubscribeAction.sendReminderEmail(cloud, thisSubscription);
                  nEmailSend++;
               }
            }

         } else {
            logMessage += "\n<br>'" + requestUrl + "' does not match with '" + liveUrl + "' therefore no reminder emails send";
            log.info("'" + requestUrl + "' does not match with '" + liveUrl + "' therefore no reminder emails send");
         }
         
      } catch(Exception e) {
         log.info(e);
      }
      logMessage += "\n<br>Number of reminders send " + nEmailSend;
      return logMessage;
   }

   public void updateEventDB() { 
   
      Cloud cloud = CloudFactory.getCloud();
      MMBaseContext mc = new MMBaseContext();
      ServletContext application = mc.getServletContext();
      String requestUrl = (String) application.getAttribute("request_url");
      if(requestUrl==null) { requestUrl = ""; }

      String emailSubject = "Notificatie van " + requestUrl;

      String toEmailAddress = NatMMConfig.toEmailAddress;
      String fromEmailAddress = NatMMConfig.fromEmailAddress; 
      String liveUrl = NatMMConfig.liveUrl;

      log.info("Started updateEventDB");
      String logMessage =  "\n<br>Started updateEventDB " + new Date();
      
      logMessage += notifyParticipants(cloud, requestUrl, liveUrl);
      updateAppAttributes(cloud);
      logMessage += "\n<br>Updated application attributes";
      logMessage += checkOnEventsWithoutLocation(cloud);
      
      logMessage += "\n<br>Finished updateEventDB " + new Date();
      log.info("Finished updateEventDB");
      
      Node emailNode = cloud.getNodeManager("email").createNode();
      emailNode.setValue("to", toEmailAddress);
      emailNode.setValue("from", fromEmailAddress);
      emailNode.setValue("subject", emailSubject);
      emailNode.setValue("replyto", fromEmailAddress);
      emailNode.setValue("body","<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\"></multipart>"
                      + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                      + "<html>" + logMessage + "</html>"
                      + "</multipart>");
      emailNode.commit();
      emailNode.getValue("mail(oneshot)");
   }
   
   private Thread getKicker(){
      Thread  kicker = Thread.currentThread();
      if(kicker.getName().indexOf("EventNotifierThread")==-1) {
         kicker.setName("EventNotifierThread / " + (new Date()));
         kicker.setPriority(Thread.MIN_PRIORITY+1); // *** does this help ?? ***
      }
      return kicker;
   }
   
   public EventNotifier() {
      Thread kicker = getKicker();
      log.info("EventNotifier(): " + kicker);
   }
   
   public void run () {
      Thread kicker = getKicker();
      log.info("run(): " + kicker); 
      updateEventDB();
   }
}