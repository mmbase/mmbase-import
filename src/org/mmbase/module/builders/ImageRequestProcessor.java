/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.Hashtable;
import java.util.Vector;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;

import org.mmbase.util.Queue;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Rico Jansen
 * @version $Id: ImageRequestProcessor.java,v 1.8 2002-02-12 19:30:42 michiel Exp $
 */
public class ImageRequestProcessor implements Runnable {

    private static Logger log = Logging.getLoggerInstance(ImageRequestProcessor.class.getName());
    private Thread kicker=null;

    private MMObjectBuilder images;
    private ImageConvertInterface convert;
    private Queue queue;
    private Hashtable table;

    public ImageRequestProcessor(MMObjectBuilder images, ImageConvertInterface convert, Queue queue, Hashtable table) {
        this.images = images;
        this.convert = convert;
        this.queue = queue;
        this.table = table;
        start();
    }

    public void start() {
        if (kicker == null) {
            kicker = new Thread(this, "ImageConvert");
            kicker.start();
        }
    }
	
    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);  
        kicker = null;
    }

    public void run() {
        ImageRequest req;
        try {
            while(kicker!=null) {
                log.debug("Waiting for request");
                req=(ImageRequest)queue.get();
                log.debug("Starting request");
                processRequest(req);
                log.debug("Done with request");
            }
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
    }

    private void processRequest(ImageRequest req) {
        Vector params;
        String ckey;
        byte[] picture,inputpicture;
        int id;

        inputpicture = req.getInput();
        params = req.getParams();
        ckey = req.getKey();
        id = req.getId();

        if (inputpicture == null || inputpicture.length == 0) {
            if (log.isDebugEnabled()) log.debug("processRequest : input is empty : " + id);
            picture = new byte[0];
        } else {
            if (log.isDebugEnabled()) log.debug("processRequest : Converting : " + id);
            picture=convert.convertImage(inputpicture, params);
            if (picture != null) {
                MMObjectNode newnode=images.getNewNode("imagesmodule");
                newnode.setValue("ckey", ckey);
                newnode.setValue("id", id);
                newnode.setValue("handle", picture);
                newnode.setValue("filesize", picture.length);
                int i=newnode.insert("imagesmodule");
                if (i<0) {
                    log.warn("processRequest: Can't insert cache entry id=" + id + " key=" + ckey);
                }
            } else {
                log.warn("processRequest(): Convert problem params : " + params);
                picture = new byte[0];
            }
            if (log.isDebugEnabled()) log.debug("processRequest : converting done : " + id);
        }
        if (log.isDebugEnabled()) log.debug("Setting output " + id);
        req.setOutput(picture);
        if (log.isDebugEnabled()) log.debug("Removing key " + id);
        table.remove(ckey);
    }
}
