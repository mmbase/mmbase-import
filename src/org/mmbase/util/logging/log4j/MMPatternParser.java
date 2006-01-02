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
 * Adds the  conversion pattern 'q' which returns a truncated level (from the _end_, not from the beginning as log4j itself would do) . To 3 chars. So it is like 'p'. Also add 'k' which give the currently memory in use (in kb).
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: MMPatternParser.java,v 1.8 2006-01-02 13:27:52 michiel Exp $
 */
public class MMPatternParser extends PatternParser {


    public MMPatternParser(String pattern) {
        super(pattern);
    }
    
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
}
