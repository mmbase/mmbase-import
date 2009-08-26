/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.applications.media.MimeType;
import java.util.*;
import java.util.regex.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.*;



/**
 *
 * @author Michiel Meeuwissen
 * @author Andre van Toly
 * @version $Id$
 */

public final class AnalyzerUtils {

    private static final Logger LOG = Logging.getLoggerInstance(AnalyzerUtils.class);

    public static final String VIDEO = "videostreamsources";
    public static final String AUDIO = "audiostreamsources";
    public static final String IMAGE = "imagesources";
    public static final String MEDIA = "mediastreamsources";

    public static final String VIDEOC = VIDEO + "caches";
    public static final String AUDIOC = AUDIO + "caches";
    public static final String IMAGEC = IMAGE + "caches";
    public static final String MEDIAC = MEDIA + "caches";

    private final ChainedLogger log = new ChainedLogger(LOG);
    AnalyzerUtils(Logger l) {
        log.addLogger(l);
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
            log.debug("MimeType " + actualMimeType + " is correct");
        }
    }

    public void toVideo(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("video", source);
        fixMimeType("video", dest);
        if (cloud != null) {
            if (! source.getNodeManager().getName().equals(VIDEO)) {
                log.info("This is video, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
                source.setNodeManager(cloud.getNodeManager(VIDEO));
                source.commit();
            }
            assert source.getNodeManager().getName().equals(VIDEO);
            if (dest != null) {
                if (! source.getNodeManager().getName().equals(VIDEOC)) {
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
            log.service("This is audio, now converting type. source: " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
            source.setNodeManager(cloud.getNodeManager(AUDIO));
            source.commit();
            assert source.getNodeManager().getName().equals(AUDIO);
            if (dest != null) {
                dest.setNodeManager(cloud.getNodeManager(AUDIOC));
                dest.commit();
                assert dest.getNodeManager().getName().equals(AUDIOC);

            }

        }
    }

    public void toImage(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("image", source);
        if (cloud != null) {
            if (log.isDebugEnabled()) {
                log.debug("This is image, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""), new Exception());
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

    /* ffmpeg reports sometimes no start and on some video's bitrate: N/A */ 
    private static final Pattern PATTERN_DURATION = Pattern.compile("\\s*Duration: (.*?),.* bitrate:.*?");
    private static final Pattern PATTERN_BITRATE  = Pattern.compile("\\s*Duration: .* bitrate: (.*?) kb/s.*?");
    private static final Pattern PATTERN_START    = Pattern.compile("\\s*Duration: .* start: (.*?), bitrate:.*?");

    public boolean duration(String l, Node source, Node des) {
        Matcher m = PATTERN_DURATION.matcher(l);
        if (m.matches()) {
            log.info(l);
            
            Node fragment = source.getNodeValue("mediafragment");
            if (! source.getNodeManager().hasField("length")) {
                toVideo(source, des);
            }
            log.info("Duration: " + m.group(1));
            long length = getLength(m.group(1));
            source.setLongValue("length", length);

            m = PATTERN_BITRATE.matcher(l);
            if (m.matches()) {
                log.info("BitRate: " + m.group(1));
                source.setIntValue("bitrate", 1000 * Integer.parseInt(m.group(1)));
            }

            m = PATTERN_START.matcher(l);
            if (m.matches()) {
                log.info("Start: " + m.group(1));
                long start = getStart(m.group(1));
                if (fragment != null) {
                    fragment.setLongValue("start", start);
                } else {
                    log.warn("mediafragment still null");
                }
            }

            return true;
        } else {
            return false;
        }
    }
    
    private static final Pattern VIDEO_PATTERN    = Pattern.compile(".*?\\sVideo: .*?, .*?, ([0-9]+)x([0-9]+).*?([0-9]+)\\s+kb/s.*");
    // Not all video's have bitrate. E.g. basic.mov:
    //Stream #0.1(eng): Video: h264, yuv420p, 640x480, 29.97 tb(r)
    private static final Pattern VIDEO_PATTERN2    = Pattern.compile(".*?\\sVideo: .*?, .*?, ([0-9]+)x([0-9]+).*");

    protected void incChannels(Node source, Node dest) {
        if (source.isNull("channels") || source.getIntValue("channels") <= 0) {
            source.setIntValue("channels", 1);
        } else if (source.getIntValue("channels") == 1) {
            source.setIntValue("channels", 2);
        }
        if (dest != null) {
            dest.setIntValue("channels", source.getIntValue("channels"));
        }
    }

    public boolean video(String l, Node source, Node dest) {
        Matcher m = VIDEO_PATTERN.matcher(l);
        if (m.matches()) {
            toVideo(source, dest);

            log.debug("width: "  + m.group(1));
            log.debug("height: " + m.group(2));
            log.debug("BitRate: " + m.group(3));
            incChannels(source, dest);

            source.setIntValue("width", Integer.parseInt(m.group(1)));
            source.setIntValue("height", Integer.parseInt(m.group(2)));
            source.setIntValue("bitrate", Integer.parseInt(m.group(3)));

            return true;
        }
        m = VIDEO_PATTERN2.matcher(l);
        if (m.matches()) {
            toVideo(source, dest);
            incChannels(source, dest);
            source.setIntValue("width", Integer.parseInt(m.group(1)));
            source.setIntValue("height", Integer.parseInt(m.group(2)));
            return true;
        }
        return false;
    }

    private static final Pattern IMAGE_PATTERN    = Pattern.compile(".*?\\sVideo: .*?, .*?, ([0-9]+)x([0-9]+).*");
    /*
    use this in stead for image matching and do height and width in other method ?
    private static final Pattern IMAGE    = Pattern.compile("^Input #\\d+, image.*");
    */
    
    public boolean image(String l, Node source, Node dest) {
        /* not true: flv's do not seem to report a bitrate
        if (! source.isNull("bitrate")) {
            // already has a bitrate
            return false;
        }
        */
        Matcher m = IMAGE_PATTERN.matcher(l);
        if (m.matches()) {
            log.debug("Image match!");
            toImage(source, dest);

            log.debug("width:  " + m.group(1));
            log.debug("height: " + m.group(2));
            source.setIntValue("width", Integer.parseInt(m.group(1)));
            source.setIntValue("height", Integer.parseInt(m.group(2)));

            return true;
        } else {
            return false;
        }
    }

    private static final Pattern INPUT_PATTERN = Pattern.compile("^Input #\\d+?, (.+?), from '([^']+)?.*");
    private static final Pattern INPUT_PATTERN2 = Pattern.compile("^Input #\\d+?, (image\\d*), from.*?");
    
    public boolean image2(String l, Node source, Node dest) {
        Matcher m = INPUT_PATTERN2.matcher(l);
        if (m.matches()) {
            log.debug("## Input matches an image! Match: " + m.group(1));
            
            return true;
        } else {
            return false;
        }
        
    }
    
    private static final Pattern PATTERN_DIMENSIONS = Pattern.compile(".*?\\sVideo: (.*?), (.*?), ([0-9]+)x([0-9]+).*");
    
    public boolean dimensions(String l, Node source, Node dest) {
        Matcher m = PATTERN_DIMENSIONS.matcher(l);
        if (m.matches()) {
            log.info(l);
            toVideo(source, dest);
            
            log.debug("  codec: " + m.group(1));
            log.debug(" format: " + m.group(2));
            log.info("  width: " + m.group(3));
            log.info(" height: " + m.group(4));
            incChannels(source, dest);
            
            source.setIntValue("width", Integer.parseInt(m.group(3)));
            source.setIntValue("height", Integer.parseInt(m.group(4)));
            
            return true;
        } else {
            return false;
        }
    }
    
    private static final Pattern PATTERN_AUDIO = Pattern.compile(".*?\\sAudio: (.*?), (.*?) Hz, (stereo|mono|([0-9]+) channels), .*?");

    public boolean audio(String l, Node source, Node dest) {
        Matcher m = PATTERN_AUDIO.matcher(l);
        if (m.matches()) {
            log.info(l);
            
            log.debug("   codec: " + m.group(1));
            log.debug("   freq.: " + m.group(2));
            log.debug("channels: " + m.group(3));
            
            return true;
        } else {
            return false;
        }
    }
    
    

}
