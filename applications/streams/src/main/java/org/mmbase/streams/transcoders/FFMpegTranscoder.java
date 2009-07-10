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
import java.util.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import java.util.regex.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
@Settings({"format", "acodec", "vcodec", "ab"})
public class FFMpegTranscoder extends CommandTranscoder {

    private static final Logger log = Logging.getLoggerInstance(FFMpegTranscoder.class);

    String acodec = null;
    String vcodec = null;
    String ab = null;

    public void setAcodec(String a) {
        acodec = a;
    }
    public void setVcodec(String v) {
        vcodec = v;
    }

    public void setAb(String a) {
        ab = a;
    }

    public FFMpegTranscoder(String id) {
        super(id);
        format = Format.AVI;
    }


    @Override
    protected  String getCommand() {
        return "ffmpeg";
    }

    @Override
    protected String[] getArguments() {
        if (! in.getScheme().equals("file")) throw new UnsupportedOperationException();
        if (! out.getScheme().equals("file")) throw new UnsupportedOperationException();

        File inFile = new File(in.getPath());
        File outFile = new File(out.getPath());

        List<String> args = new ArrayList<String>();
        args.add("-y");
        if (acodec != null) {
            args.add("-acodec");
            args.add(acodec);
        }
        if (vcodec != null) {
            args.add("-vcodec");
            args.add(vcodec);
        }
        if (ab != null) {
            args.add("-ab");
            args.add(ab);
        }
        args.add("-i");
        args.add(inFile.toString());

        args.add(outFile.toString());

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

        log.debug("Returning " + w);
        return w;
    }


    public static void main(String[] argv) throws Exception {
        CommandTranscoder transcoder = new FFMpegTranscoder("1").clone();
        Logger logger = Logging.getLoggerInstance("FFMPEG");

        ChainedLogger chain = new ChainedLogger(logger);
        Analyzer a = new FFMpegAnalyzer();

        Node source = AnalyzerUtils.getTestNode();
        Node dest   = AnalyzerUtils.getTestNode();

        chain.addLogger(new AnalyzerLogger(a, source, dest));

        chain.setLevel(Level.DEBUG);


        transcoder.transcode(new File(argv[0]).toURI(), new File(argv[1]).toURI(), chain);
        a.ready(source, dest);

        System.out.println("" + source + " -> " + dest);

    }
}
