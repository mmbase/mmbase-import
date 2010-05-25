/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

/**
 * This is a listener interface for node events. Short lived objects, which want to be notified
 * about NodeEvent's can implement this, so that they are automaticly removed from the Broker if
 * they are not any more referenced otherwise.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8.5
 * @version $Id$
 */
public interface WeakNodeEventListener extends EventListener {
    public void notify(NodeEvent event);
}
