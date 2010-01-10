/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class is the main class of the security system. It loads the authentication
 * and authorization classes if needed, and they can be requested from this manager.
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id$
 */
public class MMBaseCop extends SecurityManager  {
    private static final Logger log = Logging.getLoggerInstance(MMBaseCop.class);

    /** 
     * The configuration used by our system 
     */
    private MMBaseCopConfig config;

    /**
     * The constructor, will load the classes for authorization and authentication
     * with their config files, as specied in the xml from configUrl
     * @throws  java.io.IOException When reading the file failed
     * @throws  java.lang.NoSuchMethodException When a tag was not specified
     * @throws  org.mmbase.security.SecurityException When the class could not  be loaded
     */
    public MMBaseCop() throws java.io.IOException, NoSuchMethodException, SecurityException {
        super();
        config = new MMBaseCopConfig(this);
        config.load();
        log.service("Done loading security configuration");
    }


    /**
     *	reload, will load the classes for authorization and authentication
     *	with their config files, as specied in the xml from configUrl
     *	@throws  java.io.IOException When reading the file failed
     *	@throws  java.lang.NoSuchMethodException When a tag was not specified
     *	@throws  org.mmbase.security.SecurityException When the class could not  be loaded
     */
    public void reload() throws java.io.IOException, NoSuchMethodException, SecurityException {
        log.debug("Retrieving a new security configuration...");
        MMBaseCopConfig newConfig = new MMBaseCopConfig(this);
        newConfig.load();
        // if no exception happed, the configuration can be replaced
        config.watcher.clear();
        config = newConfig;
        log.info("Done changing security configuration");
    }

    private final MMBaseCopConfig getConfig() {
        if (config == null) throw new RuntimeException("No MMBaseCopConfig in MMBaseCop!!");
        return config;
    }

    /**
     *	Returns the authentication class, which should be used.
     *	@return The authentication class which should be used.
     */
    public Authentication getAuthentication() {
        return getConfig().getAuthentication();
    }

    /**
     *	Returns the authorization class, which should be used.
     *	@return The authorization class which should be used.
     */
    public Authorization getAuthorization() {
        return getConfig().getAuthorization();
    }

    /**
     *	Returns the authorization class, which should be used(for optimizations)
     *	@return <code>true</code>When the SecurityManager should
     *	    be used.
     *	    	<code>false</code>When not.
     */
    public boolean getActive() {
        return getConfig().getActive();
    }

    /**
     * checks if the received shared secret is equals to your own shared secret
     * @param received  shared secret
     * @return true if received shared secret equals your own shared secret
     * @return false if received shared secret not equals your own shared secret
     */
    public boolean checkSharedSecret(String received) {
        return getConfig().checkSharedSecret(received);
    }

    /**
     * get the shared Secret
     * @return the shared Secret
     */
    public String getSharedSecret() {
        return getConfig().getSharedSecret();
    }
}
