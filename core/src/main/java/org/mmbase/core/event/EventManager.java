/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.IOException;
import java.util.*;
import java.net.URL;

import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.util.concurrent.*;

/**
 * This class manages all event related stuff. it is the place to register event brokers, and it
 * will propagate all events. The class is set up as a singleton. When the manager is instantiated,
 * event brokers are added for Event, NodeEvent and RelationEvent
 *
 * @author  Ernst Bunders
 * @since   MMBase-1.8
 * @version $Id$
 */
public class EventManager implements SystemEventListener {

    private static final Logger log = Logging.getLoggerInstance(EventManager.class);

    public static final String PUBLIC_ID_EVENTMANAGER = "-//MMBase//DTD eventmanager config 1.0//EN";
    public static final String DTD_EVENTMANAGER = "eventmanager_1_0.dtd";


    static {
        org.mmbase.util.xml.EntityResolver.registerPublicID(PUBLIC_ID_EVENTMANAGER, DTD_EVENTMANAGER, EventManager.class);
    }

    /**
     * the instance that this singleton will manage
     */
    private static final EventManager eventManager = new EventManager();

    /**
     * The collection of event brokers. There is one for every event type that can be sent/received
     */
    private final Set<EventBroker> eventBrokers = new CopyOnWriteArraySet<EventBroker>();
    private final List<SystemEvent.Collectable> receivedSystemEvents = new CopyOnWriteArrayList<SystemEvent.Collectable>();

    private long numberOfPropagatedEvents = 0;
    private long duration = 0;

    private final Set<EventListener> listenersFromResources = new CopyOnWriteArraySet<EventListener>();

    /**
     * use this metod to get an instance of the event manager
     */
    public static EventManager getInstance() {
        return eventManager;
    }

    public synchronized void notify(SystemEvent se) {
        log.debug("Received " + se);
        if (se instanceof SystemEvent.Collectable) {
            receivedSystemEvents.add((SystemEvent.Collectable) se);
        }
        if (se instanceof SystemEvent.ResourceLoaderChange) {
            log.service("Reconfiguring event managers, because " + se);
            watcher.onChange();
        }
    }


    protected ResourceWatcher watcher = new ResourceWatcher() {
            public void onChange(String w) {
                configure(w);
            }
        };

    private EventManager() {
        watcher.add("eventmanager.xml");
        watcher.onChange();
        watcher.start();
        addEventListener(this);

    }


    protected synchronized void configure(String resource) {
        log.service("Configuring the event manager");
        Set<EventBroker> originalEventBrokers = new HashSet<EventBroker>();
        Set<EventListener> newListeners = new HashSet<EventListener>();
        originalEventBrokers.addAll(eventBrokers);
        eventBrokers.clear();
        for (URL url : ResourceLoader.getConfigurationRoot().getResourceList(resource)) {
            try {
                log.debug("listeners of " + url);
                if (url.openConnection().getDoInput()) {

                    Document config = ResourceLoader.getDocument(url, true, EventManager.class);
                    DocumentReader configReader = new DocumentReader(config);

                    // find the event brokers
                    for (Element element: configReader.getChildElements("eventmanager.brokers", "broker")) {
                        try {
                            EventBroker broker = (EventBroker) org.mmbase.util.xml.Instantiator.getInstance(element);
                            if (broker != null) {
                                if (log.isDebugEnabled()) {
                                    log.debug("adding event broker: " + broker);
                                }
                                addEventBroker(broker);
                            }
                        } catch (Throwable ee) {
                            log.warn(ee.getMessage(), ee);
                        }
                    }
                }
            } catch (SAXException e1) {
                log.error("Something went wrong configuring the event system (" + url + "): " + e1.getMessage(), e1);
            } catch (IOException e1) {
                log.error("something went wrong configuring the event system (" + url + "): " + e1.getMessage(), e1);

            }
        }
        log.debug("Found brokers " + getBrokers());
        for (URL url : ResourceLoader.getConfigurationRoot().getResourceList(resource)) {
            try {
                log.debug("listeners of " + url);
                if (url.openConnection().getDoInput()) {

                    Document config = ResourceLoader.getDocument(url, true, EventManager.class);
                    DocumentReader configReader = new DocumentReader(config);
                    // And also  listeners (they are also often added programmatically)
                    for (Element element: configReader.getChildElements("eventmanager.listeners", "listener")) {
                        try {
                            EventListener listener = (EventListener) org.mmbase.util.xml.Instantiator.getInstance(element);
                            log.debug(" " + listener);
                            addEventListener(listener);
                            newListeners.add(listener);
                        } catch (Throwable ee) {
                            log.warn(ee.getMessage(), ee);
                        }
                    }
                }
            } catch (SAXException e1) {
                log.debug("Something went wrong configuring the event system (" + url + "): " + e1.getMessage(), e1);
            } catch (IOException e1) {
                log.debug("something went wrong configuring the event system (" + url + "): " + e1.getMessage(), e1);
            }
        }

        if (eventBrokers.size() == 0) {
            log.debug("No event brokers could not be found. This means that query-invalidation does not work correctly now. Proceeding anyway.");
        }
        for (EventBroker original :  originalEventBrokers) {
            for (EventListener el : original.getListeners()) {
                log.debug("Readding " + el);
                if (! listenersFromResources.remove(el)) {
                    addEventListener(el, false);
                }
            }
        }
        listenersFromResources.clear();
        listenersFromResources.addAll(newListeners);
        log.debug("Ready ");

    }

    /**
     * @since MMBase-1.8.5
     */

    public Collection<EventBroker> getBrokers() {
        return Collections.unmodifiableSet(eventBrokers);
    }

    /**
     * add an event broker for a specific type of event
     * @param broker
     */
    public void addEventBroker(EventBroker broker) {
        //we want only one instance of each broker
        if(! eventBrokers.contains(broker)){
            if (log.isDebugEnabled()) {
                log.debug("adding broker " + broker.toString());
            }
            eventBrokers.add(broker);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("broker " + broker.toString() + "was already registered: rejected.");
            }
        }
    }

    /**
     * remove a broker for a specific type of event
     * @param broker
     */
    public void removeEventBroker(EventBroker broker) {
        eventBrokers.remove(broker);
    }


    public synchronized void addEventListener(EventListener listener) {
        addEventListener(listener, true);
    }
    /**
     * @param listener
     */
    protected synchronized void addEventListener(EventListener listener, boolean propagateToCollected) {
        BrokerIterator i =  findBrokers(listener);
        boolean notifiedReceived = ! propagateToCollected;
        while (i.hasNext()) {
            EventBroker broker = i.next();
            if (broker.addListener(listener)) {
                if (! notifiedReceived && listener instanceof SystemEventListener) {
                    log.debug("Notifying " + receivedSystemEvents + " to " + listener);
                    notifiedReceived = true;
                    for (SystemEvent.Collectable se : receivedSystemEvents) {
                        ((SystemEventListener) listener).notify(se);
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("listener " + listener + " added to broker " + broker );
                }
            }
        }
    }


    /**
     * @param listener
     */
    public void removeEventListener(EventListener listener) {
        if (log.isDebugEnabled()) {
            log.debug("removing listener of type: " + listener.getClass().getName());
        }
        BrokerIterator i = findBrokers(listener);
        while (i.hasNext()) {
            i.next().removeListener(listener);
        }
    }

    /**
     * This method will propagate the given event to all the aproprate listeners. what makes a
     * listener apropriate is determined by it's type (class) and by possible constraint properties
     * (if the handling broker supports those
     * @see AbstractEventBroker
     * @param event
     */
    public void propagateEvent(Event event) {
        if (log.isTraceEnabled()) {
            log.trace("Propagating events to " + eventBrokers);
        }
        long startTime = System.nanoTime();
        for (EventBroker broker :  eventBrokers) {
            try {
                if (broker.canBrokerForEvent(event)) {
                    broker.notifyForEvent(event);
                    if (log.isDebugEnabled()) {
                        if (log.isTraceEnabled()) {
                            log.trace("event from '" + event.getMachine() + "': " + event + " has been accepted by broker " + broker);
                        } else {
                            log.debug("event from '" + event.getMachine() + "' has been accepted by broker " + broker);
                        }
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        if (log.isTraceEnabled()) {
                            log.trace("event from '" + event.getMachine() + "': " + event + " has been rejected by broker " + broker);
                        } else {
                            log.debug("event from '" + event.getMachine() + "' has been rejected by broker " + broker);
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        numberOfPropagatedEvents++;
        duration += (System.nanoTime() - startTime);
    }

    /**
     * Like {@link #propagateEvent} but with an extra argument 'asynchronous'.
     * @param asynchronous If true, execute the propagation in a different thread, and don't let this thread wait for the result.
     * @since MMBase-1.9.3
     */
    public void propagateEvent(final Event event, boolean asynchronous) {
        if (asynchronous) {
            ThreadPools.jobsExecutor.execute(new Runnable() {
                    public void run() {
                        propagateEvent(event);
                    }
                });
        } else {
            propagateEvent(event);
        }
    }

    /**
     * @since MMBase-1.8.1
     */
    public long getNumberOfPropagatedEvents() {
        return numberOfPropagatedEvents;
    }
    /**
     * @since MMBase-1.8.1
     */
    public long getPropagationCost() {
        return duration / 1000000;
    }
    /**
     * @since MMBase-1.9
     */
    public long getPropagationCostNs() {
        return duration;
    }


    /**
     * @param listener
     */
    private BrokerIterator findBrokers(final EventListener listener) {
        if (log.isDebugEnabled()) {
            log.debug("try to find broker for " + listener.getClass().getName());
        }
        return new BrokerIterator(eventBrokers.iterator(), listener);
    }

    /**
     * @since MMBase-1.9
     */
    public void shutdown() {
        log.service("Shutting down event manager");
        eventBrokers.clear();
        watcher.exit();
    }

    private static class BrokerIterator implements Iterator<EventBroker>, Iterable<EventBroker> {
        EventBroker next;
        final Iterator<EventBroker> i;
        final EventListener listener;

        BrokerIterator(final Iterator<EventBroker> i, final EventListener listener) {
            this.i = i;
            this.listener = listener;
            findNext();
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public Iterator<EventBroker> iterator() {
            return this;
        }
        public EventBroker next() {
            if (next == null) throw new NoSuchElementException();
            EventBroker n = next;
            findNext();
            return n;
        }
        public boolean hasNext() {
            return next != null;
        }

        protected void findNext() {
            while(i.hasNext()) {
                EventBroker broker = i.next();
                if (broker.canBrokerForListener(listener)) {
                    if (log.isDebugEnabled()) {
                        log.debug("broker " + broker + " can broker for eventlistener " + listener.getClass().getName());
                    }
                    next = broker;
                    return;
                } else if (log.isDebugEnabled()) {
                    log.debug("broker " + broker + " cannot boker for eventlistener." + listener.getClass().getName());
                }
            }
            next = null;
        }

    }



}
