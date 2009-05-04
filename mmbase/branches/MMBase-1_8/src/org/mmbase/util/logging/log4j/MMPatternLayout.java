/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging.log4j;

import org.apache.log4j.*;
import org.apache.log4j.helpers.PatternParser;

/**
 * @see MMPatternParser
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id$
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
