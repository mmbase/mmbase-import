/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

/**
 * This is the listener interface for id events
 * 
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */
public interface IdEventListener extends EventListener {
    public void notify(IdEvent event);
}
