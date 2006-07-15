/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
sQuery
*/
package org.mmbase.module.lucene;

import java.util.*;
import java.io.File;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;
import java.net.URL;
import javax.sql.DataSource;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.cache.CachePolicy;
import org.mmbase.module.Module;

import org.mmbase.core.event.*;
import org.mmbase.module.core.*;
import org.mmbase.bridge.util.xml.query.*;
import org.mmbase.bridge.util.BridgeCollections;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory;
import org.mmbase.storage.StorageManagerFactory;

import edu.emory.mathcs.backport.java.util.concurrent.*;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.mmbase.module.lucene.extraction.*;

/**
 * This is the implementation of a 'Lucene' module. It's main job is to bootstrap mmbase lucene
 * indexing, and provide some functions to give access to lucene functionality in an MMBase way.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Lucene.java,v 1.62 2006-06-06 11:28:05 michiel Exp $
 **/
public class Lucene extends Module implements NodeEventListener, IdEventListener {

    public static final String PUBLIC_ID_LUCENE_2_0 = "-//MMBase//DTD luceneindex config 2.0//EN";
    public static final String DTD_LUCENE_2_0 = "luceneindex_2_0.dtd";

    /** Most recent Lucene config DTD */
    public static final String PUBLIC_ID_LUCENE = PUBLIC_ID_LUCENE_2_0;
    public static final String DTD_LUCENE = DTD_LUCENE_2_0;

    /**
     * But we use XSD now!
     * @TODO Support for DTD's can be dropped, it was never released.
     */
    public static final String XSD_LUCENE_1_0 = "luceneindex.xsd";
    public static final String NAMESPACE_LUCENE_1_0 = "http://www.mmbase.org/xmlns/luceneindex";

    /**
     * Most recend namespace
     */
    public static final String NAMESPACE_LUCENE = NAMESPACE_LUCENE_1_0;


    /**
     * Parameter constants for Lucene functions.
     */
    protected final static Parameter VALUE = new Parameter("value", String.class);
    static { VALUE.setDescription("the search term(s)"); }

    protected final static Parameter INDEX = new Parameter("index", String.class);
    static { INDEX.setDescription("the name of the index to search in"); }

    protected final static Parameter CLASS = new Parameter("class", Class.class, IndexDefinition.class);
    static { INDEX.setDescription("the class of indices to search in (default to all classes)"); }

    protected final static Parameter SORTFIELDS = new Parameter("sortfields", String.class);
    protected final static Parameter OFFSET = new Parameter("offset", Integer.class);
    static { OFFSET.setDescription("for creating sublists"); }

    protected final static Parameter MAX = new Parameter("max", Integer.class);
    static { MAX.setDescription("for creating sublists"); }

    protected final static Parameter EXTRACONSTRAINTS = new Parameter("extraconstraints", String.class);
    static { EXTRACONSTRAINTS.setDescription("@see org.mmbase.module.lucene.Searcher#createQuery()"); }

    /*
    protected final static Parameter EXTRACONSTRAINTSLIST = new Parameter("constraints", List.class);
    static { EXTRACONSTRAINTSLIST.setDescription("@see org.mmbase.module.lucene.Searcher#createQuery()"); }
    */

    protected final static Parameter IDENTIFIER = new Parameter("identifier", String.class);
    static { IDENTIFIER.setDescription("Normally a node number, identifier (a number of) lucene document(s) in an index."); }

    static {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_LUCENE_2_0, DTD_LUCENE_2_0, Lucene.class);
        XMLEntityResolver.registerPublicID(NAMESPACE_LUCENE_1_0, XSD_LUCENE_1_0, Lucene.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_LUCENE, DTD_LUCENE, Lucene.class);
    }

    // initial wait time after startup, default 2 minutes
    private static final long INITIAL_WAIT_TIME = 2 * 60 * 1000;
    // wait time bewteen individual checks, default 5 seconds
    private static final long WAIT_TIME = 5 * 1000;
    // default path to the lucene data
    private static final String INDEX_CONFIG_FILE = "utils/luceneindex.xml";

    private static final Logger log = Logging.getLoggerInstance(Lucene.class);

    /**
     * The MMBase instance, used for low-level access
     */
    protected MMBase mmbase = null;

    private long initialWaitTime = INITIAL_WAIT_TIME;
    private long waitTime = WAIT_TIME;
    private String indexPath = null;
    private Scheduler scheduler = null;
    private String defaultIndex = null;
    private Map indexerMap    = new ConcurrentHashMap();
    private Map searcherMap   = new ConcurrentHashMap();
    private boolean readOnly = false;


    /**
     * Returns whether an element has a certain attribute, either an unqualified attribute or an attribute that fits in the
     * lucene namespace
     */
    static public boolean hasAttribute(Element element, String localName) {
        return element.hasAttributeNS(NAMESPACE_LUCENE, localName) || element.hasAttribute(localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * lucene namespace
     */
    static public String getAttribute(Element element, String localName) {
        if (element.hasAttributeNS(NAMESPACE_LUCENE, localName)) {
            return element.getAttributeNS(NAMESPACE_LUCENE, localName);
        } else {
            return element.getAttribute(localName);
        }
    }

    /**
     * This function starts a full Index of Lucene.
     * This may take a while.
     * This function can be called through the function framework.
     * <p>Parameters:</p>
     * <ul>
     * <li> - index: name of the index to reindex or empty for full indexing
     * </ul>
     * <p>Return: void</p>
     */
    protected Function fullIndexFunction = new AbstractFunction("fullIndex",
                                                                new Parameter[] {INDEX},
                                                                ReturnType.VOID) {
        public Object getFunctionValue(Parameters arguments) {
            if (scheduler == null) throw new RuntimeException("Read only");
            String index = (String) arguments.get(INDEX);
            if (index == null || "".equals(index)) {
                scheduler.fullIndex();
            } else {
                scheduler.fullIndex(index);
            }
            return null;
        }
    };
    {
        addFunction(fullIndexFunction);
    }

    /**
     * This function deletes an indexed entry from an index
     * if the Parameter 'index' has value null, all indexes are iterated over, otherwise
     * the right index is addressed.
     */
    protected Function deleteIndexFunction = new AbstractFunction("deleteIndex",
                                                                  new Parameter[] {INDEX, IDENTIFIER, CLASS},
                                                                  ReturnType.VOID) {
            public Object getFunctionValue(Parameters arguments) {
                if (scheduler == null) throw new RuntimeException("Read only");
                if(!readOnly){
                    String index      = (String) arguments.get(INDEX);
                    String identifier = (String) arguments.get(IDENTIFIER);
                    Class  klass = (Class) arguments.get(CLASS);
                    if(index == null || "".equals(index)){
                        scheduler.deleteIndex(identifier, klass);
                    } else {
                        scheduler.deleteIndex(identifier, identifier);
                    }
                }
                return null;
            }
        };
    {
        addFunction(deleteIndexFunction);
    }



    /**
     * This function can be called through the function framework.
     * It (re)loads the index for a specific item (identified by 'identifier' parameter).
     */
    protected Function updateIndexFunction = new AbstractFunction("updateIndex",
                                                                  new Parameter[] { new Parameter(IDENTIFIER, true),  CLASS},
                                                                  ReturnType.VOID) {
            public Object getFunctionValue(Parameters arguments) {
                if (scheduler == null) throw new RuntimeException("Read only");
                scheduler.updateIndex(arguments.getString(IDENTIFIER), (Class) arguments.get(CLASS));
                return null;
            }
        };
    {
        addFunction(updateIndexFunction);
    }


    /**
     * This function returns the status of the scheduler. For possible values see: Lucene.Scheduler
     */
    protected Function statusFunction = new AbstractFunction("status", Parameter.EMPTY, ReturnType.INTEGER) {
        public Object getFunctionValue(Parameters arguments) {
            return new Integer(scheduler == null ? Scheduler.READONLY : scheduler.getStatus());
        }
    };
    {
        addFunction(statusFunction);
    }
    protected Function statusDescriptionFunction = new AbstractFunction("statusdescription", new Parameter[] {Parameter.LOCALE}, ReturnType.STRING) {
        public Object getFunctionValue(Parameters arguments) {
            Locale locale = (Locale) arguments.get(Parameter.LOCALE);
            SortedMap map = SortedBundle.getResource("org.mmbase.module.lucene.resources.status",  locale,
                                                     getClass().getClassLoader(),
                                                     SortedBundle.getConstantsProvider(Scheduler.class), Integer.class, null);
            String desc = "" + map.get(new Integer(scheduler == null ? Scheduler.READONLY : scheduler.getStatus()));
            Object ass = (scheduler == null ? null : scheduler.getAssignment());
            return desc + (ass == null ? "" : " " + ass);
        }
    };
    {
        addFunction(statusDescriptionFunction);
    }
    protected Function queueFunction = new AbstractFunction("queue", Parameter.EMPTY, ReturnType.COLLECTION) {
        public Object getFunctionValue(Parameters arguments) {
            return scheduler == null ? Collections.EMPTY_LIST : scheduler.getQueue();
        }
    };
    {
        addFunction(queueFunction);
    }

    protected Function readOnlyFunction = new AbstractFunction("readOnly", Parameter.EMPTY, ReturnType.BOOLEAN){
        public Object getFunctionValue(Parameters arguments) {
            return Boolean.valueOf(readOnly);
        }
    };
    {
        addFunction(readOnlyFunction);
    }

    /**
     * This function returns Set with the names of all confiured indexes.
     */
    protected Function listFunction = new AbstractFunction("list", Parameter.EMPTY, ReturnType.SET) {
            public Object getFunctionValue(Parameters arguments) {
                return indexerMap.keySet();
            }

        };
    {
        addFunction(listFunction);
    }

    /**
     *This function returns the description as configured for a specific index and a specific locale.
     */
    protected Function descriptionFunction = new AbstractFunction("description", new Parameter[] {INDEX, Parameter.LOCALE}, ReturnType.STRING ) {
            public Object getFunctionValue(Parameters arguments) {
                String key = arguments.getString(INDEX);
                Locale locale = (Locale) arguments.get(Parameter.LOCALE);
                Indexer index = (Indexer) indexerMap.get(key);
                return index.getDescription().get(locale);
            }

        };
    {
        addFunction(descriptionFunction);
    }

    /**
     * This function starts a search fro a given string.
     * This function can be called through the function framework.
     */
    protected Function searchFunction = new AbstractFunction("search",
                                                             new Parameter[] { VALUE, INDEX, SORTFIELDS, OFFSET, MAX, EXTRACONSTRAINTS, Parameter.CLOUD },
                                                             ReturnType.NODELIST) {
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
                /*
                List moreConstraints = (List) arguments.get(EXTRACONSTRAINTSLIST);
                if (moreConstraints != null && moreConstraints.size() > 0) {
                    StringBuffer ec = new StringBuffer(extraConstraints == null ? "" : extraConstraints + " ");
                    Iterator i = moreConstraints.iterator();
                    while (i.hasNext()) {
                        ec.append(i.next().toString());
                        ec.append(" ");
                    }
                    extraConstraints = ec.toString().trim();
                }
                */
                Cloud cloud = (Cloud)arguments.get(Parameter.CLOUD);
                try {
                    return search(cloud, value, index, extraConstraints, sortFieldList, offset, max);
                } catch (ParseException pe) {
                    // search function is typically used in a JSP and the 'value' parameter filled by web-site users.
                    // They may not fill the log with errors!
                    if (log.isDebugEnabled()) {
                        log.debug(pe);
                    }
                    return BridgeCollections.EMPTY_NODELIST;
                }
            }
        };
    {
        addFunction(searchFunction);
    }

    /**
     * This function returns the size of a query on an index.
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
    {
        addFunction(searchSizeFunction);
    }

    private ContentExtractor factory;

    public void init() {
        super.init();


        ThreadPools.jobsExecutor.execute(new Runnable() {
                public void run() {
                    // Force init of MMBase
                    mmbase = MMBase.getMMBase();

                    factory = ContentExtractor.getInstance();


                    String path = getInitParameter("indexpath");
                    if (path != null) {
                        indexPath = path;
                        log.service("found module parameter for lucene index path : " + indexPath);
                    } else {
                        //try to get the index path from the strorage configuration
                        try {
                            DatabaseStorageManagerFactory dsmf = (DatabaseStorageManagerFactory)mmbase.getStorageManagerFactory();
                            indexPath = dsmf.getBinaryFileBasePath();
                            if(indexPath != null) indexPath =indexPath + dsmf.getDatabaseName() + File.separator + "lucene";
                        } catch(Exception e){}
                    }

                    if(indexPath != null) {
                        log.service("found storage configuration for lucene index path : " + indexPath);
                    } else {
                        // expand the default path (which is relative to the web-application)
                        indexPath = MMBaseContext.getServletContext().getRealPath(indexPath);
                        log.service("fall back to default for lucene index path : " + indexPath);
                    }


                    String readOnlySetting = getInitParameter("readonly");
                    while (readOnlySetting != null && readOnlySetting.startsWith("system:")) {
                        readOnlySetting = System.getProperty(readOnlySetting.substring(7));
                    }
                    if (readOnlySetting != null) {
                        if (readOnlySetting.startsWith("host:")) {
                            String host = readOnlySetting.substring(5); 
                            try {
                                boolean write = 
                                    java.net.InetAddress.getLocalHost().getHostName().equals(host) ||
                                    (System.getProperty("catalina.base") + "@" + java.net.InetAddress.getLocalHost().getHostName()).equals(host);
                                readOnly = ! write;
                            } catch (java.net.UnknownHostException uhe) {
                                log.error(uhe);
                            }
                        } else {
                            readOnly = "true".equals(readOnlySetting);
                        }
                    }
                    if (readOnly) {
                        log.info("Lucene module of this MMBase will be READONLY");
                    }

                    // initial wait time?
                    String time = getInitParameter("initialwaittime");
                    if (time != null) {
                        try {
                            initialWaitTime = Long.parseLong(time);
                            log.debug("Set initial wait time to " + time + " milliseconds");
                        } catch (NumberFormatException nfe) {
                            log.warn("Invalid value '" + time + "' for property 'initialwaittime'");
                        }
                    }
                    time = getInitParameter("waittime");
                    if (time != null) {
                        try {
                            waitTime = Long.parseLong(time);
                            log.debug("Set wait time to " + time + " milliseconds");
                        } catch (NumberFormatException nfe) {
                            log.warn("Invalid value '" + time +" ' for property 'iwaittime'");
                        }
                    }
                    while(! mmbase.getState()) {
                        if (mmbase.isShutdown()) break;
                        try {
                            log.service("MMBase not yet up, waiting for 10 seconds.");
                            Thread.sleep(10000);
                        } catch (InterruptedException ie) {
                            log.info(ie);
                            return;
                        }
                    }

                    ResourceWatcher watcher = new ResourceWatcher() {
                            public void onChange(String resource) {
                                readConfiguration(resource);
                            }
                        };
                    watcher.add(INDEX_CONFIG_FILE);
                    watcher.onChange();
                    watcher.start();

                    if (!readOnly) {
                        scheduler = new Scheduler();
                        log.service("Module Lucene started");
                        // full index ?
                        String fias = getInitParameter("fullindexatstartup");
                        if (initialWaitTime < 0 || "true".equals(fias)) {
                            scheduler.fullIndex();
                        }
                    }
                }
            });

    }



    private Cloud cloud = null;
    protected Cloud getCloud() {
        if (cloud != null && !cloud.getUser().isValid()) cloud = null;
        while (cloud == null) {
            try {
                cloud = LocalContext.getCloudContext().getCloud("mmbase", "class", null);
                cloud.setProperty(Cloud.PROP_XMLMODE, "flat");
                log.info("Using cloud of " + cloud.getUser().getIdentifier() + "(" + cloud.getUser().getRank() + ") to lucene index.");
            } catch (Throwable t) {
                log.info(t.getMessage());
            }
            if (cloud == null) {
                try {
                    log.info("No cloud found, waiting for 5 seconds");
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    return null;
                }
            }
        }
        return cloud;
    }

    public void shutdown() {
        if (scheduler != null) {
            log.service("Stopping Lucene Scheduler");
            scheduler.interrupt();
        }
}

    public String getModuleInfo() {
        return "This module performs lucene searches and maintains indices";
    }

    /**
     * MMBase Queries and sub-queries
     */
    protected MMBaseIndexDefinition createIndexDefinition (Element queryElement, Set allIndexedFieldsSet, boolean storeText, boolean mergeText, String relateFrom, Analyzer analyzer) {
        try {
            if (Lucene.hasAttribute(queryElement,"optimize")) {
                String optimize = Lucene.getAttribute(queryElement,"optimize");
                storeText = optimize.equals("none");
                mergeText = optimize.equals("full");
            }

            QueryConfigurer configurer = new IndexConfigurer(allIndexedFieldsSet, storeText, mergeText);

            MMBaseIndexDefinition queryDefinition = (MMBaseIndexDefinition) QueryReader.parseQuery(queryElement, configurer, getCloud(), relateFrom);
            queryDefinition.setAnalyzer(analyzer);
            // do not cache these queries
            queryDefinition.query.setCachePolicy(CachePolicy.NEVER);

            // MM: I think the follwing functionality should be present on MMBaseIndexDefinition itself. and not on Lucene.
            // And of course, the new event-mechanism must be used.
            if (!readOnly) {
                // register. Unfortunately this can currently only be done through the core
                Iterator i = queryDefinition.query.getSteps().iterator();
                while(i.hasNext()) {
                    Step step = (Step) i.next();
                    MMObjectBuilder builder = mmbase.getBuilder(step.getTableName());
                    log.service("Observing for builder " + builder.getTableName() + " for index " + queryElement.getAttribute("name"));
                    builder.addEventListener(this);
                }
            }



            String elementName = queryDefinition.elementManager.getName();
            NodeList childNodes = queryElement.getChildNodes();
            for (int k = 0; k < childNodes.getLength(); k++) {
                if (childNodes.item(k) instanceof Element) {
                    Element childElement = (Element) childNodes.item(k);
                    if ("related".equals(childElement.getLocalName())) {
                        queryDefinition.subQueries.add(createIndexDefinition(childElement, allIndexedFieldsSet, storeText, mergeText, elementName, analyzer));
                    }
                }
            }

            if (log.isDebugEnabled()) {
                 log.debug("Configured builder " + elementName + " with query:" + queryDefinition.query);
            }
            return queryDefinition;
        } catch (Exception e) {
            log.warn("Invalid query for index " + XMLWriter.write(queryElement, true, true), e);
            return null;
        }
    }

    protected final IdEventListener idListener = new IdEventListener() {
            // wrapping to avoid also registring it as a NodeEventListener
            public void notify(IdEvent idEvent) {
                Lucene.this.notify(idEvent);
            }
            public String toString() {
                return Lucene.this.toString();
            }
        };

    protected void readConfiguration(String resource) {
        indexerMap = new HashMap();
        searcherMap = new HashMap();
        List configList = ResourceLoader.getConfigurationRoot().getResourceList(resource);
        log.service("Reading " + configList);
        Iterator configs = configList.iterator();
        while (configs.hasNext()) {
            URL url = (URL) configs.next();
            try {
                if (! url.openConnection().getDoInput()) continue;
                Document config = ResourceLoader.getDocument(url, true, Lucene.class);
                log.service("Reading lucene search configuration from " + url);
                Element root = config.getDocumentElement();
                NodeList indexElements = root.getElementsByTagName("index");
                for (int i = 0; i < indexElements.getLength(); i++) {
                    Element indexElement = (Element) indexElements.item(i);
                    String indexName = "default";
                    if (Lucene.hasAttribute(indexElement, "name")) {
                        indexName = Lucene.getAttribute(indexElement, "name");
                    }
                    if (indexerMap.containsKey(indexName)) {
                        log.warn("Index with name " + indexName + " already exists");
                    } else {
                        boolean storeText = false; // default: no text fields are stored in the index unless noted otherwise
                        boolean mergeText = true; // default: all text fields have the "fulltext" alias unless noted otherwise
                        if (Lucene.hasAttribute(indexElement, "optimize")) {
                            String optimize = Lucene.getAttribute(indexElement, "optimize");
                            storeText = optimize.equals("none");
                            mergeText = optimize.equals("full");
                        }
                        if (defaultIndex == null) {
                            defaultIndex = indexName;
                            log.service("Default index: " + defaultIndex);
                        }
                        Set allIndexedFieldsSet = new HashSet();
                        Collection queries = new ArrayList();
                        // lists
                        NodeList childNodes = indexElement.getChildNodes();
                        Analyzer analyzer = null;
                        for (int k = 0; k < childNodes.getLength(); k++) {
                            if (childNodes.item(k) instanceof Element) {
                                Element childElement = (Element) childNodes.item(k);
                                String name = childElement.getLocalName();
                                if ("list".equals(name)||
                                    "builder".equals(name) || // backward comp. old finalist lucene
                                    "table".equals(name)) { // comp. finalist lucene
                                    IndexDefinition id = createIndexDefinition(childElement, allIndexedFieldsSet, storeText, mergeText, null, analyzer);
                                    queries.add(id);
                                    log.service("Added mmbase index definition " + id);
                                } else if ("jdbc".equals(name)) {
                                    DataSource ds =  ((DatabaseStorageManagerFactory) mmbase.getStorageManagerFactory()).getDataSource();
                                    IndexDefinition id = new JdbcIndexDefinition(ds, childElement,
                                                                                 allIndexedFieldsSet, storeText, mergeText, analyzer);
                                    queries.add(id);
                                    EventManager.getInstance().addEventListener(idListener);
                                    log.service("Added mmbase jdbc definition " + id);
                                } else if ("analyzer".equals(name)) {
                                    String className = childElement.getAttribute("class");
                                    try {
                                        Class clazz = Class.forName(className);
                                        analyzer = (Analyzer) clazz.newInstance();
                                    } catch (Exception e) {
                                        log.error("Could not instantiate analyzer " + className + " for index '" + indexName + "', falling back to default. " + e);
                                    }
                                }
                            }
                        }
                        Indexer indexer = new Indexer(indexPath, indexName, queries, getCloud(), analyzer, readOnly);
                        indexer.getDescription().fillFromXml("description", indexElement);
                        log.service("Add lucene index with name " + indexName);
                        indexerMap.put(indexName, indexer);
                        String[]  allIndexedFields = (String[]) allIndexedFieldsSet.toArray(new String[0]);
                        Searcher searcher = new Searcher(indexer, allIndexedFields);
                        searcherMap.put(indexName, searcher);
                    }
                }
            } catch (Exception e) {
                log.warn("Can't read Lucene configuration: "+ e.getMessage(), e);
            }
        }
    }

    public Searcher getSearcher(String indexName) {
        if (indexName == null || indexName.equals("")) indexName = defaultIndex;
        Searcher searcher = (Searcher)searcherMap.get(indexName);
        if (searcher == null) {
            throw new IllegalArgumentException("Index with name "+indexName+" does not exist.");
        }
        return searcher;
    }

    public org.mmbase.bridge.NodeList search(Cloud cloud, String value, String indexName, String extraConstraints, List sortFieldList, int offset, int max) throws ParseException {
        String[] sortFields = null;
        if (sortFieldList != null) {
            sortFields = (String[]) sortFieldList.toArray(new String[sortFieldList.size()]);
        }
        return getSearcher(indexName).search(cloud, value, sortFields, Searcher.createQuery(extraConstraints), offset, max);
    }

    public int searchSize(Cloud cloud, String value, String indexName, String extraConstraints) {
        return getSearcher(indexName).searchSize(cloud, value, Searcher.createQuery(extraConstraints));
    }

    public void notify(NodeEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("Received node event " + event);
        }
        if (scheduler != null) {
            switch(event.getType()) {
            case Event.TYPE_NEW:
            case Event.TYPE_CHANGE:
                scheduler.updateIndex("" + event.getNodeNumber(), MMBaseIndexDefinition.class);
                break;
            case Event.TYPE_DELETE:
                scheduler.deleteIndex("" + event.getNodeNumber(), MMBaseIndexDefinition.class);
                break;
            }
        }
    }
    public void notify(IdEvent event) {
        if (scheduler != null) {
            switch(event.getType()) {
            case Event.TYPE_DELETE:
                scheduler.deleteIndex(event.getId(), JdbcIndexDefinition.class);
                break;
            default:
                scheduler.updateIndex(event.getId(), JdbcIndexDefinition.class);
                break;

            }
        }
    }

    /**
     * Queue for index operations.
     */
    // public because the constants need to be visible for the SortedBundle
    public class Scheduler extends Thread {

        public static final int READONLY = -100;
        public static final int IDLE = 0;
        public static final int IDLE_AFTER_ERROR = -1;
        public static final int BUSY_INDEX = 1;
        public static final int BUSY_FULL_INDEX = 2;

        // status of the scheduler
        private int status = IDLE;
        private Runnable assignment = null;

        // assignments: tasks to run
        private BlockingQueue indexAssignments = new DelayQueue();

        Scheduler() {
            super("Lucene.Scheduler");
            setDaemon(true);
            start();
        }

        public int getStatus() {
            return status;
        }
        public Runnable getAssignment() {
            return assignment;
        }
        public Collection getQueue() {
            return Collections.unmodifiableCollection(indexAssignments);
        }

        public void run() {
            log.service("Start Lucene Scheduler");
            try {
                if (initialWaitTime > 0) {
                    log.info("Sleeping " + (initialWaitTime / 1000) + " seconds for initialisation");
                    Thread.sleep(initialWaitTime);
                }
            } catch (InterruptedException ie) {
                return;
            }
            while (!mmbase.isShutdown()) {
                if (log.isDebugEnabled()) {
                    log.debug("Obtain Assignment from " + indexAssignments);
                }
                try {
                    assignment = (Runnable) indexAssignments.take();
                    log.debug("Running " + assignment);
                    // do operation...
                    assignment.run();
                    status = IDLE;
                    assignment = null;
                } catch (InterruptedException e) {
                    log.debug(Thread.currentThread().getName() +" was interruped.");
                    break;
                } catch (RuntimeException rte) {
                    log.error(rte.getMessage(), rte);
                    status = IDLE_AFTER_ERROR;
                }
            }
        }


        abstract class Assignment implements Runnable, Delayed {
            private long endTime = System.currentTimeMillis() + waitTime;
            public int hashCode() {
                return toString().hashCode();
            }
            public boolean equals(Object o) {
                if (o == null) return false;
                return o.getClass().equals(getClass()) && o.toString().equals(toString());
            }
            public long  getDelay(TimeUnit unit) {
                return unit.convert(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }
            public int compareTo(Object o) {
                return (int) (getDelay(TimeUnit.MILLISECONDS) - ((Delayed) o).getDelay(TimeUnit.MILLISECONDS));
            }
        }
        void assign(Assignment assignment) {
            if (! indexAssignments.contains(assignment)) {
                indexAssignments.offer(assignment);
            } else {
                log.debug("Canceling " + assignment + ", because already queued");
            }
        }

        void updateIndex(final String number, final Class klass) {
            assign(new Assignment() {
                    public void run() {
                        log.service("Update index for " + number);
                        status = BUSY_INDEX;
                        for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                            Indexer indexer = (Indexer) i.next();
                            int updated = indexer.updateIndex(number, klass);
                            if (updated > 0) {
                                log.service(indexer.getName() + ": Updated " + updated + " index entr" + (updated > 1 ? "ies" : "y"));
                            }
                        }
                    }
                    public String toString() {
                        return "UPDATE for " + number + " " + klass;
                    }

                });
        }

        void deleteIndex(final String number, final Class klass) {
            assign(new Assignment() {
                    public void run() {
                        log.service("delete index for " + number);
                        status = BUSY_INDEX;
                        for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                            Indexer indexer = (Indexer) i.next();
                            indexer.deleteIndex(number, klass);
                        }
                    }
                    public String toString() {
                        return "DELETE for " + number + " " + klass;
                    }
                });
        }

        void deleteIndex(final String number, final String indexName) {
            assign(new Assignment() {
                    public void run() {
                        log.service("delete index for " + number);
                        status = BUSY_INDEX;
                        Indexer indexer = (Indexer)indexerMap.get(indexName);
                        if (indexer == null) {
                            log.error("No such index '" + indexName + "'");
                        } else {
                            indexer.deleteIndex(number, IndexDefinition.class);
                        }
                    }
                    public String toString() {
                        return "DELETE for " + number + " " + indexName;
                    }
                });
        }

        private final Assignment ALL_FULL_INDEX = new Assignment() {
                public void run() {
                    status = BUSY_FULL_INDEX;
                    log.service("start full index");
                    for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                        Indexer indexer = (Indexer) i.next();
                        indexer.fullIndex();
                    }
                }
                public String toString() {
                    return "FULLINDEX";
                }
                public long getDelay(TimeUnit unit) {
                    return 0;
                }
            };

        synchronized void fullIndex() {
            if (status != BUSY_FULL_INDEX) {
                assign(ALL_FULL_INDEX);
                log.service("Scheduled full index");
                // only schedule a full index if none is currently busy.
            } else {
                log.service("Cannot schedule full index because it is busy with " + getAssignment());
            }
        }
        synchronized void fullIndex(final String index) {
            if (status != BUSY_FULL_INDEX || ! assignment.equals(ALL_FULL_INDEX)) {
                if (! indexAssignments.contains(ALL_FULL_INDEX)) {
                    // only schedule a full index if no complete full index ne is currently busy or scheduled already.

                    assign(new Assignment() {
                            public void run() {
                                status = BUSY_FULL_INDEX;
                                log.service("start full index for index '" + index + "'");
                                Indexer indexer = (Indexer) indexerMap.get(index);
                                if (indexer == null) {
                                    log.error("No such index '" + index + "'");
                                } else {
                                    indexer.fullIndex();
                                }
                            }
                            public String toString() {
                                return "FULLINDEX for " + index;
                            }
                            public long getDelay(TimeUnit unit) {
                                return 0;
                            }
                        });
                    log.service("Scheduled full index for '" + index + "'");
                } else {
                    log.service("Scheduled full index for '" + index + "' because full index on every index is scheduled already");
                }
            } else {
                log.service("Cannot schedule full index for '" + index + "' because it is busy with " + getAssignment());
            }
        }

    }

    /**
     * Main for testing
     */
    public static void main(String[] args) {
        String configFile = args[0];
    }


}
