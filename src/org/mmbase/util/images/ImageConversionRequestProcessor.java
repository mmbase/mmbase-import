/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.images;

import java.util.Map;
import java.util.List;

import org.mmbase.module.core.*;

import org.mmbase.util.Queue;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * An ImageConversionRequest Processor is a daemon Thread which can handle image transformations. Normally a few of these are started.
 * Each one contains a Queue of Image request jobs it has to do, which is constantly watched for new jobs.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see    ImageConversionRequest
 */
public class ImageConversionRequestProcessor implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ImageConversionRequestProcessor.class);
    private static int idCounter =0;
    private int processorId;

    private MMObjectBuilder icaches;
    private ImageConverter convert;
    private ImageInformer  informer;
    private Queue queue;
    private Map table;

    /**
     * @javadoc
     */
    public ImageConversionRequestProcessor(MMObjectBuilder icaches, ImageConverter convert, ImageInformer informer, Queue queue, Map table) {
        this.icaches = icaches;
        this.convert = convert;
        this.queue = queue;
        this.table = table;
        this.informer = informer;
        processorId = idCounter++;
        start();
    }

    /**
     * Starts the thread for this ImageRequestProcessor.
     */
    protected void start() {
        MMBaseContext.startThread(this, "ImageConvert[" + processorId +"]");
    }


    // javadoc inherited (from Runnable)
    public void run() {
        MMBase mmbase = icaches.getMMBase();
        while (!mmbase.isShutdown()) {
            try {
                log.debug("Waiting for request");
                ImageConversionRequest req = (ImageConversionRequest) queue.get();
                log.debug("Starting request");
                processRequest(req);
                log.debug("Done with request");
            } catch (InterruptedException ie) {
                log.debug(Thread.currentThread().getName() +" was interrupted.");
                continue;
            } catch (Exception e) {
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /**
     * Takes an ImageConversionRequest object, and performs the conversion.
     * @param req The ImageConversionRequest wich must be executed.
     */
    private void processRequest(ImageConversionRequest req) throws InterruptedException {

        byte[] picture = null;
        byte[] inputPicture = req.getInput();

        MMObjectNode node = req.getNode();
        if (node == null) {
            log.error("No node in request " + req);
            return;
        }
        String ckey = node.getStringValue(Imaging.FIELD_CKEY);

        try {
            if (inputPicture == null || inputPicture.length == 0) {
                if (log.isDebugEnabled()) log.debug("processRequest : input is empty : " + node);
                // no node gets created, so node remains 'null'.
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("processRequest : Converting : " + node );
                }

                List params = req.getParams();
                picture = convert.convertImage(inputPicture, req.getInputFormat(), params);
                if (picture != null) {
                    node.setValue(Imaging.FIELD_HANDLE, picture);
                    if (node.getBuilder().hasField("height")) {
                        try {
                            Dimension predicted = (Dimension) node.getFunctionValue("dimension", null);
                            Dimension dim = Factory.getImageInformer().getDimension(picture);
                            if (log.isDebugEnabled()) {
                                if (! predicted.equals(dim)) {
                                    log.debug("Predicted dimension " + predicted + " was not equal to resulting dimension " + dim + " for icache " + node.getNumber());
                                }
                            }
                            else {
                                if (! predicted.equalsIgnoreRound(dim, 1)) {
                                    log.warn("Predicted dimension " + predicted + " was not equal to resulting dimension " + dim + " for icache " + node.getNumber());
                                }
                            }
                            node.setValue("height", dim.y);
                            node.setValue("width", dim.x);
                        } catch (java.io.IOException ioe) {
                            log.warn(ioe);
                        }
                    }
                    node.commit();
                } else {
                    log.warn("processRequest(): Convert problem params : " + params);
                }
                if (log.isDebugEnabled()) log.debug("processRequest : converting done : " + node.toString());
            }
        } finally {
            synchronized (table){
                if (log.isDebugEnabled()) {
                    log.debug("Setting output " + node + " (" + req.count() + " times requested now)");
                }
                req.ready();
                if (log.isDebugEnabled()) log.debug("Removing key " + node.getStringValue(Imaging.FIELD_CKEY));
                table.remove(ckey);
            }
        }
    }
}
