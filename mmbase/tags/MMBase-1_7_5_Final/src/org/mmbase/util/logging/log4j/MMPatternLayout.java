package org.mmbase.util.logging.log4j;

import org.apache.log4j.*;
import org.apache.log4j.helpers.PatternParser;

/**
 * @see MMPatternParser
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: MMPatternLayout.java,v 1.2 2002-10-25 14:06:11 michiel Exp $
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
