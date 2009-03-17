package nl.natuurmonumenten.activiteiten;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import nl.leocms.evenementen.Evenement;
import nl.leocms.evenementen.forms.SubscribeAction;

import org.apache.log4j.Logger;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Relation;
import org.mmbase.bridge.RelationList;

public class ActiviteitenHelper {
    private static Logger logger = Logger.getLogger(ActiviteitenHelper.class);
    
    public static Set findParentEvents(Cloud cloud, Date start, Date eind, String[] eventTypeIds, String provincieId, String natuurgebiedenId) {
        // Deze code komt uit searchresults.jsp, omdat ik er geen touw aan vast kan knopen heb ik geprobeerd deze letterlijk over te zetten vanuit de jsp code
        // ik heb geen poging gedaan om het refactoren, *************** (gecensureerd)
        
        long lDateSearchFrom = start.getTime()/1000;
        long lDateSearchTill = eind.getTime()/1000;
        logger.debug("lDateSearchFrom: " + lDateSearchFrom);
        logger.debug("lDateSearchTill: " + lDateSearchTill);
        //If the startdate is after the enddate, there can not exist any event 
        if (lDateSearchFrom >= lDateSearchTill) {
           return Collections.EMPTY_SET;
        }
        String provincieConstraint = createProvincieConstraint(provincieId);
        HashSet parentEvents = getEvents(cloud,lDateSearchFrom,lDateSearchTill, provincieConstraint);
        logger.debug("parentEvents: " + parentEvents);
        if (parentEvents.isEmpty()) {
            return parentEvents;
        }
        removeEventsNotOnInternet(cloud, parentEvents);
        if (!isEmpty(eventTypeIds)) {
            removeEventsWithoutEventType(cloud, eventTypeIds, parentEvents);
        }
        if (!isEmpty(natuurgebiedenId)) {
            removeEventsWithoutNatuurgebied(cloud, natuurgebiedenId, parentEvents);
        }
        return parentEvents;
    }
    public static Set getChildEvents(Cloud cloud, String parentNumber) {
        HashSet childEvents = new HashSet();
        NodeList childList = cloud.getList(parentNumber, "evenement1,partrel,evenement", "evenement.number", "evenement.isoninternet='true' and evenement.soort='child'", null, null, "destination", true);
        for (NodeIterator iter = childList.nodeIterator(); iter.hasNext();) {
            Node event = iter.nextNode();
            childEvents.add(event.getStringValue("evenement.number"));
        }
        return childEvents;
    }
    public static Map getChildEvents(Cloud cloud, long lDateSearchFrom, long lDateSearchTill, Set parentEvents) {
        logger.debug("parentEvents: " + parentEvents);
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (Iterator iter = parentEvents.iterator(); iter.hasNext();) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(iter.next());
        }
        String parentEventsString = sb.toString();
        logger.debug("parentEvents: " + parentEventsString);
        String childConstraints = nl.leocms.evenementen.Evenement.getEventsConstraint(lDateSearchFrom,lDateSearchTill);
        logger.debug("childConstraints voor: <" + childConstraints  + ">");
        if (!parentEvents.equals("")) {
            childConstraints = "";
        }
        logger.debug("childConstraints na: <" + childConstraints + ">");

        Map events = new TreeMap();
        
        if (!parentEvents.isEmpty()) {
            NodeList childList = cloud.getList(parentEventsString, "evenement1,partrel,evenement", "evenement.number,evenement.begindatum", childConstraints, null, null, "destination", true);
            for (NodeIterator iter = childList.nodeIterator(); iter.hasNext();) {
                Node event = iter.nextNode();
                logger.debug("found in first loop: " + event.getStringValue("evenement.number") + "[" + event.getLongValue("evenement.begindatum") + "]");
                // ik heb geen idee wat dit doet
                if(!events.containsValue(event.getStringValue("evenement.number")) ) {
                    long eventBeginDate = event.getLongValue("evenement.begindatum");
                    while(events.containsKey(new Long(eventBeginDate))) {
                       eventBeginDate++;
                    }
                    logger.debug("putting event: " + (new Long(eventBeginDate)) + ":" + event.getStringValue("evenement.number"));
                    events.put(new Long(eventBeginDate),event.getStringValue("evenement.number"));
                 }
            }
            NodeList anotherChildList = cloud.getList(parentEventsString, "evenement", "evenement.number,evenement.begindatum", childConstraints, null, null, "destination", true);
            for (NodeIterator iter = anotherChildList.nodeIterator(); iter.hasNext();) {
                Node event = iter.nextNode();
                logger.debug("found in second loop: " + event.getStringValue("evenement.number") + "[" + event.getLongValue("evenement.begindatum") + "]");
                // ik heb nog steeds geen idee wat dit doet
                if(!events.containsValue(event.getStringValue("evenement.number")) ) {
                    long eventBeginDate = event.getLongValue("evenement.begindatum");
                    while(events.containsKey(new Long(eventBeginDate))) {
                       eventBeginDate++;
                    }
                    logger.debug("putting event: " + (new Long(eventBeginDate)) + ":" + event.getStringValue("evenement.number"));
                    events.put(new Long(eventBeginDate),event.getStringValue("evenement.number"));
                 }
            }
        }
        logger.debug("events: " + events);
        return events;
    }

    public static Node getFoto(Cloud cloud, String parentNumber) {
        Node image = null;
        // jsp code gebruikt "evenement,related,evenement_type,posrel,images" als path maar dan ik niks vinden
        NodeList list = cloud.getList(parentNumber, "evenement,posrel,images", "images.number", null, null, null, null, true);
        if (list.size() > 0) {
            Node node = list.getNode(0);
            if (node != null) {
                image = cloud.getNode(node.getStringValue("images.number"));
            }
        }
        return image;
    }
    
    public static int getAantalBeschikbarePlaatsen(Node parentEvent, Node childEvent) {
        return parentEvent.getIntValue("max_aantal_deelnemers") - childEvent.getIntValue("cur_aantal_deelnemers");
    }
    
    /* Overgenomen uit SubscribeForm. Aangepast aan de webservice omgeving, o.a. het versturen van emails verwijderd.
     */  
    public static Node createParticipant(Cloud cloud, Node thisEvent, Node thisSubscription, String thisCategory, int thisNumber, Subscription subscription) {

        Node thisParticipant = null;
        thisParticipant = cloud.getNodeManager("deelnemers").createNode();

        thisParticipant.setStringValue("initials", subscription.getVoorletter());
        thisParticipant.setStringValue("firstname", subscription.getVoornaam());
        thisParticipant.setStringValue("suffix", subscription.getTussenvoegsel());
        thisParticipant.setStringValue("lastname", subscription.getAchternaam());
        thisParticipant.setStringValue("email", subscription.getEmail());
        thisParticipant.setStringValue("privatephone", subscription.getTelefoon());
        thisParticipant.setStringValue("straatnaam", subscription.getStraat());
        thisParticipant.setStringValue("huisnummer", subscription.getHuisnummer());
        thisParticipant.setStringValue("plaatsnaam", subscription.getPlaats());
        thisParticipant.setStringValue("land", subscription.getLand());
        thisParticipant.setStringValue("postcode", subscription.getPostcode());
        thisParticipant.setStringValue("lidnummer", subscription.getLidnummer());
        thisParticipant.commit();

        Relation thisRel = null;
        if(thisRel==null) {
           thisRel = thisSubscription.createRelation(thisParticipant,cloud.getRelationManager("posrel"));
        }

        // set the price for this participant
        int costs = 9999;
        String sParent = Evenement.findParentNumber(thisEvent.getStringValue("number"));
        if(!Evenement.isGroupBooking(cloud,thisParticipant.getStringValue("number"))) {
           // this is a regular excursion
           NodeList dcl = cloud.getList( sParent
                                         ,"evenement,posrel,deelnemers_categorie"
                                         ,"posrel.pos"
                                         ,"deelnemers_categorie.number='"+ thisCategory+ "'",null,null,null,false);
           if(dcl.size()>0) {
              costs = dcl.getNode(0).getIntValue("posrel.pos");
              logger.debug("costs1: " + costs);
              // if these are members of a group_excursion, but not the main group excursion participant: set costs to zero
              if(Evenement.isGroupExcursion(cloud,sParent)
                 && (costs==SubscribeAction.GROUP_EXCURSION_COSTS || costs==SubscribeAction.DEFAULT_COSTS )) {
                       costs = 0;
              }
              logger.debug("costs2: " + costs);
           }
           costs = costs * thisNumber;
           logger.debug("costs3: " + costs);
        } else {
           // this is the subscription for group excursion
           costs =  Evenement.getGroupExcursionCosts(cloud, sParent, thisSubscription.getStringValue("number"));
           logger.debug("costs4: " + costs);
        }
        logger.debug("costs5: " + costs);
        thisRel.setIntValue("pos",costs);
        thisRel.commit();

        // *** update deelnemers,related,deelnemers_categorie
        if(!Evenement.isGroupBooking(cloud,thisParticipant.getStringValue("number"))) {

           thisParticipant.setStringValue("bron", String.valueOf(thisNumber));
           thisParticipant.commit();
           RelationList relations = thisParticipant.getRelations("related","deelnemers_categorie");
           for(int r=0; r<relations.size(); r++) { relations.getRelation(r).delete(true); }
           if(!thisCategory.equals("-1")) {

             Node thisCategoryNode = cloud.getNode(thisCategory);
             thisParticipant.createRelation(thisCategoryNode,cloud.getRelationManager("related")).commit();

           }
        }

        thisEvent.commit(); // *** save to update cur_aantal_deelnemers

        return thisParticipant;
     }


    private static void removeEventsNotOnInternet(Cloud cloud, HashSet parentEvents) {
        for (Iterator iter = parentEvents.iterator(); iter.hasNext();) {
            String parentNumber = (String) iter.next();
            String constraint = createEventTypeIsOnInternetConstraint();
            NodeList list = cloud.getList(parentNumber, "evenement,related,evenement_type", "evenement.number", constraint, null, null, null, true);
            if (list.isEmpty()) {
                iter.remove();
            }
        }
    }

    private static void removeEventsWithoutNatuurgebied(Cloud cloud, String natuurgebiedenId, HashSet parentEvents) {
        logger.debug("natuurgebiedenId: " + natuurgebiedenId);
        for (Iterator iter = parentEvents.iterator(); iter.hasNext();) {
            String parentNumber = (String) iter.next();
            String constraint = createNatuurgebiedConstraint(parentNumber);
            NodeList list = cloud.getList(natuurgebiedenId, "natuurgebieden,related,evenement", "evenement.number", constraint, null, null, null, true);
            if (list.isEmpty()) {
                iter.remove();
            }
        }
    }

    private static void removeEventsWithoutEventType(Cloud cloud, String[] eventTypeIds, HashSet parentEvents) {
        for (Iterator iter = parentEvents.iterator(); iter.hasNext();) {
            String parentNumber = (String) iter.next();
            String constraint = createEventTypeConstraint(eventTypeIds);
            if (!isEmpty(constraint)) {
                NodeList list = cloud.getList(parentNumber, "evenement,related,evenement_type", "evenement.number", constraint, null, null, null, true);
                if (list.isEmpty()) {
                    iter.remove();
                }
            }
        }
    }

    private static String createProvincieConstraint(String provincieId) {
        String provincieConstraint = null;
        if (!isEmpty(provincieId)) {
            provincieConstraint = " AND lokatie like '%," + provincieId + ",%' ";
        }
        return provincieConstraint;
    }

    private static String createNatuurgebiedConstraint(String parentNumber) {
        StringBuffer sb = new StringBuffer();
        sb.append("evenement.number='");
        sb.append(parentNumber);
        sb.append("'");
        logger.debug("natuurgebiedConstraint: " + sb);
        return sb.toString();
    }

    private static String createEventTypeIsOnInternetConstraint() {
        StringBuffer sb = new StringBuffer();
        sb.append("evenement_type.isoninternet='1'");
        logger.debug("eventTypeIsOnInternetConstraint: " + sb);
        return sb.toString();
    }

    private static String createEventTypeConstraint(String[] eventTypeIds) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (int i = 0; i < eventTypeIds.length; i++) {
            if (first) {
                sb.append("evenement_type.isoninternet = 1 AND (");
                first = false;
            } else {
                sb.append(" OR ");
            }
            sb.append("evenement_type.number='");
            sb.append(eventTypeIds[i]);
            sb.append("'");
        }
        if (!first) {
            sb.append(")");
        }
        logger.debug("eventTypeConstraint: " + sb);
        return sb.toString();
    }

    
    public static HashSet getEvents(Cloud cloud, long lDateSearchFrom, long lDateSearchTill, String provincieConstraint) {
        // all parent events that contain a date in the period
        // [lDateSearchFrom,lDateSearchTill]
        HashSet events = new HashSet();
        String constraint = getEventsConstraint(lDateSearchFrom, lDateSearchTill);
        if (provincieConstraint != null) {
            constraint += provincieConstraint;
        }
        NodeList el = cloud.getList("", "evenement", "evenement.number", constraint, null, null, null, true);
        for (int e = 0; e < el.size(); e++) {
            String eventNumber = el.getNode(e).getStringValue("evenement.number");
            if (!events.contains(eventNumber)) {
                if (eventNumber != null && eventNumber.trim().length() > 0) {
                    events.add(eventNumber);
                }
            }
        }
        return events;
    }

    private static String getEventsConstraint(long lDateSearchFrom, long lDateSearchTill) {
        String sEventConstraint =
           " (" +
           "   ( " +
           "       evenement.begindatum >= " + lDateSearchFrom +
           "       AND evenement.begindatum <= " + lDateSearchTill + 
           "   ) " +
           "   OR " + 
           "   (" +
           "       evenement.einddatum >= " + lDateSearchFrom +
           "       AND evenement.einddatum <= " + lDateSearchTill + 
           "   ) " +
           " ) " +
           " AND evenement.isspare='false' AND evenement.isoninternet='true' " +
           " AND soort='parent'";
        return sEventConstraint;
     }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    private static boolean isEmpty(String[] str) {
        if (str == null || str.length == 0) {
            return true;
        }
        for (int i = 0; i < str.length; i++) {
            if (!isEmpty(str[i])) {
                return false;
            }
        }
        return true;
    }
    

}
