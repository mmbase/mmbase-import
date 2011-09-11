/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2010 André van Toly, Michiel Meeuwissen

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

import java.util.*;
import java.io.*;

import org.mmbase.servlet.FileServlet;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.*;


/**
 * Analyzes <code>segmenter</code> output after doing its job, changes url field to m3u8 index file 
 * when ready and rewrites m3u8 to removed full paths. It can wait two minutes for the filesystem to
 * be ready before starting to rewrite.
 * 
 * @author Andr&eacute; van Toly
 * @version $Id$
 */
public class SegmenterAnalyzer implements Analyzer {

    private static final Logger LOG = Logging.getLoggerInstance(SegmenterAnalyzer.class);

    public int getMaxLines() {
        return Integer.MAX_VALUE;
    }
    
    // TODO: progress matcher
    // private static final Pattern PROGRESS = Pattern.compile("\\s*(.*?) audio: ([0-9]+)kbps video: ([0-9]+)kbps, time remaining: .*");

    private final ChainedLogger log = new ChainedLogger(LOG);

    private final AnalyzerUtils util = new AnalyzerUtils(log);

    private final List<Throwable> errors =new ArrayList<Throwable>();

    public void addThrowable(Throwable t) {
        errors.add(t);
    }

    public void addLogger(Logger logger) {
        log.addLogger(logger);
    }

    public void analyze(String l, Node source, Node des) { }

    public void ready(Node sourceNode, Node destNode) {
        synchronized(util) {
            String url = destNode.getStringValue("url");

            if (FileServlet.getInstance() != null) {
                
                String filesDirectory = FileServlet.getDirectory().toString();
                if (!filesDirectory.endsWith("/")) {
                    filesDirectory = filesDirectory + "/";
                }
                File index = new File(filesDirectory + url);
                File temp  = new File(filesDirectory + url + ".tmp");

                int count = 0;
                while ((!index.exists() || index.length() < 1) && count < 12) {
                    LOG.service("Result ready, but file " + index + (index.exists() ? " is too small" : " doesn't exist") + ". Waiting 10 sec. to be sure filesystem is ready (" + count + ")");
                    try {
                        Thread.sleep(10000);
                        count++;
                    } catch (InterruptedException ie) {
                        LOG.info("Interrupted");
                        return;
                    }
                }
                
                try {
                    BufferedReader in = new BufferedReader(new FileReader(index));
                    PrintWriter pw = new PrintWriter(new FileWriter(temp));
                    
                    String line = null;
                    while((line = in.readLine()) != null) {
                        if (line.indexOf(filesDirectory) > -1) {
                            line = line.replace(filesDirectory, "");
                        }
                        pw.println(line);
                    }
                    in.close();
                    pw.close();

                    // Delete original and rename new one
                    if (!index.delete()) log.error("Could not delete file: " + index.toString());
                    if (!temp.renameTo(index)) log.error("Could not rename file to: " + index.toString());
                    
                    LOG.service("Rewrote m3u8 indexfile: " + index);
                } catch (java.io.IOException ioe) {
                    LOG.error("Could not rewrite m3u8 indexfile: " + ioe);
                }
                
            }
        }

    }

    @Override
    public SegmenterAnalyzer clone() {
        try {
            return (SegmenterAnalyzer) super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // doesn't happen
            return null;
        }
    }

}
