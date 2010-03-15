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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is used by mm-sr:relatednodes to changes in order which were made in a transaction.
 * We do this afterwards because new nodes have negative numbers.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 */
public class OrderSubmitter implements TransactionEventListener {
    private static final Logger LOG = Logging.getLoggerInstance(OrderSubmitter.class);

    private static final Map<String, OrderSubmitter> instances = new ConcurrentHashMap<String, OrderSubmitter>();

    private final String transactionName;
    private final Map<NodeQuery, List<Integer>> orders = new HashMap<NodeQuery, List<Integer>>();
    private final List<Runnable> endCallBacks = new ArrayList<Runnable>();

    protected OrderSubmitter(String tn) {
        transactionName = tn;
    }

    public static OrderSubmitter getInstance(String name) {
        synchronized(instances) {
            OrderSubmitter os = instances.get(name);
            if (os == null) {
                os = new OrderSubmitter(name);
                instances.put(name, os);
                EventManager.getInstance().addEventListener(os);
                LOG.info("Listening " + os);
            } else {
                LOG.info("Already an instance of " + name + " in " + instances);
            }
            return os;
        }
    }


    public String getTransactionName() {
        return transactionName;
    }

    public void setOrder(NodeQuery nq, List order) {

        // making sure the arrays contaisn integer
        // (it is produced in jsp, and will probably contain strings in stead)
        List<Integer> integerOrder = new ArrayList<Integer>();
        for (Object o : order) {
            integerOrder.add(org.mmbase.util.Casting.toInt(o));
        }

        orders.put(nq, integerOrder);
        LOG.info("Orders " + orders);
    }

    public void addCallbackForEnd(Runnable r) {
        if (! endCallBacks.contains(r)) {
            endCallBacks.add(r);
        }
    }



    public void notify(TransactionEvent e) {
        if (e.getTransactionName().equals(transactionName)) {
            LOG.info("" + e);
            if (e instanceof TransactionEvent.Resolve) {
                TransactionEvent.Resolve resolve = (TransactionEvent.Resolve) e;
                for (Map.Entry<NodeQuery, List<Integer>> entry : orders.entrySet()) {
                    for (Map.Entry<Integer, Integer> resolution : resolve.getResolution().entrySet()) {
                        int index = entry.getValue().indexOf(resolution.getKey());
                        if (index != -1) {
                            entry.getValue().set(index, resolution.getValue());
                        }
                    }
                    LOG.info("Resolved " + resolve.getResolution() + " order now" + entry.getValue());
                }
            }
            if (e instanceof TransactionEvent.Commit) {
                for (Map.Entry<NodeQuery, List<Integer>> entry : orders.entrySet()) {
                    int changes = Queries.reorderResult(entry.getKey(), entry.getValue());
                    LOG.info("Made " + changes + " changes for " + entry);
                }
            }
            if (e instanceof TransactionEvent.End) {
                LOG.info("Will remove " + this);
                EventManager.getInstance().removeEventListener(this);
                instances.remove(getTransactionName());
                for (Runnable r : endCallBacks) {
                    r.run();
                }
            }
        } else {
            LOG.info("Ignoring " + e);
        }
    }
    @Override
    public String toString() {
        return "OrderSubmitter for " + transactionName;
    }

    @Override
    public int hashCode() {
        return transactionName.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        return
            o != null &&
            o instanceof OrderSubmitter &&
            ((OrderSubmitter) o).getTransactionName().equals(transactionName);
    }
}

