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
 * @version $Id$
 */

public final class AnalyzerUtils {

    private static final Logger LOG = Logging.getLoggerInstance(AnalyzerUtils.class);


    private final ChainedLogger log = new ChainedLogger(LOG);
    AnalyzerUtils(Logger l) {
        log.addLogger(l);
    }




    public  long getLength(String l) {
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

    private static final Pattern DURATION = Pattern.compile("\\s*Duration: (.*?), start: (.*?), bitrate: (.*?) kb/s.*");


    public boolean duration(String l, Node source, Node des) {
        Matcher m = DURATION.matcher(l);
        if (m.matches()) {
            Node fragment = source.getNodeValue("mediafragment");
            long length = getLength(m.group(1));
            source.setLongValue("length", length);

            log.debug("Duration: " + m.group(1));
            log.debug("Start: " + m.group(2));
            log.debug("BitRate: " + m.group(3));
            source.setIntValue("bitrate", 1000 * Integer.parseInt(m.group(3)));
            return true;
        } else {
            return false;
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
            log.debug("MimType " + actualMimeType + " is correct");
        }
    }

    public void toVideo(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("video", source);
        fixMimeType("video", dest);
        if (cloud != null) {
            if (! source.getNodeManager().getName().equals("videostreamsources")) {
                log.info("This is video, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
                source.setNodeManager(cloud.getNodeManager("videostreamsources"));
                source.commit();
            }
            assert source.getNodeManager().getName().equals("videostreamsources");
            if (dest != null) {
                if (! source.getNodeManager().getName().equals("videostreamsourcescaches")) {
                    dest.setNodeManager(cloud.getNodeManager("videostreamsourcescaches"));
                    dest.commit();
                }
                assert dest.getNodeManager().getName().equals("videostreamsourcescaches");

            }

        }
    }
    public void toAudio(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("audio", source);
        fixMimeType("audio", dest);
        if (cloud != null) {
            log.service("This is audio, now converting type. source: " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""));
            source.setNodeManager(cloud.getNodeManager("audiostreamsources"));
            source.commit();
            assert source.getNodeManager().getName().equals("audiostreamsources");
            if (dest != null) {
                dest.setNodeManager(cloud.getNodeManager("audiostreamsourcescaches"));
                dest.commit();
                assert dest.getNodeManager().getName().equals("audiostreamsourcescaches");

            }

        }
    }

    public void toImage(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        fixMimeType("image", source);
        if (cloud != null) {
            log.info("This is image, now converting type. source: " + source.getNodeManager().getName() + " " + source.getNumber() + (dest != null ? " dest:" +  dest.getNumber() : ""), new Exception());
            source.setNodeManager(cloud.getNodeManager("imagesources"));
            source.commit();
        }
    }


    private static final Pattern VIDEO    = Pattern.compile(".*?\\sVideo: .*?, .*?, ([0-9]+)x([0-9]+).*?([0-9]+)\\s+kb/s.*");


    public boolean video(String l, Node source, Node dest) {
        Matcher m = VIDEO.matcher(l);
        if (m.matches()) {
            toVideo(source, dest);

            log.debug("width: "  + m.group(1));
            log.debug("height: " + m.group(2));
            log.debug("BitRate: " + m.group(3));
            if (source.isNull("channels") || source.getIntValue("channels") <= 0) {
                source.setIntValue("channels", 1);
            } else if (source.getIntValue("channels") == 1) {
                source.setIntValue("channels", 2);
            }
            if (dest != null) {
                dest.setIntValue("channels", source.getIntValue("channels"));
            }
            source.setIntValue("width", Integer.parseInt(m.group(1)));
            source.setIntValue("height", Integer.parseInt(m.group(2)));

            return true;
        } else {
            return false;
        }
    }

    private static final Pattern IMAGE    = Pattern.compile(".*?\\sVideo: .*?, .*?, ([0-9]+)x([0-9]+).*");
    public boolean image(String l, Node source, Node dest) {
        if (! source.isNull("bitrate")) {
            // already has a bitrate
            return false;
        }
        Matcher m = IMAGE.matcher(l);
        if (m.matches()) {
            toImage(source, dest);

            log.debug("width: "  + m.group(1));
            log.debug("height: " + m.group(2));
            source.setIntValue("width", Integer.parseInt(m.group(1)));
            source.setIntValue("height", Integer.parseInt(m.group(2)));

            return true;
        } else {
            return false;
        }
    }


    public static Node getTestNode() {
        Map<String, Object> sourceNode = new HashMap<String, Object>();
        sourceNode.put("width", null);
        sourceNode.put("height", null);
        sourceNode.put("bitrate", null);
        sourceNode.put("channels", null);
        sourceNode.put("length", null);
        sourceNode.put("mimetype", null);
        return new MapNode(sourceNode);

    }
}
