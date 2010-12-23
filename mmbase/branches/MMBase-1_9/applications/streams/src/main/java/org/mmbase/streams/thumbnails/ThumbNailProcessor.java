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

import java.util.*;
import java.util.concurrent.*;
import java.io.*;

import org.mmbase.streams.createcaches.Executors;
import org.mmbase.streams.createcaches.Stage;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.externalprocess.*;
import org.mmbase.util.WriterOutputStream;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: CreateCachesFunction.java 43817 2010-11-19 11:45:01Z andre $
 */

public class ThumbNailProcessor implements Processor {

    private static final Logger LOG = Logging.getLoggerInstance(ThumbNailProcessor.class);



    @Override
    public Object process(final Node node, final Field field, final Object value) {
        LOG.info("Hello " + node);
        if (node.isNull(field.getName())) {
            LOG.info("Thumbnail not yet generated. doing that now");
            final Callable<Node> callable = new ThumbNailCallable(node, field);
            Future<Node> future = Executors.submit(Stage.RECOGNIZER, callable);
            try {
                Node result = future.get();
                LOG.info("Thumbnail generation ready: " + result);
                if (result != null) {
                    return result.getValue(field.getName());
                }
            } catch (ExecutionException ee) {
                LOG.error(ee.getMessage());
            } catch (InterruptedException ie) {
                LOG.info(ie.getMessage());
            }

        }
        return value;
    }


}
