/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.Format;
import java.net.*;
import java.io.*;

import org.mmbase.util.logging.*;


/**
 * This is a transcoder that does nothing. It will simply stall infinitely, and log some bogus. This is for testing only.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class InfiniteTranscoder extends AbstractTranscoder {
    private static final Logger LOG = Logging.getLoggerInstance(InfiniteTranscoder.class);
    private int seq = 0;

    public InfiniteTranscoder(String id) {
        super(id);
        format = Format.UNKNOWN;
    }

    protected void transcode(final Logger log) throws Exception {
        LOG.info("Logging to " + log);

        while(true) {
            Thread.sleep(5000);
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            LOG.debug("Logging to " + log);

            log.debug("" + (seq++) + " " + in + " -> " + out);
        }
    }




}
