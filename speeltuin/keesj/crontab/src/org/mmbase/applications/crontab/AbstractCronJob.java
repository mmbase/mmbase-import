/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

/**
 * A straight-forward abstract implementation of JCronJob. If you exend it, you only need to
 * implement run(), and the 'cronEntry' protected member var is available. 
 * 
 * @author Michiel Meeuwissen
 */

public abstract class AbstractCronJob implements CronJob {

    protected CronEntry cronEntry;

    /**
     * {@inheritDoc}
     * Only stores the JCronEntry in protected member jCronEntry
     */
    public void init(CronEntry cronEntry) {
        this.cronEntry = cronEntry;
        init();
    }

    /**
     * You can init by overriding this too (no need to call super.init)
     */
    protected void init() {}

    /**
     * Empty implementation (probably that's what you want)
     */
    public void stop() {}

    /**
     * Implement this.
     */
    public abstract void run();
}
