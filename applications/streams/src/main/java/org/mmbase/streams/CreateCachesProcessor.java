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
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.MimeType;
import org.mmbase.servlet.FileServlet;
import org.mmbase.core.event.*;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import org.mmbase.util.logging.*;
import org.w3c.dom.*;



/**
 * This commit-processor is used on nodes of type 'streamsources' and is used to initiate the
 * conversions to other formats which are saved in 'streamsourcescaches'. Its analogy is derived
 * from the conversion of 'images' in MMBase to their resulting 'icaches' nodes.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CreateCachesProcessor implements CommitProcessor {
    private static final long serialVersionUID = 0L;

    public static String NOT = CreateCachesProcessor.class.getName() + ".DONOT";
    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesProcessor.class);

    public static final String XSD_CREATECACHES       = "createcaches.xsd";
    public static final String NAMESPACE_CREATECACHES = "http://www.mmbase.org/xmlns/createcaches";

    static {
        EntityResolver.registerSystemID(NAMESPACE_CREATECACHES + ".xsd", XSD_CREATECACHES, CreateCachesProcessor.class);
    }

    /**
     * @todo Should not be static
     */
    private static Map<String, JobDefinition> list = Collections.synchronizedMap(new LinkedHashMap<String, JobDefinition>());


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
                    Map<String, JobDefinition> newList = new LinkedHashMap<String, JobDefinition>();
                    List<CommandExecutor.Method> newExecutors = new ArrayList<CommandExecutor.Method>();
                    Document document = getResourceLoader().getDocument(resource);
                    if (document != null) {
                        org.w3c.dom.NodeList ellist = document.getDocumentElement().getChildNodes();
                        int totalTranscoders = 0;

                        for (int i = 0; i <= ellist.getLength(); i++) {
                            if (ellist.item(i) instanceof Element) {
                                Element el = (Element) ellist.item(i);
                                if (el.getTagName().equals("transcoder") || el.getTagName().equals("recognizer")) {
                                    String id = el.getAttribute("id");
                                    Transcoder transcoder;
                                    if (el.getTagName().equals("transcoder")) {
                                        transcoder = (Transcoder) Instantiator.getInstanceWithSubElement(el, id);
                                    } else {
                                        Recognizer recognizer = (Recognizer) Instantiator.getInstanceWithSubElement(el);
                                        transcoder = new RecognizerTranscoder(recognizer, id);
                                    }
                                    String in = el.getAttribute("in");
                                    if (in.length() > 0) {
                                        transcoder.setInId(in);
                                    } else {
                                        transcoder.setInId(null);
                                    }
                                    MimeType mimeType = new MimeType(el.getAttribute("mimetype"));
                                    LOG.debug("Created " + transcoder);
                                    JobDefinition def = new JobDefinition(transcoder, mimeType);
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
                                    if (newList.containsKey(id)) {
                                        LOG.warn("" + newList + " already contains an entry with id " + id);
                                    }
                                    newList.put(id, def);
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
                    list.putAll(newList);
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

    public Map<String, JobDefinition> getConfiguration() {
        return Collections.unmodifiableMap(list);
    }


    /**
     * Creates and submits a jobs transcoding everytthing as configured for one source object, this
     * produces all new 'caches' as configured in createcaches.xml.
     */
    private Job createJob(final Node node, final ChainedLogger logger) {
        Job job = runningJobs.get(node.getNumber());
        if (job != null) {
            // already running
            return null;
        }
        final Job thisJob = new Job(node, logger);
        runningJobs.put(node.getNumber(), thisJob);

        thisJob.setFuture(transcoderExecutor.submit(new Callable<Integer>() {
                    public Integer call() {
                        thisJob.setThread(Thread.currentThread());
                        int resultCount = 0;
                        try {
                            LOG.service("Using " + thisJob.results);
                            for (final Result result : thisJob) {
                                logger.service("NOW doing " + result);
                                URI in  = result.getIn();
                                URI out = result.getOut();

                                JobDefinition jd = result.getJobDefinition();
                                final List<AnalyzerLogger> analyzerLoggers = new ArrayList<AnalyzerLogger>();
                                for (Analyzer a: jd.analyzers) {
                                    AnalyzerLogger al = new AnalyzerLogger(a.clone(), thisJob.getNode(), result.getNode());
                                    analyzerLoggers.add(al);
                                    logger.addLogger(al);
                                }
                                try {
                                    jd.transcoder.transcode(in, out, logger);
                                    for (AnalyzerLogger al : analyzerLoggers) {
                                        al.getAnalyzer().ready(thisJob.getNode(), result.getNode());
                                    }
                                    resultCount++;
                                    result.ready();
                                    logger.info("READY " + thisJob + ":" + result);
                                    if (thisJob.isInterrupted() || Thread.currentThread().isInterrupted()){
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
                            logger.info("READY " + resultCount);
                            runningJobs.remove(thisJob.getNode().getNumber());
                        }
                        return resultCount;

                    }
                    })
                );

        return thisJob;

    }


    /**
     */
    void createCaches(final Cloud ntCloud, final int node) {
        if (ntCloud.hasNode(node)) {
            final ChainedLogger logger = new ChainedLogger(LOG);
            final Node ntNode = ntCloud.getNode(node);
            ntNode.getStringValue("title"); // This triggers RelatedField$Creator to create a
            LOG.info("Triggering caches for " + list + " Mediaframent " + ntNode.getNodeValue("mediafragment").getNumber());

            final Job thisJob = createJob(ntNode, logger);
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
                            return "Job canceler for " + node;
                        }
                    });
            }
        } else {
            LOG.warn("Node " + node  + " is not real.");
        }
    }

    public void commit(final Node node, final Field field) {
        if (node.getCloud().getProperty(NOT) != null) {
            LOG.service("Not doing because of property");
            return;
        }
        if (node.getNumber() > 0) {
            if (node.isChanged(field.getName())) {

                final Cloud ntCloud = node.getCloud().getNonTransactionalCloud();
                final int nodeNumber = node.getNumber();
                ThreadPools.scheduler.schedule(new Runnable() {
                        public void run() {
                            createCaches(ntCloud, nodeNumber);
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
        LOG.info("Cloned");
        clone.initWatcher();
        return clone;
    }



    /**
     * The description or definition of one 'transcoding' sub job that's doing the transcoding. This
     * combines a transcoder, with a mime type for which it must be valid, and a list of analyzers.
     */
    public class JobDefinition {
        final Transcoder transcoder;
        final List<Analyzer> analyzers;
        final MimeType mimeType;

        /**
         * Creates an JobDefinition template (used in the configuration container).
         */
        JobDefinition(Transcoder t, MimeType mt) {
            transcoder = t.clone();
            analyzers = new ArrayList<Analyzer>();
            mimeType = mt;
        }

        public Transcoder getTranscoder() {
            return transcoder;
        }
        public List<Analyzer> getAnalyzers() {
            return Collections.unmodifiableList(analyzers);
        }


        public MimeType getMimeType() {
            return mimeType;
        }

        @Override
        public String toString() {
            return "" + transcoder + " " + analyzers;
        }
    }


    /**
     * Container for the result of a JobDefinition
     */
    public class Result {
        final JobDefinition definition;
        final Node dest;
        final URI in;
        final URI out;
        Result(JobDefinition def, Node dest, URI in, URI out) {
            definition = def;
            this.dest = dest;
            this.in = in;
            this.out = out;
            if (this.dest != null) {
                LOG.info("Setting " + dest.getNumber() + " to request");
                dest.setIntValue("state",  State.REQUEST.getValue());
                dest.commit();
            }
            LOG.info("Created " + this + " " + definition.transcoder.getClass().getName(), new Exception());

        }
        public JobDefinition getJobDefinition() {
            return definition;
        }
        public Node getNode() {
            return dest;
        }
        public MimeType getMimeType() {
            if (dest == null) {
                return null;
            } else {
                return new MimeType(dest.getStringValue("mimetype"));
            }
        }

        public URI getIn() {
            return in;
        }
        public URI getOut() {
            return out;
        }
        public void ready() {
            if (dest != null) {
                LOG.info("Setting " + dest.getNumber() + " to done");
                File outFile = new File(FileServlet.getDirectory(), dest.getStringValue("url").replace("/", File.separator));
                dest.setLongValue("filesize", outFile.length());
                dest.setIntValue("state", State.DONE.getValue());
                dest.commit();

            }
        }

        public String toString() {
            if (dest != null) {
                return dest.getNumber() + ":" + out;
            } else {
                return "(NO RESULT:" + definition.toString();
            }
        }

    }


    private static long lastJobNumber = 0;

    /**
     * A Job is associated with a 'source' node, and describes what is currently happening to create
     * 'caches' nodes for it. Such a Job object is created everytime somebody create a new source
     * object, or explictely triggers the associated 'cache' objects to be (re)created.
     */
    public class Job implements Iterable<Result> {

        private final String user;
        private final Node node;
        private final Node mediaprovider;
        private final Node mediafragment;
        private final BufferedLogger logger;
        private final Map<String, Result> results = new LinkedHashMap<String, Result>();
        private final long number = lastJobNumber++;

        private int busy = 0;

        private Future<Integer> future;

        private Result current;

        private Thread thread;
        boolean interrupted = false;
        boolean ready = false;


        public Job(Node node, ChainedLogger chain) {
            user = node.getCloud().getUser().getIdentifier();
            this.node = node;
            logger = new BufferedLogger();
            logger.setLevel(Level.DEBUG);
            logger.setMaxSize(100);
            logger.setMaxAge(60000);
            chain.addLogger(logger);
            // mediafragment if it does not yet exist
            mediafragment = node.getNodeValue("mediafragment");
            mediaprovider = node.getNodeValue("mediaprovider");
            assert mediafragment != null;
            assert mediaprovider != null;
        }

        /**
         * The several stream cache nodes (which are certain already) get created here.
         */
        protected void createCacheNodes() {
            synchronized(list) {
                for (Map.Entry<String, JobDefinition> entry : list.entrySet()) {

                    // TODO check only create if always must be created or, if mimetype matches with input.
                    JobDefinition jd = entry.getValue();
                    String id = entry.getKey();
                    if (jd.transcoder.getKey() != null) {
                        String inId = jd.transcoder.getInId();
                        if ((inId == null || results.containsKey(inId))) {

                            MimeType inMimeType;
                            if (inId == null) {
                                inMimeType = new MimeType(node.getStringValue("mimetype"));
                            } else {
                                inMimeType = results.get(id).getMimeType();
                            }
                            if (! jd.getMimeType().matches(inMimeType)) {
                                continue;
                            }

                            Node resultNode = getCacheNode(jd.transcoder.getKey());

                            // virtual field actually creates relation
                            resultNode.setNodeValue("mediaprovider", mediaprovider);
                            resultNode.setNodeValue("mediafragment", mediafragment);

                            resultNode.setStringValue("key", jd.transcoder.getKey());
                            Format f = jd.transcoder.getFormat();
                            resultNode.setIntValue("format", f.toInt());
                            Codec c = jd.transcoder.getCodec();
                            if (c == null || c == Codec.UNKNOWN) {
                                resultNode.setValue("codec", null);
                            } else {
                                resultNode.setIntValue("codec", c.toInt());
                            }
                            resultNode.setNodeValue("id",    node);

                            File inFile  = new File(FileServlet.getDirectory(), node.getStringValue("url").replace("/", File.separator));
                            StringBuilder buf = new StringBuilder();
                            org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, node.getNumber(), "/");
                            buf.append(resultNode.getNumber()).append('.').append(ResourceLoader.getName(inFile.getName())).append(".").append(jd.transcoder.getFormat().toString().toLowerCase());
                            String outFileName = buf.toString();
                            resultNode.setStringValue("url", outFileName);

                            resultNode.commit();
                        }
                    }
                }
            }
        }

        public Iterator<Result> iterator() {
            final Iterator<Map.Entry<String, JobDefinition>> i = CreateCachesProcessor.this.list.entrySet().iterator();
            return new Iterator<Result>() {
                Result next;
                {
                    LOG.info("Iterating "+ CreateCachesProcessor.this.list.entrySet());
                    next = findResult();
                }

                protected Result findResult() {
                    createCacheNodes();
                    Result result = null;
                    while (i.hasNext()) {
                        Map.Entry<String, JobDefinition> n = i.next();
                        JobDefinition jd = n.getValue();
                        URI inFile;
                        Node inNode;
                        if (jd.transcoder.getInId() == null) {
                            inFile = new File(FileServlet.getDirectory(), node.getStringValue("url")).toURI();
                            inNode = node;
                        } else {
                            Result prevResult = results.get(n.getKey());
                            if (prevResult == null) {
                                logger.error("No result with id " + n.getKey() + " in " + results + ". Misconfiguration?");
                                continue;
                            }
                            inFile = prevResult.getOut();
                            inNode = prevResult.getNode();
                        }
                        if (jd.transcoder.getKey() != null) {
                            LOG.info("matcing " +  jd.getMimeType() + " of " + jd.transcoder + " with " + new MimeType(inNode.getStringValue("mimetype")));
                            if (jd.getMimeType().matches(new MimeType(inNode.getStringValue("mimetype")))) {
                                Node dest = getCacheNode(jd.transcoder.getKey());
                                if (dest == null) {
                                    LOG.warn("Could not create cache node from " + node.getNodeManager().getName() + " " + node.getNumber() + " for " + jd.transcoder.getKey());
                                    continue;
                                }
                                URI outFile = new File(FileServlet.getDirectory(), dest.getStringValue("url")).toURI();
                                result = new Result(jd, dest, inFile, outFile);
                                break;
                            } else {
                                logger.info("Skipping " + jd + " because " + inFile + " of (" + inNode.getNumber() + ", " + inNode.getStringValue("mimetype") + ") does not match mimetype.");
                            }
                        } else {
                            // recognizers;
                            result = new Result(jd, null, inFile, null);
                            break;
                        }
                    }
                    return result;
                }

                public boolean hasNext() {
                    return next != null;
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                public Result next() {
                    current = next;
                    next = findResult();
                    if (current.definition.transcoder instanceof CommandTranscoder) {
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
                            ((CommandTranscoder) current.definition.transcoder).setMethod(m);
                        }
                    }
                    if (current.getNode() != null) {
                        try {
                            LOG.info("Setting " + current.getNode().getNodeManager().getName() + " " + current.getNode().getNumber() + " to BUSY");
                            current.getNode().setIntValue("state", State.BUSY.getValue());
                            current.getNode().commit();
                        } catch (Exception e) {
                            LOG.error(e);
                        }
                    }
                    busy++;
                    return current;
                }

            };
        }

        public void setFuture(Future<Integer> f) {
            future = f;
        }
        public Logger getLogger() {
            return logger;
        }

        /**
         * Gets the node representing the 'cached' stream (the result of a conversion).
         * @param key   representation of the way the stream was created from its source
         */
        protected Node getCacheNode(final String key) {
            String ct = node.getNodeManager().getProperty("org.mmbase.streams.cachestype");
            if (ct != null) {
                for (String cacheManager : new String[] {"streamsourcescaches", "videostreamsourcescaches", "audiostreamsourcescaches"}) {
                    final NodeManager caches = node.getCloud().getNodeManager(cacheManager);
                    NodeQuery q = caches.createQuery();
                    Queries.addConstraint(q, Queries.createConstraint(q, "id",  FieldCompareConstraint.EQUAL, node));
                    Queries.addConstraint(q, Queries.createConstraint(q, "key", FieldCompareConstraint.EQUAL, key));

                    LOG.debug("Executing " + q.toSql());
                    NodeList nodes = caches.getList(q);
                    if (nodes.size() > 0) {
                        logger.service("Found existing node for " + key + "(" + node.getNumber() + "): " + nodes.getNode(0).getNumber());
                        return nodes.getNode(0);
                    }
                }

                final NodeManager caches = node.getCloud().getNodeManager(node.getNodeManager().getProperty("org.mmbase.streams.cachestype"));
                Node newNode =  caches.createNode();
                newNode.setNodeValue("id", node);
                newNode.setStringValue("key", key);
                newNode.commit();
                logger.service("Created new node for " + key + "(" + node.getNumber() + "): " + newNode.getNumber());
                return newNode;
            } else {
                return null;
            }
        }

        public Result getCurrent() {
            return current;
        }

        /**
         * The Thread in which this Job is running.
         */
        public Thread getThread() {
            return thread;
        }

        public synchronized void setThread(Thread t) {
            thread = t;
            if (t != null) {
                interrupted = t.isInterrupted();
            }
        }
        public synchronized void interrupt() {
            Node cacheNode = current.getNode();
            if (cacheNode != null) {
                cacheNode.setIntValue("state", State.INTERRUPTED.getValue());
                cacheNode.commit();
            }
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

        public String getProgress() {
            return "" + busy + "/" + results.size();
        }
        public String getUser() {
            return user;
        }
        public long getNumber() {
            return number;
        }
        public Node getNode() {
            return node;
        }

        @Override
        public String toString() {
            if (current == null) {
                return number + ":" + user + ":SCHEDULED:" + results;
            } else {
                return number + ": " + user + ":" + current + ":" + getProgress() + ":" + thread;
            }
        }
    }

}
