/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.sharehandlers.gui;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.jar.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.bundlehandlers.*;
import org.mmbase.applications.packaging.installhandlers.*;
import org.mmbase.applications.packaging.providerhandlers.*;
import org.mmbase.applications.packaging.sharehandlers.*;
import org.mmbase.applications.packaging.projects.creators.*;
import org.mmbase.applications.packaging.projects.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;

/**
 * @author     Daniel Ockeloen
 * @created    July 20, 2004
 * @version    $Id: guiController.java
 */
public class Controller {

    private static Logger log = Logging.getLoggerInstance(Controller.class);
    private static Cloud cloud;
    NodeManager manager;
    CloudContext context;


    /**
     *Constructor for the Controller object
     */
    public Controller() {
        cloud = LocalContext.getCloudContext().getCloud("mmbase");

        // hack needs to be solved
        manager = cloud.getNodeManager("typedef");
        if (manager == null) {
            log.error("Can't access builder typedef");
        }
        context = LocalContext.getCloudContext();
        if (!InstallManager.isRunning()) {
            InstallManager.init();
        }
    }


    /**
     *  Description of the Method
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean makeShareActive(String id, String wv) {
        PackageContainer p = null;
        ShareInfo shareinfo = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo != null) {
            shareinfo.setActive(true);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean makeShareInactive(String id, String wv) {
        PackageContainer p = null;
        ShareInfo shareinfo = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo != null) {
            shareinfo.setActive(false);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Gets the sharedPackages attribute of the Controller object
     *
     * @return    The sharedPackages value
     */
    public List getSharedPackages() {
        // get the current best packages
        Iterator packages = ShareManager.getSharedPackages();

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (packages.hasNext()) {
            PackageContainer p = (PackageContainer) packages.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("id", p.getId());
            virtual.setValue("name", p.getName());
            virtual.setValue("type", p.getType());
            virtual.setValue("maintainer", p.getMaintainer());
            virtual.setValue("sharedversions", "best");

            ShareInfo shareinfo = p.getShareInfo();
            if (shareinfo.isActive()) {
                virtual.setValue("active", "yes");
            } else {
                virtual.setValue("active", "no");
            }
            list.add(virtual);
        }

        return list;
    }


    /**
     *  Gets the sharedBundles attribute of the Controller object
     *
     * @return    The sharedBundles value
     */
    public List getSharedBundles() {
        // get the current best packages
        Iterator bundles = ShareManager.getSharedBundles();

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (bundles.hasNext()) {
            BundleContainer b = (BundleContainer) bundles.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("id", b.getId());
            virtual.setValue("name", b.getName());
            virtual.setValue("type", b.getType());
            virtual.setValue("maintainer", b.getMaintainer());
            virtual.setValue("sharedversions", "best");

            ShareInfo shareinfo = b.getShareInfo();
            if (shareinfo.isActive()) {
                virtual.setValue("active", "yes");
            } else {
                virtual.setValue("active", "no");
            }
            list.add(virtual);
        }

        return list;
    }


    /**
     *  Gets the notSharedPackages attribute of the Controller object
     *
     * @return    The notSharedPackages value
     */
    public List getNotSharedPackages() {
        // get the current best packages
        Iterator packages = ShareManager.getNotSharedPackages();

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (packages.hasNext()) {
            PackageContainer p = (PackageContainer) packages.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("id", p.getId());
            virtual.setValue("name", p.getName());
            virtual.setValue("type", p.getType());
            virtual.setValue("maintainer", p.getMaintainer());
            virtual.setValue("sharedversions", "best");
            list.add(virtual);
        }

        return list;
    }


    /**
     *  Gets the notSharedBundles attribute of the Controller object
     *
     * @return    The notSharedBundles value
     */
    public List getNotSharedBundles() {
        // get the current best bundles
        Iterator bundles = ShareManager.getNotSharedBundles();

        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (bundles.hasNext()) {
            BundleContainer b = (BundleContainer) bundles.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("id", b.getId());
            virtual.setValue("name", b.getName());
            virtual.setValue("type", b.getType());
            virtual.setValue("maintainer", b.getMaintainer());
            virtual.setValue("sharedversions", "best");
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Gets the remoteSharedPackages attribute of the Controller object
     *
     * @param  user         Description of the Parameter
     * @param  password     Description of the Parameter
     * @param  method       Description of the Parameter
     * @param  host         Description of the Parameter
     * @param  callbackurl  Description of the Parameter
     * @return              The remoteSharedPackages value
     */
    public List getRemoteSharedPackages(String user, String password, String method, String host, String callbackurl) {
        // get the current best packages
        Iterator packages = ShareManager.getRemoteSharedPackages(user, password, method, host);
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        while (packages.hasNext()) {
            MMObjectNode virtual = builder.getNewNode("admin");
            Object o = packages.next();
            if (o instanceof PackageContainer) {
                PackageContainer p = (PackageContainer) o;
                virtual.setValue("id", p.getId());
                virtual.setValue("name", p.getName());
                virtual.setValue("type", p.getType());
                virtual.setValue("version", p.getVersion());
                virtual.setValue("maintainer", p.getMaintainer());
                virtual.setValue("creation-date", p.getCreationDate());
                virtual.setValue("description", p.getDescription());
                virtual.setValue("releasenotes", p.getReleaseNotes());
                virtual.setValue("installationnotes", p.getInstallationNotes());
                virtual.setValue("licensename", p.getLicenseName());
                virtual.setValue("licensetype", p.getLicenseType());
                virtual.setValue("licenseversion", p.getLicenseVersion());
                virtual.setValue("licensebody", p.getLicenseBody());
                String path = ShareManager.getProvidingPath(method) + "/package.mmp?id=" + p.getId() + "&amp;version=" + p.getVersion();
                virtual.setValue("path", path);
                List l = p.getRelatedPeople("initiators");
                if (l != null) {
                    virtual.setValue("initiators", getRelatedPeopleString(l, "initiators"));
                }
                l = p.getRelatedPeople("supporters");
                if (l != null) {
                    virtual.setValue("supporters", getRelatedPeopleString(l, "supporters"));
                }
                l = p.getRelatedPeople("developers");
                if (l != null) {
                    virtual.setValue("developers", getRelatedPeopleString(l, "developers"));
                }
                l = p.getRelatedPeople("contacts");
                if (l != null) {
                    virtual.setValue("contacts", getRelatedPeopleString(l, "contacts"));
                }
            } else {
                BundleContainer p = (BundleContainer) o;
                virtual.setValue("id", p.getId());
                virtual.setValue("name", p.getName());
                virtual.setValue("type", p.getType());
                virtual.setValue("version", p.getVersion());
                virtual.setValue("creation-date", p.getCreationDate());
                virtual.setValue("maintainer", p.getMaintainer());
                virtual.setValue("description", p.getDescription());
                virtual.setValue("releasenotes", p.getReleaseNotes());
                virtual.setValue("installationnotes", p.getInstallationNotes());
                virtual.setValue("licensename", p.getLicenseName());
                virtual.setValue("licensetype", p.getLicenseType());
                virtual.setValue("licenseversion", p.getLicenseVersion());
                virtual.setValue("licensebody", p.getLicenseBody());
                String path = ShareManager.getProvidingPath(method) + "/package.mmp?id=" + p.getId() + "&amp;version=" + p.getVersion();
                virtual.setValue("path", path);

                List l = p.getRelatedPeople("initiators");
                if (l != null) {
                    virtual.setValue("initiators", getRelatedPeopleString(l, "initiators"));
                }
                l = p.getRelatedPeople("supporters");
                if (l != null) {
                    virtual.setValue("supporters", getRelatedPeopleString(l, "supporters"));
                }
                l = p.getRelatedPeople("developers");
                if (l != null) {
                    virtual.setValue("developers", getRelatedPeopleString(l, "developers"));
                }
                l = p.getRelatedPeople("contacts");
                if (l != null) {
                    virtual.setValue("contacts", getRelatedPeopleString(l, "contacts"));
                }
            }

            list.add(virtual);
        }

        // signal we have a client
        ShareManager.reportClientSession(callbackurl);

        return list;
    }


    /**
     *  Gets the relatedPeopleString attribute of the Controller object
     *
     * @param  people  Description of the Parameter
     * @param  type    Description of the Parameter
     * @return         The relatedPeopleString value
     */
    public String getRelatedPeopleString(List people, String type) {
        String body = "";
        if (people != null) {
            for (Iterator i = people.iterator(); i.hasNext(); ) {
                Person pr = (Person) i.next();
                if (type.equals("initiators")) {
                    body += "\t\t\t<initiator name=\"" + pr.getName() + "\" company=\"" + pr.getCompany() + "\" />\n";
                } else if (type.equals("developers")) {
                    body += "\t\t\t<developer name=\"" + pr.getName() + "\" company=\"" + pr.getCompany() + "\" mailto=\"" + pr.getMailto() + "\" />\n";
                } else if (type.equals("contacts")) {
                    body += "\t\t\t<contact reason=\"" + pr.getReason() + "\" name=\"" + pr.getName() + "\" mailto=\"" + pr.getMailto() + "\" />\n";
                } else if (type.equals("supporters")) {
                    body += "\t\t\t<supporter company=\"" + pr.getCompany() + "\" />\n";
                }
            }
        }
        return body;
    }



    /**
     *  Gets the shareUsers attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     The shareUsers value
     */
    public List getShareUsers(String id, String wv) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }

        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            BundleContainer b = null;
            if (wv.equals("best")) {
                b = (BundleContainer) BundleManager.getBundle(id);
            } else {
                // ok lets decode the version and provider we want
                // b=(BundleContainer(BundleManager.getBundle(id,wv);
            }
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            // get the current best packages
            Iterator users = shareinfo.getShareUsers();

            while (users.hasNext()) {
                ShareUser u = (ShareUser) users.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name", u.getName());
                list.add(virtual);
            }
        }
        return list;
    }


    /**
     *  Gets the allUsers attribute of the Controller object
     *
     * @return    The allUsers value
     */
    public List getAllUsers() {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Iterator users = ShareManager.getShareUsers();
        while (users.hasNext()) {
            ShareUser u = (ShareUser) users.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name", u.getName());
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Gets the allGroups attribute of the Controller object
     *
     * @return    The allGroups value
     */
    public List getAllGroups() {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        Iterator groups = ShareManager.getShareGroups();
        while (groups.hasNext()) {
            ShareGroup g = (ShareGroup) groups.next();
            MMObjectNode virtual = builder.getNewNode("admin");
            virtual.setValue("name", g.getName());
            list.add(virtual);
        }
        return list;
    }


    /**
     *  Gets the groupUsers attribute of the Controller object
     *
     * @param  name  Description of the Parameter
     * @return       The groupUsers value
     */
    public List getGroupUsers(String name) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        ShareGroup sg = ShareManager.getShareGroup(name);
        if (sg != null) {
            Iterator users = sg.getMembers();
            while (users.hasNext()) {
                ShareUser u = (ShareUser) users.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name", u.getName());
                list.add(virtual);
            }
        }
        return list;
    }


    /**
     *  Adds a feature to the GroupUser attribute of the Controller object
     *
     * @param  group  The feature to be added to the GroupUser attribute
     * @param  user   The feature to be added to the GroupUser attribute
     * @return        Description of the Return Value
     */
    public boolean addGroupUser(String group, String user) {
        ShareGroup sg = ShareManager.getShareGroup(group);
        if (sg != null) {
            sg.addMember(user);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  group  Description of the Parameter
     * @return        Description of the Return Value
     */
    public boolean createGroup(String group) {
        ShareManager.createGroup(group);
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  group  Description of the Parameter
     * @return        Description of the Return Value
     */
    public boolean removeGroup(String group) {
        ShareManager.removeGroup(group);
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  group  Description of the Parameter
     * @param  user   Description of the Parameter
     * @return        Description of the Return Value
     */
    public boolean removeGroupUser(String group, String user) {
        ShareGroup sg = ShareManager.getShareGroup(group);
        if (sg != null) {
            sg.removeMember(user);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Gets the shareUsersReverse attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     The shareUsersReverse value
     */
    public List getShareUsersReverse(String id, String wv) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            BundleContainer b = null;
            if (wv.equals("best")) {
                b = (BundleContainer) BundleManager.getBundle(id);
            } else {
                // ok lets decode the version and provider we want
                // b=(BundleContainer(BundleManager.getBundle(id,wv);
            }
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            // get the current best packages
            Iterator users = ShareManager.getShareUsers();

            while (users.hasNext()) {
                ShareUser u = (ShareUser) users.next();
                if (!shareinfo.containsUser(u.getName())) {
                    MMObjectNode virtual = builder.getNewNode("admin");
                    virtual.setValue("name", u.getName());
                    list.add(virtual);
                }
            }
        }
        return list;
    }


    /**
     *  Description of the Method
     *
     * @param  name  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean delUser(String name) {
        String result = ShareManager.delUser(name);
        if (result != null) {
            //virtual.setValue("feedback",result);
            return false;
        }
        ShareManager.writeShareFile();
        ShareManager.signalRemoteClients();
        return true;
    }


    /**
     *  Adds a feature to the ShareUser attribute of the Controller object
     *
     * @param  id       The feature to be added to the ShareUser attribute
     * @param  wv       The feature to be added to the ShareUser attribute
     * @param  newuser  The feature to be added to the ShareUser attribute
     * @return          Description of the Return Value
     */
    public boolean addShareUser(String id, String wv, String newuser) {

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            // well maybe its a bundle ?
            BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            // get the current best packages
            shareinfo.addUser(newuser);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Adds a feature to the ShareGroup attribute of the Controller object
     *
     * @param  id        The feature to be added to the ShareGroup attribute
     * @param  wv        The feature to be added to the ShareGroup attribute
     * @param  newgroup  The feature to be added to the ShareGroup attribute
     * @return           Description of the Return Value
     */
    public boolean addShareGroup(String id, String wv, String newgroup) {

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            // well maybe its a bundle ?
            BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            // get the current best packages
            shareinfo.addGroup(newgroup);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  account   Description of the Parameter
     * @param  password  Description of the Parameter
     * @param  method    Description of the Parameter
     * @param  ip        Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean createNewUser(String account, String password, String method, String ip) {
        String result = ShareManager.createNewUser(account, password, method, ip);
        if (result != null) {
            //virtual.setStringValue("feedback",result);
            return false;
        }
        ShareManager.writeShareFile();
        ShareManager.signalRemoteClients();
        return true;
    }


    /**
     *  Adds a feature to the Share attribute of the Controller object
     *
     * @param  id  The feature to be added to the Share attribute
     * @return     Description of the Return Value
     */
    public boolean addShare(String id) {
        PackageContainer p = (PackageContainer) PackageManager.getPackage(id);
        if (p != null) {
            ShareInfo shareinfo = p.getShareInfo();
            if (shareinfo == null) {
                shareinfo = new ShareInfo();
                shareinfo.setActive(true);
                p.setShareInfo(shareinfo);
                ShareManager.writeShareFile();
                ShareManager.signalRemoteClients();
            }
            //virtual.setStringValue("feedback","Package shared don't forget to add users/groups");
        } else {
            //virtual.setStringValue("feedback","Package you want to share is not available anymore");
            return false;
        }
        return true;
    }


    /**
     *  Adds a feature to the BundleShare attribute of the Controller object
     *
     * @param  id  The feature to be added to the BundleShare attribute
     * @return     Description of the Return Value
     */
    public boolean addBundleShare(String id) {
        BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
        if (b != null) {
            ShareInfo shareinfo = b.getShareInfo();
            if (shareinfo == null) {
                shareinfo = new ShareInfo();
                shareinfo.setActive(true);
                b.setShareInfo(shareinfo);
                ShareManager.writeShareFile();
                ShareManager.signalRemoteClients();
            }
            //virtual.setStringValue("feedback","Bundle shared don't forget to add users/groups");
        } else {
            //virtual.setStringValue("feedback","Bundle you want to share is not available anymore");
            return false;
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  id  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean delShare(String id) {
        PackageContainer p = (PackageContainer) PackageManager.getPackage(id);
        if (p != null) {
            ShareInfo shareinfo = p.getShareInfo();
            if (shareinfo != null) {
                p.removeShare();
                ShareManager.writeShareFile();
                ShareManager.signalRemoteClients();
            }
        } else {
            return false;
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  id  Description of the Parameter
     * @return     Description of the Return Value
     */
    public boolean delBundleShare(String id) {

        BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
        if (b != null) {
            ShareInfo shareinfo = b.getShareInfo();
            if (shareinfo != null) {
                b.removeShare();
                ShareManager.writeShareFile();
                ShareManager.signalRemoteClients();
            }
        } else {
            return false;
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  id       Description of the Parameter
     * @param  wv       Description of the Parameter
     * @param  olduser  Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean removeShareUser(String id, String wv, String olduser) {

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            // well maybe its a bundle ?
            BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            shareinfo.removeUser(olduser);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  id        Description of the Parameter
     * @param  wv        Description of the Parameter
     * @param  delgroup  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean removeShareGroup(String id, String wv, String delgroup) {

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            // well maybe its a bundle ?
            BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            shareinfo.removeGroup(delgroup);
            ShareManager.writeShareFile();
            ShareManager.signalRemoteClients();
        }
        return true;
    }


    /**
     *  Gets the shareGroups attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     The shareGroups value
     */
    public List getShareGroups(String id, String wv) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            BundleContainer b = null;
            if (wv.equals("best")) {
                b = (BundleContainer) BundleManager.getBundle(id);
            } else {
                // ok lets decode the version and provider we want
                // b=(BundleContainer(BundleManager.getBundle(id,wv);
            }
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            // get the current best packages
            Iterator groups = shareinfo.getShareGroups();

            while (groups.hasNext()) {
                ShareGroup g = (ShareGroup) groups.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                virtual.setValue("name", g.getName());
                list.add(virtual);
            }
        }
        return list;
    }


    /**
     *  Gets the shareGroupsReverse attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     The shareGroupsReverse value
     */
    public List getShareGroupsReverse(String id, String wv) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        ShareInfo shareinfo = null;

        PackageContainer p = null;
        if (wv.equals("best")) {
            p = (PackageContainer) PackageManager.getPackage(id);
        } else {
            // ok lets decode the version and provider we want
            // p=(PackageContainer)PackageManager.getPackage(id,wv);
        }
        if (p != null) {
            shareinfo = p.getShareInfo();
        }

        if (shareinfo == null) {
            BundleContainer b = null;
            if (wv.equals("best")) {
                b = (BundleContainer) BundleManager.getBundle(id);
            } else {
                // ok lets decode the version and provider we want
                // b=(BundleContainer(BundleManager.getBundle(id,wv);
            }
            if (b != null) {
                shareinfo = b.getShareInfo();
            }
        }

        if (shareinfo != null) {
            // get the current best packages
            Iterator groups = ShareManager.getShareGroups();

            while (groups.hasNext()) {
                ShareGroup g = (ShareGroup) groups.next();
                MMObjectNode virtual = builder.getNewNode("admin");
                if (!shareinfo.containsGroup(g.getName())) {
                    virtual.setValue("name", g.getName());
                    list.add(virtual);
                }
            }
        }
        return list;
    }


    /**
     *  Gets the shareInfo attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     The shareInfo value
     */
    public MMObjectNode getShareInfo(String id, String wv) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());

        MMObjectNode virtual = builder.getNewNode("admin");

        if (wv.equals("best")) {
            PackageContainer p = (PackageContainer) PackageManager.getPackage(id);
            if (p != null) {
                virtual.setValue("name", p.getName());
                virtual.setValue("maintainer", "" + p.getMaintainer());
                virtual.setValue("type", p.getType());

                ShareInfo scaninfo = p.getShareInfo();
                if (scaninfo != null) {
                    if (scaninfo.isActive()) {
                        virtual.setValue("active", "yes");
                    } else {
                        virtual.setValue("active", "no");
                    }
                }
            } else {
                // well maybe its a bundle ?
                BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
                if (b != null) {
                    virtual.setValue("name", b.getName());
                    virtual.setValue("maintainer", "" + b.getMaintainer());
                    virtual.setValue("type", b.getType());

                    ShareInfo scaninfo = b.getShareInfo();
                    if (scaninfo != null) {
                        if (scaninfo.isActive()) {
                            virtual.setValue("active", "yes");
                        } else {
                            virtual.setValue("active", "no");
                        }
                    }
                }
            }
        }
        return virtual;
    }



    /**
     *  Gets the bundleShareInfo attribute of the Controller object
     *
     * @param  id  Description of the Parameter
     * @param  wv  Description of the Parameter
     * @return     The bundleShareInfo value
     */
    public List getBundleShareInfo(String id, String wv) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        if (wv.equals("best")) {
            BundleContainer b = (BundleContainer) BundleManager.getBundle(id);
            if (b != null) {
                virtual.setValue("name", b.getName());
                virtual.setValue("maintainer", "" + b.getMaintainer());
                virtual.setValue("type", b.getType());

                ShareInfo scaninfo = b.getShareInfo();
                if (scaninfo != null) {
                    if (scaninfo.isActive()) {
                        virtual.setValue("active", "yes");
                    } else {
                        virtual.setValue("active", "no");
                    }
                }
            }
        }
        list.add(virtual);
        return list;
    }


    /**
     *  Gets the userInfo attribute of the Controller object
     *
     * @param  account  Description of the Parameter
     * @return          The userInfo value
     */
    public List getUserInfo(String account) {
        List list = new ArrayList();
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");

        ShareUser user = ShareManager.getShareUser(account);
        if (user != null) {
            virtual.setValue("account", user.getName());
            virtual.setValue("password", user.getPassword());
            virtual.setValue("method", user.getMethod());
            String host = user.getHost();
            if (host != null) {
                virtual.setValue("host", user.getHost());
            } else {
                virtual.setValue("host", "none");
            }
        }
        list.add(virtual);
        return list;
    }


}
