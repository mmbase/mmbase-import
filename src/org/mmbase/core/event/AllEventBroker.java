/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * a simple broker for AllEventListener. Primarily created for the
 * CluserManager, which has to propagate all local events int the mmbase cluster.
 * @author Ernst Bunders
 * @since 1.8
 *
 */
public class AllEventBroker extends AbstractEventBroker {

    public boolean canBrokerForListener(EventListener listener) {
        if (listener instanceof AllEventListener) {
            return true;
        }
        return false;
    }

    public boolean canBrokerForEvent(Event event) {
        return true;
    }

    protected void notifyEventListener(Event event, EventListener listener) throws ClassCastException {
        ((AllEventListener)listener).notify(event);
    }

    /* (non-Javadoc)
     * @see org.mmbase.core.event.AbstractEventBroker#toString()
     */
    public String toString() {
        return "All Event Broker";
    }

}
