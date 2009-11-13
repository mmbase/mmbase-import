/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.classsecurity;

import java.util.*;

import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * ClassAuthenticationWrapper wraps another Authentication implemention, and adds an extra
 * configuration file. In this configuration file the wrapped Authentication can be specified (and
 * <em>its</em> configuration file if it needs one). Besides that, also authentication credentials
 * can be linked to classes in this XML configuration file.
 *
 * @author   Michiel Meeuwissen
 * @version $Id$
 * @since    MMBase-1.8
 */
public class ClassAuthenticationWrapper extends Authentication {
    private static final Logger log = Logging.getLoggerInstance(ClassAuthenticationWrapper.class);

    private Authentication wrappedAuthentication;

    /**
     * Instantiates an Authentication implementation
     */
    private Authentication getAuthenticationInstance(String className) throws SecurityException {
        Authentication result;
        try {
            Class classType = Class.forName(className);
            Object o = classType.newInstance();
            result = (Authentication) o;
        } catch(ClassNotFoundException cnfe) {
            throw new SecurityException(cnfe);
        } catch(IllegalAccessException iae) {
            throw new SecurityException(iae);
        } catch(InstantiationException ie) {
            throw new SecurityException(ie);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * Reads the configuration file and instantiates and loads the wrapped Authentication.
     */
    protected void load() throws SecurityException {
        try {
            InputSource in = MMBaseCopConfig.securityLoader.getInputSource(configResource);
            Document document = DocumentReader.getDocumentBuilder(
               true, // validate aggresively, because no further error-handling will be done
               new org.mmbase.util.xml.ErrorHandler(false, 0), // don't log, throw exception if not valid, otherwise big chance on NPE and so on
               new org.mmbase.util.xml.EntityResolver(true, getClass()) // validate
               ).parse(in);


            Node authentication = document.getElementsByTagName("authentication").item(0);

            String wrappedClass = authentication.getAttributes().getNamedItem("class").getNodeValue();
            String wrappedUrl   = authentication.getAttributes().getNamedItem("url").getNodeValue();

            wrappedAuthentication = getAuthenticationInstance(wrappedClass);
            wrappedAuthentication.load(manager, configWatcher, wrappedUrl);
            ClassAuthentication.stopWatching();
            ClassAuthentication.load(configResource);

        } catch (java.io.IOException ioe) {
            throw new SecurityException(ioe);
        } catch (SAXException se) {
            throw new SecurityException(se);
        }

    }

    /**
     * logs-in using the first match on the class from the configuration file.
     * @param loginInfo If there are possible credentials already, they can be in this map. The new
     * one will be added. If it is null, a new Map is instantiated.
     * @param parameters Required by the login method of Authentication. I think noone ever uses it.
     */
    protected UserContext login(Map loginInfo, Object[] parameters) throws SecurityException {
        ClassAuthentication.Login l = ClassAuthentication.classCheck(null);
        if (l != null) {
            if (loginInfo == null) loginInfo = new HashMap();
            loginInfo.putAll(l.map);
            return wrappedAuthentication.login(l.application, loginInfo, parameters);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public UserContext login(String application, Map loginInfo, Object[] parameters) throws SecurityException {

        // first try 'cleanly':
        try {
            return wrappedAuthentication.login(application, loginInfo, parameters);
        } catch (UnknownAuthenticationMethodException uam) { // no luck
            return login(loginInfo, parameters);
        } catch (SecurityException se) { // perhaps not recognized
            log.warn("Authentication did not succeed " +  se.getMessage() + " trying self");
            return login(loginInfo, parameters);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValid(UserContext userContext) throws SecurityException {
        return wrappedAuthentication.isValid(userContext);
    }


}
