/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.searchrelate;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpSession;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * SessionCleaners are added to {@link OrderSubmitter#addCallbackForEnd} by order.jspx of mm-sr:relatednodes.  The new order is temporary
 * stored in the User's session. After commit, it can (and must) be removed again, for which this object ios used.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 */
public class SessionCleaner implements Runnable {
    private static final Logger LOG = Logging.getLoggerInstance(SessionCleaner.class);
    private static final String KEY = SessionCleaner.class.getName();

    private final HttpSession session;
    private final Set<String> keys = new HashSet<String>();

    SessionCleaner(HttpSession session) {
        this.session = session;
    }

    public static SessionCleaner getInstance(HttpSession session) {
        SessionCleaner instance = (SessionCleaner) session.getAttribute(KEY);
        if (instance == null) {
            instance = new SessionCleaner(session);
            session.setAttribute(KEY, instance);
        }
        return instance;
    }
    public void addKey(String key) {
        keys.add(key);
    }



    public void run() {
        try {
            LOG.info("Removing from session " + session.getId() + " " + keys);
            for (String key : keys) {
                session.removeAttribute(key);
            }
            session.removeAttribute(KEY);
        } catch (IllegalStateException ise) {
            LOG.info(session.getId() + ": " + ise.getMessage());
        }
    }

    @Override
    public int hashCode() {
        return session.getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return
            o != null &&
            o instanceof SessionCleaner &&
            ((SessionCleaner) o).session.getId().equals(session.getId());
    }

    @Override
    public String toString() {
        return "SessionCleaner:" + session.getId() + ":" + keys;
    }


}

