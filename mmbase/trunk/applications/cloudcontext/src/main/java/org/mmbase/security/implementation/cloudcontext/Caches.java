/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import java.util.*;
import org.mmbase.cache.Cache;
import org.mmbase.cache.QueryResultCache;
import org.mmbase.core.event.*;
import org.mmbase.security.Rank;
import org.mmbase.security.Operation;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class is responsible for the Cloud Context Security caches. It contains them, and sets up a
 * listener to arrange invalidation of (entries of) them  when necessary.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since  MMBase-1.9.1
 */
public abstract class Caches {

    private static final Logger log = Logging.getLoggerInstance(Caches.class);


    protected static Cache<Integer, Rank> rankCache = new Cache<Integer,Rank>(20) {
            public String getName()        { return "CCS:SecurityRank"; }
            public String getDescription() { return "Caches the rank of users. User node --> Rank"; }
        };

    protected static Cache<String, MMObjectNode> userCache = new Cache<String, MMObjectNode>(20) {
            public String getName()        { return "CCS:SecurityUser"; }
            public String getDescription() { return "Caches the users. UserName --> User Node"; }
        };




    protected static Cache<String, MMObjectNode> contextCache = new Cache<String, MMObjectNode>(30) { // 30 'contexts' (organisations or so)
            public String getName()        { return "CCS:ContextCache"; }
            public String getDescription() { return "Links owner field to Contexts MMObjectNodes"; }
        };


    protected static Cache<String, ContextProvider.AllowingContexts> allowingContextsCache = new Cache<String, ContextProvider.AllowingContexts>(200) { // 200 users.
            public String getName()        { return "CCS:AllowingContextsCache"; }
            public String getDescription() { return "Links user id to a set of contexts"; }
        };
    protected static class OperationsCache extends Cache<String, Set<MMObjectNode>> {
        OperationsCache() {
            super(100);
        }
        public String getName()        { return "CCS:SecurityOperations"; }
        public String getDescription() { return "The groups associated with a security operation";}

        public Object put(MMObjectNode context, Operation op, Set<MMObjectNode> groups) {
            return super.put(op.toString() + context.getNumber(), groups);
        }
        public Set<MMObjectNode> get(MMObjectNode context, Operation op) {
            return super.get(op.toString() + context.getNumber());
        }

    };


    protected static Cache<String,Boolean> containsCache = new Cache<String,Boolean>(500) {
            public String getName()        { return "CCS:ContainedBy"; }
            public String getDescription() { return "group + group/user --> boolean"; }
        };

    protected static OperationsCache operationsCache = new OperationsCache();



    static {
        rankCache.putCache();
        userCache.putCache();

        contextCache.putCache();
        allowingContextsCache.putCache();
        operationsCache.putCache();

        EventManager.getInstance().addEventListener(new NodeEventListener() {
                public void notify(NodeEvent event) {
                    Caches.notify(event);
                }
            });
        EventManager.getInstance().addEventListener(new RelationEventListener() {
                public void notify(RelationEvent event) {
                    Caches.notify(event);
                }
            });
    }

    public static Cache<Integer, Rank> getRankCache() {
        return rankCache;
    }

    public static Cache<String, MMObjectNode> getUserCache() {
        return userCache;
    }
    public static Cache<String, MMObjectNode> getContextCache() {
        return contextCache;
    }
    public static OperationsCache getOperationsCache() {
        return operationsCache;
    }
    public static Cache<String, ContextProvider.AllowingContexts> getAllowingContextsCache() {
        return allowingContextsCache;
    }

    public static Cache<String, Boolean> getContainsCache() {
        return containsCache;
    }


    static void notify(NodeEvent event) {

        try {
            String builder = event.getBuilderName();
            if (builder.equals(Authenticate.getInstance().getUserProvider().getUserBuilder().getTableName())) {
                invalidateCaches(event.getNodeNumber());
            }
            if (new BuilderNames(Verify.getInstance().getContextProvider().getContextQueries()).contains((builder))
                || builder.equals("rightsrel")
                || builder.equals("groups")
                || builder.equals("ranks")
                ) {
                invalidateCaches();
            }
        } catch (ClassCastException cce) {
            log.debug(cce + " (Different security implementations?)");
        }
    }
    static void notify(RelationEvent event) {
        try {
            String sourceType = event.getRelationSourceType();
            String destType = event.getRelationDestinationType();
            String userBuilder = Authenticate.getInstance().getUserProvider().getUserBuilder().getTableName();
            Collection<String> contextBuilders = new BuilderNames(Verify.getInstance().getContextProvider().getContextQueries());
            if (sourceType.equals(userBuilder)) {
                invalidateCaches(event.getRelationSourceNumber());
            }
            if (destType.equals(userBuilder)) {
                invalidateCaches(event.getRelationDestinationNumber());
            }
            if (contextBuilders.contains(sourceType) || contextBuilders.contains(destType)) {
                invalidateCaches();
            }
            if (sourceType.equals("groups") || destType.equals("groups")) {
                invalidateCaches();
            }
            if (sourceType.equals("ranks") || destType.equals("ranks")) {
                invalidateCaches();
            }
        } catch (ClassCastException cce) {
            log.debug(cce + " (Different security implementations?)");
        }
    }


    protected static boolean invalidateScheduled = false;
    private static final Timer timer = new Timer(true);
    protected static void invalidateCaches() {
        if (! invalidateScheduled) {
            invalidateScheduled = true;
            timer.schedule(new TimerTask() {
                    public void run() {
                        log.service("Invalidating all security caches now.");
                        synchronized(Caches.class) {
                            invalidateScheduled = false;
                            operationsCache.clear();
                            contextCache.clear();
                            allowingContextsCache.clear();
                            containsCache.clear();
                            Caches.class.notify();
                        }
                    }
                }, 1000);
        }
    }
    public static void waitForCacheInvalidation() throws InterruptedException {
        while (invalidateScheduled) {
            log.service("waiting for cache invalidation");
            synchronized(Caches.class) {
                Caches.class.wait();
            }
        }
    }

    protected static void invalidateCaches(int nodeNumber) {
        rankCache.remove(Integer.valueOf(nodeNumber));
        synchronized(userCache.getLock()) {
            Iterator<Map.Entry<String, MMObjectNode>> i =  userCache.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, MMObjectNode> entry = i.next();
                MMObjectNode node = entry.getValue();
                if (node == null) {
                    i.remove();
                } else {
                    if (node.getNumber() == nodeNumber) {
                        i.remove();
                    }
                }
            }
        }
    }


}
