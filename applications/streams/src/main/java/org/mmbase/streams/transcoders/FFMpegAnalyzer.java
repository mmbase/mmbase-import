/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import java.util.regex.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;



/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class FFMpegAnalyzer implements Analyzer {


    private static final Logger log = Logging.getLoggerInstance(FFMpegAnalyzer.class);


    public int getMaxLines() {
        return 100;
    }

    public void analyze(String l, Node source, Node des) {
        Cloud cloud = source.getCloud();
        if (AnalyzerUtils.duration(l, source, des)) {
            log.info("Found length " + source);
            return;
        }
        if (AnalyzerUtils.video(l, source, des)) {
            log.info("Found video " + source);
            return;
        }
    }

    public void ready(Node sourceNode, Node destNode) {
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
