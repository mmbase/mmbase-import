/*
 This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 */
package org.mmbase.applications.crontab;

/**
 * A straight-forward abstract implementation of CronJob. If you exend it, you only need to
 * implement run(), and the 'cronEntry' protected member var is available.
 *
 * @author Michiel Meeuwissen
 */

public abstract class AbstractCronJob implements CronJob {

    protected  CronEntry cronEntry;
    /**
     * {@inheritDoc}
     *
     * Stores the CronEntry in protected member cronEntry. So extensions should override
     * {@link #init()} instead (in which they can use the cronEntry member).
     */
    public final void init(CronEntry cronEntry) {
        this.cronEntry = cronEntry;
        init();
    }

    /**
     * You can init by overriding this (no need to call super.init)
     * This is called by {@link #init(CronEntry)}
     */
    protected void init() {}

    /**
     * Empty implementation (probably that's what you want)
     */
    public void stop() {}

    public CronEntry getEntry() {
        return cronEntry;
    }

    /**
     * Implement this.
     */
    public abstract void run();
}
