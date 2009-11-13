/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import org.mmbase.module.core.*;
import org.mmbase.util.HashCodeUtil;
import org.mmbase.storage.search.RelationStep;
/**
 * This class is a wrapper for relation event listeners that only want to listen
 * to events concerning a specific builder - more specifically, events concerning changes
 * in the relations from a specific builder's nodes.
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public class TypedRelationEventListenerWrapper implements RelationEventListener {

    private final MMObjectBuilder builder;
    private final String nodeType;
    private final RelationEventListener wrappedListener;
    private final int direction;
    private final boolean descendants;

    /**
     * @param builder The builder which must constrain the listener
     * @param wrappedListener the relation event listener you want to wrap
     * @param direction At which side of the relation nodes of this builders can be: {@link org.mmbase.storage.search.RelationStep#DIRECTIONS_SOURCE}, {@link org.mmbase.storage.search.RelationStep#DIRECTIONS_DESTINATION}, or {@link org.mmbase.storage.search.RelationStep#DIRECTIONS_BOTH}
     * @param descendants Whether also descendants of the given builder must be listened to. ('true' would be the must logical value).
     */
    public TypedRelationEventListenerWrapper(MMObjectBuilder builder, RelationEventListener wrappedListener, int direction, boolean descendants) {
        this.builder = builder;
        this.nodeType = builder.getTableName();
        this.wrappedListener = wrappedListener;
        this.direction = direction;
        this.descendants = descendants;
    }


    private boolean notify(RelationEvent event, String eventNodeType) {
        if (nodeType.equals(eventNodeType)) {
            wrappedListener.notify(event);
            return true;
        } else if (descendants) {
            MMObjectBuilder eventBuilder = MMBase.getMMBase().getBuilder(eventNodeType);
            if(nodeType.equals("object") || (eventBuilder != null && eventBuilder.isExtensionOf(builder))) {
                wrappedListener.notify(event);
                return true;
            }
        }
        return false;
    }

    public void notify(RelationEvent event) {
        switch(direction) {
        case RelationStep.DIRECTIONS_SOURCE:
            notify(event, event.getRelationSourceType());
            break;
        case RelationStep.DIRECTIONS_DESTINATION:
            notify(event, event.getRelationDestinationType());
            break;
        case RelationStep.DIRECTIONS_BOTH:
        default:
            if (! notify(event, event.getRelationSourceType())) {
                notify(event, event.getRelationDestinationType());
            }
        }
    }

    public String toString() {
        return "TypedRelationEventListenerWrapper(" + wrappedListener + ")";
    }

    public boolean equals(Object o) {
        if (o instanceof TypedRelationEventListenerWrapper) {
            TypedRelationEventListenerWrapper tw = (TypedRelationEventListenerWrapper) o;
            return
                builder.equals(tw.builder) &&
                wrappedListener.equals(tw.wrappedListener) &&
                direction == tw.direction &&
                descendants == tw.descendants;
        } else {
            return false;
        }
    }
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, builder);
        result = HashCodeUtil.hashCode(result, wrappedListener);
        result = HashCodeUtil.hashCode(result, direction);
        return result;

    }


}
