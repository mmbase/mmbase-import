/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.classsecurity;

import org.mmbase.security.SecurityException;
import org.mmbase.security.Authentication;
import org.mmbase.security.UserContext;

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
 * @author Michiel Meeuwissen
 * @version $Id: ClassAuthenticationWrapper.java,v 1.1 2004-03-25 18:00:38 michiel Exp $
 */
public class ClassAuthenticationWrapper extends Authentication {
    private static final Logger log = Logging.getLoggerInstance(ClassAuthenticationWrapper.class);

    public static final String PUBLIC_ID_CLASSSECURITY_1_0 = "-//MMBase//DTD classsecurity config 1.0//EN";
    public static final String DTD_CLASSSECURITY_1_0       = "classsecurity_1_0.dtd";
    

    static {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_CLASSSECURITY_1_0, DTD_CLASSSECURITY_1_0, ClassAuthenticationWrapper.class);
    }
    private Authentication wrappedAuthentication;
    private Map authenticatedClasses = new HashMap();




    private Authentication getAuthentication(String className) throws SecurityException {
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


    protected void load() throws SecurityException {
        try {
            InputSource in = new InputSource(new FileInputStream(configFile));
            Document document = DocumentReader.getDocumentBuilder(true, /* validate */
                                                                  new XMLErrorHandler(false, 0), /* don't log, throw exception if not valid */
                                                                  new XMLEntityResolver(true, getClass()) /* validate */
                                                                  ).parse(in);
            
            
            Node authentication = document.getElementsByTagName("authentication").item(0);
            
            String wrappedClass = authentication.getAttributes().getNamedItem("class").getNodeValue();
            String wrappedUrl   = authentication.getAttributes().getNamedItem("url").getNodeValue();
            
            
            wrappedAuthentication = getAuthentication(wrappedClass);

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
                authenticatedClasses.put(Pattern.compile(clazz), new Login(method, map));
                    
            }

        } catch (Exception fnfe) {
            throw new SecurityException(fnfe);
        }



    }


    public UserContext login(String application, Map loginInfo, Object[] parameters) throws SecurityException {

        if (application.equals("class")) {
            Throwable t = new Throwable();
            StackTraceElement[] stack = t.getStackTrace();

            Iterator i = authenticatedClasses.entrySet().iterator();
            OUTER_LOOP:
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                Pattern p = (Pattern) entry.getKey();
                
                for (int j = 2; j < stack.length; j++) {
                    String className = stack[j].getClassName();
                    if (p.matcher(className).matches()) {
                        Login n = (Login) entry.getValue();
                        application = n.method;
                        if (loginInfo == null) loginInfo = new HashMap();
                        loginInfo.putAll(n.map);
                        break OUTER_LOOP;
                    }
                    
                }
                
            }
        }
        return wrappedAuthentication.login(application, loginInfo, parameters);
    
    }

    // javadoc inherited
    public boolean isValid(UserContext userContext) throws SecurityException {
        return wrappedAuthentication.isValid(userContext);
    }

    class  Login {
        String method;
        Map    map;
        Login(String m, Map ma) {
            method = m;
            map = ma;
        }
        public String toString() {
            return method;
        }
    }

}
