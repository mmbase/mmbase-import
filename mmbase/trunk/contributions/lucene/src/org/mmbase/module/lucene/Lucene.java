/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.w3c.dom.*;

import org.mmbase.util.*;
import org.mmbase.util.Queue;
import org.mmbase.module.Module;
import org.mmbase.module.core.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: Lucene.java,v 1.5 2005-01-19 08:57:08 pierre Exp $
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

    private static final Logger log = Logging.getLoggerInstance(Lucene.class);

    MMBase mmbase;
    String[] allIndexedFields = null;

    private String luceneIndexPath = "WEB-INF/data/lucene";
    private String configFile = "utils/luceneindex.xml";

    private Scheduler scheduler;
    private String defaultIndex = null;
    private Map indexerMap = new HashMap();
    private Map searcherMap = new HashMap();

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
     * This function starts a search fro a given string.
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
        readConfiguration(fullIndexPath);
        addFunction(fullIndexFunction);
        addFunction(searchFunction);
        addFunction(searchSizeFunction);
        scheduler = new Scheduler();
        log.info("Module Lucene started");
        // full index ??
        String fias = getInitParameter("fullindexatstartup");
        if ("true".equals(fias)) {
            scheduler.fullIndex();
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
                    // mark this builder as a specialization index buidler
                    // specialization index builders are skipped during full index
                    builderProperties.put("specialization",Boolean.TRUE);
                }
                builder = builder.getParentBuilder();
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
                    boolean storeText = true;
                    boolean mergeText = true;
                    if (indexElement.hasAttribute("optimize")) {
                        String optimize = indexElement.getAttribute("optimize");
                        storeText = optimize.equals("none");
                        mergeText = optimize.equals("full");
                    }
                    if (defaultIndex==null) defaultIndex = indexName;
                    Set allIndexedFieldsSet = new HashSet();
                    Map buildersToIndex = new HashMap();
                    NodeList builderElements = root.getElementsByTagName("builder");
                    for (int j = 0; j < builderElements.getLength(); j++) {
                        Element builderElement = (Element) builderElements.item(j);
                        if (builderElement.hasAttribute("name")) {
                            String builderName = builderElement.getAttribute("name");
                            MMObjectBuilder builder = mmbase.getBuilder(builderName);
                            if (builder != null) {
                                Set builderSet = new HashSet();
                                NodeList fieldElements = builderElement.getElementsByTagName("field");
                                for (int k = 0 ; k < fieldElements.getLength(); k++) {
                                    Element fieldElement = (Element) fieldElements.item(k);
                                    if (fieldElement.hasAttribute("name")) {
                                        String fieldName = fieldElement.getAttribute("name");
                                        builderSet.add(fieldName);
                                        allIndexedFieldsSet.add(fieldName);
                                    } else {
                                        log.warn("field tag has no 'name' attribute");
                                    }
                                }
                                if (builderSet.size() > 0) {
                                    Map builderProperties = new HashMap();
                                    builderProperties.put("fieldset",builderSet);
                                    buildersToIndex.put(builderName, builderProperties);
                                    builder.addLocalObserver(this);
                                    builder.addRemoteObserver(this);
                                    if (log.isDebugEnabled()) {
                                         log.debug("Configured builder "+builderName+" with field set:" + builderSet);
                                    }
                                } else {
                                    log.warn("builder tag has no valid fields");
                                }
                            } else {
                                log.warn("builder with name '" + builderName +"' does not exist.");
                            }
                        } else {
                            log.warn("builder tag has no 'name' attribute");
                        }
                    }
                    OptimizeBuilderConfig(buildersToIndex);
                    String thisIndex = fullIndexPath + java.io.File.separator + indexName;
                    Indexer indexer = new Indexer(thisIndex,buildersToIndex,storeText,mergeText,mmbase);
                    log.service("Add lucene index with name " + indexName);
                    indexerMap.put(indexName,indexer);
                    String[]  allIndexedFields = (String[])allIndexedFieldsSet.toArray(new String[0]);
                    Searcher searcher = new Searcher(thisIndex,allIndexedFields,mergeText,mmbase);
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
       // if this concerns a change or new node, update the index with that node
       if (ctype.equals("c") || ctype.equals("n")) {
           scheduler.updateIndex(number);
       // if this concerns removing a node, remove the index of that node
       } else if (ctype.equals("d")) {
           scheduler.deleteIndex(number);
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
                Thread.sleep(INITIAL_WAIT_TIME);
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
