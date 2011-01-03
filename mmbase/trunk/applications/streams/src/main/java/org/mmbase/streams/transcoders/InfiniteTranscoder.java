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

package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.Format;

import org.mmbase.util.logging.*;


/**
 * This is a transcoder that does nothing. It will simply stall infinitely, and log some bogus. This is for testing only.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
@Settings({})
public class InfiniteTranscoder extends AbstractTranscoder {
    private static final Logger LOG = Logging.getLoggerInstance(InfiniteTranscoder.class);
    private int seq = 0;

    public InfiniteTranscoder() {
        format = Format.UNKNOWN;
    }

    @SuppressWarnings("SleepWhileHoldingLock")
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
