/*
 * Created on 7-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.Properties;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * This class is the event broker implementation for the RelationEvent
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public class RelationEventBroker extends AbstractEventBroker {

    private static Logger log = Logging.getLoggerInstance(RelationEventBroker.class);

    /**
     * use this property to make shure your listener only gets the relation
     * events where the node number matches the given value.
     */
    public static final String PROPERTY_NODETYPE = "nodetype";

    /*
     * (non-Javadoc)
     * 
     * @see event.AbstractEventBroker#canBrokerFor(event.EventListener)
     */
    public boolean canBrokerForListener(EventListener listener) {
        // if(listener.getClass().equals(RelationEventListener.class))return
        // true;
        if (listener instanceof RelationEventListener) return true;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see event.AbstractEventBroker#shouldNotifyForEvent(event.Event)
     */
    public boolean canBrokerForEvent(Event event) {
        return (event.getClass().equals(RelationEvent.class));
    }

    /*
     * (non-Javadoc)
     * 
     * @see event.AbstractEventBroker#notifyEventListener(event.Event,
     *      event.EventListener)
     */
    protected void notifyEventListener(Event event, EventListener listener) throws ClassCastException {
        RelationEvent re = (RelationEvent) event;
        RelationEventListener rel = (RelationEventListener) listener;
        Properties p = rel.getConstraintsForEvent(re);

        MMObjectBuilder builder = MMBase.getMMBase().getBuilder(re.getBuilderName());
        if (builder.broadcastChanges()) {
            if (p != null) {
                String nodeType = p.getProperty(PROPERTY_NODETYPE);
                if (nodeType.equals(re.getRelationSourceType())
                    || nodeType.equals(re.getRelationDestinationType())) {
                    rel.notify(re);
                } else {
                    log.debug("the constraints set by " + rel + " were not met by event " + re);
                }
            } else {
                // no constraints
                rel.notify(re);
            }
        }

    }

}
