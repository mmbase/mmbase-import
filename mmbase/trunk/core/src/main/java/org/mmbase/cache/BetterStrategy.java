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
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.database.BasicSqlHandler;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This release strategy is a bit better than 'BasicReleaseStrategy, and also a bit more sophisticated.
 *
 * @since MMBase 1.8
 * @author Ernst Bunders
 * @version $Id$
 */
public class BetterStrategy extends ReleaseStrategy {

    //public BetterStrategy() {}
    private static final BasicSqlHandler sqlHandler = new BasicSqlHandler();
    private static final Logger log = Logging.getLoggerInstance(BetterStrategy.class);


    private static final Logger nodeEventLog = Logging.getLoggerInstance(BetterStrategy.class.getName() + ".nodeevent");
    private static final Logger relationEventLog = Logging.getLoggerInstance(BetterStrategy.class.getName() + ".relationevent");

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
        return "This strategy performs all kinds of checks to test if the node or relation event actually matches the query. " +
            "For node events the type is checked, as well as some other things. For relation events the type is checked as well as " +
            "the source and destination. Then there are some other things like: 'new node events should not flush queries with " +
            "more than one step, because they have no relation yet'. It also checks if a certain change in a node actually can affect the " +
            "outcome of a query.";
    }

    protected boolean doEvaluate(RelationEvent event, SearchQuery query, List<MMObjectNode> cachedResult) {
        return shouldRelease(event, query);
    }

    /**
     * @see org.mmbase.cache.ReleaseStrategy#doEvaluate(org.mmbase.core.event.NodeEvent,

     * org.mmbase.storage.search.SearchQuery, java.util.List)
     *
     * @return true if query should be released
     */
    protected final boolean doEvaluate(NodeEvent event, SearchQuery query, List<MMObjectNode> cachedResult) {
        if (log.isDebugEnabled()) {
            log.debug(event.toString());
        }
        return shouldRelease(event, query);
    }

    /**
     * Check all the rules that concern node events. if no rules match we return <code>true</code>.
     * @param event
     * @param query
     * @return
     */
    private boolean shouldRelease(NodeEvent event, SearchQuery query) {
        switch (event.getType()) {
        case Event.TYPE_NEW:
            // query has more than one step, all 'new node' events can be ignored, because this
            // node has no relations yet.
            if (query.getSteps().size() > 1) {
                logResult("no flush: 'new node' event in multistep query", query, event);
                return false; // don't release
            }
            if(! checkSteps(event, query)) {
                logResult("no flush: the query has nodes set and this event's node is not one of them, or this step has no steps of corresponding type", query, event);
                return false;
            }
            break;

        case Event.TYPE_DELETE:
            if(! checkSteps(event, query)) {
                logResult("no flush: the query has nodes set and this event's node is not one of them, or this step has no steps of corresponding type", query, event);
                return false;
            }
            break;

        case Event.TYPE_CHANGE:
            if(! checkSteps(event, query)) {
                logResult("no flush: the query has nodes set and this event's node is not one of them, or this step has no steps of corresponding type", query, event);
                return false;
            }
            //if the changed field(s) do not occur in the fields or constraint section
            //of the query, it does not have to be flushed
            if(! checkChangedFieldsMatch(event, query)) {
                logResult("no flush: the fields that have changed are not used in the query", query, event);
                return false;
            }

            //if the query is aggregating, and of type count, and the changed fields(s) do
            //not occur in the constraint: don't flush the query
            if(checkAggregationCount(event, query)) {
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
             logResult("no flush: either source, destination or role does not match to the query", query, event);
             return false;
         }


         switch (event.getType()) {
         case Event.TYPE_NEW:
             log.debug(">> relation event type new");
             /*
              * Put all rules here that apply to new relation events
              */

             break;

         case Event.TYPE_DELETE:
             log.debug(">> relation event type delete");
             /*
              * Put all rules here that apply to removed relation events
              */

             break;

         case Event.TYPE_CHANGE:
             log.debug(">> relation event type changed");
             /*
              * Put all rules here that apply to changed relation events
              */

             //if the changed field(s) do not occur in the fields or constraint section
             //of the query, it does not have to be flushed
             if(! checkChangedFieldsMatch(event.getNodeEvent(), query)) {
                 logResult("no flush: the changed relation fields do not match the fields or constraints of the query", query, event);
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
        for (StepField field : query.getFields()) {
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
        MMObjectBuilder eventBuilder = MMBase.getMMBase().getBuilder(event.getBuilderName());
        for (String fieldName : event.getChangedFields()) {
            if(getConstraintsForField(fieldName, eventBuilder, constraint, query).size() > 0){
                return false;
            }
        }
        //all tests survived, query should not be flushed
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
        if (log.isDebugEnabled()) {
            log.debug("method: checkPathMatches()");
            log.debug(event.toString());
            log.debug("query: " + query.toString());
        }
        MMBase mmb = MMBase.getMMBase();
        String          eventSourceType = event.getRelationSourceType();
        String          eventDestType   = event.getRelationDestinationType();
        MMObjectBuilder eventSource     = mmb.getBuilder(eventSourceType);
        MMObjectBuilder eventDest       = mmb.getBuilder(eventDestType);


        Iterator<Step> i = query.getSteps().iterator();
        Step prevStep = i.next();
        String stepDest = prevStep.getTableName();
        while (i.hasNext()) {
            String stepSource = stepDest;
            RelationStep step = (RelationStep) i.next();
            Step nextStep = i.next();
            stepDest = nextStep.getTableName();
            //when source or destination are null (no active builders), this event can not affect the cache of this mmbase app
            boolean matchesProper = (eventSource != null && eventDest != null)
                    && (eventSourceType.equals(stepSource) || eventSource.isExtensionOf(mmb.getBuilder(stepSource)))
                    && (eventDestType.equals(stepDest) || eventDest.isExtensionOf(mmb.getBuilder(stepDest)));
            boolean matches = matchesProper || ( // matchesInverse
                    (eventSource != null && eventDest != null)
                            && (eventDestType.equals(stepSource) || eventDest.isExtensionOf(mmb.getBuilder(stepSource)))
                            && (eventSourceType.equals(stepDest) || eventSource.isExtensionOf(mmb.getBuilder(stepDest))));


            Integer role = step.getRole();
            if (matches &&
                (role == null || role.intValue() == event.getRole())) {
                return true;
            }
        }
        return false;
    }






    /**
     * Checks if a query object contains reference to (one of) the changed field(s).
     * Matches are looked for in the stepfields and in the constraints.
     * @param event
     * @param query
     * @return true if the type of the node for this event matches either a stepfield or a constriant
     */
    private boolean checkChangedFieldsMatch(NodeEvent event, SearchQuery query){
        if (log.isDebugEnabled()) {
            log.debug("method: checkChangedFieldsMatch(). changed fields: " + event.getChangedFields().size());
        }
        boolean constraintsFound = false;
        boolean fieldsFound = false;
        boolean sortordersFound = false;
        String eventBuilderName = event.getBuilderName();
        MMBase mmb = MMBase.getMMBase();
        MMObjectBuilder eventBuilder = mmb.getBuilder(eventBuilderName);
        search:
        for (String fieldName : event.getChangedFields()) {
            //first test the constraints
            List<Constraint> constraintsForFieldList = getConstraintsForField(fieldName, eventBuilder, query.getConstraint(), query);
            if(constraintsForFieldList.size() > 0) {
                constraintsFound = true;
                if (log.isDebugEnabled()) {
                    log.debug("matching constraint found: " + constraintsForFieldList.size());
                }
                break search;
            }

            for (StepField field : query.getFields()) {
                if (field.getFieldName().equals(fieldName)
                    && (field.getStep().getTableName().equals(eventBuilderName) ||
                        eventBuilder.isExtensionOf(mmb.getBuilder(field.getStep().getTableName())))
                        ) {
                    fieldsFound = true;
                    if(log.isDebugEnabled()) {
                        log.debug("matching field found: " + field.getStep().getTableName() + "." + field.getFieldName());
                    }
                    break search;
                }
            }

            //test the sortorders
            List<SortOrder> sortordersForFieldList = getSortordersForField(fieldName, eventBuilder, query.getSortOrders(), query);
            if(sortordersForFieldList.size() > 0) {
                sortordersFound = true;
                if (log.isDebugEnabled()) {
                    log.debug("matching sortorders found: " + sortordersForFieldList.size());
                }
                break search;
            }
        }
        if(log.isDebugEnabled()){
            String logMsg ="";
            if (!sortordersFound) logMsg = logMsg + "  no matching sortorders found";
            if (!fieldsFound) logMsg = "no matching fields found, ";
            if (!constraintsFound) logMsg = logMsg + "  no matching constraints found";
            log.debug(logMsg);
        }
        //now test the result
        return sortordersFound || fieldsFound || constraintsFound;
    }

    /**
     * This method investigates all the steps of a query that correspond to the nodetype of the
     * node event. for each step a check is made if this step has 'nodes' set, and so, if the changed
     * node is one of them.
     *
     * Also it checks if the step is of a corresponding type. It returns also false if no step
     * matched the type of the node event.
     * @param event a NodeEvent
     * @param query
     * @return true if (all) the step(s) matching this event have nodes set, and non of these
     * match the number of the changed node (in which case the query should not be flused)
     */
    private final boolean checkSteps(NodeEvent event, SearchQuery query) {
        //this simple optimization only works for nodeEvents
        MMBase mmb = MMBase.getMMBase();
        String eventTable = event.getBuilderName();
        MMObjectBuilder eventBuilder = mmb.getBuilder(eventTable);
        //perhaps the builder of the event is locally inactive
        if(eventBuilder != null){
            for (Step step : query.getSteps()) {
                String table = step.getTableName();
                if (! (table.equals(eventTable) ||
                       eventBuilder.isExtensionOf(mmb.getBuilder(table)))) continue;
                Set<Integer> nodes = step.getNodes();
                if (nodes == null || nodes.size() == 0 ||  nodes.contains(event.getNodeNumber())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void logResult(String comment, SearchQuery query, Event event) {
        if(log.isDebugEnabled() || nodeEventLog.isDebugEnabled() || relationEventLog.isDebugEnabled()){
            String role = "";
             Logger logger;
             if (event instanceof RelationEvent) {
                 logger = relationEventLog;
             } else if (event instanceof NodeEvent) {
                 logger = nodeEventLog;
             } else {
                 logger = log;
             }

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
                logger.debug("\n******** \n**" + comment + "\n**" + event.toString() + role + "\n**" + sqlHandler.toSql(query, sqlHandler) + "\n******");
            } catch (SearchQueryException e) {
                logger.warn(e);
            }
        }
    }
}
