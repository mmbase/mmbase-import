/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloud;

import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;
import org.mmbase.security.Authentication;

import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Security from within MMBase
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: Authenticate.java,v 1.7 2003-03-04 15:29:37 nico Exp $
 */
class User extends UserContext {
    private String user;
    private Rank rank;
    private long key;

    User(String user, Rank rank, long key) {
    this.user = user;
    this.rank = rank;
    this.key = key;
    }

    public String getIdentifier() {
    return user;
    }

    public Rank getRank() throws org.mmbase.security.SecurityException {
    return rank;
    }

    public String toString() {
    return user + "[" + rank + "]";
    }

    long getKey() {
    return key;
    }
}

public class Authenticate extends Authentication {
    private static Logger log=Logging.getLoggerInstance(Authenticate.class.getName());
    private long validKey;
    private static UserBuilder builder = null;

    public Authenticate() {
        validKey = System.currentTimeMillis();
    }

    protected void load() {
    //log.debug("using: '" + configFile + "' as config file for authentication");
    }

    public UserContext login(String moduleName, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
        log.trace("login-module: '"+moduleName+"'");
    if(moduleName.equals("anonymous")) {
        log.debug("[anonymous login]");
        return new User("anonymous", Rank.ANONYMOUS, validKey);
    }
    else if(moduleName.equals("name/password")) {
        // look if the builder is good and such things....
        checkBuilder();
        if(builder==null) {
                throw new org.mmbase.security.SecurityException("builder wasnt loaded");
            }
            String username = (String)loginInfo.get("username");
        String password = (String)loginInfo.get("password");
            log.trace("login-module: '"+moduleName+"' username: '"+username+"' password: '"+password+"'");
            if(username == null) throw new org.mmbase.security.SecurityException("expected the property 'username' with login");
        if(username.equals("anonymous")) throw new org.mmbase.security.SecurityException("'anonymous' aint allowed to do a login");
        if(password == null) throw new org.mmbase.security.SecurityException("expected the property 'password' with login");

        if(builder.exists(username, password)) {
            //yipee, a logon !!!
        Rank rank;
        if(username.equals("admin")) {
            rank = Rank.ADMIN;
            log.info("[admin login]");
        }
        else {
            rank = Rank.BASICUSER;
            log.info("[anonymous login("+username+")]");
        }
        return new User(username, rank, validKey);
        }
        // helaas, pindakaas...
        return null;
    }
    else {
        String msg = "login module with name '" + moduleName + "' not found, only know 'anonymous' and 'name/password' ";
        log.error(msg);
            throw new org.mmbase.security.SecurityException(msg);
    }
    }

    public boolean isValid(UserContext usercontext) throws org.mmbase.security.SecurityException {
        log.debug(usercontext);
    return ((User)usercontext).getKey() == validKey;
    }

    private void checkBuilder() throws org.mmbase.security.SecurityException {
        if(builder == null) {
            org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
            builder =  (UserBuilder) mmb.getMMObject("mmbaseusers");
            if(builder == null) {
                String msg = "builder mmbaseusers not found";
        log.error(msg);
            throw new org.mmbase.security.SecurityException(msg);
        }   // optional check if the user admin does exist...
        }
    }
}
