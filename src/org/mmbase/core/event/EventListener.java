/*
 * Created on 7-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.util.Properties;

/**
 * This interface is the common type for custom EventListener interfaces. to
 * create such a interface extend this one and add a method that will receive an
 * event of the specific type.
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public interface EventListener {
    /**
     * 
     * @param event
     * @return A properties object containing key-value properties for the given
     *         event (type). This way a class implementing several event
     *         listeners can give constraints for them separately. If there are
     *         no constraint properties return null.<br/> the properties given
     *         must be known to the event broker for this particular event.
     */
    public Properties getConstraintsForEvent(Event event);
}
