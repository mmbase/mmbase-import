/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.module;

import org.mmbase.bridge.*;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;
import com.finalist.mmbase.util.CloudFactory;
import java.util.Date;
import nl.leocms.signalering.SignaleringUtil;
import nl.leocms.util.PropertiesUtil;

/**
 * Module that searches for content items that are moving towards the deadline.
 * Also it send email to people who get a reminder for a task to complete. 
 * 
 * @author Ronald Kramp
 * @version $Revision  $
 */
public class SignaleringModule extends Module implements Runnable {
   /** MMBase logging system */
    private static Logger log = Logging.getLoggerInstance(SignaleringModule.class.getName());
    
   //Object sync;
   private static Thread thread = null;
   
   /** time in seconds the content items will be put in for signalering for their deadline */
    int expireTime = 60 * 60 * 24 * 7; // 7 days in seconds
    
    /** time in seconds the thread checks for signaleringen */
   int signaleringPeriod = 60* 60 * 12; // every 12 hours

    /** The mmbase instance */
    private MMBase mmb = null;

   
   /**
     * @see org.mmbase.module.Module#onload()
     */
    public void onload() {
    }
   
   /**
     * @see org.mmbase.module.Module#init()
     * Also read for the config parameters expiretime and signaleringperiod from de module.xml
     */
    public void init() {
        mmb = (MMBase) Module.getModule("MMBASEROOT");
        // Initialize the module.
        if (((getInitParameter("expiretime") != null)) && (!getInitParameter("expiretime").equals(""))) {
         try {
            expireTime = Integer.parseInt(getInitParameter("expiretime"));
         }
         catch (NumberFormatException nfe) {
            log.warn("expiretime parameter is not set correctly");
         }
      } 
      else {
         log.warn("expiretime parameter missing");
      }
      log.info("Using expiretime: " + expireTime);
      
      if (((getInitParameter("signaleringperiod") != null)) && (!getInitParameter("signaleringperiod").equals(""))) {
         try {
            signaleringPeriod = Integer.parseInt(getInitParameter("signaleringperiod"));
         }
         catch (NumberFormatException nfe) {
            log.warn("signaleringperiod parameter is not set correctly");
         }
      } 
      else {
         log.warn("signaleringperiod parameter missing");
      }
      log.info("Using signaleringperiod: " + signaleringPeriod);
        // Start thread to wait for mmbase to be up and running.
        thread = new Thread(this);
      thread.setDaemon(true);
      thread.start();
    }
   
   /*
    * The thread in which the singaleringen will be added and removed
    */
   private void searchForSignalering() {
      log.info("Signalering thread started");

      while (true) {
         try {
            // wait for mmbase to put in default data
            Thread.sleep(signaleringPeriod * 1000);
         } 
         catch (InterruptedException e) {
            log.warn("Interupted while sleeping , continuing");
         }
         long dateInMillis = (new Date()).getTime() / 1000;
         long dateWithExpireTime = dateInMillis + expireTime;
         Cloud cloud = CloudFactory.getCloud();
         
         
         // check every contentelement on expire date.
         // if contentelement expires add signalering to table 
         try {
            NodeList contentElementsNodeList = cloud.getList("",
                  "contentelement,schrijver,users", 
                  "contentelement.number,users.number,contentelement.verloopdatum", 
                  "contentelement.verloopdatum < " + dateWithExpireTime, null, null, null, true);
           
            NodeIterator contentElementListIterator = contentElementsNodeList.nodeIterator();
            while (contentElementListIterator.hasNext()) {
               Node tempNode = contentElementListIterator.nextNode();
               String userNumber = tempNode.getStringValue("users.number");
               String contentElementNumber = tempNode.getStringValue("contentelement.number");
               int verloopdatum = tempNode.getIntValue("contentelement.verloopdatum");
               
               SignaleringUtil.addSignalering(contentElementNumber, userNumber, verloopdatum, SignaleringUtil.VERLOOP);
            }
         } 
         catch (Throwable e) {
            log.error("Throwable error:" + e.getMessage());
            log.debug(Logging.stackTrace(e));
         }
         
         // check every signalering on status remove and expire date from contentelement is expired
         // than remove these nodes
         try {
            NodeList contentElementsNodeList = cloud.getList("",
                  "contentelement,betreft,signalering", 
                  "signalering.number", 
                  "[signalering.status] = 'remove' AND contentelement.verloopdatum > 0 AND contentelement.verloopdatum < " + dateInMillis, null, null, null, true);
           
            NodeIterator contentElementListIterator = contentElementsNodeList.nodeIterator();
            while (contentElementListIterator.hasNext()) {
               Node tempNode = contentElementListIterator.nextNode();
               String signaleringNodeNumber = tempNode.getStringValue("signalering.number");
               
               SignaleringUtil.removeNode(signaleringNodeNumber);
            }
         } 
         catch (Throwable e) {
            log.error("Throwable error:" + e.getMessage());
            log.debug(Logging.stackTrace(e));
         }
         
         
         // send email for signaleringen that have a repeating email notification
         NodeList list = cloud.getNodeManager("signalering").getList("[herhalingdatum] < " + dateInMillis + " AND [herhalingdatum] > 0 AND [status]='init' and [type] = 4", null, null);

         for (int x = 0; x < list.size(); x++) {
            Node node = list.getNode(x);

            try {
               node.setStringValue("status", "reminder");
               node.commit();
               NodeList aanList = node.getRelatedNodes("users", "aan", "DESTINATION");

               if ((aanList != null) && (aanList.size() == 1)) {
                  Node userNode = aanList.getNode(0);
                  String voornaam = userNode.getStringValue("voornaam");
                  String achternaam = userNode.getStringValue("achternaam");
                  String tussenVoegsel = userNode.getStringValue("tussenvoegsel");
                  String emailAdres = userNode.getStringValue("emailadres");
                  String name = voornaam + (((tussenVoegsel != null) && (!tussenVoegsel.trim().equals(""))) ? " " + tussenVoegsel : "") + " " + achternaam;
                  
                  String emailAdresFrom = PropertiesUtil.getProperty("mail.sender.email");
                  String nameFrom = PropertiesUtil.getProperty("mail.sender.name");
                  
                  NodeList afzenderList = node.getRelatedNodes("users","afzender", "DESTINATION");
                  
                  if ((afzenderList != null) && (afzenderList.size() == 1)) {
                     Node userFromNode = afzenderList.getNode(0);
                  
                     String voornaamFrom = userFromNode.getStringValue("voornaam");
                     String achternaamFrom = userFromNode.getStringValue("achternaam");
                     String tussenVoegselFrom = userFromNode.getStringValue("tussenvoegsel");
                     emailAdresFrom = userFromNode.getStringValue("emailadres");
                     nameFrom = voornaamFrom + (((tussenVoegselFrom != null) && (!tussenVoegselFrom.trim().equals(""))) ? " " + tussenVoegselFrom : "") + " " + achternaamFrom;
                  }
                  try {

                    Node emailNode = cloud.getNodeManager("email").createNode();
                    emailNode.setValue("to", emailAdres);
                    emailNode.setValue("from", emailAdresFrom);
                    emailNode.setValue("subject", "Takenlijst verloop");
                    emailNode.setValue("replyto", emailAdresFrom);
                    emailNode.setValue("body","Beste " + name + ",\n\nEr is een taak in uw takenlijst die verloopt.\n" +
                        "Ga naar de beheer omgeving van de website om de taak te bekijken en uit te voeren.\n\n" +
                        "Met vriendelijke groet,\n\n" + nameFrom);
                    emailNode.commit();
                    emailNode.getValue("mail(oneshot)");
                  }
                  catch(Exception e) {
                     log.warn("Could not send email: " + e);
                  }
               }
            } 
            catch (BridgeException e) {
               log.error(e);
            }
         }
      }
   }
   
   /**
     * Wait for mmbase to be up and running,
     * then execute the tests.
     */
    public void run() {
        // Wait for mmbase to be up & running.
        while (!mmb.getState()) {
            try {
                Thread.sleep(1000);
            } 
            catch (InterruptedException e) {}
        }
        searchForSignalering();
    }
}