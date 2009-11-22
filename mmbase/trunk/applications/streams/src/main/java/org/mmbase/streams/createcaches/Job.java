/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.createcaches;

import org.mmbase.streams.transcoders.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.*;
import org.mmbase.util.externalprocess.CommandExecutor;
import org.mmbase.applications.media.State;
import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.MimeType;
import org.mmbase.servlet.FileServlet;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import org.mmbase.util.logging.*;

/**
 * A Job is associated with a 'source' node, and describes what is currently happening to create
 * 'caches' nodes for it. Such a Job object is created everytime somebody create a new source
 * object, or explicitly triggers the associated 'cache' objects to be (re)created.
 *
 */
public class Job implements Iterable<Result> {
    private static final Logger LOG = Logging.getLoggerInstance(Job.class);
    private static long lastJobNumber = 0;


    private final String user;
    private Node node;
    private Node mediaprovider;
    private Node mediafragment;
    final BufferedLogger logger;
    private final Map<String, Result> lookup = new LinkedHashMap<String, Result>();
    private final List<Result>        results = new ArrayList<Result>();
    private final long number = lastJobNumber++;
    private int busy = 0;

    Future<Integer> future;

    private Result current;

    private Thread thread;
    boolean interrupted = false;
    boolean ready = false;
    final Processor processor;

    public Job(Processor processor, Cloud cloud, ChainedLogger chain) {
        user = cloud.getUser().getIdentifier();
        logger = new BufferedLogger();
        logger.setLevel(Level.DEBUG);
        logger.setMaxSize(100);
        logger.setMaxAge(60000);
        chain.addLogger(logger);
        for (Map.Entry<String, JobDefinition> dum : processor.list.entrySet()) {
            results.add(null);
        }
        this.processor = processor;
    }

    /**
     * Defines the several Results by reading the JobDefinitions in the list.
     * Creates streamsourcescaches for transcoders and asigns TranscoderResults to them or creates
     * RecognizerResults for JobDefinitions of recognizers.
     */
    protected void findResults() {
        int i = -1;
        for (Map.Entry<String, JobDefinition> entry : processor.list.entrySet()) {
            i++;
            if (results.get(i) == null) {
                JobDefinition jd = entry.getValue();
                URI inURI;
                Node inNode;

                // inNode (input stream) to be used
                if (jd.getInId() == null) {
                    String url = node.getStringValue("url");
                    assert url.length() > 0;
                    File f = new File(processor.getDirectory(), url);
                    assert f.exists() : "No such file " + f;
                    inURI = f.toURI();
                    inNode = node;
                } else {
                    if (! processor.list.containsKey(jd.getInId())) {
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

                    if (prevResult.isReady() && inNode.getIntValue("state") == State.FAILED.getValue()) {
                        LOG.warn("BREAK, transcoding of inNode failed " + inNode);
                        break;
                    }
                }

                // mimetype: skip when no match
                if (! jd.getMimeType().matches(new MimeType(inNode.getStringValue("mimetype")))) {
                    LOG.info("SKIPPING " + jd);
                    results.set(i, new SkippedResult(jd, inURI));
                    continue;
                } else {
                    LOG.info("NOT SKIPPING " + jd);
                }

                assert inURI != null;
                // not a recognizer (it has a transcoder key)
                if (jd.transcoder.getKey() != null) {
                    LOG.info(jd.getMimeType());
                    LOG.info("" + inNode);
                    Node dest = getCacheNode(jd.transcoder.getKey());   // gets node (and creates when yet not present)
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

                    File inFile  = new File(processor.getDirectory(), Job.this.node.getStringValue("url").replace("/", File.separator));

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


                    String destFile = dest.getStringValue("url");
                    assert destFile != null;
                    assert destFile.length() > 0;
                    File outFile = new File(processor.getDirectory(), destFile);

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
                    Result result = new TranscoderResult(processor.getDirectory(), jd, dest, inURI, outURI);

                    LOG.info("Added result to results list with key: " + dest.getStringValue("key"));
                    results.set(i, result);
                    lookup.put(jd.getId(), result);
                } else {
                    // recognizers
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
            int i = 0;
            public boolean hasNext() {
                for (int j = i; j < results.size(); j++) {
                    if (results.get(j) != null && ! results.get(j).isReady()) {
                        LOG.debug("Found to do at " + j + " -> " + results.get(j));
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
                    for (; i < results.size(); i++) {
                        if (results.get(i) != null && ! results.get(i).isReady()) {
                            current = results.get(i);
                            Job.this.notifyAll();
                            i++;
                            break;
                        }
                    }
                }

                if (current.definition.transcoder instanceof CommandTranscoder) {
                    // Get free method
                    CommandExecutor.Method m = null;
                    synchronized(processor.executors) {
                        for (CommandExecutor.Method e : processor.executors) {
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
                        LOG.debug("Setting " + destination.getNodeManager().getName() + " " + destination.getNumber() + " to BUSY " + i);
                        destination.setIntValue("state", State.BUSY.getValue());
                        destination.commit();
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
                busy++;
                LOG.debug(" Returning at " + i + " " + current + " (" + current.isReady() + ")");
                return current;
            }

        };
    }

    /**
     * Start actually executing this Jobs.
     */
    public void submit(Cloud cloud, int n, ChainedLogger chain) {
        JobCallable callable = new JobCallable(this, cloud, chain, n);
        submit(callable);
    }

    /**
     * Re-submit this job.
     */
    void submit(final JobCallable jc)  {
       if (getStage() == Stage.READY) {
           LOG.info("Will not submit, because we're ready" + jc);
       } else {
           LOG.info("Will submit " + jc);
           ThreadPools.jobsExecutor.execute(new Runnable() {
                   public void run() {
                       jc.init();
                       synchronized(Job.this) {
                           findResults();
                           if (getCurrent() == null) {
                               iterator().next();
                           }
                           Stage s = getCurrent().getStage();
                           LOG.info("to " + s);
                           future = processor.threadPools.get(s).submit(jc);
                           Job.this.notifyAll();
                       }
                   }
               });
       }
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
            for (String cacheManager : processor.cacheManagers) {
                if (src.getCloud().hasNodeManager(cacheManager)) { // may not be the case during junit tests e.g.
                    final NodeManager caches = src.getCloud().getNodeManager(cacheManager);
                    NodeQuery q = caches.createQuery();
                    Queries.addConstraint(q, Queries.createConstraint(q, "id",  FieldCompareConstraint.EQUAL, src));
                    Queries.addConstraint(q, Queries.createConstraint(q, "key", FieldCompareConstraint.EQUAL, key));

                    LOG.debug("Execute query " + q.toSql());
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

    /**
     * The source Node on which this Job will run.
     */
    public void setNode(Node n) {
        node = n;
        // mediafragment if it does not yet exist
        mediafragment = node.getNodeValue("mediafragment");
        mediaprovider = node.getNodeValue("mediaprovider");
        assert mediafragment != null : "Mediafragment should not be null";
        //assert mediaprovider != null : "Mediaprovider should not be null";
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
    public boolean reached(Stage s) {
        LOG.info("Comparing for " + getStage() + ">=" + s);
        return getStage().ordinal() >= s.ordinal();
    }
    synchronized public void ready() {
        ready = true;
        notifyAll();
    }

    public synchronized void waitUntil(Stage stage)
                                 throws InterruptedException {
        LOG.info("Waiting for " + stage);
        while (! reached(stage)) {
            wait();
        }
    }

    public synchronized void waitUntilAfter(Stage stage) throws InterruptedException {
        LOG.info("Waiting untill after " + stage);
        while (getStage().ordinal() <= stage.ordinal()) {
            wait();
        }
    }

    public Stage getStage() {
        if (ready) return Stage.READY;
        Result res = getCurrent();
        return res == null ? Stage.UNSTARTED : res.getJobDefinition().getStage();
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

