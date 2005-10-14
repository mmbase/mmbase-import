/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.List;
import java.util.Set;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.storage.search.SearchQuery;
import org.mmbase.storage.search.Step;


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
 * @version $Id: BasicReleaseStrategy.java,v 1.6 2005-10-14 21:44:03 michiel Exp $
 */
public class BasicReleaseStrategy extends ReleaseStrategy {


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
    protected boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult) {
        //this simple optimization only works for nodeEvents
        boolean shouldRelease = true;
        if(event.getType() != NodeEvent.EVENT_TYPE_RELATION_CHANGED){
            Step thisStep = getStepForEvent(event, query);
            if(thisStep != null){
                Set nodes = thisStep.getNodes();
                if(nodes != null && nodes.size() > 0 && ! nodes.contains(new Integer(event.getNode().getNumber()))) {
                    shouldRelease = false;
                }
            }
        }
        return shouldRelease;
    }

}
