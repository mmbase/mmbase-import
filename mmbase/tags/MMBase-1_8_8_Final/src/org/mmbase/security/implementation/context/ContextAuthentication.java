/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.*;
import org.mmbase.security.SecurityException;

import java.util.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;

import org.xml.sax.InputSource;

import org.apache.xpath.XPathAPI;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Authentication based on a XML-configuration file. The XML file contains besides users, groups and
 * contexts (used for ContextAuthorization).
 *
 * @author Eduard Witteveen
 * @version $Id$
 * @see    ContextAuthorization
 */
public class ContextAuthentication extends Authentication {
    private static final Logger log = Logging.getLoggerInstance(ContextAuthentication.class);
    private Map  loginModules = new LinkedHashMap();
    private Document document;

    /** Public ID of the Builder DTD version 1.0 */
    public static final String PUBLIC_ID_SECURITY_CONTEXT_CONFIG_1_0 = "-//MMBase//DTD security context config 1.0//EN";
    public static final String PUBLIC_ID_SECURITY_CONTEXT_CONFIG_1_1 = "-//MMBase//DTD security context config 1.1//EN";
    public static final String PUBLIC_ID_SECURITY_CONTEXT_CONFIG_1_2 = "-//MMBase//DTD security context config 1.2//EN";

    /** DTD resource filename of the Builder DTD version 1.0 */
    public static final String DTD_SECURITY_CONTEXT_CONFIG_1_0 = "securitycontextconfig_1_0.dtd";
    public static final String DTD_SECURITY_CONTEXT_CONFIG_1_1 = "securitycontextconfig_1_1.dtd";
    public static final String DTD_SECURITY_CONTEXT_CONFIG_1_2 = "securitycontextconfig_1_2.dtd";

    static {
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_SECURITY_CONTEXT_CONFIG_1_0, DTD_SECURITY_CONTEXT_CONFIG_1_0, MMBaseCopConfig.class);
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_SECURITY_CONTEXT_CONFIG_1_1, DTD_SECURITY_CONTEXT_CONFIG_1_1, MMBaseCopConfig.class);
        org.mmbase.util.XMLEntityResolver.registerPublicID(PUBLIC_ID_SECURITY_CONTEXT_CONFIG_1_2, DTD_SECURITY_CONTEXT_CONFIG_1_2, MMBaseCopConfig.class);
    }

    public ContextAuthentication() {
    }

    protected void load() {
        if (log.isDebugEnabled()) {
            log.debug("using: '" + configResource + "' as config file for context-authentication");
        }

        try {
            InputSource in = MMBaseCopConfig.securityLoader.getInputSource(configResource);
            document = org.mmbase.util.XMLBasicReader.getDocumentBuilder(this.getClass()).parse(in);
        } catch(org.xml.sax.SAXException se) {
            log.error("error parsing file :"+configResource);
            String message = "error loading configfile :'" + configResource + "'("+se + "->"+se.getMessage()+"("+se.getMessage()+"))";
            log.error(message);
            log.error(Logging.stackTrace(se));
            throw new SecurityException(message);
        } catch(java.io.IOException ioe) {
            log.error("error parsing file :"+configResource);
            log.error(Logging.stackTrace(ioe));
            throw new SecurityException("error loading configfile :'"+configResource+"'("+ioe+")" );
        }
        if (log.isDebugEnabled()) {
            log.debug("loaded: '" +  configResource + "' as config file for authentication");
            log.debug("going to load the modules...");
        }

        // do the xpath query...
        String xpath = "/contextconfig/loginmodules/module";
        if (log.isDebugEnabled()) log.debug("going to execute the query:" + xpath );
        NodeIterator found;
        try {
            found = XPathAPI.selectNodeIterator(document, xpath);
        } catch(javax.xml.transform.TransformerException te) {
            log.error("error executing query: '" + xpath + "' ");
            log.error( Logging.stackTrace(te));
            throw new SecurityException("error executing query: '"+xpath+"' ");
        }
        // we now have a list of login modules.. process them all, and load them...
        for(Node contains = found.nextNode(); contains != null; contains = found.nextNode()) {
            NamedNodeMap nnm = contains.getAttributes();
            String moduleName = nnm.getNamedItem("name").getNodeValue();
            String className = nnm.getNamedItem("class").getNodeValue();

            log.debug("going to try to load module with the name '" + moduleName + "' with class: " + className);
            ContextLoginModule module;
            try {
                Class moduleClass = Class.forName(className);
                module = (ContextLoginModule) moduleClass.newInstance();
            } catch(Exception e) {
                String msg = "could not load module with the name: '" + moduleName + "' with class: " + className;
                log.error(msg);
                log.error( Logging.stackTrace(e));
                throw new SecurityException(msg);
            }
            module.load(document, getKey(), moduleName, manager);
            log.info("loaded module with the name: '" + moduleName + "' with class: " + className);
            loginModules.put(moduleName, module);
        }

        if (!loginModules.containsKey("class")) {
            ContextLoginModule classModule =  new ClassLogin();
            log.info("The class login module was not configured. It is needed sometimes. Now loading module with the name 'class' with class: " + classModule.getClass());
            classModule.load(document, getKey(), "class", manager);
            loginModules.put("class", classModule);
        }

        log.debug("done loading the modules...");
    }


    public UserContext login(String moduleName, Map loginInfo, Object[] parameters) throws SecurityException {
        // look if we can find our login module...
        if(!loginModules.containsKey(moduleName)) {
            throw new UnknownAuthenticationMethodException("could not load module with name: '" +  moduleName + "'");
        }
        ContextLoginModule module = (ContextLoginModule) loginModules.get(moduleName);
        // and we do the login...
        UserContext user = module.login(loginInfo, parameters);
        if (log.isServiceEnabled()) {
            if(user == null) {
                log.debug("login on module with name '" + moduleName + "' failed");
            } else {
                if (user.getRank().getInt() > Rank.ANONYMOUS_INT) {
                    log.debug("login on module with name '" + moduleName + "' was succesfull for user with id: '" + user.getIdentifier() + "'");
                }
            }
        }
        return user;
    }

    /**
     * this method does nothing..
     */
    public boolean isValid(UserContext userContext) throws SecurityException {
        if ( getKey() == ((ContextUserContext)userContext).getKey()) return true;
        log.debug("not valid because " + getKey () + " != " + ((ContextUserContext) userContext).getKey());
        return false;
    }

    public String[] getTypes() {
        return (String[]) loginModules.keySet().toArray(new String[] {});
    }
}
