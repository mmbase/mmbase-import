/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.images;

import java.util.Map;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.io.*;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * An ImageConversionRequest Processor is a daemon Thread which can handle image transformations. Normally a few of these are started.
 * Each one contains a BlockingQueue of Image request jobs it has to do, which is constantly watched for new jobs.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id: ImageConversionRequestProcessor.java,v 1.6 2009-04-22 08:20:05 michiel Exp $
 * @see    ImageConversionRequest
 */
public class ImageConversionRequestProcessor implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ImageConversionRequestProcessor.class);
    private static int idCounter =0;
    private final int processorId;
    private Thread thread;
    private boolean shutdown = false;

    private ImageConverter convert;
    private final BlockingQueue<ImageConversionRequest> queue;
    private final Map<ImageConversionReceiver, ImageConversionRequest> table;


    /**
     * @javadoc
     */
    public ImageConversionRequestProcessor(ImageConverter convert,
                                           BlockingQueue<ImageConversionRequest> queue,
                                           Map<ImageConversionReceiver, ImageConversionRequest> table) {
        this.convert = convert;
        this.queue = queue;
        this.table = table;
        processorId = idCounter++;
        thread = MMBaseContext.startThread(this, "ImageConvert[" + processorId +"]");
    }


    protected void shutdown() {
        shutdown = true;
        thread.interrupt();
    }

    // javadoc inherited (from Runnable)
    public void run() {
        MMBase mmbase = MMBase.getMMBase();
        log.debug("Started request processor");
        while (!mmbase.isShutdown() && !shutdown) {
            try {
                log.debug("Waiting for request");
                ImageConversionRequest req = queue.take();
                log.debug("Starting request " + this);
                processRequest(req);
                log.debug("Done with request");
            } catch (InterruptedException ie) {
                log.debug(Thread.currentThread().getName() +" was interrupted for " + this);
                convert = null;
                break;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.debug("Finished request processor " + this + " " + thread);
    }

    /**
     * Takes an ImageConversionRequest object, and performs the conversion.
     * @param req The ImageConversionRequest wich must be executed.
     */
    private void processRequest(ImageConversionRequest req) {

        InputStream inputPicture = req.getInput();
        ImageConversionReceiver rec = req.getReceiver();

        log.debug("Processing " + req);
        try {
            if (inputPicture == null) {
                if (log.isDebugEnabled()) log.debug("processRequest : input is empty : " + req);
                // no node gets created, so node remains 'null'.
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("processRequest : Converting : " + req );
                }

                List<String> params = req.getParams();
                try {
                    OutputStream out = rec.getOutputStream();
                    int length = convert.convertImage(inputPicture, req.getInputFormat(), out, params);


                    if (length > 0) {
                        rec.setSize(length);
                        if (rec.wantsDimension()) {
                            Dimension dim = Factory.getImageInformer().getDimension(rec.getInputStream());
                            rec.setDimension(dim);
                        }
                        rec.ready();
                    } else {
                        log.warn("processRequest(): Convert problem params : " + params);
                    }
                } catch (java.io.IOException ioe) {
                    log.error(ioe);
                }
            }
        } finally {
            log.debug("Ready processing request");
            synchronized (table){
                if (log.isDebugEnabled()) {
                    log.debug("Setting output " + req + " (" + req.count() + " times requested now)");
                }
                req.ready();
                table.remove(rec);
            }
        }
    }

    public String toString() {
        return super.toString() + " converter: " + convert;
    }
}
