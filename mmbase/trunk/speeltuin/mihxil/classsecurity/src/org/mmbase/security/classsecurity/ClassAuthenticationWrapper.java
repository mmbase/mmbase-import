/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.classsecurity;

import org.mmbase.security.SecurityException;
import org.mmbase.security.*;


import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.*;


import java.util.*;
import java.util.regex.*;
import java.io.*;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.*;


/**
 * ClassAuthenticationWrapper wraps another Authentication implemention, and adds an extra
 * configuration file. In this configuration file the wrapped Authentication can be specified (and
 * <em>its</em> configuration file if it needs one). Besides that, also authentication credentials
 * can be linked to classes in this XML configuration file.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ClassAuthenticationWrapper.java,v 1.2 2004-04-01 22:48:17 michiel Exp $
 */
public class ClassAuthenticationWrapper extends Authentication {
    private static final Logger log = Logging.getLoggerInstance(ClassAuthenticationWrapper.class);

    public static final String PUBLIC_ID_CLASSSECURITY_1_0 = "-//MMBase//DTD classsecurity config 1.0//EN";
    public static final String DTD_CLASSSECURITY_1_0       = "classsecurity_1_0.dtd";
    
    
    static {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_CLASSSECURITY_1_0, DTD_CLASSSECURITY_1_0, ClassAuthenticationWrapper.class);
    }
    private Authentication wrappedAuthentication;
    private static List authenticatedClasses = new ArrayList();


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
            InputSource in = new InputSource(new FileInputStream(configFile));
            Document document = DocumentReader.getDocumentBuilder(
               true, // validate aggresively, because no further error-handling will be done
               new XMLErrorHandler(false, 0), // don't log, throw exception if not valid, otherwise big chance on NPE and so on
               new XMLEntityResolver(true, getClass()) // validate
               ).parse(in);
            
            
            Node authentication = document.getElementsByTagName("authentication").item(0);
            
            String wrappedClass = authentication.getAttributes().getNamedItem("class").getNodeValue();
            String wrappedUrl   = authentication.getAttributes().getNamedItem("url").getNodeValue();
            
            
            wrappedAuthentication = getAuthenticationInstance(wrappedClass);

            wrappedAuthentication.load(manager, fileWatcher, wrappedUrl);

            
            NodeList authenticates = document.getElementsByTagName("authenticate");
            
            for (int i = 0; i < authenticates.getLength(); i ++) {
                Node node = authenticates.item(i);
                String clazz    = node.getAttributes().getNamedItem("class").getNodeValue();
                String method   = node.getAttributes().getNamedItem("method").getNodeValue();

                
                Node property   = node.getFirstChild();
                Map map = new HashMap();
                while (property != null) {
                    if (property instanceof Element && property.getNodeName().equals("property")) {
                        String name     = property.getAttributes().getNamedItem("name").getNodeValue();
                        String value    = property.getAttributes().getNamedItem("value").getNodeValue();
                        map.put(name, value);
                    }
                    property = property.getNextSibling();
                }
                authenticatedClasses.add(new Login(Pattern.compile(clazz), method, map));
                
            }

        } catch (Exception fnfe) {
            throw new SecurityException(fnfe);
        }



    }

    /**
     * Checks wether the (indirectly) calling class is authenticated by the
     * ClassAuthenticationWrapper (using a stack trace). This method can be called from an
     * Authentication implementation, e.g. to implement the 'class' application itself (if the
     * authentication implementation does understand the concept itself, then passwords can be
     * avoided in the wrappers' configuration file).
     *
     * @param application Only checks this 'authentication application'. Can be <code>null</code> to
     * check for every application.
     * @returns A Login object if yes, <code>null</code> if not.
     */
    public static Login classCheck(String application) {
        Throwable t = new Throwable();
        StackTraceElement[] stack = t.getStackTrace();
        
        Iterator i = authenticatedClasses.iterator();

        while(i.hasNext()) {
            Login n = (Login) i.next();
            if (application == null || application.equals(n.application)) {
                Pattern p = n.classPattern;
                for (int j = 0; j < stack.length; j++) {
                    String className = stack[j].getClassName();
                    if (className.startsWith("org.mmbase.security.")) continue;
                    if (className.startsWith("org.mmbase.bridge.implementation.")) continue;
                    log.trace("Checking " + className);
                    if (p.matcher(className).matches()) {
                        log.debug("" + className + " matches!");
                        return n;
                    }
                }
            }
        }
        return null;
    }

    /**
     * logs-in using the first match on the class from the configuration file.
     * @param loginInfo If there are possible credentials already, they can be in this map. The new
     * one will be added. If it is null, a new Map is instantiated.
     * @param parameters Required by the login method of Authentication. I think noone ever uses it.
     */
    protected UserContext login(Map loginInfo, Object[] parameters) throws SecurityException {
        Login l = classCheck(null);
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

    /**
     * A structure to hold the login information.
     */
    public class  Login {
        Pattern classPattern;
        String application;
        Map    map;
        Login(Pattern p , String a, Map m) {
            classPattern = p;
            application = a;
            map = m;
        }

        public Map getMap() {
            return map;
        }
        public String toString() {
            return application;
        }
    }

}
