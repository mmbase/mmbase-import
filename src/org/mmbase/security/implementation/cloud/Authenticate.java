/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloud;

import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;
import org.mmbase.security.BasicUser;
import org.mmbase.security.Authentication;

import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Very simply security from within MMBase. You might want to look at Cloud Context Security which offers a much more powerfull implementation.
 *
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class Authenticate extends Authentication {
    private static final Logger log=Logging.getLoggerInstance(Authenticate.class);
    private long validKey;
    private static UserBuilder builder = null;

    public Authenticate() {
        validKey = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     * Implementation is empty here.
     */
    protected void load() {
        //log.debug("using: '" + configFile + "' as config file for authentication");
    }

    /**
     * Gets rank from user, whici sin
     * @since MMBase-1.8
     */
    protected Rank getRank(String userName) {
        if(userName.equals("admin")) {
            log.debug("[admin login]");
            return Rank.ADMIN;
        } else {
            return Rank.BASICUSER;
        }
    }

    /**
     * {@inheritDoc}
     */
    public UserContext login(String moduleName, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
        if (log.isTraceEnabled()) {
            log.trace("login-module: '" + moduleName + "'");
        }
        if("anonymous".equals(moduleName)) {
            log.debug("[anonymous login]");
            return new User("anonymous", Rank.ANONYMOUS, validKey, "anonymous");
        } else if("name/password".equals(moduleName)) {
            // look if the builder is good and such things....
            checkBuilder();
            if(builder==null) {
                throw new org.mmbase.security.SecurityException("builder wasnt loaded");
            }
            String userName = (String)loginInfo.get("username");
            String password = (String)loginInfo.get("password");
            log.trace("login-module: '" +moduleName + "' username: '" + userName + "' password: '" + password + "'");
            if(userName == null) throw new org.mmbase.security.SecurityException("expected the property 'username' with login");
            if(userName.equals("anonymous")) throw new org.mmbase.security.SecurityException("'anonymous' is not allowed to do a login");
            if(password == null) throw new org.mmbase.security.SecurityException("expected the property 'password' with login");

            if(builder.exists(userName, password)) {
                return new User(userName, getRank(userName), validKey, "name/password");
            } else {
                // helaas, pindakaas...
                return null;
            }
        } else if ("class".equals(moduleName)) {
            org.mmbase.security.classsecurity.ClassAuthentication.Login li = org.mmbase.security.classsecurity.ClassAuthentication.classCheck("class");
            if (li == null) {
                throw new SecurityException("Class authentication failed  (class not authorized)");
            }
            String userName = (String) li.getMap().get("username");
            if (userName == null && "administrator".equals(li.getMap().get("rank"))) userName = "admin";
            if (userName == null) throw new org.mmbase.security.SecurityException("expected the property 'username' with login");
            if (userName.equals("admin") || builder.exists(userName, null)) {
                return new User(userName, getRank(userName), validKey, "class");
            } else {
                return null;
            }

        } else {
            throw new org.mmbase.security.UnknownAuthenticationMethodException("login module with name '" + moduleName + "' not found, only know 'anonymous' and 'name/password' ");
        }
    }

    /**
     * {@inheritDoc}
     */
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


    /**
     * The user object for 'cloud' security.
     */
    private static class User extends BasicUser {
        private String user;
        private Rank rank;
        private long key;
        
        User(String user, Rank rank, long key, String app) {
            super(app);
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

}


