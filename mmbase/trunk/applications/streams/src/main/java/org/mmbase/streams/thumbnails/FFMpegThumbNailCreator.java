
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

import org.mmbase.bridge.*;
import org.mmbase.streams.createcaches.Executors;
import org.mmbase.util.WriterOutputStream;
import org.mmbase.util.externalprocess.CommandExecutor;
import org.mmbase.util.externalprocess.ProcessException;
import org.mmbase.util.logging.*;

/**
 * Contains the functionality to fill the 'handle' field of a thumbnails object. For that it uses ffmpeg.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class FFMpegThumbNailCreator implements  Callable<Long> {

    private static final Logger LOG = Logging.getLoggerInstance(FFMpegThumbNailCreator.class);


    private final String field;
    private final Node source;
    private final Node node;

    private boolean useSharedDir = true;


    FFMpegThumbNailCreator(Node node, Field field) {
        Cloud myCloud = node.getCloud().getCloudContext().getCloud("mmbase", "class", null);
        this.source = myCloud.getNode(node.getIntValue("id"));
        this.node   = myCloud.getNode(node.getNumber());
        this.field = field.getName();
    }

    protected File getTempDir() throws IOException {
        File result;
        if (! useSharedDir) {
            result = File.createTempFile(FFMpegThumbNailCreator.class.getName(), ".dir");
        } else {
            File thumbNailDir = new File(org.mmbase.module.core.MMBase.getMMBase().getDataDir(), "workdir_thumbnails");
            thumbNailDir.mkdirs();
            result = File.createTempFile("dir", "", thumbNailDir);
        }
        result.delete();
        result.mkdir();
        result.deleteOnExit();
        return result;

    }


    @Override
    public Long call() {
        int count = 1;

        File input = (File) source.getFunctionValue("file", null).get();
        if (input == null || ! input.canRead()) {
            LOG.debug("Cannot read " + input);
            return null;
        }
        if (input.length() == 0) {
            LOG.debug("File is empty " + input);
            return null;
        }
        CommandExecutor.Method method = Executors.getFreeExecutor();
        String command = "ffmpeg";
        List<String> args = new ArrayList<String>();
        args.add("-i");
        args.add(input.getAbsolutePath());
        args.add("-an"); // audio doesn't make sense
        args.add("-ss");
        args.add(String.format(Locale.US, "%.2f", node.getDoubleValue("time") / 1000));
        //args.add("-t");
        //args.add("00:00:01");
        args.add("-vframes");
        args.add("" + count);
        try {
            File tempDir = getTempDir();
            File tempFile = new File(tempDir, "thumbnail.%d.png");
            tempFile.deleteOnExit(); // the file is explicitely deleted too, but only if it looks okay, otherwise you
                                     // have time to explore the situation as long as the jvm is running
            args.add(tempFile.getAbsolutePath());
            OutputStream outStream = new WriterOutputStream(new LoggerWriter(LOG, Level.SERVICE), System.getProperty("file.encoding"));
            OutputStream errStream = new WriterOutputStream(new LoggerWriter(LOG, Level.DEBUG), System.getProperty("file.encoding"));
            CommandExecutor.execute(outStream, errStream, method, command, args.toArray(new String[args.size()]));
            File file = new File(String.format(tempFile.getAbsolutePath(), 1));
            long result;
            if (file.exists() && file.length() > 0) {
                node.setInputStreamValue(field, new FileInputStream(file), file.length());
                result = file.length();
                file.delete();
                node.commit();
                tempDir.delete();
            } else {
                LOG.warn("No file " + file + " produced (file exists: " + file.exists() + " file length: " + file.length() + " )");
                result = 0;
            }
            return result;
        } catch (IOException ioe) {
            LOG.error(ioe.getMessage(), ioe);
        } catch (ProcessException pe) {
            LOG.error(pe.getMessage(), pe);
        } catch (InterruptedException  ie) {
            LOG.service(ie.getMessage(), ie);
        }
        return -1l;
    }
}
