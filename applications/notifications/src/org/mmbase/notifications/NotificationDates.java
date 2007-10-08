/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
sQuery
*/
package org.mmbase.notifications;

import java.util.*;
import org.mmbase.bridge.*;
/**
 * Function wich determines for a certain programs node the dates for wich notifications must be
 * issued.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NotificationDates.java,v 1.1 2007-10-08 10:00:54 michiel Exp $
 **/
public class NotificationDates {
    protected Node node;

    public void setNode(Node n) {
        node = n;
    }
    public List<Date> dates() {
        List<Date> result = new ArrayList<Date>();
        Cloud cloud = node.getCloud();
        NodeList nl = node.getRelatedNodes();
        // TODO
        return result;

    }

}
