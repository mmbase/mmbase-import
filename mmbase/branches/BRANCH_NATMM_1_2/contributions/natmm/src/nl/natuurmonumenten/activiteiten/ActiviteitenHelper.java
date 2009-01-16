package nl.natuurmonumenten.activiteiten;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;

public class ActiviteitenHelper {
    public static Map findEvents(Cloud cloud, Date start, Date eind, String[] eventTypeIds, String provincieId, String natuurgebiedenId) {
        // Deze code komt uit searchresults.jsp, omdat ik er geen touw aan vast kan knopen heb ik geprobeerd deze letterlijk over te zetten vanuit de jsp code
        // ik heb geen poging gedaan om het refactoren, *************** (gecensureerd)
        
        long lDateSearchFrom = start.getTime()/1000;
        long lDateSearchTill = eind.getTime()/1000;
        System.out.println("lDateSearchFrom: " + lDateSearchFrom);
        System.out.println("lDateSearchTill: " + lDateSearchTill);
        
        // geen idee, maar ze rekken de tijd nog wat op
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long lDateSearchFromDefault = (cal.getTime().getTime()/1000);
        cal.add(Calendar.YEAR,1); // cache events for one year from now
        long lDateSearchTillDefault = (cal.getTime().getTime()/1000);

        
        StringBuffer defaultParentEvents = new StringBuffer();
        if (lDateSearchTill <= lDateSearchTillDefault) {
            HashSet defaultEvents = nl.leocms.evenementen.Evenement.getEvents(cloud,lDateSearchFromDefault,lDateSearchTillDefault);
            System.out.println("defaultEvents: " + defaultEvents);
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
        System.out.println("defaultParentEvents: " + defaultParentEvents);

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
        System.out.println("eventTypeConstraint: " + eventTypeConstraint);
        
        String provincieConstraint = null;
        if (provincieId != null) {
            provincieConstraint = "lokatie like '%," + provincieId + ",%'";
        }
        System.out.println("provincieConstraint: " + provincieConstraint);
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

            System.out.println("natuurgebiedenId: " + natuurgebiedenId);
            if (natuurgebiedenId != null) {
                parentBelongsToNatuurgebied = false;
                NodeList list2 = cloud.getList(natuurgebiedenId, "natuurgebieden,related,evenement", "evenement.number", "evenement.number='" + parentNumber +"'", null, null, null, true);
                if (!list2.isEmpty()) {
                    parentBelongsToNatuurgebied = true;
                }
            }
            System.out.println("parentBelongsToNatuurgebied: " + parentBelongsToNatuurgebied);

            if (parentBelongsToActivityType && parentBelongsToNatuurgebied) {
                if (parentNumber != null && parentNumber.trim().length() > 0) {
                    parentEvents.add(parentNumber);
                }
            }
        }
        System.out.println("parentEvents: " + parentEvents);
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
        System.out.println("parentEvents: " + parentEventsString);
        String childConstraints = nl.leocms.evenementen.Evenement.getEventsConstraint(lDateSearchFrom,lDateSearchTill);
        System.out.println("childConstraints voor: <" + childConstraints  + ">");
        if (!parentEvents.equals("")) {
            childConstraints = "";
        }
        System.out.println("childConstraints na: <" + childConstraints + ">");

        Map events = new TreeMap();
        if (!parentEvents.isEmpty()) {
            NodeList childList = cloud.getList(parentEventsString, "evenement1,partrel,evenement", "evenement.number,evenement.begindatum", childConstraints, null, null, "destination", true);
            for (NodeIterator iter = childList.nodeIterator(); iter.hasNext();) {
                Node event = iter.nextNode();
                System.out.println("found in first loop: " + event.getStringValue("evenement.number") + "[" + event.getLongValue("evenement.begindatum") + "]");
                // ik heb geen idee wat dit doet
                if(!events.containsValue(event.getStringValue("evenement.number")) ) {
                    long eventBeginDate = event.getLongValue("evenement.begindatum");
                    while(events.containsKey(new Long(eventBeginDate))) {
                       eventBeginDate++;
                    }
                    System.out.println("putting event: " + (new Long(eventBeginDate)) + ":" + event.getStringValue("evenement.number"));
                    events.put(new Long(eventBeginDate),event.getStringValue("evenement.number"));
                 }
            }
            NodeList anotherChildList = cloud.getList(parentEventsString, "evenement", "evenement.number,evenement.begindatum", childConstraints, null, null, "destination", true);
            for (NodeIterator iter = anotherChildList.nodeIterator(); iter.hasNext();) {
                Node event = iter.nextNode();
                System.out.println("found in second loop: " + event.getStringValue("evenement.number") + "[" + event.getLongValue("evenement.begindatum") + "]");
                // ik heb nog steeds geen idee wat dit doet
                if(!events.containsValue(event.getStringValue("evenement.number")) ) {
                    long eventBeginDate = event.getLongValue("evenement.begindatum");
                    while(events.containsKey(new Long(eventBeginDate))) {
                       eventBeginDate++;
                    }
                    System.out.println("putting event: " + (new Long(eventBeginDate)) + ":" + event.getStringValue("evenement.number"));
                    events.put(new Long(eventBeginDate),event.getStringValue("evenement.number"));
                 }
            }
        }
        System.out.println("events: " + events);
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
}
