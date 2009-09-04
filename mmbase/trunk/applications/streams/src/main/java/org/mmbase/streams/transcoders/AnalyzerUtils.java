/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.MimeType;
import org.mmbase.applications.media.State;
import java.util.*;
import java.util.regex.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.*;

/**
 * Utility methods common for the analyzers that look for several media audio, video and images,
 * that try to extract information, convert nodetypes to the matching kind etc.
 *
 * @author Michiel Meeuwissen
 * @author Andre van Toly
 * @version $Id$
 */

public final class AnalyzerUtils implements java.io.Serializable {

    private static final Logger LOG = Logging.getLoggerInstance(AnalyzerUtils.class);

    public static final String VIDEO = "videostreamsources";
    public static final String AUDIO = "audiostreamsources";
    public static final String IMAGE = "imagesources";
    public static final String MEDIA = "streamsources";

    public static final String VIDEOC = VIDEO + "caches";
    public static final String AUDIOC = AUDIO + "caches";
    public static final String IMAGEC = IMAGE + "caches";   /* only for testing, does not exist */
    public static final String MEDIAC = MEDIA + "caches";

    private final ChainedLogger log = new ChainedLogger(LOG);
    AnalyzerUtils(Logger... loggers) {
        for (Logger l : loggers) {
            log.addLogger(l);
        }
    }

    /**
     * @todo Should this perhaps be arranged in the respective builders themselves. It seems a
     * requiredment or videosources to have a video/* mimetype
     *
     */
    protected void fixMimeType(String type, Node node) {
        if (node == null) return;
        MimeType actualMimeType = new MimeType(node.getStringValue("mimetype"));
        if (! actualMimeType.getType().equals(type)) {
            MimeType newType = new MimeType(type, actualMimeType.getSubType());
            node.setStringValue("mimetype", newType.toString());
            log.service("Fixing mime type " + actualMimeType + "-> " + newType) ;

        } else {
            if (log.isDebugEnabled()) log.debug("MimeType " + actualMimeType + " is correct");
        }
    }

    public void toVideo(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("video", source);
        fixMimeType("video", dest);
        if (cloud != null) {
            if (! source.getNodeManager().getName().equals(VIDEO)) {
                log.service("This is video, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
                source.setNodeManager(cloud.getNodeManager(VIDEO));
                source.commit();
            }
            assert source.getNodeManager().getName().equals(VIDEO);
            if (dest != null) {
                if (! dest.getNodeManager().getName().equals(VIDEOC)) {
                    dest.setNodeManager(cloud.getNodeManager(VIDEOC));
                    dest.commit();
                }
                assert dest.getNodeManager().getName().equals(VIDEOC);
            }
        }
    }

    public void toAudio(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("audio", source);
        fixMimeType("audio", dest);
        if (cloud != null) {
            if (! source.getNodeManager().getName().equals(AUDIO)) {
                log.service("This is audio, now converting type. source: " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
                source.setNodeManager(cloud.getNodeManager(AUDIO));
                source.commit();
            }
            assert source.getNodeManager().getName().equals(AUDIO);
            if (dest != null) {
                if (! dest.getNodeManager().getName().equals(AUDIOC)) {
                    dest.setNodeManager(cloud.getNodeManager(AUDIOC));
                    dest.commit();
                }
                assert dest.getNodeManager().getName().equals(AUDIOC);
            }
        }
    }

    public void toImage(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("image", source);
        if (cloud != null) {
            if (log.isDebugEnabled()) {
                log.service("This is image, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""), new Exception());
            }
            if (cloud.hasNodeManager(IMAGE)) { // happens for example during junit tests
                source.setNodeManager(cloud.getNodeManager(IMAGE));
                source.commit();
            }
        }
    }


    public long getLength(String l) {
        String[] duration = l.split(":");
        int i = duration.length - 1;
        long len = (long) (Float.parseFloat(duration[i]) * 1000L); // secs
        i--;
        if (i >= 0) {
            len += Integer.parseInt(duration[i]) * 60 * 1000; // minutes
            i--;
            if (i >= 0) {
                len += Integer.parseInt(duration[i]) * 60 * 60 * 1000; // hours
                i--;
                if (i >= 0) {
                    len += Integer.parseInt(duration[i]) * 24 * 60 * 60 * 1000; // days
                    i--;
                    if (i >= 0) {
                        log.warn("Hmmm, could not parse " + l);
                    }
                }

            }
        }
        return len;
    }

    public long getStart(String s) {
        long l = 0;
        try {
            double dValue = Double.parseDouble(s);
            Double d = new Double(dValue * 1000);
            l = d.longValue();
        } catch (NumberFormatException e) {
            log.warn("Start '" + s + "' is not a valid number: " + e);
        }
        return l;
    }

    private static final Pattern PATTERN_UNKNOWN     = Pattern.compile("(.*?): Unknown format.*");
    private static final Pattern PATTERN_UNSUPPORTED = Pattern.compile("\\s*(.*)Unsupported video codec.*?");

    /* Looks for messages from ffmpeg that it does not support this kind of file.
     * browserevent.ram: Unknown format
     * [NULL @ 0x1804800]Unsupported video codec
    */
    public boolean unsupported(String l, Node source, Node dest) {
        Matcher m = PATTERN_UNKNOWN.matcher(l.trim());
        if (m.matches()) {
            log.warn("UNKNOWN format: " + m.group(1) + " : " + source.getNumber() + " " + source.getStringValue("url") + " matched on " + l);
            source.setIntValue("state", State.SOURCE_UNSUPPORTED.getValue());
            source.commit();
            return true;
        }
        m = PATTERN_UNSUPPORTED.matcher(l);
        if (m.matches()) {
            log.warn("UNSUPPORTED " + m.group(1) + " : " + source.getNumber());

            source.setIntValue("state", State.SOURCE_UNSUPPORTED.getValue());
            source.commit();
            return true;
        }
        return false;
    }

    /* ffmpeg reports sometimes no start and on some video's bitrate: N/A */
    private static final Pattern PATTERN_DURATION = Pattern.compile("\\s*Duration: (.*?),.* bitrate:.*?");
    private static final Pattern PATTERN_BITRATE  = Pattern.compile("\\s*Duration: .* bitrate: (.*?) kb/s.*?");
    private static final Pattern PATTERN_START    = Pattern.compile("\\s*Duration: .* start: (.*?), bitrate:.*?");

    /**
     * Matches duration, records that and tries to match bitrate and start on that same line.
     *
     */
    public boolean duration(String l, Node source, Node dest) {
        Matcher m = PATTERN_DURATION.matcher(l);
        if (m.matches()) {
            //log.debug("### Duration match: " + l);

            Node fragment = source.getNodeValue("mediafragment");
            // log.debug("mediafragment: " + source.getNodeValue("mediafragment"));

            if (! source.getNodeManager().hasField("length")) {
                toVideo(source, dest);
            }
            if (log.isDebugEnabled()) log.debug("duration: " + m.group(1));
            long length = getLength(m.group(1));
            source.setLongValue("length", length);
            if (dest != null) {
                dest.setLongValue("length", length);
            }

            m = PATTERN_BITRATE.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("bitrate: " + m.group(1));
                int bitrate = 1000 * Integer.parseInt(m.group(1));
                source.setIntValue("bitrate", bitrate);
                if (dest != null) dest.setIntValue("bitrate", bitrate);
            }

            m = PATTERN_START.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("start: " + m.group(1));
                long start = getStart(m.group(1));
                if (fragment != null) {
                    fragment.setLongValue("start", start);
                    fragment.commit();
                } else {
                    log.warn("mediafragment still null");
                }
            }
            return true;

        } else {
            return false;
        }
    }

    private static final Pattern VIDEO_PATTERN        = Pattern.compile(".*?\\sVideo: .*?, .*?, ([0-9]+)x([0-9]+).*");
    private static final Pattern VIDEOBITRATE_PATTERN = Pattern.compile(".*?\\sVideo: .* bitrate: (.*?) kb/s.*");

    public boolean video(String l, Node source, Node dest) {
        Matcher m = VIDEO_PATTERN.matcher(l);
        if (m.matches()) {
            //log.info("### VIDEO match: " + l);
            if (! source.getNodeManager().getName().equals(IMAGE)) {
                toVideo(source, dest);
            }

            source.setIntValue("width", Integer.parseInt(m.group(1)));
            source.setIntValue("height", Integer.parseInt(m.group(2)));

            m = VIDEOBITRATE_PATTERN.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("bitrate: " + m.group(1));
                int bitrate = 1000 * Integer.parseInt(m.group(1));
                source.setIntValue("bitrate", bitrate);
                dest.setIntValue("bitrate", bitrate);
            }

            return true;
        }
        return false;
    }

    private static final Pattern IMAGE2_PATTERN = Pattern.compile("^Input #\\d+?, (image\\d*), from.*?");

    /**
     * Matches on Input and looks for the 'image2' format which indicates that the input is an image.
     *
     */
    public boolean image2(String l, Node source, Node dest) {
        Matcher m = IMAGE2_PATTERN.matcher(l);
        if (m.matches()) {
            log.info("image2 match: " + l);
            toImage(source, dest);
            return true;
        } else {
            return false;
        }

    }

    private static final Pattern PATTERN_DIMENSIONS = Pattern.compile(".*?\\sVideo: (.*?), (.*?), ([0-9]+)x([0-9]+).*");

    /**
     * Looks for width and height when it finds a match for Video, and looks for bitrate after that.
     * Works also for images.
     */
    public boolean dimensions(String l, Node source, Node dest) {
        Matcher m = PATTERN_DIMENSIONS.matcher(l);
        if (m.matches()) {
            //log.info("### Dimensions match: " + l);

            if (!source.getNodeManager().getName().equals(IMAGE)) {
                toVideo(source, dest);
            }

            //log.debug("  codec: " + m.group(1));
            //log.debug(" format: " + m.group(2));
            if (log.isDebugEnabled()) log.debug("width: "  + m.group(3));
            if (log.isDebugEnabled()) log.debug("height: " + m.group(4));
            source.setIntValue("width", Integer.parseInt(m.group(3)));
            source.setIntValue("height", Integer.parseInt(m.group(4)));
            if (dest != null) {
                dest.setIntValue("width", Integer.parseInt(m.group(3)));
                dest.setIntValue("height", Integer.parseInt(m.group(4)));
            }

            m = VIDEOBITRATE_PATTERN.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("bitRate: " + m.group(1));
                source.setIntValue("bitrate", Integer.parseInt(m.group(1)));
                if (dest != null) dest.setIntValue("bitrate", Integer.parseInt(m.group(1)));
            }

            return true;
        } else {
            return false;
        }
    }

    private static final Pattern PATTERN_AUDIO = Pattern.compile(".*?\\sAudio: (.*?), (.*?) Hz, (stereo|mono|([0-9]+) channels), .*?");
    private static final Pattern PATTERN_BITRATE2  = Pattern.compile("\\s*Audio: .* bitrate: (.*?) kb/s.*?");

    /**
     * Looks for audio channel(s).
     */
    public boolean audio(String l, Node source, Node dest) {
        Matcher m = PATTERN_AUDIO.matcher(l);
        if (m.matches()) {
            //log.info("### Audio match: " + l);

            //log.debug("   codec: " + m.group(1));
            //log.debug("   freq.: " + m.group(2));
            //log.debug("channels: " + m.group(3));
            String channels = m.group(3);
            if (source.getNodeManager().hasField("channels")) {
                if (channels.equals("stereo") || channels.equals("2")) {
                    source.setIntValue("channels", org.mmbase.applications.media.builders.MediaSources.STEREO);
                    if (dest != null) {
                        dest.setIntValue("channels", org.mmbase.applications.media.builders.MediaSources.STEREO);
                    }
                } else {
                    source.setIntValue("channels", org.mmbase.applications.media.builders.MediaSources.MONO);
                    if (dest != null) {
                        dest.setIntValue("channels", org.mmbase.applications.media.builders.MediaSources.MONO);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }



}
