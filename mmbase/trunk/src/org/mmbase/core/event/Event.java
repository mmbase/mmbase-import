/*
 * Created on 6-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.Serializable;

import org.mmbase.module.core.MMBase;

/**
 * This class is the base class for all mmbase events
 * 
 * @author  Ernst Bunders
 * @since   MMBase-1.8
 * @version $Id: Event.java,v 1.6 2006-02-07 13:21:00 ernst Exp $
 */
public abstract class Event implements Serializable, Cloneable{

    protected String machine;

    public String getMachine() {
        return machine;
    }

    public String getName() {
        return "Event";
    }

    /**
     * @param machine the (local) machine name. if null the local machine name is extracted from MMBase
     */
    public Event(String machine) {
        if(machine == null)machine = MMBase.getMMBase().getMachineName();
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
