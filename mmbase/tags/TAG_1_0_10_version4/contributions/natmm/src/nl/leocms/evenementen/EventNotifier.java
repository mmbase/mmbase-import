package nl.leocms.evenementen;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;

import nl.leocms.evenementen.forms.SubscribeAction;
import nl.leocms.util.DoubleDateNode;
import nl.leocms.util.ApplicationHelper;
import nl.leocms.applications.NatMMConfig;

import javax.servlet.*;

/**
 * Created by Henk Hangyi (MMatch)
 */

public class EventNotifier implements Runnable {

   private static final Logger log = Logging.getLoggerInstance(EventNotifier.class);
   
   private static String FULLYBOOKED_EVENT = "Volgeboekte activiteit";
   private static String LESSTHANMIN_EVENT = "Activiteit met minder dan het minimum aantal deelnemers";

   public String getEventMessage(Node thisEvent, String eventMessage, String type) {
      String newline = "<br/>";
      if(type.equals("plain")) { newline = "\n"; }
      String message = "Beste CAD contactpersoon," + newline + newline 
               + "De activiteit " + thisEvent.getStringValue("titel") + " op " + (new DoubleDateNode(thisEvent)).getReadableValue()
               + " " + eventMessage + newline + newline 
               + "Dit bericht is automatisch door het CAD gegenereerd.";
      return message;
   }

   public int sendEventNotification(Cloud cloud, Node thisEvent, String eventType, String eventMessage) {
      
      String fromEmailAddress = NatMMConfig.getFromCADAddress();
      String emailSubject = eventType + " " + thisEvent.getStringValue("titel") + ", " + (new DoubleDateNode(thisEvent)).getReadableValue();
      int nEmailSend = 0;

      Node emailNode = cloud.getNodeManager("email").createNode();
      emailNode.setValue("from", fromEmailAddress);
      emailNode.setValue("subject", emailSubject);
      emailNode.setValue("replyto", fromEmailAddress);
      emailNode.setValue("body",
                      "<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\">"
                         + getEventMessage(thisEvent, eventMessage, "plain")
                      + "</multipart>"
                      + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
                        + "<html>"
                           + getEventMessage(thisEvent, eventMessage, "html")
                        + "</html>"
                      + "</multipart>");
      emailNode.commit();
       
      String emailField = "medewerkers.email";
      NodeIterator uNodes= cloud.getList(thisEvent.getStringValue("number")
         , "evenement,related,medewerkers"
         , emailField, null, null, null, null, true).nodeIterator();  
      if(!uNodes.hasNext()) {
         emailField = "users.emailadres";
         uNodes= cloud.getList(thisEvent.getStringValue("number")
            , "evenement,schrijver,users"
            , emailField, null, null, null, null, true).nodeIterator();
      }
      while(uNodes.hasNext()) {
         String emailAddress = uNodes.nextNode().getStringValue(emailField);
         if(emailAddress!=null&&!emailAddress.equals("")) {
            emailNode.setValue("to", emailAddress);
            emailNode.commit();
            emailNode.getValue("mail(oneshotkeep)");
            nEmailSend++;
         }
      }

      thisEvent.createRelation(emailNode,cloud.getRelationManager("related")).commit();
      return nEmailSend;
   }

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

   public String notifyParticipants(Cloud cloud) { 
   
      String logMessage = "";
      int nEmailSend = 0;
      try {   
         // list all the subscription:
         // - who booked for an event more than one month ago,
         // - and the event is now less than one week ahead,
         // - and the event is not canceled
         // - the participants did not receive a reminder email.
         long now = (new Date().getTime())/1000;
         long one_day = 24*60*60;
         long one_week = 7*one_day;
         long one_month = 31*one_day;
         NodeIterator iNodes= cloud.getList(null
            , "evenement,posrel,inschrijvingen"
            , "inschrijvingen.number"
            , "inschrijvingen.datum_inschrijving < '" + (now - one_month) + "'"
              + " AND evenement.begindatum > '" + now + "'"
              + " AND evenement.begindatum < '" + (now + one_week) + "'"
              + " AND evenement.iscanceled='false'"
            , null, null, null, false).nodeIterator();
         while(iNodes.hasNext()) {
             Node nextNode = iNodes.nextNode();
             String thisSubscription = nextNode.getStringValue("inschrijvingen.number");
             if(!Evenement.isGroupSubscription(cloud,thisSubscription)
                && cloud.getList(thisSubscription
                   , "inschrijvingen,related,email"
                   , null
                   , "email.subject LIKE 'Herinnering aanmelding %'"
                   , null, null, null, false).isEmpty()) {
               SubscribeAction.sendReminderEmail(cloud, thisSubscription);
               nEmailSend++;
            }
         }   
      } catch(Exception e) {
         log.info(e);
      }
      logMessage += "\n<br>Number of reminders send " + nEmailSend;
      return logMessage;
   }

   public void isCanceledNotification(Cloud cloud, String sEvent) {
      int nEmailSend = sendEventNotification(cloud, cloud.getNode(sEvent), "Geannuleerde activiteit"," is geannuleerd.");
   }

   public String lessThanMin(Cloud cloud) { 
   
      String logMessage = "";
      int nEmailSend = 0;
      
      try {
         
         // list all the events:
         // - the event is now less than one week ahead,
         // - and the minimum number of participants is not reached
         long now = (new Date().getTime())/1000;
         long one_day = 24*60*60;
         
         NodeIterator eNodes = cloud.getNodeManager("evenement").getList(
                                    "begindatum > '" + now + "'"
                                    + " AND begindatum < '" + (now + 2*one_day) + "'"
                                    + " AND cur_aantal_deelnemers < min_aantal_deelnemers",
                                    "begindatum", 
                                    "UP").nodeIterator();
         while(eNodes.hasNext()) {
            Node childEvent = eNodes.nextNode();
            if(cloud.getList(childEvent.getStringValue("number")
                   , "evenement,related,email"
                   , null
                   , "email.subject LIKE '" + LESSTHANMIN_EVENT + "%'"
                   , null, null, null, false).isEmpty()) {
               nEmailSend += sendEventNotification(cloud, childEvent, LESSTHANMIN_EVENT, 
                  " vindt over twee dagen plaats, maar heeft minder dan het minimum aantal deelnemers.");
            }
         }
      } catch(Exception e) {
         log.info(e);
      }
      logMessage += "\n<br>Number of 'less than minimum' notifications send " + nEmailSend;
      return logMessage;
   }

   public String isFullyBooked(Cloud cloud) { 
   
      String logMessage = "";
      int nEmailSend = 0;
      
      try {
         
         // list all the events:
         // - that are fully booked,
         // - and no notification email was send
         long now = (new Date().getTime())/1000;
         NodeIterator eNodes = cloud.getNodeManager("evenement").getList("begindatum > '" + now + "'", "begindatum", "UP").nodeIterator();
         while(eNodes.hasNext()) {
             Node childEvent = eNodes.nextNode();
             String sChild = childEvent.getStringValue("number");
             String sParent = Evenement.findParentNumber(sChild);
             Node parentEvent = cloud.getNode(sParent);
             if(Evenement.isFullyBooked(parentEvent, childEvent)) {
               if(cloud.getList(sChild
                      , "evenement,related,email"
                      , null
                      , "email.subject LIKE '" + FULLYBOOKED_EVENT + "%'"
                      , null, null, null, false).isEmpty()) {
                  nEmailSend += sendEventNotification(cloud, childEvent, FULLYBOOKED_EVENT," is volgeboekt. Indien aanwezig, open een reservedatum.");
               }
            }
         }
      } catch(Exception e) {
         log.info(e);
      }
      logMessage += "\n<br>Number of 'fully booked' notifications send " + nEmailSend;
      return logMessage;
   }
   
   private boolean isFirstDayOfNewQuarter() {
      Calendar cal = Calendar.getInstance();
      int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
      int month = cal.get(Calendar.MONTH);
      return (dayOfMonth==1)&&(month%3==0);
   }
   
   private String getCheckAccountMessage(String type) {
      String newline = "<br/>";
      if(type.equals("plain")) { newline = "\n"; }
      return "Dit bericht is verstuurd in het kader van de driemaandelijkse controle op de in de website gebruikte emailaddressen." + newline
         + "Stuur alstublieft ter controle een reply op deze email naar" + newline + newline
         + "Email:" + NatMMConfig.getToEmailAddress() + newline + newline
         + "Bij voorbaat dank, de webmasters." + newline + newline;
   }
   
   public String checkEmailAccounts(Cloud cloud) {
      
      TreeSet emailAccounts = new TreeSet();
      emailAccounts.add(NatMMConfig.getFromEmailAddress());
      emailAccounts.add(NatMMConfig.getFromCADAddress());
      emailAccounts.add(NatMMConfig.getToSubscribeAddress());
      NodeList nlFormulieren = cloud.getNodeManager("formulier").getList("emailadressen != ''",null,null);
      for(int n=0; n<nlFormulieren.size(); n++) {
         String thisEmailAddres =  nlFormulieren.getNode(n).getStringValue("emailadressen") + ";";
         int semicolon = thisEmailAddres.indexOf(";");
         while(semicolon>-1)
         {
            emailAccounts.add(thisEmailAddres.substring(0,semicolon));
            thisEmailAddres = thisEmailAddres.substring(semicolon+1);
            semicolon = thisEmailAddres.indexOf(";");
         }
      }
      Node emailNode = cloud.getNodeManager("email").createNode();
      emailNode.setValue("from", NatMMConfig.getToEmailAddress());
      emailNode.setValue("replyto", NatMMConfig.getToEmailAddress());
      emailNode.setValue("body",
             "<multipart id=\"plaintext\" type=\"text/plain\" encoding=\"UTF-8\">"
                + getCheckAccountMessage("plain")
             + "</multipart>"
             + "<multipart id=\"htmltext\" alt=\"plaintext\" type=\"text/html\" encoding=\"UTF-8\">"
               + "<html>"
                  + getCheckAccountMessage("html")
               + "</html>"
             + "</multipart>");
      emailNode.commit();
      
      String logMessage = "";
      int nEmailSend = 0;
      while(!emailAccounts.isEmpty()) {
        String emailAddress = (String) emailAccounts.first();
        if(com.cfdev.mail.verify.EmailVerifier.validateEmailAddressSyntax(emailAddress)) {
           emailNode.setValue("subject", "Controle op in de website gebruikte emailadres " + emailAddress);
           emailNode.setValue("to", emailAddress);
           emailNode.commit();
           emailNode.getValue("mail(oneshot)");
           logMessage += "\n<br>Send email to: " + emailAddress;
           nEmailSend++;
        } else {
           logMessage += "\n<br>" + emailAddress +  " is not a valid email adres";
        }
        emailAccounts.remove(emailAddress);
      }
      return "\n<br>Number of emails for checking accounts is " + nEmailSend + logMessage;
   }

   public String groupEventConfirmationPeriodExpired(Cloud cloud) { 
      String logMessage = "";
      
      // groupevent confirmationperiod expired notifications are only sent on monday
      Calendar rightNow = Calendar.getInstance();
      int day = rightNow.get(Calendar.DAY_OF_WEEK);
      
      if (day == Calendar.MONDAY) {
         int nEmailSend = 0;
         
         try {   
            // list all the group subscription:
            // - not confirmed
            // - confirmation period expired
            long now = (new Date().getTime())/1000;
            long one_day = 24*60*60;
            long two_weeks = 14*one_day;
            
            NodeIterator iNodes= cloud.getList(null
               , "evenement,posrel,inschrijvingen,related,inschrijvings_status"
               , "inschrijvingen.number, evenement.number"
               , "inschrijvingen.datum_inschrijving < '" + (now - two_weeks) + "'"
                 + " AND evenement.begindatum > '" + now + "'"       
                 + " AND (inschrijvings_status.naam = 'aangemeld' OR"
                 + " (inschrijvings_status.naam = 'website-aanmelding')"
                 + " AND evenement.iscanceled='false'"
               , null, null, null, false).nodeIterator();
            
            while(iNodes.hasNext()) {
                Node nextNode = iNodes.nextNode();
                String thisSubscription = nextNode.getStringValue("inschrijvingen.number");
                String thisEvent= nextNode.getStringValue("evenement.number");
                
                // send notification if this event or its parent event is a group excursion
                if (Evenement.isGroupExcursion(cloud, Evenement.findParentNumber(thisEvent))) {
                   SubscribeAction.sendConfirmationPeriodExpired(cloud, thisSubscription);
                   nEmailSend++;
                } 
            }   
         } catch(Exception e) {
            log.info(e);
         }
         logMessage += "\n<br>Number of groupevent confirmationperiod expired send " + nEmailSend;         
      }
      else {
         logMessage += "\n<br>Groupevent confirmationperiod expired notifications are only send on monday";        
      }      
      return logMessage;
   }   
   
   public void updateEventDB(Cloud cloud) { 
   
      MMBaseContext mc = new MMBaseContext();
      ServletContext application = mc.getServletContext();
      String requestUrl = (String) application.getAttribute("request_url");
      if(requestUrl==null) { requestUrl = ""; }

      String emailSubject = "Notificatie van " + requestUrl;

      String toEmailAddress = NatMMConfig.getToEmailAddress();
      String fromEmailAddress = NatMMConfig.getFromEmailAddress(); 

      log.info("Started updateEventDB");
      String logMessage =  "\n<br>Started updateEventDB " + new Date();

      boolean isProduction = NatMMConfig.isProductionApplication();
      
      if(isProduction) {
         logMessage += "\n<br>Site is production; reminder emails are send";
         log.info("Site is production; reminder emails are send");            
         
         logMessage += notifyParticipants(cloud);
         logMessage += lessThanMin(cloud);
         logMessage += isFullyBooked(cloud);
         if(false && isFirstDayOfNewQuarter()) {
            // this check does not work in practice (a) people only react first time they receive this email
            // and (b) the backoffice application at Natuurmonumenten can only handle fixed format email
            logMessage += checkEmailAccounts(cloud);
         }
         logMessage += groupEventConfirmationPeriodExpired(cloud);
      } else {
         logMessage += "\n<br>Site is no production, therefore no reminder emails send";
         log.info("Site is no production, therefore no reminder emails send");
      }

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
		  Cloud	cloud = CloudFactory.getCloud();
      ApplicationHelper ap = new ApplicationHelper(cloud);
      if(ap.isInstalled("NatMM")) {
        updateEventDB(cloud);
      }
   }
}