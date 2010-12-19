/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * A simple broker for AllEventListener. Primarily created for {@link
 * org.mmbase.clustering.ClusterManager}, which has to propagate all local events to the mmbase
 * cluster.
 *
 * @author Ernst Bunders
 * @since 1.8
 * @version $Id$
 * @see  AllEventListener
 *
 */
public class AllEventBroker extends AbstractEventBroker {

    @Override
    public boolean canBrokerForListener(EventListener listener) {
        if (listener instanceof AllEventListener) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canBrokerForEvent(Event event) {
        return true;
    }

    @Override
    protected void notifyEventListener(Event event, EventListener listener) throws ClassCastException {
        ((AllEventListener)listener).notify(event);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.AbstractEventBroker#toString()
     */
    @Override
    public String toString() {
        return "All Event Broker";
    }

}
