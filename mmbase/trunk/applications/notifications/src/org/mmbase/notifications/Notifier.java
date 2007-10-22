/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.module.*;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.core.event.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.Casting;
import java.util.concurrent.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class maintains all notifyables and takes care of the actual submission of all
 * notifications.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Notifier.java,v 1.4 2007-10-22 12:51:18 michiel Exp $
 **/
public class Notifier extends ReloadableModule implements NodeEventListener, RelationEventListener, Runnable {

    private static final Logger log = Logging.getLoggerInstance(Notifier.class);

    protected boolean running = true;
    protected final Set<String> relevantBuilders = new HashSet<String>();

    DelayQueue<Notifyable> queue = new DelayQueue<Notifyable>();

    @Override
    public void reload() {
        loadNotifyables();
    }


    protected void loadNotifyables() {
        log.info("Loading notifyables");
        Date now = new Date();
        relevantBuilders.clear();
        relevantBuilders.addAll(Arrays.asList(getInitParameter("builders").split("\\s*,\\s*")));
        queue.clear();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);

        NodeManager notifyables = cloud.getNodeManager("notifyables");
        NodeQuery nq = notifyables.createQuery();
        Queries.addConstraint(nq, Queries.createConstraint(nq, "lastcheck", FieldCompareConstraint.LESS, now));
        Queries.addConstraint(nq, Queries.createConstraint(nq, "status", FieldCompareConstraint.EQUAL, 1));

        NodeIterator ni = notifyables.getList(nq).nodeIterator();

        long future = 3600 * 24 * 7; // a week
        String futureParameter = getInitParameter("future");
        if (futureParameter != null && ! "".equals(futureParameter)) {
            future = Long.parseLong(futureParameter);
        }

        while (ni.hasNext()) {

            Node notifyable = ni.nextNode();
            Date lastCheck  = notifyable.getDateValue("lastcheck");
            Date futureDate = new Date(lastCheck.getTime() + 1000 * future);

            NodeIterator pi = notifyable.getRelatedNodes("object", "related", null).nodeIterator();
            while (pi.hasNext()) {
                Node p = pi.nextNode();
                log.info("Using " + p);
                Function datesFunction = p.getFunction("dates");
                if (datesFunction == null) {
                    log.error("No function 'dates' defined on " + p);
                    continue;
                }
                Parameters params = datesFunction.createParameters();
                params.set("since", lastCheck);
                params.set("until", futureDate);
                Collection<Date> dates = Casting.toCollection(datesFunction.getFunctionValue(null));
                log.info("Found dates " + dates);
                for (Date date : dates) {
                    Notifyable.addNotifyables(queue, notifyable, date);
                }
            }
        }
        log.info("Loaded " + queue);
    }

    @Override
    public void init() {
        loadNotifyables();
        Thread thread = new Thread(MMBaseContext.getThreadGroup(), this);
        thread.start();
        EventManager.getInstance().addEventListener(this);
    }

    @Override
    public void shutdown() {
        running = false;
    }
    // implementation of Runnable
    public void run() {
        while (running) {
            try {
                Notifyable notifyable = queue.take();
                log.service("Found notifyiable " + notifyable);
                Node n = notifyable.getNode();
                notifyable.send();

                n.setDateValue("lastcheck", new Date());
                n.commit();

                if (queue.size() == 0) {
                    log.info("Queue is empty, refilling it");
                    loadNotifyables();
                }
            } catch (InterruptedException ie) {
                log.service(ie);
                return;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.service("Shut down " + this);
    }

    // implementation of NodeEventListener
    public void notify(NodeEvent ne) {
        if (log.isTraceEnabled()) {
            log.trace("received " + ne);
        }
        // TODO, this is a bit too crude.
        if (relevantBuilders.contains(ne.getBuilderName())) {
            loadNotifyables();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Ignoring because " + ne.getBuilderName() + " not in " + relevantBuilders);
            }
        }
    }
    public void notify(RelationEvent re) {
        // TODO, this is a bit too crude.
        if (relevantBuilders.contains(re.getRelationSourceType()) || relevantBuilders.contains(re.getRelationDestinationType())) {
            loadNotifyables();
        }
    }


    {
        addFunction(new AbstractFunction/*<List>*/("list", new Parameter[] {}, ReturnType.LIST) {
                public Object getFunctionValue(Parameters arguments) {
                    return new ArrayList<Notifyable>(queue);
                }
            });

    }



}
