/*
 * Created on 20-jul-2005 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package org.mmbase.cache;

import java.util.Iterator;
import java.util.List;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.storage.search.RelationStep;
import org.mmbase.storage.search.SearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @since MMBase 1.8
 * @author Ernst Bunders
 * @version $Id: BetterStrategy.java,v 1.4 2005-09-23 13:59:26 pierre Exp $
 */
public class BetterStrategy extends ReleaseStrategy {

    public BetterStrategy() {
        super("better strategy");
    }

    private static Logger log = Logging.getLoggerInstance(BetterStrategy.class);

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#getName()
     */
    public String getName() {
        return "Better Release Strategy";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#getDescription()
     */
    public String getDescription() {
        return "This is work in progress. It does some basic checking. If a query has "
            + "more than one step, all 'new' events can be ignored, becouse a new node has no relations yet. "
            + "If a query has one step, all relation changed events can be ignored. "
            + "if a relation event concerns a role that is not part of this query, the event can be ignored.";
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.module.core.NodeEvent,
     *      org.mmbase.storage.search.SearchQuery, java.util.List)
     */
    protected boolean doEvaluate(NodeEvent event, SearchQuery query,
            List cachedResult) {
        boolean shouldRelease = true;

        // first lets test if the query has one step and the event is a relation
        // event
        if (query.getSteps().size() == 1 && !(event instanceof RelationEvent)) {
            shouldRelease = false;
        }

        // now if a query has more than one step, all 'new node' events can be
        // ignored, becouse this node has no relations yet, and thous can not
        // be part of the query.
        if (shouldRelease && query.getSteps().size() > 1) {
            if (event.getType() == NodeEvent.EVENT_TYPE_NEW) {
                shouldRelease = false;
            }
        }

        // if a query has more steps that one and the event is a relation event
        // we check if the role of the relation is allso in the query.
        // The role need not allways be defined in the query. if an undefined
        // role exists
        // between the source and the target of the relation event, this
        // optimization
        // can not be done
        if (shouldRelease && query.getSteps().size() > 1
            && event instanceof RelationEvent) {
            RelationEvent relEvent = (RelationEvent) event;
            boolean invalidate = false;

            // iterate over all the relations steps and check if one matches
            // source - destination - role
            for (Iterator i = getRelationSteps(query).iterator(); i.hasNext();) {
                RelationStep step = (RelationStep) i.next();
                if (step.getPrevious().getTableName().equals(
                    relEvent.getRelationSourceType())
                    && step.getNext().getTableName().equals(
                        relEvent.getRelationDestinationType())) {
                    // wow! did we do that??

                    if (step.getRole() != null
                        && step.getRole().intValue() == relEvent.getRole()) {
                        invalidate = true;
                    } else if (step.getRole() == null) {
                        // alas! this could be hour role (but we don't know :( )
                        invalidate = true;
                    }

                    if (invalidate) break;
                }
            }

            if (invalidate) shouldRelease = true;
        }
        return shouldRelease;
    }

}
