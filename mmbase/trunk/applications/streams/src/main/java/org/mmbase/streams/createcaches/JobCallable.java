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
 * @version $Id$
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
            // This triggers RelatedField$Creator to create a mediafragment
            ///ntNode.getStringValue("title");
            //Node mediafragment = ntNode.getNodeValue("mediafragment");
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
                        //LOG.debug("next !");
                        iterator.next();
                    }
                    current = thisJob.getCurrent();

                    LOG.info("Found " + current);
                    if (current.isReady()) {
                        thisJob.ready();
                        Processor.runningJobs.remove(thisJob.getNode().getNumber());
                        //LOG.info("1: returning resultCount: " + resultCount);
                        return resultCount;
                    }

                }
                if (result != null && result.getStage() != current.getStage()) {
                    LOG.service("Will do next stage " + current.getStage() + " now (was " + result + "), first returning");
                    try {
                        thisJob.submit(this);
                    } catch (Exception e) {
                    }
                    //LOG.info("2: returning resultCount: " + resultCount);
                    return resultCount;
                }
                result = current;
                LOG.info("NOW doing " + result);
                final URI in  = result.getIn();
                final URI out = result.getOut();
                LOG.info("in -> out: " + in.toString() + " -> " + out.toString());

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
                thisJob.ready();
                Processor.runningJobs.remove(thisJob.getNode().getNumber());
            }
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            Processor.runningJobs.remove(thisJob.getNode().getNumber());
            throw e;
        } finally {
            logger.info("FINALLY " + resultCount);
            // needing synchronization here
            thisJob.notifyAll(); // notify waiters
        }
        //logger.info("3: returning resultCount: " + resultCount);
        return resultCount;
    }

    @Override
    public String toString() {
        return "JobCallable[" + thisJob + "]";
    }

}
