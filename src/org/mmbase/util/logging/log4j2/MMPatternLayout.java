package org.mmbase.util.logging.log4j2;

import org.apache.log4j.*;
import org.apache.log4j.helpers.PatternParser;

/**
 * @see MMPatternParser
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: MMPatternLayout.java,v 1.1 2002-04-25 15:25:15 michiel Exp $
*/
public class MMPatternLayout extends PatternLayout {
  public MMPatternLayout() {
    this(DEFAULT_CONVERSION_PATTERN);
  }

  public MMPatternLayout(String pattern) {
    super(pattern);
  }
    
  public PatternParser createPatternParser(String pattern) {
    return new MMPatternParser(
      pattern == null ? DEFAULT_CONVERSION_PATTERN : pattern);
  }
  
}
