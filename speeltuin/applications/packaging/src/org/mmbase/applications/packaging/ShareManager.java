/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;

import java.io.File;
import java.util.*;

import org.w3c.dom.*;

/**
 * provider manager, maintains the package/bundles providers and abstracts
 * their access methods for the Bundle and Package manager.
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class ShareManager {
    private static Logger log = Logging.getLoggerInstance(ShareManager.class);
    private static boolean state = false;

    private static String callbackurl = "";

    private static String providername = "";

    private static HashMap users = new HashMap();

    private static HashMap groups = new HashMap();

    private static HashMap clients = new HashMap();

    private static HashMap providingpaths = new HashMap();

    /** DTD resource filename of the sharing DTD version 1.0 */
    public static final String DTD_SHARING_1_0 = "shared_1_0.dtd";

    /** Public ID of the sharing DTD version 1.0 */
    public static final String PUBLIC_ID_SHARING_1_0 = "-//MMBase//DTD shared config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_SHARING_1_0, "DTD_SHARING_1_0", ShareManager.class);
    }

    
    public static synchronized void init() {
        if (!isRunning()) {
            state=true;
            readShared();
        }
    }

    public static boolean isRunning() {
        return state;
    }



    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator getSharedPackages() {
        // first get the PackageManager
        if (PackageManager.isRunning()) {
            Iterator p = PackageManager.getPackages();
            ArrayList reallyshared = new ArrayList();
            while (p.hasNext()) {
                PackageContainer e = (PackageContainer)p.next();
                if (e.isShared()) {
                    reallyshared.add(e);
                }
            }
            return reallyshared.iterator();
        } else {
            return (new ArrayList()).iterator();
        }
    }


    /**
     */
    public static Iterator getSharedBundles() {
        // first getthe BundleManager
        if (BundleManager.isRunning()) {
            Iterator b = BundleManager.getBundles();
            ArrayList reallyshared = new ArrayList();
            while (b.hasNext()) {
                BundleContainer e = (BundleContainer)b.next();
                if (e.isShared()) {
                    reallyshared.add(e);
                }
            }
            return reallyshared.iterator();
        } else {
            return (new ArrayList()).iterator();
        }
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator getNotSharedPackages() {
        // first get the PackageManager
        if (PackageManager.isRunning()) {
            Iterator p = PackageManager.getPackages();
            ArrayList reallynotshared = new ArrayList();
            while (p.hasNext()) {
                PackageContainer e = (PackageContainer)p.next();
                if (!e.isShared()) {
                    reallynotshared.add(e);
                }
            }
            return reallynotshared.iterator();
        } else {
            return (new ArrayList()).iterator();
        }
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator getNotSharedBundles() {
        // first get the BundleManager
        if (BundleManager.isRunning()) {
            Iterator b = BundleManager.getBundles();
            ArrayList reallynotshared = new ArrayList();
            while (b.hasNext()) {
                BundleContainer e = (BundleContainer)b.next();
                if (!e.isShared()) {
                    reallynotshared.add(e);
                }
            }
            return reallynotshared.iterator();
        } else {
            return (new ArrayList()).iterator();
        }
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator getRemoteSharedPackages(String user,String password,String method,String host) {
        // first get the PackageManager
        if (PackageManager.isRunning()) {
            Iterator p = PackageManager.getPackages();
            ArrayList reallyshared = new ArrayList();
            while (p.hasNext()) {
                PackageContainer e = (PackageContainer)p.next();
                if (e.isShared()) {
                    ShareInfo shareinfo = e.getShareInfo();
                    if (shareinfo!=null && shareinfo.isActive()) {
                        if (shareinfo.sharedForUser(user,password,method,host)) {
                            reallyshared.add(e);
                        }
                    }
                }
            }

            Iterator b = BundleManager.getBundles();
            while (b.hasNext()) {
                BundleContainer e = (BundleContainer)b.next();
                if (e.isShared()) {
                    ShareInfo shareinfo = e.getShareInfo();
                    if (shareinfo != null && shareinfo.isActive()) {
                        if (shareinfo.sharedForUser(user,password,method,host)) {
                            reallyshared.add(e);
                        }
                    }
                }
            }
            return reallyshared.iterator();
        } else {
            return (new ArrayList()).iterator();
        }
    }


    public static void readShared() {
        String filename = MMBaseContext.getConfigPath()+File.separator+"packaging"+File.separator+"sharing.xml";
        File file = new File(filename);
        if(file.exists()) {
            XMLBasicReader reader = new XMLBasicReader(filename,ShareManager.class);
            if(reader != null) {
                decodeSettings(reader);
                decodeProvidingPaths(reader);
                decodeUsers(reader);
                decodeGroups(reader);

                // decode packages
                for(Enumeration ns = reader.getChildElements("shared","packaging");ns.hasMoreElements(); ) {
                    Element n = (Element)ns.nextElement();

                        for(Enumeration ns2 = reader.getChildElements(n,"package");ns2.hasMoreElements(); ) {
                            Element n2 = (Element)ns2.nextElement();
                            NamedNodeMap nm = n2.getAttributes();
                            if (nm != null) {
                                String name = null;
                                String maintainer = null;
                                String type = null;
                                String versions = null;
                                String active = null;
                
                                // decode name
                                org.w3c.dom.Node n3 = nm.getNamedItem("name");
                                if (n3 != null) {
                                    name = n3.getNodeValue();
                                }

                                // decode maintainer
                                n3 = nm.getNamedItem("maintainer");
                                if (n3 != null) {
                                    maintainer = n3.getNodeValue();
                                }

                                // decode type
                                n3 = nm.getNamedItem("type");
                                if (n3 != null) {
                                    type = n3.getNodeValue();
                                }

                                // decode versions
                                n3 = nm.getNamedItem("versions");
                                if (n3 != null) {
                                    versions = n3.getNodeValue();
                                } 

                                // decode active
                                n3 = nm.getNamedItem("active");
                                if (n3 != null) {
                                    active = n3.getNodeValue();
                                }


                                // create its id (name+maintainer)
                                String id = name+"@"+maintainer+"_"+type;
                                id = id.replace(' ','_');
                                id = id.replace('/','_');
                                PackageContainer p = (PackageContainer)PackageManager.getPackage(id);
                                if (p != null) {
                                    ShareInfo shareinfo = p.getShareInfo();
                                    if (shareinfo == null) {
                                        shareinfo = new ShareInfo();
                                        if (active.equals("true")) {
                                            shareinfo.setActive(true);
                                        } else {
                                            shareinfo.setActive(false);
                                        }
                                    }
                                    if (versions.equals("best")) {
                                        p.setShareInfo(shareinfo);
                                    }
                                    decodeLogins(p,reader,n2);
                                } else {    
                                    log.error("trying to share a non available package, ignoring");
                                }
                            } 
                        }
                }

                // decode bundles
                for(Enumeration ns = reader.getChildElements("shared","bundles");ns.hasMoreElements(); ) {
                    Element n = (Element)ns.nextElement();
                    for(Enumeration ns2 = reader.getChildElements(n,"bundle");ns2.hasMoreElements(); ) {
                        Element n2 = (Element)ns2.nextElement();
                        NamedNodeMap nm = n2.getAttributes();
                        if (nm != null) {
                            String name = null;
                            String maintainer = null;
                            String type = null;
                            String versions = null;
                            String active = null;
                
                            // decode name
                            org.w3c.dom.Node n3 = nm.getNamedItem("name");
                            if (n3 != null) {
                                name = n3.getNodeValue();
                            }

                            // decode maintainer
                            n3 = nm.getNamedItem("maintainer");
                            if (n3 != null) {
                                maintainer = n3.getNodeValue();
                            }

                            // decode type
                            n3 = nm.getNamedItem("type");
                            if (n3 != null) {
                                type = n3.getNodeValue();
                            }

                            // decode versions
                            n3 = nm.getNamedItem("versions");
                            if (n3 != null) {
                                versions = n3.getNodeValue();
                            }

                            // decode active
                            n3=nm.getNamedItem("active");
                            if (n3!=null) {
                                active=n3.getNodeValue();
                            }

                            // create its id (name+maintainer)
                            String id = name+"@"+maintainer+"_"+type;
                            id = id.replace(' ','_');
                            id = id.replace('/','_');
                            BundleContainer b = (BundleContainer)BundleManager.getBundle(id);
                            if (b != null) {
                                ShareInfo shareinfo = b.getShareInfo();
                                if (shareinfo == null) {
                                    shareinfo = new ShareInfo();
                                    if (active.equals("true")) {
                                        shareinfo.setActive(true);
                                    } else {
                                        shareinfo.setActive(false);
                                    }
                                }
                                if (versions.equals("best")) {
                                    b.setShareInfo(shareinfo);
                                }
                                decodeBundleLogins(b,reader,n2);
                            } else {    
                                log.error("trying to share a non available package, ignoring");
                            }
                        }
                    }
                }
            }
        } else {
            log.error("missing shares file : "+filename);
        }
    }
    

    private static boolean decodeLogins(PackageContainer p,XMLBasicReader reader,Element e) {
        ShareInfo s = p.getShareInfo();
        if (s != null) {
            Enumeration e2 = reader.getChildElements(e,"login");
            while (e2.hasMoreElements()) {
                org.w3c.dom.Node loginnode = (org.w3c.dom.Node)e2.nextElement();
                NamedNodeMap nm = loginnode.getAttributes();
                if (nm != null) {
                    // decode possible user
                    org.w3c.dom.Node n = nm.getNamedItem("user");
                    if (n != null) {
                        String user = n.getNodeValue();
                        s.addUser(user);
                    }

                    // decode possible group
                    n = nm.getNamedItem("group");
                    if (n != null) {
                        String group = n.getNodeValue();
                        s.addGroup(group);
                    }
                }
            }
        }
        return true;
    }

    private static boolean decodeBundleLogins(BundleContainer b,XMLBasicReader reader,Element e) {
        ShareInfo s = b.getShareInfo();
        if (s != null) {
            Enumeration e2 = reader.getChildElements(e,"login");
            while (e2.hasMoreElements()) {
                org.w3c.dom.Node loginnode = (org.w3c.dom.Node)e2.nextElement();
                NamedNodeMap nm = loginnode.getAttributes();
                if (nm != null) {
                    // decode possible user
                    org.w3c.dom.Node n = nm.getNamedItem("user");
                    if (n != null) {
                        String user = n.getNodeValue();
                        s.addUser(user);
                    }

                    // decode possible group
                    n = nm.getNamedItem("group");
                    if (n != null) {
                        String group = n.getNodeValue();
                        s.addGroup(group);
                    }
                }
            }
        }
        return true;
    }



    private static boolean decodeUsers(XMLBasicReader reader) {
        for(Enumeration ns = reader.getChildElements("shared","users");ns.hasMoreElements(); ) {
            Element n = (Element)ns.nextElement();
            for(Enumeration ns2 = reader.getChildElements(n,"user");ns2.hasMoreElements(); ) {
                Element n2 = (Element)ns2.nextElement();
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                    String name = null;
                    String password = null;
                    String method = null;
                    String ip = null;
            
                    // decode name
                    org.w3c.dom.Node n3 = nm.getNamedItem("name");
                    if (n3 != null) {
                        name = n3.getNodeValue();
                    }

                    // decode password
                    n3 = nm.getNamedItem("password");
                    if (n3 != null) {
                        password = n3.getNodeValue();
                    }

                    // decode method
                    n3 = nm.getNamedItem("method");
                    if (n3 != null) {
                        method = n3.getNodeValue();
                    }

                    // decode ip
                    n3 = nm.getNamedItem("ip");
                    if (n3 != null) {
                        ip = n3.getNodeValue();
                    }

                    ShareUser su = new ShareUser(name);
                    if (password != null) su.setPassword(password);
                    if (method != null) su.setMethod(method);
                    if (ip != null) su.setHost(ip);
                    users.put(name,su);
                }
            }
        }
        return true;
    }

    private static boolean decodeProvidingPaths(XMLBasicReader reader) {
        for(Enumeration ns = reader.getChildElements("shared","providingpaths");ns.hasMoreElements(); ) {
            Element n = (Element)ns.nextElement();
            for(Enumeration ns2 = reader.getChildElements(n,"providingpath");ns2.hasMoreElements(); ) {
                Element n2 = (Element)ns2.nextElement();
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                    String method = null;
                    String path = null;
            
                    // decode path
                    org.w3c.dom.Node n3 = nm.getNamedItem("path");
                    if (n3 != null) {
                        path = n3.getNodeValue();
                    }

                    // decode method
                    n3 = nm.getNamedItem("method");
                    if (n3 != null) {
                        method = n3.getNodeValue();
                    }
    
                    if (path != null && method != null) {
                        providingpaths.put(method,path);
                    }
                }
            }
        }
        return true;
    }


    private static boolean decodeSettings(XMLBasicReader reader) {
        for(Enumeration ns = reader.getChildElements("shared","settings");ns.hasMoreElements(); ) {
            Element n = (Element)ns.nextElement();
            org.w3c.dom.Node n2 = n.getFirstChild();
                while (n2 != null) {
                String name = n2.getNodeName();
                org.w3c.dom.Node n3 = n2.getFirstChild();
                if (n3 != null) {
                    String value = n3.getNodeValue();
                    if (name.equals("providername")) {
                        providername = value;
                    } else if (name.equals("callbackurl")) {
                        callbackurl = value;
                    }
                }
                n2 = n2.getNextSibling();
            }
        }
        return true;
    }


    private static boolean decodeGroups(XMLBasicReader reader) {
        for(Enumeration ns = reader.getChildElements("shared","groups");ns.hasMoreElements(); ) {
            Element n = (Element)ns.nextElement();

            for(Enumeration ns2 = reader.getChildElements(n,"group");ns2.hasMoreElements(); ) {
                Element n2 = (Element)ns2.nextElement();
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                    String name = null;
            
                    // decode name
                    org.w3c.dom.Node n3 = nm.getNamedItem("name");
                    if (n3 != null) {
                        name = n3.getNodeValue();
                    }

                    ShareGroup sg = new ShareGroup(name);
                    for(Enumeration ns3 = reader.getChildElements(n2,"member");ns3.hasMoreElements(); ) {
                        Element n4 = (Element)ns3.nextElement();
                        NamedNodeMap nm2 = n4.getAttributes();
                        if (nm2 != null) {
                            String member = null;
                            // decode member
                            n3 = nm2.getNamedItem("user");
                            if (n3 != null) {
                                member = n3.getNodeValue();
                                sg.addMember(member);
                            }
                        }
                    }  
                    groups.put(name,sg);
                }
            }
        }
        return true;
    }

    public static boolean createGroup(String name) {
        if (!name.equals("") && groups.get(name) == null) {
            ShareGroup sg = new ShareGroup(name);
            groups.put(name,sg);        
            writeShareFile();
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeGroup(String name) {
        groups.remove(name);        
        writeShareFile();
        return true;
    }

    public static Iterator getShareUsers() {
        return users.values().iterator();
    }

    public static Iterator getShareGroups() {
        return groups.values().iterator();
    }

    public static ShareUser getShareUser(String name) {
        Object o=users.get(name);
        if (o!=null) {
            return (ShareUser)users.get(name);
        } 
        log.error("Share refers to a user ("+name+") that is not defined");
        return null;
    }

    public static ShareGroup getShareGroup(String name) {
        Object o = groups.get(name);
        if (o != null) {
            return (ShareGroup)groups.get(name);
        } 
        log.error("Share refers to a group ("+name+") that is not defined");
        return null;
    }

    public static boolean writeShareFile() {
        ShareFileWriter.write();
        return true;
    }

    public static String getProviderName() {
        return providername;
    }


    public static void setProviderName(String name) {
        providername = name;
    }

    public static String getCallbackUrl() {
        return callbackurl;
    }

    public static void setCallbackUrl(String url) {
        callbackurl = url;
    }

    public static String createNewUser(String account,String password,String method,String ip) {
        if (!users.containsKey(account)) {
            ShareUser su = new ShareUser(account);
            if (password != null) su.setPassword(password);
            if (method != null) su.setMethod(method);
            if (ip != null && !ip.equals("none")) su.setHost(ip);
            users.put(account,su);
            return "user added";
        } else {
            return "user allready defined";
        }
    }


    public static String delUser(String account) {
        if (users.containsKey(account)) {
            users.remove(account);
            return "user deleted";
        } else {
            return "user not found so can't delete it";
        }
    }

    public static void reportClientSession(String callbackurl) {
        ShareClientSession scs = (ShareClientSession)clients.get(callbackurl);
        if (scs != null) {
        } else {
            if (callbackurl != null && !callbackurl.equals("")) {
                scs=new ShareClientSession(callbackurl);
                clients.put(callbackurl,scs);
            }
        }    
    }

    public static void signalRemoteClients() {
        Iterator e = clients.values().iterator();
        while (e.hasNext()) {
            ShareClientSession s = (ShareClientSession)e.next();
            s.sendRemoteSignal(getProviderName());
        }
    } 

    public static String getProvidingPath(String method) {
        return (String)providingpaths.get(method);
    }

    public static HashMap getProvidingPaths() {
        return providingpaths;
    }

}
