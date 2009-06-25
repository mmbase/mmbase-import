/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams;

import org.mmbase.streams.transcoders.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.storage.search.*;
import org.mmbase.security.UserContext;
import org.mmbase.security.ActionRepository;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.*;
import org.mmbase.util.xml.*;
import org.mmbase.util.externalprocess.CommandExecutor;
import org.mmbase.datatypes.processors.*;
import org.mmbase.applications.media.State;
import org.mmbase.servlet.FileServlet;
import org.mmbase.core.event.*;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import org.mmbase.util.logging.*;
import org.w3c.dom.*;



/**
 * This commit-processor is used on node of the type 'streamsources', and is used to initiate
 * conversions to other formats.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CreateCachesProcessor implements CommitProcessor {
    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesProcessor.class);

    public static final String XSD_CREATECACHES       = "createcaches.xsd";
    public static final String NAMESPACE_CREATECACHES = "http://www.mmbase.org/xmlns/createcaches";

    static {
        EntityResolver.registerSystemID(NAMESPACE_CREATECACHES + ".xsd", XSD_CREATECACHES, CreateCachesProcessor.class);
    }

    private static List<JobDefinition> list = new CopyOnWriteArrayList<JobDefinition>();


    private static int transSeq = 0;
    public final ThreadPoolExecutor transcoderExecutor = new ThreadPoolExecutor(3, 3, 5 * 60 , TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return ThreadPools.newThread(r, "TranscoderThread-" + (transSeq++));
            }
        });
    {
        ThreadPools.getThreadPools().put(CreateCachesProcessor.class.getName() + ".transcoders", transcoderExecutor);
    }

    private final List<CommandExecutor.Method> executors = new CopyOnWriteArrayList<CommandExecutor.Method>();
    {
        executors.add(new CommandExecutor.Method());
        executors.add(new CommandExecutor.Method());
        executors.add(new CommandExecutor.Method());
    }

    private String configFile = "streams/createcaches.xml";
    private final ResourceWatcher watcher = new ResourceWatcher() {
            @Override
            public void onChange(String resource) {
                try {
                    LOG.service("Reading " + resource);
                    List<JobDefinition> newList = new ArrayList<JobDefinition>();
                    List<CommandExecutor.Method> newExecutors = new ArrayList<CommandExecutor.Method>();
                    Document document = getResourceLoader().getDocument(resource);
                    if (document != null) {
                        org.w3c.dom.NodeList ellist = document.getDocumentElement().getChildNodes();
                        int totalTranscoders = 0;

                        for (int i = 0; i <= ellist.getLength(); i++) {
                            if (ellist.item(i) instanceof Element) {
                                Element el = (Element) ellist.item(i);
                                if (el.getTagName().equals("transcoder")) {
                                    Transcoder transcoder = (Transcoder) Instantiator.getInstanceWithSubElement(el);
                                    LOG.debug("Created " + transcoder);
                                    JobDefinition def = new JobDefinition(transcoder);
                                    org.w3c.dom.NodeList childs = el.getChildNodes();
                                    for (int j = 0; j <= childs.getLength(); j++) {
                                        if (childs.item(j) instanceof Element) {
                                            Element child = (Element) childs.item(j);
                                            if (child.getTagName().equals("loganalyzer")) {
                                                Analyzer analyzer = (Analyzer) Instantiator.getInstance(child);
                                                def.analyzers.add(analyzer);

                                            }
                                        }
                                    }
                                    newList.add(def);
                                } else if (el.getTagName().equals("localhost")) {
                                    int max = Integer.parseInt(el.getAttribute("max_simultaneous_transcoders"));
                                    totalTranscoders += max;
                                    for (int j = 1; j <= max; j++) {
                                        newExecutors.add(new CommandExecutor.Method());
                                    }
                                } else if (el.getTagName().equals("server")) {
                                    int max = Integer.parseInt(el.getAttribute("max_simultaneous_transcoders"));
                                    totalTranscoders += max;
                                        String host = el.getAttribute("host");
                                        int    port = Integer.parseInt(el.getAttribute("port"));
                                        for (int j = 1; j <= max; j++) {
                                            newExecutors.add(new CommandExecutor.Method(host, port));
                                        }
                                }
                            }
                        }
                        LOG.info("Set max simultaneous transcoders to " + totalTranscoders);
                        transcoderExecutor.setCorePoolSize(totalTranscoders);
                        transcoderExecutor.setMaximumPoolSize(totalTranscoders);
                    } else {
                        LOG.warn("No " + resource);
                    }
                    list.clear();
                    list.addAll(newList);
                    synchronized(executors) {
                        executors.clear();
                        executors.addAll(newExecutors);
                    }
                    LOG.info("Reading of configuration file successfull. Transcoders now " + list + ". Executors " + executors);
                } catch (Exception e)  {
                    LOG.error(e.getClass() + " " + e.getMessage() + " Transcoders now " + list + " (not changed)", e);
                }
            }
        };

    protected void initWatcher() {
        LOG.info("Adding for " + this + " " + configFile);
        watcher.exit();
        watcher.add(configFile);
        watcher.setDelay(10000);
        watcher.onChange();
        watcher.start();
    }
    {
        initWatcher();
    }

    public void setConfigFile(final String configFile) {
        LOG.info("Addding " + configFile);
        this.configFile = configFile;
        initWatcher();
    }




    protected Node getCacheNode(final String cacheManager, final Node node, final String key,  final Logger logger) {
        final NodeManager caches = node.getCloud().getNodeManager(cacheManager);
        NodeQuery q = caches.createQuery();
        Queries.addConstraint(q, Queries.createConstraint(q, "id",  FieldCompareConstraint.EQUAL, node));
        Queries.addConstraint(q, Queries.createConstraint(q, "key", FieldCompareConstraint.EQUAL, key));

        LOG.service("Executing " + q.toSql());
        NodeList nodes = caches.getList(q);
        if (nodes.size() > 0) {
            return nodes.getNode(0);
        }
        return null;
    }

    /**
     * Gets, and if necessary creates, the node representing the 'cached' stream (the result of a
     * conversion).
     * @param node The original node
     * @param mediaprovider
     * @param mediafragment
     * @param transcoder The transcoder providing the 'key'.
     */

    protected Node getCacheNode(final Node node, final Node mediaprovider, final Node mediafragment, final Transcoder t, final Logger logger) {
        assert mediafragment != null;
        assert mediaprovider != null;


        final String key = t.getKey();
        Node resultNode = null;
        for (String cacheType : new String[] {"streamsourcescaches", "videostreamsourcescaches", "audiostreamsourcescaches"}) {
            resultNode = getCacheNode(cacheType, node, key, logger);
            if (resultNode != null) break;
        }

        final NodeManager caches = node.getCloud().getNodeManager(node.getNodeManager().getProperty("org.mmbase.streams.cachestype"));

        if (resultNode != null) {
            resultNode.setIntValue("state",  State.REQUEST.getValue());
            resultNode.commit();
        } else {
            resultNode = caches.createNode();
            resultNode.setIntValue("state",  State.REQUEST.getValue());
            resultNode.setStringValue("key", t.getKey());
            resultNode.setIntValue("format", t.getFormat().toInt());
            resultNode.setIntValue("codec", t.getCodec().toInt());
            resultNode.setNodeValue("id",    node);
            resultNode.commit();

            // virtual field actually creates relation
            resultNode.setNodeValue("mediaprovider", mediaprovider);
            resultNode.setNodeValue("mediafragment", mediafragment);
            resultNode.commit();
            logger.info("Created cache node " + resultNode.getNumber()  + " for provider " + mediaprovider.getNumber() + " fragment " + mediafragment.getNumber());

        }
        return resultNode;
    }




    private static final Map<Integer, Job> runningJobs = Collections.synchronizedMap(new LinkedHashMap<Integer, Job>());


    public static Set<Job> myJobs(UserContext u) {
        Set<Job> myjobs = new LinkedHashSet<Job>();
        synchronized(runningJobs) {
            for (Job j : runningJobs.values()) {
                if (j.user.equals(u.getIdentifier())) {
                    myjobs.add(j);
                }
            }
        }
        return myjobs;
    }
    public static Collection<Job> runningJobs() {
        return Collections.unmodifiableCollection(runningJobs.values());
    }

    public static Job getJob(Node node) {
        return runningJobs.get(node.getNumber());
    }

    public static String cancelJob(Node node) {
        if (node.getCloud().may(ActionRepository.getInstance().get("streams", "cancel_jobs"), null)) {
            Job job = runningJobs.get(node.getNumber());
            if (job == null) {
                return "No job for node #" + node.getNumber();
            } else {
                job.interrupt();
                if (job.future.cancel(true)) {
                    String message = "Canceled " + job.future;
                    job.logger.info(message);
                    return message;
                } else {
                    return "Could not cancel " + job;
                }

            }
        } else {
            return "You may not cancel jobs";
        }
    }

    public List<JobDefinition> getConfiguration() {
        return Collections.unmodifiableList(list);
    }


    private Job createJob(final Node node, final Node mediaprovider, final Node mediafragment, final ChainedLogger logger) {
        Job job = runningJobs.get(node.getNumber());
        if (job != null) {
            // already running
            return null;
        }
        final Job thisJob = new Job(node, logger, list.size());

        runningJobs.put(node.getNumber(), thisJob);
        thisJob.setFuture(transcoderExecutor.submit(new Callable<Integer>() {
                    public Integer call() {
                        thisJob.setThread(Thread.currentThread());
                        int result = 0;
                        try {
                            final List<JobDefinition> clones = new ArrayList<JobDefinition>();
                            try {
                                for (JobDefinition jd : list) {
                                    JobDefinition clone = new JobDefinition(jd);
                                    clones.add(clone);
                                    getCacheNode(node, mediaprovider, mediafragment, clone.transcoder, logger);
                                }
                            } catch (Exception e) {
                                logger.error(e.getClass() + " " + e.getMessage(), e);
                            }
                            LOG.info("Using " + clones);
                            for (final JobDefinition jd : clones) {
                                logger.service("NOW doing " + jd);
                                thisJob.setTranscoder(jd.transcoder);
                                Node cacheNode = CreateCachesProcessor.this.getCacheNode(node, mediaprovider, mediafragment, jd.transcoder, logger);
                                File inFile = new File(FileServlet.getDirectory(), node.getStringValue("url"));
                                URI in = inFile.toURI();
                                StringBuilder buf = new StringBuilder();
                                org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, cacheNode.getNumber(), "/");
                                buf.append(cacheNode.getNumber()).append(".");
                                buf.append(ResourceLoader.getName(inFile.getName())).append(".").append(jd.transcoder.getFormat().toString().toLowerCase());
                                File outFile = new File(FileServlet.getDirectory(), buf.toString().replace("/", File.separator));
                                logger.service("Transcoding with " + jd.transcoder + " for " + in + " -> " + outFile);
                                final List<AnalyzerLogger> analyzerLoggers = new ArrayList<AnalyzerLogger>();
                                for (Analyzer a: jd.analyzers) {
                                    AnalyzerLogger al = new AnalyzerLogger(a.clone(), node, cacheNode);
                                    analyzerLoggers.add(al);
                                    logger.addLogger(al);
                                }
                                try {
                                    cacheNode.setIntValue("state", State.BUSY.getValue());
                                    cacheNode.setStringValue("url", buf.toString());
                                    cacheNode.commit();
                                    if (jd.transcoder instanceof CommandTranscoder) {
                                        // Get free method
                                        CommandExecutor.Method m = null;
                                        synchronized(executors) {
                                            for (CommandExecutor.Method e : executors) {
                                                if (! e.isInUse()) {
                                                    e.setInUse(true);
                                                    m = e;
                                                    break;
                                                }
                                            }
                                        }
                                        if (m == null) {
                                            LOG.error("There should always be a free CommandExecutor. Using LAUCHER now.");
                                        } else {
                                            ((CommandTranscoder) jd.transcoder).setMethod(m);
                                        }
                                    }

                                    jd.transcoder.transcode(in, outFile.toURI(), logger);
                                    for (AnalyzerLogger al : analyzerLoggers) {
                                        al.getAnalyzer().ready(node, cacheNode);
                                    }
                                    if (node.isChanged()) {
                                        node.commit();
                                    }
                                    cacheNode.setLongValue("filesize", outFile.length());
                                    cacheNode.setIntValue("state",
                                                          State.DONE.getValue());
                                    cacheNode.commit();
                                    result++;
                                    logger.info("READY " + thisJob);
                                    if (thisJob.isInterrupted() || Thread.currentThread().isInterrupted()){
                                        cacheNode.setIntValue("state", State.INTERRUPTED.getValue());
                                        cacheNode.commit();
                                        logger.info("Interrupted");
                                        break;
                                    }
                                } catch (InterruptedException ie) {
                                    thisJob.interrupt();
                                    logger.info("Interrupted");
                                    break;
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                } finally {
                                    for (AnalyzerLogger al : analyzerLoggers) {
                                        logger.removeLogger(al);
                                    }
                                }

                            }
                            if (! thisJob.isInterrupted()) {
                                thisJob.ready();
                            }
                        } catch (RuntimeException e) {
                            logger.error(e.getMessage(), e);
                            throw e;
                        } finally {
                            logger.info("READY " + result);
                            runningJobs.remove(thisJob.getNodeNumber());
                        }
                        return result;

                    }
                    })
                );

        return thisJob;

    }

    void createCaches(final Cloud ntCloud, final Node node) {
        if (ntCloud.hasNode(node.getNumber())) {
            final ChainedLogger logger = new ChainedLogger(LOG);
            final Node ntNode = ntCloud.getNode(node.getNumber());
            ntNode.getStringValue("title"); // This triggers RelatedField$Creator to create a
            // mediafragment if it does not yet exist
            final Node mediafragment = ntCloud.getNode(ntNode.getNodeValue("mediafragment").getNumber());
            final Node mediaprovider = ntCloud.getNode(ntNode.getNodeValue("mediaprovider").getNumber());

            LOG.info("Triggering caches for " + list + " Mediaframent " + mediafragment);

            final Job thisJob = createJob(ntNode,
                                          mediaprovider,
                                          mediafragment, logger);
            if (thisJob != null) {

                // If the node happens to be deleted before the future with cache creations is ready, cancel the future
                EventManager.getInstance().addEventListener(new WeakNodeEventListener() {
                        public void notify(NodeEvent event) {
                            if (event.getNodeNumber() == ntNode.getNumber() && event.getType() == Event.TYPE_DELETE) {
                                /*
                                if (thisJob.future.cancel(true)) {
                                    logger.info("Canceled " + thisJob.future + " for " + event.getBuilderName() + " " + event.getNodeNumber());
                                }
                                */
                            }
                        }
                        public String toString() {
                            return "Job canceler for " + node.getNumber();
                        }
                    });
            }
        } else {
            LOG.warn("Node " + node.getNumber() + " is not real.");
        }
    }


    public void commit(final Node node, final Field field) {
        if (node.getNumber() > 0) {
            if (node.isChanged(field.getName())) {

                final Cloud ntCloud = node.getCloud().getNonTransactionalCloud();
                ThreadPools.scheduler.schedule(new Runnable() {
                        public void run() {
                            createCaches(ntCloud, node);
                        }
                    }, 2, TimeUnit.SECONDS);
            } else {
                LOG.debug("Field " + field + " not changed " + node.getChanged());
            }
        } else {
            LOG.info("Cannot execute processor, because node has not yet a real number " + node);
        }

    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        CreateCachesProcessor clone = (CreateCachesProcessor) super.clone();
        LOG.info("CLoned");
        clone.initWatcher();
        return clone;
    }



    public class JobDefinition {
        public final Transcoder transcoder;
        public final List<Analyzer> analyzers;
        JobDefinition(Transcoder t) {
            transcoder = t;
            analyzers = new ArrayList<Analyzer>();
        }
        JobDefinition(JobDefinition jd) {
            transcoder = jd.transcoder.clone();
            analyzers  = jd.analyzers;
        }

        public Transcoder getTranscoder() {
            return transcoder;
        }
        public List<Analyzer> getAnalyzers() {
            return Collections.unmodifiableList(analyzers);
        }

        @Override
        public String toString() {
            return "" + transcoder + " " + analyzers;
        }
    }


    private static long lastJobNumber = 0;
    public class Job {

        private final String user;
        private final int nodeNumber;
        private final BufferedLogger logger;
        private final int size;
        private final long number = lastJobNumber++;

        private int busy = 0;

        private Future<Integer> future;
        private Transcoder transcoder;
        private Thread thread;
        boolean interrupted = false;
        boolean ready = false;

        public Job(Node node, ChainedLogger chain, int s) {
            user = node.getCloud().getUser().getIdentifier();
            nodeNumber = node.getNumber();
            logger = new BufferedLogger();
            logger.setLevel(Level.DEBUG);
            logger.setMaxSize(100);
            logger.setMaxAge(60000);
            chain.addLogger(logger);
            size = s;
        }
        public void setFuture(Future<Integer> f) {
            future = f;
        }
        public Logger getLogger() {
            return logger;
        }

        public Thread getThread() {
            return thread;
        }
        public synchronized void setThread(Thread t) {
            thread = t;
            if (t != null) {
                interrupted  = t.isInterrupted();
            }
        }
        public synchronized void interrupt() {
            interrupted = true;
            if (thread != null) {
                logger.info("Interrupting " + thread);
                thread.interrupt();
            } else {
                logger.info("No Thread in " + this);
            }
        }
        public boolean isInterrupted() {
            return interrupted;
        }
        public boolean isReady() {
            return ready;
        }
        public void ready() {
            ready = true;
        }

        public void setTranscoder(Transcoder t) {
            transcoder = t;
            busy++;
        }
        public Transcoder getTranscoder() {
            return transcoder;
        }
        public String getProgress() {
            return "" + busy + "/" + size;
        }
        public String getUser() {
            return user;
        }
        public long getNumber() {
            return number;
        }
        public int getNodeNumber() {
            return nodeNumber;
        }

        @Override
        public String toString() {
            if (transcoder == null) {
                return number + ":" + user + ":SCHEDULED:" + list;
            } else {
                return number + ": " + user + ":" + transcoder + ":" + getProgress() + ":" + thread;
            }
        }
    }

}
