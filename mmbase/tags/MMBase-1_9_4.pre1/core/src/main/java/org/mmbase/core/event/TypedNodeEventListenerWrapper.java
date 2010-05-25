/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import org.mmbase.module.core.*;
import org.mmbase.util.HashCodeUtil;


/**
 * This class is a wrapper for node event listeners that only want to listen to
 * events concerning a specific builder.
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public class TypedNodeEventListenerWrapper implements NodeEventListener {

    private final MMObjectBuilder builder;
    private final String nodeType;
    private final NodeEventListener wrappedListener;
    private final boolean descendants;

    /**
     * @param builder The builder for which to filter events.
     * @param listener the node event listener you want to wrap
     * @param descendants Whether also descendants of the given builder must be listened to. ('true' would be the must logical value).
     */
    public TypedNodeEventListenerWrapper(MMObjectBuilder builder, NodeEventListener listener, boolean descendants) {
        this.builder = builder;
        this.nodeType = builder.getTableName();
        wrappedListener = listener;
        this.descendants = descendants;
    }


    public void notify(NodeEvent event) {
        if (nodeType.equals(event.getBuilderName())) {
            wrappedListener.notify(event);
        } else if (descendants) {
            MMObjectBuilder eventBuilder = MMBase.getMMBase().getBuilder(event.getBuilderName());
            if(nodeType.equals("object") || (eventBuilder != null && eventBuilder.isExtensionOf(builder))) {
                wrappedListener.notify(event);
            }
        }
    }

    public String toString() {
        return "TypedNodeEventListenerWrapper(" + wrappedListener + ")";
    }

    public boolean equals(Object o) {
        if (o instanceof TypedNodeEventListenerWrapper) {
            TypedNodeEventListenerWrapper tw = (TypedNodeEventListenerWrapper) o;
            return
                nodeType.equals(tw.nodeType) &&
                wrappedListener.equals(tw.wrappedListener) &&
                descendants == tw.descendants;
        } else {
            return false;
        }
    }
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, nodeType);
        result = HashCodeUtil.hashCode(result, wrappedListener);
        return result;

    }

}
