
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
 * @version $Id$
 */

public class ThumbNailCallable implements  Callable<Node> {

    private static final Logger LOG = Logging.getLoggerInstance(ThumbNailCallable.class);


    private final String field;
    private final Node source;
    private final Node node;
    public ThumbNailCallable(Node node, Field field) {
        Cloud myCloud = node.getCloud().getCloudContext().getCloud("mmbase", "class", null);
        this.source = myCloud.getNode(node.getIntValue("id"));
        this.node   = myCloud.getNode(node.getNumber());
        this.field = field.getName();
    }


    @Override
    public synchronized Node call() {
        int count = 1;

        CommandExecutor.Method method = Executors.getFreeExecutor();
        String command = "ffmpeg";
        List<String> args = new ArrayList<String>();
        args.add("-i");
        args.add(source.getFunctionValue("file", null).toString());
        args.add("-vframes");
        args.add("" + count);
        args.add("-itsoffset");
        args.add(String.format(Locale.US, "%.2f", node.getFloatValue("time") / 1000));
        try {
            File tempFile = File.createTempFile(ThumbNailProcessor.class.getName(), ".%d.png");
            args.add(tempFile.getAbsolutePath());
            OutputStream outStream = new WriterOutputStream(new LoggerWriter(LOG, Level.SERVICE), System.getProperty("file.encoding"));
            OutputStream errStream = new WriterOutputStream(new LoggerWriter(LOG, Level.DEBUG), System.getProperty("file.encoding"));
            CommandExecutor.execute(outStream, errStream, method, command, args.toArray(new String[args.size()]));
            File file = new File(String.format(tempFile.getAbsolutePath(), 1));
            node.setInputStreamValue(field, new FileInputStream(file), file.length());
            node.commit();
            return node;
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        } catch (ProcessException pe) {
            LOG.error(pe.getMessage(), pe);
        } catch (InterruptedException  ie) {
            LOG.service(ie.getMessage(), ie);
        }
        this.notifyAll();
        return null;
    }
}