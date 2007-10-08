/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
sQuery
*/
package org.mmbase.notifications;
import org.mmbase.bridge.*;
import java.util.concurrent.*;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A Notifyable is a wrapper arround an MMBase node of the type 'Notifyable'.
 * @author Michiel Meeuwissen
 * @version $Id: Notifyable.java,v 1.2 2007-10-08 16:55:17 michiel Exp $
 **/
public class Notifyable implements Delayed {

    private static final Logger log = Logging.getLoggerInstance(Notifyable.class);

    protected static Map<String, Notification> cache = new HashMap<String, Notification>();

    protected final Node node;
    protected final Date date;
    protected final Date notificationTime;

    public Notifyable(Node n, Date d, Date nt) {
        node = n;
        date = d;
        notificationTime = nt;
    }
    public long getDelay(TimeUnit u) {
        Date now = new Date();
        return u.convert(notificationTime.getTime() - now.getTime(), TimeUnit.MILLISECONDS);

    }
    public int compareTo(Delayed o) {
        return (int) (o.getDelay(TimeUnit.MILLISECONDS) - getDelay(TimeUnit.MILLISECONDS));
    }

    public void send() {
        log.service("Notifying " + getNode());
        RelationList rl = getNode().getRelations("notify", "object");
        RelationIterator ri = rl.relationIterator();
        while (ri.hasNext()) {
            Relation rel = ri.nextRelation();
            String className = rel.getStringValue("type");
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
                log.service("Using " + not);
                not.send(rel.getSource(), getNode());
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

    public boolean equals(Object o) {
        if (o instanceof Notifyable) {
            Notifyable n = (Notifyable) o;
            return n.getNode().getNumber() == getNode().getNumber() && n.getDate().equals(getDate());
        } else {
            return false;
        }
    }

    public String toString() {
        return "Event " + node.getFunctionValue("gui", null) + " on " + date + " (notify at " + notificationTime + ", due in " +
            (getDelay(TimeUnit.SECONDS) / 60) + " minutes)";
    }

}
