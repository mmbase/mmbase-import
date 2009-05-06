/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.List;
import java.io.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.MMObjectNode;

/**
 * Defines one Image convert request.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 * @version $Id: ImageConversionRequest.java,v 1.2 2009-04-22 06:56:51 michiel Exp $
 */
public class ImageConversionRequest {

    private static final Logger log = Logging.getLoggerInstance(ImageConversionRequest.class);

    private boolean ready = false;
    protected final List<String> params;
    private  byte[] bytes;
    private  final InputStream in;
    private int count = 0;
    protected String format;
    private final ImageConversionReceiver receiver;


    public ImageConversionRequest(List<String> params, byte[] in, String format, MMObjectNode icacheNode) {
        this(in, format, new NodeReceiver(icacheNode), params);
    }

    /**
     * @since MMBase-1.9
     */
    public ImageConversionRequest(byte[] in, String format, ImageConversionReceiver receiver, List<String> params) {
        this.receiver = receiver;
        this.bytes = in;
        this.in = new ByteArrayInputStream(in);
        this.params = params;
        this.format = format;
    }
    public ImageConversionRequest(InputStream in, String format, ImageConversionReceiver receiver, List<String> params) {
        this.receiver = receiver;
        this.in = in;
        bytes = null;
        this.params = params;
        this.format = format;
    }

    /**
     * The parameters describing the conversion.
     */
    List<String> getParams() {
        return params;
    }

    /**
     * The byte[] on which the conversion must happen.
     */
    InputStream getInput() {
        return in;
    }
    byte[] getByteArray() throws IOException {
        if (bytes == null) {
            if (in instanceof BytesInputStream) {
                bytes = ((BytesInputStream) in).getBuffer();
            } else {
                // lets hope the inputstream is seekable then
                in.reset();
                bytes = new byte[in.available()];
                in.read(bytes, 0, in.available());
            }
        }
        return bytes;
    }


    String getInputFormat() {
        return format;
    }

    /**
     * @since MMBase-1.9
     */
    public ImageConversionReceiver getReceiver() {
        return receiver;
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
                log.info("Interrupted wait");
                return;
            }
            log.service("Ready " + toString());
        }
    }


    synchronized void ready() {
        count = 0;
        ready = true;
        notifyAll();
    }

    /**
     * Returns how many request are waiting for the result of this image transformation.
     */
    int count() {
        return count;
    }

}
