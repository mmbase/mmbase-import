/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.basic;

import java.util.Map;
import java.io.File;

import org.mmbase.security.Rank;

import org.mmbase.util.ExtendedProperties;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Simple implemetation, to provide authentication from files...
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id$
 */
public class FileLoginModule implements LoginModule {
    private static Logger log=Logging.getLoggerInstance(FileLoginModule.class.getName());
    private File configFile = null;

    public void load(Map<String, Object> properties) {
        String passwordFile = (String)properties.get("file");

        if (passwordFile == null || passwordFile.equals("")) {
            configFile = new File(org.mmbase.module.core.MMBaseContext.getConfigPath() + java.io.File.separator + "accounts.properties");
            log.warn("property file not specified, now using as config file :" + configFile);
        } else {
            configFile = new File(passwordFile);
        }

        if (! configFile.isAbsolute()) {
            File   parentFile   = (File) properties.get("_parentFile");
            log.debug("" + configFile.getPath() + " is not absolute.");
            configFile = new File(parentFile.getParent() + File.separator + configFile.getPath());
            log.debug("Trying " + configFile.getAbsolutePath());
        }

        log.debug("trying to load file login modules with password file:"  + configFile.getAbsolutePath());

        if ( ! configFile.exists() ) {
            log.error("file: '"+configFile+"' did not exist.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' did not exist.");
        }
        if ( ! configFile.isFile() ) {
            log.error("file: '"+configFile+"' is not a file.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' is not a file.");
        }
        if ( ! configFile.canRead() ) {
            log.error("file: '"+configFile+"' is not readable.");
            throw new org.mmbase.security.SecurityException("file: '"+configFile+"' is not readable.");
        }
        log.debug("file login loaded");
    }

    public boolean login(NameContext user, Map<String, ?> loginInfo,  Object[] parameters) {
        if(!loginInfo.containsKey("username")) throw new org.mmbase.security.SecurityException("key 'username' not found  in login information");
        if(!loginInfo.containsKey("password")) throw new org.mmbase.security.SecurityException("key 'password' not found  in login information");
        ExtendedProperties reader = new ExtendedProperties();

        log.debug("reading accounts from " + configFile);
        java.util.Hashtable accounts = reader.readProperties(configFile.getAbsolutePath());

        if (accounts == null) {
            log.error("Could not find accounts!");
        }

        // do a list with usernames and passwords...
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

        // Admins are admins
		if ("admin".equals(loginInfo.get("username"))) {
			user.setRank(Rank.getRank("administrator"));
		}

        log.info("user: '" + loginInfo.get("username") + "' passed this login module");
        return true;
    }
}
