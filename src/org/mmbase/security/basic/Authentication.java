package org.mmbase.security.basic;

//import org.mmbase.security.UserContext;
import org.mmbase.util.ExtendedProperties;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * simple implemetation, to provide authentication from files...
 */
public class Authentication extends org.mmbase.security.Authentication {
    private static Logger log=Logging.getLoggerInstance(Authentication.class.getName());

    private String configFile = null;

    protected void load() {

        if (configUrl == null || configUrl.equals("")) {
            configFile = org.mmbase.module.core.MMBaseContext.getConfigPath() + java.io.File.separator + "accounts.properties";
        } else {
            configFile = configUrl;
        }

        log.debug("trying to load authentication with config file:"  + configUrl);        

        java.io.File file = new java.io.File(configFile);
        if ( !file.exists() ) {
            log.error("file: '"+configFile+"' did not exist.");
            throw new org.mmbase.security.SecurityException("file: '"+configUrl+"' did not exist.");
        }
        if ( !file.isFile() ) {
            log.error("file: '"+configFile+"' is not a file.");
            throw new org.mmbase.security.SecurityException("file: '"+configUrl+"' is not a file.");
        }
        if ( !file.canRead() ) {
            log.error("file: '"+configFile+"' is not readable.");
            throw new org.mmbase.security.SecurityException("file: '"+configUrl+"' is not readable.");
        }
        log.debug("authentication loaded");
    }


    public org.mmbase.security.UserContext login(String application, org.mmbase.security.UserContext userContext, Object[] parameters)
    	throws org.mmbase.security.SecurityException
    {        
        if (application.compareTo("name/password") != 0) {
            return new UserContext("");
        }
        ExtendedProperties reader = new ExtendedProperties();

        log.debug("reading accounts from " + configFile);
        java.util.Hashtable accounts = reader.readProperties(configFile);

        if (accounts == null) {
            log.error("Could not find accounts!");
        }

        // do a list with usernames and passwords...
        java.util.Enumeration e =  accounts.keys();
        log.debug("There are  "+accounts.size()+" users which can logon to our system");
        if ( !accounts.containsKey(userContext.get("username"))) {
            log.debug("username: '"+userContext.get("username")+"' not found");
            return null;
         }
        String neededPass = (String) accounts.get(userContext.get("username"));
        if (((String)userContext.get("password")).compareTo(neededPass) != 0) {
            log.debug("username/password combination invalid(in userContext values user:'"+
                      userContext.get("username") + "' password:'" +
                      userContext.get("password") + "')");
            return null;
        }
        log.info("user: '"+userContext.get("username")+" logged in to the system");
        return new UserContext((String)userContext.get("username"));
    }
}
