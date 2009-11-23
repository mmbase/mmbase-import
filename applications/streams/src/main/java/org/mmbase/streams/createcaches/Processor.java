/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.createcaches;

import org.mmbase.streams.transcoders.*;
import org.mmbase.streams.createcaches.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.security.UserContext;
import org.mmbase.security.ActionRepository;
import org.mmbase.util.*;
import org.mmbase.util.xml.*;
import org.mmbase.util.externalprocess.CommandExecutor;
import org.mmbase.datatypes.processors.*;
import org.mmbase.applications.media.MimeType;
import org.mmbase.servlet.FileServlet;
import org.mmbase.core.event.*;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
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

public class Processor implements CommitProcessor, java.io.Externalizable {
    private static final long serialVersionUID = 0L;

    public static String NOT = Processor.class.getName() + ".DONOT";
    private static final Logger LOG = Logging.getLoggerInstance(Processor.class);

    public static final String XSD_CREATECACHES       = "createcaches.xsd";
    public static final String NAMESPACE_CREATECACHES = "http://www.mmbase.org/xmlns/createcaches";

    static {
        EntityResolver.registerSystemID(NAMESPACE_CREATECACHES + ".xsd", XSD_CREATECACHES, Processor.class);
    }

    /**
     */
    protected final Map<String, JobDefinition> list = Collections.synchronizedMap(new LinkedHashMap<String, JobDefinition>());


    String[] cacheManagers = new String[] {"streamsourcescaches", "videostreamsourcescaches", "audiostreamsourcescaches"};

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

    public final Map<Stage, ThreadPoolExecutor> threadPools = new EnumMap<Stage, ThreadPoolExecutor>(Stage.class);
    final List<CommandExecutor.Method> executors = new CopyOnWriteArrayList<CommandExecutor.Method>();
    {
        threadPools.put(Stage.TRANSCODER, new ThreadPoolExecutor(3, 3, 5 * 60 , TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    return ThreadPools.newThread(r, "TranscoderThread-" + Stage.TRANSCODER + "-" + (transSeq++));
                }
            }));
        threadPools.put(Stage.RECOGNIZER, new ThreadPoolExecutor(3, 3, 5 * 60 , TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return ThreadPools.newThread(r, "TranscoderThread-" + Stage.RECOGNIZER + "-" + (transSeq++));
            }
            }));
        // fill the complete map, so we don't have to think about it any more later on.
        for (Stage s : Stage.values()) {
            if (s.ordinal() < Stage.TRANSCODER.ordinal()) {
                threadPools.put(s, threadPools.get(Stage.RECOGNIZER));
            }
            if (s.ordinal() > Stage.RECOGNIZER.ordinal()) {
                threadPools.put(s, threadPools.get(Stage.TRANSCODER));
            }
        }
        for (Map.Entry<Stage, ThreadPoolExecutor> e : threadPools.entrySet()) {
            ThreadPools.getThreadPools().put(Processor.class.getName() + "." + e.getKey().toString(), e.getValue());
        }

        for (int i = 0; i < 6; i++) {
            executors.add(new CommandExecutor.Method());
        }
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
                    Map<Stage, Integer> totals = new EnumMap<Stage, Integer>(Stage.class);

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
                                        LOG.warn("Wrong ordering (config err) " + stage + " < " + prevStage);
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
                                                    def.addAnalyzer(analyzer);
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
                                    Stage s = Stage.valueOf(el.getAttribute("stage").toUpperCase());
                                    Integer t = totals.get(s);
                                    if (t == null) t = 0;
                                    t += max;
                                    totals.put(s, t);
                                    for (int j = 1; j <= max; j++) {
                                        newExecutors.add(new CommandExecutor.Method());
                                    }
                                } else if (el.getTagName().equals("server")) {
                                    int max = Integer.parseInt(el.getAttribute("max_simultaneous_transcoders"));
                                    Stage s = Stage.valueOf(el.getAttribute("stage").toUpperCase());
                                    Integer t = totals.get(s);
                                    if (t == null) t = 0;
                                    t += max;
                                    totals.put(s, t);
                                    String host = el.getAttribute("host");
                                    int    port = Integer.parseInt(el.getAttribute("port"));
                                    for (int j = 1; j <= max; j++) {
                                        newExecutors.add(new CommandExecutor.Method(host, port));
                                    }
                                }
                            }
                        }
                        for (Map.Entry<Stage, Integer> e : totals.entrySet()) {
                            threadPools.get(e.getKey()).setCorePoolSize(e.getValue());
                            threadPools.get(e.getKey()).setMaximumPoolSize(e.getValue());
                        }
                    } else {
                        LOG.warn("No " + resource);
                    }
                    list.clear();
                    list.putAll(newList);
                    synchronized(executors) {
                        executors.clear();
                        executors.addAll(newExecutors);
                    }
                    LOG.service("Reading of configuration file " + resource + " successfull. Transcoders now " + list + ". Executors " + executors + ". Max simultaneous transcoders: " + totals);
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
    public Processor() {
        initWatcher();
    }
    Processor(final String configFile) {
        setConfigFile(configFile);
    }


    public void setConfigFile(final String configFile) {
        LOG.service("Adding " + configFile);
        this.configFile = configFile;
        initWatcher();
    }

    protected static final Map<Integer, Job> runningJobs = Collections.synchronizedMap(new LinkedHashMap<Integer, Job>());

    /* Jobs user with this context has started */
    public static Set<Job> myJobs(UserContext u) {
        Set<Job> myjobs = new LinkedHashSet<Job>();
        synchronized(runningJobs) {
            for (Job j : runningJobs.values()) {
                if (j.getUser().equals(u.getIdentifier())) {
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
    
    protected static boolean removeJob(Node node) {
        Job job = runningJobs.get(node.getNumber());
        boolean done = job.future.isDone();
        if (done) {
            runningJobs.remove(node.getNumber());
        } else {
            job.logger.info("This job has not completed yet.");
        }
        return done;
    }

    public static String cancelJob(Node node) {
        if (node.getCloud().may(ActionRepository.getInstance().get("streams", "cancel_jobs"), null)) {
            Job job = runningJobs.get(node.getNumber());
            if (job == null) {
                return "No job for node #" + node.getNumber();
            } else {
                job.interrupt();
                String msg = "";
                if (job.future.isDone()) {
                    msg = "This job has already completed. ";
                    job.logger.info(msg);
                }
                if (job.future.cancel(true)) {
                    String message = msg + "Canceled job for node #" + node.getNumber() + " (" + job.future + ")";
                    job.logger.info(message);
                    removeJob(node);
                    return message;
                } else {
                    return msg + "Could not cancel " + job;
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
     * produces all new 'streamssourcescaches' as configured in createcaches.xml.
     * @param node      source stream
     * @param logger    a logger that keeps track
     * @return job trans coding a source stream in (an)other stream(s)
     */
    private Job createJob(final Cloud ntCloud, final int node, final ChainedLogger logger) {
        synchronized(runningJobs) {
            Job job = runningJobs.get(node);
            if (job != null) {  // already running?
                LOG.warn("This job is already running, node #" + node);
                return null;
            }
            final Job thisJob = new Job(this, ntCloud, logger);
            runningJobs.put(node, thisJob);

            thisJob.submit(ntCloud, node, logger);

            return thisJob;
        }
    }


    /**
     * Creates caches nodes when not existing by creating a transcoding Job
     * @param ntCloud   a non transactional cloud
     * @param int       node number
     * @return Job recognizing and/or transcoding the source stream
     */
    public Job createCaches(final Cloud ntCloud, final int node) {
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

    /**
     * Commits the mediastreamsources node, calls createCaches to create resulting streamscaches nodes
     * and start transcoding.
     */
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
    protected Processor clone() throws CloneNotSupportedException {
        Processor clone = (Processor) super.clone();
        LOG.info("Cloned");
        clone.initWatcher();
        return clone;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput stream) throws IOException {
    }

}
