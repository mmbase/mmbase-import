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

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.servlet.FileServlet;

import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.transformers.StringTransformer;
import org.mmbase.util.transformers.Asciifier;
import org.mmbase.util.transformers.Identifier;

import org.mmbase.applications.media.Format;


/**
 * The transcoder that uses <code>segmenter</code> to create segments of a stream including their
 * m3u8 index file to be distributed over a cellular network. 
 * The source of the segmenter can be found: http://svn.assembla.com/svn/legend/segmenter/
 * It accepts the following arguments:
 *   segmenter sample_low.ts 10 sample_low sample_low.m3u8 http://www.openimages.eu/
 * The input file, output prefix and index prefix arguments are automatically filled,  
 * specify segment duration (default 10 sec.) and httpPrefix (hostname) in 'createcaches.xml'.
 * TODO: replace "/" with File.separator
 *
 * @author Andr&eacute; van Toly
 * @version $Id$
 */
@Settings({"duration", "httpPrefix"})
public class SegmenterTranscoder extends CommandTranscoder {

    private static final Logger log = Logging.getLoggerInstance(SegmenterTranscoder.class);
    private static CharTransformer identifier = new Identifier();
    private static StringTransformer asciifier = new Asciifier();

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
     * Saves mimetype (application/x-mpegurl) in destination node when not set, deletes former 
     * segments when re-transcoding and removes punctuation, whitespace etc from filename.
     * @param dest  destination node (streamsourcescaches)
     */
    @Override
    public void init(Node dest) {
        String mt = dest.getStringValue("mimetype");
        if (mt == null || "".equals(mt)) { 
            dest.setStringValue("mimetype", "application/x-mpegurl");
        }
        
        String fileName = dest.getStringValue("url");
        if (fileName.length() < 1) {
            log.warn("Still empty fileName: '" + fileName + "' of #" + dest.getNumber());
        } else {
            
            // remove punctuation from fileName
            String regex = "^((.*\\/)?([0-9]+\\.?[0-9]*)?\\.)(.*)\\.m3u8";
            Pattern FILE_PATTERN = Pattern.compile(regex);
            Matcher m = FILE_PATTERN.matcher(fileName);
            
            StringBuilder m3u8 = new StringBuilder(fileName);

            if (m.matches()) {
                log.debug("match 1: " + m.group(1));
                log.debug("match 2: " + m.group(2));
                log.debug("match 3: " + m.group(3));
                log.debug("match 4: " + m.group(4));

                String begin = m.group(1);
                String base = m.group(4);
                String ext = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
                
                log.debug("begin: " + begin);
                log.debug("base : " + base);
                log.debug("ext  : " + ext);
                
                m3u8 = new StringBuilder(begin);
                base = asciifier.transform(base);
                m3u8.append(identifier.transform(base)).append(ext); 
            }

            /*
            if (fileName.indexOf('.') > -1) {
                String base = fileName.substring(0, fileName.lastIndexOf('.'));
                String ext = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
                String begin = "";
                
                if (base.indexOf('.') > -1) {
                    begin = base.substring(0, base.lastIndexOf('.') + 1);
                    base = base.substring(base.lastIndexOf('.') + 1, base.length());
                    m3u8.append(begin);
                } else if (base.indexOf('/') > -1) {
                    begin = base.substring(0, base.lastIndexOf('/') + 1);
                    base = base.substring(base.lastIndexOf('/') + 1, base.length());
                    m3u8.append(begin);
                }
                base = asciifier.transform(base);
                m3u8.append(identifier.transform(base)).append(ext); 
            }
            */
            
            fileName = m3u8.toString();
            dest.setStringValue("url", fileName);
            
            // get arguments to delete ts segements
            String fileprefix = fileName.substring(0, fileName.lastIndexOf("."));
            if (fileprefix.indexOf("/") > -1) {
                fileprefix = fileprefix.substring(fileprefix.lastIndexOf("/") + 1, fileprefix.length());
            }
            
            File file = new File(FileServlet.getDirectory(), fileName);
            String dir = file.toString();
            dir = dir.substring(0, dir.lastIndexOf("/"));

            if (log.isDebugEnabled()) {
                log.debug("  fileName: " + fileName);
                log.debug("fileprefix: " + fileprefix);
                log.debug("       dir: " + dir);
            }
            
            FilenameFilter filter = new FilterPrefix(fileprefix);
            String[] dirlist = new File(dir).list(filter);
            if (dirlist != null) {
                for (int i = 0; i < dirlist.length; i++) {
                    File f = new File(dir, dirlist[i]);
                    if (f.delete()) {
                        log.service("Deleted old version of '" + dirlist[i] + "'");
                    } else {
                        log.error("Could not delete old version of file '" + dirlist[i] + "'");
                    }
                }
            }
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


class FilterPrefix implements FilenameFilter {
    
    protected static String str;
    public FilterPrefix(String s) {
        str = s;
    }
    
    public boolean accept(File dir, String name) {
        if (name.startsWith(str)) {
            return true;
        }
        return false;
    }
}
