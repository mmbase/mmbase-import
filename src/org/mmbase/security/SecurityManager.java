package org.mmbase.security;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class is the main class of the security system. It loads the authentication
 *  and authorization classes if needed, and they can be requested from this manager.
 */
public final class SecurityManager {
    private static Logger log=Logging.getLoggerInstance(SecurityManager.class.getName());

    /** our current authentication class */
    private Authentication authentication;

    /** our current authorization class */
    private Authorization authorization;

    /** if the securitymanager is configured to functionate */
    private boolean active = false;

	/** the shared secret used by this system */
    private static String sharedSecret;

    /**
     *	The constructor, will load the classes for authorization and authentication
     *	with their config files, as specied in the xml from configUrl
     *	@exception  java.io.IOException When reading the file failed
     *	@exception  java.lang.NoSuchMethodException When a tag was not specified
     *	@exception  org.mmbase.security.SecurityException When the class could not
     *	    be loaded
     */
    public SecurityManager(String configUrl)
      throws java.io.IOException, java.lang.NoSuchMethodException, SecurityException
    {
    	SecurityXmlReader reader = new SecurityXmlReader(configUrl);

      	// are we active ?
      	String sActive = reader.getAttribute("/security", "active");
      	if(sActive.equalsIgnoreCase("true")) {
	    log.debug("SecurityManager will be active");
	    active = true;
	}
      	else if(sActive.equalsIgnoreCase("false")) {
	    log.debug("SecurityManager will NOT be active");
	    active = false;
	}
      	else {
	    log.error("security attibure active must have value of true or false("+configUrl+")");
	    throw new SecurityException("security attibure active must have value of true or false");
	}

      	// load our authentication...
      	String authClass = reader.getAttribute("/security/authentication", "class");
      	if(authClass == null) {
	    log.error("attribute class could not be found in authentication("+configUrl+")");
    	    throw new java.util.NoSuchElementException("class in authentication");
	}
      	String authUrl = reader.getAttribute("/security/authentication", "url");
      	if(authUrl == null) {
	    log.error("attribute url could not be found in authentication("+configUrl+")");
	    throw new java.util.NoSuchElementException("url in authentication");
	}
      	loadAuthentication(authClass, authUrl);

      	// load our authorization...
      	String auteClass = reader.getAttribute("/security/authorization", "class");
      	if(auteClass == null) {
	    log.error("attribute class could not be found in auhotization("+configUrl+")");
	    throw new java.util.NoSuchElementException("class in authorization");
	}
      	String auteUrl = reader.getAttribute("/security/authorization", "url");
      	if(auteUrl == null) {
	    log.error("attribute url could not be found in auhotization("+configUrl+")");
	    throw new java.util.NoSuchElementException("url in authorization");
	}
      	loadAuthorization(auteClass, auteUrl);

        // load the sharedSecret
      	sharedSecret = reader.getValue("/security/sharedsecret");
      	if(sharedSecret == null) {
	    log.error("sharedsecret could not be found in security("+configUrl+")");
	    throw new java.util.NoSuchElementException("sharedsecret in security");
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
     * @param receive shared secret
     * @return true if received shared secret equals your own shared secret
     * @return false if received shared secret not equals your own shared secret
     */
    public static boolean checkSharedSecret(String key) {
        if(sharedSecret.equals(key)) {
            return true;
        } else {
            log.error("the shared "+sharedSecret+"!="+key+" secrets don't match.");
            return false;
        }
    }

    /**
     * get the shared Secret
     * @return the shared Secret
     */
    public static String getSharedSecret() {
        return sharedSecret;
    }

    private void loadAuthentication(String className, String configUrl) throws SecurityException {
    	log.debug("Using class:"+className+" with config:"+configUrl+" for Authentication");
      	try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            authentication = (Authentication) o;
            authentication.load(this, configUrl);
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
      	log.debug("Authentication loaded");
    }

    private void loadAuthorization(String className, String configUrl) throws SecurityException {
    	log.debug("Using class:"+className+" with config:"+configUrl+" for Authorization");
      	try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            authorization = (Authorization) o;
            authorization.load(this, configUrl);
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
      	log.debug("Authorization loaded");
    }
}
