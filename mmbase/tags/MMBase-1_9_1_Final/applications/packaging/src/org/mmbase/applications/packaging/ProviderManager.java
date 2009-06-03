/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.packaging;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.applications.packaging.providerhandlers.DiskProvider;
import org.mmbase.applications.packaging.providerhandlers.HttpProvider;
import org.mmbase.applications.packaging.providerhandlers.PackageDiscovery;
import org.mmbase.applications.packaging.providerhandlers.ProviderFileWriter;
import org.mmbase.applications.packaging.providerhandlers.ProviderInterface;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/**
 * provider manager, maintains the package/bundles providers and abstracts
 * their access methods for the Bundle and Package manager.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class ProviderManager {
    private static Logger log = Logging.getLoggerInstance(ProviderManager.class);
    private static boolean state = false;
    private static PackageDiscovery packagediscovery = null;

    // Contains all providers key=provider value=reference to provider
    private static HashMap<String, ProviderInterface> providers = null;

    // Contains all providerhandlers
    private static HashMap<String, String> providerhandlers = null;


    /** DTD resource filename of the providerhandlers DTD version 1.0 */
    public static final String DTD_PROVIDERHANDLERS_1_0 = "providerhandlers_1_0.dtd";
    public static final String DTD_PROVIDERS_1_0 = "providers_1_0.dtd";
    public static final String DTD_SHAREAUTOCONFIG_1_0 = "shareautoconfig_1_0.dtd";

    public static final String PUBLIC_ID_PROVIDERHANDLERS_1_0 = "-//MMBase//DTD providerhandlers config 1.0//EN";
    public static final String PUBLIC_ID_PROVIDERS_1_0 = "-//MMBase//DTD providers config 1.0//EN";
    public static final String PUBLIC_ID_SHAREAUTOCONFIG_1_0 = "-//MMBase//DTD shareautoconfig 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_PROVIDERHANDLERS_1_0, DTD_PROVIDERHANDLERS_1_0, ProviderManager.class);
        EntityResolver.registerPublicID(PUBLIC_ID_PROVIDERS_1_0, DTD_PROVIDERS_1_0, ProviderManager.class);
        EntityResolver.registerPublicID(PUBLIC_ID_SHAREAUTOCONFIG_1_0, DTD_SHAREAUTOCONFIG_1_0, ProviderManager.class);
    }


    public static synchronized void init() {
        if (!isRunning()) {
            readProviderHandlers();
            readProviders();
            packagediscovery = new PackageDiscovery();
            state=true;
        }
    }

    public static boolean isRunning() {
        return state;
    }

    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator<ProviderInterface> getProviders() {
        if (providers == null) init();
        return providers.values().iterator();
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static ProviderInterface getProvider(String name) {
        if (providers == null) init();
    Object o = providers.get(name);
    if (o != null) {
        return (ProviderInterface)o;
    } else {
        return null;
    }
    }

    public static void resetSleepCounter() {
        packagediscovery.resetSleepCounter();
    }




    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static ProviderInterface get(String name) {
        if (providers == null) init();
        Object o = providers.get(name);
        if (o != null) {
            return (ProviderInterface)o;
        } else {
            log.error("Can't find provider with name : "+name);
            return null;
        }
    }


    public static boolean delete(String name) {
        if (providers == null) init();
        Object o = providers.get(name);
        if (o != null) {
            // close provider for new incomming packages
            ((ProviderInterface)o).close();

            // small trick set provider time check to now
            // so all packages it has seem expired
            ((ProviderInterface)o).signalUpdate();

            // remove all the offline packages from this provider
            PackageManager.removeOfflinePackages((ProviderInterface)o);

            // remove all the offline bundle from this provider
            BundleManager.removeOfflineBundles((ProviderInterface)o);

            // remove the provider itself.
            providers.remove(name);
            return true;
        } else {
            return false;
        }
    }

    public static void readProviders() {
        providers = new HashMap<String, ProviderInterface>();
        String filename = PackageManager.getConfigPath()+File.separator+"packaging"+File.separator+"providers.xml";

        File file = new File(filename);
        if(file.exists()) {
            ExtendedDocumentReader reader = new ExtendedDocumentReader(filename,ProviderManager.class);
            if(reader != null) {
                for (Element n: reader.getChildElements("providers", "provider")) {
                    NamedNodeMap nm = n.getAttributes();
                    if (nm != null) {
                        String name = null;
                        String method = null;
                        String maintainer = null;

                        // decode name
                        org.w3c.dom.Node n2 = nm.getNamedItem("name");
                        if (n2 != null) {
                            name = n2.getNodeValue();
                        }

                        // decode the method
                        n2 = nm.getNamedItem("method");
                        if (n2 != null) {
                            method = n2.getNodeValue();
                        }

                        // decode the maintainer
                        n2 = nm.getNamedItem("maintainer");
                        if (n2 != null) {
                            maintainer = n2.getNodeValue();
                        }

                        if (method != null) {
                            // try to create this handler
                            String classname = providerhandlers.get(method);
                            if (classname != null) {
                                try {
                                    Class newclass = Class.forName(classname);
                                    ProviderInterface newprovider = (ProviderInterface)newclass.newInstance();
                                    newprovider.init(n,name,method,maintainer);
                                    providers.put(name,newprovider);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } else {
            log.error("missing package providers file : "+filename);
        }
    }


    public static boolean writeProviderFile() {
        ProviderFileWriter.write();
        return true;
    }

    // should be moved to the correct class
    public static String addDiskProvider(String name,String path) {
        if (get(name) == null) {
            DiskProvider d = new DiskProvider();
            d.init(name,"disk","local",path);
            providers.put(name,d);
        } else {
            String feedback = " '"+name+"' allready in your providers list !";
            return feedback;
        }
        String feedback = " '"+name+"' added, see overview for details";
        return feedback;
    }

    // should be moved to the corrent class
    public static String addSubscribeProvider(String url) {
        ExtendedDocumentReader reader = getAutoConfigReader(url);
        // lets hunt for the file, should be a little nicer
        if (reader == null) reader = getAutoConfigReader(url+"/mmbase/packagemanager/shareautoconfig.jsp");
        if (reader == null) reader = getAutoConfigReader(url+"/mmbase-webapp/mmbase/packagemanager/shareautoconfig.jsp");
        if (reader == null) reader = getAutoConfigReader(url+"/shareautoconfig.jsp");
        if (reader == null) reader = getAutoConfigReader(url+"/mmbase-webapp/shareautoconfig.jsp");
        if (reader != null) {
            String name = "unknown";
            String method = "unknown";
            String maintainer = "unknown";
            String account = "guest";
            String password = "guest";
            String path = "";
            for (Element e: reader.getChildElements("shareautoconfig")) {
                String field = e.getNodeName();
                org.w3c.dom.Node n = e.getFirstChild();
                if (field.equals("name")) name = n.getNodeValue();
                if (field.equals("method")) method = n.getNodeValue();
                if (field.equals("maintainer")) maintainer = n.getNodeValue();
                if (field.equals("account")) account = n.getNodeValue();
                if (field.equals("password")) password = n.getNodeValue();
                if (field.equals("path")) path = n.getNodeValue();
            }
            if (method.equals("http")) {
                if (get(name) == null) {
                    ProviderInterface h = new HttpProvider();
                    h.setAccount(account);
                    h.setPassword(password);
                    h.init(name,method,maintainer,path);
                    providers.put(name,h);
                } else {
                    String feedback = " '"+name+"' allready in your providers list !";
                    return feedback;
                }
                String feedback = " '"+name+"' added, see overview for details";
                return feedback;
            }
        }
        String feedback = "unable to find share autoconfig file at that location";
        return feedback;
    }

    // should be moved to the correct class
    private static ExtendedDocumentReader getAutoConfigReader(String url) {
        ExtendedDocumentReader reader = null;
        try {
            if (url.indexOf("http://") != 0) url = "http://"+url;
            URL includeURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) includeURL.openConnection();
            BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
            reader = new ExtendedDocumentReader(new InputSource(input),ProviderManager.class);
            return reader;
        } catch(Exception e) {
            return null;
        }
    }


    public static void readProviderHandlers() {
       providerhandlers = new HashMap<String, String>();
       String filename = PackageManager.getConfigPath()+File.separator+"packaging"+File.separator+"providerhandlers.xml";

        File file = new File(filename);
        if(file.exists()) {
            ExtendedDocumentReader reader = new ExtendedDocumentReader(filename,ProviderManager.class);
            if(reader != null) {
                for (Element n: reader.getChildElements("providerhandlers", "providerhandler")) {
                    NamedNodeMap nm = n.getAttributes();
                    if (nm != null) {
                        String method = null;
                        String classname = null;
                        String state = null;
                        String basescore = null;

                        // decode method
                        org.w3c.dom.Node n2 = nm.getNamedItem("method");
                        if (n2 != null) {
                            method = n2.getNodeValue();
                        }

                        // decode the state
                        n2 = nm.getNamedItem("state");
                        if (n2 != null) {
                            state = n2.getNodeValue();
                        }

                        // decode the basescore
                        n2 = nm.getNamedItem("basescore");
                        if (n2 != null) {
                            basescore = n2.getNodeValue();
                        }

                        // decode the class
                        n2 = nm.getNamedItem("class");
                        if (n2 != null) {
                            classname = n2.getNodeValue();
                        }
                        providerhandlers.put(method,classname);
                    }
                }
            }
        } else {
            log.error("missing providerhandler file : "+filename);
        }
    }

    public static HashMap<String, String> getProviderHandlers() {
        return providerhandlers;
    }

    public static void discoverPackages() {
    packagediscovery.getPackages();
    }

}
