/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.workflow;

import org.mmbase.security.implementation.cloudcontext.*;
import org.mmbase.security.implementation.cloudcontext.builders.Groups;
import org.mmbase.security.implementation.cloudcontext.builders.Ranks;
import org.mmbase.security.implementation.cloudcontext.builders.RightsRel;


import java.util.*;

import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.module.core.*;
import org.mmbase.module.core.NodeSearchQuery;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.cache.Cache;
import org.mmbase.cache.AggregatedResultCache;
import org.mmbase.util.ChainedList;
import org.mmbase.bridge.Query;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * For the moment only supports contextes prefixed with deleted:. But when necesary we'll also add e.g. 'unchecked:' or
 * so.
 * In other words we are using security to make nodes invisible.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since  MMBase-1.9.6
 */
public  class WorkFlowContextProvider extends BasicContextProvider {

    private static final Logger LOG = Logging.getLoggerInstance(WorkFlowContextProvider.class);
    static final String DELETED = "deleted:";


    public WorkFlowContextProvider(NodeSearchQuery... q) {
        super(q);
    }


    public WorkFlowContextProvider(MMObjectBuilder... b) {
        super(b);
    }

    public WorkFlowContextProvider(String... b) {
        super(b);
    }


    /**
     * Returns the MMObjectNode representing the 'context' which is identifier with the given name.
     */
    @Override
    public MMObjectNode getContextNode(String context) {
        if (context.startsWith(DELETED)) {
            context =  context.substring(DELETED.length(), context.length());
        }
        return  super.getContextNode(context);
    }
    @Override
    public String getContext(User userContext, MMObjectNode node) {
        boolean deleted = node.getStringValue("owner").startsWith(DELETED);
        String context = super.getContext(userContext, node);
        if (deleted) {
            context = DELETED + context;
        }
        return context;
    }

    @Override
    public boolean mayDo(User user, MMObjectNode nodeId, Operation operation) throws SecurityException {
        if (user.getRank().getInt() < Rank.ADMIN_INT) {
            String s = nodeId.getStringValue("owner");
            if (s.startsWith(DELETED)) {
                return false;
            }
        }
        return super.mayDo(user, nodeId, operation);
    }

    protected Authorization.QueryCheck addWorkFlowConstraints(List<Constraint> constraints, User userContext, Query query, Operation operation) {
        if (userContext.getRank().getInt() < Rank.ADMIN_INT && operation == Operation.READ) {
            List<Step> steps = query.getSteps();
            for (Step step : steps) {
                FieldConstraint newConstraint = query.createConstraint(query.createStepField(step, "owner"), FieldCompareConstraint.LIKE, DELETED + "%");
                query.setInverse(newConstraint, true);
                query.setCaseSensitive(newConstraint, true);
                constraints.add(newConstraint);
            }
        }
        return null;
    }
    /**
     * @since MMBase-1.9.6
     */
    @Override
    protected Authorization.QueryCheck addConstraints(List<Constraint> constraints, User userContext, Query query, Operation operation) {
        Authorization.QueryCheck sup = super.addConstraints(constraints, userContext, query, operation);
        if (sup != null) return sup;
        Authorization.QueryCheck wc = addWorkFlowConstraints(constraints, userContext, query, operation);
        if (wc != null) return wc;
        return null;
    }


    @Override
    public Set<String> getPossibleContexts(User user, MMObjectNode node)  throws org.mmbase.security.SecurityException {
        SortedSet set = new TreeSet<String>();
        for (String c : super.getPossibleContexts(user, node)) {
            set.add(c);
            set.add(DELETED + c);
        }
        return set;
    }






}
