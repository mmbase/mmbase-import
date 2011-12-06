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

package org.mmbase.streams.thumbnails;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.mmbase.streams.createcaches.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.*;

/**
 * Function that submits {@link FFMpegThumbNailCreator} to create an image.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class WaitFunction extends NodeFunction<Node> {

    private static final Logger LOG = Logging.getLoggerInstance(WaitFunction.class);

    private final static int TIMEOUT = 2;
    public final static Parameter[] PARAMETERS = {  };
    public WaitFunction() {
        super("wait", PARAMETERS);
    }


    static long wait(final Node thumb) {
        return wait(thumb, TIMEOUT);
    }

    static long wait(final Node thumb, Integer timeout) {
        if (timeout < 0) timeout = TIMEOUT;
        if (thumb.isNull("handle")) {
            LOG.debug("Triggering a conversion");
            FFMpegThumbNailCreator callable = new FFMpegThumbNailCreator(thumb, thumb.getNodeManager().getField("handle"));
            Future<Long> future = Executors.submit(Stage.RECOGNIZER, callable);
            try {
                LOG.service("And waiting " + timeout + " seconds for it");
                long result = (Long) future.get(timeout, TimeUnit.SECONDS); // wait for result
                LOG.service("Found " + result + " bytes");
                return result;
            } catch (TimeoutException te) {
                LOG.warn(te.getMessage(), te);
                return 0;
            } catch (InterruptedException ie) {
                LOG.warn(ie.getMessage(), ie);
                return 0;
            } catch (ExecutionException ee) {
                LOG.error(ee.getMessage(), ee);
                return 0;
            }

        } else {
            return thumb.getSize("handle");
        }
    }

    @Override
    protected Node getFunctionValue(final Node node, final Parameters parameters) {
        long result = wait(node);
        if (LOG.isDebugEnabled()) {
            LOG.debug("result: " + result);
        }
        if (result < 1) {
            return ThumbNailFunction.getDefault(node.getCloud());   // TODO: does this work? do we ever get a default?
        }
        return node;
    }


}
