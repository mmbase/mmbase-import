package org.mmbase.applications.crontab.modules;

import org.mmbase.module.*;
import org.mmbase.applications.crontab.*;
import java.util.*;

import org.mmbase.util.logging.*;


/**
 * Starts a crontab for MMBase.
 *
 * @author Michiel Meeuwissen
 */
public class JCrontabModule extends ReloadableModule {
    private static final Logger log = Logging.getLoggerInstance(JCrontabModule.class);
    protected JCronDaemon jCronDaemon = null;
    

    private Set myEntries = new HashSet();

    public JCrontabModule() {
        jCronDaemon = JCronDaemon.getInstance();
    }

    public void init() {
        Map params = getInitParameters();
        Iterator i = getInitParameters().entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String value = (String) entry.getValue();
            StringTokenizer tokens = new StringTokenizer(value, "\n|");
            String times;
            if (tokens.hasMoreTokens()) {
                times = tokens.nextToken().trim();
            } else {
                log.error("No times in " + value);
                continue;
            }
            String className;
            if (tokens.hasMoreTokens()) {
                className = tokens.nextToken().trim();
            } else {
                log.error("No className  " + value);
                continue;
            }
            String description;
            if (tokens.hasMoreTokens()) {
                description = className;
                className = tokens.nextToken().trim();
            } else {
                description = times;
            }
            try {
                JCronEntry job = new JCronEntry((String) entry.getKey(), times, description, className);
                myEntries.add(job);
                jCronDaemon.add(job);
            } catch (Exception e) {
                log.error("Could not add to JCronDaemon " + entry.getKey() + "|" + times + "|" + description + "|" + className + " " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public void reload() {
        super.reload();
        log.info("Reloading crontab");
        Iterator i = myEntries.iterator();
        while (i.hasNext()) {
            jCronDaemon.remove((JCronEntry) i.next());
        }
        myEntries.clear();
        init();
    }
    
}
