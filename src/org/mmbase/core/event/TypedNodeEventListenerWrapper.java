/*
 * Created on 9-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.Properties;

/**
 * This class is a wrapper for node event listeners that only want to listen to
 * events concerning a specific builder.
 *
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

    /*
     * (non-Javadoc)
     * @see org.mmbase.core.event.NodeEventListener#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        if(event.getBuilderName().equals(nodeType))
            wrappedListener.notify(event);
    }

    public String toString() {
        return "TypedNodeEventListenerWrapper(" + wrappedListener + ")";
    }

}
