package org.mmbase.applications.crontab;

/**
 * A straight-forward abstract implementation of JCronJob. If you exend it, you only need to
 * implement run(), and the 'jCronEntry' protected member var is available. 
 * 
 * @author Michiel Meeuwissen
 */


public abstract class BasicCronJob implements JCronJob {

    protected JCronEntry jCronEntry;

    /**
     * {@inheritDoc}
     * Only stores the JCronEntry in protected member jCronEntry
     */
    public void init(JCronEntry j) {
        jCronEntry = j;
        init();
    }

    /**
     * You can init by overriding this too (no need to call super.init)
     */
    protected void init() {
    }

    /**
     * Empty implementation (probably that's what you want)
     */
    public void stop() {
    }

    /**
     * Implement this.
     */
    public abstract void run();
}
