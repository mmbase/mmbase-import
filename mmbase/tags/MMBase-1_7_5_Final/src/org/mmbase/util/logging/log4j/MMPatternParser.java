 package org.mmbase.util.logging.log4j;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Adds the  conversion pattern 'q' which returns a truncated level (from the _end_, not from the beginning as log4j itself would do) . To 3 chars. So it is like 'p'. Also add 'k' which give the currently memory in use (in kb).
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: MMPatternParser.java,v 1.6 2003-03-04 13:28:50 nico Exp $
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
        } else {
            super.finalizeConverter(c);
        }
    }
    
    private class TruncatedLevelPatternConverter extends PatternConverter {
        TruncatedLevelPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }
        
        public String convert(LoggingEvent event) {
            return event.getLevel().toString().substring(0, 3);
        }
    }  
    
    private class MemoryPatternConverter extends PatternConverter {        
        MemoryPatternConverter(FormattingInfo formattingInfo) {
            super(formattingInfo);
        }
        
        public String convert(LoggingEvent event) {
            Runtime rt = Runtime.getRuntime();
            return  "" + (rt.totalMemory() - rt.freeMemory()) / 1024;
        }
    }  
}
