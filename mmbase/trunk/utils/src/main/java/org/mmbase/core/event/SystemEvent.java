/*
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative. The
 * license (Mozilla version 1.0) can be read at the MMBase site. See
 * http://www.MMBase.org/license
 */
package org.mmbase.core.event;

import java.io.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.9.3
 * @version $Id: TransactionEvent.java 41369 2010-03-15 20:54:45Z michiel $
 */
public abstract class SystemEvent extends Event {
    private static final Logger LOG = Logging.getLoggerInstance(SystemEvent.class);

    public SystemEvent() {
    }


    /**
     * A SystemEvent that is also Collectable will be collected by the EventManger and also issued to EventListeners which are added after the event
     * happened.
     */
    public static abstract class  Collectable extends SystemEvent {
    }

    /**
     * Notifies that the local MMBase is now fully up and running
     */
    public static class Up extends Collectable {
        private final String databaseName;
        private final File  dataDir;
        public Up(String databaseName, File dataDir) {
            this.databaseName = databaseName;
            this.dataDir = dataDir;
        }
        public String getDatabaseName() {
            return databaseName;
        }
        public File getDataDir() {
            return dataDir;
        }
    }

    /**
     * Notifies that the database is now usable
     */
    public static class DataSourceAvailable extends Collectable {
        private final javax.sql.DataSource dataSource;
        private final String prefix;
        public DataSourceAvailable(javax.sql.DataSource ds, String pref) {
            dataSource = ds;
            prefix = pref;
        }
        public javax.sql.DataSource getDataSource() {
            return dataSource;
        }
        public String getPrefix() {
            return prefix;
        }
        @Override
        public String toString() {
            return dataSource + " p:" + prefix;
        }
    }



    public static class ServletContext extends Collectable  {
        private final javax.servlet.ServletContext servletContext;
        public ServletContext(javax.servlet.ServletContext sc) {
            servletContext = sc;
        }
        public javax.servlet.ServletContext getServletContext() {
            return servletContext;
        }
    }

    public static class Shutdown extends Collectable {
    }

    /**
     * Notifies the first determination or change in the 'machinename'
     */
    public static class MachineName extends Collectable {
        private final String name;
        public MachineName(String n) {
            name = n;
        }
        public String getName() {
            return name;
        }
    }

    static {
        SystemEventListener logger = new SystemEventListener() {
                @Override
                public void notify(SystemEvent s) {
                    LOG.service(" Received " + s);
                }
                @Override
                public String toString() {
                    return "SystemEventLogger";
                }
            };
        EventManager.getInstance().addEventListener(logger);
    }

}
