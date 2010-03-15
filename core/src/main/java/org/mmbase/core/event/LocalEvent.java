/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * An event that does not need broadcasting to other servers
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.9.3
 * @version $Id: LocalEvent.java 41369 2010-03-15 20:54:45Z michiel $
 */
public abstract class LocalEvent extends Event {

    private static final long serialVersionUID = 1L;
    public LocalEvent() {
        super(null, TYPE_UNSPECIFIED);
    }

}
