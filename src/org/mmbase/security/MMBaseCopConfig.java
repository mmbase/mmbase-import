/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.ResourceWatcher;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;

/**
 *  This class is the main class of the security system. It loads the authentication
 *  and authorization classes if needed, and they can be requested from this manager.
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: MMBaseCopConfig.java,v 1.25 2005-10-12 19:06:42 michiel Exp $
 */
public class MMBaseCopConfig {
    private static final Logger log = Logging.getLoggerInstance(MMBaseCopConfig.class);

    public static final ResourceLoader securityLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("security");

    /** looks if the files have been changed */
    protected ResourceWatcher watcher;

    /** our current authentication class */
    private Authentication authentication;

    /** our current authorization class */
    private Authorization authorization;

    /** if the securitymanager is configured to functionate */
    private boolean active = false;

    /** the shared secret used by this system */
    private String sharedSecret = null;


    /** the shared secret used by this system */
    private MMBaseCop cop;

    /** the class that watches if we have to reload...*/
    private class SecurityConfigWatcher extends ResourceWatcher  { 
        private MMBaseCop cop;
        
        public SecurityConfigWatcher(MMBaseCop cop) {
            super(securityLoader); 
            if(cop == null) throw new RuntimeException("MMBase cop was null");
            // log.debug("Starting the file watcher");
            this.cop = cop;
        }
        
        public void onChange(String s) {
            try {
                cop.reload();
            } catch(Exception e) {
                log.error(e);
                log.error(Logging.stackTrace(e));
            }
        }
    }

    /** Public ID of the Builder DTD version 1.0 */
    public static final String  PUBLIC_ID_SECURITY_1_0 = "-//MMBase//DTD security config 1.0//EN";
    private static final String PUBLIC_ID_SECURITY_1_0_FAULT = "//MMBase - security//";

    /** DTD resource filename of the Builder DTD version 1.0 */
    public static final String DTD_SECURITY_1_0 = "security_1_0.dtd";

    static {
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_SECURITY_1_0, DTD_SECURITY_1_0, MMBaseCopConfig.class);
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_SECURITY_1_0_FAULT, DTD_SECURITY_1_0, MMBaseCopConfig.class);
    }
    
    /**
     * The constructor, will load the classes for authorization and authentication
     * with their config files, as specied in the xml from configUrl
     * @exception  java.io.IOException When reading the file failed
     * @exception   .. When XML not validates.
     * @exception  org.mmbase.security.SecurityException When the class could not  be loaded
     *	   
     *  @param mmbaseCop  The MMBaseCop for which this is a configurator
     */
    MMBaseCopConfig(MMBaseCop mmbaseCop) throws java.io.IOException, NoSuchMethodException, SecurityException {

        java.net.URL config = securityLoader.getResource("security.xml");
        log.info("using: '" + config + "' as configuration file for security");
        
        watcher = new SecurityConfigWatcher(mmbaseCop);
        watcher.add("security.xml");
        watcher.start();

        cop = mmbaseCop;


    }

    /**
     * @since MMBase-1.8
     */
    void load() throws java.io.IOException {
        DocumentReader reader = new DocumentReader(securityLoader.getInputSource("security.xml"), this.getClass());

        // are we active ?
        String sActive = reader.getElementAttributeValue(reader.getElementByPath("security"),"active");
        if(sActive.equalsIgnoreCase("true")) {
            log.debug("SecurityManager will be active");
            active = true;
        } else if(sActive.equalsIgnoreCase("false")) {
            log.debug("SecurityManager will NOT be active");
            active = false;
        } else {
            throw new SecurityException("security attribute 'active' must have the value 'true' or 'false'");
        }

        // load the sharedSecret
        sharedSecret = reader.getElementValue(reader.getElementByPath("security.sharedsecret"));

        if(active) {
            
            // first instantiate authentication and authorization, during load they can check each others class then.

            org.w3c.dom.Element entry = reader.getElementByPath("security.authentication");
            String authenticationClass = reader.getElementAttributeValue(entry,"class");
            String authenticationUrl = reader.getElementAttributeValue(entry, "url");
            authentication = getAuthentication(authenticationClass);


            entry = reader.getElementByPath("security.authorization");
            String authorizationClass = reader.getElementAttributeValue(entry,"class");
            String authorizationUrl = reader.getElementAttributeValue(entry,"url");
            authorization = getAuthorization(authorizationClass);


            if (log.isDebugEnabled()) {
                log.debug("Loading class:" + authentication.getClass().getName() + " with config:" + authenticationUrl + " for Authentication");
            }           
            authentication.load(cop, watcher, authenticationUrl);

            if (log.isDebugEnabled()) {
                log.debug("Using class:" + authorization.getClass().getName() + " with config:" + authorizationUrl + " for Authorization");
            }
            authorization.load(cop, watcher,  authorizationUrl);


        } else {
            // we dont use security...
            authentication = new NoAuthentication();
            authentication.load(cop, watcher, null);
            authorization = new NoAuthorization();
            authorization.load(cop, watcher, null);
            log.debug("Retrieved dummy security classes");
        }
    }

    /**
     *	Returns the authentication class, which should be used.
     *	@return The authentication class which should be used.
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     *	Returns the authorization class, which should be used.
     *	@return The authorization class which should be used.
     */
    public Authorization getAuthorization() {
        return authorization;
    }

    /**
     *	Returns the authorization class, which should be used(for optimizations)
     *	@return <code>true</code>When the SecurityManager should
     *	    be used.
     *	    	<code>false</code>When not.
     */
    public boolean getActive() {
        return active;
    }

    /**
     * checks if the received shared secret is equals to your own shared secret
     * @param received shared secret
     * @return true if received shared secret equals your own shared secret
     * @return false if received shared secret not equals your own shared secret
     */
    public boolean checkSharedSecret(String received) {
        if (sharedSecret != null) {
            if(sharedSecret.equals(received)) {
                return true;
            } else {
                log.error("the shared " + sharedSecret + "!=" + received + " secrets don't match.");
            }
        }
        return false;
    }


    /**
     * get the shared Secret
     * @return the shared Secret
     */
    public String getSharedSecret() {
        return sharedSecret;
    }

    private Authentication getAuthentication(String className) throws SecurityException {
        Authentication result;
        try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            result = (Authentication) o;
            log.info("Setting manager of " + result + " to " + cop);
            result.manager = cop;
        } catch(ClassNotFoundException cnfe) {
            throw new SecurityException(cnfe);
        } catch(IllegalAccessException iae) {
            throw new SecurityException(iae);
        } catch(InstantiationException ie) {
            throw new SecurityException(ie);
        }
        return result;
    }

    private Authorization getAuthorization(String className) throws SecurityException {

        Authorization result;
        try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            result = (Authorization) o;            
            log.info("Setting manager of " + result + " to " + cop);
            result.manager = cop;
        }
        catch(java.lang.ClassNotFoundException cnfe) {
            log.debug("", cnfe);
            throw new SecurityException(cnfe.toString());
        }
        catch(java.lang.IllegalAccessException iae) {
            log.debug("", iae);
            throw new SecurityException(iae.toString());
        }
        catch(java.lang.InstantiationException ie) {
            log.debug("", ie);
            throw new SecurityException(ie.toString());
        }
        return result;
    }
}
