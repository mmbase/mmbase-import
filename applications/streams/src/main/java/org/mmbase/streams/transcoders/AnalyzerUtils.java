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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mmbase.applications.media.Codec;
import org.mmbase.applications.media.State;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.util.MimeType;


import org.mmbase.util.logging.*;

/**
 * Utility methods common for the analyzers that look for several media audio, video and images,
 * that try to extract information, convert nodetypes to the matching kind etc.
 *
 * @author Michiel Meeuwissen
 * @author Andr&eacute; van Toly
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

    private boolean updateSource      = false;
    private boolean updateDestination = false;

    AnalyzerUtils(Logger... loggers) {
        for (Logger l : loggers) {
            log.addLogger(l);
        }
    }

    public void setUpdateSource(boolean b) {
        updateSource = b;
    }
    public boolean getUpdateSource() {
        return updateSource;
    }
    public void setUpdateDestination(boolean b) {
        updateDestination = b;
    }

    /**
     * This fixes the first part (f.e. audio/*) when needed, not the complete MimeType.
     * @todo Should this perhaps be arranged in the respective builders themselves. It seems a
     * requirement of videosources to have a video/* mimetype
     *
     */
    protected void fixMimeType(String type, Node node) {
        if (node == null) return;
        MimeType actualMimeType = new MimeType(node.getStringValue("mimetype"));
        if (! actualMimeType.getType().equals(type)) {
            MimeType newType = new MimeType(type, actualMimeType.getSubType());
            node.setStringValue("mimetype", newType.toString());
            log.info("Fixed mime type for node #" + node.getNumber() + ": " + actualMimeType + "-> " + newType) ;
        } else {
            if (log.isDebugEnabled()) log.debug("MimeType " + actualMimeType + " is correct");
        }
    }

    public synchronized void toVideo(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("video", source);
        fixMimeType("video", dest);
        if (cloud != null) {
            if (updateSource && (! source.getNodeManager().getName().equals(VIDEO))) {
                log.info("This is video, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
                source.setNodeManager(cloud.getNodeManager(VIDEO));
                source.commit();
            }
            assert source.getNodeManager().getName().equals(VIDEO) : "" + updateSource;
            if (dest != null) {
                if (! dest.getNodeManager().getName().equals(VIDEOC)) {
                    dest.setNodeManager(cloud.getNodeManager(VIDEOC));
                    dest.commit();
                }
                assert dest.getNodeManager().getName().equals(VIDEOC);
            }
        } else {
            log.warn("No cloud");
        }
    }

    public synchronized void toAudio(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("audio", source);
        fixMimeType("audio", dest);
        if (cloud != null) {
            if (updateSource && ! source.getNodeManager().getName().equals(AUDIO)) {
                log.info("This is audio, now converting type. source: " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
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
            if (updateSource && cloud.hasNodeManager(IMAGE)
                    && ! source.getNodeManager().getName().equals(IMAGE)) {
                if (log.isDebugEnabled()) {
                    log.debug("This is image, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
                }
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
    private static final Pattern PATTERN_INVALID_DATA = Pattern.compile("(.*)Invalid data found when processing input");

    /* Looks for messages from ffmpeg that it does not support this kind of file.
     * browserevent.ram: Unknown format
     * [NULL @ 0x1804800]Unsupported video codec
    */
    public boolean unsupported(String l, Node source, Node dest) {
        if (! updateSource) {
            return false;
        }
        {
            Matcher m = PATTERN_UNKNOWN.matcher(l.trim());
            if (m.matches()) {
                log.warn("UNKNOWN format: " + m.group(1) + " : " + source.getNumber() + " " + source.getStringValue("url") + " matched on " + l);
                source.setIntValue("state", State.SOURCE_UNSUPPORTED.getValue());
                source.commit();
                return true;
            }
        }
        {
            Matcher m = PATTERN_UNSUPPORTED.matcher(l);
            if (m.matches()) {
                log.warn("UNSUPPORTED " + m.group(1) + " : " + source.getNumber());

                source.setIntValue("state", State.SOURCE_UNSUPPORTED.getValue());
                source.commit();
                return true;
            }
        }
        {
            Matcher m = PATTERN_INVALID_DATA.matcher(l.trim());
            if (m.matches()) {
                log.warn("INVALID DATA " + m.group(1) + " : " + source.getNumber());
                source.setIntValue("state", State.SOURCE_UNSUPPORTED.getValue());
                source.commit();
                return true;
            }
        }
        return false;
    }

    /* Output #0, mpeg, to 'presto.mpeg': */
    private static final Pattern PATTERN_OUTPUT = Pattern.compile("^Output #\\d+?, (.*), to.*?");

    public boolean output(String l, Node source, Node dest) {
        Matcher m = PATTERN_OUTPUT.matcher(l);
        if (m.matches()) {
            log.info("### OUTPUT match: " + l);
            if (log.isDebugEnabled()) log.debug("format: " + m.group(1));
            updateDestination = true;
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
            Node fragment = source.getNodeValue("mediafragment");

            if (! source.getNodeManager().hasField("length")) {
                toVideo(source, dest);
            }

            if (log.isDebugEnabled()) log.debug("duration: " + m.group(1));
            long length = getLength(m.group(1));
            if (updateSource) {
                source.setLongValue("length", length);
            }
            if (updateDestination && dest != null) {
                dest.setLongValue("length", length);
            }

            m = PATTERN_BITRATE.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("bitrate: " + m.group(1));
                int bitrate = 1000 * Integer.parseInt(m.group(1));
                if (updateSource) {
                    source.setIntValue("bitrate", bitrate);
                }
                if (updateDestination && dest != null) {
                    dest.setIntValue("bitrate", bitrate);
                }
            }

            m = PATTERN_START.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("start: " + m.group(1));
                long start = getStart(m.group(1));
                Cloud cloud = source.getCloud();
                if (updateSource && fragment != null && cloud.hasNode(fragment.getNumber())) {
                    fragment.setLongValue("start", start);
                    fragment.commit();
                    if (log.isDebugEnabled()) log.debug("Set mediafragment's field start: " + start);
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

            if (updateSource) {
                source.setIntValue("width", Integer.parseInt(m.group(1)));
                source.setIntValue("height", Integer.parseInt(m.group(2)));
            }

            m = VIDEOBITRATE_PATTERN.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("bitrate: " + m.group(1));
                int bitrate = 1000 * Integer.parseInt(m.group(1));
                if (updateSource) {
                    source.setIntValue("bitrate", bitrate);
                }
                if (updateDestination && dest != null) {
                    dest.setIntValue("bitrate", bitrate);
                }
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
            //log.info("image2 match: " + l);
            toImage(source, dest);
            return true;
        } else {
            return false;
        }

    }

    private static final Pattern PATTERN_DIMENSIONS    = Pattern.compile(".*?\\sVideo: (.*?), (.*?), ([0-9]+)x([0-9]+).*");
    private static final Pattern VIDEOBITRATE2_PATTERN = Pattern.compile(".*?\\sVideo: .*, (.*?) kb/s.*");
    private static final Pattern VIDEOFPS_PATTERN      = Pattern.compile(".*?\\sVideo: .*, (.*?) fps.*");

    /**
     * Looks for width and height when it finds a match for Video, and looks for bitrate after that.
     * Works also for images.
     */
    public boolean dimensions(String l, Node source, Node dest) {
        Matcher m = PATTERN_DIMENSIONS.matcher(l);
        if (m.matches()) {
            //log.info("dimensions match: " + l);

            if (! source.getNodeManager().getName().equals(IMAGE)) {
                toVideo(source, dest);
            }

            if (log.isDebugEnabled()) {
                log.debug(" codec: " + m.group(1));
                log.debug("format: " + m.group(2));
                log.debug(" width: "  + m.group(3));
                log.debug("height: " + m.group(4));
            }

            if (updateSource) {
                if (source.getIntValue("codec") < 0) {
                    source.setIntValue("codec", libtoCodec(m.group(1)).toInt() );
                }
                source.setIntValue("width", Integer.parseInt(m.group(3)));
                source.setIntValue("height", Integer.parseInt(m.group(4)));
            }
            if (updateDestination && dest != null) {
                if (dest.getIntValue("codec") < 0) {
                    dest.setIntValue("codec", libtoCodec(m.group(1)).toInt() );
                }
                dest.setIntValue("width", Integer.parseInt(m.group(3)));
                dest.setIntValue("height", Integer.parseInt(m.group(4)));
            }

            m = VIDEOBITRATE_PATTERN.matcher(l);
            Matcher m2 = VIDEOBITRATE2_PATTERN.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("bitrate: " + m.group(1));
                int bitrate = 1000 * Integer.parseInt(m.group(1));
                if (updateSource) {
                    source.setIntValue("bitrate", bitrate);
                }
                if (updateDestination && dest != null) {
                    dest.setIntValue("bitrate", bitrate);
                }
            } else if (m2.matches()) {
                if (log.isDebugEnabled()) log.debug("bitrate: " + m2.group(1));
                int bitrate = 1000 * Integer.parseInt(m2.group(1));
                if (updateSource) {
                    source.setIntValue("bitrate", bitrate);
                }
                if (updateDestination && dest != null) {
                    dest.setIntValue("bitrate", bitrate);
                }
            }

            m = VIDEOFPS_PATTERN.matcher(l);
            if (m.matches()) {
                if (log.isDebugEnabled()) log.debug("fps: " + m.group(1));
                double d = Double.parseDouble(m.group(1));
                int fps = (int)Math.round(d);
                if (updateSource) {
                    source.setIntValue("fps", fps );
                }
                if (updateDestination && dest != null) {
                    dest.setIntValue("fps", fps );
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static final Pattern PATTERN_AUDIO = Pattern.compile(".*?\\sAudio: (.*?), (.*?) Hz, (stereo|mono|([0-9]+) channels), .*?");
    private static final Pattern AUDIOBITRATE_PATTERN  = Pattern.compile(".*?\\sAudio: .* bitrate: (.*?) kb/s.*");
    private static final Pattern AUDIOBITRATE2_PATTERN = Pattern.compile(".*?\\sAudio: .*, (.*?) kb/s.*");

    /**
     * Looks for audio channel(s).
     */
    public boolean audio(String l, Node source, Node dest) {
        Matcher m = PATTERN_AUDIO.matcher(l);
        if (m.matches()) {
            //log.info("### Audio match: " + l);

            if (log.isDebugEnabled()) {
                log.debug("   codec: " + m.group(1));
                log.debug("   freq.: " + m.group(2));
                log.debug("channels: " + m.group(3));
            }

            String channels = m.group(3);
            int ch = org.mmbase.applications.media.builders.MediaSources.MONO;
            if (channels.equals("stereo") || channels.startsWith("2")) {
                ch = org.mmbase.applications.media.builders.MediaSources.STEREO;
            }

            if (source.getNodeManager().hasField("channels") && updateSource) {
                if (source.getIntValue("channels") < 0) source.setIntValue("channels", ch);

                if (source.getNodeManager().hasField("acodec") && source.getIntValue("acodec") < 0) {
                    source.setIntValue("acodec", libtoCodec(m.group(1)).toInt() );
                } else if (source.getIntValue("codec") < 0) {
                    source.setIntValue("codec", libtoCodec(m.group(1)).toInt() );
                }
            }
            if (updateDestination && dest != null) {
                if (dest.getIntValue("channels") < 0) {
                    dest.setIntValue("channels", ch);
                }
                if (dest.getNodeManager().hasField("acodec")) {
                    dest.setIntValue("acodec", libtoCodec(m.group(1)).toInt() );
                } else {
                    dest.setIntValue("codec", libtoCodec(m.group(1)).toInt() );
                }
            }

            if (source.getNodeManager().getName().equals(AUDIO) ||
                    (dest != null && dest.getNodeManager().getName().equals(AUDIOC))) {
                m = AUDIOBITRATE_PATTERN.matcher(l);
                Matcher m2 = AUDIOBITRATE2_PATTERN.matcher(l);
                if (m.matches()) {
                    if (log.isDebugEnabled()) log.debug("bitrate: " + m.group(1));
                    int bitrate = 1000 * Integer.parseInt(m2.group(1));
                    if (updateSource) {
                        source.setIntValue("bitrate", Integer.parseInt(m.group(1)));
                    }
                    if (updateDestination && dest != null) {
                        dest.setIntValue("bitrate", Integer.parseInt(m.group(1)));
                    }
                } else if (m2.matches()) {
                    if (log.isDebugEnabled()) log.debug("bitrate: " + m2.group(1));
                    int bitrate = 1000 * Integer.parseInt(m2.group(1));
                    if (updateSource) {
                        source.setIntValue("bitrate", bitrate);
                    }
                    if (updateDestination && dest != null) {
                        dest.setIntValue("bitrate", bitrate);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static Codec libtoCodec(String str) {
        if (str.equals("libvpx")) str = "vp8";
        if (str.equals("libx264")) str = "h264";
        if (str.equals("libfaac")) str = "aac";
        if (str.equals("libmp3lame")) str = "mp3";
        if (str.equals("mpeg1video")) str = "mpeg";
        if (str.equals("mpeg2video")) str = "mpeg2";
        if (str.startsWith("lib")) str = str.substring(3, str.length());

        return Codec.get(str);
    }


}
