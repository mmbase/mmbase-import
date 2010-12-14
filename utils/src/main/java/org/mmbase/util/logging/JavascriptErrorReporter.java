/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.logging;
import java.util.*;
import org.mozilla.javascript.*;

/**
 * Straight forward implementation of org.mozilla.javascript.ErrorReporter based on MMBase logging.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.6
 */
public class JavascriptErrorReporter implements ErrorReporter {

    private final Logger log;
    public JavascriptErrorReporter(Logger log) {
        this.log = log;
    }

    @Override
    public void warning(String message, String sourceName,
                        int line, String lineSource, int lineOffset) {
        if (line < 0) {
            log.warn(sourceName + " " + message);
        } else {
            log.warn(sourceName + " " + line + ':' + lineOffset + ':' + message);
        }
    }

    @Override
    public void error(String message, String sourceName,
                      int line, String lineSource, int lineOffset) {
        if (line < 0) {
            log.error(sourceName + " " + message);
        } else {
            log.error(sourceName + " " + line + ':' + lineOffset + ':' + message);
        }
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName,
                                           int line, String lineSource, int lineOffset) {
        error(message, sourceName, line, lineSource, lineOffset);
        return new EvaluatorException(message);
    }
}

