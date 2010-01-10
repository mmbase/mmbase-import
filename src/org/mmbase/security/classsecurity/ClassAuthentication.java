/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.classsecurity;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.net.URL;
import java.net.URLConnection;

import org.mmbase.security.SecurityException;
import org.mmbase.security.MMBaseCopConfig;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.*;
import org.xml.sax.InputSource;


/**
 * Provides the static utility methods to authenticate by class. Default a file
 * security/&lt;classauthentication.xml&gt; is used for this. If using the wrapper authentication,
 * its configuration file, contains this configuration.
 *
 * @author   Michiel Meeuwissen
 * @version  $Id$
 * @see      ClassAuthenticationWrapper
 * @since    MMBase-1.8
 */
public class ClassAuthentication {
    private static final Logger log = Logging.getLoggerInstance(ClassAuthentication.class);

    public static final String PUBLIC_ID_CLASSSECURITY_1_0 = "-//MMBase//DTD classsecurity config 1.0//EN";
    public static final String DTD_CLASSSECURITY_1_0       = "classsecurity_1_0.dtd";
    static {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_CLASSSECURITY_1_0, DTD_CLASSSECURITY_1_0, ClassAuthentication.class);
    }
    private static List authenticatedClasses = null;


    static ResourceWatcher watcher = null;

    /**
     * Stop watchin the config file, if there is watched one. This is needed when security
     * configuration switched to ClassAuthenticationWrapper (which will not happen very often).
     */

    static void stopWatching() {
        if (watcher != null) {
            watcher.exit();
        }
    }

    private ClassAuthentication() {
        //Static Utility class
    }

    /**
     * Reads the configuration file and instantiates and loads the wrapped Authentication.
     */
    protected static synchronized void load(String configFile) throws SecurityException {
        List resourceList = MMBaseCopConfig.securityLoader.getResourceList(configFile);
        log.info("Loading " + configFile + "( " + resourceList + ")");
        List ac = new ArrayList(); // temporary stores 'authenticatedClasses'
        ListIterator it = resourceList.listIterator();
        while (it.hasNext()) it.next();
        while (it.hasPrevious()) {
            URL u = (URL) it.previous();
            try {
                URLConnection con = u.openConnection();
                if (! con.getDoInput()) continue;
                InputSource in = new InputSource(con.getInputStream());
                Document document = DocumentReader.getDocumentBuilder(true, // validate aggresively, because no further error-handling will be done
                                                                      new XMLErrorHandler(false, 0), // don't log, throw exception if not valid, otherwise big chance on NPE and so on
                                                                      new XMLEntityResolver(true, ClassAuthentication.class) // validate
                                                                      ).parse(in);

                NodeList authenticates = document.getElementsByTagName("authenticate");

                for (int i = 0; i < authenticates.getLength(); i ++) {
                    Node node = authenticates.item(i);
                    String clazz    = node.getAttributes().getNamedItem("class").getNodeValue();
                    String method   = node.getAttributes().getNamedItem("method").getNodeValue();
                    int    weight   = Integer.parseInt(node.getAttributes().getNamedItem("weight").getNodeValue());
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
                    ac.add(new Login(Pattern.compile(clazz), method, Collections.unmodifiableMap(map), weight));
                }
            } catch (Exception e) {
                log.error(u + " " + e.getMessage(), e);
            }
        }

        Collections.sort(ac);

        { // last fall back, everybody may get the 'anonymous' cloud.
            Map map = new HashMap();
            map.put("rank", "anonymous");
            ac.add(new Login(Pattern.compile(".*"), "class", Collections.unmodifiableMap(map), Integer.MIN_VALUE));
        }

        // This method is responsible for this list, so we return an unmodifable version
        // Also, the authenticatedClasses member must remain null as long as it is not yet fully
        // read (classChecked is locked then)
        authenticatedClasses = Collections.unmodifiableList(ac);
        log.service("Class authentication: " + ac);

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
     * @return A Login object if yes, <code>null</code> if not.
     */
    public static Login classCheck(String application) {
        if (authenticatedClasses == null) {
            synchronized(ClassAuthentication.class) { // if load is running this locks
                if (authenticatedClasses == null) { // if locked, load was running and this now skips, so load is not called twice.
                    String configFile = "classauthentication.xml";
                    watcher = new ResourceWatcher(MMBaseCopConfig.securityLoader) {
                            public void onChange(String resource) {
                                load(resource);
                            }
                        };
                    watcher.add(configFile);
                    watcher.start();
                    watcher.onChange();
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.trace("Class authenticating (" + authenticatedClasses + ")");
        }
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
                        if (log.isDebugEnabled()) {
                            log.debug("" + className + " matches! ->" + n + " " + n.getMap());
                        }
                        return n;
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Failed to authenticate " + Arrays.asList(stack) + " with " + authenticatedClasses);
        }
        return null;
    }

    /**
     * A structure to hold the login information.
     */
    public static class  Login implements Comparable {
        final Pattern classPattern;
        final String application;
        final Map    map;
        final int    weight;
        Login(Pattern p , String a, Map m, int w) {
            classPattern = p;
            application = a;
            map = m;
            weight = w;
        }

        public Map getMap() {
            return map;
        }
        public String toString() {
            return "" + weight + ":" + classPattern.pattern() + (application.equals("class") ? "" : ": " + application) + " " + map;
        }
        public int compareTo(Object o) {
            if (o instanceof Login) {
                return ((Login) o).weight - this.weight;
            } else {
                return 0;
            }
        }
    }

}
