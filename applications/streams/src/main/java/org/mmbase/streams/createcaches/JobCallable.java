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
 * This is the actual callable that can be submitted to the Executors.
 * It can actually be submitted multiple times. Until it is ready.
 */
class JobCallable implements Callable<Integer> {
    private static final Logger LOG = Logging.getLoggerInstance(JobCallable.class);
    private final Job thisJob;
    private final Cloud ntCloud;
    private final ChainedLogger logger;
    private final int node;
    private Node ntNode;
    private Iterator<Result> iterator;

    public JobCallable(Job j, Cloud cloud, ChainedLogger l, int node) {
        this.thisJob = j;
        this.ntCloud = cloud;
        this.logger = l;
        this.node   = node;
        init();

    }
    protected void init() {
        if (ntNode == null) {
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
                    return;
                }
            }
            ntNode = ntCloud.getNode(node);
            ntNode.getStringValue("title"); // This triggers RelatedField$Creator to create a mediafragment
            Node mediafragment = ntNode.getNodeValue("mediafragment");
            thisJob.setNode(ntNode);
        }
        if (iterator == null) {
            iterator = thisJob.iterator();
        }
    }

    public Integer call() {

        int resultCount = 0;
        try {
            Result result = thisJob.getCurrent();
            while (true) {
                LOG.info("Executing " + thisJob);
                Result current = thisJob.getCurrent();
                if (current == null || current.isReady()) {
                    while (iterator.hasNext()) {
                        iterator.next();
                    }
                    current = thisJob.getCurrent();

                    LOG.info("Found " + current);
                    if (current.isReady()) {
                        thisJob.ready();
                        return resultCount;

                    }

                }
                if (result != null && result.getStage() != current.getStage()) {
                    LOG.info("Will do next stage " + current.getStage() + " now (was " + result + "), first returning");
                    thisJob.submit(this);
                    return resultCount;
                }
                result = current;
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
            //thisJob.ready(); // notify waiters
            //runningJobs.remove(thisJob.getNode().getNumber());
        }
        return resultCount;
    }

}
