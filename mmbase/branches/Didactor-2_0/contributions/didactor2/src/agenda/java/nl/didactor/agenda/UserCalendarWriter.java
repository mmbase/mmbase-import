package nl.didactor.agenda;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import nl.eo.calendar.*;

/**
 * This calendar writer will return all personal and class related
 * calendaritems for the given username.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class UserCalendarWriter implements CalendarWriter {
    private Logger log = Logging.getLoggerInstance(UserCalendarWriter.class.getName());

    String username;
    Cloud cloud;
    int currentclass = -1;
        
    public UserCalendarWriter(Cloud cloud, String username) {
        this.username = username;
        this.cloud = cloud;
    }

    public UserCalendarWriter(Cloud cloud, String username, int klas) {
        this.username = username;
        this.cloud = cloud;
        this.currentclass = klas;
    }
    
    public void write(CalendarView calendarView) {
        long startTime = calendarView.getStartTime().getTime().getTime() / 1000;
        long stopTime = calendarView.getStopTime().getTime().getTime() / 1000;
        writePersonal(calendarView, startTime, stopTime);
        writeClass(calendarView, startTime, stopTime);
        writeInvitations(calendarView, startTime, stopTime);
    }

    private void writeInvitations(CalendarView calendarView, long startTime, long stopTime) {
        NodeList nodes = cloud.getList("", //startnode 
                           "agendas,eventrel,items,invitationrel,people",  //path
                           "items.number,items.title,items.body,items.repeatuntil,items.repeatinterval,eventrel.start,eventrel.stop",   //fields
                            "((NOT ((eventrel.start < " + startTime
                             + " AND eventrel.stop < " + startTime
                             + ") OR (eventrel.start > " + stopTime
                             + " AND eventrel.stop > " + stopTime + ")) "
                             + ") OR (" 
                             + "eventrel.start < " + stopTime 
                             + " AND items.repeatuntil > " + startTime
                             + ")) AND ("
                             + " people.username = '" + username + "')",
                           "eventrel.start", //sortby
                           null, null, false);

        for (NodeIterator ni = nodes.nodeIterator(); ni.hasNext();) {
            addNode(ni.nextNode(), calendarView, startTime, stopTime, 0);
        }
    }


    private void writePersonal(CalendarView calendarView, long startTime, long stopTime) {
        NodeList nodes = cloud.getList("", //startnode 
                           "people,agendas,eventrel,items",  //path
                           "items.number,items.title,items.body,items.repeatuntil,items.repeatinterval,eventrel.start,eventrel.stop",   //fields
                            "((NOT ((eventrel.start < " + startTime
                             + " AND eventrel.stop < " + startTime
                             + ") OR (eventrel.start > " + stopTime
                             + " AND eventrel.stop > " + stopTime + ")) "
                             + ") OR (" 
                             + "eventrel.start < " + stopTime 
                             + " AND items.repeatuntil > " + startTime
                             + ")) AND ("
                             + " people.username = '" + username + "')",
                           "eventrel.start", //sortby
                           null, null, false);

        for (NodeIterator ni = nodes.nodeIterator(); ni.hasNext();) {
            addNode(ni.nextNode(), calendarView, startTime, stopTime, 0);
        }
    }

    // Find all items related to classes this user is member of.
    //   -> if starttime is inbetween the given 'startTime' and 'stopTime'
    //   -> or the starttime is before 'stopTime' AND repeatuntil is after 'startTime'
    private void writeClass(CalendarView calendarView, long startTime, long stopTime) {
        NodeList nodes = cloud.getList("", //startnode
                            "people,classes,agendas,eventrel,items", //path
                            "classes.number,items.number,items.title,items.body,items.repeatinterval,items.repeatuntil,eventrel.start,eventrel.stop", //fields
                            "((NOT ((eventrel.start < " + startTime
                             + " AND eventrel.stop < " + startTime
                             + ") OR (eventrel.start > " + stopTime
                             + " AND eventrel.stop > " + stopTime + ")) "
                             + ") OR (" 
                             + "eventrel.start < " + stopTime 
                             + " AND items.repeatuntil > " + startTime
                             + ")) AND ("
                             + " people.username = '" + username + "')",
                             "eventrel.start", //sortby
                             null, null, false);
        for (NodeIterator ni = nodes.nodeIterator(); ni.hasNext();) {
            addNode(ni.nextNode(), calendarView, startTime, stopTime, 1);
        }
    }

    private void addNode(Node node, CalendarView calendarView, long startTime, long stopTime, int type) {
        if (node.getIntValue("items.repeatinterval") <= 0) {
            Calendar start = Calendar.getInstance();
            Calendar stop = Calendar.getInstance();
            start.setTime(new Date(node.getLongValue("eventrel.start") * 1000));
            stop.setTime(new Date(node.getLongValue("eventrel.stop") * 1000));
            if (stop.before(start)) {
                throw new StopTimeBeforeStartTimeException("Stop time before start time (eventrel node " + node.getStringValue("eventrel.number") + ").");
            }
            CalendarItem calendarItem = new CalendarItem(start, stop, CalendarItem.DAYITEM);
            calendarItem.setTeaser(node.getStringValue("items.title"));
            calendarItem.setExtraField("body", node.getStringValue("items.body"));
            calendarItem.setExtraField("number", "" + node.getStringValue("items.number"));
            if (type == 1) {
                if (node.getIntValue("classes.number") == currentclass) {
                    calendarItem.setExtraField("type", "1");
                } else {
                    calendarItem.setExtraField("type", "2");
                }
            } else {
                calendarItem.setExtraField("type", "" + type);
            }
            calendarView.add(calendarItem);
        } else {
            long start = node.getLongValue("eventrel.start");
            long stop = node.getLongValue("eventrel.stop");
            long end = node.getLongValue("items.repeatuntil");
            long step = node.getIntValue("items.repeatinterval") * 24 * 60 * 60;

            for (int i = 0; (i + start) < end && (i + start) < stopTime; i+= step) {
                if ((i + start) > startTime) {
                    Calendar startCal = Calendar.getInstance();
                    Calendar stopCal = Calendar.getInstance();
                    startCal.setTime(new Date(1000 * (i + start)));
                    stopCal.setTime(new Date(1000 * (i + stop)));
                    if (stopCal.before(startCal)) {
                        throw new StopTimeBeforeStartTimeException("Stop time before start time (eventrel node " + node.getStringValue("eventrel.number") + ").");
                    }
                    CalendarItem calendarItem = new CalendarItem(startCal, stopCal, CalendarItem.DAYITEM);
                    calendarItem.setTeaser(node.getStringValue("items.title"));
                    calendarItem.setExtraField("body", node.getStringValue("items.body"));
                    calendarItem.setExtraField("number", "" + node.getStringValue("items.number"));
                    if (type == 1) {
                        if (node.getIntValue("classes.number") == currentclass) {
                            calendarItem.setExtraField("type", "1");
                        } else {
                            calendarItem.setExtraField("type", "2");
                        }
                    } else {
                        calendarItem.setExtraField("type", "" + type);
                    }
                    calendarView.add(calendarItem);
                }
            }
        }        
    }
}
