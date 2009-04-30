/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import java.net.*;
import java.io.*;
import java.util.regex.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;



/**
 * A trancoder base on an external command, like <code>ffmpeg</code> or <code>ffmpeg2theora</code>
 *
 * @author Michiel Meeuwissen
 */

public class FFMpeg2TheoraTranscoder extends CommandTranscoder {

    @Override
    protected  String getCommand() {
        return "ffmpeg2theora";
    }


    @Override
    protected String[] getArguments(URI in, URI out) {
        if (! in.getScheme().equals("file")) throw new UnsupportedOperationException();
        if (! out.getScheme().equals("file")) throw new UnsupportedOperationException();

        File inFile = new File(in.getPath());
        File outFile = new File(out.getPath());

        return new String[] { "-o", outFile.toString(), inFile.toString() };
    }

    private static final Pattern PROGRESS = Pattern.compile(".*time elapsed.*");
    @Override
    protected LoggerWriter getErrorWriter(Logger log) {
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
    public String getExtension() {
        return "ogv";
    }

    public static void main(String[] argv) throws Exception {
        CommandTranscoder transcoder = new FFMpeg2TheoraTranscoder();
        Logger logger = Logging.getLoggerInstance("FFMPEG2THEORA");
        logger.setLevel(Level.SERVICE);
        transcoder.transcode(new File(argv[0]).toURI(), new File(argv[1]).toURI(), logger);
    }
}
