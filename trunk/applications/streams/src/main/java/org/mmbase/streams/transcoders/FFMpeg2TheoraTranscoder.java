/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;


import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;



/**
 * A trancoder base on an external command, like <code>ffmpeg</code> or <code>ffmpeg2theora</code>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
@Settings({"videoQuality",  "keyInt", "height", "width"})
public class FFMpeg2TheoraTranscoder extends CommandTranscoder {


    private static final Logger log = Logging.getLoggerInstance(FFMpeg2TheoraTranscoder.class);


    public FFMpeg2TheoraTranscoder(String id) {
        super(id);
        format = Format.OGV;
        codec  = Codec.THEORA;
    }


    int videoQuality = 5;
    int keyInt = 64;
    Integer height = null;
    Integer width  = null;

    public void setVideoQuality(int vq) {
        videoQuality = vq;
    }

    public void setKeyInt(int ki) {
        keyInt = ki;
    }

    public void setHeight(int y) {
        height = y;
    }

    public void setWidth(int x) {
        width = x;
    }

    @Override
    protected  String getCommand() {
        return "ffmpeg2theora";
    }

    @Override
    protected String[] getArguments() {
        if (! in.getScheme().equals("file")) {
            throw new UnsupportedOperationException();
        }
        if (! out.getScheme().equals("file")) {
            throw new UnsupportedOperationException();
        }

        File inFile = new File(in.getPath());
        File outFile = new File(out.getPath());

        List<String> args = new ArrayList<String>();
        args.add("-o"); args.add(outFile.toString());
        args.add("--videoquality"); args.add("" + videoQuality);
        args.add("--keyint"); args.add("" + keyInt);

        if (width != null) {
            args.add("-x"); args.add("" + width);
        }
        if (height != null) {
            args.add("-y"); args.add("" + height);
        }


        args.add(inFile.toString());

        return args.toArray(new String[args.size()]);
    }

    long count = 0;

    private static final Pattern PROGRESS = Pattern.compile(".*time (elapsed|remaining).*");

    @Override
    protected LoggerWriter getOutputWriter(Logger l) {
        LoggerWriter w = new LoggerWriter(l, Level.SERVICE) {
                @Override
                public Level getLevel(String line) {
                    count ++;
                    if (count % 100 != 0 && PROGRESS.matcher(line).matches()) {
                        return Level.DEBUG;
                    }
                    return null;
                }
            };

        return w;
    }


    public static void main(String[] argv) throws Exception {
        FFMpeg2TheoraTranscoder ff = new FFMpeg2TheoraTranscoder("1");
        ff.setHeight(100);
        //ff.setWidth(100);
        System.out.println("KEY" + ff + " -> " + AbstractTranscoder.getInstance(ff.getKey()));
        System.exit(0);
        CommandTranscoder transcoder = ff.clone();
        Logger logger = Logging.getLoggerInstance("FFMPEG2THEORA");
        ChainedLogger chain = new ChainedLogger(logger);
        Node source = AnalyzerUtils.getTestNode();
        Node dest   = AnalyzerUtils.getTestNode();
        Analyzer a = new FFMpeg2TheoraAnalyzer();
        chain.addLogger(new AnalyzerLogger(a, source, dest));
        chain.setLevel(Level.SERVICE);
        transcoder.transcode(new File(argv[0]).toURI(), new File(argv[1]).toURI(), chain);
        a.ready(source, dest);

        System.out.println("" + source + " -> " + dest);
    }
}
