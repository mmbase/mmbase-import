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

    public void analyze(String l, Node source, Node des) {
        Cloud cloud = source.getCloud();
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
        log.info("Ready " + sourceNode.getNumber() + (destNode == null ? "" : (" -> " + destNode.getNumber())));
        if (sourceNode.isNull("bitrate")) {
            log.service("Node " + sourceNode.getNumber() + " is an image");
            util.toImage(sourceNode, destNode);
        } else if (! sourceNode.getNodeManager().hasField("width")) {
            log.service("Node " + sourceNode.getNumber() + " is audio");
            util.toAudio(sourceNode, destNode);
        } else {
            log.service("Node " + sourceNode.getNumber() + " is video");
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
