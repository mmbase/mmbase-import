/*
 * Created on 9-sep-2005
 *
 */
package org.mmbase.core.event;

import java.util.Properties;

/**
 *  This class is a wrapper for relation event listeners that only want to listen to 
 * events from a specific builder.
 * @author Ernst Bunders
 * @since MMBase-1.8
 * 
 */
public class TypedRelationEventWrapper implements RelationEventListener {
    private String nodeType;

    private RelationEventListener wrappedListener;

    /**
     * @param nodeType should be a valid builder name
     * @param wrappedListener the relation event listener you want to wrap
     */
    public TypedRelationEventWrapper(String nodeType,
            RelationEventListener wrappedListener) {
        this.nodeType = nodeType;
        this.wrappedListener = wrappedListener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mmbase.core.event.RelationEventListener#fire(org.mmbase.core.event.RelationEvent)
     */
    public void fire(RelationEvent event) {
        wrappedListener.fire(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mmbase.core.event.EventListener#getConstraintsForEvent(org.mmbase.core.event.Event)
     */
    public Properties getConstraintsForEvent(Event event) {
        Properties p = new Properties();
        p.setProperty(RelationEventBroker.PROPERTY_NODETYPE, nodeType);
        return p;
    }

}