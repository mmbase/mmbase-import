/*
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;
import java.lang.ref.*;

/**
 * A Logger that buffers in memory, and on request logs again to a certain other logger.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: WriterLogger.java,v 1.4 2009/03/16 15:17:02 michiel Exp $
 * @since   MMBase-1.9.1
 */

public class BufferedLogger extends AbstractSimpleImpl {

    private static final Logger LOG = Logging.getLoggerInstance(BufferedLogger.class);

    protected static List<WeakReference<BufferedLogger>> instances = new CopyOnWriteArrayList<WeakReference<BufferedLogger>>();

    static {
        org.mmbase.util.ThreadPools.scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    for (WeakReference<BufferedLogger> ref : instances) {
                        BufferedLogger buffer = ref.get();
                        if (buffer != null) {
                            int cleaned = buffer.cleanup();
                            LOG.debug("Cleaned up " + cleaned + " log entry from " + buffer);

                        }
                    }
                }
            },
            10,
            10, TimeUnit.SECONDS);
    }

    private final List<LogEntry> history = Collections.synchronizedList(new LinkedList<LogEntry>());

    private long maxAge = Long.MAX_VALUE;
    private int maxSize = Integer.MAX_VALUE;

    public BufferedLogger() {
        instances.add(new WeakReference<BufferedLogger>(this));
    }

    public void setMaxAge(long ma) {
        maxAge = ma;
    }
    public void setMaxSize(int ms) {
        maxSize = ms;
    }

    protected int cleanup() {
        int tot = 0;
        while (history.size() > maxSize) {
            history.remove(0);
            tot++;
        }
        long deleteStamp = System.currentTimeMillis() - maxAge;
        while (history.get(0).timeStamp.getTime() < deleteStamp) {
            history.remove(0);
            tot++;
        }
        return tot;
    }


    @Override
    protected void log(String s, Level level) {
        LOG.debug("buffering " + level + " " + s);
        history.add(new LogEntry(s, level));
        if (history.size() > maxSize) {
            history.remove(0);
        }
    }

    public void reLog(final Logger log, final boolean clear, final Date after) {
        synchronized(history) {
            Iterator<LogEntry> i = history.iterator();
            while(i.hasNext()) {
                LogEntry entry = i.next();
                if (clear) i.remove();
                if (entry.timeStamp.after(after)) {
                    LOG.debug("Relogging " + log);
                    Logging.log(entry.level, log, entry.line);
                } else {
                    LOG.debug("Not relogging " + log + " (too old)");
                }
            }
        }
    }
    /**
     * Utitliy function to 'relog' to a String.
     */
    public String getList(Level l) {
        StringWriter w = new StringWriter();
        reLog(new WriterLogger(w, l), false, new Date(0));
        return w.toString();
    }
    public String getDebugList() {
        return getList(Level.DEBUG);
    }


    protected class LogEntry {
        final Date timeStamp = new Date();
        final String line;
        final Level level;
        public LogEntry(String line, Level level) {
            this.level = level;
            this.line = line;
        }
    }




}
