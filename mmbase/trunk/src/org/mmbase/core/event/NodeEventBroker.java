/*
 * Created on 7-sep-2005
 *
 */
package org.mmbase.core.event;

import java.util.Properties;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class is the event broker implementation for the NodeEvent
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public class NodeEventBroker extends AbstractEventBroker {
    
    private static Logger log = Logging.getLoggerInstance(NodeEventBroker.class);
    
    /**
     * use this property to make shure your listener only gets the 
     * node events where the node number matches the given value.
     */
    public static final String PROPERTY_NODETYPE = "nodetype";
	

	/* (non-Javadoc)
	 * @see event.AbstractEventBroker#canBrokerFor(java.lang.Class)
	 */
	public boolean canBrokerForListener(EventListener listener) {
		//if(listener.getClass().equals(NodeEventListener.class))return true;
		if(listener instanceof NodeEventListener)return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see event.AbstractEventBroker#shouldNotifyForEvent(event.Event)
	 */
	public boolean canBrokerForEvent(Event event) {
		if(event.getClass().equals(NodeEvent.class))return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see event.AbstractEventBroker#notifyEventListeners()
	 */
	protected void notifyEventListener(Event event, EventListener listener ) {
		NodeEvent ne = (NodeEvent)event;
		NodeEventListener nel = (NodeEventListener)listener;
		Properties p = nel.getConstraintsForEvent(ne);
		if(p != null){
		    String nodeType = p.getProperty(PROPERTY_NODETYPE);
		    if(nodeType.equals(ne.getBuilderName())){
		        nel.notify(ne);
		    }else{
		        log.debug("the constraints set by "+nel+" were not met by event "+ne);
		    }
		}else{
		    //no constraints
		    nel.notify(ne);
		}
	}

}
