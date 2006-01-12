/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

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
import org.mmbase.cache.CachePolicy;
import org.mmbase.module.Module;
import org.mmbase.module.core.*;
import org.mmbase.bridge.util.xml.query.*;
import org.mmbase.bridge.util.BridgeCollections;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.util.Queue;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory;
import org.mmbase.storage.StorageManagerFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.mmbase.module.lucene.extraction.*;

/**
 * This is the implementation of a 'Lucene' module. It's main job is to bootstrap mmbase lucene
 * indexing, and provide some functions to give access to lucene functionality in an MMBase way.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Lucene.java,v 1.31 2006-01-12 14:09:41 ernst Exp $
 **/
public class Lucene extends Module implements MMBaseObserver {

    public static final String PUBLIC_ID_LUCENE_2_0 = "-//MMBase//DTD luceneindex config 2.0//EN";
    public static final String DTD_LUCENE_2_0 = "luceneindex_2_0.dtd";

    /** Most recent Lucene config DTD */
    public static final String PUBLIC_ID_LUCENE = PUBLIC_ID_LUCENE_2_0;
    public static final String DTD_LUCENE = DTD_LUCENE_2_0;

    /**
     * But we use XSD now!
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
    protected final static Parameter INDEX = new Parameter("index", String.class);
    protected final static Parameter SORTFIELDS = new Parameter("sortfields", String.class);
    protected final static Parameter OFFSET = new Parameter("offset", Integer.class);
    protected final static Parameter MAX = new Parameter("max", Integer.class);
    protected final static Parameter EXTRACONSTRAINTS = new Parameter("extraconstraints", String.class);

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
    private Scheduler scheduler;
    private String defaultIndex = null;
    private Map indexerMap = new HashMap();
    private Map searcherMap = new HashMap();
    private boolean readOnly = false;


    protected Cloud cloud;

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
     */
    protected Function fullIndexFunction = new AbstractFunction("fullIndex",
                                                                new Parameter[] {INDEX},
                                                                ReturnType.VOID) {
        public Object getFunctionValue(Parameters arguments) {
            String index = (String) arguments.get("index");
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
     * This function can be called through the function framework.
     */
    protected Function updateIndexFunction = new AbstractFunction("updateIndex",
                                                                  new Parameter[] {new Parameter("identifier", String.class, true)},
                                                                  ReturnType.VOID) {
        public Object getFunctionValue(Parameters arguments) {
            scheduler.updateIndex(arguments.getString("identifier"));
            return null;
        }
    };
    {
        addFunction(updateIndexFunction);
    }


    /**
     * This function returns the status of the scheduler.
     */
    protected Function statusFunction = new AbstractFunction("status", Parameter.EMPTY, ReturnType.INTEGER) {
        public Object getFunctionValue(Parameters arguments) {
            return new Integer(scheduler.getStatus());
        }
    };
    {
        addFunction(statusFunction);
    }

    protected Function listFunction = new AbstractFunction("list", Parameter.EMPTY, ReturnType.SET) {
            public Object getFunctionValue(Parameters arguments) {
                return indexerMap.keySet();
            }

        };
    {
        addFunction(listFunction);
    }
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

        // Force init of MMBase
        mmbase = MMBase.getMMBase();

        log.info("Adding extractors");

        factory = ContentExtractor.getInstance();

        // traditional Lucenemodule extractors
        factory.addExtractor("org.mmbase.module.lucene.extraction.impl.PDFBoxExtractor");
        factory.addExtractor("org.mmbase.module.lucene.extraction.impl.SwingRTFExtractor");
        //factory.addExtractor("org.mmbase.module.lucene.extraction.impl.POIWordExtractor");
        factory.addExtractor("org.mmbase.module.lucene.extraction.impl.POIExcelExtractor");
        factory.addExtractor("org.mmbase.module.lucene.extraction.impl.TextMiningExtractor");

        // path to the lucene index (a directory on disk writeable to the web-application)
        // this path should be a direct path
        String path = getInitParameter("indexpath");
        if (path != null) {
            indexPath = path;
            log.service("found module parameter for lucine index path : " + indexPath);
        }else {
            //try to get the index path from the strorage configuration
            try{
                DatabaseStorageManagerFactory dsmf = (DatabaseStorageManagerFactory)mmbase.getStorageManagerFactory();
                indexPath = dsmf.getBinaryFileBasePath();
                if(indexPath != null) indexPath =indexPath + dsmf.getDatabaseName() + File.separator + "lucene";
            } catch(Exception e) {}
        }

        if(indexPath != null){
            log.service("found storage configuration for lucine index path : " + indexPath);
        }else{
            // expand the default path (which is relative to the web-application)
            indexPath = MMBaseContext.getServletContext().getRealPath(indexPath);
            log.service("fall back to default for lucine index path : " + indexPath);
        }


        // read only?
        readOnly = "true".equals(getInitParameter("readonly"));

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
                log.debug("Set initial wait time to " + time + " milliseconds");
            } catch (NumberFormatException nfe) {
                log.warn("Invalid value '" + time +" ' for property 'initialwaittime'");
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
        cloud = LocalContext.getCloudContext().getCloud("mmbase", "class", null);
        cloud.setProperty(Cloud.PROP_XMLMODE, "flat");
        log.info("Using cloud of " + cloud.getUser().getIdentifier() + "(" + cloud.getUser().getRank() + ") to lucene index.");
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
            log.info("Module Lucene started");
            // full index ?
            String fias = getInitParameter("fullindexatstartup");
            if (initialWaitTime < 0 || "true".equals(fias)) {
                scheduler.fullIndex();
            }
        }
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

            MMBaseIndexDefinition queryDefinition = (MMBaseIndexDefinition) QueryReader.parseQuery(queryElement, configurer, cloud, relateFrom);
            queryDefinition.setAnalyzer(analyzer);
            // do not cache these queries
            queryDefinition.query.setCachePolicy(CachePolicy.NEVER);

            String elementName = queryDefinition.elementManager.getName();
            if (!readOnly) {
                // register. Unfortunately this can currently only be done through the core
                MMObjectBuilder builder = mmbase.getBuilder(elementName);
                log.service("Observering for " + builder);
                builder.addLocalObserver(this);
                builder.addRemoteObserver(this);
            }

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
                            log.info("Default index: " + defaultIndex);
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
                                    log.info("Added mmbase index definition " + id);
                                } else if ("jdbc".equals(name)) {
                                    DataSource ds =  (DataSource) mmbase.getStorageManagerFactory().getAttribute(org.mmbase.storage.implementation.database.Attributes.DATA_SOURCE);
                                    IndexDefinition id = new JdbcIndexDefinition(ds, childElement,
                                                                                 allIndexedFieldsSet, storeText, mergeText, analyzer);
                                    queries.add(id);
                                    log.info("Added mmbase jdbc definition " + id);
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
                        Indexer indexer = new Indexer(indexPath, indexName, queries, cloud, analyzer);
                        indexer.getDescription().fillFromXml("description", indexElement);
                        log.service("Add lucene index with name " + indexName);
                        indexerMap.put(indexName, indexer);
                        String[]  allIndexedFields = (String[]) allIndexedFieldsSet.toArray(new String[0]);
                        Searcher searcher = new Searcher(indexer, allIndexedFields);
                        searcherMap.put(indexName, searcher);
                    }
                }
            } catch (Exception e) {
                log.warn("Can't read Lucene configuration: "+ e.getMessage());
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

    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(machine, number, builder, ctype);
    }

    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
        return nodeChanged(machine, number, builder, ctype);
    }

    public boolean nodeChanged(String machine, String number, String builder, String ctype) {
        log.info("Received " + number);
        if (!readOnly) {
            // if this concerns a change or new node, update the index with that node
            if (ctype.equals("c") || ctype.equals("n") || ctype.equals("r") {
                scheduler.updateIndex(number);
            // if this concerns removing a node, remove the index of that node
            } else if (ctype.equals("d")) {
                scheduler.deleteIndex(number);
            }
        }
        return true;
    }

    /**
     * Queue for index operations.
     */
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
            log.service("Start Lucene Scheduler");
            try {
                if (initialWaitTime > 0) {
                    log.info("Sleeping for initialisation");
                    Thread.sleep(initialWaitTime);
                }
            } catch (InterruptedException ie) {
                return;
            }
            while (!mmbase.isShutdown()) {
                log.debug("Obtain Assignment");
                try {
                    Runnable assignment = (Runnable) indexAssignments.get();
                    // do operation...
                    assignment.run();
                    status = IDLE;
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    log.debug(Thread.currentThread().getName() +" was interruped.");
                    break;
                } catch (RuntimeException rte) {
                    log.error(rte.getMessage(), rte);
                    status = IDLE_AFTER_ERROR;
                }
            }
        }

        public void updateIndex(final String number) {
            Runnable assignment = new Runnable() {
                    public void run() {
                        log.service("update index");
                        status = BUSY_INDEX;
                        for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                            Indexer indexer = (Indexer) i.next();
                            indexer.updateIndex(number);
                        }
                    }

                };
            indexAssignments.append(assignment);

        }

        public void deleteIndex(final String number) {
            Runnable assignment = new Runnable() {
                    public void run() {
                        log.service("delete index");
                        status = BUSY_INDEX;
                        for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                            Indexer indexer = (Indexer) i.next();
                            indexer.deleteIndex(number);
                        }
                    }
                };
            indexAssignments.append(assignment);
        }

        public void fullIndex() {
            if (status != BUSY_FULL_INDEX) {
                Runnable assignment = new Runnable() {
                        public void run() {
                            status = BUSY_FULL_INDEX;
                            log.service("start full index");
                            for (Iterator i = indexerMap.values().iterator(); i.hasNext(); ) {
                                Indexer indexer = (Indexer) i.next();
                                indexer.fullIndex();
                            }
                        }
                    };
                indexAssignments.append(assignment);
                log.service("Scheduled full index");
                // only schedule a full index if none is currently busy.
            } else {
                log.service("Cannot schedule full index because it is busy");
            }
        }
        public void fullIndex(final String index) {
            if (status != BUSY_FULL_INDEX) {
                Runnable assignment = new Runnable() {
                        public void run() {
                            status = BUSY_FULL_INDEX;
                            log.service("start full index for index '" + index + "'");
                            Indexer indexer = (Indexer) indexerMap.get(index);
                            indexer.fullIndex();
                        }
                    };
                indexAssignments.append(assignment);
                log.service("Scheduled full index for '" + index + "'");
                // only schedule a full index if none is currently busy.
            } else {
                log.service("Cannot schedule full index because it is busy");
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
