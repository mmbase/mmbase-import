package org.mmbase.security.implementation;

import java.util.HashMap;
import java.io.File;

import org.mmbase.util.ExtendedProperties;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * simple implemetation, to provide authentication from files...
 */
public class FileLoginModule implements LoginModule {
    private static Logger log=Logging.getLoggerInstance(FileLoginModule.class.getName());
    private String configFile = null;

    public void load(HashMap properties) {
        String passwordFile = (String)properties.get("file");

        if (passwordFile == null || passwordFile.equals("")) {
            configFile = org.mmbase.module.core.MMBaseContext.getConfigPath() + java.io.File.separator + "accounts.properties";
            log.warn("property file not specified, now using as config file :" + configFile);
        } else {
            configFile = passwordFile;
        }

        log.debug("trying to load file login modules with password file:"  + configFile);

        File file = new File(configFile);

        if (! file.isAbsolute()) {
            File   parentFile   = (File) properties.get("_parentFile");
            file = new File(parentFile.getParent() + File.separator + configFile);
            log.debug("" + configFile + " is not absolute. Trying " + file.getAbsolutePath());            
        }

        if ( !file.exists() ) {
            log.error("file: '"+configFile+"' did not exist.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' did not exist.");
        }
        if ( !file.isFile() ) {
            log.error("file: '"+configFile+"' is not a file.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' is not a file.");
        }
        if ( !file.canRead() ) {
            log.error("file: '"+configFile+"' is not readable.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' is not readable.");
        }
        log.debug("file login loaded");
    }

    public boolean login(NameContext user, HashMap loginInfo,  Object[] parameters) {
    	if(!loginInfo.containsKey("username")) throw new org.mmbase.security.SecurityException("key 'username' not found  in login information");
    	if(!loginInfo.containsKey("password")) throw new org.mmbase.security.SecurityException("key 'password' not found  in login information");	
        ExtendedProperties reader = new ExtendedProperties();

        log.debug("reading accounts from " + configFile);
        java.util.Hashtable accounts = reader.readProperties(configFile);

        if (accounts == null) {
            log.error("Could not find accounts!");
        }

        // do a list with usernames and passwords...
        java.util.Enumeration e =  accounts.keys();
        log.debug("There are  "+accounts.size()+" users which can logon to our system");
        if ( !accounts.containsKey(loginInfo.get("username"))) {
            log.debug("username: '"+loginInfo.get("username")+"' not found");
            return false;
         }
        String neededPass = (String) accounts.get(loginInfo.get("username"));
        if (!neededPass.equals(loginInfo.get("password"))) {
            log.debug("username/password combination invalid(in HashTable values user:'"+
                      loginInfo.get("username") + "' password:'" +
                      loginInfo.get("password") + "')");
            return false;
        }

        // set the identifier
        user.setIdentifier((String)loginInfo.get("username"));

        log.info("user: '"+loginInfo.get("username")+" passed this login module");
        return true;
    }
}
