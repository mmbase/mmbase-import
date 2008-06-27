package com.finalist.cmsc.logging;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.StringMatchFilter;

/**
 * This is a very simple filter based on string matching.
 * <p>
 * The filter admits two options <b>StringToMatch</b> and <b>AcceptOnMatch</b>.
 * If there is a match between the value of the StringToMatch option and the
 * message inside the stacktrace of the
 * {@link org.apache.log4j.spi.LoggingEvent}, then the
 * {@link #decide(LoggingEvent)} method returns
 * {@link org.apache.log4j.spi.Filter#ACCEPT} if the <b>AcceptOnMatch</b>
 * option value is true, if it is false then
 * {@link org.apache.log4j.spi.Filter#DENY} is returned. If there is no match,
 * {@link org.apache.log4j.spi.Filter#NEUTRAL} is returned. Based on
 * {@link org.apache.log4j.varia.StringMatchFilter} but uses a different decide
 * method.
 */
public class ExceptionStringMatchFilter extends StringMatchFilter {

   @Override
   public int decide(LoggingEvent event) {
      String[] reps = event.getThrowableStrRep();
      if (reps == null || getStringToMatch() == null) {
         return Filter.NEUTRAL;
      }

      // check if any of the stracktraces contains the message
      boolean isMatch = false;
      for (String str : reps) {
         if (str != null && str.indexOf(getStringToMatch()) != -1) {
            isMatch = true;
            break;
         }
      }
      if (!isMatch) {
         return Filter.NEUTRAL;
      }
      else { // we've got a match
         if (getAcceptOnMatch()) {
            return Filter.ACCEPT;
         }
         else {
            return Filter.DENY;
         }
      }
   }

}
