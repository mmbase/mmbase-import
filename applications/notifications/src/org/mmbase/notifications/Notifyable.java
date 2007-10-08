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

/**
 * A Notifyable is a wrapper arround an MMBase node of the type 'No
 * @author Michiel Meeuwissen
 * @version $Id: Notifyable.java,v 1.1 2007-10-08 10:00:54 michiel Exp $
 **/
public class Notifyable implements Delayed {

    protected final Node node;
    public Notifyable(Node n) {
        node = n;
    }
    public long getDelay(TimeUnit u) {
        Date now = new Date();
        Date notificationTime = node.getDateValue("date");
        return u.convert(notificationTime.getTime() - now.getTime(), TimeUnit.MILLISECONDS);

    }
    public int compareTo(Delayed o) {
        return (int) (o.getDelay(TimeUnit.MILLISECONDS) - getDelay(TimeUnit.MILLISECONDS));
    }

    public void send() {
        //...
    }

    public Node getNode () {
        return node;
    }
    public boolean equals(Object o) {
        if (o instanceof Notifyable) {
            Notifyable n = (Notifyable) o;
            return n.getNode().getNumber() == getNode().getNumber();
        } else {
            return false;
        }
    }

}
