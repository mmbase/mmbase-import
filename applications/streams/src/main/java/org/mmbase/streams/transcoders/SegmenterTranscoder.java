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

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.servlet.FileServlet;

import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.Format;


/**
 * The transcoder that uses <code>segmenter</code> to create segments of a stream including their
 * m3u8 index file to be distributed over a cellular network. 
 * The source of the segmenter can be found: http://svn.assembla.com/svn/legend/segmenter/
 * It accepts the following arguments:
 *   segmenter sample_low.ts 10 sample_low sample_low.m3u8 http://www.openimages.eu/
 * The input file, output prefix and index prefix arguments are automatically filled,  
 * specify segment duration (default 10 sec.) and httpPrefix (hostname) in 'createcaches.xml'.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: SegmenterTranscoder.java 41564 2010-03-22 19:42:15Z andre $
 */
@Settings({"duration", "httpPrefix"})
public class SegmenterTranscoder extends CommandTranscoder {

    private static final Logger log = Logging.getLoggerInstance(SegmenterTranscoder.class);


    @Override
    protected LoggerWriter getErrorWriter(Logger log) {
        // ffmpeg write also non-errors to stderr, so lets not log on ERROR, but on SERVICE.
        // also pluging an error-detector here.
        return new LoggerWriter(new ChainedLogger(log, new ErrorDetector(Pattern.compile("\\s*Unknown encoder.*"))), Level.SERVICE);
    }

    int duration = 10;
    String httpPrefix = "http://localhost:8080/";

    public SegmenterTranscoder() {
        format = Format.M3U8;
    }
    
    public void setDuration(int d) {
        duration = d;
    }

    public void setHttpPrefix(String h) {
        httpPrefix = h;
    }

    @Override
    protected  String getCommand() {
        return "segmenter";
    }

    /**
     * Saves mimetype (video/*) in destination node when not set.
     * @param dest  destination node (streamsourcescaches)
     */
    public void init(Node dest) {
        String mt = dest.getStringValue("mimetype");
        if (mt == null || "".equals(mt)) { 
            dest.setStringValue("mimetype", "video/*");
        }
    }
    
    @Override
    protected String[] getArguments() {
        if (! in.getScheme().equals("file")) throw new UnsupportedOperationException();
        if (! out.getScheme().equals("file")) throw new UnsupportedOperationException();

        File inFile = new File(in.getPath());
        File outFile = new File(out.getPath());
        
        //String filesDirectory = FileServlet.getDirectory().toString();
        String filesPath = FileServlet.getBasePath("files");
        if (filesPath.startsWith("/")) {
            filesPath = filesPath.substring(1);
        }
        
        String outStr = outFile.toString();
        String file_prefix = outStr.substring(0, outStr.lastIndexOf('.'));
        String index_file  = file_prefix + ".m3u8";

        if (log.isDebugEnabled()) {
            log.debug("filesPath: " + filesPath);
            log.debug("file_prefix: " + file_prefix);
            log.debug("index_file:  " + index_file);
        }
        
        List<String> args = new ArrayList<String>();
        
        args.add(inFile.toString());
        args.add("" + duration);
        args.add(file_prefix);
        args.add(index_file);
        args.add(httpPrefix + filesPath);
        
        //args.add(out);

        return args.toArray(new String[args.size()]);
    }

    private static final Pattern PROGRESS = Pattern.compile(".*time remaining.*");

    @Override
    protected LoggerWriter getOutputWriter(final Logger log) {
        LoggerWriter w = new LoggerWriter(log, Level.SERVICE) {
                @Override
                public Level getLevel(String line) {
                    if (PROGRESS.matcher(line).matches()) {
                        return Level.DEBUG;
                    }
                    return null;
                }
            };

        return w;
    }

    @Override
    public SegmenterTranscoder clone() {
        return (SegmenterTranscoder) super.clone();
    }

}
