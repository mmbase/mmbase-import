/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.builders;

import java.util.Map;
import java.util.List;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.Queue;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A ImageRequest Processor is a daemon Thread which can handle image transformations. Normally a few of these are started.
 * Each one contains a Queue of Image request jobs it has to do, which is constantly watched for new jobs.
 *
 * @author Rico Jansen
 * @version $Id: ImageRequestProcessor.java,v 1.15 2004-02-06 14:26:04 pierre Exp $
 * @see ImageRequest
 */
public class ImageRequestProcessor implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ImageRequestProcessor.class);
    private static int idCounter =0;
    private int processorId;

    private MMObjectBuilder icaches;
    private ImageConvertInterface convert;
    private Queue queue;
    private Map table;

    /**
     * @javadoc
     */
    public ImageRequestProcessor(MMObjectBuilder icaches, ImageConvertInterface convert, Queue queue, Map table) {
        this.icaches = icaches;
        this.convert = convert;
        this.queue = queue;
        this.table = table;
        processorId = idCounter++;
        start();
    }

    /**
     * Starts the thread for this ImageRequestProcessor.
     */
    protected void start() {
        Thread kicker = new Thread(this, "ImageConvert[" + processorId +"]");
        kicker.setDaemon(true);
        kicker.start();
    }


    // javadoc inherited (from Runnable)
    public void run() {
        while (true) {
            try {
                log.debug("Waiting for request");
                ImageRequest req = (ImageRequest) queue.get();
                log.debug("Starting request");
                processRequest(req);
                log.debug("Done with request");
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Takes an ImageRequest object and calls setOutput on it (after having determined that).
     * @param req The ImageRequest wich must be executed.
     */
    private void processRequest(ImageRequest req) {

        byte[] picture = null;
        byte [] inputpicture = req.getInput();
        List params = req.getParams();
        String ckey = req.getKey();
        int      id = req.getId();

        try {
            if (inputpicture == null || inputpicture.length == 0) {
                if (log.isDebugEnabled()) log.debug("processRequest : input is empty : " + id);
            } else {
                if (log.isDebugEnabled()) log.debug("processRequest : Converting : " + id);
                picture = convert.convertImage(inputpicture, params);
                if (picture != null) {
                    MMObjectNode newNode = icaches.getNewNode("imagesmodule");
                    newNode.setValue("ckey", ckey);
                    newNode.setValue("id", id);
                    newNode.setValue("handle", picture);
                    newNode.setValue("filesize", picture.length);
                    int i = newNode.insert("imagesmodule");
                    if (i < 0) {
                        log.warn("processRequest: Can't insert cache entry id=" + id + " key=" + ckey);
                    }
                } else {
                    log.warn("processRequest(): Convert problem params : " + params);
                }
                if (log.isDebugEnabled()) log.debug("processRequest : converting done : " + id);
            }
        } finally {
            synchronized (table){
                if (log.isDebugEnabled()) {
                    log.debug("Setting output " + id + " (" + req.count() + " times requested now)");
                }
                req.setOutput(picture);
                if (log.isDebugEnabled()) log.debug("Removing key " + id);
                table.remove(ckey);
            }
        }
    }
}
