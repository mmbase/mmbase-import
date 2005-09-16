/*
 * Created on 6-sep-2005
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

/**
 * This class is the base class for all mmbase events
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 */
public abstract class Event {

    protected String name;

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}
