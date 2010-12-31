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


import org.mmbase.applications.media.Format;
import org.mmbase.applications.media.Codec;
import java.io.*;
import java.util.regex.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;



/**
 * This transcoder uses the command <code>ffmpeg2theora</code>. Possible parameters to be set in
 * 'createcaches.xml' are: videoQuality (--videoquality), keyInt (--keyint), height (-y) and width (-x).
 * Others can be added but will be at the end of the commands parameters.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
@Settings({"videoQuality", "keyInt", "height", "width"})
public class FFMpeg2TheoraTranscoder extends CommandTranscoder {

    private static final Logger log = Logging.getLoggerInstance(FFMpeg2TheoraTranscoder.class);

    public FFMpeg2TheoraTranscoder() {
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

    /**
     * Saves values video codec in codec (Theora)) and audio codec in acodec (Vorbis) in destination node.
     * @param dest  destination node (streamsourcescaches)
     */
    @Override
    public void init(Node dest) {
        dest.setIntValue("codec", Codec.THEORA.toInt() );
        if (dest.getNodeManager().hasField("acodec")) {
            dest.setIntValue("acodec", Codec.VORBIS.toInt() );
        }
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
    protected LoggerWriter getErrorWriter(Logger l) {
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


    /*
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
    */
}
