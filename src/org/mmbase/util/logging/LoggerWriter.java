/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;

import java.io.*;
import java.util.*;

/**
 * A Writer that logs every line to a certain logger.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.1
 */

public class LoggerWriter extends  Writer {

    protected final  Logger logger;

    private final StringBuilder buffer = new StringBuilder();
    private final Level level;

    /**
     * @param log The logger to which this Writer must write everythin
     * @param lev On which level this must happen. If you want to log on different levels, then
     * override {@link #getLevel(String)}
     */
    public LoggerWriter(Logger log, Level lev) {
        logger = log;
        level = lev;
    }

    protected Level getLevel(String line) {
        return level;
    }

    protected void logLine(String line) {
        Level l = getLevel(line);
        if (l == null) l = level;
        Logging.log(l, logger, line);
    }


    @Override
    public void write(char[] buf, int start, int end) throws IOException {
        buffer.append(buf, start, end);
        flush();
    }

    @Override
    public void flush() throws IOException {
        String[] lines = buffer.toString().split("[\\n\\r]");
        int used = 0;
        for (int i = 0 ; i < lines.length - 1; i++) {
            logLine(lines[i]);
            used += lines[i].length();
            used ++;
        }
        buffer.delete(0, used);
    }
    @Override
    public void close() throws IOException {
        flush();
        logLine(buffer.toString());
    }
}
