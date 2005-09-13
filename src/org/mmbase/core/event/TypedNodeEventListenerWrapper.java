/*
 * Created on 9-sep-2005
 *
 */
package org.mmbase.core.event;

import java.util.Properties;

/**
 * This class is a wrapper for node event listeners that only want to listen to 
 * events from a specific builder. 
 * @author Ernst Bunders 
 * @since MMBase-1.8
 */
public class TypedNodeEventListenerWrapper implements NodeEventListener {

    private String nodeType;
    private NodeEventListener wrappedListener;
    
    
    /**
     * @param nodeType should be a valid builder name
     * @param listener the node event listener you want to wrap
     */
    public TypedNodeEventListenerWrapper(String nodeType, NodeEventListener listener) {
        this.nodeType = nodeType;
        wrappedListener = listener;
        
    }
    /* (non-Javadoc)
     * @see org.mmbase.core.event.NodeEventListener#fire(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        wrappedListener.notify(event);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.EventListener#getConstraintsForEvent(org.mmbase.core.event.Event)
     */
    public Properties getConstraintsForEvent(Event event) {
        Properties p = new Properties();
        p.setProperty(NodeEventBroker.PROPERTY_NODETYPE, nodeType);
        return p;
    }

}
