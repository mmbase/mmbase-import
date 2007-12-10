/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
sQuery
*/
package org.mmbase.notifications;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: Notification.java,v 1.6 2007-12-10 15:51:11 michiel Exp $
 **/
public abstract class Notification {
    private static final Logger log = Logging.getLoggerInstance(Notification.class);

    /**
     *
     */
    public abstract void send(Node recipient, Node notifyable, Date date);

    protected static String getMessage(Node notifyable, String type) {
        String message = notifyable.getStringValue("message");
        NodeManager messages = notifyable.getCloud().getNodeManager("notify_message");
        NodeQuery q = Queries.createRelatedNodesQuery(notifyable, messages, "related", "destination");
        Queries.addConstraint(q, Queries.createConstraint(q, "type", FieldCompareConstraint.EQUAL, type));
        NodeList nl = messages.getList(q);
        if (nl.size() > 0) {
            message = nl.getNode(0).getStringValue("message");
            log.debug("Found alternative message for " + type + " ");
        }
        return message;
    }


}
