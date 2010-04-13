/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.implementation.basic;

import org.w3c.dom.Element;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.xml.EntityResolver;

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
        EntityResolver.registerPublicID(PUBLIC_ID_BASICSECURITY_1_0, DTD_BASICSECURITY_1_0, AuthenticationHandler.class);
    }

    private Map<String, LoginModule> modules = new HashMap<String, LoginModule>();
    private Map<String, Rank> moduleRanks    = new HashMap<String, Rank>();

    protected void load() {
        DocumentReader reader;
        try {
            org.xml.sax.InputSource in = MMBaseCopConfig.securityLoader.getInputSource(configResource);
            log.debug("using: '" + configResource + "' as config file for authentication");
            reader = new DocumentReader(in, getClass());
        } catch (Exception e) {
            throw new SecurityException(e);
        }



        log.debug("Trying to load all loginmodules:");
        for (Element modTag: reader.getChildElements(reader.getElementByPath("authentication"), "loginmodule")) {
            String modName = reader.getElementAttributeValue(modTag, "name");
            if (modName.equals("")) {
                throw new SecurityException("module attribute name was not defined in :" + configResource);
            }
            String modClass = reader.getElementAttributeValue(modTag, "class");
            if (modClass.equals("")) {
                throw new SecurityException("module attribute class was not defined in :" + configResource + " for module: " + modName);
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
            Map<String, Object> properties = new HashMap<String, Object>();
            for (Element propTag: reader.getChildElements(modTag, "property")) {
                String propName = reader.getElementAttributeValue(propTag, "name");
                String propValue = reader.getElementValue(propTag).trim();
                properties.put(propName, propValue);
                log.debug("\tadding key : " + propName + " with value : " + propValue);
            }
            properties.put("_parentFile", configResource);
            // if module's configuration uses filenames, they probably want to be relative to this one.
            module.load(properties);
            modules.put(modName, module);
            moduleRanks.put(modName, modRank);
            log.debug("Loaded loginmodule with name: " + modName);
        }
        log.debug("Loaded all loginmodules " + listModules());
    }

    public UserContext login(String moduleName, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
        LoginModule module = modules.get(moduleName);
        if (module == null) {
            log.error("Login Module with name '" + moduleName + "' not found ! (available:" + listModules() + ")");
            throw new UnknownAuthenticationMethodException("Login Module with name '" + moduleName + "' not found ! (available:" + listModules() + ")");
        }
        NameContext newUser = new NameContext(moduleRanks.get(moduleName), moduleName);
        if (module.login(newUser, loginInfo, parameters)) {
            // our login succeeded..
            // check if the identifier was set by the loginModule, when invalid will trow exception..
            newUser.getIdentifier();
            return newUser;
        }
        return null;
    }

    private String listModules() {
        StringBuffer loginModulesAvailable = new StringBuffer();
        for (String mod : modules.keySet()) {
            loginModulesAvailable.append("\"").append(mod).append("\" ");
        }
        return loginModulesAvailable.toString();
    }

    /**
     * this method does nothing..
     */
    public boolean isValid(UserContext usercontext) throws org.mmbase.security.SecurityException {
        return true;
    }
}
