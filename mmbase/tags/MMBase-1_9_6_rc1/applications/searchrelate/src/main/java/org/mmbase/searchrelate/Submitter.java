/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.searchrelate;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.mmbase.core.event.*;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import javax.servlet.http.HttpSession;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is used by mm-sr:relatednodes to execute some things after a transaction is committed.
 *
 * In the first place this was for changes in order which were made in a transaction.  We do this afterwards because to sort a query result,
 * it is important that the query can be executed. Which is not possible when nodes are modified or new in a transaction.
 *
 * It will probably also be used to clean up other stuff from the session.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 */
public class Submitter implements TransactionEventListener {
    private static final Logger LOG = Logging.getLoggerInstance(Submitter.class);

    private static final String KEY_RESOLUTIONS = Submitter.class.getName() + ".resolutions";
    private static final Map<String, Submitter> instances = new ConcurrentHashMap<String, Submitter>();

    private final String transactionName;
    private final Map<NodeQuery, List<Integer>> orders = new HashMap<NodeQuery, List<Integer>>();
    private final List<Runnable> endCallBacks = new ArrayList<Runnable>();
    private HttpSession session;


    protected Submitter(String tn) {
        transactionName = tn;
    }

    public static Submitter getInstance(String name, HttpSession session) {
        synchronized(instances) {
            Submitter os = instances.get(name);
            if (os == null) {
                os = new Submitter(name);
                instances.put(name, os);
                EventManager.getInstance().addEventListener(os);
                LOG.debug("Listening " + os);
            } else {
                LOG.debug("Already an instance of " + name + " in " + instances);
            }
            if (session != null) {
                os.session = session;
            }
            return os;
        }
    }


    public String getTransactionName() {
        return transactionName;
    }


    /**
     * Sets the new order for a certain nq, which must be committed after the transaction.
     * @param order The list with node numbers. The node numbers may still be negative (from transaction).
     *              The numbers may also be Strings.
     */
    public void setOrder(NodeQuery nq, List order) {

        // making sure the arrays contaisn integer
        // (it is produced in jsp, and will probably contain strings in stead)
        List<Integer> integerOrder = new ArrayList<Integer>();
        for (Object o : order) {
            integerOrder.add(org.mmbase.util.Casting.toInt(o));
        }

        orders.put(nq, integerOrder);
        LOG.debug("Orders " + orders);
    }

    public void addCallbackForEnd(Runnable r) {
        if (! endCallBacks.contains(r)) {
            endCallBacks.add(r);
        }
    }



    public void notify(TransactionEvent e) {
        if (e.getTransactionName().equals(transactionName)) {
            LOG.debug("" + e);
            if (e instanceof TransactionEvent.Resolve) {
                TransactionEvent.Resolve resolve = (TransactionEvent.Resolve) e;
                List<NodeQuery> keys = new ArrayList<NodeQuery>(orders.keySet());

                if (session != null) {
                    Map<Integer, Integer> resolutions = (Map<Integer, Integer>) session.getAttribute(KEY_RESOLUTIONS);
                    if (resolutions == null) {
                        resolutions =  new ConcurrentHashMap<Integer, Integer>();
                        session.setAttribute(KEY_RESOLUTIONS, resolutions);
                    }
                    resolutions.putAll(resolve.getResolution());
                    LOG.debug("put " + KEY_RESOLUTIONS + " -> " + resolutions + " in session");
                } else {
                    LOG.debug("No session in " + this);
                }

                // first fix queries which were using startNodes that are new.
                for (NodeQuery q : keys) {
                    NodeQuery toChange = (NodeQuery) Queries.fixQuery(q, resolve.getResolution());
                    if (toChange != null) {
                        List<Integer> value = orders.remove(q);
                        orders.put(toChange, value);
                        LOG.debug("Replaced " + q.toSql() + " with " + toChange.toSql());
                    }
                }

                // The fix the new nodes in the the saved lists with orders.
                for (Map.Entry<NodeQuery, List<Integer>> entry : orders.entrySet()) {
                    for (Map.Entry<Integer, Integer> resolution : resolve.getResolution().entrySet()) {
                        int index = entry.getValue().indexOf(resolution.getKey());
                        if (index != -1) {
                            entry.getValue().set(index, resolution.getValue());
                        }
                    }
                    LOG.debug("Resolved " + resolve.getResolution() + " order now" + entry.getValue());
                }
            }
            if (e instanceof TransactionEvent.Commit) {

                // Commit the actual new order to the database too.
                for (Map.Entry<NodeQuery, List<Integer>> entry : orders.entrySet()) {
                    try {
                        int changes = Queries.reorderResult(entry.getKey(), entry.getValue());
                        LOG.service("Made " + changes + " changes for " + entry);
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                }
            }
            if (e instanceof TransactionEvent.End) {
                LOG.service("Will remove " + this);
                EventManager.getInstance().removeEventListener(this);
                instances.remove(getTransactionName());
                for (Runnable r : endCallBacks) {
                    try {
                        LOG.service("Calling " + r);
                        r.run();
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } else {
            LOG.debug("Ignoring " + e);
        }
    }


    @Override
    public String toString() {
        return "Submitter for " + transactionName;
    }

    @Override
    public int hashCode() {
        return transactionName.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        return
            o != null &&
            o instanceof Submitter &&
            ((Submitter) o).getTransactionName().equals(transactionName);
    }
}

