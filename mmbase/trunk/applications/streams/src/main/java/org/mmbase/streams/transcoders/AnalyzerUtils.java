/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

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

    private AnalyzerUtils() {
        // no instances for this class
    }

    private static final Logger log = Logging.getLoggerInstance(AnalyzerUtils.class);


    public static  long getLength(String l) {
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

    public static boolean duration(String l, Node source, Node des) {
        Matcher m = DURATION.matcher(l);
        if (m.matches()) {
            Node fragment = source.getNodeValue("mediafragment");
            long length = getLength(m.group(1));
            source.setLongValue("length", length);

            log.info("Duration: " + m.group(1));
            log.info("Start: " + m.group(2));
            log.info("BitRate: " + m.group(3));
            source.setIntValue("bitrate", 1000 * Integer.parseInt(m.group(3)));
            return true;
        } else {
            return false;
        }
    }

    public static void toVideo(Node source, Node dest) {
        Cloud cloud = source.getCloud();
        if (cloud != null) {
            log.service("This is video, now converting type. source: " + source.getNumber() + " dest:" +  dest.getNumber());
            source.setNodeManager(cloud.getNodeManager("videostreamsources"));
            dest.setNodeManager(cloud.getNodeManager("videostreamsourcescaches"));
            source.commit();
            dest.commit();
            assert source.getNodeManager().getName().equals("videostreamsources");
            assert dest.getNodeManager().getName().equals("videostreamsourcescaches");
        }
    }


    private static final Pattern VIDEO    = Pattern.compile(".*?\\sVideo: .*?, .*?, ([0-9]+)x([0-9]+).*?([0-9]+)\\s+kb/s.*");

    public static boolean video(String l, Node source, Node dest) {
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
            dest.setIntValue("channels", source.getIntValue("channels"));
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
        return new MapNode(sourceNode);

    }
}
