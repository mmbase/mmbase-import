/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.security.implementation.basic;

import org.w3c.dom.Element;
import org.mmbase.util.XMLBasicReader;
import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;
import org.mmbase.security.Authentication;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Authentication based on a config file..
 * @javadoc
 * @author Eduard Witteveen
 * @version $Id: AuthenticationHandler.java,v 1.5 2003-03-04 15:29:36 nico Exp $
 */
public class AuthenticationHandler extends Authentication {
    private static Logger log=Logging.getLoggerInstance(AuthenticationHandler.class.getName());

    // hashmap of the modules..
    private HashMap modules = new HashMap();
    // hashmap of the ranks of the modules..
    private HashMap moduleRanks = new HashMap();

    protected void load() {
        log.debug("using: '" +  configFile + "' as config file for authentication");
        XMLBasicReader reader = new XMLBasicReader(configFile.getAbsolutePath(), getClass());

        log.debug("Trying to load all loginmodules:");
        Enumeration list = reader.getChildElements(reader.getElementByPath("authentication"),"loginmodule");
    while(list.hasMoreElements()) {
        Element modTag = (Element)list.nextElement();
        String modName = reader.getElementAttributeValue(modTag, "name");
            if(modName.equals("")) {
                log.error("module attribute name was not defined in :" + configFile);
                throw new SecurityException("module attribute name was not defined in :" + configFile);
            }
        String modClass = reader.getElementAttributeValue(modTag, "class");
            if(modClass.equals("")) {
                log.error("module attribute class was not defined in :" + configFile + " for module: " + modName);
                throw new SecurityException("module attribute class was not defined in :" + configFile + " for module: " + modName);
            }
            String modRankString = reader.getElementAttributeValue(modTag, "rank");
            if(modRankString.equals("")) {
                log.error("module attribute rank was not defined in :" + configFile + " for module: " + modName);
                throw new SecurityException("module attribute rank was not defined in :" + configFile + " for module: " + modName);
            }
            Rank modRank = Rank.getRank(modRankString);

        log.debug("Trying to load login module with name: " + modName);

            // create the module...
            LoginModule module;
            try {
                Class moduleClass = Class.forName(modClass);
                module = (LoginModule) moduleClass.newInstance();
            }
        catch(Exception e) {
                log.error("Could not create Login Module with class name " + modClass);
                throw new SecurityException("Could not create Login Module with class name " + modClass);
            }

            // retrieve the properties...
            Enumeration propEnum = reader.getChildElements(modTag,"property");
            HashMap properties = new HashMap();
            while(propEnum.hasMoreElements()) {
                Element propTag = (Element)propEnum.nextElement();
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
    log.debug("Loaded all loginmodules "+ listModules());
    }


    public UserContext login(String moduleName, Map loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
    LoginModule module = (LoginModule) modules.get(moduleName);
    if(module == null) {
        log.error("Login Module with name '" + moduleName + "' not found ! (available:"+listModules()+")");
        throw new SecurityException("Login Module with name '" + moduleName + "' not found ! (available:"+listModules()+")");
    }
    NameContext newUser = new NameContext((Rank)moduleRanks.get(moduleName));
    if(module.login(newUser, loginInfo, parameters)) {
        // our login succeeded..

            // check if the identifier was set by the loginModule, when invalid will trow exception..
            newUser.getIdentifier();

        return newUser;
    }
    return null;
    }

    private String listModules() {
        Iterator i = modules.keySet().iterator();
        String loginModulesAvailable="";
        while(i.hasNext()) {
            loginModulesAvailable += "\"" + (String) i.next() + "\" ";
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
