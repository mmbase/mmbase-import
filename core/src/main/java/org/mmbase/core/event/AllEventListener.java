/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * This is a listener interface for every type of event. Primarily created for
 * {@link org.mmbases.clustering.CluserManager}, which has to propagate all local events to the mmbase cluster.
 * @author Ernst Bunders
 * @since 1.8
 * @version $Id$
 * @see  AllEventBroker
 *
 */
public interface AllEventListener extends EventListener {
    public void notify(Event event);
}
