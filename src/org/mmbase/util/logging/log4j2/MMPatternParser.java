package org.mmbase.util.logging.log4j2;

import org.apache.log4j.*;
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Adds the  conversion pattern 'q' which returns a shorted level (to 3 chars) (so it is like 'p').
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: MMPatternParser.java,v 1.1 2002-04-25 15:25:15 michiel Exp $
 */
public class MMPatternParser extends PatternParser {


  public MMPatternParser(String pattern) {
    super(pattern);
  }
    
  public void finalizeConverter(char c) {
    if (c == 'q') {
      addConverter(new UserDirPatternConverter(formattingInfo));
      currentLiteral.setLength(0);
    } else {
      super.finalizeConverter(c);
    }
  }
  
  private class UserDirPatternConverter extends PatternConverter {
    UserDirPatternConverter(FormattingInfo formattingInfo) {
      super(formattingInfo);
    }

    public String convert(LoggingEvent event) {
        return event.level.toString().substring(0, 3);
    }
  }  
}
