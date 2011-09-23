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

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.applications.media.State;

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
 * @author Andr&eacute; van Toly
 * @version $Id$
 */

public class FFMpegAnalyzer implements Analyzer {

    private static final Logger LOG = Logging.getLoggerInstance(FFMpegAnalyzer.class);
    
    private final ChainedLogger log = new ChainedLogger(LOG);
    private final AnalyzerUtils util = new AnalyzerUtils(log);
    private List<Throwable> errors = new ArrayList<Throwable>();

    private static boolean supported = true;     // we're very optimistic

    public void setUpdateSource(boolean b) {
        util.setUpdateSource(b);
    }
    
    public void setUpdateDestination(boolean b) {
        util.setUpdateDestination(b);
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

    private String canbe = AnalyzerUtils.MEDIA;

    public void analyze(String l, Node source, Node des) {
        synchronized(util) {

            if (util.unsupported(l, source, des)) {
                log.warn("Not supported! " + l);
                supported = false;
                return;
            }

            if (util.image2(l, source, des)) {
                canbe = AnalyzerUtils.IMAGE;
                return;
            }

            if (util.output(l, source, des)) {
                util.setUpdateDestination(true);
                return;
            }
            
            if (util.duration(l, source, des)) {
                return;
            }

            if (util.dimensions(l, source, des)) {
                if (! canbe.equals(AnalyzerUtils.IMAGE)) {    /* no image2 seen yet, so it can be video */
                    canbe = AnalyzerUtils.VIDEO;
                }
                return;
            }

            if (util.audio(l, source, des)) {
                if (! canbe.equals(AnalyzerUtils.VIDEO)) {    /* no video seen yet, so it can be audio */
                    canbe = AnalyzerUtils.AUDIO;
                }
            }

        }
    }

    public void ready(Node sourceNode, Node destNode) {
        synchronized(util) {
            log.service("Ready() " + sourceNode + (destNode == null ? "" : (" -> " + destNode.getNumber())) + " = canbe: " + canbe);

            if (canbe.equals(AnalyzerUtils.IMAGE) 
                            && (sourceNode.isNull("bitrate") || sourceNode.getIntValue("bitrate") <= 0)) {
                log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is an image " + sourceNode);
                util.toImage(sourceNode, destNode);     // is already done (above) ?

            } else if (canbe.equals(AnalyzerUtils.AUDIO) 
                            && ( !sourceNode.getNodeManager().hasField("width") || sourceNode.isNull("width") )) {
                log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is audio " + sourceNode);
                util.toAudio(sourceNode, destNode);

            } else if (! sourceNode.getNodeManager().getName().equals(AnalyzerUtils.IMAGE) ) {
                log.info("Node " + sourceNode.getNumber() + " " + sourceNode.getStringValue("url") + " is video " + sourceNode + " -> " + sourceNode.getNodeManager().getName());
                util.toVideo(sourceNode, destNode);
                assert sourceNode.getNodeManager().getName().equals(AnalyzerUtils.VIDEO);
            }
            
            if (util.getUpdateSource()) {
                if (supported
                    && (sourceNode.getIntValue("state") == State.SOURCE_UNSUPPORTED.getValue()
                    || sourceNode.getIntValue("state") == State.UNDEFINED.getValue() )) {
                    sourceNode.setIntValue("state", State.SOURCE.getValue());
                }
                sourceNode.commit();
            }
            if (destNode != null) {
                destNode.commit();
            }

            log.info("READY for " + sourceNode.getNodeManager().getName() + " " + sourceNode.hashCode() + " " + sourceNode.getNumber() + " - updateSource: " + util.getUpdateSource());
        }

    }

    @Override
    public FFMpegAnalyzer clone() {
        try {
            return (FFMpegAnalyzer) super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // doesn't happen
            return null;
        }
    }

}
