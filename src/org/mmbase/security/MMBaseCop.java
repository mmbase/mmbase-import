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
    
    private class MMBaseCopConfig {
    	/** our current authentication class */
    	Authentication authentication;

    	/** our current authorization class */
    	Authorization authorization;

    	/** if the securitymanager is configured to functionate */
    	boolean active = false;

	/** the shared secret used by this system */
    	String sharedSecret = null;    
    }

    /** the configuration used by our system */
    private MMBaseCopConfig config;
    
    /** the file from which the config is loaded..*/
    private File configFile;
    
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

        configFile = new File(configPath);
    	if (! configFile.isAbsolute()) { // so relative to currently
    	    // being parsed file. make it absolute, 
            log.debug("config file was not absolutely given (" + configPath + ")");
            configFile = new File(configFile.getParent() + File.separator + configPath);
            log.debug("will use: " + configFile.getAbsolutePath());
	}
        log.info("using: '" + configFile.getAbsolutePath() + "' as config file for security");	
	config = loadConfig();
        log.info("done loading security configuration");	
    }

    
    /**
     *	The constructor, will load the classes for authorization and authentication
     *	with their config files, as specied in the xml from configUrl
     *	@exception  java.io.IOException When reading the file failed
     *	@exception  java.lang.NoSuchMethodException When a tag was not specified
     *	@exception  org.mmbase.security.SecurityException When the class could not
     *	    be loaded
     */
    public void reload() throws java.io.IOException, java.lang.NoSuchMethodException, SecurityException {
        log.info("gonna retrieve a new security configuration...");
	MMBaseCopConfig freshConfig = loadConfig();
        log.info("gonna change the security configration now");
    	synchronized(this) {
    	    config = freshConfig;
    	}	
        log.info("done changing security configuration");
    }
    
    /**
     *	Returns the authentication class, which should be used.
     *	@return The authentication class which should be used.
     */
    public Authentication getAuthentication() {
    	synchronized(this) {
    	    return config.authentication;
    	}
    }

    /**
     *	Returns the authorization class, which should be used.
     *	@return The authorization class which should be used.
     */
    public Authorization getAuthorization() {
    	synchronized(this) {
    	    return config.authorization;
	}
    }

    /**
     *	Returns the authorization class, which should be used(for optimizations)
     *	@return <code>true</code>When the SecurityManager should
     *	    be used.
     *	    	<code>false</code>When not.
     */
    public boolean getActive() {
    	synchronized(this) {
    	    return config.active;
	}
    }

    /**
     * checks if the received shared secret is equals to your own shared secret
     * @param receive shared secret
     * @return true if received shared secret equals your own shared secret
     * @return false if received shared secret not equals your own shared secret
     */
    public boolean checkSharedSecret(String key) {
    	synchronized(this) {
            if (config.sharedSecret!=null) {
    	        if(config.sharedSecret.equals(key)) return true;
    	        else log.error("the shared "+config.sharedSecret+"!="+key+" secrets don't match.");
            }
    	    return false;
	}
    }

    /**
     * get the shared Secret
     * @return the shared Secret
     */
    public String getSharedSecret() {
    	synchronized(this) {
            return config.sharedSecret;
	}
    }
    
    private MMBaseCopConfig loadConfig() throws SecurityException {
    	String configPath = configFile.getAbsolutePath();
    	XMLBasicReader reader = new XMLBasicReader(configPath);
    	MMBaseCopConfig result = new MMBaseCopConfig();	

      	// are we active ?
      	String sActive = reader.getElementAttributeValue(reader.getElementByPath("security"),"active");
      	if(sActive.equalsIgnoreCase("true")) {
	    log.debug("SecurityManager will be active");
	    result.active = true;
	}
      	else if(sActive.equalsIgnoreCase("false")) {
	    log.debug("SecurityManager will NOT be active");
	    result.active = false;
	}
      	else {
	    log.error("security attibure active must have value of true or false("+configPath+")");
	    throw new SecurityException("security attibure active must have value of true or false");
	}

      	if(result.active) {
    	    // load our authentication...
    	    org.w3c.dom.Element entry = reader.getElementByPath("security.authentication");
	    if(entry == null) throw new java.util.NoSuchElementException("security/authentication");
    	    String authClass = reader.getElementAttributeValue(entry,"class");
      	    if(authClass == null) {
	    	log.error("attribute class could not be found in authentication("+configPath+")");
    	    	throw new java.util.NoSuchElementException("class in authentication");
	    }
      	    String authUrl = reader.getElementAttributeValue(entry,"url");
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
      	    result.authentication = getAuthentication(authClass, authFile.getAbsolutePath());
	    log.debug("Authentication retrieved");

      	    // load our authorization...
    	    entry = reader.getElementByPath("security.authorization");
	    if(entry == null) throw new java.util.NoSuchElementException("security.authorization");
      	    String auteClass = reader.getElementAttributeValue(entry,"class");
      	    if(auteClass == null) {
	    	log.error("attribute class could not be found in auhotization("+configPath+")");
	    	throw new java.util.NoSuchElementException("class in authorization");
	    }    	    
      	    String auteUrl = reader.getElementAttributeValue(entry,"url");
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
      	    result.authorization = getAuthorization(auteClass, auteFile.getAbsolutePath());
	    log.debug("Authorization retrieved");

	}
	else {
	    // we dont use security...
    	    result.authentication = new NoAuthentication();
	    result.authentication.load(this, null);
	    result.authorization = new NoAuthorization();
	    result.authorization.load(this, null);
	    log.debug("Retrieved dummy security classes");
	}
        // load the sharedSecret
      	result.sharedSecret = reader.getElementValue(reader.getElementByPath("security.sharedsecret"));
      	if(result.sharedSecret == null) {
	    log.error("sharedsecret could not be found in security("+configPath+")");
	} 
    	log.debug("Shared Secret retrieved");
	return result;
    }    
    
    private Authentication getAuthentication(String className, String configUrl) throws SecurityException {
    	log.debug("Using class:"+className+" with config:"+configUrl+" for Authentication");
	Authentication result;
      	try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            result = (Authentication) o;
            result.load(this, configUrl);
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

    private Authorization getAuthorization(String className, String configUrl) throws SecurityException {
    	log.debug("Using class:"+className+" with config:"+configUrl+" for Authorization");
	Authorization result;
      	try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            result = (Authorization) o;
            result.load(this, configUrl);
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
