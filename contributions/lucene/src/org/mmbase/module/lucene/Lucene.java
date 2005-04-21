/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.w3c.dom.*;

import org.mmbase.bridge.BridgeException;
import org.mmbase.bridge.util.Queries;
import org.mmbase.module.Module;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.*;
import org.mmbase.util.Queue;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: Lucene.java,v 1.8 2005-04-21 07:11:41 pierre Exp $
 **/
public class Lucene extends Module implements MMBaseObserver {

    /** Public ID of the Lucene config DTD version 1.0 */
    public static final String PUBLIC_ID_LUCENE_1_0 = "-//MMBase//DTD luceneindex config 1.0//EN";
    /** DTD resource filename of the Lucene config DTD version 1.0 */
    public static final String DTD_LUCENE_1_0 = "luceneindex_1_0.dtd";

    /** Public ID of the most recent Lucene config DTD */
    public static final String PUBLIC_ID_LUCENE = PUBLIC_ID_LUCENE_1_0;
    /** DTD repource filename of the most recent Lucene config DTD */
    public static final String DTD_LUCENE = DTD_LUCENE_1_0;

    private static long INITIAL_WAIT_TIME = 5 * 60 * 1000; // initial wait time after startup, default 5 minutes
    private static long WAIT_TIME = 5 * 1000; // wait time bewteen individual checks, default 5 seconds

    private long initialWaitTime = INITIAL_WAIT_TIME;

    private static final Logger log = Logging.getLoggerInstance(Lucene.class);

    MMBase mmbase;

    private String luceneIndexPath = "WEB-INF/data/lucene";
    private String configFile = "utils/luceneindex.xml";

    private Scheduler scheduler;
    private String defaultIndex = null;
    private Map indexerMap = new HashMap();
    private Map searcherMap = new HashMap();
    private boolean readOnly = false;

    /**
     * This function starts a full Index of Lucene.
     * This may take a while.
     * This function can be called through the function framework.
     */
    protected Function fullIndexFunction = new AbstractFunction("fullIndex", Parameter.EMPTY, ReturnType.VOID) {
        public Object getFunctionValue(Parameters arguments) {
            scheduler.fullIndex();
            return null;
        }
    };

    /**
     * This function starts a search for a given string.
     * This function can be called through the function framework.
     */
     protected Function searchFunction = new AbstractFunction("search",
                              new Parameter[] { new Parameter("value",String.class),
                                                new Parameter("index",String.class),
                                                new Parameter("offset",Integer.class),
                                                new Parameter("max",Integer.class),
                                                new Parameter("extraconstraints",String.class),
                                                Parameter.CLOUD },
                              ReturnType.LIST) {
        public Object getFunctionValue(Parameters arguments) {
            String value = arguments.getString("value");
            String index = arguments.getString("index");
            // offset
            int offset = 0;
            Integer offsetParameter = (Integer)arguments.get("offset");
            if (offsetParameter != null) offset = offsetParameter.intValue();
            if (offset < 0) offset = 0;
            // max
            int max = -1;
            Integer maxParameter = (Integer)arguments.get("max");
            if (maxParameter != null) max = maxParameter.intValue();
            String extraConstraints = arguments.getString("extraconstraints");
            return search(value, index, extraConstraints, offset, max);
        }
    };

    /**
     * This function starts a search fro a given string.
     * This function can be called through the function framework.
     */
     protected Function searchSizeFunction = new AbstractFunction("searchsize",
                              new Parameter[] { new Parameter("value",String.class),
                                                new Parameter("index",String.class),
                                                new Parameter("extraconstraints",String.class),
                                                Parameter.CLOUD },
                              ReturnType.INTEGER) {
        public Object getFunctionValue(Parameters arguments) {
            String value = arguments.getString("value");
            String index = arguments.getString("index");
            String extraConstraints = arguments.getString("extraconstraints");
            return new Integer(searchSize(value, index, extraConstraints));
        }
    };

    public void init() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_LUCENE, DTD_LUCENE, Lucene.class);
        super.init();
        mmbase = MMBase.getMMBase();
        String fullIndexPath = MMBaseContext.getServletContext().getRealPath(luceneIndexPath);
        readOnly = "true".equals(getInitParameter("readonly"));

        String time = getInitParameter("initialwaittime");
        if (time != null) {
            try {
                initialWaitTime = Long.parseLong(time);
                log.debug("Set initial wait time to "+time+" milliseconds");
            } catch (NumberFormatException nfe) {
                log.warn("Invalid value '"+time+"' for property 'initialwaittime'");
            }
        }

        readConfiguration(fullIndexPath);
        addFunction(searchFunction);
        addFunction(searchSizeFunction);
        if (!readOnly) {
            addFunction(fullIndexFunction);
            scheduler = new Scheduler();
            log.info("Module Lucene started");
            // full index ?
            String fias = getInitParameter("fullindexatstartup");
            if (initialWaitTime <= 0 || "true".equals(fias)) {
                scheduler.fullIndex();
            }
        }
    }

    public String getModuleInfo() {
        return "This module performs lucene searches and maintains indices";
    }

    private void OptimizeBuilderConfig(Map buildersToIndex) {
        for (Iterator i = buildersToIndex.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            String builderName = (String)entry.getKey();
            Map builderProperties = (Map)entry.getValue();
            Set fieldSet = (Set)builderProperties.get("fieldset");
            MMObjectBuilder builder = mmbase.getBuilder(builderName);
            // merge all field sets of parentbuilders.
            builder = builder.getParentBuilder();
            while (builder!=null) {
                Map parentProperties = (Map)buildersToIndex.get(builder.getTableName());
                if (parentProperties != null) {
                    // merge fieldset, so a node can be indexed for all the rigth fields
                    // in one go
                    Set parentSet = (Set)parentProperties.get("fieldset");
                    fieldSet.addAll(parentSet);
                    // mark this builder as a specialization index builder
                    // specialization index builders are skipped during full index
                    builderProperties.put("specialization",Boolean.TRUE);
                }
                builder = builder.getParentBuilder();
            }
        }
    }

    protected List getFields(NodeList fieldElements, Indexer.QueryDefinition queryDefinition, Set allIndexedFieldsSet,
                        boolean storeText, boolean mergeText) {
        List fieldList = new ArrayList();
        queryDefinition.fields = new ArrayList();
        for (int k = 0; k < fieldElements.getLength(); k++) {
            Element fieldElement = (Element) fieldElements.item(k);
            if (fieldElement.hasAttribute("name")) {
                String fieldName = fieldElement.getAttribute("name");
                fieldList.add(fieldName);

                Indexer.FieldDefinition fieldDefinition = new Indexer.FieldDefinition();
                fieldDefinition.fieldName = fieldName;
                if (fieldElement.hasAttribute("keyword")) {
                    fieldDefinition.keyWord = "true".equals(fieldElement.getAttribute("keyword"));
                } else {
                    FieldDefs field = queryDefinition.builderResolver.getField(fieldName);
                    int type = field.getDBType();
                    fieldDefinition.keyWord =
                        type == FieldDefs.TYPE_DATETIME || type == FieldDefs.TYPE_BOOLEAN ||
                        type == FieldDefs.TYPE_INTEGER || type == FieldDefs.TYPE_LONG ||
                        type == FieldDefs.TYPE_DOUBLE || type == FieldDefs.TYPE_FLOAT;
                }
                String alias = null;
                if (fieldElement.hasAttribute("alias")) {
                    alias = fieldElement.getAttribute("alias");
                } else if (mergeText && !fieldDefinition.keyWord) {
                    alias = "fulltext";
                }
                if (alias != null) {
                    fieldDefinition.alias = alias;
                    if (!fieldDefinition.keyWord) {
                        allIndexedFieldsSet.add(alias);
                    }
                } else if (!fieldDefinition.keyWord) {
                    allIndexedFieldsSet.add(fieldName);
                }
                if (fieldElement.hasAttribute("store")) {
                    fieldDefinition.storeText = "true".equals(fieldElement.getAttribute("store"));
                } else {
                    fieldDefinition.storeText = !fieldDefinition.keyWord && storeText;
                }
                if (fieldElement.hasAttribute("password")) {
                    fieldDefinition.decryptionPassword = fieldElement.getAttribute("password");
                }
                queryDefinition.fields.add(fieldDefinition);
            } else {
                 log.warn("field tag has no 'name' attribute");
           }
        }
        return fieldList;
    }

    private Step getStep(SearchQuery query, String stepName) {
        List steps = query.getSteps();
        for (Iterator i = steps.iterator(); i.hasNext();) {
            Step step = (Step)i.next();
            if (stepName.equals(step.getAlias()) || stepName.equals(step.getTableName())) {
                return step;
            }
        }
        return null;
    }


    protected void addConstraints(Indexer.QueryDefinition queryDefinition, NodeList constraintsElements) throws BridgeException, SearchQueryException {
        if (constraintsElements.getLength() > 0 ) {
            Constraint constraints = null;
            for (int k = 0; k < constraintsElements.getLength(); k++) {
                Element constraintElement = (Element) constraintsElements.item(k);
                Step step = queryDefinition.mainStep;
                FieldDefs fieldDef = null;
                String fieldName = constraintElement.getAttribute("field");
                int pos = fieldName.indexOf(".");
                if (pos == -1) {
                    fieldDef = queryDefinition.builderResolver.getField(fieldName);
                } else {
                    String stepName = fieldName.substring(0,pos);
                    fieldName = fieldName.substring(pos+1);
                    step = getStep(queryDefinition.query, stepName);
                    fieldDef = mmbase.getBuilder(step.getTableName()).getField(fieldName);
                }
                StepField field = new BasicStepField(step,fieldDef);
                Object value = constraintElement.getAttribute("value");
                // convert value for stupid searchquery
                int type = fieldDef.getDBType();
                switch (type) {
                    case FieldDefs.TYPE_DATETIME : {
                        value = Casting.toDate(value);
                        break;
                    }
                    case FieldDefs.TYPE_BOOLEAN : {
                        value = "true".equals((String)value) ? Boolean.TRUE : Boolean.FALSE;
                        break;
                    }
                    case FieldDefs.TYPE_NODE : {
                        MMObjectNode node = mmbase.getRootBuilder().getNode((String)value);
                        if (node == null) {
                            throw new IllegalArgumentException("node with number/alias " + value + "does not exist");
                        }
                        value = new Long(node.getNumber());
                        break;
                    }
                    case FieldDefs.TYPE_INTEGER : {
                        value = new Integer((String)value);
                        break;
                    }
                    case FieldDefs.TYPE_LONG : {
                        value = new Long((String)value);
                        break;
                    }
                    case FieldDefs.TYPE_DOUBLE : {
                        value = new Double((String)value);
                        break;
                    }
                    case FieldDefs.TYPE_FLOAT : {
                        value = new Float((String)value);
                        break;
                    }
                }
                BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(field, value);
                if (constraintElement.hasAttribute("operator")) {
                    String operator = constraintElement.getAttribute("operator");
                    constraint.setOperator(Queries.getOperator(operator));
                }
                if (constraintElement.hasAttribute("inverse")) {
                    constraint.setInverse("true".equals(constraintElement.getAttribute("inverse")));
                }
                if (constraints == null) {
                    constraints = constraint;
                } else if (constraints instanceof BasicCompositeConstraint) {
                    ((BasicCompositeConstraint)constraints).addChild(constraint);
                } else {
                    constraints = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND).addChild(constraints).addChild(constraint);
                }
            }
            queryDefinition.query.setConstraint(constraints);
        }
    }

    protected void addToIndex (NodeList queryElements, Collection queries, Set allIndexedFieldsSet, boolean storeText, boolean mergeText) {
        for (int j = 0; j < queryElements.getLength(); j++) {
            Element queryElement = (Element) queryElements.item(j);
            try {
                if (queryElement.hasAttribute("optimize")) {
                    String optimize = queryElement.getAttribute("optimize");
                    storeText = optimize.equals("none");
                    mergeText = optimize.equals("full");
                }

                if (queryElement.hasAttribute("type") || queryElement.hasAttribute("name") || queryElement.hasAttribute("path")) {
                    String element = null;
                    String path = null;
                    if (queryElement.hasAttribute("type")) {
                        path = queryElement.getAttribute("type");
                    } else if (queryElement.hasAttribute("name")) {
                        path = queryElement.getAttribute("name");
                    } else{
                        if (queryElement.hasAttribute("element")) {
                          element = queryElement.getAttribute("element");
                        }
                        path = queryElement.getAttribute("path");
                    }
                    List builders  = StringSplitter.split(path);
                    if (element == null) {
                        element = (String)builders.get(builders.size()-1);
                    }

                    List searchDirs = Collections.EMPTY_LIST;
                    if (queryElement.hasAttribute("searchdirs")) {
                        String dirs = queryElement.getAttribute("searchdirs");
                        searchDirs = StringSplitter.split(dirs);
                    }

                    Indexer.QueryDefinition queryDefinition = new Indexer.QueryDefinition();
                    queryDefinition.elementBuilder = mmbase.getBuilder(element);
                    if (element.equals(path)) {
                        queryDefinition.builderResolver = queryDefinition.elementBuilder;
                    } else {
                        queryDefinition.builderResolver = mmbase.getClusterBuilder();
                    }

                    List fields = getFields(queryElement.getElementsByTagName("field"), queryDefinition, allIndexedFieldsSet, storeText, mergeText);
                    if (fields.size() > 0) {

                        if (element.equals(path)) {
                            queryDefinition.query = new NodeSearchQuery(queryDefinition.elementBuilder);
                            queryDefinition.mainStep = (Step)queryDefinition.query.getSteps().get(0);
                        } else {
                            queryDefinition.query = mmbase.getClusterBuilder().getMultiLevelSearchQuery(
                                        null, fields, null, builders, null, null, null, searchDirs);
                            queryDefinition.mainStep = getStep(queryDefinition.query, element);
                        }
                        addConstraints(queryDefinition,queryElement.getElementsByTagName("constraint"));

                        queries.add(queryDefinition);

                        if (!readOnly) {
                            queryDefinition.elementBuilder.addLocalObserver(this);
                            queryDefinition.elementBuilder.addRemoteObserver(this);
                        }
                        if (log.isDebugEnabled()) {
                             log.debug("Configured builder " + element + " with query:" + queryDefinition.query);
                        }
                    } else {
                        log.warn("constraints tag has no valid fields");
                    }
                } else {
                    log.warn("constraints tag has no 'path' attribute");
                }
            } catch (Exception e) {
                log.warn("Invalid query for index");
                log.error(Logging.stackTrace(e));
            }
        }
    }

    protected void readConfiguration(String fullIndexPath) {
        try {
            Document config = ResourceLoader.getConfigurationRoot().getDocument(configFile);
            log.service("Reading lucene search configuration from " + configFile);
            Element root = config.getDocumentElement();
            NodeList indexElements = root.getElementsByTagName("index");
            for (int i = 0; i < indexElements.getLength(); i++) {
                Element indexElement = (Element) indexElements.item(i);
                String indexName = "default";
                if (indexElement.hasAttribute("name")) {
                    indexName = indexElement.getAttribute("name");
                }
                if (indexerMap.containsKey(indexName)) {
                    log.warn("Index with name " + indexName + "already exists");
                } else {
                    boolean storeText = false; // default: no text fields are stored in the index unless noted otherwise
                    boolean mergeText = true; // default: all text fields have the "fulltext" alias unless noted otherwise
                    if (indexElement.hasAttribute("optimize")) {
                        String optimize = indexElement.getAttribute("optimize");
                        storeText = optimize.equals("none");
                        mergeText = optimize.equals("full");
                    }
                    if (defaultIndex==null) defaultIndex = indexName;
                    Set allIndexedFieldsSet = new HashSet();
                    Collection queries = new ArrayList();
                    // constraints
                    NodeList queryElements = root.getElementsByTagName("query");
                    addToIndex(queryElements, queries, allIndexedFieldsSet, storeText, mergeText);
                    // builder
                    queryElements = root.getElementsByTagName("builder");
                    addToIndex(queryElements, queries, allIndexedFieldsSet, storeText, mergeText);

                    /** OptimizeBuilderConfig(buildersToIndex); **/
                    String thisIndex = fullIndexPath + java.io.File.separator + indexName;
                    Indexer indexer = new Indexer(thisIndex,queries,mmbase);
                    log.service("Add lucene index with name " + indexName);
                    indexerMap.put(indexName,indexer);
                    String[]  allIndexedFields = (String[])allIndexedFieldsSet.toArray(new String[0]);
                    Searcher searcher = new Searcher(thisIndex,allIndexedFields,mmbase);
                    searcherMap.put(indexName,searcher);
                }
            }
        } catch (Exception e) {
            log.warn("Can't read Lucene configuration: "+ e.getMessage());
        }
    }

    protected Searcher getSearcher(String indexName) {
        if (indexName == null || indexName.equals("")) indexName = defaultIndex;
        Searcher searcher = (Searcher)searcherMap.get(indexName);
        if (searcher == null) {
            throw new IllegalArgumentException("Index with name "+indexName+" does not exist.");
        }
        return searcher;
    }

    public List search(String value, String indexName, String extraConstraints, int offset, int max) {
        return getSearcher(indexName).search(value, Searcher.createQuery(extraConstraints), offset, max);
    }

    public int searchSize(String value, String indexName, String extraConstraints) {
        return getSearcher(indexName).searchSize(value, Searcher.createQuery(extraConstraints));
    }

    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(machine, number, builder, ctype);
    }

    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(machine, number, builder, ctype);
    }

    public boolean nodeChanged(String machine, String number, String builder, String ctype) {
        if (!readOnly) {
            // if this concerns a change or new node, update the index with that node
            if (ctype.equals("c") || ctype.equals("n")) {
                scheduler.updateIndex(number);
            // if this concerns removing a node, remove the index of that node
            } else if (ctype.equals("d")) {
                scheduler.deleteIndex(number);
            }
        }
        return true;
    }

    class Scheduler extends Thread {

        boolean startFullIndex = false;
        Queue indexAssignments = new Queue();

        Scheduler() {
            super("Lucene.Scheduler");
            setDaemon(true);
            start();
        }

        public void run() {
            log.debug("Start Lucene Scheduler");
            try {
                Thread.sleep(initialWaitTime);
            } catch (InterruptedException ie) {
                return;
            }
            while (true) {
                log.debug("Obtain Assignment");
                Assignment assignment = (Assignment)indexAssignments.get();
                // do operation...
                if (assignment.operation == Assignment.FULL_INDEX) {
                    log.debug("start full index");
                    for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                        Indexer indexer = (Indexer) i.next();
                        indexer.fullIndex();
                    }
                } else if (assignment.operation == Assignment.UPDATE_INDEX) {
                    log.debug("update index");
                    for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                        Indexer indexer = (Indexer) i.next();
                        indexer.updateIndex(assignment.number);
                    }
                } else if (assignment.operation == Assignment.DELETE_INDEX) {
                    log.debug("delete index");
                    for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                        Indexer indexer = (Indexer) i.next();
                        indexer.deleteIndex(assignment.number);
                    }
                }
            }
        }

        public void updateIndex(String number) {
            Assignment assignment = new Assignment();
            assignment.operation = Assignment.UPDATE_INDEX;
            assignment.number = number;
            indexAssignments.append(assignment);
        }

        public void deleteIndex(String number) {
            Assignment assignment = new Assignment();
            assignment.operation = Assignment.DELETE_INDEX;
            assignment.number = number;
            indexAssignments.append(assignment);
        }

        public void fullIndex() {
            log.debug("schedule full index");
            Assignment assignment = new Assignment();
            assignment.operation = Assignment.FULL_INDEX;
            indexAssignments.append(assignment);
        }

        class Assignment {
            static final int UPDATE_INDEX = 0;
            static final int DELETE_INDEX = 1;
            static final int FULL_INDEX   = 2;

            int operation;
            String number;
        }

    }

}
