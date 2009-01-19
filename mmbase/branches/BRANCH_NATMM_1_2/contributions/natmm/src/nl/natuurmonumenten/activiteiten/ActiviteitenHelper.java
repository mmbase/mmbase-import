package nl.natuurmonumenten.activiteiten;

import java.util.Calendar;
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
    
    public static Map findEvents(Cloud cloud, Date start, Date eind, String[] eventTypeIds, String provincieId, String natuurgebiedenId) {
        // Deze code komt uit searchresults.jsp, omdat ik er geen touw aan vast kan knopen heb ik geprobeerd deze letterlijk over te zetten vanuit de jsp code
        // ik heb geen poging gedaan om het refactoren, *************** (gecensureerd)
        
        long lDateSearchFrom = start.getTime()/1000;
        long lDateSearchTill = eind.getTime()/1000;
        logger.debug("lDateSearchFrom: " + lDateSearchFrom);
        logger.debug("lDateSearchTill: " + lDateSearchTill);
        
        // geen idee, maar ze rekken de tijd nog wat op
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long lDateSearchFromDefault = (cal.getTime().getTime()/1000);
        cal.add(Calendar.YEAR,1); // cache events for one year from now
        long lDateSearchTillDefault = (cal.getTime().getTime()/1000);

        
        StringBuffer defaultParentEvents = new StringBuffer();
        if (lDateSearchTill <= lDateSearchTillDefault) {
            HashSet defaultEvents = nl.leocms.evenementen.Evenement.getEvents(cloud,lDateSearchFromDefault,lDateSearchTillDefault);
            logger.debug("defaultEvents: " + defaultEvents);
            boolean first = true;
            for (Iterator iter = defaultEvents.iterator(); iter.hasNext();) {
                String eventnumber = (String) iter.next();
                if (first) {
                    first = false;
                } else {
                    defaultParentEvents.append(",");
                }
                defaultParentEvents.append(eventnumber);
            }
        }
        logger.debug("defaultParentEvents: " + defaultParentEvents);

        StringBuffer eventTypeConstraint = new StringBuffer();
        if (eventTypeIds != null) {
            eventTypeConstraint.append("(");
            boolean first = true;
            for (int i = 0; i < eventTypeIds.length; i++) {
                if (first) {
                    first = false;
                } else {
                    eventTypeConstraint.append(" OR ");
                }
                eventTypeConstraint.append("evenement_type.number='");
                eventTypeConstraint.append(eventTypeIds[i]);
                eventTypeConstraint.append("'");
            }
            eventTypeConstraint.append(")");
        }
        logger.debug("eventTypeConstraint: " + eventTypeConstraint);
        
        String provincieConstraint = null;
        if (provincieId != null) {
            provincieConstraint = "lokatie like '%," + provincieId + ",%'";
        }
        logger.debug("provincieConstraint: " + provincieConstraint);
        Set parentEvents = new HashSet();
        NodeList list = cloud.getList(defaultParentEvents.toString(), "evenement,related,evenement_type", "evenement.number", provincieConstraint, null, null, null, true);
        for (NodeIterator iter = list.nodeIterator(); iter.hasNext();) {
            Node node = iter.nextNode();
            String parentNumber = node.getStringValue("evenement.number");
            boolean parentBelongsToActivityType = true;
            boolean parentBelongsToNatuurgebied = true;
            
            // check evenementType
            if (eventTypeIds != null) {
                // TODO how to find 1 node if it exists, not the whole list?
                NodeList list2 = cloud.getList(parentNumber, "evenement,related,evenement_type", "evenement.number", "evenement_type.isoninternet='1'", null, null, null, true);
                if (!list2.isEmpty()) {
                    parentBelongsToActivityType = false;
                }
                NodeList list3 = cloud.getList(parentNumber, "evenement,related,evenement_type", "evenement.number", eventTypeConstraint.toString(), null, null, null, true);
                if (!list3.isEmpty()) {
                    parentBelongsToActivityType = true;
                }
            }

            logger.debug("natuurgebiedenId: " + natuurgebiedenId);
            if (natuurgebiedenId != null) {
                parentBelongsToNatuurgebied = false;
                NodeList list2 = cloud.getList(natuurgebiedenId, "natuurgebieden,related,evenement", "evenement.number", "evenement.number='" + parentNumber +"'", null, null, null, true);
                if (!list2.isEmpty()) {
                    parentBelongsToNatuurgebied = true;
                }
            }
            logger.debug("parentBelongsToNatuurgebied: " + parentBelongsToNatuurgebied);

            if (parentBelongsToActivityType && parentBelongsToNatuurgebied) {
                if (parentNumber != null && parentNumber.trim().length() > 0) {
                    parentEvents.add(parentNumber);
                }
            }
        }
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

}
