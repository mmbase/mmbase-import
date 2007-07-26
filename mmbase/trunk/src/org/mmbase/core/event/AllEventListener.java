/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.core.event;


/**
 * This is a listener interface for every type of event. Primarily created for the
 * CluserManager, which has to propagate all local events int the mmbase cluster.
 * @author Ernst Bunders
 * @since 1.8
 * @version $Id: AllEventListener.java,v 1.2 2007-07-26 11:45:54 michiel Exp $
 *
 */
public interface AllEventListener extends EventListener {
    public void notify(Event event);
}
