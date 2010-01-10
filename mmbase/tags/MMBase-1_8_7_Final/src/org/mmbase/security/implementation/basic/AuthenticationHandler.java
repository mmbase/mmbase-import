/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.implementation.basic;

import org.w3c.dom.Element;
import org.mmbase.util.XMLBasicReader;
import org.mmbase.util.XMLEntityResolver;

import org.mmbase.security.*;
import org.mmbase.security.SecurityException;

import java.util.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Authentication based on a config files. There is an XML file (`authentication.xml') which defines
 * several modules (conected to the 'module/method' String). There are now three moduiles in this
 * implementation. 'anonymous' for the anonyunous user. 'name/password' for 'basic users'. The
 * username/passwords of the basic users are defined in an account.properties file. The last module
 * is 'admin' which authenticates only on password.
 *
 * @todo MM: I think it should be possible for admin to login with name/password to, how else could
 * you use HTTP authentication (e.g. admin pages).
 * @author Eduard Witteveen
 * @version $Id$
 */
public class AuthenticationHandler extends Authentication {
    private static final Logger log = Logging.getLoggerInstance(AuthenticationHandler.class);

    public static final String PUBLIC_ID_BASICSECURITY_1_0 = "-//MMBase//DTD securitybasicauth config 1.0//EN";
    public static final String DTD_BASICSECURITY_1_0       = " securitybasicauth_1_0.dtd";
    
    
    static {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_BASICSECURITY_1_0, DTD_BASICSECURITY_1_0, AuthenticationHandler.class);
    }

    // hashmap of the modules..
    private Map modules = new HashMap();
    // hashmap of the ranks of the modules..
    private Map moduleRanks = new HashMap();

    protected void load() {
        log.debug("using: '" + configFile + "' as config file for authentication");
        XMLBasicReader reader = new XMLBasicReader(configFile.getAbsolutePath(), getClass());

        log.debug("Trying to load all loginmodules:");
        for (Iterator modIter = reader.getChildElements(reader.getElementByPath("authentication"), "loginmodule"); modIter.hasNext();) {
            Element modTag = (Element) modIter.next();
            String modName = reader.getElementAttributeValue(modTag, "name");
            if (modName.equals("")) {
                log.error("module attribute name was not defined in :" + configFile);
                throw new SecurityException("module attribute name was not defined in :" + configFile);
            }
            String modClass = reader.getElementAttributeValue(modTag, "class");
            if (modClass.equals("")) {
                log.error("module attribute class was not defined in :" + configFile + " for module: " + modName);
                throw new SecurityException("module attribute class was not defined in :" + configFile + " for module: " + modName);
            }
            String modRankString = reader.getElementAttributeValue(modTag, "rank");
            Rank modRank;
            if (modRankString.equals("")) {
                modRank = null;
            } else {
                modRank = Rank.getRank(modRankString);
            }

            log.debug("Trying to load login module with name: " + modName);

            // create the module...
            LoginModule module;
            try {
                Class moduleClass = Class.forName(modClass);
                module = (LoginModule)moduleClass.newInstance();
            } catch (Exception e) {
                log.error("Could not create Login Module with class name " + modClass);
                throw new SecurityException("Could not create Login Module with class name " + modClass);
            }

            // retrieve the properties...
            HashMap properties = new HashMap();
            for (Iterator propIter = reader.getChildElements(modTag, "property"); propIter.hasNext();) {
                Element propTag = (Element) propIter.next();
                String propName = reader.getElementAttributeValue(propTag, "name");
                String propValue = reader.getElementValue(propTag).trim();
                properties.put(propName, propValue);
                log.debug("\tadding key : " + propName + " with value : " + propValue);
            }
            properties.put("_parentFile", configFile);
            // if module's configuration uses filenames, they probably want to be relative to this one.
            module.load(properties);
            modules.put(modName, module);
            moduleRanks.put(modName, modRank);
            log.debug("Loaded loginmodule with name: " + modName);
        }
        log.debug("Loaded all loginmodules " + listModules());
    }

    public UserContext login(String moduleName, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
        LoginModule module = (LoginModule)modules.get(moduleName);
        if (module == null) {
            log.error("Login Module with name '" + moduleName + "' not found ! (available:" + listModules() + ")");
            throw new UnknownAuthenticationMethodException("Login Module with name '" + moduleName + "' not found ! (available:" + listModules() + ")");
        }
        NameContext newUser = new NameContext((Rank)moduleRanks.get(moduleName), moduleName);
        if (module.login(newUser, loginInfo, parameters)) {
            // our login succeeded..
            // check if the identifier was set by the loginModule, when invalid will trow exception..
            newUser.getIdentifier();
            return newUser;
        }
        return null;
    }

    private String listModules() {
        Iterator i = modules.keySet().iterator();
        String loginModulesAvailable = "";
        while (i.hasNext()) {
            loginModulesAvailable += "\"" + (String)i.next() + "\" ";
        }
        return loginModulesAvailable;
    }

    /**
     * this method does nothing..
     */
    public boolean isValid(UserContext usercontext) throws org.mmbase.security.SecurityException {
        return true;
    }
}
