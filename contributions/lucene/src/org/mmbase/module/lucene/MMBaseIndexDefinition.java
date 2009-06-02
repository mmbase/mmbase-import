/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.util.xml.query.*;
import org.mmbase.storage.search.*;
import org.mmbase.cache.*;
import org.mmbase.core.event.*;
import org.mmbase.util.logging.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;

/**
 * An MMBase Lucene Index is configured by an MMBase Queries, and 'supbqueries' thereof. Also its
 * fields can have extra attributes specific to Lucene searching.
 *
 * @author Pierre van Rooden
 * @version $Id$
 **/
class MMBaseIndexDefinition extends QueryDefinition implements IndexDefinition {
    static private final Logger log = Logging.getLoggerInstance(MMBaseIndexDefinition.class);
    /**
     * The default maximum number of nodes that are returned by a call to the searchqueryhandler.
     */
    public static final int MAX_NODES_IN_QUERY = 200;

    /**
     * The maximum number of nodes that are returned by a call to the searchqueryhandler.
     */
    protected int maxNodesInQuery = MAX_NODES_IN_QUERY;

    /**
     * Subqueries for this index. The subqueries are lists whose starting element is the element node from the
     * current index result.
     */
    protected final List<IndexDefinition> subQueries = new ArrayList<IndexDefinition>();

    protected Analyzer analyzer;

    protected String id;

    // not configurable for these kind of indices.
    protected final List<String> identifierFields = Collections.unmodifiableList(new ArrayList<String>(Arrays.asList("number")));

    private final Map<String, Float> boosts = new HashMap<String, Float>();


    private final ChainedReleaseStrategy releaseStrategy = new ChainedReleaseStrategy();


    MMBaseIndexDefinition() {
    }

    public ChainedReleaseStrategy getReleaseStrategy() {
        return releaseStrategy;
    }

    public void setId(String i) {
        id = i;
    }
    public String getId() {
        return id;
    }
    public void setAnalyzer(Analyzer a) {
        analyzer = a;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public Node getNode(Cloud userCloud, Document doc) {
        String identifier = doc.get("number");
        if (userCloud.hasNode(identifier)) {
            if (log.isTraceEnabled()) {
                log.trace("a node (" + identifier + ") " + "'" + doc.get("builder") + "'");
            }
            if (userCloud.mayRead(identifier)) {
                return userCloud.getNode(identifier);
            } else {
                log.warn("Found a unreadable node '" + identifier + "' (" + userCloud.getUser() + "), returning empty map");
                return new MapNode(Collections.EMPTY_MAP);
            }
        } else {
            log.warn("Found a unknown node, returning empty map");
            return new MapNode(Collections.EMPTY_MAP);
        }

    }
    public List<String> getIdentifierFields() {
        return identifierFields;
    }

    public boolean inIndex(String identifier) {
        Cloud cloud = query.getCloud();
        if (! cloud.hasNode(identifier)) {
            log.debug("No such node " + identifier + " (" + getId() + ")");
            return false;
        }
        Node node = cloud.getNode(identifier);
        NodeEvent pseudoEvent = new NodeEvent(null, node.getNodeManager().getName(), node.getNumber(), Collections.EMPTY_MAP,
                                              new NodeMap(node), Event.TYPE_CHANGE);
        boolean result =  releaseStrategy.evaluate(pseudoEvent, query, null).shouldRelease();
        log.debug("Node " + identifier + (result ? " IS " : " IS NOT ") + "in index " + getId() + " according to " + releaseStrategy);
        return result;
    }
    /**
     * Converts an MMBase Node Iterator to an Iterator of IndexEntry-s.
     */
    protected CloseableIterator<MMBaseEntry> getCursor(final NodeIterator nodeIterator,
                                                       final Collection<? extends FieldDefinition> f) {
        return new CloseableIterator<MMBaseEntry>() {
            int i = 0;
            public boolean hasNext() {
                return nodeIterator != null && nodeIterator.hasNext();
            }
            public void remove() {
                nodeIterator.remove();
            }
            public MMBaseEntry next() {
                Node node = nodeIterator.nextNode();
                MMBaseEntry entry = new MMBaseEntry(node, (Collection<IndexFieldDefinition>) f, isMultiLevel,
                                                    elementManager, elementStep, subQueries);
                i++;
                if (log.isServiceEnabled()) {
                    if (i % 100 == 0) {
                        log.service("mmbase cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                    } else if (log.isDebugEnabled()) {
                        log.trace("mmbase cursor " + i + " (now at id=" + entry.getIdentifier() + ")");
                    }
                }
                return entry;
            }
            public void close() {
                // no need for closing
            }
        };
    }

    public CloseableIterator<MMBaseEntry> getCursor() {
        return getCursor(getNodeIterator((String) null), fields);
    }

    public CloseableIterator<MMBaseEntry> getSubCursor(String identifier) {
        return getCursor(getNodeIterator(identifier), fields);
    }

    /**
     * Creates an (Huge)NodeListIterator for this index definition
     * @param id A node number. If used, the query will be limited. This is used to update the index on change of that node.
     * @return the query result as a NodeIterator object
     */
    protected NodeIterator getNodeIterator(final String id) {
        try {
            Query q = query.clone();
            String elementNumberFieldName = "number";
            if (isMultiLevel) {
                elementNumberFieldName = elementManager.getName() + ".number";
            }
            if (id != null) {
                Integer number = Integer.valueOf(id);
                Constraint comp = null;
                if (query.getCloud().hasNode(number.intValue())) {
                    Node node = query.getCloud().getNode(number.intValue());
                    NodeManager nm = node.getNodeManager();
                    //for (Step step : q.getSteps()) {
                    for (Iterator i = q.getSteps().iterator(); i.hasNext();) {
                        Step step = (Step) i.next();
                        NodeManager stepManager = query.getCloud().getNodeManager(step.getTableName());
                        if (stepManager.equals(nm) || stepManager.getDescendants().contains(nm)) {
                            StepField numberField = q.createStepField(step, "number");
                            Constraint constraint = q.createConstraint(numberField, number);
                            comp = q.createConstraint(comp, CompositeConstraint.LOGICAL_OR, constraint);
                        }
                    }
                }
                if (comp == null) return BridgeCollections.EMPTY_NODELIST.nodeIterator();
                Queries.addConstraint(q, comp);

            }
            StepField elementNumberField = q.createStepField(elementNumberFieldName);
            q.addSortOrder(elementNumberField, SortOrder.ORDER_DESCENDING); // this sort order makes it possible to filter out duplicates.
            if (log.isTraceEnabled()) {
                log.trace("Query for node '" + id + "': " + q.toSql());
            }
            return new HugeNodeListIterator(q, maxNodesInQuery);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString() + fields + "SUB[" + subQueries + "] releasestrategy: " + releaseStrategy;
    }

}

