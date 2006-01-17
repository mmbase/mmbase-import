/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab.modules;

import java.util.*;
import org.mmbase.util.xml.UtilReader;
import org.mmbase.applications.crontab.*;
import org.mmbase.module.WatchedReloadableModule;
import org.mmbase.util.logging.*;

/**
 * Starts a crontab for MMBase as a Module.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CrontabModule.java,v 1.4 2006-01-17 15:14:26 michiel Exp $
 */
public class CrontabModule extends WatchedReloadableModule {
	
    private static final Logger log = Logging.getLoggerInstance(CrontabModule.class);
    protected CronDaemon cronDaemon = null;

    /** 
     * Need to remember which crontab entries where 'mine', to known which must be removed if
     * configuration changes.
     */
    private Set myEntries = new LinkedHashSet();

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
        Iterator i = getInitParameters().entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry)i.next();
            addJob(entry);
        }
        readMoreJobs();
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
        
        try {
            CronEntry job = new CronEntry((String)entry.getKey(), times, description, className, configString, type);
            log.service("Found job: " + job);
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

}
