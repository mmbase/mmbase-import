package nl.didactor.component.assessment.email_notification.cron;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import net.sf.mmapps.modules.cloudprovider.*;
import nl.didactor.component.assessment.email_notification.model.Email;



public class Sender extends Thread{
   private static Logger log = Logging.getLoggerInstance(Sender.class);

   private CloudProvider cloudProvider;
   private Cloud cloud;
   private String sNotificationFrom = null;

   public Sender() {
      cloudProvider = CloudProviderFactory.getCloudProvider();
      cloud = cloudProvider.getAdminCloud();
   }

   public void run() {
      log.info("Assessment Email Notifications Sender has been started.");

      NodeList nlAdmin = cloud.getList("",
                                       "people",
                                       "people.number",
                                       "people.username='admin'",
                                       null, null, null, false);

      if(nlAdmin.size() > 0){
         String sNotificationFrom = cloud.getNode(nlAdmin.getNode(0).getStringValue("people.number")).getStringValue("email");
         if("".equals(sNotificationFrom)){
            log.error("Admin's email is empty. Assessment Email Notifications Sender will be stopped.");
            this.destroy();
         }
         else{
            log.debug("Admin's email=\"" + sNotificationFrom + "\" for using in Email Feedback");
         }
      }
      else{
         log.error("Can't find admin node to send a Email Notification. Assessment Email Notifications Sender will be stopped.");
         this.destroy();
      }



      NodeList nlEmailNotifications = cloud.getList("",
         "email_notifications",
         "email_notifications.number, email_notifications.trigger_type",
         "email_notifications.trigger_type >= '0'",
         null, null, null, false);

      log.info("We have got " + nlEmailNotifications.size() + " active email notifications");


      //The whole list of emails
      HashSet hsetUsers;

      for(NodeIterator it = nlEmailNotifications.nodeIterator(); it.hasNext();){
         Node nodeEmailNotification = cloud.getNode(it.nextNode().getStringValue("email_notifications.number"));

         log.debug("Emails in list in Email Notification=" + nodeEmailNotification.getNumber());
         log.debug("------------------------------------------------");



         switch(nodeEmailNotification.getIntValue("trigger_type")){
            case 0:{
               log.info("\"Send On Date\" mode ");
               if((new Date()).getTime() > nodeEmailNotification.getLongValue("senddate") * 1000){
                  //It's time we sent the notifications
                  nodeEmailNotification.setStringValue("trigger_type", "-1");
                  nodeEmailNotification.commit();


                  hsetUsers = collectUsers(nodeEmailNotification);
                  sendEmails(hsetUsers, nodeEmailNotification);
               }
               break;
            }
            case 1:{
               log.info("\"Send On not finished lesson\" mode ");

               //Send immediatly
               hsetUsers = collectUsers(nodeEmailNotification);
               ArrayList arliEmails = getEmails(hsetUsers, nodeEmailNotification);
               sendEmails(arliEmails);
               break;
            }
         }
      }
   }





   /**
    * Collects all related users
    *
    * @param nodeEmailNotification Node
    * @return HashSet
    */
   private HashSet collectUsers(Node nodeEmailNotification){
      HashSet hashsetUsers = new HashSet();

      NodeList nlPeople = nodeEmailNotification.getRelatedNodes("people");
      addPeople(hashsetUsers, nlPeople);


      NodeList nlClasses = nodeEmailNotification.getRelatedNodes("classes");
      for(NodeIterator it2 = nlClasses.nodeIterator(); it2.hasNext();){
         Node nodeClass = it2.nextNode();

         nlPeople = nodeClass.getRelatedNodes("people", "classrel", "source");
         addPeople(hashsetUsers, nlPeople);
      }

      return hashsetUsers;
   }






   /**
    * Do user filtering
    * @param hsetUsers HashSet
    * @return HashSet
    */
   private ArrayList getEmails(HashSet hsetUsers, Node nodeEmailNotification){
      ArrayList arliResult = new ArrayList();


      for(Iterator it = hsetUsers.iterator(); it.hasNext();){
         Node nodeUser = (Node) it.next();

         log.debug("User=" + nodeUser.getNumber() + " has got a suspicious list cleaned");

         //Suspicious list
         HashMap hmapSuspiciousFeedbacks = new HashMap();


         NodeList nlVirtual = cloud.getList("component.assessment",
                                            "components,settingrel,educations,posrel,learnblocks,classrel,people",
                                            "learnblocks.number,classrel.number,educations.number",
                                            "people.number='" + nodeUser.getNumber() + "'",
                                            "posrel.pos",
                                            null, null, true);


         Node nodeLastEducation = null;
         boolean bPrevousFeedbackPresent = false;
         for(NodeIterator it2 = nlVirtual.nodeIterator(); it2.hasNext();){
            Node nodeVirtual = it2.nextNode();
            Node nodeLession = nodeVirtual.getNodeValue("learnblocks.number");
            Node nodeClassrel = nodeVirtual.getNodeValue("classrel.number");
            Node nodeEducation = nodeVirtual.getNodeValue("educations.number");


            log.debug("Current education=" + nodeEducation.getNumber());
            if(nodeLastEducation != null){
               log.debug("Prevoius education=" + nodeLastEducation.getNumber());
            }
            else{
               log.debug("Prevoius education=null");
            }
            if((nodeLastEducation != null) && (nodeEducation != nodeLastEducation)){
               //We have passed all learnblocks in the nodeLastEducation
               if(bPrevousFeedbackPresent == true){
                  //The education was closed
                  //So we removing it from the suspicious list
                  hmapSuspiciousFeedbacks.remove(nodeLastEducation);
                  log.debug("Educaiton=" + nodeLastEducation.getNumber() + " for user=" + nodeUser.getNumber() + " HAS BEEN CLOSED. No feedback needed");
               }
               else{
                  //The education is still open and has got at least one feedback for its lessions
               }
            }



            NodeList nlFeedbacks = nodeClassrel.getRelatedNodes("popfeedback");

            if(nlFeedbacks.size() == 0){
               //No feedback for this lession. It means this is not closed one.
               log.debug("Learnblock=" + nodeLession.getNumber() + " has got no feedback.");
               bPrevousFeedbackPresent = false;
            }
            else{
               //There is a feedback
               Node nodeFeedBack = nlFeedbacks.getNode(0);
               log.debug("Learnblock=" + nodeLession.getNumber() + " has got a feedback=" + nodeFeedBack.getNumber());

               hmapSuspiciousFeedbacks.put(nodeEducation, nodeFeedBack);
               bPrevousFeedbackPresent = true;;
               log.debug("Education=" + nodeEducation.getNumber() + " has been ADDED to the suspicious list.");
            }

            nodeLastEducation = nodeEducation;
         }


         //Composing Email objects
         for(Iterator it3 = hmapSuspiciousFeedbacks.keySet().iterator(); it3.hasNext();){
            Node nodeEducation = (Node) it3.next();
            Node nodeFeedback = (Node) hmapSuspiciousFeedbacks.get(nodeEducation);

            int iLastFeedBackAge_In_Days = nodeFeedback.getFunctionValue("age", null).toInt();

            if(nodeEmailNotification.getIntValue("trigger_setting1") * 7 > iLastFeedBackAge_In_Days){
               Email email = new Email();
               email.setSubject(nodeEducation.getStringValue("name") + ":" + nodeEmailNotification.getStringValue("subject"));
               email.setBody(nodeEmailNotification.getStringValue("body"));
               email.setFrom(sNotificationFrom);
               email.setTo(nodeUser.getStringValue("email"));

               arliResult.add(email);
               log.debug("User=" + nodeUser.getNumber() + " will recieve a email feedback. It has got more than " + nodeEmailNotification.getIntValue("trigger_setting1") + " days since the latest learnblock was closed");
            }
         }
      }
      return arliResult;
   }




   /**
    * Add people to the list
    *
    * @param hsetEmails HashSet
    * @param nlPeople NodeList
    * @param nodeEmailNotification Node
    */
   private void addPeople(HashSet hsetEmails, NodeList nlPeople){
      for(NodeIterator it2 = nlPeople.nodeIterator(); it2.hasNext();){
         Node nodePerson = it2.nextNode();

         hsetEmails.add(nodePerson);
         log.debug("User=" + nodePerson.getNumber() + " is under control. He can receive a notification if the conditions are ok.");
      }
   }




   /**
    * Sends emails
    *
    * @param hsetEmails HashSet
    * @param sFrom String
    * @param sTo String
    * @param sSubjet String
    * @param sBody String
    */
   private void sendEmails(HashSet hsetUsers, Node nodeEmailNotification){
      for (Iterator it = hsetUsers.iterator(); it.hasNext(); ) {
         Node nodeUser = (Node) it.next();

         log.debug("A email notification to the user=" + nodeUser.getNumber());

         Email email = new Email();
         email.setFrom(sNotificationFrom);
         email.setSubject(nodeEmailNotification.getStringValue("subject"));
         email.setTo(nodeUser.getStringValue("email"));
         email.setBody(nodeEmailNotification.getStringValue("body"));
         sendTheEmail(email);
      }
   }
   private void sendEmails(ArrayList arliEmails){
      for(Iterator it = arliEmails.iterator(); it.hasNext();){
         Email email = (Email) it.next();
         sendTheEmail(email);
      }
   }



   /**
    * Sends this particular letter
    *
    * @param email Email
    */
   private void sendTheEmail(Email email){
      Node emailNode = cloud.getNodeManager("emails").createNode();
      emailNode.setValue("from", email.getFrom());
      emailNode.setValue("subject", email.getSubject());
      emailNode.setValue("to", email.getTo());
      emailNode.setValue("body", email.getBody());
      emailNode.setValue("date", "" + (new Date()).getTime() / 1000);
      emailNode.commit();

      emailNode.getValue("mail(oneshot)");
   }

}
