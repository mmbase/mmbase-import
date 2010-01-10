/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.notifications;
import org.mmbase.module.*;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMBase;
import org.mmbase.core.event.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.*;

import java.util.concurrent.*;
import java.util.*;
import java.util.regex.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class maintains all notifyables and takes care of the actual submission of all
 * notifications.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public class Notifier extends WatchedReloadableModule implements NodeEventListener, RelationEventListener, Runnable {

    private static final Logger log = Logging.getLoggerInstance(Notifier.class);

    protected boolean running = false;
    DelayQueue<Notifyable> queue = new DelayQueue<Notifyable>();

    protected Collection<String> getRelevantBuilders() {
        return Arrays.asList(getInitParameter("builders").split("\\s*,\\s*"));
    }

    @Override
    public void reload() {
        boolean active = determinActive();
        if (active) {
            loadNotifyables();
            if (! running) {
                startThread();
            }
        } else {
            if (running) {
                queue.clear();
                running = false;
            }
        }
    }


    protected synchronized void loadNotifyables() {
        log.service("Loading notifyables");
        Date now = new Date();
        queue.clear();
        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);

        NodeManager notifyables = cloud.getNodeManager("notifyables");
        NodeQuery nq = notifyables.createQuery();

        SortedMap<Object, String> map = SortedBundle.getResource("org.mmbase.notifications.resources.offset",
                                                                 null, null, null, Integer.class, null);

        Integer first = (Integer) map.entrySet().iterator().next().getKey();
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
            Date futureDate = new Date(new Date().getTime() + 1000 * future);

            NodeIterator pi = notifyable.getRelatedNodes("object", "related", "source").nodeIterator();
            while (pi.hasNext()) {
                Node p = pi.nextNode();
                if (log.isTraceEnabled()) {
                    log.trace("Using " + p);
                }
                Function datesFunction;
                try {
                    datesFunction = p.getFunction("dates");
                } catch (NotFoundException nfe) {
                    log.error("No function 'dates' defined on " + p);
                    continue;
                }
                Parameters params = datesFunction.createParameters();
                params.set("since", lastCheck);
                params.set("until", futureDate);
                Collection<Date> dates = Casting.toCollection(datesFunction.getFunctionValue((Parameters) null));
                log.debug("Found dates " + dates + " between " + lastCheck + " and " + futureDate + " for notifyable " + p.getNumber());
                for (Date date : dates) {
                    Notifyable.addNotifyables(queue, notifyable, date, lastCheck);
                }
            }
        }
    }

    @Override
    public void init() {
        super.init();
        if (determinActive()) {
            loadNotifyables();
            startThread();
        } else {
            log.service("Notifier not active");
        }
    }

    protected void startThread() {
        log.service("Starting notifier thread");
        Thread thread = new Thread(MMBaseContext.getThreadGroup(), this);
        thread.start();
        EventManager.getInstance().addEventListener(this);
        running = true;
    }

    /**
     * @todo Similar code in org.mmbase.module.lucene.Lucene, org.mmbase.sms.Sender Generalize this.
     */
    protected boolean determinActive() {

        boolean active = true;

        String setting = getInitParameter("active");
        while (setting != null && setting.startsWith("system:")) {
            setting = System.getProperty(setting.substring(7));
        }
        if (setting != null) {
            if (setting.startsWith("host:")) {
                Pattern host = Pattern.compile(setting.substring(5));
                try {
                    String hostName = java.net.InetAddress.getLocalHost().getHostName();
                    String catalinaName = (System.getProperty("catalina.base") + "@" + java.net.InetAddress.getLocalHost().getHostName());
                    active =
                        host.matcher(hostName).matches() ||
                        host.matcher(catalinaName).matches();
                    log.debug("" + host + " matches " + hostName + " or " + catalinaName + ": " + active);
                } catch (java.net.UnknownHostException uhe) {
                    log.error(uhe);
                }
            } else if (setting.startsWith("machinename:")) {
                Pattern machineName = Pattern.compile(setting.substring(12));
                active = machineName.matcher(MMBase.getMMBase().getMachineName()).matches();
                log.debug("" + machineName + " matches " + MMBase.getMMBase().getMachineName() + ": " + active);
            } else {
                active = "true".equals(setting);
            }
        }
        return active;
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
        Collection<String> relevantBuilders = getRelevantBuilders();
        // TODO, this is a bit too crude.
        if (relevantBuilders.contains(ne.getBuilderName())) {
            if (log.isDebugEnabled()) {
                log.debug("Received " + ne + "");
            }
            loadNotifyables();

        } else {
            if (log.isTraceEnabled()) {
                log.trace("Ignoring because " + ne.getBuilderName() + " not in " + relevantBuilders);
            }
        }
    }
    public void notify(RelationEvent re) {
        // TODO, this is a bit too crude.
        Collection<String> relevantBuilders = getRelevantBuilders();
        if (relevantBuilders.contains(re.getRelationSourceType()) || relevantBuilders.contains(re.getRelationDestinationType())) {
            if (log.isDebugEnabled()) {
                log.debug("Received " + re);
            }
            loadNotifyables();
        }
    }


    {
        addFunction(new AbstractFunction/*<List>*/("list", new Parameter[] {}, ReturnType.LIST) {
                public List getFunctionValue(Parameters arguments) {
                    synchronized(Notifier.this) {
                        return new ArrayList<Notifyable>(queue);
                    }
                }
            });

    }

    {
        addFunction(new AbstractFunction/*<Boolean>*/("running", new Parameter[] {}, ReturnType.LIST) {
                public Boolean getFunctionValue(Parameters arguments) {
                    return running;
                }
            });

    }



}
