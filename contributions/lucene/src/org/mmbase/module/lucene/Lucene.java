/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.cache.CachePolicy;
import org.mmbase.module.Module;
import org.mmbase.module.core.*;
import org.mmbase.bridge.util.xml.query.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.util.Queue;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 *
 * @author Pierre van Rooden
 * @version $Id: Lucene.java,v 1.17 2005-12-20 13:28:06 pierre Exp $
 **/
public class Lucene extends Module implements MMBaseObserver {

    /** Public ID of the Lucene config DTD version 2.0 */
    public static final String PUBLIC_ID_LUCENE_2_0 = "-//MMBase//DTD luceneindex config 2.0//EN";
    /** DTD resource filename of the Lucene config DTD version 2.0 */
    public static final String DTD_LUCENE_2_0 = "luceneindex_2_0.dtd";

    /** Public ID of the most recent Lucene config DTD */
    public static final String PUBLIC_ID_LUCENE = PUBLIC_ID_LUCENE_2_0;
    /** DTD repource filename of the most recent Lucene config DTD */
    public static final String DTD_LUCENE = DTD_LUCENE_2_0;

    /** XSD resource filename of the Lucene config XSD version 1.0 */
    public static final String XSD_LUCENE_1_0 = "luceneindex.xsd";
    /** XSD namespace of the Lucene config XSD version 1.0 */
    public static final String NAMESPACE_LUCENE_1_0 = "http://www.mmbase.org/xmlns/luceneindex";

    /** XSD namespace of the Lucene config XSD most recent version version */
    public static final String NAMESPACE_LUCENE = NAMESPACE_LUCENE_1_0;

    // Default namespace to use when parsing the DOM. This circumvents oddities
    // and inconsistencies with how namespaces are treated and assigned in DOM
    // (particularly regarding unqualified attributes) and with non-validating documents
    public static final String XMLNS = "*";

    /**
     * Parameter constants for Lucene functions.
     */
    protected final static Parameter VALUE = new Parameter("value",String.class);
    protected final static Parameter INDEX = new Parameter("index",String.class);
    protected final static Parameter SORTFIELDS = new Parameter("sortfields",String.class);
    protected final static Parameter OFFSET = new Parameter("offset",Integer.class);
    protected final static Parameter MAX = new Parameter("max",Integer.class);
    protected final static Parameter EXTRACONSTRAINTS = new Parameter("extraconstraints",String.class);

    static {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_LUCENE_2_0, DTD_LUCENE_2_0, Lucene.class);
        XMLEntityResolver.registerPublicID(NAMESPACE_LUCENE_1_0, XSD_LUCENE_1_0, Lucene.class);
    }

    // initial wait time after startup, default 5 minutes
    private static final long INITIAL_WAIT_TIME = 5 * 60 * 1000;
    // wait time bewteen individual checks, default 5 seconds
    private static final long WAIT_TIME = 5 * 1000;
    // default path to the lucene data
    private static final String INDEX_PATH = "WEB-INF/data/lucene";
    // default path to the lucene data
    private static final String INDEX_CONFIG_FILE = "utils/luceneindex.xml";
    // Log
    private static final Logger log = Logging.getLoggerInstance(Lucene.class);

    /**
     * The MMBase instance, used for low-level access
     */
    protected MMBase mmbase = null;
    /**
     * The cloud for the instance, used for bridge-level access
     */
    protected Cloud cloud = null;

    private long initialWaitTime = INITIAL_WAIT_TIME;
    private String indexPath = null;
    private Scheduler scheduler;
    private String defaultIndex = null;
    private Map indexerMap = new HashMap();
    private Map searcherMap = new HashMap();
    private boolean readOnly = false;



    /**
     * Returns whether an element has a certain attribute, either an unqualified attribute or an attribute that fits in the
     * lucene namespace
     */
    static public boolean hasAttribute(Element element, String localName) {
        return element.hasAttributeNS(NAMESPACE_LUCENE,localName) || element.hasAttribute(localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * lucene namespace
     */
    static public String getAttribute(Element element, String localName) {
        if (element.hasAttributeNS(NAMESPACE_LUCENE,localName)) {
            return element.getAttributeNS(NAMESPACE_LUCENE,localName);
        } else {
            return element.getAttribute(localName);
        }
    }

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
     * This function returns the status of the scheduler.
     */
    protected Function statusFunction = new AbstractFunction("status", Parameter.EMPTY, ReturnType.INTEGER) {
        public Object getFunctionValue(Parameters arguments) {
            return new Integer(scheduler.getStatus());
        }
    };

    /**
     * This function starts a search for a given string.
     * This function can be called through the function framework.
     */
    protected Function searchFunction = new AbstractFunction("search",
                              new Parameter[] { VALUE, INDEX, SORTFIELDS, OFFSET, MAX, EXTRACONSTRAINTS, Parameter.CLOUD },
                              ReturnType.LIST) {
        public Object getFunctionValue(Parameters arguments) {
            String value = arguments.getString(VALUE);
            String index = arguments.getString(INDEX);
            List sortFieldList = Casting.toList(arguments.getString(SORTFIELDS));
            // offset
            int offset = 0;
            Integer offsetParameter = (Integer)arguments.get(OFFSET);
            if (offsetParameter != null) offset = offsetParameter.intValue();
            if (offset < 0) offset = 0;
            // max
            int max = -1;
            Integer maxParameter = (Integer)arguments.get(MAX);
            if (maxParameter != null) max = maxParameter.intValue();
            String extraConstraints = arguments.getString(EXTRACONSTRAINTS);
            Cloud cloud = (Cloud)arguments.get(Parameter.CLOUD);
            return search(cloud, value, index, extraConstraints, sortFieldList, offset, max);
        }
    };

    /**
     * This function starts a search fro a given string.
     * This function can be called through the function framework.
     */
     protected Function searchSizeFunction = new AbstractFunction("searchsize",
                              new Parameter[] { VALUE, INDEX, EXTRACONSTRAINTS, Parameter.CLOUD },
                              ReturnType.INTEGER) {
        public Object getFunctionValue(Parameters arguments) {
            String value = arguments.getString(VALUE);
            String index = arguments.getString(INDEX);
            String extraConstraints = arguments.getString(EXTRACONSTRAINTS);
            Cloud cloud = (Cloud)arguments.get(Parameter.CLOUD);
            return new Integer(searchSize(cloud, value, index, extraConstraints));
        }
    };

    public void init() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_LUCENE, DTD_LUCENE, Lucene.class);
        super.init();
        // Force init of MMBase
        mmbase = MMBase.getMMBase();

        // path to the lucene index (a directory on disk writeable to the web-application)
        // this path should be a direct path
        String path = getInitParameter("indexpath");
        if (path != null) {
            indexPath = path;
        } else {
            // expand the default path (which is relative to the web-application)
            indexPath = MMBaseContext.getServletContext().getRealPath(INDEX_PATH);
        }

        // read only?
        readOnly = "true".equals(getInitParameter("readonly"));

        // initial wait time?
        String time = getInitParameter("initialwaittime");
        if (time != null) {
            try {
                initialWaitTime = Long.parseLong(time);
                log.debug("Set initial wait time to "+time+" milliseconds");
            } catch (NumberFormatException nfe) {
                log.warn("Invalid value '"+time+"' for property 'initialwaittime'");
            }
        }
        while(! mmbase.getState()) {
            try {
                log.service("MMBase not yet up, waiting..");
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
                log.info(ie);
                return;
            }
        }

        // Obtain a cloud to use for indexing
        // XXX: should solve possible security issues (when not all objects can be read by anonymous.
        // --> Can use class-security for that!
        // For now, use an anonymous cloud
        cloud = LocalContext.getCloudContext().getCloud("mmbase");

        readConfiguration();
        addFunction(searchFunction);
        addFunction(searchSizeFunction);
        addFunction(statusFunction);
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

    protected void addToIndex (Element queryElement, Collection queries, Set allIndexedFieldsSet, boolean storeText, boolean mergeText, String relateFrom) {
        try {
            if (Lucene.hasAttribute(queryElement,"optimize")) {
                String optimize = Lucene.getAttribute(queryElement,"optimize");
                storeText = optimize.equals("none");
                mergeText = optimize.equals("full");
            }

            QueryConfigurer configurer = new IndexConfigurer(allIndexedFieldsSet, storeText, mergeText);

            IndexDefinition queryDefinition = (IndexDefinition) QueryReader.parseQuery(queryElement, configurer, cloud, relateFrom);

            // do not cache these queries
            queryDefinition.query.setCachePolicy(CachePolicy.NEVER);

            queries.add(queryDefinition);

            String elementName = queryDefinition.elementManager.getName();
            if (!readOnly) {
                // register. Unfortunately this can currently only be done through the core
                MMObjectBuilder builder = mmbase.getBuilder(elementName);
                builder.addLocalObserver(this);
                builder.addRemoteObserver(this);
            }

            NodeList childNodes = queryElement.getChildNodes();
            for (int k = 0; k < childNodes.getLength(); k++) {
                if (childNodes.item(k) instanceof Element) {
                    Element childElement = (Element) childNodes.item(k);
                    if ("related".equals(childElement.getLocalName())) {
                        addToIndex(childElement, queryDefinition.subQueries, allIndexedFieldsSet, storeText, mergeText, elementName);
                    }
                }
            }

            if (log.isDebugEnabled()) {
                 log.debug("Configured builder " + elementName + " with query:" + queryDefinition.query);
            }
        } catch (Exception e) {
            log.warn("Invalid query for index");
            log.error(Logging.stackTrace(e));
        }
    }

    protected void readConfiguration() {
        try {
            Document config = ResourceLoader.getConfigurationRoot().getDocument(INDEX_CONFIG_FILE, true, Lucene.class);
            log.service("Reading lucene search configuration from " + INDEX_CONFIG_FILE);
            Element root = config.getDocumentElement();
            NodeList indexElements = root.getElementsByTagNameNS("*","index");
            for (int i = 0; i < indexElements.getLength(); i++) {
                Element indexElement = (Element) indexElements.item(i);
                String indexName = "default";
                if (Lucene.hasAttribute(indexElement,"name")) {
                    indexName = Lucene.getAttribute(indexElement,"name");
                }
                if (indexerMap.containsKey(indexName)) {
                    log.warn("Index with name " + indexName + " already exists");
                } else {
                    boolean storeText = false; // default: no text fields are stored in the index unless noted otherwise
                    boolean mergeText = true; // default: all text fields have the "fulltext" alias unless noted otherwise
                    if (Lucene.hasAttribute(indexElement,"optimize")) {
                        String optimize = Lucene.getAttribute(indexElement,"optimize");
                        storeText = optimize.equals("none");
                        mergeText = optimize.equals("full");
                    }
                    if (defaultIndex==null) defaultIndex = indexName;
                    Set allIndexedFieldsSet = new HashSet();
                    Collection queries = new ArrayList();
                    // lists
                    NodeList childNodes = indexElement.getChildNodes();
                    for (int k = 0; k < childNodes.getLength(); k++) {
                        if (childNodes.item(k) instanceof Element) {
                            Element childElement = (Element) childNodes.item(k);
                            if ("list".equals(childElement.getLocalName())||
                                "builder".equals(childElement.getLocalName()) || // backward comp. old finalist lucene
                                "table".equals(childElement.getLocalName())) { // backward comp. finalist lucene
                               addToIndex(childElement, queries, allIndexedFieldsSet, storeText, mergeText, null);
                            }
                        }
                    }

                    String thisIndex = indexPath + java.io.File.separator + indexName;
                    Indexer indexer = new Indexer(thisIndex,queries,cloud);
                    log.service("Add lucene index with name " + indexName);
                    indexerMap.put(indexName,indexer);
                    String[]  allIndexedFields = (String[])allIndexedFieldsSet.toArray(new String[0]);
                    Searcher searcher = new Searcher(thisIndex,allIndexedFields);
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

    public List search(Cloud cloud, String value, String indexName, String extraConstraints, List sortFieldList, int offset, int max) {
        String[] sortFields = null;
        if (sortFieldList != null) {
            sortFields = (String[]) sortFieldList.toArray(new String[sortFieldList.size()]);
        }
        return getSearcher(indexName).search(cloud, value, sortFields, Searcher.createQuery(extraConstraints), offset, max);
    }

    public int searchSize(Cloud cloud, String value, String indexName, String extraConstraints) {
        return getSearcher(indexName).searchSize(cloud, value, Searcher.createQuery(extraConstraints));
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

        static final int IDLE = 0;
        static final int IDLE_AFTER_ERROR = -1;
        static final int BUSY_INDEX = 1;
        static final int BUSY_FULL_INDEX = 2;

        // status of the scheduler
        private int status = IDLE;
        // assignments: tasks to run
        private Queue indexAssignments = new Queue();

        Scheduler() {
            super("Lucene.Scheduler");
            setDaemon(true);
            start();
        }

        public int getStatus() {
            return status;
        }

        public void run() {
            log.debug("Start Lucene Scheduler");
            try {
                Thread.sleep(initialWaitTime);
            } catch (InterruptedException ie) {
                return;
            }
            while (!mmbase.isShutdown()) {
                log.debug("Obtain Assignment");
                try {
                    Assignment assignment = (Assignment)indexAssignments.get();
                    // do operation...
                    if (assignment.operation == Assignment.FULL_INDEX) {
                        log.debug("start full index");
                        status = BUSY_FULL_INDEX;
                        for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                            Indexer indexer = (Indexer) i.next();
                            indexer.fullIndex();
                        }
                    } else if (assignment.operation == Assignment.UPDATE_INDEX) {
                        log.debug("update index");
                        status = BUSY_INDEX;
                        for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                            Indexer indexer = (Indexer) i.next();
                            indexer.updateIndex(assignment.number);
                        }
                    } else if (assignment.operation == Assignment.DELETE_INDEX) {
                        log.debug("delete index");
                        status = BUSY_INDEX;
                        for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                            Indexer indexer = (Indexer) i.next();
                            indexer.deleteIndex(assignment.number);
                        }
                    }
                    log.debug("end action");
                    status = IDLE;
                } catch (InterruptedException e) {
                    log.debug(Thread.currentThread().getName() +" was interruped.");
                    break;
                } catch (RuntimeException rte) {
                    log.error(rte.getMessage());
                    status = IDLE_AFTER_ERROR;
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
            // only schedule a full index if none is currently busy.
            if (status != BUSY_FULL_INDEX) {
                Assignment assignment = new Assignment();
                assignment.operation = Assignment.FULL_INDEX;
                indexAssignments.append(assignment);
            }
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
