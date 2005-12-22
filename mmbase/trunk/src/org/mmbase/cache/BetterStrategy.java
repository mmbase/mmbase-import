/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mmbase.core.event.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.database.BasicSqlHandler;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @javadoc
 * @since MMBase 1.8
 * @author Ernst Bunders
 * @version $Id: BetterStrategy.java,v 1.12 2005-12-22 10:13:22 ernst Exp $
 */
public class BetterStrategy extends ReleaseStrategy {

    //public BetterStrategy() {}
    BasicSqlHandler sqlHandler = new BasicSqlHandler();
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

    protected boolean doEvaluate(RelationEvent event, SearchQuery query, List cachedResult) {
        return shouldRelease((RelationEvent) event, query);
    }

    /**
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.core.event.NodeEvent,
     * org.mmbase.storage.search.SearchQuery, java.util.List)
     * 
     * @return true if query should be released
     */
    protected boolean doEvaluate(NodeEvent event, SearchQuery query, List cachedResult) {
        log.debug(event.toString());
        return shouldRelease(event, query);
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
        if(getStepsForType(query, MMBase.getMMBase().getBuilder(event.getBuilderName()) ).size() == 0 ){
            logResult("no flush: type of event is not found in query path", query, event);
            return false;
        }
        
        //check if the step(s) matching this event's node type have 'nodes' set, and if so, check
        //if changed node is included
        if(checkNodesSet(event, query)){
            logResult("no flush: the query has nodes set and this event's node is not one of them", query, event);
            return false;
        }

        switch (event.getType()) {
            case NodeEvent.EVENT_TYPE_NEW:
                log.debug(">> node event type new");
                /*
                 * Put all the rules that apply for new node events
                 */

                // query has more than one step, all 'new node' events can be ignored, becouse this
                // node has no relations yet.
                if (query.getSteps().size() > 1){
                    logResult("no flush: 'new node' event in multistep query", query, event);
                    return false; // don't release
                }

                break;

            case NodeEvent.EVENT_TYPE_DELETE:
                log.debug(">> node event type delete");
                /*
                 * Put all rules here that apply to removed node events
                 */

                break;

            case NodeEvent.EVENT_TYPE_CHANGED:
                log.debug(">> node event type changed");
                /*
                * Put all rules here that apply to changede nodes
                */
                
                //if the changed field(s) do not occur in the fields or constraint section
                //of the query, it dous not have to be flushed
                if(! checkChangedFieldsMatch(event, query)){
                    logResult("no flush: the fields that have changed are not used in the querie", query, event);
                    return false;
                }
                
                //if the query is aggregating, and of type count, and the changed fields(s) do 
                //not occur in the constraint: don't flush the query
                if(checkAggregationCount(event, query)){
                    logResult("query is aggregating and fields are of type count, changed fields do not affect the query result", query, event);
                    return false;
                }
                
        }
        logResult("flush: no reason not to", query, event);
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
             logResult("no flush: query has one step and event is relation event", query, event);
             return false ;//don't release
         }
         
         // if a query has more steps that one and the event is a relation event
         // we check if the role of the relation is allso in the query.
         if (! checkPathMatches(event, query)){
             logResult("no flush: either source, destination or role dous not match to the query", query, event);
             return false;
         }

             
         switch (event.getType()) {
            case NodeEvent.EVENT_TYPE_NEW:
                log.debug(">> relation event type new");
                /*
                 * Put all rules here that apply to new relation events
                 */

                break;

            case NodeEvent.EVENT_TYPE_DELETE:
                log.debug(">> relation event type delete");
                /*
                 * Put all rules here that apply to removed relation events
                 */

                break;

            case NodeEvent.EVENT_TYPE_CHANGED:
                log.debug(">> relation event type changed");
                /*
                 * Put all rules here that apply to changed relation events
                 */
                
                //if the changed field(s) do not occur in the fields or constraint section
                //of the query, it dous not have to be flushed
                if(! checkChangedFieldsMatch(event.getNodeEvent(), query)) {
                    logResult("the changed (relation) fields do not match the fields or constraints of the query", query, event);
                    return false;
                }

                break;

        }
         logResult("flush: no reason not to", query, event);
        return true;
    }

    /**
	 * @param event
	 * @param query
	 * @return true if query is aggragating, of type count, and the changed fields do 
	 * not occur in the constraint (no flush)
	 */
	private boolean checkAggregationCount(NodeEvent event, SearchQuery query) {
		log.debug("method: checkAggregationCount()");
		if(!query.isAggregating()){
			return false;
		}
		//test if all changed fields are aggreagting and of type count, if not: return false;
		for(Iterator i = query.getFields().iterator();  i.hasNext(); ){
			StepField field = (StepField) i.next();
			if(event.getChangedFields().contains(field.getFieldName()) ){
				if( ! (field instanceof AggregatedField)) {
					return false;
				}
				if( ! (((AggregatedField)field).getAggregationType() == AggregatedField.AGGREGATION_TYPE_COUNT) ){
					return false;
				}
			}
		}
		//now check the constraints: if there are any constraints for any of the changed fields: false;
		Constraint constraint = query.getConstraint();
		if(constraint == null){
			return true;
		}
		for (Iterator i = event.getChangedFields().iterator(); i.hasNext();) {
			String fieldName = (String) i.next();
			if(getConstraintsForField(fieldName, event.getBuilderName(), constraint, query).size() > 0){
				return false;
			}
		}
		//all tests survived, query should not be flused
		return true;
	}

	/**
	 * @param event
	 * @param query
	 * @return true if sourcetype, role and destination from relation event match query
	 */
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
            
            //check this relation step
            String stepSource = step.getPrevious().getTableName();
            String stepDestination = step.getNext().getTableName();
            if (( stepSource.equals(event.getRelationSourceType()) && stepDestination.equals(event.getRelationDestinationType()) ) ||
                ( stepDestination.equals(event.getRelationSourceType()) && stepSource.equals(event.getRelationDestinationType()) )) {
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
            List constraintsForFieldList = getConstraintsForField(fieldName, event.getBuilderName(), query.getConstraint(), query); 
            if(constraintsForFieldList.size() > 0){
                constraintsFound = true;
                log.debug("matching constraint found: " + constraintsForFieldList.size());
                break search;
            }
            
            // then test the fields (only if no constraint match was found)
            for (Iterator fieldIterator = query.getFields().iterator(); fieldIterator.hasNext();) {
                StepField field = (StepField) fieldIterator.next();
                if (field.getFieldName().equals(fieldName)
                    && field.getStep().getTableName().equals(event.getBuilderName())) {
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
        List steps = getStepsForType(query, MMBase.getMMBase().getBuilder(event.getBuilderName()));
        for (Iterator i = steps.iterator(); i.hasNext();) {
            Step step = (Step) i.next();
            Set nodes = step.getNodes();
            if (nodes == null || nodes.size() == 0 || nodes.contains(new Integer(event.getNodeNumber()))) {
                //we're done. if one of the steps dous not meet one of the abouve conditions:
                return false;
            }
        }
        return true; 
    }
    
    private void logResult(String comment, SearchQuery query, Event event){
        if(log.isDebugEnabled()){
            String role="";
            // a small hack to limit the output
            if (event instanceof RelationEvent) {
                //get the role name
                RelationEvent revent = (RelationEvent) event;
                MMObjectNode relDef = MMBase.getMMBase().getBuilder("reldef").getNode(revent.getRole());
                role = " role: " + relDef.getStringValue("sname") + "/" + relDef.getStringValue("dname"); 
                //filter the 'object' events
                if (revent.getRelationSourceType().equals("object")
                        || revent.getRelationDestinationType().equals("object"))
                    return;
            }
            try {
                log.debug("\n******** \n**" + comment + "\n**" + event.toString() + role + "\n**"
                        + sqlHandler.toSql(query, sqlHandler) + "\n******");
            } catch (SearchQueryException e) {
                e.printStackTrace();
            }
        }
    }
}
