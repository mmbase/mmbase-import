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
@Settings({"format", "acodec", "vcodec", "vpre", "aq", "ab", "b", "async", "r", "ac"})
public class FFMpegTranscoder extends CommandTranscoder {

    private static final Logger log = Logging.getLoggerInstance(FFMpegTranscoder.class);

    String acodec = null;
    String vcodec = null;
    String vpre = null;
    String ab = null;
    String aq = null;
    String b = null;
    String async = null;
    String r = null;
    String ac = null;

    /* Audio codec to use -acodec */
    public void setAcodec(String a) {
        acodec = a;
    }
    /* Video codec to use -vcodec */
    public void setVcodec(String v) {
        vcodec = v;
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

        args.add("-i");
        args.add(inFile.toString());

        if (acodec != null) {
            args.add("-acodec");
            args.add(acodec);
        }
        if (vcodec != null) {
            args.add("-vcodec");
            args.add(vcodec);
        }
        if (vpre != null) {
            args.add("-vpre");
            args.add(vpre);
        }
        if (aq != null) {
            args.add("-aq");
            args.add(aq);
        }
        if (ab != null) {
            args.add("-ab");
            args.add(ab);
        }
        if (b != null) {
            args.add("-b");
            args.add(b);
        }
        if (r != null) {
            args.add("-r");
            args.add(r);
        }
        if (ac != null) {
            args.add("-ac");
            args.add(ac);
        }

        args.add(outFile.toString());
        args.add("-y"); // overwrite

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

}
