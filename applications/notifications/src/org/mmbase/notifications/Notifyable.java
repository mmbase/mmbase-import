/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import java.util.concurrent.*;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Notifyable is a wrapper arround an MMBase node of the type 'Notifyable'.
 * @author Michiel Meeuwissen
 * @version $Id: Notifyable.java,v 1.8 2007-12-10 18:15:05 michiel Exp $
 **/
public class Notifyable implements Delayed {

    private static final Logger log = Logging.getLoggerInstance(Notifyable.class);

    protected static Map<String, Notification> cache = new HashMap<String, Notification>();

    protected final Node node;
    protected final Date date;
    protected final int prevOffset;
    protected final int offset;
    protected final boolean offsetNull;

    public static void addNotifyables(Queue queue, Node notifyable, Date date, Date after) {
        SortedSet<Integer> sortedOffsets = new TreeSet<Integer>();
        for (String offset : Collections.list(ResourceBundle.getBundle("org.mmbase.notifications.resources.offset").getKeys())) {
            sortedOffsets.add(Integer.parseInt(offset));
        }

        int prevOffset = Integer.MIN_VALUE;
        for (int offset : sortedOffsets) {
            Calendar help = Calendar.getInstance();
            help.setTime(date);
            help.add(Calendar.SECOND, offset);
            Date occurDate = help.getTime();
            if (occurDate.after(after)) {
                Notifyable not = new Notifyable(notifyable, date, prevOffset, offset);
                log.service("Queuing " + not);
                queue.add(not);
            }
            prevOffset = offset;
        }

    }


    public Notifyable(Node n, Date d, int po, int o) {
        node = n;
        date = d;
        prevOffset = po;
        offset = o;
        offsetNull = n.getIntValue("offset") == o;
    }

    public long getDelay(TimeUnit u) {
        Date now = new Date();
        long result =  u.convert(date.getTime() - now.getTime() + (1000L * offset) - 5000, TimeUnit.MILLISECONDS);
        return result;
    }

    public int compareTo(Delayed o) {
        return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    public NodeList getNotifications() {
        try {
            Cloud cloud = getNode().getCloud();

            NodeQuery query = Queries.createRelationNodesQuery(getNode(), cloud.getNodeManager("object"), "notify", null);
            Queries.addConstraint(query, Queries.createConstraint(query, "notify.status", FieldValueConstraint.EQUAL, 1));
            Constraint cons = Queries.createConstraint(query, "notify.offset", Queries.OPERATOR_BETWEEN,
                                                       prevOffset + 1, offset, false);
            if (offsetNull) {
                Constraint nul = query.createConstraint(query.createStepField("notify.offset"));
                cons = query.createConstraint(cons, CompositeConstraint.LOGICAL_OR , null);
            }

            Queries.addConstraint(query, cons);
            log.debug("for offset " + offset + ":"  + query.toSql());
            NodeList rl = query.getNodeManager().toRelationManager().getList(query);
            return rl;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void send() {
        if (log.isServiceEnabled()) {
            log.service("Notifying " + getNode() + " at offset " + offset);
        }

        NodeIterator ri = getNotifications().nodeIterator();
        while (ri.hasNext()) {
            Relation rel = ri.nextNode().toRelation();
            String className = rel.getStringValue("type");
            int offset = rel.getIntValue("offset");
            log.service("using relation " + rel + " class " + className );

            if (! "".equals(className)) {
                Notification not = cache.get(className);
                if (not == null) {
                    try {
                        not = ((Class<Notification>) Class.forName(className)).newInstance();
                        cache.put(className, not);
                    } catch (ClassNotFoundException cnfe) {
                        log.error(cnfe);
                        continue;
                    } catch (InstantiationException ie) {
                        log.error(ie);
                        continue;
                    } catch (IllegalAccessException iae) {
                        log.error(iae);
                        continue;
                    }
                }
                Node recipient = rel.getSource();
                Date requestedDate = new Date(date.getTime() + rel.getIntValue("offset"));
                log.service("Using " + not);
                not.send(rel, requestedDate);
            }
        }

    }

    /**
     * returs node of type notifyable
     */
    public Node getNode () {
        return node;
    }
    public Date getDate() {
        return date;
    }
    public Date getNotificationDate() {
        return new Date(date.getTime() + offset * 1000);
    }

    public boolean equals(Object o) {
        if (o instanceof Notifyable) {
            Notifyable n = (Notifyable) o;
            return n.getNode().getNumber() == getNode().getNumber() && n.getDate().equals(getDate());
        } else {
            return false;
        }
    }
    public String getDue() {
        return (getDelay(TimeUnit.SECONDS) / 60) + " minutes";
    }

    public String toString() {
        return "Event " + node.getFunctionValue("gui", null) + " on " + date + " (notify at " + getNotificationDate() + ", due in " + getDue();
    }

}
