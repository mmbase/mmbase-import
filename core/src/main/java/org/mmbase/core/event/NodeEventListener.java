/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

/**
 * This is the listener interface for node events
 * 
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id$
 */
public interface NodeEventListener extends EventListener {
    public void notify(NodeEvent event);
}
