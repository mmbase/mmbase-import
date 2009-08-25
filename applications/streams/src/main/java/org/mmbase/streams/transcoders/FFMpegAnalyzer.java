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

public class FFMpegAnalyzer implements Analyzer {


    private static final Logger LOG = Logging.getLoggerInstance(FFMpegAnalyzer.class);

    private final ChainedLogger log = new ChainedLogger(LOG);

    private final AnalyzerUtils util = new AnalyzerUtils(log);


    private List<Throwable> errors =new ArrayList<Throwable>();

    public void addThrowable(Throwable t) {
        errors.add(t);
    }

    public int getMaxLines() {
        return 100;
    }

    public void addLogger(Logger logger) {
        log.addLogger(logger);
    }
    
    private String probably = util.MEDIA;

/*

Input #0, mov,mp4,m4a,3gp,3g2,mj2, from 'presto.mp4':
  Duration: 00:00:10.56, start: 0.000000, bitrate: 389 kb/s
    Stream #0.0(eng): Audio: aac, 44100 Hz, 2 channels, s16
    Stream #0.1(eng): Video: mpeg4, yuv420p, 352x288 [PAR 1:1 DAR 11:9], 30 tbr, 600 tbn, 1k tbc

*/

    public void analyze(String l, Node source, Node des) {
        Cloud cloud = source.getCloud();
        
        if (util.image2(l, source, des)) {
            log.service("Probably an image " + source);
            probably = util.IMAGE;
            return;
        }

        if (util.duration(l, source, des)) {
            log.service("Found length " + source);
            return;
        }
        if (util.video(l, source, des)) {
            log.service("Found video " + source);
            return;
        }
        if (util.image(l, source, des)) {
            log.service("Found image " + source);
            return;
        }
    }

    public void ready(Node sourceNode, Node destNode) {
        log.service("Ready() " + sourceNode.getNumber() + (destNode == null ? "" : (" -> " + destNode.getNumber())));
        if (sourceNode.isNull("bitrate") || sourceNode.getIntValue("bitrate") <= 0) {
            /* BUG: this is incorrect, on some video's like flv ffmpeg does not report bitrate */
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an image");
            util.toImage(sourceNode, destNode);
        } else if ((! sourceNode.getNodeManager().hasField("width"))) {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an audio because nodemanager " + sourceNode.getNodeManager());
            util.toAudio(sourceNode, destNode);
        } else if (sourceNode.isNull("width")) {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an audio because width is null " + sourceNode);
            util.toAudio(sourceNode, destNode);
        } else {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an video");
            util.toVideo(sourceNode, destNode);
        }
        //
    }

    public FFMpegAnalyzer clone() {
        try {
            return (FFMpegAnalyzer) super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // doesn't happen
            return null;
        }
    }

}
