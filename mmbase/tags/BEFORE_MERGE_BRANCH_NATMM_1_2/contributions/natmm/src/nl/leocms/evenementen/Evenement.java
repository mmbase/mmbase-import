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
package nl.leocms.evenementen;

import org.mmbase.util.logging.*;
import nl.leocms.util.DoubleDateNode;
import java.util.*;
import com.finalist.mmbase.util.CloudFactory;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;

/**
 * Evenement
 *
 * @author Henk Hangyi
 * @version $Revision: 1.15 $, $Date: 2008-10-23 13:14:26 $
 *
 */

public class Evenement extends DoubleDateNode {
   private static final Logger log = Logging.getLoggerInstance(Evenement.class);

   private String isSpareDate;
   private String isOnInternet;
   private String isCanceled;

   public Evenement() {
      super();
      this.isSpareDate = "false";
      this.isOnInternet = "true";
      this.isCanceled = "false";
   }

   public Evenement(int nextNewNodeNumber) {
      super(nextNewNodeNumber);
      this.isSpareDate = "false";
      this.isOnInternet = "true";
      this.isCanceled = "false";
   }

   public Evenement(Node node) {
      super(node);
      setIsSpareDate(node.getStringValue("isspare"));
      setIsOnInternet(node.getStringValue("isoninternet"));
      setIsCanceled(node.getStringValue("iscanceled"));
   }

   public String getIsSpareDate() {
      return isSpareDate;
   }

   public void setIsSpareDate(String isSpareDate) {
      if (isSpareDate != null && isSpareDate.equals("true")) {
         this.isSpareDate = "true";
      }
      else {
         this.isSpareDate = "false";
      }
   }

   public String getIsOnInternet() {
      return isOnInternet;
   }

   public void setIsOnInternet(String isOnInternet) {
      if (isOnInternet != null && isOnInternet.equals("true")) {
         this.isOnInternet = "true";
      }
      else {
         this.isOnInternet = "false";
      }
   }

   public String getIsCanceled() {
      return isCanceled;
   }

   public void setIsCanceled(String isCanceled) {
      if (isCanceled != null && isCanceled.equals("true")) {
         this.isCanceled = "true";
      }
      else {
         this.isCanceled = "false";
      }
   }

   public String getCommaSeparatedValue() {
      return super.getCommaSeparatedValue() + "," + this.isSpareDate + "," + this.isOnInternet + "," + this.isCanceled;
   }

   public boolean hasChild() {
      boolean hasChild = false;
      // negative numbers implies virtual events
      if (this.getNumber().indexOf("-") == -1) {
         Cloud cloud = CloudFactory.getCloud();
         Node thisEvent = cloud.getNode(this.getNumber());
         NodeList evenementList = thisEvent.getRelatedNodes("evenement", "partrel", "destination");
         hasChild = (evenementList.size() > 0);
      }
      return hasChild;
   }

   public static boolean isOfCategory(Node parent, String sParticipantsCategory) {
      boolean isOfCategory = false;
      NodeList nl = parent.getRelatedNodes("deelnemers_categorie", "posrel", null);
      for (int i = 0; i < nl.size(); i++) {
         if (nl.getNode(i).getStringValue("number").equals(sParticipantsCategory)) {
            isOfCategory = true;
         }
      }
      return isOfCategory;
   }

   public static int getCategoryCosts(Node thisSubscription, String sParticipantsCategory) {
      int costs = 0;
      RelationList relations = thisSubscription.getRelations("posrel", "deelnemers");
      for (int p = 0; p < relations.size(); p++) {
         Relation relation = relations.getRelation(p);
         Node thisParticipant = relation.getDestination();
         log.debug("found participant " + thisParticipant.getStringValue("number"));
         NodeList nl = thisParticipant.getRelatedNodes("deelnemers_categorie", "related", null);
         for (int dc = 0; dc < nl.size(); dc++) {
            log.debug("found dc " + nl.getNode(dc).getStringValue("number"));
            if (nl.getNode(dc).getStringValue("number").equals(sParticipantsCategory)) {
               costs += relation.getIntValue("pos");
               log.debug("costs is now " + costs);
            }
         }
      }
      return costs;
   }

   public static Node getGroupExcursion(Node parentEvent) {
      Node dc = null;
      NodeList dcl = parentEvent.getRelatedNodes("deelnemers_categorie");
      for (int i = 0; i < dcl.size(); i++) {
         if (dcl.getNode(i).getIntValue("groepsactiviteit") == 1) {
            dc = dcl.getNode(i);
         }
      }
      return dc;
   }

   public static boolean isGroupExcursion(Node parentEvent) {
      boolean isGroupExcursion = false;
      NodeList dcl = parentEvent.getRelatedNodes("deelnemers_categorie");
      for (int i = 0; i < dcl.size(); i++) {
         if (dcl.getNode(i).getIntValue("groepsactiviteit") == 1) {
            isGroupExcursion = true;
         }
      }
      return isGroupExcursion;
   }

   public static boolean isGroupExcursion(Cloud cloud, String sParent) {
      NodeList dcl = cloud.getList(sParent
                                   , "evenement,posrel,deelnemers_categorie"
                                   , "posrel.pos"
                                   , "deelnemers_categorie.groepsactiviteit='1'", null, null, null, false);
      return (dcl.size() > 0);
   }

   public static boolean isGroupBooking(Cloud cloud, String sParticipant) {
      NodeList nl = cloud.getList(sParticipant
                                  , "deelnemers,related,deelnemers_categorie"
                                  , "deelnemers_categorie.number"
                                  , "deelnemers_categorie.groepsactiviteit='1'", null, null, null, false);
      return (nl.size() > 0);
   }

   public static boolean isGroupSubscription(Cloud cloud, String sSubscription) {
      NodeList nl = cloud.getList(sSubscription
                                  , "inschrijvingen,posrel,deelnemers,related,deelnemers_categorie"
                                  , "deelnemers_categorie.number"
                                  , "deelnemers_categorie.groepsactiviteit='1'", null, null, null, false);
      return (nl.size() > 0);
   }

   public static int getGroupExcursionCosts(Cloud cloud, String sParent, String sSubscription) {

      int costs = -1;
      NodeList nl = cloud.getList(sSubscription
                                  , "inschrijvingen,daterel,bevestigings_teksten,posrel,evenement"
                                  , "posrel.pos"
                                  , "evenement.number='" + sParent + "'", null, null, null, false);
      if (nl.size() > 0) {
         costs = nl.getNode(0).getIntValue("posrel.pos");
      }
      else {
         nl = cloud.getList(sParent
                            , "evenement,posrel,deelnemers_categorie"
                            , "posrel.pos"
                            , "deelnemers_categorie.groepsactiviteit='1'", null, null, null, false);
         if (nl.size() > 0) {
            costs = nl.getNode(0).getIntValue("posrel.pos");
         }
      }
      return costs;
   }

   public static void updateGroupExcursionCosts(Cloud cloud, String sParent, String sSubscription) {

      RelationList relations = cloud.getNode(sSubscription).getRelations("posrel", "deelnemers");
      for (int r = 0; r < relations.size(); r++) {
         Relation thisRel = relations.getRelation(r);
         String sParticipant = thisRel.getDestination().getStringValue("number");
         if (isGroupBooking(cloud, sParticipant)) {
            int costs = getGroupExcursionCosts(cloud, sParent, sSubscription);
            thisRel.setIntValue("pos", costs);
            thisRel.commit();
         }
      }
   }

   public static String getNextOccurence(String thisParent) {
      // *** returns the next occurence of this event, if not available it returns the parent
      long now = (new Date().getTime()) / 1000 - (24 * 60 * 60);
      String nextChild = thisParent;
      String sChildConstraint =
         " (( evenement2.dagomschrijving LIKE '' AND evenement2.begindatum > " + now + ") "
         + " OR ( evenement2.dagomschrijving NOT LIKE '' AND  evenement2.einddatum > " + now + ")) "
         + " AND ( evenement2.isspare!='true') AND ( evenement2.isoninternet='true' )";
      Cloud cloud = CloudFactory.getCloud();
      NodeList cl = cloud.getList(thisParent,
                                  "evenement,partrel,evenement2", "evenement.begindatum,evenement2.number,evenement2.begindatum", sChildConstraint,
                                  "evenement2.begindatum", "UP", "DESTINATION", true);
      if (cl.size() > 0) {
         long parentBegin = cl.getNode(0).getLongValue("evenement.begindatum");
         long nextChildBegin = cl.getNode(0).getLongValue("evenement2.begindatum");
         if ( (parentBegin <= now) || (nextChildBegin < parentBegin)) {
            nextChild = cl.getNode(0).getStringValue("evenement2.number");
         }
         
      }
      return nextChild;
   }

   public boolean hasBooking() {
      boolean hasBooking = false;
      // negative numbers implies virtual events
      if (this.getNumber().indexOf("-") == -1) {
         Cloud cloud = CloudFactory.getCloud();
         Node thisEvent = cloud.getNode(this.getNumber());
         NodeList bookingList = thisEvent.getRelatedNodes("inschrijvingen", "posrel", "destination");
         hasBooking = (bookingList.size() > 0);
      }
      return hasBooking;
   }

   public static boolean isFullyBooked(Node parentEvent, Node childEvent) {
      int iMaxNumber = -1;
      try {
         iMaxNumber = (new Integer(parentEvent.getStringValue("max_aantal_deelnemers"))).intValue();
      }
      catch (Exception e) {}
      if (iMaxNumber == -1) iMaxNumber = 9999;
      int iChildCurParticipants = -1;
      try {
         iChildCurParticipants = (new Integer(childEvent.getStringValue("cur_aantal_deelnemers"))).intValue();
      }
      catch (Exception e) {}
      return (iChildCurParticipants >= iMaxNumber);
   }

   public static String getStatus(Cloud cloud, String status_alias) {
      return cloud.getNode(status_alias).getStringValue("number");
   }

   public static boolean hasStatus(Cloud cloud, Node childEvent, String status_alias) {
      NodeList nl = cloud.getList(childEvent.getStringValue("number")
                                  , "evenement,posrel,inschrijvingen,related,inschrijvings_status", "inschrijvings_status.number"
                                  , "inschrijvings_status.number = '" + getStatus(cloud, status_alias) + "'"
                                  , null, null, null, false);
      return (nl.size() > 0);
   }

   public static String altEvent(Cloud cloud, String sParent, String sChild) {
      // find within the alternative for sParent, the event that takes place at the same time as sChild
      String altEvent = "-1";
      Node parent = cloud.getNode(sParent);
      NodeList nl = parent.getRelatedNodes("evenement", "altrel", null);
      if (nl.size() != 0) {
         log.debug("there is an alternative to " + sParent);
         Node child = cloud.getNode(sChild);
         long cBegin = child.getLongValue("begindatum") - 60 * 60;
         long cEnd = child.getLongValue("einddatum") + 60 * 60;
         Node altparent = nl.getNode(0);
         String sAltParent = altparent.getStringValue("number");
         long pBegin = altparent.getLongValue("begindatum");
         long pEnd = altparent.getLongValue("einddatum");
         if (cBegin < pBegin && pEnd < cEnd) {
            log.debug("the altparent " + sAltParent + " is at the same time as sChild");
            altEvent = sAltParent;
         }
         else {
            nl = cloud.getList(sAltParent, "evenement1,partrel,evenement", "evenement.number"
                               , "evenement.begindatum > " + cBegin + " AND evenement.einddatum < " + cEnd
                               , "evenement.begindatum", "UP", null, true);
            if (nl.size() != 0) {
               log.debug("one of the childs of altparent " + sAltParent + " is at the same time as sChild " + sChild);
               altEvent = nl.getNode(0).getStringValue("evenement.number");
            }
         }
      }
      return altEvent;
   }

   public static String[] altEventLink(Cloud cloud, String sParent, String sChild) {
      String altEvent = altEvent(cloud, sParent, sChild);
      String altLink = "";
      if (!altEvent.equals("-1")) {
         Node altNode = cloud.getNode(altEvent);
         Node parentNode = altNode;
         NodeList nl = altNode.getRelatedNodes("evenement", "partrel", "SOURCE");
         if (nl.size() != 0) {
            parentNode = nl.getNode(0);
         }
         altLink = "subscribelink.jsp?p=" + parentNode.getStringValue("number") + "&e=" + altEvent;
      }
      return new String[] {
         altEvent, altLink};
   }

   public static boolean exceedsMaxPrice(Cloud cloud, String thisParent, String paymentTypeID) {
      // This method checks if the event exceeds the maximum price for a paymentType
      // *** Payment types (payment_type)
      String sINGBon = "232657";
      // *** Participants categories (deelnemers_categorie)
      String sDeelnemerPlus = "90165";
      String sGezinNietLeden = "70550";
      String sGezinLeden = "70548";
      String sKinderenNietLeden = "67954";
      String sKinderenLeden = "67953";
      String sKinderen = "591";
      String sNietLeden = "589";
      String sLeden = "587";
      String sNietLedenMetRolstoel = "585";
      String sKinderenMetRolstoel = "583";
      String sLedenMetRolstoel = "581";
      // *** Event types (event_types)
      String sVaren = "32996";
      String sKinderEnGezinsActiviteit = "32995";
      String sWandelen = "32992";
      boolean exceedsMaxPrice = false;

      String activityTypeID = "";
      NodeList activityTypeList = cloud.getList(thisParent, "evenement,related,evenement_type", "evenement_type.number", null, null, null, null, false);
      if (activityTypeList.size() > 0) {
         activityTypeID = activityTypeList.getNode(0).getStringValue("evenement_type.number");
      }

      if (paymentTypeID != null
          && paymentTypeID.equals(sINGBon)) {
         if (activityTypeID.equals(sVaren) || activityTypeID.equals(sKinderEnGezinsActiviteit) || activityTypeID.equals(sWandelen)) {
            NodeList nl = cloud.getList(thisParent,
                                        "evenement,posrel,deelnemers_categorie", "posrel.pos,deelnemers_categorie.number", null, null, null, "DESTINATION", true);
            for (int n = 0; n < nl.size(); n++) {
               String catNumber = nl.getNode(n).getStringValue("deelnemers_categorie.number");
               boolean isLeden = catNumber.equals(sLeden) || catNumber.equals(sLedenMetRolstoel);
               boolean isNietLeden = catNumber.equals(sNietLeden) || catNumber.equals(sNietLedenMetRolstoel);
               boolean isKinderenTot12Jaar = catNumber.equals(sKinderen) || catNumber.equals(sKinderenMetRolstoel)
                  || catNumber.equals(sKinderenLeden) || catNumber.equals(sKinderenNietLeden);
               int catPrice = nl.getNode(n).getIntValue("posrel.pos");
               if (activityTypeID.equals(sWandelen)) {

                  if (isLeden) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 400);
                  }
                  else if (isNietLeden) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 700);
                  }
                  else if (isKinderenTot12Jaar) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 200);
                  }

               }
               else if (activityTypeID.equals(sVaren)) {

                  if (isLeden) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 800);
                  }
                  else if (isNietLeden) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 1200);
                  }
                  else if (isKinderenTot12Jaar) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 400);
                  }
               }
               else if (activityTypeID.equals(sKinderEnGezinsActiviteit)) {

                  if (catNumber.equals(sGezinLeden)) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 300);
                  }
                  else if (catNumber.equals(sGezinNietLeden)) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 500);
                  }
                  else if (catNumber.equals(sKinderenLeden)) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 300);
                  }
                  else if (catNumber.equals(sKinderenNietLeden)) {
                     exceedsMaxPrice = exceedsMaxPrice || (catPrice > 500);
                  }
               }
            }
         }
      }

      return exceedsMaxPrice;
   }

   public static boolean subscriptionClosed(Node parentEvent, Node childEvent) {
      boolean subscriptionClosed = true;
      
      // the subscription is always open if the option "geen sluitingstijd" has been set
      if (parentEvent.getIntValue("reageer") == 0) {
         return false;
      }
      
      // subscription is not used for multiple day events
      DoubleDateNode ddn = new DoubleDateNode(childEvent);
      Calendar cal = Calendar.getInstance();
      cal.setTime(ddn.getBegin());
      int beginDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
      int beginMonth = cal.get(Calendar.MONTH);
      int beginYear = cal.get(Calendar.YEAR);
      cal.setTime(ddn.getEnd());
      int endDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
      int endMonth = cal.get(Calendar.MONTH);
      int endYear = cal.get(Calendar.YEAR);
      if (!"".equals(parentEvent.getStringValue("dagomschrijving"))) { // for period subscriptions are not necessary
         subscriptionClosed = false;
      } else { // for date check on close date
         int timeBeforeStart = parentEvent.getIntValue("reageer") * 60 * 60;
         long closeDate = (childEvent.getLongValue("begindatum") - timeBeforeStart) * 1000;
         subscriptionClosed = ( (new Date()).getTime() > closeDate);
      }
      return subscriptionClosed;
   }

   public NodeList getSortedList(Cloud cloud, String sParentEvent) {
      // insert the parent into the list of childs
      NodeList nl = cloud.getList(sParentEvent, "evenement1,partrel,evenement", "evenement.number", null, "evenement.begindatum", "UP", "destination", true);
      if (nl.size() != 0) {
         DoubleDateNode ddnParent = new DoubleDateNode(cloud.getNode(sParentEvent));
         NodeList parentList = cloud.getList(sParentEvent, "evenement", "evenement.number", null, null, null, null, false);
         if (ddnParent.compareTo(new DoubleDateNode(cloud.getNode(nl.getNode(0).getStringValue("evenement.number")))) == -1) {
            // parent is before first child, add parent at begin
            nl.addAll(0, parentList);
         }
         else if (ddnParent.compareTo(new DoubleDateNode(cloud.getNode(nl.getNode(nl.size() - 1).getStringValue("evenement.number")))) == 1) {
            // parent is after last child, add parent at end
            nl.addAll(nl.size(), parentList);
         }
         else {
            // parent is before i+1, but after i, add parent at i
            int i = 0;
            DoubleDateNode ddn = new DoubleDateNode(cloud.getNode(nl.getNode(i).getStringValue("evenement.number")));
            DoubleDateNode ddn1 = new DoubleDateNode(cloud.getNode(nl.getNode(i + 1).getStringValue("evenement.number")));
            while (! (ddnParent.compareTo(ddn) != -1 && ddnParent.compareTo(ddn1) == -1)) {
               i++;
               ddn = ddn1;
               ddn1 = new DoubleDateNode(cloud.getNode(nl.getNode(i + 1).getStringValue("evenement.number")));
            }
            nl.addAll(i, parentList);
         }
      }
      else {
         nl = cloud.getList(sParentEvent, "evenement", "evenement.number", null, null, null, null, false);
      }
      return nl;
   }

   public static String getEventsConstraint(long lDateSearchFrom, long lDateSearchTill) {
      // in case of a date it should fall in the period [lDateSearchFrom,lDateSearchTill]
      // in case of a period it should overlap [lDateSearchFrom,lDateSearchTill]
      String sEventConstraint =
         " (( evenement.dagomschrijving LIKE '' AND evenement.begindatum > " + lDateSearchFrom + " AND evenement.einddatum < " + (lDateSearchTill + 24 * 60 * 60) + " ) "
         + " OR ( evenement.dagomschrijving NOT LIKE '' AND  evenement.einddatum > " + lDateSearchFrom + " AND evenement.begindatum < " + (lDateSearchTill + 24 * 60 * 60) + " )) "
         + " AND ( evenement.isspare='false') AND ( evenement.isoninternet='true' )";
      return sEventConstraint;
   }

   public static boolean isOnInternet(Node thisEvent, long lDateSearchFrom) {
      // at least one of the dates should be on internet
      boolean isOnInternet = thisEvent.getStringValue("isoninternet").equals("true")
         && thisEvent.getLongValue("verloopdatum") > lDateSearchFrom;
      if (thisEvent.getStringValue("soort").equals("parent")) {
         NodeList evenementList = thisEvent.getRelatedNodes("evenement", "partrel", "destination");
         for (int e = 0; e < evenementList.size(); e++) {
            Node childEvent = evenementList.getNode(e);
            isOnInternet = isOnInternet
               || (childEvent.getStringValue("isoninternet").equals("true") && childEvent.getLongValue("verloopdatum") > lDateSearchFrom);
         }
      }
      return isOnInternet;
   }

   public static String getBookableEventsConstraint(long lDateSearchFrom, long lDateSearchTill) {
      // only dates, which fall in the period [lDateSearchFrom,lDateSearchTill]
      String sEventConstraint =
         " evenement.dagomschrijving LIKE '' "
         + " AND evenement.begindatum > " + lDateSearchFrom
         + " AND evenement.einddatum < " + (lDateSearchTill + 24 * 60 * 60) + " ) "
         + " AND ( evenement.isspare='false') "
         + " AND ( evenement.iscanceled='false') "
         + " AND ( evenement.isoninternet='true' )";
      return sEventConstraint;
   }

   public static HashSet getEvents(Cloud cloud, long lDateSearchFrom, long lDateSearchTill) {
      // all parent events that contain a date in the period [lDateSearchFrom,lDateSearchTill]
      HashSet events = new HashSet();
      NodeList el = cloud.getList("", "evenement", "evenement.number", getEventsConstraint(lDateSearchFrom, lDateSearchTill), null, null, null, true);
      for (int e = 0; e < el.size(); e++) {
         String event_number = el.getNode(e).getStringValue("evenement.number");
         String parent_number = Evenement.findParentNumber(event_number);
         if (!events.contains(parent_number)) {
            events.add(parent_number);
         }
      }
      return events;
   }
   
   /**
    * Find parent for child, if there is a parent.
    *
    * @param sEvent String
    * @return String
    */
   public static String findParentNumber(String sEvent) {
      Cloud cloud = CloudFactory.getCloud();
      Node thisEvent = cloud.getNode(sEvent);
      String parentNumber = thisEvent.getStringValue("number");
      if (thisEvent.getStringValue("soort").equals("child")) {
         RelationList parentRelations = thisEvent.getRelations("partrel", "evenement");
         if (!parentRelations.isEmpty()) {
            Node parentEvent = parentRelations.getRelation(0).getSource();
            parentNumber = parentEvent.getStringValue("number");
         }
         else {
            log.debug("Could not find parent for child " + thisEvent.getStringValue("number") + ", setting parent to child");
         }
      }
      return parentNumber;
   }

   /**
    * Can the user book this event?
    *
    * @param nodeEvent Node
    * @param memberid String
    * @return boolean
    */
   public static boolean isAuthenticated(Node nodeEvent, String memberid) {
      NodeList nlPaymentTypes = nodeEvent.getRelatedNodes("deelnemers_categorie");
      boolean memberOnly = "Leden".equalsIgnoreCase(nlPaymentTypes.getNode(0).getStringValue("naam"));

      Cloud cloud = nodeEvent.getCloud();
      boolean isMember = false;
      if(memberid!=null) {
         Node nodeMember = cloud.getNode(memberid);
         isMember = "true".equals(nodeMember.getStringValue("lidnummer"));
      }

      return !memberOnly || (memberOnly && isMember);
   }
   
   public static boolean isAgenda(Cloud cloud, String pageID) {
     return pageID.equals(cloud.getNodeByAlias("agenda").getStringValue("number"));
   }
}
