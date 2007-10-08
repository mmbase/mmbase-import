/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
sQuery
*/
package org.mmbase.notifications;

/**
 * @author Michiel Meeuwissen
 * @version $Id: Notification.java,v 1.1 2007-10-08 10:00:54 michiel Exp $
 **/
public abstract class Notification {

    public abstract void send(String message);


}
