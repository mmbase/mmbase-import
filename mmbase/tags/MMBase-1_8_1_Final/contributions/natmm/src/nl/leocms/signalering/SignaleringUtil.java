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
package nl.leocms.signalering;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

import com.finalist.mmbase.util.CloudFactory;

import nl.leocms.authorization.AuthorizationHelper;
import nl.leocms.authorization.Roles;
import nl.leocms.content.ContentUtil;
import nl.leocms.util.PropertiesUtil;

/**
 * Util class to put or remove nodes in / from the signalering table.
 * @author  Ronald Kramp
 * @version $Revision   $
 */
public class SignaleringUtil {
   
   public final static int VERLOOP = 1;
   public final static int WIJZIGING = 2;
   public final static int LINK_WIJZIGING = 3;
   public final static int TAAK = 4;
   
   public final static String INIT = "init";
   public final static String REMINDER = "reminder";
   public final static String DONE = "done";
   public final static String REMOVE = "remove";
   
   // will be used in the takenlijst.jsp
   public final static String[] SIGNALERING_TYPES = new String[] {"", "Verloop", "Wijziging", "Linkwijziging", "Taak"};
   
   private static Logger log = Logging.getLoggerInstance(SignaleringUtil.class.getName());
   
   /**
    * add a signalering node
    * @param contentElementNodeNumber the contentelement related to the signalering
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   public static void addSignalering(String contentElementNodeNumber, int type) {
      Cloud cloud = CloudFactory.getCloud();
      NodeList contentElementsNodeList = cloud.getList(contentElementNodeNumber,
            "contentelement,schrijver,users", 
            "users.number", 
            null, null, null, null, true);
      if (contentElementsNodeList != null) {
         String[] userNodeNumbers = new String[contentElementsNodeList.size()];
         NodeIterator contentElementListIterator = contentElementsNodeList.nodeIterator();
         
         int i = 0;
         while (contentElementListIterator.hasNext()) {
            Node tempNode = contentElementListIterator.nextNode();
            String userNodeNumber = tempNode.getStringValue("users.number");
            userNodeNumbers[i++] = userNodeNumber;
         }
         addSignalering(contentElementNodeNumber, userNodeNumbers, -1, type);
      }
   }      
   
   
   /**
    * add a signalering node
    * @param contentElementNodeNumber the contentelement to related to the signalering
    * @param userNodeNumberTo the user who receives the signalering
    * @param verloopdatum the date the signalering expires
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   public static void addSignalering(String contentElementNodeNumber, 
         String userNodeNumberTo, 
         long verloopdatum, 
         int type) {
      addSignalering(contentElementNodeNumber, new String[] {userNodeNumberTo}, verloopdatum, type);
   }
   
   /**
    * add a signalering node
    * @param contentElementNodeNumber the contentelement to related to the signalering
    * @param userNodeNumbersTo the users who receives the signalering
    * @param verloopdatum the date the signalering expires
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   public static void addSignalering(String contentElementNodeNumber, 
         String[] userNodeNumbersTo, 
         long verloopdatum, 
         int type) {
      addSignalering(contentElementNodeNumber, userNodeNumbersTo, null, null, null, verloopdatum, type);
   }

   /**
    * add a signalering node
    * @param contentElementNodeNumber the contentelement to related to the signalering
    * @param userNodeNumberTo the users who receives the signalering
    * @param userNodeNumberFrom the user who sends/made the signalering
    * @param buildername the type of contentelement, cq name of the builder 
    * @param description extra info for the signalering
    * @param verloopdatum the date the signalering expires
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   public static void addSignalering(String contentElementNodeNumber, 
         String userNodeNumberTo, 
         String userNodeNumberFrom, 
         String buildername,
         String description, 
         long verloopdatum, 
         int type) {
      addSignalering(contentElementNodeNumber, new String[] {userNodeNumberTo}, userNodeNumberFrom, buildername, description, verloopdatum, type);
   }   
   
   /**
    * add a signalering node
    * @param contentElementNodeNumber the contentelement to related to the signalering
    * @param userNodeNumbersTo the users who receives the signalering
    * @param userNodeNumberFrom the user who sends/made the signalering
    * @param buildername the type of contentelement, cq name of the builder 
    * @param description extra info for the signalering
    * @param verloopdatum the date the signalering expires
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   public static void addSignalering(String contentElementNodeNumber, 
         String[] userNodeNumbersTo, 
         String userNodeNumberFrom, 
         String buildername,
         String description, 
         long verloopdatum, 
         int type) {
      addSignalering(contentElementNodeNumber, userNodeNumbersTo, userNodeNumberFrom, buildername, description, verloopdatum, -1, false, type);
   }   

   /**
    * add a signalering node
    * This method also checks if the signalering is not already added
    * @param contentElementNodeNumber the contentelement to related to the signalering
    * @param userNodeNumberTo the user who receives the signalering
    * @param userNodeNumberFrom the user who sends/made the signalering
    * @param buildername the type of contentelement, cq name of the builder 
    * @param description extra info for the signalering
    * @param verloopdatum the date the signalering expires
    * @param herhalingdatum the date the singalering thread should send an extra email
    * @param sendEmail indicator for sending an email at once to the receiver of the singalering
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   public static void addSignalering(String contentElementNodeNumber, 
         String userNodeNumberTo, 
         String userNodeNumberFrom, 
         String buildername, 
         String description, 
         long verloopdatum, 
         long herhalingdatum, 
         boolean sendEmail, 
         int type) {
      addSignalering(contentElementNodeNumber, new String[] {userNodeNumberTo}, userNodeNumberFrom, buildername, description, verloopdatum, herhalingdatum, sendEmail, type);
   }
   
   /**
    * add a signalering node
    * This method also checks if the signalering is not already added
    * @param contentElementNodeNumber the contentelement to related to the signalering
    * @param userNodeNumbersTo the users who receives the signalering
    * @param userNodeNumberFrom the user who sends/made the signalering
    * @param buildername the type of contentelement, cq name of the builder 
    * @param description extra info for the signalering
    * @param verloopdatum the date the signalering expires
    * @param herhalingdatum the date the singalering thread should send an extra email
    * @param sendEmail indicator for sending an email at once to the receiver of the singalering
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   public static void addSignalering(String contentElementNodeNumber, 
         String[] userNodeNumbersTo, 
         String userNodeNumberFrom, 
         String buildername, 
         String description, 
         long verloopdatum, 
         long herhalingdatum, 
         boolean sendEmail, 
         int type) {
      if (type == TAAK) {
         //this is a manual signalering for a page object
         // always add this one
         createSignaleringNode(contentElementNodeNumber, userNodeNumbersTo, userNodeNumberFrom, buildername, description, verloopdatum, herhalingdatum, sendEmail, type);
      }
      else {
         Cloud cloud = CloudFactory.getCloud();
         String constraints = "(";
         for (int i = 0; i < userNodeNumbersTo.length; i++) {
            constraints += userNodeNumbersTo[i] + ", ";
         }
         constraints = constraints.substring(0, constraints.lastIndexOf(","));
         constraints += ")";

         NodeList checkSignaleringNodeList= cloud.getList(contentElementNodeNumber,
               "contentelement,betreft,signalering,aan,users", 
               "signalering.number", 
               "[signalering.type] = " + type + " AND [users.number] IN " + constraints, null, null, null, true);
         if ((checkSignaleringNodeList == null) || (checkSignaleringNodeList.size() <= 0)) {
            createSignaleringNode(contentElementNodeNumber, userNodeNumbersTo, userNodeNumberFrom, buildername, description, verloopdatum, herhalingdatum, sendEmail, type);
         }
      }
   }
   
   /**
    * create the signalering node
    * @param contentElementNodeNumber the contentelement to related to the signalering
    * @param userNodeNumberTo the user who receives the signalering
    * @param userNodeNumberFrom the user who sends/made the signalering
    * @param buildername the type of contentelement, cq name of the builder 
    * @param description extra info for the signalering
    * @param verloopdatum the date the signalering expires
    * @param herhalingdatum the date the singalering thread should send an extra email
    * @param sendEmail indicator for sending an email at once to the receiver of the singalering
    * @param type the type of the signalering (Verloop, wijziging, link_wijziging of taak)
    **/
   private static void createSignaleringNode(String contentElementNodeNumber, 
         String[] userNodeNumbersTo, 
         String userNodeNumberFrom, 
         String buildername, 
         String description, 
         long verloopdatum, 
         long herhalingdatum, 
         boolean sendEmail, 
         int type) {
            
      if ((userNodeNumbersTo != null) && (userNodeNumbersTo.length > 0)) {            
            
         long dateInMillis = (new Date()).getTime() / 1000;
         Cloud cloud = CloudFactory.getCloud();
         Transaction transaction = cloud.getTransaction(contentElementNodeNumber);
                     
         NodeManager signaleringNodeManager = transaction.getNodeManager("signalering");
               
         Node signaleringNode = signaleringNodeManager.createNode();
         signaleringNode.setLongValue("creatiedatum", dateInMillis);
         signaleringNode.setLongValue("herhalingdatum", herhalingdatum);
         signaleringNode.setLongValue("verloopdatum", verloopdatum);
         if ((description != null) && (!description.trim().equals(""))) {
            signaleringNode.setStringValue("omschrijving", description);
         }
         if ((buildername != null) && (!buildername.trim().equals(""))) {
            signaleringNode.setStringValue("builder", buildername);
         }
         signaleringNode.setStringValue("status", "init");
         signaleringNode.setIntValue("type", type);
         signaleringNode.commit();
      
         Node contentElementNode = transaction.getNode(contentElementNodeNumber);
         RelationManager betreftRelation = transaction.getRelationManager("betreft");         
         Relation relationBetreftWithContentElement = betreftRelation.createRelation(signaleringNode, contentElementNode);   
         RelationManager aanRelation = transaction.getRelationManager("aan");
         
         Node userFromNode = null;
      
         if ((userNodeNumberFrom != null) && (!userNodeNumberFrom.trim().equals(""))) {
            userFromNode = transaction.getNode(userNodeNumberFrom);
            RelationManager afzenderRelation = transaction.getRelationManager("afzender");
            Relation relationAfzenderWithUser = afzenderRelation.createRelation(signaleringNode, userFromNode);
            relationAfzenderWithUser.commit();
         }
         
         for (int i = 0; i < userNodeNumbersTo.length; i++) {
            Node userNode = transaction.getNode(userNodeNumbersTo[i]);
            Relation relationAanWithUser = aanRelation.createRelation(signaleringNode, userNode);
            relationBetreftWithContentElement.commit();
            relationAanWithUser.commit();
            
            boolean emailSignalering = userNode.getBooleanValue("emailsignalering");
      
            if (((type == TAAK) && (sendEmail)) || ((type != TAAK) && (emailSignalering))) {
               try {
                  String voornaam = userNode.getStringValue("voornaam");
                  String achternaam = userNode.getStringValue("achternaam");
                  String tussenVoegsel = userNode.getStringValue("tussenvoegsel");
                  String emailAdres = userNode.getStringValue("emailadres");
                  String name = voornaam + (((tussenVoegsel != null) && (!tussenVoegsel.trim().equals(""))) ? " " + tussenVoegsel : "") + " " + achternaam;
                  
                  String emailAdresFrom = PropertiesUtil.getProperty("mail.sender.email");
                  String nameFrom = PropertiesUtil.getProperty("mail.sender.name");
                  
                  if (userFromNode != null) {
                     String voornaamFrom = userFromNode.getStringValue("voornaam");
                     String achternaamFrom = userFromNode.getStringValue("achternaam");
                     String tussenVoegselFrom = userFromNode.getStringValue("tussenvoegsel");
                     emailAdresFrom = userFromNode.getStringValue("emailadres");
                     nameFrom = voornaamFrom + (((tussenVoegselFrom != null) && (!tussenVoegselFrom.trim().equals(""))) ? " " + tussenVoegselFrom : "") + " " + achternaamFrom;
                  }

                Node emailNode = cloud.getNodeManager("email").createNode();
                emailNode.setValue("to", emailAdres);
                emailNode.setValue("from", emailAdresFrom);
                emailNode.setValue("subject", "Takenlijst signalering");
                emailNode.setValue("replyto", emailAdresFrom);
                emailNode.setValue("body","Beste " + name + ",\n\nEr is een taak in uw takenlijst toegevoegd.\n" +
                     "Ga naar de beheer omgeving van de website om de taak te bekijken en uit te voeren.\n\n" +
                     "Extra opmerking: " + ((description != null) ? description : "geen") + "\n\n" +
                     "Met vriendelijke groet,\n\n" + nameFrom);
                emailNode.commit();
                emailNode.getValue("mail(oneshot)");
               }
               catch(Exception e) {
                  log.warn("Could not send email: " + e);
               }
            }
         }
         relationBetreftWithContentElement.commit();
         transaction.commit();
      }
   }

   /**
    * Removes the node from the signalering table
    * if the signalering node has the type VERLOOP and the contentelement verloopdatum 
    * has changed it will be removed. If not not it will get status remove.
    * @param number the number of the node to be removed
    **/
   public static void removeNode(String number) {
      Cloud cloud = CloudFactory.getCloud();
      Transaction transaction = cloud.getTransaction(number);
      
      Node signaleringNode = transaction.getNode(number);
      int type = signaleringNode.getIntValue("type");
      String status = signaleringNode.getStringValue("status");
      
      if (type == VERLOOP) {
         try {
            NodeList signaleringNodeList = cloud.getList(number,
                  "signalering,betreft,contentelement", 
                  "signalering.number", 
                  "[contentelement.verloopdatum] > [signalering.verloopdatum]", null, null, null, true);
           
            if ((signaleringNodeList != null) && (signaleringNodeList.size() > 0)) {
               signaleringNode.delete(true);
            }
            else {
               signaleringNode.setStringValue("status", REMOVE);
            }
         } 
         catch (Throwable e) {
            log.error("Throwable error:" + e.getMessage());
            log.debug(Logging.stackTrace(e));
         }
      }
      else {
         signaleringNode.delete(true);
      }
      
      signaleringNode.commit();
      transaction.commit();
   }
   
   public static void createLinkWijziging(Node content, Cloud c) {
      NodeList pages = content.getRelatedNodes("pagina", "contentrel", "SOURCE");
      
      if (!pages.isEmpty()) {
         //create signalering LINKWIJZIGING
         String contentElementNodeNumber = ""+content.getNumber();
         
         AuthorizationHelper auth = new AuthorizationHelper(c);
         ContentUtil cu = new ContentUtil(c);
      
         NodeIterator nIter = pages.nodeIterator();
         while(nIter.hasNext()) {
            Node pagina = nIter.nextNode();
            
            Node creatieRubriek = cu.getCreatieRubriek(pagina);
            List schrijvers = auth.getUsersWithRights(creatieRubriek, Roles.SCHRIJVER);
      
            String[] userNodeNumbers = new String[schrijvers.size()];
            Iterator iter = schrijvers.iterator();
            int i = 0;
            while (iter.hasNext()) {
               Node schrijver = (Node) iter.next();
               userNodeNumbers[i] = ""+schrijver.getNumber();
               i++;
            }
            
            SignaleringUtil.addSignalering(contentElementNodeNumber, userNodeNumbers, -1, LINK_WIJZIGING);
         }
      }
   }

   public static void createWijziging(Node pagina, Cloud c) {
      AuthorizationHelper auth = new AuthorizationHelper(c);
      ContentUtil cu = new ContentUtil(c);
      
      Node creatieRubriek = cu.getCreatieRubriek(pagina);
      List schrijvers = auth.getUsersWithRights(creatieRubriek, Roles.SCHRIJVER);
      
      String contentElementNodeNumber = ""+pagina.getNumber();
      String[] userNodeNumbers = new String[schrijvers.size()];
      Iterator iter = schrijvers.iterator();
      int i = 0;
      while (iter.hasNext()) {
         Node schrijver = (Node) iter.next();
         userNodeNumbers[i] = ""+schrijver.getNumber();
         i++;
      }
      SignaleringUtil.addSignalering(contentElementNodeNumber, userNodeNumbers, -1, WIJZIGING);
   }

}
