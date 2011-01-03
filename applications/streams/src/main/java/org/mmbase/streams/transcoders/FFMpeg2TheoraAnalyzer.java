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

    private final ChainedLogger log = new ChainedLogger(LOG);

    private final AnalyzerUtils util = new AnalyzerUtils(log);

    private final List<Throwable> errors = new ArrayList<Throwable>();

    public void addThrowable(Throwable t) {
        errors.add(t);
    }

    public void addLogger(Logger logger) {
        log.addLogger(logger);
    }

    public void analyze(String l, Node source, Node des) {
        synchronized(util) {
            Cloud cloud = source.getCloud();
            if (util.duration(l, source, des)) {
                length = source.getLongValue("length");
                return;
            }
    
            if (util.audio(l, source, des)) {
                util.setUpdateDestination(true);
                util.audio(l, source, des);
                util.setUpdateDestination(false);
                return;
            }

            {
                Matcher m = RESIZE.matcher(l);
                if (m.matches()) {
                    util.toVideo(source, des);
                    log.debug("Found " + m);
                    des.setIntValue("width", Integer.parseInt(m.group(3)));
                    des.setIntValue("height", Integer.parseInt(m.group(4)));
                    des.commit();
    
                } else {
                    Matcher n = NORESIZE.matcher(l);
                    if (n.matches()) {
                        log.debug("Found " + n);
                        util.toVideo(source, des);
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

    }

    public void ready(Node sourceNode, Node destNode) {
        synchronized(util) {
            //System.out.println("length: " + length + " prevPos " + prevPos);
            if (bits > 0 && length > 0) {
                destNode.setIntValue("bitrate", (int) (bits / length));
            }
        }
    }

    @Override
    public FFMpeg2TheoraAnalyzer clone() {
        try {
            return (FFMpeg2TheoraAnalyzer) super.clone();
        } catch (CloneNotSupportedException cnfe) {
            // doesn't happen
            return null;
        }
    }

}
