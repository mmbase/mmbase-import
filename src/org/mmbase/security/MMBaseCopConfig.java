/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.io.File;

import org.mmbase.util.XMLBasicReader;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class is the main class of the security system. It loads the authentication
 *  and authorization classes if needed, and they can be requested from this manager.
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: MMBaseCopConfig.java,v 1.16 2004-04-02 10:38:33 michiel Exp $
 */
public class MMBaseCopConfig {
    private static final Logger log = Logging.getLoggerInstance(MMBaseCopConfig.class);

    /** the file from which the config is loaded..*/
    private File configFile;

    /** looks if the files have been changed */
    private SecurityFileWatcher watcher;

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
    private class SecurityFileWatcher extends org.mmbase.util.FileWatcher  { 
        //    	private final static Logger log=Logging.getLoggerInstance(SecurityFileWatcher.class.getName());
        private MMBaseCop cop;
        
        public SecurityFileWatcher(MMBaseCop cop) {
            super(true); // true: continue after change
            if(cop == null) throw new RuntimeException("MMBase cop was null");
            // log.debug("Starting the file watcher");
            this.cop = cop;
        }
        

        public void add(File file) {
            //log.info(Logging.stackTrace() + "Adding: " + file);
            super.add(file);
        }
        public void onChange(File file) {
            try {
                log.debug("Going to reload the MMBase-cop since the file '" + file.getAbsolutePath() + "' was changed.");
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

    public static void registerPublicIDs() {
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_SECURITY_1_0, DTD_SECURITY_1_0, MMBaseCopConfig.class);
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_SECURITY_1_0_FAULT, DTD_SECURITY_1_0, MMBaseCopConfig.class);
    }
    
    /**
     *	The constructor, will load the classes for authorization and authentication
     *	with their config files, as specied in the xml from configUrl
     *	@exception  java.io.IOException When reading the file failed
     *	@exception  java.lang.NoSuchMethodException When a tag was not specified
     *	@exception  org.mmbase.security.SecurityException When the class could not  be loaded
     *	   
     *  @param mmbaseCop  The MMBaseCop for which this is a configurator
     *  @param configFile Configuration file ('security.xml')
     */
    public MMBaseCopConfig(MMBaseCop mmbaseCop, File configFile) throws java.io.IOException, NoSuchMethodException, SecurityException {
        this.configFile = configFile;
        log.info("using: '" + configFile.getAbsolutePath() + "' as configuration file for security");

        watcher = new SecurityFileWatcher(mmbaseCop);

        cop = mmbaseCop;

        String configPath = configFile.getAbsolutePath();
        XMLBasicReader reader = new XMLBasicReader(configPath, this.getClass());

        // are we active ?
        String sActive = reader.getElementAttributeValue(reader.getElementByPath("security"),"active");
        if(sActive.equalsIgnoreCase("true")) {
            log.debug("SecurityManager will be active");
            active = true;
        } else if(sActive.equalsIgnoreCase("false")) {
            log.debug("SecurityManager will NOT be active");
            active = false;
        } else {
            throw new SecurityException("security attibute active must have the value 'true' or 'false'");
        }

        // load the sharedSecret
        sharedSecret = reader.getElementValue(reader.getElementByPath("security.sharedsecret"));
        if(sharedSecret == null) {
            String msg = "sharedsecret could not be found in security("+configPath+")";
            log.error(msg);
            throw new java.util.NoSuchElementException(msg);
        }
        log.debug("Shared Secret retrieved");

        if(active) {
            // load our authentication...
            org.w3c.dom.Element entry = reader.getElementByPath("security.authentication");
            if(entry == null) throw new java.util.NoSuchElementException("security/authentication");
            String authClass = reader.getElementAttributeValue(entry,"class");
            if(authClass == null) {
                String msg = "attribute class could not be found in authentication("+configPath+")";
                log.error(msg);
                throw new java.util.NoSuchElementException(msg);
            }
            String authUrl = reader.getElementAttributeValue(entry, "url");
            if(authUrl == null) {
                String msg = "attribute url could not be found in authentication(" + configPath + ")";
                log.error(msg);
                throw new java.util.NoSuchElementException(msg);
            }
            // make the url absolute in case it isn't:
            File authFile = null;
            if (! authUrl.equals("")) {
                authFile = new File(authUrl);
            }
            if (authFile != null  && (! authFile.isAbsolute())) { // so relative to currently
                // being parsed file. make it absolute,
                log.debug("authentication file was not absolutely given (" + authUrl + ")");
                authFile = new File(configFile.getParent() + File.separator + authUrl);
                log.debug("will use: " + authFile.getAbsolutePath());
            }
            authentication = getAuthentication(authClass, authFile != null ? authFile.getAbsolutePath() : null);
            log.debug("Authentication retrieved");

            // load our authorization...
            entry = reader.getElementByPath("security.authorization");
            if(entry == null) throw new java.util.NoSuchElementException("security.authorization");
            String auteClass = reader.getElementAttributeValue(entry,"class");
            if(auteClass == null) {
                String msg = "attribute class could not be found in auhotization("+configPath+")";
                log.error(msg);
                throw new java.util.NoSuchElementException(msg);
            }
            String auteUrl = reader.getElementAttributeValue(entry,"url");
            if(auteUrl == null) {
                String msg ="attribute url could not be found in auhotization("+configPath+")";
                log.error(msg);
                throw new java.util.NoSuchElementException(msg);
            }
            // make the url absolute in case it isn't:
            File auteFile = null;

            if (! auteUrl.equals("")) {
                auteFile = new File(auteUrl);
            }
            if (auteFile != null && (! auteFile.isAbsolute())) { // so relative to currently
                // being parsed file. make it absolute,
                log.debug("authorization file was not absolutely given (" + auteUrl + ")");
                auteFile = new File(configFile.getParent() + File.separator + auteUrl);
                log.debug("will use: " + auteFile.getAbsolutePath());
            }
            authorization = getAuthorization(auteClass, auteFile != null ? auteFile.getAbsolutePath() : null);
            log.debug("Authorization retrieved");

            // add our config file..
            watcher.add(configFile);
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
     * stops tracking changes on the files...
     */
    public void stopWatching() {
        watcher.exit();
    }


    /**
     * starts tracking changes on the files...
     */
    public void startWatching() {
        watcher.start();
    }

    /**
     * get the shared Secret
     * @return the shared Secret
     */
    public String getSharedSecret() {
        return sharedSecret;
    }

    private Authentication getAuthentication(String className, String configUrl) throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Using class:" + className + " with config:" + configUrl + " for Authentication");
        }
        Authentication result;
        try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            result = (Authentication) o;
            result.load(cop, watcher, configUrl);
        } catch(ClassNotFoundException cnfe) {
            throw new SecurityException(cnfe);
        } catch(IllegalAccessException iae) {
            throw new SecurityException(iae);
        } catch(InstantiationException ie) {
            throw new SecurityException(ie);
        }
        return result;
    }

    private Authorization getAuthorization(String className, String configUrl) throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("Using class:" + className + " with config:" + configUrl + " for Authorization");
        }
        Authorization result;
        try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            result = (Authorization) o;
            result.load(cop, watcher, configUrl);
        }
        catch(java.lang.ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            throw new SecurityException(cnfe.toString());
        }
        catch(java.lang.IllegalAccessException iae) {
            iae.printStackTrace();
            throw new SecurityException(iae.toString());
        }
        catch(java.lang.InstantiationException ie) {
            ie.printStackTrace();
            throw new SecurityException(ie.toString());
        }
        return result;
    }
}
