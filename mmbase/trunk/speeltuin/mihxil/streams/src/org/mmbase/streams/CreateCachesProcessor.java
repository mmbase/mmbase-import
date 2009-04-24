/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams;

import org.mmbase.streams.transcoders.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.*;
import org.mmbase.datatypes.processors.*;
import org.mmbase.applications.media.State;
import java.util.*;
import java.io.*;
import java.net.*;
import org.mmbase.util.logging.*;
import org.mmbase.servlet.FileServlet;


/**
 * This class constains Setter and Getter method for 'binary' file fields. In such field you can set
 * a FileItem, and it is stored as a file, using the FileServlet to produce an URL. The (string)
 * field itself only contains a file name.
 *
 * The file could (and currently is supposed to)  be served with {@link org.mmbase.servlet.FileServlet}.
 *
 * @author Michiel Meeuwissen
 */

public class CreateCachesProcessor implements CommitProcessor {

    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesProcessor.class);

    private final List<Transcoder> list = new ArrayList<Transcoder>();

    {
        // this must be configurable
        list.add(new FFMpeg2TheoraTranscoder());
    }


    protected Node getCacheNode(final Node node, final Transcoder t, Logger logger) {
        final NodeManager caches = node.getCloud().getNonTransactionalCloud().getNodeManager("streamsourcescaches");
        NodeQuery q = caches.createQuery();
        Queries.addConstraint(q, Queries.createConstraint(q, "id", FieldCompareConstraint.EQUAL, node));
        Queries.addConstraint(q, Queries.createConstraint(q, "key", FieldCompareConstraint.EQUAL, t.getKey()));
        NodeList nodes = caches.getList(q);
        Node resultNode;
        if (nodes.size() > 0) {
            resultNode = nodes.get(0);
        } else {
            resultNode = caches.createNode();
            resultNode.setNodeValue("id", node);
            resultNode.setStringValue("key", t.getKey());
            resultNode.setIntValue("state", State.REQUEST.ordinal());
            logger.service("Created " + resultNode);
        }
        return resultNode;
    }



    public void commit(final Node node, final Field field) {
        if (node.isChanged(field.getName())) {
            LOG.info("Field '" + field + " was changed. Triggering caches.");
            final ChainedLogger logger = new ChainedLogger();
            logger.addLogger(Logging.getLoggerInstance("CACHES." + node.getCloud().getUser().getIdentifier()));
            logger.addLogger(LOG);

            for (Transcoder t : list) {
                Node cacheNode = getCacheNode(node, t, logger);
                if (cacheNode.isNew()) {
                    cacheNode.commit();
                }
            }
            ThreadPools.jobsExecutor.execute(new Runnable() {
                    public void run() {
                        for (Transcoder t : list) {
                            logger.service("Creating with " + t);
                            Node cacheNode = CreateCachesProcessor.this.getCacheNode(node, t, logger);
                            File inFile = new File(FileServlet.getDirectory(), node.getStringValue("url"));;
                            URI in = inFile.toURI();
                            String fileName = ResourceLoader.getName(inFile.getName()) + "." + t.getExtension();
                            StringBuilder buf = new StringBuilder();
                            org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, cacheNode.getNumber(), "/");
                            buf.append("/").append(cacheNode.getNumber()).append(".");
                            buf.append(ResourceLoader.getName(inFile.getName())).append(".").append(t.getExtension());
                            File outFile = new File(FileServlet.getDirectory(), buf.toString().replace("/", File.separator));
                            try {
                                cacheNode.setIntValue("state", State.BUSY.ordinal());
                                cacheNode.commit();
                                t.transcode(in, outFile.toURI(), logger);
                                cacheNode.setStringValue("url", buf.toString());
                                cacheNode.setIntValue("state", State.DONE.ordinal());
                                cacheNode.commit();

                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }


                        }
                    }
                });

        }
    }
}
