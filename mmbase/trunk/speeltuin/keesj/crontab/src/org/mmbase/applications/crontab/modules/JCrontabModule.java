package org.mmbase.applications.crontab.modules;

import org.mmbase.module.*;
import org.mmbase.applications.crontab.*;
import java.util.*;

import org.mmbase.util.logging.*;


/**
 * Starts a crontab for MMBase as a Module.
 *
 * @author Michiel Meeuwissen
 */
public class JCrontabModule extends ReloadableModule {
    private static final Logger log = Logging.getLoggerInstance(JCrontabModule.class);
    protected JCronDaemon jCronDaemon = null;
    
    /** 
     * Need to remember which crontab entries where 'mine', to known which must be removed if
     * configuration changes.
     */
    private Set myEntries = new HashSet();

    public JCrontabModule() {
        jCronDaemon = JCronDaemon.getInstance();
    }

    /**
     * Interpretates all initParameters as crontab entries. The key is not very important but must
     * be unique. The value are actually two or three values, separated by tabs newlines or '|',
     * whatever you like most.
     <pre>
      &lt;cron time&gt;
      [&lt;description&gt;] 
      &lt;class name of a JCronJob&gt;
      </pre>
     */
    public void init() {
        Map params = getInitParameters();
        Iterator i = getInitParameters().entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String value = (String) entry.getValue();
            StringTokenizer tokens = new StringTokenizer(value, "\t\n|");
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

    /**
     * All previously added entries are removed from the cron-daemon and the currently configured
     * ones are added (init is called).
     */
    public void reload() {
        log.info("Reloading crontab");
        Iterator i = myEntries.iterator();
        while (i.hasNext()) {
            jCronDaemon.remove((JCronEntry) i.next());
        }
        myEntries.clear();
        init();
    }
    
}
