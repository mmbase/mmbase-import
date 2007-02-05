/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab.modules;

import java.util.*;
import java.util.regex.*;
import org.mmbase.util.xml.UtilReader;
import org.mmbase.util.functions.*;
import org.mmbase.applications.crontab.*;
import org.mmbase.module.WatchedReloadableModule;
import org.mmbase.util.logging.*;

/**
 * Starts a crontab for MMBase as a Module.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CrontabModule.java,v 1.10 2007-02-05 14:39:10 michiel Exp $
 */
public class CrontabModule extends WatchedReloadableModule {

    private static final Logger log = Logging.getLoggerInstance(CrontabModule.class);
    protected CronDaemon cronDaemon = null;

    /**
     * Need to remember which crontab entries where 'mine', to known which must be removed if
     * configuration changes.
     */
    private Set<CronEntry> myEntries = new LinkedHashSet<CronEntry>();

    public CrontabModule() {
        cronDaemon = CronDaemon.getInstance();
    }

    /**
     * Interpretates all initParameters as crontab entries. The key is not very important but must
     * be unique. The value are actually two or three or four values, separated by tabs newlines or '|',
     * whatever you like most.
     <pre>
      &lt;cron time&gt;
      &lt;class name of a CronJob&gt;
      [&lt;description&gt;]
      [&lt;configuration-string&gt;]
      </pre>
     */
    public void init() {
        for (Map.Entry entry : getInitParameters().entrySet()) {
            addJob(entry);
        }
        readMoreJobs();
    }

    protected void shutdown() {
        cronDaemon.stop();
    }

    protected void addJob(Map.Entry entry) {
        String value = (String)entry.getValue();
        String[] tokens = value.trim().split("[\n|]");
        String times;
        if (tokens.length > 0) {
            times = tokens[0].trim();
        } else {
            log.error("No times in " + value);
            return;
        }
        String className;
        if (tokens.length > 1) {
            className = tokens[1].trim();
        } else {
            log.error("No className  " + value);
            return;
        }
        String description = null;
        String configString = null;
        String type = null;
        Pattern servers = CronEntry.ALL;

        if (tokens.length > 2) {
            description = tokens[2].trim();
        }
        if (description == null || description.length() == 0) {
            description = (String)entry.getKey();
        }

        if (tokens.length > 3) {
            configString = tokens[3].trim();
        }
        if (tokens.length > 4) {
            type = tokens[4].trim();
        }

        if (tokens.length > 5) {
            servers = Pattern.compile(tokens[5].trim());
        }

        try {
            CronEntry job = new CronEntry((String)entry.getKey(), times, description, className, configString, type, servers);
            log.debug("Found job: " + job);
            myEntries.add(job);
            cronDaemon.add(job);
        } catch (Exception e) {
            log.error("Could not add to CronDaemon " + entry.getKey() + "|" + times + "|" + description + "|" + className + " " + e.getClass().getName() + ": " + e.getMessage());
        }
    }



    /**
     * All previously added entries are removed from the cron-daemon and the currently configured
     * ones are added (init is called).
     */
    public void reload() {
        log.info("Reloading crontab");
        Iterator i = myEntries.iterator();
        while (i.hasNext()) {
            cronDaemon.remove((CronEntry)i.next());
        }
        myEntries.clear();
        init();
    }

    /**
     * @since MMBase-1.8
     */
    private Map utilProperties = new UtilReader("crontab.xml", new Runnable() { public void run() { reload();}}).getProperties();

    public void readMoreJobs() {
        Iterator i = utilProperties.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            addJob(entry);
        }

    }
    /**
     * @since MMBase-1.8
     */
    protected Function<Set<CronEntry>> listFunction = new AbstractFunction<Set<CronEntry>>("list") {
            public Set<CronEntry> getFunctionValue(Parameters arguments) {
                return cronDaemon.getEntries();
            }

        };
    {
        addFunction(listFunction);
    }

    protected final static Parameter<String> ENTRY = new Parameter<String>("entry", String.class, true);
    protected final static Parameter<Integer> THREAD = new Parameter<Integer>("thread", Integer.class, new Integer(0));
    /**
     * @since MMBase-1.8
     */
    protected Function<Boolean> kickFunction = new AbstractFunction<Boolean>("kick", ENTRY) {
            public Boolean getFunctionValue(Parameters arguments) {
                String id = arguments.get(ENTRY);
                return cronDaemon.getCronEntry(id).kick();
            }

        };
    {
        addFunction(kickFunction);
    }

    /**
     * @since MMBase-1.8
     */
    protected Function<Boolean> interruptFunction = new AbstractFunction<Boolean>("interrupt", ENTRY, THREAD) {
            public Boolean getFunctionValue(Parameters arguments) {
                String id = arguments.get(ENTRY);
                Integer thread = arguments.get(THREAD);
                Interruptable t = cronDaemon.getCronEntry(id).getThread(thread.intValue());
                return t != null && t.interrupt();
            }

        };
    {
        addFunction(interruptFunction);
    }


    /**
     * @since MMBase-1.8
     */
    protected Function<Boolean> aliveFunction = new AbstractFunction<Boolean>("alive") {
            public Boolean getFunctionValue(Parameters arguments) {
                return cronDaemon.isAlive();
            }

        };
    {
        addFunction(aliveFunction);
    }

    /**
     * @since MMBase-1.8
     */
    protected Function<Boolean> stopFunction = new AbstractFunction<Boolean>("stop") {
            public Boolean getFunctionValue(Parameters arguments) {
                cronDaemon.stop();
                return cronDaemon.isAlive();
            }

        };
    {
        addFunction(stopFunction);
    }

    /**
     * @since MMBase-1.8
     */
    protected Function<Boolean> startFunction = new AbstractFunction<Boolean>("start") {
            public Boolean getFunctionValue(Parameters arguments) {
                cronDaemon.start();
                return cronDaemon.isAlive();
            }

        };
    {
        addFunction(startFunction);
    }

    /**
     * @since MMBase-1.8
     */
    protected Function<Boolean> reloadFunction = new AbstractFunction<Boolean>("reload") {
            public Boolean getFunctionValue(Parameters arguments) {
                reload();
                return cronDaemon.isAlive();
            }

        };
    {
        addFunction(reloadFunction);
    }


}
