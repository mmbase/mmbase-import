/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;

import org.mmbase.core.event.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.search.SearchQuery;
import org.mmbase.storage.search.Step;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This class provides a very basic release strategy. In fact it will release
 * the cache in nearly every instance, except when the event is for a step in
 * the <code>SearchQuery</code> that has 'nodes' set for it and the changed
 * node is not one of those. This release strategy is in fact the original one
 * and will allways be the first in the hierarchy of the
 * <code>ChainedReleaseStrategy</code>.
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id: BasicReleaseStrategy.java,v 1.12 2006-01-24 16:08:01 michiel Exp $
 */
public class BasicReleaseStrategy extends ReleaseStrategy {

    private static Logger log = Logging.getLoggerInstance(BasicReleaseStrategy.class);
	
    public BasicReleaseStrategy(){
    }

    public String getName() {
        return "Basic release strategy";
    }


    /* (non-Javadoc)
     * @see org.mmbase.cache.QueryResultCacheReleaseStrategy#getDescription()
     */
    public String getDescription() {
        return "This strategy does exactly what the original cache release "+
            "implementation did. It checks if the step of the query that maches the "+
            "type of the changed node has 'nodes' set. if it does, and the changed "+
            "node is not one of them, the query should not be flushed from cache";
    }

    /* (non-Javadoc)
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.module.core.NodeEvent, org.mmbase.storage.search.SearchQuery, java.util.List)
     */
    protected final boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult) {
        //this simple optimization only works for nodeEvents
        
        int shouldKeep = 0;
        List steps = getStepsForType(query, MMBase.getMMBase().getBuilder(event.getBuilderName()));
        Integer number = new Integer(event.getNodeNumber());
        for (Iterator i = steps.iterator(); i.hasNext();) {
            Step step = (Step) i.next();
            Set nodes = step.getNodes();
            //if one of the steps for this node event has no nodes set: flush
            if(nodes == null || nodes.size() == 0) return true;
            //if a step defines nodes, than it does not want to invalidate the cache-entry if the node of the event is not one of them.
            if(! nodes.contains(number)) {
                shouldKeep++; 
            }

        }
        //if not all steps of this type had nodes that did not include this one, flush
        return shouldKeep != steps.size(); //shouldrelease         
    }

    protected boolean doEvaluate(RelationEvent event, SearchQuery query, List cachedResult) {
        // no strategy for relation events
    	log.debug("basic strategy: flush: relation event");
        return true;
    }

}
