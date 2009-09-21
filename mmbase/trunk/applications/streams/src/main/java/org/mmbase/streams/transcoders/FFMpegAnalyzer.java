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
import static org.mmbase.streams.transcoders.AnalyzerUtils.*;


/**
 *
 * An implementation of Analyzer that uses FFmpeg to analyze files. It tries to recognize
 * images, audio and video - in that order. FFmpeg reports various information when you throw a file
 * at it with 'ffmpeg -i file.ext' some of these lines are examined f.e.:
 * <pre>
 *  Input #0, wav, from 'basic.wav':
 *    Duration: 00:00:02.94, bitrate: 384 kb/s
 *      Stream #0.0: Audio: pcm_s24le, 8000 Hz, 2 channels, s16, 384 kb/s
 * </pre>
 *
 * @author Michiel Meeuwissen
 * @author Andre van Toly
 * @version $Id$
 */

public class FFMpegAnalyzer implements Analyzer {


    private static final Logger LOG = Logging.getLoggerInstance(FFMpegAnalyzer.class);

    private final ChainedLogger log = new ChainedLogger(LOG);

    private final AnalyzerUtils util = new AnalyzerUtils(log);


    private List<Throwable> errors = new ArrayList<Throwable>();


    public void setUpdateSource(boolean b) {
        util.setUpdateSource(b);
    }

    public void addThrowable(Throwable t) {
        errors.add(t);
    }

    public int getMaxLines() {
        return 1000;
    }

    public void addLogger(Logger logger) {
        log.addLogger(logger);
    }

    private String canbe = MEDIA;

    public void analyze(String l, Node source, Node des) {
        Cloud cloud = source.getCloud();

        if (util.unsupported(l, source, des)) {
            // TODO: make something to report this to user
            log.warn("Not supported! " + l);
            return;
        }

        if (util.image2(l, source, des)) {
            if (log.isDebugEnabled()) log.debug("Found an image " + source);
            canbe = AnalyzerUtils.IMAGE;
            return;
        }

        if (util.duration(l, source, des)) {
            if (log.isDebugEnabled()) log.debug("Found length " + source);
            return;
        }

        if (util.dimensions(l, source, des)) {
            if (log.isDebugEnabled()) log.debug("Found dimensions " + source);
            return;
        }

        if (util.audio(l, source, des)) {
            if (log.isDebugEnabled()) log.debug("Found audio: " + source);

            if (! canbe.equals(VIDEO)) {
                /* no video seen yet, so it can be audio */
                canbe = AUDIO;
            }
        }

    }

    public void ready(Node sourceNode, Node destNode) {
        log.service("Ready() " + sourceNode.getNumber() + (destNode == null ? "" : (" -> " + destNode.getNumber())));

        if (canbe.equals(IMAGE) && (sourceNode.isNull("bitrate") || sourceNode.getIntValue("bitrate") <= 0)) {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an image " + sourceNode);
            util.toImage(sourceNode, destNode);

        } else if (canbe.equals(AUDIO) && !sourceNode.getNodeManager().hasField("width") ) {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is audio " + sourceNode);
            util.toAudio(sourceNode, destNode);

        } else if (canbe.equals(AUDIO) && sourceNode.isNull("width")) {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is audio " + sourceNode);
            util.toAudio(sourceNode, destNode);

        } else {
            util.toVideo(sourceNode, destNode);
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is video " + sourceNode + " -> " + sourceNode.getNodeManager().getName());
            assert sourceNode.getNodeManager().getName().equals(VIDEO);
        }
        if (util.getUpdateSource()) {
            sourceNode.commit();
        }
        if (destNode != null) {
            destNode.commit();
        }

        log.info("READY for " + sourceNode.getNodeManager().getName() + " " + sourceNode.hashCode() + " " + sourceNode.getNumber());

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
