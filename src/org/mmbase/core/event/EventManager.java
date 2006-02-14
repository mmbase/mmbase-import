/*
 * Created on 30-sep-2005
 *
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

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArraySet;

/**
 * This class manages all event related stuff. it is the place to register event brokers, and it
 * will propagate all events. The class is set up as a singleton, with lazy instantiation. When the
 * manager is instantiated, event brokers are added for Event, NodeEvent and RelationEvent
 * @author  Ernst Bunders
 * @since   MMBase-1.8
 * @version $Id: EventManager.java,v 1.9 2006-02-14 22:44:29 michiel Exp $
 */
public class EventManager {

    private static final Logger log = Logging.getLoggerInstance(EventManager.class);

    public static final String PUBLIC_ID_EVENTMANAGER = "-//MMBase//DTD eventmanager config 1.0//EN";
    public static final String DTD_EVENTMANAGER = "eventmanager_1_0.dtd";


    static {
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_EVENTMANAGER, DTD_EVENTMANAGER, EventManager.class);
    }

    /**
     * the instance that this singleton will manage
     */
    private static EventManager eventManager;

    /**
     * The collection of event brokers. There is one for every event type that can be sent/received
     */
    private Set eventBrokers = new CopyOnWriteArraySet();

    /**
     * use this metod to get an instance of the event manager
     */
    public static EventManager getInstance() {
        if (eventManager == null) {
            eventManager = new EventManager();
        }
        return eventManager;
    }

    private static Object findInstance(String className) {
        if (className == null || "".equals(className)) return null;
        try {
            Class aClass = Class.forName(className);
            Object newInstance = aClass.newInstance();
            return newInstance;
        } catch (ClassNotFoundException e) {
            log.error("could not find class with name " + className);
            log.error(e);
        } catch (InstantiationException e) {
            log.error("could not instantiate class with name" + className);
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error("the constructor of " + className + " is not accessible");
            log.error(e);
        }
        return null;
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
    }


    protected void configure(String resource) {
        log.service("Configuring the event manager");
        eventBrokers.clear();
        Iterator i =  ResourceLoader.getConfigurationRoot().getResourceList(resource).iterator();
        while (i.hasNext()) {
            URL url = (URL) i.next();
            try {
                if (url.openConnection().getDoInput()) {
                    
                    Document config = ResourceLoader.getDocument(url, true, EventManager.class);
                    DocumentReader configReader = new DocumentReader(config);
                    
                    // find the event brokers
                    Iterator e = configReader.getChildElements("eventmanager.brokers", "broker");
                    while (e.hasNext()) {
                        Element element = (Element) e.next();
                        String className = element.getAttribute("class");
                        AbstractEventBroker broker = (AbstractEventBroker) findInstance(className);
                        if (broker != null) {
                            if (log.isDebugEnabled()) {
                                log.debug("adding event broker: " + broker);
                            }
                            addEventBroker(broker);
                        }
                    }
                }
            } catch (SAXException e1) {
                log.error("Something went wrong configuring the event system (" + url + "): " + e1.getMessage(), e1);
            } catch (IOException e1) {
                log.error("something went wrong configuring the event system (" + url + "): " + e1.getMessage(), e1);
                
            }
        }
        if (eventBrokers.size() == 0) {
            log.fatal("No event brokers could not be found. This means that query-invalidation does not work correctly now. Proceeding anyway.");
            return;
        }
    }
    /**
     * add an event broker for a specific type of event
     * @param broker
     */
    public void addEventBroker(AbstractEventBroker broker) {
        
        //we want only one instance of each broker
        if(! eventBrokers.contains(broker)){
            log.service("adding broker " + broker.toString());
            eventBrokers.add(broker);
        }else{
            log.service("broker " + broker.toString() + "was already registered: rejected.");
        }
    }

    /**
     * remove a broker for a specific type of event
     * @param broker
     */
    public void removeEventBroker(AbstractEventBroker broker) {
        eventBrokers.remove(broker);
    }

    /**
     * @param listener
     */
    public void addEventListener(EventListener listener) {
        BrokerIterator i =  findBrokers(listener);
        while (i.hasNext()) {
            AbstractEventBroker broker = i.nextBroker(); 
            if(broker.addListener(listener)) {
                log.service("listener " + listener + " added to broker " + broker );
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
            i.nextBroker().removeListener(listener);
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
        if (log.isDebugEnabled()) {
            log.trace("Propagating events to " + eventBrokers);
        }
        for (Iterator i = eventBrokers.iterator(); i.hasNext();) {
            AbstractEventBroker broker = (AbstractEventBroker) i.next();
            if (broker.canBrokerForEvent(event)) {
                broker.notifyForEvent(event);
                if (log.isDebugEnabled()) {
                    log.debug("event: " + event + " has been accepted by broker " + broker);
                }
            }
        }
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

    private static class BrokerIterator implements Iterator {
        AbstractEventBroker next;
        final Iterator i;
        final EventListener listener;
        
        BrokerIterator(final Iterator i, final EventListener listener) {
            this.i = i;
            this.listener = listener;
            findNext();
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
        public Object next() {
            return nextBroker();
        }
        public boolean hasNext() {
            return next != null;
        }
        public AbstractEventBroker nextBroker() {
            if (next == null) throw new NoSuchElementException();
            AbstractEventBroker n = next;
            findNext();
            return n;
        }
        protected void findNext() {
            while(i.hasNext()) {
                AbstractEventBroker broker = (AbstractEventBroker) i.next();
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
