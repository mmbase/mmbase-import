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
 * @version $Id: LoggerWriter.java,v 1.1 2009-04-24 16:28:39 michiel Exp $
 * @since   MMBase-1.9.1
 */

public class LoggerWriter extends  Writer {

    protected final  Logger logger;

    private final StringBuilder buffer = new StringBuilder();
    private final Level level;

    public LoggerWriter(Logger log, Level lev) {
        logger = log;
        level = lev;
    }


    public void write(char[] buf, int start, int end) throws IOException {
        buffer.append(buf, start, end);
        flush();
    }


    public void flush() throws IOException {
        String[] lines = buffer.toString().split("[\\n\\r]");
        int used = 0;
        for (int i = 0 ; i < lines.length - 1; i++) {
            Logging.log(level, logger, lines[i]);
            used += lines[i].length();
            used ++;
        }
        buffer.delete(0, used);
    }
    public void close() throws IOException {
        flush();
        Logging.log(level, logger, buffer.toString());
    }
}
