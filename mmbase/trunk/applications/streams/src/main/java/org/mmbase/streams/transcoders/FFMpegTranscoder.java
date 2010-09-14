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

import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.Format;
import java.net.*;
import java.io.*;
import java.util.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import java.util.regex.*;


/**
 * Transcoder that uses <code>ffmpeg</code> to transcode media. Possible parameters to be set in 
 * 'createcaches.xml' are: forceFormat (-f), acodec (-acodec), vcodec (-vcodec), 
 * vpre (-vpre), aq (-aq), ab (-ab), bitrate or b (-b), async (-async), framesPerSecond or 
 * r (-r), audioChannels or ac (-ac), width and height (combined to -s). 
 * Others can be added as extra parameters but will be at the end of the commands parameters. See the
 * documentation for FFmpeg for more information.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
@Settings({"forceFormat", "acodec", "vcodec", "vpre", "aq", "ab", "b", "async", "r", "ac", "width", "height"})
public class FFMpegTranscoder extends CommandTranscoder {

    private static final Logger log = Logging.getLoggerInstance(FFMpegTranscoder.class);

    String forceFormat = null;
    String acodec = null;
    String vcodec = null;
    String vpre = null;
    String ab = null;
    String aq = null;
    String b = null;
    String async = null;
    String r = null;
    String ac = null;

    Integer width = null;
    Integer height = null;

    public void setForceFormat(String f) {
        forceFormat = f;
    }

    /* Audio codec to use -acodec */
    public void setAcodec(String a) {
        acodec = a;
    }
    /* Video codec to use -vcodec */
    public void setVcodec(String v) {
        vcodec = v;
    }

    public Codec getCodec() {
        if (vcodec != null) {
            return AnalyzerUtils.libtoCodec(vcodec);
        } else if (acodec != null) {
            return AnalyzerUtils.libtoCodec(acodec);
        } else {
            return null;
        }
    }

    /* Video codec preset file, f.e. '-vcodec libx264 -vpre hq' http://ffmpeg.org/ffmpeg-doc.html#SEC16 */
    public void setVpre(String vp) {
        vpre = vp;
    }

    public void setAb(String a) {
        ab = a;
    }
    public void setAbitrate(String a) {
        ab = a;
    }

    /* Audio quality variable bit rate (VBR): 0-255 (0 = highest, 255 = lowest) */
    public void setAq(String a) {
        aq = a;
    }

    public void setB(String b) {
        this.b = b;
    }
    public void setBitrate(String b) {
        this.b = b;
    }

    public void setAsync(String a) {
        async = a;
    }

    // fps
    public void setR(String r) {
        this.r = r;
    }
    public void setFramesPerSecond(String r) {
        this.r = r;
    }

    public void setAc(String a) {
        ac = a;
    }
    public void setAudioChannels(String a) {
        ac = a;
    }

    public void setHeight(int y) {
        height = y;
    }

    public void setWidth(int x) {
        width = x;
    }

    @Override
    protected LoggerWriter getErrorWriter(Logger log) {
        // ffmpeg write also non-errors to stderr, so lets not log on ERROR, but on SERVICE.
        // also pluging an error-detector here.
        return new LoggerWriter(new ChainedLogger(log, new ErrorDetector(Pattern.compile("\\s*Unknown encoder.*"))), Level.SERVICE);
    }

    public FFMpegTranscoder() {
        format = Format.AVI;
    }

    /* Saving values like width, height, normally when re-transcoding */
    public void init(Node dest) {
        if (width != null && dest.getNodeManager().hasField("width")) {
            dest.setIntValue("width", width);
            if (height != null) {
                dest.setIntValue("height", height);
            }        
        }
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

        args.add("-i");
        args.add(inFile.toString());

        if (forceFormat != null) {
            args.add("-f");
            args.add(forceFormat);
        }
        // video
        if (vcodec != null) {
            args.add("-vcodec");
            args.add(vcodec);
        }
        if (b != null) {
            args.add("-b");
            args.add(b);
        }
        if (r != null) {
            args.add("-r");
            args.add(r);
        }
        if (vpre != null) {
            args.add("-vpre");
            args.add(vpre);
        }
        if (width != null && height != null) {
            args.add("-s");
            args.add(width + "x" + height);
        }
        // audio
        if (acodec != null) {
            args.add("-acodec");
            args.add(acodec);
        }
        if (aq != null) {
            args.add("-aq");
            args.add(aq);
        }
        if (ab != null) {
            args.add("-ab");
            args.add(ab);
        }
        if (ac != null) {
            args.add("-ac");
            args.add(ac);
        }

        if (async != null) {
            args.add("-async");
            args.add(async);
        }
        args.add("-y"); // overwrite

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

        return w;
    }

    @Override
    public FFMpegTranscoder clone() {
        return (FFMpegTranscoder) super.clone();
    }

}
