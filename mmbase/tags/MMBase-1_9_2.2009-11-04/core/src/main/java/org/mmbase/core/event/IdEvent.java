/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * Very generic event type, which only adds an 'id' property. This can be used for events on all
 * kind of objects which are somehow identified by an ID. Of course, the default event types like
 * 'NEW', 'CHANGE' and 'DELETE' can very well make sense.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.8
 * @version $Id$
 */
public class IdEvent extends Event  {


    private static final long serialVersionUID = 1L;

    private String id;


    public IdEvent(String machineName, int type, String id) {
        super(machineName, type);
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public String toString() {
        return id + " " + eventType;
    }

}
