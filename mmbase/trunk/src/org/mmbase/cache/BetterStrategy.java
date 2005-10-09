/*
 * Created on 20-jul-2005 TODO To change the template for this generated file go
 * to Window - Preferences - Java - Code Style - Code Templates
 */
package org.mmbase.cache;

import java.util.Iterator;
import java.util.List;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @since MMBase 1.8
 * @author Ernst Bunders
 * @version $Id: BetterStrategy.java,v 1.5 2005-10-09 14:55:02 ernst Exp $
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

    /**
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.module.core.NodeEvent,
     *      org.mmbase.storage.search.SearchQuery, java.util.List)
     */
    protected boolean doEvaluate(NodeEvent event, SearchQuery query,
            List cachedResult) {

        if(event.getType() == NodeEvent.EVENT_TYPE_RELATION_CHANGED){
            return shouldRelease((NodeEvent)event, query);
        }else{
            return shouldRelease((RelationEvent)event, query);
        }
    }
    
    
    
    /**
     * Check all the rules that concern node events. if no rules match we return <code>true</code>.
     * @param event
     * @param query
     * @return
     */
    private boolean shouldRelease(NodeEvent event, SearchQuery query){
        switch (event.getType()) {
            default:
                //here are all the rules that are not specific to a type (or to more than one)
                
                // query has one step and the event is a relation event
                if (query.getSteps().size() == 1 && !(event instanceof RelationEvent)) {
                    return false ;//don't release
                }
            
            case NodeEvent.EVENT_TYPE_NEW:
                
                // query has more than one step, all 'new node' events can be ignored, becouse this node has no relations yet.
                if (query.getSteps().size() > 1) return false; //don't release
        
                break;
                
            case NodeEvent.EVENT_TYPE_DELETE:
                
                break;
                
            case NodeEvent.EVENT_TYPE_CHANGED:
                
                /* not finished
                 
                //if query is not aggregate, and the changed fields are not part of the select or where clouse of the query.
                if(! query.isAggregating()){
                    boolean shoudRelease = false;
                    //first check the steps
                    boolean fieldMatches = false;
                    for (Iterator i = getNodeSteps(query, event.getNode().getBuilder()).iterator(); i.hasNext();) {
                        Step step = (Step) i.next();
                        for (Iterator ii = getStepFields(query, step).iterator(); ii.hasNext();) {
                            StepField field = (StepField) ii.next();
                            if(event.hasChanged(field.getFieldName()))fieldMatches = true;
                        }
                    }
                    
                    //if fieldMatches is still false we check the constraints
                    if(! fieldMatches){
                        
                    }
                }
                */
                
                break;
                
  
        }
        return true;
    }
    
    /**
     * check all the rules that concern relation events. if no rules match we return <code>true</code>.
     * @param event
     * @param query
     * @return
     */
    private boolean shouldRelease(RelationEvent event, SearchQuery query){
        switch (event.getRelationEventType()) {
            default:
                //here are all the rules that are not specific to a type (or to more than one)
                
                // if a query has more steps that one and the event is a relation event
                // we check if the role of the relation is allso in the query.
                if (query.getSteps().size() > 1 && event instanceof RelationEvent) {
                    RelationEvent relEvent = (RelationEvent) event;
                    // iterate over all the relations steps and check if one matches
                    // source - destination - role
                    boolean shouldRelease = false;
                    for (Iterator i = getRelationSteps(query).iterator(); i.hasNext();) {
                        RelationStep step = (RelationStep) i.next();
                        if (step.getPrevious().getTableName().equals(relEvent.getRelationSourceType()) && 
                                step.getNext().getTableName().equals(relEvent.getRelationDestinationType())) {
                            if (step.getRole() == null || step.getRole().intValue() == relEvent.getRole()) {
                                //we found one relation that could match the one in the event
                                shouldRelease = true;
                            }
                        }
                    }
                    if(!shouldRelease) return false; //don't release

                }
                
            case NodeEvent.EVENT_TYPE_NEW:
                
                break;
                
            case NodeEvent.EVENT_TYPE_DELETE:
                
                break;
                
            case NodeEvent.EVENT_TYPE_CHANGED:
                
                break;
                

        }
        return true;
    }

}
