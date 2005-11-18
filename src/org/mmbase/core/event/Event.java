/*
 * Created on 6-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.Serializable;

/**
 * This class is the base class for all mmbase events
 * 
 * @author  Ernst Bunders
 * @since   MMBase-1.8
 * @version $Id: Event.java,v 1.5 2005-11-18 15:11:30 ernst Exp $
 */
public abstract class Event implements Serializable, Cloneable{

    protected String machine;

    /**
     * @return Returns the machine.
     */
    public String getMachine() {
        return machine;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return "Event";
    }

    /**
     * @param machine
     */
    public Event(String machine) {
        this.machine = machine;
    }
    
    public Object clone(){
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {}
        return clone;
    }

}
