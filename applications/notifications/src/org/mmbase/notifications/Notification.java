/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
sQuery
*/
package org.mmbase.notifications;

import org.mmbase.bridge.*;
/**
 * @author Michiel Meeuwissen
 * @version $Id: Notification.java,v 1.2 2007-10-08 16:55:17 michiel Exp $
 **/
public abstract class Notification {

    public abstract void send(Node recipient, Node notifyable);


}
