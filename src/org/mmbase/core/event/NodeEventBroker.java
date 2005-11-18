/*
 * Created on 7-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.Properties;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class is the event broker implementation for the NodeEvent
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public class NodeEventBroker extends AbstractEventBroker {

    private static Logger log = Logging
        .getLoggerInstance(NodeEventBroker.class);

    /**
     * use this property to make shure your listener only gets the node events
     * where the node number matches the given value.
     */
    public static final String PROPERTY_NODETYPE = "nodetype";

    /*
     * (non-Javadoc)
     * 
     * @see event.AbstractEventBroker#canBrokerFor(java.lang.Class)
     */
    public boolean canBrokerForListener(EventListener listener) {
        return listener instanceof NodeEventListener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see event.AbstractEventBroker#shouldNotifyForEvent(event.Event)
     */
    public boolean canBrokerForEvent(Event event) {
        return (event.getClass().equals(NodeEvent.class));
    }

    /*
     * (non-Javadoc)
     * 
     * @see event.AbstractEventBroker#notifyEventListeners()
     */
    protected void notifyEventListener(Event event, EventListener listener) {
        NodeEvent ne = (NodeEvent) event; //!!!!!
        NodeEventListener nel = (NodeEventListener) listener;
        Properties p = nel.getConstraintsForEvent(ne);
        MMObjectBuilder builder = MMBase.getMMBase().getBuilder(ne.getBuilderName()) ;
        if (builder.broadcastChanges()) {
            if (p != null) {
                String nodeType = p.getProperty(PROPERTY_NODETYPE);
                if (nodeType.equals(builder.getTableName())) {
                    nel.notify(ne);
                }
            } else {
                // no constraints
                nel.notify(ne);
            }
        }
    }

}
