/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;

import java.io.*;

/**
 * A Writer that logs every line to a certain logger.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.1
 */

public class LoggerWriter extends  Writer {

    private static final Logger LOG = Logging.getLoggerInstance(LoggerWriter.class);

    protected final  Logger logger;

    private final StringBuilder buffer = new StringBuilder();
    private final Level level;
    protected String prefix = "";
    private long count = 0;

    /**
     * @param log The logger to which this Writer must write everythin
     * @param lev On which level this must happen. If you want to log on different levels, then
     * override {@link #getLevel(String)}
     */
    public LoggerWriter(Logger log, Level lev) {
        logger = log;
        level = lev;
    }

    /**
     * @since MMBase-1.9.2
     */
    public LoggerWriter(Logger log, Level lev, String p) {
        this(log, lev);
        prefix = p;
    }

    protected Level getLevel(String line) {
        return level;
    }

    /**
     * @since MMBase-1.9.2
     */
    protected String getPrefix() {
        return prefix;
    }

    protected void logLine(String line) {
        Level l = getLevel(line);
        if (l == null) l = level;
        Logging.log(l, logger, getPrefix() + line);
        count++;
    }

    /**
     * @since MMBase-1.9.2
     */
    public long getCount() {
        return count;
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
        if (buffer.length() > 0) {
            logLine(buffer.toString());
        }
    }
}
