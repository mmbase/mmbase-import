/*

This file is part of the MMBase Streams application,
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.streams.createcaches;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;

import org.mmbase.applications.media.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.servlet.FileServlet;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.streams.transcoders.CommandTranscoder;
import org.mmbase.util.*;
import org.mmbase.util.externalprocess.CommandExecutor;
import org.mmbase.util.logging.*;

/**
 * A Job is associated with a 'source' node, and describes what is currently happening to create
 * 'caches' nodes for it. Such a Job object is created everytime somebody creates a new source
 * object, or explicitly triggers the associated 'cache' objects to be (re)created.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
 
public class Job implements Iterable<Result> {
    private static final Logger LOG = Logging.getLoggerInstance(Job.class);
    private static long lastJobNumber = 0;


    private final String user;
    private Node node;
    private Node mediaprovider;
    private Node mediafragment;
    final BufferedLogger logger;
    private Map<String, JobDefinition> jobdefs = new LinkedHashMap<String, JobDefinition>();
    private final Map<String, Result> lookup = new LinkedHashMap<String, Result>();
    private final List<Result>        results = new ArrayList<Result>();
    private final long number = lastJobNumber++;
    private int busy = 0;
    private int skipped = 0;

    Future<Integer> future;

    private Result current;

    private Thread thread;
    boolean interrupted = false;
    boolean ready = false;
    final Processor processor;

    public Job(Processor processor, Cloud cloud, ChainedLogger chain) {
        this(processor, processor.list, cloud, chain);
    }
    
    /**
     * A Job is defined by several {@link JobDefinition}'s, the output is goes to loggers in 
     * {@link ChainedLogger}
     *
     * @param processor reads config, is called by user etc.
     * @param list      the definitions
     * @param cloud     mmbase cloud nodes belong to
     * @param chain     loggers
     */
    public Job(Processor processor, Map<String, JobDefinition> list, Cloud cloud, ChainedLogger chain) {
        user = cloud.getUser().getIdentifier();
        logger = new BufferedLogger();
        logger.setLevel(Level.DEBUG);
        logger.setMaxSize(100);
        logger.setMaxAge(60000);
        chain.addLogger(logger);
        for (Map.Entry<String, JobDefinition> dum : list.entrySet()) {
            results.add(null);
        }
        this.processor = processor;
        this.jobdefs = list;
    }

    /**
     * Defines the several {@link Result}s by reading the {@link JobDefinition}s in the list.
     * Creates streamsourcescaches for {@link Transcoder}s and asigns {@link TranscoderResult}s to 
     * them or creates {@link RecognizerResult}s for {@link JobDefinition}s of recognizers.
     */
    protected void findResults() {
        int i = -1;
        for (Map.Entry<String, JobDefinition> entry : jobdefs.entrySet()) {
            i++;
            if (results.get(i) == null) {
                JobDefinition jd = entry.getValue();
                URI inURI;
                Node inNode; // inNode (input stream) to be used

                if (jd.getInId() == null) { // using the original source node
                    String url = node.getStringValue("url");
                    if (url.length() < 0) LOG.error("No value for field url: " + url);
                    assert url.length() > 0;
                    File f = new File(processor.getDirectory(), url);
                    LOG.service("New (in)file: " + f);
                    assert f.exists() : "No such file " + f;
                    
                    // make sure there is an in file to use
                    int w = 0;
                    while (!f.exists() && !f.isFile() && w < 30) {
                        LOG.service("Checking if (in)file exists '" + f + "'. Waiting 5 sec. to be sure filesystem is ready (" + w + ")");
                        try {
                            getThread().sleep(5000);
                            w++;
                        } catch (InterruptedException ie) {
                            LOG.info("Interrupted" + ie);
                        }
                    }
                    if (!f.exists() && !f.isFile()) LOG.error("NO INFILE! '" + f + "', but continuing anyway.");
                    
                    inURI = f.toURI();
                    inNode = node;
                
                } else {    // using a previously cached node
                    String inId = jd.getInId();
                    if (! jobdefs.containsKey(inId) && node.getCloud().hasNode(inId)) {
                        // use an existing cache node
                        
                        inNode = node.getCloud().getNode(inId);
                        String url = inNode.getStringValue("url");
                        if (url.length() < 0) {
                            LOG.error("No value for field url: " + url);
                            break;
                        }

                        File f = new File(processor.getDirectory(), url);
                        LOG.service("Using (in)file '" + f + "' of cache #" + inId + " as input");
                        
                        if (!f.exists() && !f.isFile()) {
                            LOG.error("NO INFILE! '" + f );
                            break;
                        }
                        
                        inURI = f.toURI();
                        
                    } else {    // use inId from config
                        if (! jobdefs.containsKey(inId)) {
                            LOG.warn("Configuration error, no such job definition with id: " + inId);
                        continue;
                    }
                        Result prevResult = lookup.get(inId);
                    if (prevResult == null || ! prevResult.isReady()) {
                        // no result possible yet.
                        continue;
                    }
                    inURI = prevResult.getOut();
                    inNode = prevResult.getDestination();
                    
                    }
                    
                    if (inNode == null) {
                        inNode = node;
                    }

                    if (inNode.getIntValue("state") > State.SOURCE.getValue()) {
                        LOG.warn("BREAK, transcoding of inNode failed, it is removed, interrupted or unsupported #" + inNode);
                        break;
                    }
                }

                // mimetype: skip when there is no match between current jd and inNode
                if (! jd.getMimeType().matches(new MimeType(inNode.getStringValue("mimetype")))) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("SKIPPING " + jd);
                    }
                    results.set(i, new SkippedResult(jd, inURI));
                    skipped++;
                    continue;
                }

                assert inURI != null;
               
                if (jd.transcoder.getKey() != null) {  // not a recognizer (it has a transcoder key)
                    LOG.service(jd.getMimeType());
                    LOG.service("inNode: " + inNode);
                    Node dest = getCacheNode(inNode, jd.transcoder.getKey());   // gets node (and creates when yet not present)
                    if (dest == null) {
                        LOG.warn("Could not create cache node from " + node.getNodeManager().getName() + " " + node.getNumber() + " for " + jd.transcoder.getKey());
                        continue;
                    }

                    assert mediafragment != null;
                    // virtual field actually creates relation
                    dest.setNodeValue("mediaprovider", mediaprovider);
                    dest.setNodeValue("mediafragment", mediafragment);

                    File inFile  = new File(processor.getDirectory(), Job.this.node.getStringValue("url").replace("/", File.separator));

                    StringBuilder buf = new StringBuilder();
                    org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, Job.this.node.getNumber(), "/");
                    buf.append("/");
                    buf.append(dest.getNumber()).append('.').append(ResourceLoader.getName(inFile.getName())).append(".").append(jd.transcoder.getFormat().toString().toLowerCase());
                    String outFileName = buf.toString();
                    if (outFileName.startsWith("/")) {
                        outFileName = outFileName.substring(1);
                    }
                    
                    LOG.service("outFileName: '" + outFileName + "'");
                    assert outFileName != null;
                    assert outFileName.length() > 0;
                    dest.setStringValue("url", outFileName);
                    jd.transcoder.init(dest);

                    dest.commit();
                    

                    String destFileName = dest.getStringValue("url");
                    
                    // XXX: Sometimes a commit fails (partly), doing it and checking it again  */
                    int w = 0;
                    while (destFileName.length() < 1 && w < 10) {
                        LOG.warn("No value for field url: '" + destFileName + "' in #" + dest.getNumber() + ", committing it again and trying again in 5 sec. (" + w + ")");
                        
                        dest.setStringValue("url", outFileName);
                        dest.commit();
                        
                        try {
                            getThread().sleep(5000);
                            w++;
                        } catch (InterruptedException ie) {
                            LOG.info("Interrupted" + ie);
                        }
                        
                        destFileName = dest.getStringValue("url");
                    }
                    
                    if (destFileName.length() < 1) {
                        LOG.error("Still empty destFileName: '" + destFileName + "' of #" + dest.getNumber());
                    } else {
                        LOG.service("destFileName: '" + destFileName + "'");
                    }
                    assert destFileName != null;
                    assert destFileName.length() > 0;
                    
                    File outFile = new File(processor.getDirectory(), destFileName);
                    if (outFile.exists()) { 
                        if (outFile.delete()) {
                            LOG.service("Former version of file '" + outFile + "' deleted");
                        } else {
                            LOG.error("Could not remove former version of file '" + outFile + "'");
                        }
                    }

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

                    LOG.service("Added result to results list with key: " + dest.getStringValue("key"));
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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Found to do at " + j + " -> " + results.get(j));
                        }
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
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Setting " + destination.getNodeManager().getName() + " " + destination.getNumber() + " " + destination.getStringValue("id") + "/" + destination.getStringValue("key") + " to BUSY " + i);
                        }
                        destination.setIntValue("state", State.BUSY.getValue());
                        destination.commit();
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
                busy++;
                if (LOG.isDebugEnabled()) {
                    LOG.debug(" Returning at " + i + " " + current + " (" + current.isReady() + ")");
                }
                return current;
            }

        };
    }

    /**
     * Start actually executing this Job by submitting it at {@link JobCallable}.
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
           LOG.service("Will not submit, because we're ready " + jc);
       } else {
           LOG.service("Will submit " + jc);
           ThreadPools.jobsExecutor.execute(new Runnable() {
                   public void run() {
                       jc.init();
                       synchronized(Job.this) {
                           findResults();
                           if (getCurrent() == null) {
                               iterator().next();
                           }
                           Stage s = getCurrent().getStage();
                           LOG.service(jc.toString() + " to " + s);
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
     * Gets and/or creates the node representing the 'cached' stream, uses this (source) node as
     * infile to create cache from.
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
     * exists, if not it creates a new one.
     *
     * @param src   source node to create cache stream from, can be another cache
     * @param key   representation of the way the stream was created from its source, f.e. transcoding parameters
     * @return cache stream node, in builder specified in 'org.mmbase.streams.cachestype'.
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

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Execute query " + q.toSql());
                    }
                    NodeList nodes = caches.getList(q);
                    if (nodes.size() > 0) {
                        logger.service("Found existing node for " + key + "(" + src.getNumber() + "): " + nodes.getNode(0).getNumber());
                        return nodes.getNode(0);
                    }
                }
            }

            final NodeManager caches = src.getCloud().getNodeManager(src.getNodeManager().getProperty("org.mmbase.streams.cachestype"));
            Node newNode = caches.createNode();
            newNode.setNodeValue("id", src);
            newNode.setStringValue("key", key);

            newNode.commit();
            LOG.service("CREATED " + newNode.getNumber() + " (" + src.getNumber() + "/" + key + ")");

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
     * Thread in which this Job is running.
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
     * Source Node on which this Job will run.
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
            logger.service("Interrupting " + thread);
            thread.interrupt();
        } else {
            logger.service("No Thread in " + this);
        }
    }
    public boolean isInterrupted() {
        return interrupted;
    }
    public boolean reached(Stage s) {
        if (LOG.isDebugEnabled()) LOG.debug("Comparing for " + getStage() + ">=" + s);
        return getStage().ordinal() >= s.ordinal();
    }

    synchronized public void ready() {
        if (isInterrupted()) {
            ready = true;
        }
        notifyAll();

        if (future.isDone()) {
            processor.runningJobs.remove(getNode().getNumber());
            /* this makes no sence: 
            ready = true;
        } else {
            LOG.warn("This job has not completed yet."); */
        }
        ready = true;
    }

    public synchronized void waitUntil(Stage stage)
                                 throws InterruptedException {
        LOG.service("Waiting for " + stage);
        while (! reached(stage)) {
            wait();
        }
    }

    public synchronized void waitUntilAfter(Stage stage) throws InterruptedException {
        LOG.service("Waiting untill after " + stage);
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
        int done = busy + skipped;
        return "" + done + "/" + results.size();
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

