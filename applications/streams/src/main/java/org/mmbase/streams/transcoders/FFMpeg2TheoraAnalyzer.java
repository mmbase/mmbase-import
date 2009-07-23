/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import java.util.regex.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;



/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class FFMpeg2TheoraAnalyzer implements Analyzer {


    private static final Logger LOG = Logging.getLoggerInstance(FFMpeg2TheoraAnalyzer.class);


    public int getMaxLines() {
        return Integer.MAX_VALUE;
    }


    private static final Pattern NORESIZE   = Pattern.compile("\\s*Resize: ([0-9]+)x([0-9]+).*");
    private static final Pattern RESIZE   = Pattern.compile("\\s*Resize: ([0-9]+)x([0-9]+) => ([0-9]+)x([0-9]+).*");
    private static final Pattern PROGRESS = Pattern.compile("\\s*(.*?) audio: ([0-9]+)kbps video: ([0-9]+)kbps, time remaining: .*");

    private long length = 0;
    private double bits = -1;
    private long prevPos = 0;

    private ChainedLogger log = new ChainedLogger(LOG);

    private AnalyzerUtils util = new AnalyzerUtils(log);

    private List<Throwable> errors =new ArrayList<Throwable>();

    public void addThrowable(Throwable t) {
        errors.add(t);
    }

    public void addLogger(Logger logger) {
        log.addLogger(logger);
    }



    public void analyze(String l, Node source, Node des) {
        Cloud cloud = source.getCloud();
        if (util.duration(l, source, des)) {
            length = source.getLongValue("length");
            log.info("Found length " + source);
            return;
        }
        if (util.video(l, source, des)) {
            log.info("Found video " + source);
            return;
        }

        {
            Matcher m = RESIZE.matcher(l);
            if (m.matches()) {
                util.toVideo(source, des);
                log.info("Found " + m);
                source.setIntValue("width", Integer.parseInt(m.group(1)));
                source.setIntValue("height", Integer.parseInt(m.group(2)));
                source.commit();
                des.setIntValue("width", Integer.parseInt(m.group(3)));
                des.setIntValue("height", Integer.parseInt(m.group(4)));
                des.commit();

            } else {
                Matcher n = NORESIZE.matcher(l);
                if (n.matches()) {
                    log.info("Found " + m);
                    util.toVideo(source, des);
                    source.setIntValue("width", Integer.parseInt(n.group(1)));
                    source.setIntValue("height", Integer.parseInt(n.group(2)));
                    source.commit();
                    des.setIntValue("width", Integer.parseInt(n.group(1)));
                    des.setIntValue("height", Integer.parseInt(n.group(2)));
                    des.commit();

                }
            }
        }
        {
            Matcher m = PROGRESS.matcher(l);
            if (m.matches()) {
                long pos = util.getLength(m.group(1));
                long audioBitrate = Integer.parseInt(m.group(2));
                long videoBitrate = Integer.parseInt(m.group(3));
                bits += ((double) (audioBitrate + videoBitrate)) * ((double) pos - prevPos) * 1000;
                //System.out.println("" + pos + "ms "  + (audioBitrate + videoBitrate) + " -> " + (bits / pos) + " " + (100 * pos / length) + " %");

                prevPos = pos;
            }
        }


    }

    public void ready(Node sourceNode, Node destNode) {
        System.out.println("length: " + length + " prevPos " + prevPos);
        if (bits > 0 && length > 0) {
            destNode.setIntValue("bitrate", (int) (bits / length));
        }
    }

    public FFMpeg2TheoraAnalyzer clone() {
        try {
            return (FFMpeg2TheoraAnalyzer) super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // doesn't happen
            return null;
        }
    }

}
