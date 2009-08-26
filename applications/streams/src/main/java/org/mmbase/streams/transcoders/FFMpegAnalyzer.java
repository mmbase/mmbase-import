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
    
    private String canbe = util.MEDIA;

    public void analyze(String l, Node source, Node des) {
        Cloud cloud = source.getCloud();
        
        if (util.image2(l, source, des)) {
            log.info("Probably an image " + source);
            canbe = util.IMAGE;
            return;
        }

        if (util.duration(l, source, des)) {
            log.info("Found length " + source);
            return;
        }
        
        /*
        if (util.video(l, source, des)) {
            log.info("Found video " + source);
            return;
        }
        */
        
        if (util.dimensions(l, source, des)) {
            log.info("Found dimensions " + source);
            return;
        }
        
        if (util.audio(l, source, des)) {
            log.info("Found audio: " + source);
            
            if (! canbe.equals(util.VIDEO)) {
                /* no video seen yet, so it can be audio */
                canbe = util.AUDIO;
            }
        }
        
        if (util.image(l, source, des)) {
            log.info("Found image " + source);
            return;
        }
    }

    public void ready(Node sourceNode, Node destNode) {
        log.service("Ready() " + sourceNode.getNumber() + (destNode == null ? "" : (" -> " + destNode.getNumber())));
        log.info("canbe: " + canbe);
        if ((sourceNode.isNull("bitrate") || sourceNode.getIntValue("bitrate") <= 0) && canbe.equals(util.IMAGE)) {
            /* BUG: this is incorrect, on some video's like flv ffmpeg does not report bitrate */
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an image");
            util.toImage(sourceNode, destNode);
        } else if (canbe.equals(util.AUDIO) && !sourceNode.getNodeManager().hasField("width") ) {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an audio because nodemanager " + sourceNode.getNodeManager());
            util.toAudio(sourceNode, destNode);
        } else if (canbe.equals(util.AUDIO) && sourceNode.isNull("width")) {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an audio because width is null " + sourceNode);
            util.toAudio(sourceNode, destNode);
        } else {
            log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is a video");
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
