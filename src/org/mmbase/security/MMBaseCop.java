package org.mmbase.security;

import java.io.File;

import org.mmbase.util.XMLBasicReader;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class is the main class of the security system. It loads the authentication
 *  and authorization classes if needed, and they can be requested from this manager.
 */
public class MMBaseCop extends java.lang.SecurityManager  {
    private static Logger log=Logging.getLoggerInstance(MMBaseCop.class.getName());

    /** our current authentication class */
    private Authentication authentication;

    /** our current authorization class */
    private Authorization authorization;

    /** if the securitymanager is configured to functionate */
    private boolean active = false;

	/** the shared secret used by this system */
    private String sharedSecret = null;

    /**
     *	The constructor, will load the classes for authorization and authentication
     *	with their config files, as specied in the xml from configUrl
     *	@exception  java.io.IOException When reading the file failed
     *	@exception  java.lang.NoSuchMethodException When a tag was not specified
     *	@exception  org.mmbase.security.SecurityException When the class could not
     *	    be loaded
     */
    public MMBaseCop(String configPath) throws java.io.IOException, java.lang.NoSuchMethodException, SecurityException {
        super();

        File configFile = new File(configPath);
        log.debug("using: '" + configPath + "' as config file for security");
    	XMLBasicReader reader = new XMLBasicReader(configPath);

      	// are we active ?
      	String sActive = reader.getElementAttributeValue(reader.getElementByPath("security"),"active");
      	if(sActive.equalsIgnoreCase("true")) {
	    log.debug("SecurityManager will be active");
	    active = true;
	}
      	else if(sActive.equalsIgnoreCase("false")) {
	    log.debug("SecurityManager will NOT be active");
	    active = false;
	}
      	else {
	    log.error("security attibure active must have value of true or false("+configPath+")");
	    throw new SecurityException("security attibure active must have value of true or false");
	}

      	if(active) {
    	    // load our authentication...
    	    String authClass = reader.getElementAttributeValue(reader.getElementByPath("security.authentication"),"class");
      	    if(authClass == null) {
	    	log.error("attribute class could not be found in authentication("+configPath+")");
    	    	throw new java.util.NoSuchElementException("class in authentication");
	    }
      	    String authUrl = reader.getElementAttributeValue(reader.getElementByPath("security.authentication"),"url");
      	    if(authUrl == null) {
	    	log.error("attribute url could not be found in authentication("+configPath+")");
	    	throw new java.util.NoSuchElementException("url in authentication");
	    }
	    // make the url absolute in case it isn't:
            File authFile = new File(authUrl);
            if (! authFile.isAbsolute()) { // so relative to currently
            	// being parsed file. make it absolute, 
            	log.debug("authentication file was not absolutely given (" + authUrl + ")");
            	authFile = new File(configFile.getParent() + File.separator + authUrl);
            	log.debug("will use: " + authFile.getAbsolutePath());            
            }
      	    loadAuthentication(authClass, authFile.getAbsolutePath());

      	    // load our authorization...
      	    String auteClass = reader.getElementAttributeValue(reader.getElementByPath("security.authorization"),"class");
      	    if(auteClass == null) {
	    	log.error("attribute class could not be found in auhotization("+configPath+")");
	    	throw new java.util.NoSuchElementException("class in authorization");
	    }
      	    String auteUrl = reader.getElementAttributeValue(reader.getElementByPath("security.authorization"),"url");
      	    if(auteUrl == null) {
	    	log.error("attribute url could not be found in auhotization("+configPath+")");
	    	throw new java.util.NoSuchElementException("url in authorization");
	    }
            // make the url absolute in case it isn't:
            File auteFile = new File(auteUrl); 
            if (! auteFile.isAbsolute()) { // so relative to currently
            	// being parsed file. make it absolute, 
            	log.debug("authorization file was not absolutely given (" + auteUrl + ")");
            	auteFile = new File(configFile.getParent() + File.separator + auteUrl);
            	log.debug("will use: " + auteFile.getAbsolutePath());            
            }
      	    loadAuthorization(auteClass, auteFile.getAbsolutePath());
	}
	else {
    	    authentication = new NoAuthentication();
	    authentication.load(this, null);
	    authorization = new NoAuthorization();
	    authorization.load(this, null);	    
	}
        // load the sharedSecret
      	String sharedSecret = reader.getElementValue(reader.getElementByPath("security.sharedsecret"));
      	if(sharedSecret == null) {
	    log.error("sharedsecret could not be found in security("+configPath+")");
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
    public boolean checkSharedSecret(String key) {
        if (sharedSecret!=null) {
            if(sharedSecret.equals(key)) {
                return true;
            } else {
                log.error("the shared "+sharedSecret+"!="+key+" secrets don't match.");
                return false;
            }
        } else {
            return(false);
        }
    }

    /**
     * get the shared Secret
     * @return the shared Secret
     */
    public String getSharedSecret() {
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
