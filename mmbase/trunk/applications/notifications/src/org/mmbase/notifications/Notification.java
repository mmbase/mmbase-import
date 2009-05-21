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
import org.mmbase.util.functions.*;
import java.util.*;
import java.text.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 **/
public abstract class Notification {
    private static final Logger log = Logging.getLoggerInstance(Notification.class);

    /**
     *
     */
    public abstract void send(Relation notification, Date date);

    private static final List<Object> FILLER = new ArrayList<Object>();
    static {
        for (int i = 0; i < 10; i++) {
            FILLER.add(null);
        }
    }

    protected Object[] getFormatParameters(Relation notification, Date date) {
        String name;
        try {
            name = notification.getSource().getFunctionValue("name", null).toString();
        } catch (NotFoundException nfe) {
            name = notification.getSource().getFunctionValue("gui", null).toString();
        }
        String fullName;
        try {
            fullName = notification.getSource().getFunctionValue("fullName", null).toString();
        } catch (NotFoundException nfe) {
            fullName = notification.getSource().getFunctionValue("gui", null).toString();
        }
        List<Object> params = new ArrayList<Object>(FILLER);
        params.set(0, name);
        params.set(1, fullName);
        params.set(2, date);
        params.set(3, notification.getIntValue("offset")); // need to inverse_offet resourcebundle
                                                           // to convert into normal strings.
        //params.set(2, null); /* free spot (forward compatibility) */

        Node notifyable = notification.getDestination();
        NodeIterator pi = notifyable.getRelatedNodes("object", "related", "source").nodeIterator();
        while (pi.hasNext()) {
            Node p = pi.nextNode();
            Function paramsFunction;
            Parameters a;
            try {
                paramsFunction = p.getFunction("messageParameters");
                a = paramsFunction.createParameters();
                a.set("date", date);

            } catch (NotFoundException nfe) {
                log.debug("No function 'messageParameters' defined on " + p);
                continue;
            }
            for (Object param : (Iterable<Object>) paramsFunction.getFunctionValue(a)) {
                params.add(param);
            }

        }
        if (log.isDebugEnabled()) {
            log.debug("Format parameters " + params);
        }
        return params.toArray();
    }

    protected String format(Relation notification, String text, Object[] parameters) {
        MessageFormat mf = new MessageFormat(text, notification.getCloud().getLocale());
        return mf.format(parameters);
    }

    protected String getMessage(Relation notification) {
        Node notifyable = notification.getDestination();
        String message = notifyable.getStringValue("message");
        NodeManager messages = notifyable.getCloud().getNodeManager("notify_messages");
        NodeQuery q = Queries.createRelatedNodesQuery(notifyable, messages, "related", "destination");
        Queries.addConstraint(q, Queries.createConstraint(q, "type", FieldCompareConstraint.EQUAL, getClass().getName()));
        NodeList nl = messages.getList(q);
        if (nl.size() > 0) {
            message = nl.getNode(0).getStringValue("message");
            log.debug("Found alternative message for " + getClass().getName() + " ");
        }
        return message;
    }


}
