/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.cache;

import java.util.*;

import org.mmbase.bridge.util.Queries;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 * @since MMBase 1.8
 * @author Ernst Bunders
 * @version $Id: BetterStrategy.java,v 1.8 2005-10-31 19:53:49 ernst Exp $
 */
public class BetterStrategy extends ReleaseStrategy {

    public BetterStrategy() {}

    private static Logger log = Logging.getLoggerInstance(BetterStrategy.class);

    // inheritdoc
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
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.core.event.NodeEvent,
     * org.mmbase.storage.search.SearchQuery, java.util.List)
     * 
     * @return true if query should be released
     */
    protected boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult) {
        log.debug(event.toString());
        if (event instanceof RelationEvent) {
            return shouldRelease((RelationEvent) event, query);
        } else {
            return shouldRelease(event, query);
        }
    }

    /**
     * Check all the rules that concern node events. if no rules match we return <code>true</code>.
     * @param event
     * @param query
     * @return
     */
    private boolean shouldRelease(NodeEvent event, SearchQuery query) {
        /*
         * Here are all the preconditions that must be met to proceed. Basic checks to determin
         * if this event has to be evaluated on this query at all
         */
        
        //check if this event matches the path of the query
        if(getStepsForType(query, event.getNode().getBuilder()).size() == 0 ){
            log.debug("no flush: type of event is not found in query path");
            return false;
        }
        
        //check if the step(s) matching this event's node type have 'nodes' set, and if so, check
        //if changed node is included
        if(checkNodesSet(event, query)){
            log.debug("no flush: the query has nodes set and this event's node is not one of them");
            return false;
        }

        switch (event.getType()) {
            case NodeEvent.EVENT_TYPE_NEW:
                log.debug("node event type new");
                /*
                 * Put all the rules that apply for new node events
                 */

                // query has more than one step, all 'new node' events can be ignored, becouse this
                // node has no relations yet.
                if (query.getSteps().size() > 1) return false; // don't release

                break;

            case NodeEvent.EVENT_TYPE_DELETE:
                log.debug("node event type delete");
                /*
                 * Put all rules here that apply to removed node events
                 */

                break;

            case NodeEvent.EVENT_TYPE_CHANGED:
                log.debug("node event type changed");
                /*
                * Put all rules here that apply to changede nodes
                */
                
                //if the changed field(s) do not occur in the fields or constraint secion
                //of the query, it dous not have to be flushed
                if(! checkChangedFieldsMatch(event, query)) return false;
                
        }
        return true;
    }

    /**
     * check all the rules that concern relation events. if no rules match we return
     * <code>true</code>.
     * @param event
     * @param query
     * @return
     */
    private boolean shouldRelease(RelationEvent event, SearchQuery query) {

        /*
         * Here are all the preconditions that must be met to proceed. Basic checks to determin
         * if this event has to be evaluated on this query at all
         */

         //query has one step and the event is a relation event
         if (query.getSteps().size() == 1 ){
             log.debug("no flush: query has one step and event is relation event");
             return false ;//don't release
         }
         
         // if a query has more steps that one and the event is a relation event
         // we check if the role of the relation is allso in the query.
         if (! checkPathMatches(event, query)){
             log.debug("no flush: either source, destination or role dous not match to the query");
             return false;
         }

             
         switch (event.getRelationEventType()) {
            case NodeEvent.EVENT_TYPE_NEW:
                log.debug("relation event type new");
                /*
                 * Put all rules here that apply to new relation events
                 */

                break;

            case NodeEvent.EVENT_TYPE_DELETE:
                log.debug("relation event type delete");
                /*
                 * Put all rules here that apply to removed relation events
                 */

                break;

            case NodeEvent.EVENT_TYPE_CHANGED:
                log.debug("relation event type changed");
                /*
                 * Put all rules here that apply to changed relation events
                 */

                break;

        }
        return true;
    }

    private boolean checkPathMatches(RelationEvent event, SearchQuery query){
        // check if the path in the query maches the relation event:
        // - the source and destination objects should be there
        // - the role either matches or is not specified 
        log.debug("method: checkPathMatches()");
        log.debug(event.toString());
        log.debug("query: "+query.toString());
        boolean match = false;
        for (Iterator i = getRelationSteps(query).iterator(); i.hasNext();) {
            RelationStep step = (RelationStep) i.next();
            if (step.getPrevious().getTableName().equals(event.getRelationSourceType())
                && step.getNext().getTableName().equals(event.getRelationDestinationType())) {
                if (step.getRole() == null || step.getRole().intValue() == event.getRole()) 
                    match = true;
            }
        }
        return match;
    }
    
 
    
    
    
    
    /**
     * Checks if a query object contains reference to (one of) the changed field(s).
     * Matches are looked for in the stepfields and in the constraints.
     * @param event
     * @param query
     * @return true if the type of the node for this event matches either a stepfield or a constriant
     */
    private boolean checkChangedFieldsMatch(NodeEvent event, SearchQuery query){
        log.debug("method: checkChangedFieldsMatch(). changed fields: " + event.getChangedFields().size());
        boolean constraintsFound = false;
        boolean fieldsFound = false;
        search:
        for (Iterator i = event.getChangedFields().iterator(); i.hasNext();) {
            String fieldName = (String) i.next();
            //first test the constraints
            String builderName =event.getNode().getBuilder().getTableName();
            List constraintsForFieldList = getConstraintsForField(fieldName, builderName, query.getConstraint(), query); 
            if(constraintsForFieldList.size() > 0){
                constraintsFound = true;
                log.debug("matching constraint found: " + constraintsForFieldList.size());
                break search;
            }
            
            // then test the fields (only if no constraint match was found)
            for (Iterator fieldIterator = query.getFields().iterator(); fieldIterator.hasNext();) {
                StepField field = (StepField) fieldIterator.next();
                if (field.getFieldName().equals(fieldName)
                    && field.getStep().getTableName().equals(event.getNode().getBuilder().getTableName())) {
                    fieldsFound = true;
                    if(log.isDebugEnabled())log.debug("matching field found: " + field.getStep().getTableName() + "." + field.getFieldName());
                    break search;
                }
            }
        }
        if(log.isDebugEnabled()){
            String logMsg ="";
            if(!fieldsFound)logMsg = "no matching fields found, ";
            if(!constraintsFound)logMsg = logMsg + "  no matching constraints found";
            log.debug(logMsg);
        }
        //now test the result
        return (fieldsFound || constraintsFound);
    }
    
    /**
     * This method investigates all the steps of a query that correspond to the nodetype of the 
     * node event. for each step a check is made if this step has 'nodes' set, and so, if the changed 
     * node is one of them.
     * @param event a NodeEvent
     * @param query
     * @return true if (all) the step(s) matching this event have nodes set, and non of these 
     * match the number of the changed node (in which case the query should not be flused)
     */
    private boolean checkNodesSet(NodeEvent event, SearchQuery query){
        //this simple optimization only works for nodeEvents
        List steps = getStepsForType(query, event.getNode().getBuilder());
        for (Iterator i = steps.iterator(); i.hasNext();) {
            Step step = (Step) i.next();
            Set nodes = step.getNodes();
            if (nodes == null || nodes.size() == 0 || nodes.contains(new Integer(event.getNode().getNumber()))) {
                //we're done. if one of the steps dous not meet one of the abouve conditions:
                return false;
            }
        }
        return true; 
    }
}
