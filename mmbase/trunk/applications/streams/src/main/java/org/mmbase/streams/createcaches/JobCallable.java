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

import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import org.mmbase.util.logging.*;


/**
 * This is the actual callable that can be submitted to the Executors.
 * It can be submitted multiple times. Until it is ready.
 *
 * It will do that itself, if it detects that the {@link Stage} of the job would change.
 *
 * This boils down to iterating the {@link Job}.
 *
 * @author Michiel Meeuwissen
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
        assert node > 0;

    }

    Job getJob() {
        return thisJob;
    }


    /**
     * Init will wait until the associated source node does actually exist.
     * It will then also create the related mediafragment.
     */
    protected synchronized void init() {
        if (ntNode == null) {
            if (ntCloud instanceof org.mmbase.bridge.implementation.BasicCloud) {
                try {
                    synchronized(ntCloud) {
                        while (! ntCloud.hasNode(node)) {
                            ntCloud.wait(1000);
                            LOG.info("Still no node " + node + "");
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
            notifyAll();
        }
        if (iterator == null) {
            iterator = thisJob.iterator();
        }
    }


    void waitForNode() throws InterruptedException {
        synchronized(this) {
            while(thisJob.getNode() == null) {
                wait();
            }
        }
    }


    public Integer call() {
        init();
        thisJob.setThread(Thread.currentThread());
        int resultCount = 0;
        try {
            Result result = thisJob.getCurrent();
            while (true) {
                LOG.info("Checking to execute " + thisJob);
                Result current = thisJob.getCurrent();
                if (current == null || current.isReady()) {
                    if (iterator.hasNext()) {
                        LOG.info("next !");
                        iterator.next();
                    }
                    current = thisJob.getCurrent();

                    LOG.info("Found " + current);
                    if (current.isReady()) {
                        thisJob.ready();
                        LOG.info("1: returning resultCount: " + resultCount);
                        return resultCount;
                    }

                }
                if (result != null && result.getStage() != current.getStage()) {
                    LOG.info("Will do next stage " + current.getStage() + " now (was " + result + "), first returning");
                    try {
                        thisJob.submit(this);
                    } catch (Exception e) {
                    }
                    LOG.info("2: returning resultCount: " + resultCount);
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
        logger.info("3: returning resultCount: " + resultCount);
        return resultCount;
    }

}
