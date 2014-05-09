/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.List;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.MMObjectNode;

/**
 * Defines one Image convert request.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class ImageConversionRequest {

    private static final Logger log = Logging.getLoggerInstance(ImageConversionRequest.class);

    private boolean ready = false;
    private List params;
    private byte[] in;
    private int count = 0;
    private MMObjectNode icacheNode;
    private String format;

    /**
     * @javadoc
     */
    public ImageConversionRequest(List params, byte[] in, String format, MMObjectNode icacheNode) {
        this.in = in;
        this.params = params;
        this.icacheNode = icacheNode;
        this.format = format;
    }

    /**
     * The parameters describing the conversion.
     */
    public List getParams() {
        return params;
    }

    /**
     * The byte[] on which the conversion must happen.
     */
    public byte[] getInput() {
        return in;
    }

    /**
     * The icache node in which the conversion results must be stored.
     */
    public MMObjectNode getNode() {
        return icacheNode;
    }
    public String getInputFormat() {
        return format;
    }


    /**
     * Waits until the conversion result is ready.
     */
    public synchronized void waitForConversion() {
        if (! ready) { // the request is in progress, wait until it is ready.
            log.service("Waiting for " + toString());
            count++;
            try {
                wait();
            } catch (InterruptedException e) {
            }
            log.service("Ready " + toString());
        }
    }


    public synchronized void ready() {
        count = 0;
        ready = true;
        notifyAll();
    }

    /**
     * Returns how many request are waiting for the result of this image transformation.
     */
    public int count() {
        return count;
    }

    // javadoc inherited (of Object)
    public String toString() {
        return icacheNode.getStringValue("id") + " --> " + icacheNode.getNumber() + " " + params;
    }
}
