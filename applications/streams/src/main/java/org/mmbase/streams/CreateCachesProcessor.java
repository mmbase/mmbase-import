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
 * Several transcodings of media files can be configured with recognizers and transcoders. The
 * recognizer with id 'recognizer' can be configured before the transcodings start to look
 * if the sources contain the correct type (video, audio or image).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CreateCachesProcessor implements CommitProcessor, java.io.Externalizable {
    private static final long serialVersionUID = 0L;

    public static String NOT = CreateCachesProcessor.class.getName() + ".DONOT";
    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesProcessor.class);

    public static final String XSD_CREATECACHES       = "createcaches.xsd";
    public static final String NAMESPACE_CREATECACHES = "http://www.mmbase.org/xmlns/createcaches";

    static {
        EntityResolver.registerSystemID(NAMESPACE_CREATECACHES + ".xsd", XSD_CREATECACHES, CreateCachesProcessor.class);
    }

    /**
     */
    protected final Map<String, JobDefinition> list = Collections.synchronizedMap(new LinkedHashMap<String, JobDefinition>());


    private  String[] cacheManagers = new String[] {"streamsourcescaches", "videostreamsourcescaches", "audiostreamsourcescaches"};

    private File fileServletDirectory;

    public void setDirectory(File dir) {
        fileServletDirectory = dir;
    }
    public File getDirectory() {
        if (fileServletDirectory == null) {
            return FileServlet.getDirectory();
        } else {
            return fileServletDirectory;
        }
    }

    public void setCacheManagers(String... cm) {
        cacheManagers = cm;
    }

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
    protected final ResourceWatcher watcher = new ResourceWatcher() {
            @Override
            public void onChange(String resource) {
                try {
                    LOG.debug("Reading " + resource);
                    Map<String, JobDefinition> newList = new LinkedHashMap<String, JobDefinition>();
                    List<CommandExecutor.Method> newExecutors = new ArrayList<CommandExecutor.Method>();
                    Document document = getResourceLoader().getDocument(resource);
                    int totalTranscoders = 0;

                    if (document != null) {
                        org.w3c.dom.NodeList ellist = document.getDocumentElement().getChildNodes();

                        Stage prevStage = Stage.RECOGNIZER;
                        for (int i = 0; i <= ellist.getLength(); i++) {
                            if (ellist.item(i) instanceof Element) {
                                Element el = (Element) ellist.item(i);
                                if (el.getTagName().equals("transcoder") || el.getTagName().equals("recognizer")) {
                                    String id = el.getAttribute("id");
                                    Transcoder transcoder;
                                    if (el.getTagName().equals("transcoder")) {
                                        transcoder = (Transcoder) Instantiator.getInstanceWithSubElement(el);
                                    } else {
                                        Recognizer recognizer = (Recognizer) Instantiator.getInstanceWithSubElement(el);
                                        transcoder = new RecognizerTranscoder(recognizer);
                                    }
                                    String in = el.getAttribute("in");
                                    String label = el.getAttribute("label");
                                    MimeType mimeType = new MimeType(el.getAttribute("mimetype"));
                                    LOG.debug("Created " + transcoder);
                                    Stage stage = Stage.valueOf(el.getTagName().toUpperCase());
                                    if (stage.ordinal() < prevStage.ordinal()) {
                                        LOG.warn("Wrong ordering " + stage + " < " + prevStage);
                                    }
                                    prevStage = stage;
                                    JobDefinition def = new JobDefinition(id, in.length() > 0 ? in : null, label.length() > 0 ? label : null, transcoder, mimeType, stage);
                                    org.w3c.dom.NodeList childs = el.getChildNodes();
                                    for (int j = 0; j <= childs.getLength(); j++) {
                                        if (childs.item(j) instanceof Element) {
                                            Element child = (Element) childs.item(j);
                                            if (child.getTagName().equals("loganalyzer")) {
                                                try {
                                                    Analyzer analyzer = (Analyzer) Instantiator.getInstance(child);
                                                    def.analyzers.add(analyzer);
                                                } catch (Exception e) {
                                                    LOG.error(XMLWriter.write(child) + ": " + e.getMessage(), e);
                                                }

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
                    LOG.service("Reading of configuration file " + resource + " successfull. Transcoders now " + list + ". Executors " + executors + ". Max simultaneous transcoders: " + totalTranscoders);
                } catch (Exception e)  {
                    LOG.error(e.getClass() + " " + e.getMessage() + " In " + resource + " Transcoders now " + list + " (not changed)", e);
                }
            }
        };

    protected void initWatcher() {
        LOG.service("Adding for " + this + " " + configFile);
        watcher.exit();
        watcher.add(configFile);
        watcher.setDelay(10000);
        watcher.onChange();
        watcher.start();
    }
    public CreateCachesProcessor() {
        initWatcher();
    }
    CreateCachesProcessor(final String configFile) {
        setConfigFile(configFile);
    }


    public void setConfigFile(final String configFile) {
        LOG.service("Adding " + configFile);
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
                    String message = "Canceled job for node #" + node.getNumber() + " (" + job.future + ")";
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
     * Creates and submits a job transcoding everything as configured for one source object, this
     * produces all new 'caches' as configured in createcaches.xml.
     * @param node      source stream
     * @param logger    a logger that keeps track
     * @return job trans coding a source stream in (an)other stream(s)
     */
    private Job createJob(final Cloud ntCloud, final int node, final ChainedLogger logger) {
        synchronized(runningJobs) {
            Job job = runningJobs.get(node);
            if (job != null) {
                LOG.warn("This job is already running, node #" + node);
                // already running
                return null;
            }
            final Job thisJob = new Job(ntCloud, logger);
            runningJobs.put(node, thisJob);

            thisJob.setFuture(transcoderExecutor.submit(new Callable<Integer>() {
                        public Integer call() {
                            thisJob.setThread(Thread.currentThread());
                            if (ntCloud instanceof org.mmbase.bridge.implementation.BasicCloud) {
                                try {
                                    synchronized(ntCloud) {
                                        while (! ntCloud.hasNode(node)) {
                                            ntCloud.wait(200);
                                        }
                                    }
                                } catch (InterruptedException ie) {
                                    LOG.info(ie);
                                    return null;
                                }
                            }
                            final Node ntNode = ntCloud.getNode(node);
                            ntNode.getStringValue("title"); // This triggers RelatedField$Creator to create a mediafragment
                            Node mediafragment = ntNode.getNodeValue("mediafragment");
                            thisJob.setNode(ntNode);
                            int resultCount = 0;
                            try {
                                LOG.info("Executing " + thisJob);
                                for (final Result result : thisJob) {
                                    LOG.info("NOW doing " + result);
                                    URI in  = result.getIn();
                                    URI out = result.getOut();

                                    JobDefinition jd = result.getJobDefinition();
                                    final List<AnalyzerLogger> analyzerLoggers = new ArrayList<AnalyzerLogger>();
                                    for (Analyzer a: jd.analyzers) {
                                        AnalyzerLogger al = new AnalyzerLogger(a.clone(), thisJob.getNode(), result.getDestination());
                                        analyzerLoggers.add(al);
                                        logger.addLogger(al);
                                    }
                                    assert in != null;

                                    try {
                                        jd.transcoder.transcode(in, out, logger);
                                        for (AnalyzerLogger al : analyzerLoggers) {
                                            al.getAnalyzer().ready(thisJob.getNode(), result.getDestination());
                                        }
                                        resultCount++;
                                        result.ready();
                                        logger.info("RESULT " + thisJob + "(" + thisJob.getNode().getNodeManager().getName() + ":" + thisJob.getNode().getNumber() + "):" + result);
                                        if (thisJob.isInterrupted() || Thread.currentThread().isInterrupted()){
                                            logger.info("Interrupted");
                                            break;
                                        }
                                    } catch (InterruptedException ie) {
                                        thisJob.interrupt();
                                        logger.info("Interrupted");
                                        break;
                                    } catch (Throwable e) {
                                        result.ready();
                                        logger.error(e.getMessage(), e);
                                    } finally {
                                        for (AnalyzerLogger al : analyzerLoggers) {
                                            logger.removeLogger(al);
                                        }
                                    }
                                    thisJob.findResults();

                                }
                                if (! thisJob.isInterrupted()) {
                                    logger.info("READY " + thisJob + "(" + thisJob.getNode().getNodeManager().getName() + ":" + thisJob.getNode().getNumber() + ")");
                                    //thisJob.getNode().commit();
                                }
                            } catch (RuntimeException e) {
                                logger.error(e.getMessage(), e);
                                throw e;
                            } finally {
                                logger.info("FINALLY " + resultCount);
                                thisJob.ready(); // notify waiters
                                runningJobs.remove(thisJob.getNode().getNumber());
                            }
                            return resultCount;
                        }

                    })
                );

            return thisJob;
        }

    }


    /**
     * Creates caches nodes when not existing
     * @param ntCloud   cloud
     * @param int       node number
     */
    Job createCaches(final Cloud ntCloud, final int node) {
        final ChainedLogger logger = new ChainedLogger(LOG);
        final Job thisJob = createJob(ntCloud, node, logger);

        LOG.info("Triggering caches for " + list + "  -> " + thisJob);


        if (thisJob != null) {

            // If the node happens to be deleted before the future with cache creations is ready, cancel the future
            EventManager.getInstance().addEventListener(new WeakNodeEventListener() {
                    public void notify(NodeEvent event) {
                        if (event.getNodeNumber() == node && event.getType() == Event.TYPE_DELETE) {
                            /*
                              if (thisJob.future.cancel(true)) {
                              logger.info("Canceled " + thisJob.future + " for " + event.getBuilderName() + " " + event.getNodeNumber());
                              }
                            */
                        }
                    }
                    @Override
                        public String toString() {
                        return "Job canceled for " + node;
                    }
                });
        }
        return thisJob;
    }

    public void commit(final Node node, final Field field) {
        if (node.getCloud().getProperty(NOT) != null) {
            LOG.service("Not doing because of property");
            return;
        }
        if (node.getNumber() > 0) {

            if (node.isChanged(field.getName())) {
                LOG.service("For node " + node.getNumber() + ", the field '" + field.getName() + "' is changed " + node.getChanged() + ". That means that we must schedule create caches");
                final Cloud ntCloud = node.getCloud().getNonTransactionalCloud();
                final int nodeNumber = node.getNumber();
                createCaches(ntCloud, nodeNumber);
            } else {
                LOG.debug("Field " + field + " not changed " + node.getChanged());
            }
        } else {
            LOG.info("Cannot execute processor, because node has not yet a real number " + node);
        }

    }
    @Override
    protected CreateCachesProcessor clone() throws CloneNotSupportedException {
        CreateCachesProcessor clone = (CreateCachesProcessor) super.clone();
        LOG.info("Cloned");
        clone.initWatcher();
        return clone;
    }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput stream) throws IOException {
    }

    /**
     * The description or definition of one 'transcoding' sub job that's doing the transcoding. This
     * combines a transcoder, with a mime type for which it must be valid, and a list of analyzers.
     */
    public class JobDefinition implements Serializable {
        private static final long serialVersionUID = 0L;
        final Transcoder transcoder;
        final List<Analyzer> analyzers;
        final MimeType mimeType;

        final String inId;
        final String id;
        final String label;

        final Stage stage;
        /**
         * Creates an JobDefinition template (used in the configuration container).
         */
        JobDefinition(String id, String inId, String label, Transcoder t, MimeType mt, Stage s) {
            assert id != null;
            transcoder = t.clone();
            analyzers = new ArrayList<Analyzer>();
            mimeType = mt;
            this.id = id;
            this.inId = inId;
            this.label = label;
            this.stage = s;
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
        public Stage getStage() {
            return stage;
        }
        public String getId() {
            return id;
        }
        public String getInId() {
            return inId;
        }
        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return "" + transcoder + " " + analyzers + "(" + label + ")";
        }
    }


    public abstract class Result {
        final JobDefinition definition;
        final URI in;
        boolean ready = false;
        Result(JobDefinition def, URI in) {
            assert in != null;
            definition = def;
            this.in = in;
        }

        public JobDefinition getJobDefinition() {
            return definition;
        }

        public abstract Node getDestination();

        //public abstract Node getNode();

        public abstract URI getOut();

        public URI getIn() {
            return in;
        }
        public void ready() {
            ready = true;

        }
        public boolean isReady() {
            return ready;
        }
        public abstract MimeType getMimeType();

    }

    /**
     * Container for the result of a JobDefinition
     * @TODO constructors correspond to more or less essentially different situation, perhaps clearer to use 2 extensions
     */
    public class TranscoderResult extends Result {
        final Node dest;
        final URI out;

        TranscoderResult(JobDefinition def, Node dest, URI in, URI out) {
            super(def, in);
            assert out != null;
            this.dest = dest;
            this.out = out;
            LOG.info("Setting " + dest.getNumber() + " to request");
            dest.setIntValue("state",  State.REQUEST.getValue());
            dest.commit();
            LOG.info("Created Result " + this + " " + definition.transcoder.getClass().getName());
        }

        public Node getDestination() {
            return dest;
        }

        public URI getOut() {
            return out;
        }
        public void ready() {
            super.ready();
            if (dest != null) {
                LOG.info("Setting " + dest.getNumber() + " to done");
                File outFile = new File(getDirectory(), dest.getStringValue("url").replace("/", File.separator));
                dest.setLongValue("filesize", outFile.length());
                dest.setIntValue("state", State.DONE.getValue());
                if (definition.getLabel() != null && dest.getNodeManager().hasField("label")) {
                    dest.setStringValue("label", definition.getLabel());
                }
                dest.commit();
            }
        }

        @Override
        public String toString() {
            if (dest != null) {
                return dest.getNumber() + ":" + out;
            } else {
                return "(NO RESULT:" + definition.toString() + ")";
            }

        }
        public MimeType getMimeType() {
            return new MimeType(getDestination().getStringValue("mimetype"));
        }


    }
    public class RecognizerResult extends Result {
        final Node source;
        RecognizerResult(JobDefinition def, Node source, URI in) {
            super(def, in);
            this.source = source;
        }
        public Node getSource() {
            return source;
        }
        public Node getDestination() {
            return null;
        }
        public URI getOut() {
            return getIn();
        }
        public MimeType getMimeType() {
            return new MimeType(getSource().getStringValue("mimetype"));
        }
        @Override
        public void ready() {
            super.ready();
            if (definition.getLabel() != null && source.getNodeManager().hasField("label")) {
                source.setStringValue("label", definition.getLabel());
            }

        }
    }
    public class SkippedResult extends Result {
        SkippedResult(JobDefinition def, URI in) {
            super(def, in);
        }
        public Node getDestination() {
            return null;
        }
        public URI getOut() {
            return null;
        }
        public MimeType getMimeType() {
            return null;

        }
        public boolean isReady() {
            return true;
        }
    }


    private static long lastJobNumber = 0;

    /**
     * A Job is associated with a 'source' node, and describes what is currently happening to create
     * 'caches' nodes for it. Such a Job object is created everytime somebody create a new source
     * object, or explictely triggers the associated 'cache' objects to be (re)created.
     *
     */
    public class Job implements Iterable<Result> {

        private final String user;
        private Node node;
        private Node mediaprovider;
        private Node mediafragment;
        private final BufferedLogger logger;
        private final Map<String, Result> lookup = new LinkedHashMap<String, Result>();
        private final List<Result>        results = new ArrayList<Result>();
        private final long number = lastJobNumber++;
        private int busy = 0;

        private Future<Integer> future;

        private Result current;

        private Thread thread;
        boolean interrupted = false;
        boolean ready = false;


        public Job(Cloud cloud, ChainedLogger chain) {
            user = cloud.getUser().getIdentifier();
            logger = new BufferedLogger();
            logger.setLevel(Level.DEBUG);
            logger.setMaxSize(100);
            logger.setMaxAge(60000);
            chain.addLogger(logger);
        }

        /**
         * The several stream cache nodes (which are certain already) get created here.
         * It checks if the source node is not of state 'SOURCE_UNSUPPORTED'.
         */
        protected void createCacheNodes() {
            LOG.debug("state: " + node.getIntValue("state") + " nodenr: " + node.getNumber());
            if ( node.getIntValue("state") == State.SOURCE_UNSUPPORTED.getValue() ) {
                LOG.warn("Source not supported: " + node.getNumber() + " " + node.getStringValue("url") + " " + node.getStringValue("format"));
                return;
            }
            LOG.info("Results are now " + results);
            synchronized(CreateCachesProcessor.this.list) {
                for (Map.Entry<String, JobDefinition> entry : CreateCachesProcessor.this.list.entrySet()) {

                    JobDefinition jd = entry.getValue();
                    String id = entry.getKey();

                    if (jd.transcoder.getKey() != null) {

                        String inId = jd.getInId();
                        LOG.info(jd + ": " + inId);
                        if ((inId == null || lookup.containsKey(inId))) {

                        } else {

                            LOG.info("Skipping " + jd + " because inid '" + inId + "' is not yet in " + results);
                        }
                    }
                }
            }
        }
        protected void findResults() {
            // createCacheNodes();
            int i = -1;
            for (Map.Entry<String, JobDefinition> n : CreateCachesProcessor.this.list.entrySet()) {
                i++;
                if (results.get(i) == null) {
                    JobDefinition jd = n.getValue();
                    URI inURI;
                    Node inNode;
                    if (jd.getInId() == null) {
                        String url = node.getStringValue("url");
                        assert url.length() > 0;
                        File f = new File(getDirectory(), url);
                        assert f.exists() : "No such file " + f;
                        inURI = f.toURI();
                        inNode = node;
                    } else {
                        if (! CreateCachesProcessor.this.list.containsKey(jd.getInId())) {
                            LOG.warn("Configuration error, no such job definition with id '" + jd.getInId());
                            continue;
                        }
                        Result prevResult = lookup.get(jd.getInId());
                        if (prevResult == null || ! prevResult.isReady()) {
                            // no result possible yet.
                            continue;
                        }
                        inURI = prevResult.getOut();
                        inNode = prevResult.getDestination();
                        if (inNode == null) {
                            inNode = node;
                        }

                    }

                    if (! jd.getMimeType().matches(new MimeType(inNode.getStringValue("mimetype")))) {
                        LOG.info("SKIPPING " + jd);
                        results.set(i, new SkippedResult(jd, inURI));
                        continue;
                    } else {
                        LOG.info("NOT SKIPPING " + jd);
                    }

                    assert inURI != null;
                    if (jd.transcoder.getKey() != null) {
                        LOG.info(jd.getMimeType());
                        LOG.info("" + inNode);
                        Node dest = getCacheNode(jd.transcoder.getKey());
                        if (dest == null) {
                            LOG.warn("Could not create cache node from " + node.getNodeManager().getName() + " " + node.getNumber() + " for " + jd.transcoder.getKey());
                            continue;
                        }

                        assert mediafragment != null;
                        // virtual field actually creates relation
                        dest.setNodeValue("mediaprovider", mediaprovider);
                        dest.setNodeValue("mediafragment", mediafragment);

                        Format f = jd.transcoder.getFormat();
                        dest.setIntValue("format", f.toInt());
                        Codec c = jd.transcoder.getCodec();
                        if (c == null || c == Codec.UNKNOWN) {
                            dest.setValue("codec", null);
                        } else {
                            dest.setIntValue("codec", c.toInt());
                        }
                        dest.setNodeValue("id", Job.this.node);

                        File inFile  = new File(getDirectory(), Job.this.node.getStringValue("url").replace("/", File.separator));

                        StringBuilder buf = new StringBuilder();
                        org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, Job.this.node.getNumber(), "/");
                        buf.append("/");
                        buf.append(dest.getNumber()).append('.').append(ResourceLoader.getName(inFile.getName())).append(".").append(jd.transcoder.getFormat().toString().toLowerCase());
                        String outFileName = buf.toString();
                        if (outFileName.startsWith("/")) {
                            outFileName = outFileName.substring(1);
                        }
                        assert outFileName != null;
                        assert outFileName.length() > 0;
                        dest.setStringValue("url", outFileName);

                        dest.commit();


                        System.out.println("Created " + dest);
                        String destFile = dest.getStringValue("url");
                        assert destFile != null;
                        assert destFile.length() > 0;
                        File outFile = new File(getDirectory(), destFile);

                        if (FileServlet.getInstance() != null) {
                            File inMeta = FileServlet.getInstance().getMetaFile(inFile);
                            if (inMeta.exists()) {
                                Map<String, String> meta = FileServlet.getInstance().getMetaHeaders(inFile);
                                String cd = meta.get("Content-Disposition");
                                if (cd != null) {
                                    String inDisposition =  cd.substring("attachment; filename=".length());
                                    if (inDisposition.startsWith("\"") && inDisposition.endsWith("\"")) {
                                        inDisposition = inDisposition.substring(1, inDisposition.length() - 1);
                                    }
                                    String outDisposition = ResourceLoader.getName(inDisposition) + "." + jd.transcoder.getFormat().toString().toLowerCase();
                                    meta.put("Content-Disposition", "attachment; filename=\"" + outDisposition + "\"");

                                    FileServlet.getInstance().setMetaHeaders(outFile, meta);
                                }
                            }
                        }

                        URI outURI = outFile.toURI();
                        Result result = new TranscoderResult(jd, dest, inURI, outURI);
                        LOG.info("Added result to results list with key: " + dest.getStringValue("key"));
                        results.set(i, result);
                        lookup.put(jd.getId(), result);
                    } else {
                        // recognizers;
                        Result result = new RecognizerResult(jd, inNode, inURI);
                        results.set(i, result);
                        lookup.put(jd.getId(), result);
                    }

                }
            }
            LOG.info("RESULTS now " + results);

        }

        public Iterator<Result> iterator() {
            return new Iterator<Result>() {
                int i = -1;
                public boolean hasNext() {
                    for (int j = i + 1 ; j < results.size(); j++) {
                        if (results.get(j) != null && ! results.get(j).isReady()) {
                            return true;
                        }
                    }
                    return false;
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                public Result next() {
                    synchronized(Job.this) {
                        while(current == null || current.isReady()) {
                            i++;
                            current = results.get(i);
                            Job.this.notifyAll();
                        }
                    }
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
                    Node destination = current.getDestination();
                    if (destination != null) {
                        try {
                            LOG.info("Setting " + destination.getNodeManager().getName() + " " + destination.getNumber() + " to BUSY");
                            destination.setIntValue("state", State.BUSY.getValue());
                            destination.commit();
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
             * Gets and/or creates the node representing the 'cached' stream.
             *
             * @param key   representation of the way the stream was created from its source
         * @return cached stream node
         */
        protected Node getCacheNode(final String key) {
            return getCacheNode(node, key);
        }



        /**
         * Gets and/or creates the node representing the 'cached' stream (the result of a conversion),
         * see the builder property 'org.mmbase.streams.cachestype'. It first looks if it already
         * exists in the cloud or otherwise will create one.
         *
         * @param src   source node to create stream from
         * @param key   representation of the way the stream was created from its source
         * @return cached stream node
         */
        protected Node getCacheNode(Node src, final String key) {
            String ct = src.getNodeManager().getProperty("org.mmbase.streams.cachestype");
            if (ct != null) {
                for (String cacheManager : cacheManagers) {
                    if (src.getCloud().hasNodeManager(cacheManager)) { // may not be the case during junit tests e.g.
                        final NodeManager caches = src.getCloud().getNodeManager(cacheManager);
                        NodeQuery q = caches.createQuery();
                        Queries.addConstraint(q, Queries.createConstraint(q, "id",  FieldCompareConstraint.EQUAL, src));
                        Queries.addConstraint(q, Queries.createConstraint(q, "key", FieldCompareConstraint.EQUAL, key));

                        LOG.debug("Executing " + q.toSql());
                        NodeList nodes = caches.getList(q);
                        if (nodes.size() > 0) {
                            logger.service("Found existing node for " + key + "(" + src.getNumber() + "): " + nodes.getNode(0).getNumber());
                            return nodes.getNode(0);
                        }
                    }
                }

                final NodeManager caches = src.getCloud().getNodeManager(src.getNodeManager().getProperty("org.mmbase.streams.cachestype"));
                Node newNode =  caches.createNode();
                newNode.setNodeValue("id", src);
                newNode.setStringValue("key", key);

                newNode.commit();
                LOG.info("CREATED " + src.getNumber() + " " + key);

                logger.service("Created new node for " + key + "(" + src.getNumber() + "): " + newNode.getNumber());
                return newNode;
            } else {
                throw new IllegalStateException("No property 'org.mmbase.streams.cachestype' in " + src.getNodeManager());
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
            notifyAll();
            if (t != null) {
                interrupted = t.isInterrupted();
            }
        }
        public void setNode(Node n) {
            node = n;
            // mediafragment if it does not yet exist
            mediafragment = node.getNodeValue("mediafragment");
            mediaprovider = node.getNodeValue("mediaprovider");
            assert mediafragment != null : "Mediafragment should not be null";
            //assert mediaprovider != null : "Mediaprovider should not be null";
            for (Map.Entry<String, JobDefinition> dum : CreateCachesProcessor.this.list.entrySet()) {
                results.add(null);
            }
            findResults();
        }
        public synchronized void interrupt() {
            Node cacheNode = current.getDestination();
            if (cacheNode != null && node.getCloud().hasNode(cacheNode.getNumber())) {
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
        public synchronized void ready() {
            ready = true;
            notifyAll();
        }

        public synchronized void waitUntilReady() throws InterruptedException {
            while (! isReady()) {
                wait();
            }
        }
        public Stage getStage() {
            if (ready) return Stage.READY;
            Result res = getCurrent();
            return res == null ? Stage.UNSTARTED : res.getJobDefinition().getStage();
        }

        public void waitUntilTranscoding() throws InterruptedException {
            Stage stage = getStage();
            while (stage.ordinal() < Stage.TRANSCODER.ordinal()) {
                synchronized(this) {
                    LOG.info("Not yet transcoding, but " + stage + " " + getCurrent());
                    wait(100000);
                }
                stage = getStage();
            }
        }

        public String getProgress() {
            return "" + busy + "/" + results.size();
        }
        public int getBusy() {
            return busy;
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
        public Node getMediafragment() {
            return mediafragment;
        }

        @Override
        public String toString() {
            if (current == null) {
                return number + ": " + user + ":SCHEDULED:" + results;
            } else {
                return number + ": " + user + ":" + current + ":" + getProgress() + ":" + thread;
            }
        }
    }


    public static enum Stage {
        UNSTARTED,
        RECOGNIZER,
        TRANSCODER,
        READY;
    }

}
