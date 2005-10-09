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

import org.mmbase.util.ResourceLoader;
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
 * @author Ernst Bunders
 * @since 1.8
 * 
 */
public class EventManager {

    private static final Logger log = Logging.getLoggerInstance(EventManager.class);

    /**
     * the instance that this singleton will manage
     */
    private static EventManager eventManager;

    /**
     * the collection of event brokers. There is one for every event type that can be sent/received
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

    public static void configure() {
        try {
            Document config = ResourceLoader.getConfigurationRoot().getDocument("eventmanager.xml");
            DocumentReader configReader = new DocumentReader(config);

            log.debug("configuring the event manager");
            // make shure we have an instance
            getInstance();

            // find the event brokers
            Iterator e = configReader.getChildElements("eventmanager.brokers", "broker");
            while (e.hasNext()) {
                Element element = (Element) e.next();
                String className = element.getAttribute("class");
                AbstractEventBroker broker = (AbstractEventBroker) findInstance(className);
                if (broker != null) {
                    log.debug("adding event broker: " + broker);
                    eventManager.addEventBroker(broker);
                }
            }
        } catch (SAXException e1) {
            log.error("something went wrong configuring the event system");
            log.error(e1);
        } catch (IOException e1) {
            log.error("something went wrong configuring the event system");
            log.error(e1);
            e1.printStackTrace();
        }
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

    private EventManager() {}

    /**
     * add an event broker for a specific type of event
     * @param broker
     * @since MMBase-1.8
     */
    public void addEventBroker(AbstractEventBroker broker) {
        log.service("adding broker" + broker);
        eventBrokers.add(broker);
    }

    /**
     * remove a broker for a specific type of event
     * @param broker
     * @since MMBase-1.8
     */
    public void removeEventBroker(AbstractEventBroker broker) {
        eventBrokers.remove(broker);
    }

    /**
     * @param listener
     * @since MMBase-1.8
     */
    public void addEventListener(EventListener listener) {
        log.service("adding listener " + listener);
        AbstractEventBroker[] brokers = findBrokersFor(listener);
        if (brokers != null) {
            for (int i = 0; i < brokers.length; i++) {
                brokers[i].addListener(listener);
                log.debug("listener added to " + brokers[i]);
            }
        }
    }

    /**
     * @param listener
     * @since MMBase-1.8
     */
    public void removeEventListener(EventListener listener) {
        log.service("removing listnerer of type : " + listener.getClass().getName());
        AbstractEventBroker[] brokers = findBrokersFor(listener);
        if (brokers != null) {
            for (int i = 0; i < brokers.length; i++) {
                brokers[i].removeListener(listener);
            }
        }
    }

    /**
     * This method will propagate the given event to all the aproprate listeners. what makes a
     * listener apropriate is determined by it's type (class) and by possible constraint properties
     * (if the handling broker supports those
     * @see AbstractEventBroker
     * @param event
     * @since MMBase-1.8
     */
    public void propagateEvent(Event event) {
        for (Iterator i = eventBrokers.iterator(); i.hasNext();) {
            AbstractEventBroker broker = (AbstractEventBroker) i.next();
            if (broker.canBrokerForEvent(event)) {
                broker.notifyForEvent(event);
                log.debug("event: " + event.toString() + " has been accepted by broker " + broker.toString());
            }
        }
    }

    /**
     * @param listener
     * @since MMBase-1.8
     */
    private AbstractEventBroker[] findBrokersFor(EventListener listener) {
        log.debug("try to find broker for " + listener);

        List result = new ArrayList();
        for (Iterator i = eventBrokers.iterator(); i.hasNext();) {
            AbstractEventBroker broker = (AbstractEventBroker) i.next();
            log.debug("evaluating broker " + broker);
            if (broker.canBrokerForListener(listener)) {
                log.debug("broker " + broker + " can broker for eventlistener.");
                result.add(broker);
            }
        }
        if (result.size() > 0) { return (AbstractEventBroker[]) result.toArray(new AbstractEventBroker[result.size()]); }
        return null;
    }

}
