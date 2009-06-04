/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.log4j;

import org.mmbase.util.logging.Logging;
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Adds several conversion patterns to the ones supported natively by log4j.
 * See <a href="http://logging.apache.org/log4j/docs/api/org/apache/log4j/PatternLayout.html">log4j pattern layout</a>
 <table>
 <tr><th>Conversion Pattern</th><th>Effect</th></tr>
 <tr><td>%q</td><td>A truncated level (from the _end_, not from the beginning as log4j's %p itself would do) . To 3 chars.</td></tr>
 <tr><td>%k</td><td>Currently memory in use (in kb).</td></tr>
 <tr><td>%N</td><td>Machine Name of current MMBase (or 'localhost' if not set).</td></tr>
 </table>
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id$
 */
public class MMPatternParser extends PatternParser {


    public MMPatternParser(String pattern) {
        super(pattern);
    }

    @Override
    public void finalizeConverter(char c) {
        if (c == 'q') {
            addConverter(new TruncatedLevelPatternConverter(formattingInfo));
            currentLiteral.setLength(0);
        } else if (c == 'k') {
            addConverter(new MemoryPatternConverter(formattingInfo));
            currentLiteral.setLength(0);
        } else if (c == 'N') {
            addConverter(new MachineNamePatternConverter(formattingInfo));
            currentLiteral.setLength(0);
        } else if (c == 'T') {
            addConverter(new ThreadGroupPatternConverter(formattingInfo));
            currentLiteral.setLength(0);
        } else {
            super.finalizeConverter(c);
        }
    }

    private static class TruncatedLevelPatternConverter extends PatternConverter {
        TruncatedLevelPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }

        public String convert(LoggingEvent event) {
            return event.getLevel().toString().substring(0, 3);
        }
    }

    private static class MemoryPatternConverter extends PatternConverter {
        MemoryPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }

        public String convert(LoggingEvent event) {
            Runtime rt = Runtime.getRuntime();
            return  "" + (rt.totalMemory() - rt.freeMemory()) / 1024;
        }
    }
    /**
     * @since MMBase-1.8
     */
    private static class MachineNamePatternConverter extends PatternConverter {
        MachineNamePatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }

        public String convert(LoggingEvent event) {
            return  "" + Logging.getMachineName();
        }
    }
    /**
     * @since MMBase-1.9
     */
    private static class ThreadGroupPatternConverter extends PatternConverter {
        ThreadGroupPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }

        public String convert(LoggingEvent event) {
            return  "" + Thread.currentThread().getThreadGroup().getName();
        }
    }
}
