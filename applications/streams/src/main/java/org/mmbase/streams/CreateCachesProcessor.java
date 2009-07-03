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

    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesProcessor.class);

    public static final String XSD_CREATECACHES       = "createcaches.xsd";
    public static final String NAMESPACE_CREATECACHES = "http://www.mmbase.org/xmlns/createcaches";

    static {
        EntityResolver.registerSystemID(NAMESPACE_CREATECACHES + ".xsd", XSD_CREATECACHES, CreateCachesProcessor.class);
    }

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
                                    }
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


    private Job createJob(final Node node, final ChainedLogger logger) {
        Job job = runningJobs.get(node.getNumber());
        if (job != null) {
            // already running
            return null;
        }
        final Job thisJob = new Job(node, logger, list);
        runningJobs.put(node.getNumber(), thisJob);

        thisJob.setFuture(transcoderExecutor.submit(new Callable<Integer>() {
                    public Integer call() {
                        thisJob.setThread(Thread.currentThread());
                        int result = 0;
                        try {
                            LOG.service("Using " + thisJob.clones);
                            for (final JobDefinition jd : thisJob) {
                                logger.service("NOW doing " + jd);
                                URI in = jd.getIn();
                                URI out = jd.getOut();

                                final List<AnalyzerLogger> analyzerLoggers = new ArrayList<AnalyzerLogger>();
                                for (Analyzer a: jd.analyzers) {
                                    AnalyzerLogger al = new AnalyzerLogger(a.clone(), thisJob.getNode(), jd.getResultNode());
                                    analyzerLoggers.add(al);
                                    logger.addLogger(al);
                                }
                                try {
                                    jd.transcoder.transcode(in, out, logger);
                                    for (AnalyzerLogger al : analyzerLoggers) {
                                        al.getAnalyzer().ready(thisJob.getNode(), jd.getResultNode());
                                    }
                                    result++;
                                    logger.info("READY " + thisJob);
                                    if (thisJob.isInterrupted() || Thread.currentThread().isInterrupted()){
                                        Node cacheNode = jd.getResultNode();
                                        if (cacheNode != null) {
                                            cacheNode.setIntValue("state", State.INTERRUPTED.getValue());
                                            cacheNode.commit();
                                        }
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
                            runningJobs.remove(thisJob.getNode().getNumber());
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
            LOG.info("Triggering caches for " + list + " Mediaframent " + node.getNodeValue("mediafragment").getNumber());

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
        LOG.info("Cloned");
        clone.initWatcher();
        return clone;
    }



    /**
     * The description or definition of a job that's doing the transcoding.
     */
    public class JobDefinition {
        public final Transcoder transcoder;
        public final Node dest;
        public final List<Analyzer> analyzers;
        public final URI in;
        public final URI out;
        JobDefinition(Transcoder t) {
            transcoder = t;
            analyzers = new ArrayList<Analyzer>();
            dest = null;
            in = null;
            out = null;
        }
        JobDefinition(JobDefinition jd, Node dest, URI in, URI out) {
            transcoder = jd.transcoder.clone();
            analyzers  = jd.analyzers;
            this.dest = dest;
            this.in = in;
            this.out = out;
        }

        public Transcoder getTranscoder() {
            return transcoder;
        }
        public List<Analyzer> getAnalyzers() {
            return Collections.unmodifiableList(analyzers);
        }
        public Node getResultNode() {
            return dest;
        }

        public URI getIn() {
            return in;
        }
        public URI getOut() {
            return out;
        }

        @Override
        public String toString() {
            return "" + transcoder + " " + analyzers;
        }
    }


    private static long lastJobNumber = 0;
    public class Job implements Iterable<JobDefinition> {

        private final String user;
        private final Node node;
        private final Node mediaprovider;
        private final Node mediafragment;
        private final BufferedLogger logger;
        private final Map<String, JobDefinition> clones = new LinkedHashMap<String, JobDefinition>();
        private final long number = lastJobNumber++;

        private int busy = 0;

        private Future<Integer> future;

        private JobDefinition current;

        private Thread thread;
        boolean interrupted = false;
        boolean ready = false;


        public Job(Node node, ChainedLogger chain, Map<String, JobDefinition> list) {
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
            File inFile = new File(FileServlet.getDirectory(), node.getStringValue("url"));

            try {
                synchronized(list) {
                    for (Map.Entry<String, JobDefinition> entry : list.entrySet()) {
                        JobDefinition jd = entry.getValue();
                        String id = entry.getKey();
                        if (jd.transcoder.getFormat() != null) {
                            Node resultNode = getCacheNode(jd.transcoder.getKey());
                            resultNode.setIntValue("state",  State.REQUEST.getValue());
                            resultNode.setStringValue("key", jd.transcoder.getKey());
                            resultNode.setIntValue("format", jd.transcoder.getFormat().toInt());
                            resultNode.setIntValue("codec", jd.transcoder.getCodec().toInt());
                            resultNode.setNodeValue("id",    node);
                            resultNode.commit();

                            StringBuilder buf = new StringBuilder();
                            org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, resultNode.getNumber(), "/");
                            buf.append(resultNode.getNumber()).append(".");
                            buf.append(ResourceLoader.getName(inFile.getName())).append(".").append(jd.transcoder.getFormat().toString().toLowerCase());
                            String outFileName = buf.toString();
                            resultNode.setStringValue("url", outFileName);
                            URI in = inFile.toURI();
                            File outFile = new File(FileServlet.getDirectory(), outFileName.replace("/", File.separator));


                            // virtual field actually creates relation
                            resultNode.setNodeValue("mediaprovider", mediaprovider);
                            resultNode.setNodeValue("mediafragment", mediafragment);
                            resultNode.commit();
                            logger.info("Created cache node " + resultNode.getNumber()  + " for provider " + mediaprovider.getNumber() + " fragment " + mediafragment.getNumber());
                            URI inURI;
                            if (jd.transcoder.getInId() == null) {
                                inURI = inFile.toURI();
                            } else {
                                JobDefinition other = clones.get(jd.transcoder.getInId());
                                if (other == null) {
                                    logger.warn("No job definition with id '" + jd.transcoder.getInId() + "' found");
                                    inURI = null;
                                } else {
                                    inURI = other.getOut();
                                }
                            }
                            if (inURI != null) {
                                JobDefinition clone = new JobDefinition(jd, resultNode, inURI, outFile.toURI());
                                clones.put(id, clone);
                            }
                        } else {

                            JobDefinition clone = new JobDefinition(jd, null, inFile.toURI(), null);
                            logger.info("Cachenode less job" + clone);
                            clones.put(id, clone);

                        }

                    }

                }
            } catch (Exception e) {
                chain.error(e.getClass() + " " + e.getMessage(), e);
            }
        }

        public Iterator<JobDefinition> iterator() {
            final Iterator<Map.Entry<String, JobDefinition>> i = clones.entrySet().iterator();
            return new Iterator<JobDefinition>() {
                public boolean hasNext() {
                    return i.hasNext();
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                public JobDefinition next() {
                    if (current != null && current.getResultNode() != null) {
                        File outFile = new File(FileServlet.getDirectory(), current.getResultNode().getStringValue("url").replace("/", File.separator));
                        current.getResultNode().setLongValue("filesize", outFile.length());
                        current.getResultNode().setIntValue("state",
                                                            State.DONE.getValue());
                        current.getResultNode().commit();
                    }
                    current = i.next().getValue();
                    if (current.transcoder instanceof CommandTranscoder) {
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
                            ((CommandTranscoder) current.transcoder).setMethod(m);
                        }
                    }
                    busy++;
                    if (current.getResultNode() != null) {
                        current.getResultNode().setIntValue("state", State.BUSY.getValue());
                    }
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
         * @param cacheManager
         * @param node  the original node from which the 'cached' stream was created
         * @param key   representation of the way the stream was created from its source
         * @param logger
         */
        protected Node getCacheNode(final String key) {

            for (String cacheManager : new String[] {"streamsourcescaches", "videostreamsourcescaches", "audiostreamsourcescaches"}) {
                final NodeManager caches = node.getCloud().getNodeManager(cacheManager);
                NodeQuery q = caches.createQuery();
                Queries.addConstraint(q, Queries.createConstraint(q, "id",  FieldCompareConstraint.EQUAL, node));
                Queries.addConstraint(q, Queries.createConstraint(q, "key", FieldCompareConstraint.EQUAL, key));

                LOG.service("Executing " + q.toSql());
                NodeList nodes = caches.getList(q);
                if (nodes.size() > 0) {
                    return nodes.getNode(0);
                }
            }
            final NodeManager caches = node.getCloud().getNodeManager(node.getNodeManager().getProperty("org.mmbase.streams.cachestype"));
            Node newNode =  caches.createNode();
            newNode.setNodeValue("id", node);
            newNode.setStringValue("key", key);
            newNode.commit();
            return newNode;
        }


        public JobDefinition getCurrent() {
            return current;
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

        public String getProgress() {
            return "" + busy + "/" + clones.size();
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
                return number + ":" + user + ":SCHEDULED:" + clones;
            } else {
                return number + ": " + user + ":" + current + ":" + getProgress() + ":" + thread;
            }
        }
    }

}
