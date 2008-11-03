/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.applications.packaging;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.mmbase.applications.packaging.bundlehandlers.BundleContainer;
import org.mmbase.applications.packaging.packagehandlers.PackageContainer;
import org.mmbase.applications.packaging.sharehandlers.ShareClientSession;
import org.mmbase.applications.packaging.sharehandlers.ShareFileWriter;
import org.mmbase.applications.packaging.sharehandlers.ShareGroup;
import org.mmbase.applications.packaging.sharehandlers.ShareInfo;
import org.mmbase.applications.packaging.sharehandlers.ShareUser;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

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

    private static HashMap<String, ShareUser> users = new HashMap<String, ShareUser>();

    private static HashMap<String, ShareGroup> groups = new HashMap<String, ShareGroup>();

    private static HashMap<String, ShareClientSession> clients = new HashMap<String, ShareClientSession>();

    private static HashMap<String, String> providingpaths = new HashMap<String, String>();

    /** DTD resource filename of the sharing DTD version 1.0 */
    public static final String DTD_SHARING_1_0 = "shared_1_0.dtd";

    /** Public ID of the sharing DTD version 1.0 */
    public static final String PUBLIC_ID_SHARING_1_0 = "-//MMBase//DTD shared config 1.0//EN";

    /**
     * Register the Public Ids for DTDs used by DatabaseReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_SHARING_1_0, "DTD_SHARING_1_0", ShareManager.class);
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
    public static Iterator<PackageContainer> getSharedPackages() {
        // first get the PackageManager
        if (PackageManager.isRunning()) {
            Iterator<PackageContainer> p = PackageManager.getPackages();
            ArrayList<PackageContainer> reallyshared = new ArrayList<PackageContainer>();
            while (p.hasNext()) {
                PackageContainer e = p.next();
                if (e.isShared()) {
                    reallyshared.add(e);
                }
            }
            return reallyshared.iterator();
        } else {
            return (new ArrayList<PackageContainer>()).iterator();
        }
    }


    /**
     */
    public static Iterator<BundleContainer> getSharedBundles() {
        // first getthe BundleManager
        if (BundleManager.isRunning()) {
            Iterator<BundleContainer> b = BundleManager.getBundles();
            ArrayList<BundleContainer> reallyshared = new ArrayList<BundleContainer>();
            while (b.hasNext()) {
                BundleContainer e = b.next();
                if (e.isShared()) {
                    reallyshared.add(e);
                }
            }
            return reallyshared.iterator();
        } else {
            return (new ArrayList<BundleContainer>()).iterator();
        }
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator<PackageContainer> getNotSharedPackages() {
        // first get the PackageManager
        if (PackageManager.isRunning()) {
            Iterator<PackageContainer> p = PackageManager.getPackages();
            ArrayList<PackageContainer> reallynotshared = new ArrayList<PackageContainer>();
            while (p.hasNext()) {
                PackageContainer e = p.next();
                if (!e.isShared()) {
                    reallynotshared.add(e);
                }
            }
            return reallynotshared.iterator();
        } else {
            return (new ArrayList<PackageContainer>()).iterator();
        }
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator<BundleContainer> getNotSharedBundles() {
        // first get the BundleManager
        if (BundleManager.isRunning()) {
            Iterator<BundleContainer> b = BundleManager.getBundles();
            ArrayList<BundleContainer> reallynotshared = new ArrayList<BundleContainer>();
            while (b.hasNext()) {
                BundleContainer e = b.next();
                if (!e.isShared()) {
                    reallynotshared.add(e);
                }
            }
            return reallynotshared.iterator();
        } else {
            return (new ArrayList<BundleContainer>()).iterator();
        }
    }


    /**
     * return all packages based on the input query
     * @return all packages
     */
    public static Iterator<Object> getRemoteSharedPackages(String user,String password,String method,String host) {
        // first get the PackageManager
        if (PackageManager.isRunning()) {
            Iterator<PackageContainer> p = PackageManager.getPackages();
            ArrayList<Object> reallyshared = new ArrayList<Object>();
            while (p.hasNext()) {
                PackageContainer e = p.next();
                if (e.isShared()) {
                    ShareInfo shareinfo = e.getShareInfo();
                    if (shareinfo!=null && shareinfo.isActive()) {
                        if (shareinfo.sharedForUser(user,password,method,host)) {
                            reallyshared.add(e);
                        }
                    }
                }
            }

            Iterator<BundleContainer> b = BundleManager.getBundles();
            while (b.hasNext()) {
                BundleContainer e = b.next();
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
            return (new ArrayList<Object>()).iterator();
        }
    }


    public static void readShared() {
        String filename = PackageManager.getConfigPath()+File.separator+"packaging"+File.separator+"sharing.xml";
        File file = new File(filename);
        if(file.exists()) {
            ExtendedDocumentReader reader = new ExtendedDocumentReader(filename,ShareManager.class);
            if(reader != null) {
                decodeSettings(reader);
                decodeProvidingPaths(reader);
                decodeUsers(reader);
                decodeGroups(reader);

                // decode packages
                for (Element n: reader.getChildElements("shared", "packaging")) {
                    for (Element n2: reader.getChildElements(n, "package")) {
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
                for (Element n: reader.getChildElements("shared", "bundles")) {
                    for (Element n2: reader.getChildElements(n, "bundle")) {
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


    private static boolean decodeLogins(PackageContainer p,ExtendedDocumentReader reader,Element e) {
        ShareInfo s = p.getShareInfo();
        if (s != null) {
            for (Element loginnode: reader.getChildElements(e,"login")) {
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

    private static boolean decodeBundleLogins(BundleContainer b,ExtendedDocumentReader reader,Element e) {
        ShareInfo s = b.getShareInfo();
        if (s != null) {
            for (Element loginnode: reader.getChildElements(e, "login")) {
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



    private static boolean decodeUsers(ExtendedDocumentReader reader) {
        for (Element n: reader.getChildElements("shared", "users")) {
            for (Element n2: reader.getChildElements(n, "user")) {
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

    private static boolean decodeProvidingPaths(ExtendedDocumentReader reader) {
        for (Element n: reader.getChildElements("shared", "providingpaths")) {
            for (Element n2: reader.getChildElements(n, "providingpath")) {
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


    private static boolean decodeSettings(ExtendedDocumentReader reader) {
        for (Element n: reader.getChildElements("shared", "settings")) {
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


    private static boolean decodeGroups(ExtendedDocumentReader reader) {
        for (Element n: reader.getChildElements("shared", "groups")) {
            for (Element n2: reader.getChildElements(n,"group")) {
                NamedNodeMap nm = n2.getAttributes();
                if (nm != null) {
                    String name = null;

                    // decode name
                    org.w3c.dom.Node n3 = nm.getNamedItem("name");
                    if (n3 != null) {
                        name = n3.getNodeValue();
                    }

                    ShareGroup sg = new ShareGroup(name);
                    for (Element n4: reader.getChildElements(n2,"member")) {
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

    public static Iterator<ShareUser> getShareUsers() {
        return users.values().iterator();
    }

    public static Iterator<ShareGroup> getShareGroups() {
        return groups.values().iterator();
    }

    public static ShareUser getShareUser(String name) {
        Object o=users.get(name);
        if (o!=null) {
            return users.get(name);
        }
        log.error("Share refers to a user ("+name+") that is not defined");
        return null;
    }

    public static ShareGroup getShareGroup(String name) {
        Object o = groups.get(name);
        if (o != null) {
            return groups.get(name);
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
        ShareClientSession scs = clients.get(callbackurl);
        if (scs != null) {
        } else {
            if (callbackurl != null && !callbackurl.equals("")) {
                scs=new ShareClientSession(callbackurl);
                clients.put(callbackurl,scs);
            }
        }
    }

    public static void signalRemoteClients() {
        Iterator<ShareClientSession> e = clients.values().iterator();
        while (e.hasNext()) {
            ShareClientSession s = e.next();
            s.sendRemoteSignal(getProviderName());
        }
    }

    public static String getProvidingPath(String method) {
        return providingpaths.get(method);
    }

    public static HashMap<String, String> getProvidingPaths() {
        return providingpaths;
    }

}
