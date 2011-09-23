package org.mmbase.poll;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * If the mmbase 'crontab' module is active this class can (and defaultly is) scheduled to
 * periodically run to update the 'total' field of the poll builder (if this field exists).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class TotalUpdater implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(TotalUpdater.class);



    public void run() {
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        NodeManager poll = cloud.getNodeManager("poll");
        if (poll.hasField("total")) {
            NodeQuery q = poll.createQuery();
            Date now = new Date();
            Constraint cons1 = Queries.createConstraint(q, "begin", FieldCompareConstraint.LESS, now);
            Constraint cons2 = Queries.createConstraint(q, "end", FieldCompareConstraint.GREATER, now);
            Queries.addConstraint(q, cons1);
            Queries.addConstraint(q, cons2);
            NodeList nodes = poll.getList(q);
            NodeIterator ne = nodes.nodeIterator();
            Total total = new Total();
            total.setCalculate(true);
            while (ne.hasNext()) {
                Node p = ne.nextNode();
                total.setNode(p);
                int t = total.total();
                if (t != p.getIntValue("total")) {
                    log.service("Updated total of poll " + p.getNumber() + " to " + t);
                    p.setIntValue("total", t);
                    p.commit();
                }
            }
        } else {
            log.warn("no 'total' field");
        }

    }
}
